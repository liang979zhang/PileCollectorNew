package com.jsti.pile.collector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.jsti.pile.collector.common.AppProfiles;
import com.jsti.pile.collector.db.DataService;
import com.jsti.pile.collector.json.BaseDataSyncResult;
import com.jsti.pile.collector.model.Expressway;
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

import java.util.ArrayList;
import java.util.List;

import static com.jsti.pile.collector.common.AppProfiles.KEY_LAST_LOGIN_USER;

/**
 * 创建收集任务
 */
public class CreateCollectActivity extends BaseNavActivity implements OnClickListener {
    private Spinner mRoadListSpinner;
    private RadioGroup mPileDirectionGroup;
    private RadioGroup mRoadDirectionGroup;
    private RadioButton mRoadUpDirection;
    private RadioButton mRoadDownDirection;
    private EditText mBigPile;
    private EditText mSmallPile;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private final List<Expressway> mData = new ArrayList<Expressway>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_collect);
        initWidgets();
        checkLoadBaseData();
        refreshUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGPS();
    }

    private void initWidgets() {
        getNavigationBar().setTitle("开始收集");
        getNavigationBar().addFromRight(NavItems.COLLECT_HISTORY);
        getNavigationBar().addFromLeft(NavItems.LOGOUT);
        mRoadListSpinner = (Spinner) findViewById(R.id.road_list_spinner);
        mPileDirectionGroup = (RadioGroup) findViewById(R.id.pile_direction_group);
        mRoadDirectionGroup = (RadioGroup) findViewById(R.id.road_direction_group);
        mRoadUpDirection = (RadioButton) findViewById(R.id.road_up_direction);
        mRoadDownDirection = (RadioButton) findViewById(R.id.road_down_direction);
        mBigPile = (EditText) findViewById(R.id.big_pile);
        mSmallPile = (EditText) findViewById(R.id.small_pile);
        findViewById(R.id.next_step).setOnClickListener(this);
        //
        mRoadListSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
        //
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(0xFF222222, 0xFF99CC00, 0XFFFFBB33, 0XFFFF4444);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
    }

    private final OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(false);
            syncBaseData();
        }
    };

    @Override
    public void onItemClick(View v, NavItem item, NavigationBar nav) {
        if (item.getId() == NavItems.COLLECT_HISTORY.getId()) {
            Intent intent = new Intent(this, CollectHistoryActivity.class);
            startActivity(intent);
        } else if (item.getId() == NavItems.LOGOUT.getId()) {
            logout();
        }
    }

    private void logout() {
        AppProfiles.getInstance(this).logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private final OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Expressway ew = mData.get(position);
            mRoadUpDirection.setText(ew.getUpDirection());
            mRoadDownDirection.setText(ew.getDownDirection());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mRoadUpDirection.setText("");
            mRoadDownDirection.setText("");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                checkConfigAndStartCollect();
                break;
        }
    }

    private void checkConfigAndStartCollect() {
        int p = mRoadListSpinner.getSelectedItemPosition();
        if (p == AdapterView.INVALID_POSITION) {
            Utils.toast(this, "请选择道路");
            return;
        }
        final Expressway expressway = mData.get(p);
        //
        String roadDirection;
        String roadDirectionName = null;
        switch (mRoadDirectionGroup.getCheckedRadioButtonId()) {
            case R.id.road_up_direction:
                roadDirection = "S";
                roadDirectionName = expressway.getUpDirection();
                break;
            case R.id.road_down_direction:
                roadDirection = "X";
                roadDirectionName = expressway.getDownDirection();
                break;
            default:
                Utils.toast(this, "请选择方向");
                return;
        }
        //
        boolean isCollectPileASC;
        switch (mPileDirectionGroup.getCheckedRadioButtonId()) {
            case R.id.pile_ASC:
                isCollectPileASC = true;
                break;
            case R.id.pile_DESC:
                isCollectPileASC = false;
                break;
            default:
                Utils.toast(this, "请选择巡查桩号方向");
                return;
        }
        Integer bigPile = safeValueToInt(mBigPile);
        Integer smallPile = safeValueToInt(mSmallPile);
        if (bigPile == null) {
            Utils.toast(this, "请填写起始桩号");
            return;
        }
        if (smallPile == null) {
            Utils.toast(this, "请填写距离");
            return;
        }
        final int startPile = PileUtils.megerPileToInt(bigPile, smallPile);
//        User user = AppProfiles.getInstance(this).getLoginUser();
        User user = App.SP_System.getBeanFromSp(KEY_LAST_LOGIN_USER);
        RoadCollectTask oldTask = DataService.getInstance(this).getRoadTask(user.getUid(), expressway.getId(),
                startPile, roadDirection);
        RoadCollectTask task;
        if (oldTask != null) {
            task = oldTask;
        } else {
            task = new RoadCollectTask();
            task.setRoadCode(expressway.getCode());
            task.setRoadId(expressway.getId());
            task.setRoadName(expressway.getName());
            task.setCreatorId(user.getUid());
            task.setCreatorName(user.getUsername());
            task.setStartPile(startPile);
            task.setDirection(roadDirection);
            task.setDirectionName(roadDirectionName);
            task.setStartTime(System.currentTimeMillis());
            task.setCollectStatus(RoadCollectTask.COLLECT_STATUS_NEVER_START);
            task.setSubmitToServer(false);
            task.setCollectPileASC(isCollectPileASC);
            task.setLastFindPile(task.getStartPile());
            task.setStartPileCollected(false);
            DataService.getInstance(this).addCollectTask(task);
        }
        CollectorActivity.start(this, task);
    }

    private Integer safeValueToInt(EditText e) {
        String str = e.getText().toString();
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void checkLoadBaseData() {
        List<Expressway> data = AppProfiles.getInstance(this).getExpresswayData();
        if (data != null && !data.isEmpty()) {
            mData.addAll(data);
        }
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                syncBaseData();
            }
        }, 1000);
    }

    private void syncBaseData() {
        showProgressDialog("同步基础数据...", false);
        User user = App.SP_System.getBeanFromSp(KEY_LAST_LOGIN_USER);

        OkGo.<BaseDataSyncResult>get(UrlUtils.getBaseUrl() + "/intf/collector/syncBaseData.intf")
                .tag(this)
                .params("ver", "")
                .params("uid", user.getUid())
                .execute(new GsonCallback<BaseDataSyncResult>() {
                    @Override
                    public void onSuccess(Response<BaseDataSyncResult> response) {
                        BaseDataSyncResult result = response.body();
                        dismissProgressDialog();
                        if (result != null && result.isSuccess()) {
                            List<Expressway> datas = result.getExpressways();
                            if (datas == null || datas.isEmpty()) {
                                String msg = result.getMessage();
                                if (TextUtils.isEmpty(msg)) {
                                    onSyncDataFailed("没有同步到基础数据");
                                } else {
                                    onSyncDataFailed(msg);
                                }
                            } else {
                                mData.clear();
                                mData.addAll(datas);
                                refreshUI();
                            }
                            return;
                        }
                        onSyncDataFailed("同步失败,请确保网络可用后重试");
                    }

                    @Override
                    public void onError(Response<BaseDataSyncResult> response) {
                        super.onError(response);

                        dismissProgressDialog();
                        onSyncDataFailed("同步失败,请确保网络可用后重试");
                    }
                });

//		RequestEntity req = API.syncBaseData("", AppProfiles.getInstance(this).getLoginUser().getUid());
//		HttpEngine.getInstance().enqueue(req, new RequestCallback<BaseDataSyncResult>() {
//
//			@Override
//			public BaseDataSyncResult onResponseInBackground(String resp, Object reqId) throws Exception {
//				BaseDataSyncResult result = Json.parse(resp, BaseDataSyncResult.class);
//				if (result != null && result.isSuccess()) {
//					List<Expressway> datas = result.getExpressways();
//					if (datas != null && !datas.isEmpty()) {
//						AppProfiles.getInstance(CreateCollectActivity.this).setExpresswayData(datas);
//					}
//				}
//				return result;
//			}
//
//			@Override
//			public void onResult(BaseDataSyncResult result, Object reqId) {
//				dismissProgressDialog();
//				if (result != null && result.isSuccess()) {
//					List<Expressway> datas = result.getExpressways();
//					if (datas == null || datas.isEmpty()) {
//						String msg = result.getMessage();
//						if (TextUtils.isEmpty(msg)) {
//							onSyncDataFailed("没有同步到基础数据");
//						} else {
//							onSyncDataFailed(msg);
//						}
//					} else {
//						mData.clear();
//						mData.addAll(datas);
//						refreshUI();
//					}
//					return;
//				}
//				onSyncDataFailed("同步失败,请确保网络可用后重试");
//			}
//
//			@Override
//			public void onFailure(IOException e, Object reqId) {
//				dismissProgressDialog();
//				onSyncDataFailed("同步失败,请确保网络可用后重试");
//			}
//		});
    }

    private void onSyncDataFailed(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("基础数据同步失败").setMessage(msg).setCancelable(false)
                .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        syncBaseData();
                    }
                }).setNegativeButton("切换帐号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                logout();
            }
        });
        if (!mData.isEmpty()) {
            builder.setNeutralButton("使用老数据", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        builder.create().show();
    }

    private void refreshUI() {
        int size = mData.size();
        String[] values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = mData.get(i).getCode() + "-" + mData.get(i).getName();
        }
        mRoadListSpinner
                .setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, values));
        if (size > 0) {
            mRoadListSpinner.setSelection(0);
        }
    }

    @Override
    public void onBackPressed() {
        Utils.onBackPressedCheckExit(this);
    }
}
