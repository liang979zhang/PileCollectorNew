package com.jsti.pile.collector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.jsti.pile.collector.common.AppProfiles;
import com.jsti.pile.collector.db.DataService;
import com.jsti.pile.collector.json.BaseResult;
import com.jsti.pile.collector.model.RoadCollectTask;
import com.jsti.pile.collector.model.User;
import com.jsti.pile.collector.utils.GsonCallback;
import com.jsti.pile.collector.utils.PileUtils;
import com.jsti.pile.collector.utils.UrlUtils;
import com.jsti.pile.collector.utils.Utils;
import com.jsti.pile.collector.widgets.NavItems;
import com.jsti.pile.collector.widgets.NavigationBar;
import com.jsti.pile.collector.widgets.NavigationBar.NavItem;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.jsti.pile.collector.common.AppProfiles.KEY_LAST_LOGIN_USER;

/**
 * 桩号收集数据详情页面
 */
public class CollectDataDetailActivity extends BaseNavActivity implements OnClickListener {
	public static final String KEY_ROAD_COLLECT_DATA_CHANGED = "key_data_changed";
	public static final String KEY_ROAD_COLLECT_DATA = "key_road_collect_data";
	//
	private TextView roadCodeTv;
	private TextView collectDirectionTv;
	private TextView startPileTv;
	private TextView endPileTv;
	private TextView dataStatusTv;
	private TextView pileExactCountTv;
	private TextView PileGPSReferCountTv;
	private TextView startTimeTv;
	private TextView endTiemTv;
	//
	private RoadCollectTask mTask;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_data_detail);
		Object obj = getIntent().getSerializableExtra(KEY_ROAD_COLLECT_DATA);
		if (!(obj instanceof RoadCollectTask)) {
			Utils.toast(this, "无效的收集任务");
			return;
		}
		user = App.SP_System.getBeanFromSp(KEY_LAST_LOGIN_USER);
		mTask = (RoadCollectTask) obj;
		initWidgets();

		fillUI();
		initNav();
	}

	private void initNav() {
		NavigationBar nav = getNavigationBar();
		nav.addFromLeft(NavItems.back);
		nav.setTitle("数据详情");
	}

	@Override
	public void onItemClick(View v, NavItem item, NavigationBar nav) {
		if (item.getId() == NavItems.back.getId()) {
			finish();
		}
	}

	/**
	 * 填充数据
	 */
	private void fillUI() {
		roadCodeTv.setText(mTask.getRoadCode());
		collectDirectionTv.setText(mTask.getDirectionName());
		startPileTv.setText(PileUtils.toPileString(mTask.getStartPile()));
		endPileTv.setText(PileUtils.toPileString(mTask.getLastFindPile()));
		dataStatusTv.setText(mTask.isSubmitToServer() ? "已提交" : "待提交");
		pileExactCountTv.setText(String.valueOf(mTask.getExactPileCount()));
		PileGPSReferCountTv.setText(String.valueOf(mTask.getGpsReferCount()));
		startTimeTv.setText(formatDate(mTask.getStartTime()));
		if (mTask.getCollectStatus() == RoadCollectTask.COLLECT_STATUS_FINISHED) {
			endTiemTv.setText(formatDate(mTask.getFinishTime()));
		} else {
			endTiemTv.setText("未结束");
		}
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

	private String formatDate(long time) {
		return dateFormat.format(new Date(time));
	}

	private void initWidgets() {
		roadCodeTv = (TextView) findViewById(R.id.road_code);
		collectDirectionTv = (TextView) findViewById(R.id.collect_direction);
		startPileTv = (TextView) findViewById(R.id.start_pile);
		endPileTv = (TextView) findViewById(R.id.end_pile);
		dataStatusTv = (TextView) findViewById(R.id.data_status);
		pileExactCountTv = (TextView) findViewById(R.id.pile_exact_count);
		PileGPSReferCountTv = (TextView) findViewById(R.id.pile_gps_refer_count);
		startTimeTv = (TextView) findViewById(R.id.start_time);
		endTiemTv = (TextView) findViewById(R.id.end_time);
		//
		findViewById(R.id.delete_data).setOnClickListener(this);
		findViewById(R.id.append_data).setOnClickListener(this);
		findViewById(R.id.submit_data).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete_data:
			deleteData();
			break;
		case R.id.append_data:
			startAppendData();
			break;
		case R.id.submit_data:
			submitCollectData(true);
			break;
		}
	}

	private void startAppendData() {
		CollectorActivity.start(this, mTask);
		finish();
	}

	private boolean dataChanged = false;

	private void deleteData() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("删除数据");
		builder.setMessage("确定要删除数据吗？");
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				DataService.getInstance(CollectDataDetailActivity.this).deleteCollectTask(mTask.getCreatorId(),
						mTask.getRoadId(), mTask.getStartPile(), mTask.getDirection());
				dataChanged = true;
				finish();
			}
		});
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		}).show();
	}

	private void submitCollectData(boolean checkStatus) {
		if (checkStatus && mTask.getCollectStatus() != RoadCollectTask.COLLECT_STATUS_FINISHED) {
			new AlertDialog.Builder(this).setTitle("提交数据").setMessage("该道路还没有收集完成，是否要提交？")
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
							submitCollectData(false);
						}

					}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
						}
					}).create().show();
			return;
		}
		this.showProgressDialog("正在提交数据...", false);

		File file = new File(mTask.getFilePath());
		if (file.exists()) {
			Log.e("tag", "文件存在");

		} else {
			Log.e("tag", "文件不存在");
		}
		OkGo.<BaseResult>post(UrlUtils.getBaseUrl()+"/intf/collector/pile/postCollectResult.intf")
				.tag(this)
				.params("uid",user.getUid())
				.params("roadId",mTask.getRoadId())
				.params("finishTime",mTask.getFinishTime())
				.params("file",file)
				.execute(new GsonCallback<BaseResult>() {
					@Override
					public void onSuccess(Response<BaseResult> response) {
						CollectDataDetailActivity.this.dismissProgressDialog();
						BaseResult result = response.body();
						if (result != null && result.isSuccess()) {
							mTask.setSubmitToServer(true);
							DataService.getInstance(CollectDataDetailActivity.this).addCollectTask(mTask);
							dataChanged = true;
							fillUI();
							Utils.toast(CollectDataDetailActivity.this, "提交成功");
						} else {
							Utils.toast(CollectDataDetailActivity.this, "提交失败");
						}
					}

					@Override
					public void onError(Response<BaseResult> response) {
						super.onError(response);


					}
				});

