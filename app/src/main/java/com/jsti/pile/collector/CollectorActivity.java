package com.jsti.pile.collector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.jsti.pile.collector.common.CommonParams;
import com.jsti.pile.collector.common.LocationManager;
import com.jsti.pile.collector.common.LocationManager.LocationListener;
import com.jsti.pile.collector.db.DataService;
import com.jsti.pile.collector.model.Pile;
import com.jsti.pile.collector.model.RoadCollectTask;
import com.jsti.pile.collector.utils.PileUtils;
import com.jsti.pile.collector.utils.Utils;
import com.jsti.pile.collector.widgets.NavItems;
import com.jsti.pile.collector.widgets.NavigationBar;
import com.jsti.pile.collector.widgets.NavigationBar.NavItem;
import com.jsti.pile.collector.widgets.PileConfirmDialog;
import com.jsti.pile.collector.widgets.PileConfirmDialog.PileConfirmCallback;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 桩号收集页面
 */
public class CollectorActivity extends BaseNavActivity
		implements LocationListener, OnClickListener, PileConfirmCallback {
	private static final String TAG = "CollectorActivity";
	public static final String EXTRA_COLLECT_TASK = "collect_task";
	private LocationManager mLocationManager;
	private RoadCollectTask mCollectTask;
	private PileConfirmDialog dialog;
	private TextView mRoadeNameTv;
	private TextView mRoadeDireactionTv;
	private View mCollectIndecator;
	private Animation mCollectIndecatorAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);
		Intent intent = getIntent();
		mCollectTask = (RoadCollectTask) intent.getSerializableExtra(EXTRA_COLLECT_TASK);
		if (mCollectTask == null) {
			Utils.toast(this, "道路收集初始化失败，收集任务信息不能为空");
			return;
		}
		updateCruiseTargetLatLng();
		dialog = new PileConfirmDialog(this, this);
		initWidgets();
		makeCollectOutputFile();
		mLocationManager = new LocationManager(this, this);
		mRoadeNameTv.setText(mCollectTask.getRoadCode() + "" + mCollectTask.getRoadName());
		mRoadeDireactionTv.setText(mCollectTask.getDirectionName());
		mLocationManager.start();
		showProgressDialog("正在初始化...");
		mRoadeDireactionTv.postDelayed(new Runnable() {
			@Override
			public void run() {
				dismissProgressDialog();
				startCollect();
			}
		}, 3000);
	}

	private void updateCruiseTargetLatLng() {
		mCruiseTargetLatLng = new LatLng(mCollectTask.getLastScanLatitude(), mCollectTask.getLastScanLongitude());
	}

	@Override
	public void onItemClick(View v, NavItem item, NavigationBar nav) {
		if (item.getId() == NavItems.FINISH_COLLECT.getId()) {
			confirmFinish();
		}
	}

	@Override
	public void onBackPressed() {
		confirmFinish();
	}

	@Override
	public void finish() {
		super.finish();
		if (mCollectResultWriter != null) {
			try {
				mCollectResultWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mCollectTask.setFinishTime(System.currentTimeMillis());
		mCollectTask.setCollectStatus(RoadCollectTask.COLLECT_STATUS_FINISHED);
		onCollectTaskInfoChanged();
		try {
			mLocationManager.stop();
		} catch (Exception e) {
			//
		}
	}

	private void confirmFinish() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("结束收集任务").setMessage("确定要结束收集任务吗？")
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						finish();
					}
				}).show();
	}

	private Button mCollectSwitchBtn;

	private void initWidgets() {
		NavigationBar nav = getNavigationBar();
		nav.setTitle(mCollectTask.getRoadCode() + "桩号收集");
		nav.addFromRight(NavItems.FINISH_COLLECT);
		nav.setNavgationListener(this);
		//
		mCollectIndecator = findViewById(R.id.collect_indecator);
		mCollectIndecatorAnim = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
		mCollectIndecator.setAnimation(mCollectIndecatorAnim);
		//
		mRoadeNameTv = (TextView) findViewById(R.id.road_name);
		mRoadeDireactionTv = (TextView) findViewById(R.id.road_direaction);
		findViewById(R.id.collect_find_pile).setOnClickListener(this);
		mCollectSwitchBtn = (Button) findViewById(R.id.collect_switch);
		mCollectSwitchBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.collect_find_pile:
			onCrossPile();
			break;
		case R.id.collect_switch:
			switchCollectStatus();
			break;
		}
	}

	private LatLng mLastConfirmLatlng;
	private double mLastConfirmLatLngDist;

	private void onCrossPile() {
		mLastConfirmLatlng = lastScanLatLng;
		if (mLastConfirmLatlng == null) {
			Utils.toast(this, "定位失败，请检查网络和GPS设置");
			return;
		}
		LatLng ll = new LatLng(mCollectTask.getLastPileLatitude(), mCollectTask.getLastPileLongitude());
		mLastConfirmLatLngDist = DistanceUtil.getDistance(mLastConfirmLatlng, ll);
		if (mLastConfirmLatLngDist < 15) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("经过桩号").setMessage("收集桩号过于密集，是否要继续？")
					.setPositiveButton("继续", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							onFindPile(mLastConfirmLatlng, true,
									(int) Math.min(1000, Math.round(mLastConfirmLatLngDist)));
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).show();
			return;
		} else {
			onFindPile(mLastConfirmLatlng, true, (int) Math.min(1000, Math.round(mLastConfirmLatLngDist)));
		}
	}

	private void switchCollectStatus() {
		if (mLocationManager.isRunning()) {
			pauseCollect();
		} else {
			startCollect();
		}
	}

	private void onFindPile(LatLng latLng, boolean confirm, int pileStep) {
		Pile pile = new Pile();
		pile.setCollectDirection(mCollectTask.getDirection());
		pile.setCollectTime(System.currentTimeMillis());
		pile.setLatitude(latLng.latitude);
		pile.setLongitude(latLng.longitude);
		int newPileValue;
		int lastCollectPileNo = mCollectTask.getLastFindPile();
		if (mCollectTask.isCollectPileASC()) {
			newPileValue = lastCollectPileNo + pileStep;
		} else {
			newPileValue = lastCollectPileNo - pileStep;
		}
		pile.setNumber(newPileValue);
		pile.setPileType(Pile.TYPE_EXACT_PILE);
		//
		if (confirm) {
			dialog.setConfirmInfo(pile, mCollectTask.getStartPile());
			dialog.show();
		} else {
			savePile(pile);
		}
	}

	@Override
	public void onConfirmAccept(Pile pile, int newPileNumber, int olderPileNumber) {
		if (newPileNumber >= 0) {
			pile.setNumber(newPileNumber);
		}
		dialog.dismiss();
		if (pile.getNumber() == mCollectTask.getLastFindPile()
				&& pile.getLatitude() == mCollectTask.getLastPileLatitude()
				&& pile.getLongitude() == mCollectTask.getLastPileLongitude()) {
			Utils.toast(this, "桩号已存在");
			return;
		}
		savePile(pile);
		onCollectTaskInfoChanged();
	}

	@Override
	public void onConfirmCancled(Pile pile) {
		// do nothing
	}

	private void onCollectTaskInfoChanged() {
		// Update
		synchronized (mCollectTask) {
			DataService.getInstance(this).addCollectTask(mCollectTask);
		}
	}

	private File mCollectOutputFile;
	private FileWriter mCollectResultWriter;

	private void makeCollectOutputFile() {
		String fileName = mCollectTask.makeLocalSaveFileName();
		mCollectOutputFile = new File(CommonParams.PATH_COLLECT_RESULT, fileName);
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			Utils.toast(this, "请打开手机存储权限限制");
			exitApp();
			return;
		}
		File parentFile = mCollectOutputFile.getParentFile();
		if (!parentFile.exists()) {
			if (!parentFile.mkdirs()) {
				Utils.toast(this, "创建文件失败，轻确保手机有存储空间");
				exitApp();
				return;
			}
		}
		if (!mCollectOutputFile.exists()) {
			try {
				mCollectOutputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Utils.toast(this, "创建文件失败，轻确保手机有存储空间");
				exitApp();
			}
		}
		try {
			mCollectResultWriter = new FileWriter(mCollectOutputFile, true);
		} catch (IOException e) {
			e.printStackTrace();
			Utils.toast(this, "打开文件收集文件失败");
			exitApp();
		}
		mCollectTask.setFilePath(mCollectOutputFile.getAbsolutePath());
		onCollectTaskInfoChanged();
	}

	private void exitApp() {
		finish();
	}

	private void pauseCollect() {
		updateCruiseTargetLatLng();
		mCollectIndecatorAnim.cancel();
		mCollectIndecator.clearAnimation();
		mCollectIndecator.invalidate();
		mCollectSwitchBtn.setText("继续");
		mLocationManager.stop();
		mCollectTask.setCollectStatus(RoadCollectTask.COLLECT_STATUS_PAUSED);
		onCollectTaskInfoChanged();
	}

	private void startCollect() {
		startCollect(true);
	}

	private void startCollect(boolean cruise) {
		mCollectSwitchBtn.setText("暂停");
		if (!cruise || mCollectTask.getCollectStatus() == RoadCollectTask.COLLECT_STATUS_NEVER_START) {
			mCollectTask.setCollectStatus(RoadCollectTask.COLLECT_STATUS_COLLECTING);
			onCollectTaskInfoChanged();
		} else {
			// 先巡航到上一次暂停的点
			mCollectTask.setCollectStatus(RoadCollectTask.COLLECT_STATUS_COLLECTING);
			startCruise();
		}
		mLocationManager.start();
		mCollectIndecator.postDelayed(new Runnable() {
			@Override
			public void run() {
				mCollectIndecatorAnim.setDuration(800);
				mCollectIndecatorAnim.setRepeatCount(-1);
				mCollectIndecatorAnim.setRepeatMode(Animation.RESTART);
				mCollectIndecator.startAnimation(mCollectIndecatorAnim);
			}
		}, 1500);
	}

	private final static int CRUISE_MATCH_MIN_COUNT = 2;
	private View mCruiseView;
	private TextView mCruiseLastFindPile;
	private TextView mCruiseDistToTarget;
	private boolean isCruiseToStopEnd;
	private LatLng mCruiseTargetLatLng;
	private int mCruiseMatchedCount = 0;
	private Dialog mCruiseDialog;

	private void startCruise() {
		isCruiseToStopEnd = true;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mCruiseView = LayoutInflater.from(this).inflate(R.layout.dialog_cruise, null, false);
		mCruiseLastFindPile = (TextView) mCruiseView.findViewById(R.id.last_find_pile);
		mCruiseDistToTarget = (TextView) mCruiseView.findViewById(R.id.dist_to_target);
		mCruiseLastFindPile.setText(PileUtils.toPileString(mCollectTask.getLastFindPile()));
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				finish();
			}
		});
		mCruiseDialog = builder.setTitle("巡航定位").setCancelable(false).setView(mCruiseView).create();
		mCruiseDialog.show();
	}

	private static final float COLLECT_REFRE_POINT_MIN_DIST = CommonParams.DEFAULT_MIN_MOVE_DIST;
	private LatLng lastRefLatLng;
	private LatLng lastScanLatLng;

	private static final int MSG_NEW_LOCATION = 10086;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_NEW_LOCATION:
				handlerNewLatLng((LatLng) msg.obj);
				break;
			}
		}
	};

	private long lastShowCruiseFailedToastTime;

	private void handlerNewLatLng(LatLng newLatLng) {
		if (mCollectTask == null || mCollectTask.getCollectStatus() != RoadCollectTask.COLLECT_STATUS_COLLECTING) {
			return;
		}
		lastScanLatLng = newLatLng;
		if (isCruiseToStopEnd) {
			if (mCruiseTargetLatLng == null) {
				mCruiseTargetLatLng = newLatLng;
			}
			double dist = DistanceUtil.getDistance(mCruiseTargetLatLng, newLatLng);
			if (dist > 1000) {
				mCruiseDistToTarget.setText(String.format("%.2fkm", dist / 1000));
			} else {
				mCruiseDistToTarget.setText(String.format("%.2fm", dist));
			}
			Log.d("ttt", "dist:" + dist + ",mCruiseTargetLatLng:" + mCruiseTargetLatLng + ",newLatLng:" + newLatLng);
			if (dist < 15) {
				if (mCruiseMatchedCount == 0) {
					Toast.makeText(this, "靠近目标，正在匹配", Toast.LENGTH_SHORT).show();
				}
				mCruiseMatchedCount++;
			} else {
				mCruiseMatchedCount = 0;
				if (System.currentTimeMillis() - lastShowCruiseFailedToastTime > 2000) {
					lastShowCruiseFailedToastTime = System.currentTimeMillis();
					Toast.makeText(this, "偏离目标", Toast.LENGTH_SHORT).show();
				}
			}
			if (mCruiseMatchedCount >= CRUISE_MATCH_MIN_COUNT) {
				isCruiseToStopEnd = false;
				//
				mCollectTask.setCollectStatus(RoadCollectTask.COLLECT_STATUS_COLLECTING);
				onCollectTaskInfoChanged();
				if (mCruiseDialog != null && mCruiseDialog.isShowing()) {
					mCruiseDialog.dismiss();
				}
				Toast.makeText(this, "巡航成功", Toast.LENGTH_SHORT).show();
				startCollect(false);
			}
		} else {
			// collect pile
			mCollectTask.setLastScanLatitude(lastScanLatLng.latitude);
			mCollectTask.setLastScanLongitude(lastScanLatLng.longitude);
			if (!mCollectTask.isStartPileCollected()) {
				onFindPile(lastScanLatLng, false, 0);
				mCollectTask.setStartPileCollected(true);
				onCollectTaskInfoChanged();
			}
			if (lastRefLatLng == null) {
				lastRefLatLng = newLatLng;
			} else {
				double dist = DistanceUtil.getDistance(lastRefLatLng, lastScanLatLng);
				if (dist >= COLLECT_REFRE_POINT_MIN_DIST) {
					mCollectTask.setGpsReferCount(mCollectTask.getGpsReferCount() + 1);
					savePile(newLatLng.longitude, newLatLng.latitude);
					if (CommonParams.DEBUG) {
						Log.d(TAG, "add refre point LatLng(" + newLatLng.latitude + "," + newLatLng.longitude + ")");
					}
					lastRefLatLng = lastScanLatLng;
				}
			}
			onCollectTaskInfoChanged();
		}
	}

	@Override
	public void onNewLatLng(double longitude, double latitude) {
		Message msg = handler.obtainMessage(MSG_NEW_LOCATION);
		msg.obj = new LatLng(latitude, longitude);
		msg.sendToTarget();
	}

	/**
	 * 保存坐标
	 * @param longitude
	 * @param latitude
	 */
	private void savePile(double longitude, double latitude) {
		Pile pile = new Pile();
		pile.setCollectDirection(mCollectTask.getDirection());
		pile.setCollectTime(System.currentTimeMillis());
		pile.setLatitude(latitude);
		pile.setLongitude(longitude);
		pile.setNumber(-1);
		pile.setPileType(Pile.TYPE_NOT_EXACT_PILE);
		savePile(pile);
	}

	/**
	 *  生成桩号文件
	 * @param pile
	 */
	private void savePile(Pile pile) {
		if (mCollectResultWriter == null) {
			Utils.toast(this, "创建记录失败，请确保收集有足够的存储空间");
		} else {
			if (pile.getPileType() == Pile.TYPE_EXACT_PILE) {
				mCollectTask.setLastFindPile(pile.getNumber());
				mCollectTask.setLastPileLatitude(pile.getLatitude());
				mCollectTask.setLastPileLongitude(pile.getLongitude());
				mCollectTask.setExactPileCount(mCollectTask.getExactPileCount() + 1);
				onCollectTaskInfoChanged();
			}
			//
			StringBuilder sb = new StringBuilder();
			sb.append(pile.getNumber());
			sb.append(',');
			sb.append(pile.getLongitude());
			sb.append(',');
			sb.append(pile.getLatitude());
			sb.append(',');
			sb.append(pile.getCollectTime());
			sb.append(',');
			sb.append(pile.getPileType());
			// sb.append(',');
			// sb.append(pile.getCollectDirection());
			sb.append("\n");
			String str = sb.toString();
			Log.d(TAG, "record pile:" + str);
			try {
				mCollectResultWriter.write(str);
				mCollectResultWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
				Utils.toast(this, "创建记录失败，请确保收集有足够的存储空间");
				exitApp();
			}
		}
	}

	private boolean isActivityResumed = false;

	@Override
	protected void onPause() {
		super.onPause();
		isActivityResumed = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		isActivityResumed = true;
		checkGPS();
	}

	@Override
	public void onFindError(String msg) {
		Log.d(TAG, "on loaction found error:" + msg);
		if (isActivityResumed) {
			if (!TextUtils.isEmpty(msg)) {
				Utils.toast(this, msg);
			}
		}
	}

	public static void start(Context context, RoadCollectTask task) {
		Intent intent = new Intent(context, CollectorActivity.class);
		intent.putExtra(EXTRA_COLLECT_TASK, task);
		context.startActivity(intent);
	}
}
