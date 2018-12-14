package com.jsti.pile.collector;

import java.util.ArrayList;
import java.util.List;

import com.jsti.pile.collector.adapter.CollectHistoryAdapter;
import com.jsti.pile.collector.common.AppProfiles;
import com.jsti.pile.collector.db.DataService;
import com.jsti.pile.collector.model.RoadCollectTask;
import com.jsti.pile.collector.model.User;
import com.jsti.pile.collector.widgets.NavItems;
import com.jsti.pile.collector.widgets.NavigationBar;
import com.jsti.pile.collector.widgets.NavigationBar.NavItem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import static com.jsti.pile.collector.common.AppProfiles.KEY_LAST_LOGIN_USER;

/**
 * 桩号收集历史页面
 */
public class CollectHistoryActivity extends BaseNavActivity {
	private final List<RoadCollectTask> mData = new ArrayList<RoadCollectTask>();
	private final CollectHistoryAdapter mAdapter = new CollectHistoryAdapter();
	private ListView mListView;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_history);
		user = App.SP_System.getBeanFromSp(KEY_LAST_LOGIN_USER);
		DataService.getInstance(this).getAllTask(user.getUid(), mData);
		mAdapter.setData(mData, false);
		initWidgets();
		initNav();
	}

	private void initNav() {
		NavigationBar nav = getNavigationBar();
		nav.addFromLeft(NavItems.back);
		nav.setTitle("历史记录");
	}

	@Override
	public void onItemClick(View v, NavItem item, NavigationBar nav) {
		if (item.getId() == NavItems.back.getId()) {
			finish();
		}
	}

	private void initWidgets() {
		mListView = (ListView) findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(onItemClickListener);
	}

	private static final int REQ_SHOW_DETAIL = 10086;
	private RoadCollectTask lastShowDetailTask;
	private final OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Object obj = parent.getAdapter().getItem(position);
			if (obj instanceof RoadCollectTask) {
				lastShowDetailTask = (RoadCollectTask) obj;
				CollectDataDetailActivity.startForResult(CollectHistoryActivity.this, lastShowDetailTask,
						REQ_SHOW_DETAIL);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_SHOW_DETAIL && resultCode == RESULT_OK
				&& data.getBooleanExtra(CollectDataDetailActivity.KEY_ROAD_COLLECT_DATA_CHANGED, false)) {
			reloadData();
		}
	}

	private void reloadData() {
		mData.clear();
		DataService.getInstance(this).getAllTask(user.getUid(), mData);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
		reloadData();
	}
}