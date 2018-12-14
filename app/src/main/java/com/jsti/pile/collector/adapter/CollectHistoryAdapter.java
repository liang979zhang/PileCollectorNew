package com.jsti.pile.collector.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.jsti.pile.collector.R;
import com.jsti.pile.collector.model.RoadCollectTask;
import com.jsti.pile.collector.utils.PileUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 桩号收集历史
 */
public class CollectHistoryAdapter extends InnerBaseAdapter<RoadCollectTask> {

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_road_collect_task, parent,
					false);
			//
			vh.roadCode = (TextView) convertView.findViewById(R.id.road_code);
			vh.collectDirection = (TextView) convertView.findViewById(R.id.road_collect_direction);
			vh.dataStatus = (TextView) convertView.findViewById(R.id.road_collect_data_status);
			vh.startPile = (TextView) convertView.findViewById(R.id.road_collect_start_pile);
			vh.endPile = (TextView) convertView.findViewById(R.id.road_collect_finish_pile);
			vh.startTime = (TextView) convertView.findViewById(R.id.road_collect_start_time);
			vh.endTime = (TextView) convertView.findViewById(R.id.road_collect_finish_time);
			//
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		RoadCollectTask task = getData(position);
		vh.roadCode.setText(task.getRoadCode());
		vh.collectDirection.setText(task.getDirectionName());
		vh.dataStatus.setText(task.isSubmitToServer() ? "已提交" : "待提交");
		vh.startPile.setText(PileUtils.toPileString(task.getStartPile()));
		vh.endPile.setText(PileUtils.toPileString(task.getLastFindPile()));
		vh.startTime.setText(formatDate(task.getStartTime()));
		if (task.getCollectStatus() == RoadCollectTask.COLLECT_STATUS_FINISHED) {
			vh.endTime.setText(formatDate(task.getFinishTime()));
		} else {
			vh.endTime.setText("未结束");
		}
		return convertView;
	}

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

	private String formatDate(long time) {
		return dateFormat.format(new Date(time));
	}

	static class ViewHolder {
		TextView roadCode;
		TextView collectDirection;
		TextView dataStatus;
		TextView startPile;
		TextView endPile;
		TextView startTime;
		TextView endTime;
	}
}
