package com.jsti.pile.collector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

/**
 * application base activity
 */
public abstract class BaseActivity extends Activity {
	private ProgressDialog mProgressDialog;

	private boolean isAttachedToWindow = false;
	private boolean needShowProgressDialogOnAttached = false;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		isAttachedToWindow = true;
		if (needShowProgressDialogOnAttached) {
			needShowProgressDialogOnAttached = false;
			innerShowProgressDialog();
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isAttachedToWindow = false;
	}

	protected void showProgressDialog(boolean cancelable) {
		showProgressDialog("正在处理...", cancelable);
	}

	protected void showProgressDialog() {
		showProgressDialog(false);
	}

	protected void showProgressDialog(int msgResId) {
		showProgressDialog(msgResId, false);
	}

	protected void showProgressDialog(int msgResId, boolean cancelable) {
		showProgressDialog(getString(msgResId), cancelable);
	}

	protected void showProgressDialog(String msg) {
		showProgressDialog(msg, false);
	}

	protected void showProgressDialog(String msg, boolean cancelable) {
		if (mProgressDialog == null) {
			mProgressDialog = new CustormProgressDialog(this);
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.setCancelable(cancelable);
		mProgressDialog.setMessage(msg);
		innerShowProgressDialog();
	}

	private void innerShowProgressDialog() {
		if (mProgressDialog != null) {
			if (isAttachedToWindow) {
				mProgressDialog.show();
			} else {
				needShowProgressDialogOnAttached = true;
			}
		}
	}

	protected void dismissProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	static class CustormProgressDialog extends ProgressDialog {

		public CustormProgressDialog(Context context, int theme) {
			super(context, theme);
		}

		public CustormProgressDialog(Context context) {
			super(context);
		}

		@Override
		public void setView(View view) {
			super.setView(view);
			configProgressBarStyle(view);
		}

		@Override
		public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
				int viewSpacingBottom) {
			super.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
			configProgressBarStyle(view);
		}

		private void configProgressBarStyle(View rootView) {
			// View v = rootView.findViewById(android.R.id.progress);
			// if (v instanceof ProgressBar) {
			// ProgressBar p = (ProgressBar) v;
			// p.setIndeterminateDrawable(v.getResources().getDrawable(
			// R.drawable.bg_progressbar_style_1));
			// }
		}
	}

}
