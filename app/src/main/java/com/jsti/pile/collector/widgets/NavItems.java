package com.jsti.pile.collector.widgets;

import com.jsti.pile.collector.R;
import com.jsti.pile.collector.widgets.NavigationBar.NavItem;
import com.jsti.pile.collector.widgets.NavigationBar.NavItem.ResType;

public class NavItems {

	public static final NavItem back = img(R.drawable.nav_back);
	public static final NavItem ok = img(R.drawable.nav_ok);
	public static final NavItem FINISH_COLLECT = text(R.string.finish_collect);
	public static final NavItem COLLECT_HISTORY = text(R.string.nav_collect_history);
	public static final NavItem LOGOUT = text(R.string.nav_logout);

	// ------------------------------------------------------------------------
	public static NavItem text(int stringResId) {
		return new BaseItem(stringResId, ResType.string);
	}

	public static NavItem img(int drawableResId) {
		return new BaseItem(drawableResId, ResType.drawable);
	}

	public static NavItem view(int layoutResId) {
		return new BaseItem(layoutResId, ResType.layout);
	}

	private static class BaseItem implements NavItem {
		private int resId;
		private int id;
		private ResType resType;

		public BaseItem(int resId, ResType resType) {
			this.resId = resId;
			this.id = resId;
			this.resType = resType;
		}

		public int getResId() {
			return resId;
		}

		public ResType getResType() {
			return resType;
		}

		public int getId() {
			return id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			result = prime * result + resId;
			result = prime * result + ((resType == null) ? 0 : resType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BaseItem other = (BaseItem) obj;
			return id == other.id;
		}

	}
}