//		RequestEntity req = API.postCollectResult(user.getUid(), mTask.getRoadId(), mTask.getFinishTime(),
//				mTask.makePostToServerFileName(), mTask.getFilePath());
//		HttpEngine.getInstance().enqueue(req, new RequestCallback<BaseResult>() {
//
//			@Override
//			public BaseResult onResponseInBackground(String resp, Object reqId) throws Exception {
//				return Json.parse(resp, BaseResult.class);
//			}
//
//			@Override
//			public void onResult(BaseResult result, Object reqId) {
//				CollectDataDetailActivity.this.dismissProgressDialog();
//				if (result != null && result.isSuccess()) {
//					mTask.setSubmitToServer(true);
//					DataService.getInstance(CollectDataDetailActivity.this).addCollectTask(mTask);
//					dataChanged = true;
//					fillUI();
//					Utils.toast(CollectDataDetailActivity.this, "提交成功");
//				} else {
//					Utils.toast(CollectDataDetailActivity.this, "提交失败");
//				}
//			}
//
//			@Override
//			public void onFailure(IOException e, Object reqId) {
//				CollectDataDetailActivity.this.dismissProgressDialog();
//				Utils.toast(CollectDataDetailActivity.this, "提交失败");
//			}
//		});
	}

	@Override
	public void finish() {
		if (dataChanged) {
			Intent intent = new Intent();
			intent.putExtra(KEY_ROAD_COLLECT_DATA_CHANGED, true);
			this.setResult(RESULT_OK, intent);
		}
		super.finish();
	}

	public static void startForResult(Activity context, RoadCollectTask task, int requestCode) {
		Intent intent = new Intent(context, CollectDataDetailActivity.class);
		intent.putExtra(KEY_ROAD_COLLECT_DATA, task);
		context.startActivityForResult(intent, requestCode);
	}
}
