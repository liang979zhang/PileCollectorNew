package com.jsti.pile.collector.widgets;

import com.jsti.pile.collector.R;
import com.jsti.pile.collector.common.CommonParams;
import com.jsti.pile.collector.model.Pile;
import com.jsti.pile.collector.utils.PileUtils;
import com.jsti.pile.collector.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 桩号确认Dialog
 */
public class PileConfirmDialog extends Dialog {
	private TextView mStartPileTv;
	private EditText mBigPileEd;
	private EditText mSmallPileEd;
	private TextView mPilePreview;
	private Button okBtn;

	private final PileConfirmCallback mPileConfirmCallback;

	public PileConfirmDialog(Context context, PileConfirmCallback c) {
		super(context);
		mPileConfirmCallback = c;
		setContentView(R.layout.dialog_pile_confirm);
		//
		mStartPileTv = (TextView) findViewById(R.id.start_pile);
		mBigPileEd = (EditText) findViewById(R.id.big_pile);
		mBigPileEd.setOnTouchListener(mOnTouchListener);
		mSmallPileEd = (EditText) findViewById(R.id.small_pile);
		mSmallPileEd.setOnTouchListener(mOnTouchListener);
		mPilePreview = (TextView) findViewById(R.id.pile_preview);
		findViewById(R.id.cancle).setOnClickListener(viewOnClickListener);
		okBtn = (Button) findViewById(R.id.ok);
		okBtn.setOnClickListener(viewOnClickListener);
		//
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(lp);
		//
		setCanceledOnTouchOutside(false);
	}

	private final OnTouchListener mOnTouchListener = new OnTouchListener() {

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			stopCountdown();
			okBtn.setText("确认录入");
			return false;
		}
	};

	private final View.OnClickListener viewOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.cancle:
				if (mPileConfirmCallback != null) {
					mPileConfirmCallback.onConfirmCancled(mFoundPile);
				}
				dismiss();
				break;
			case R.id.ok:
				makePileConfirmFinish();
				break;
			}
		}
	};

	@Override
	public void dismiss() {
		stopCountdown();
		super.dismiss();
	}

	private void stopCountdown() {
		okBtn.removeCallbacks(countdownRunnable);
	}

	private boolean makePileConfirmFinish() {
		stopCountdown();
		if (!isWattingConfirm) {
			Log.d("PileConfirm", "pile was confirmed,so ignore this once");
			return true;
		}
		isWattingConfirm = false;
		Integer big = safeValueToInt(mBigPileEd);
		Integer small = safeValueToInt(mSmallPileEd);
		if (big == null || small == null) {
			Utils.toast(getContext(), "添加失败，请填写合法的桩号");
			isWattingConfirm = true;
			return false;
		}
		if (big < 0 || small < 0) {
			Utils.toast(getContext(), "添加失败，桩号不能为负数");
			isWattingConfirm = true;
			return false;
		}
		if (mPileConfirmCallback != null) {
			mPileConfirmCallback.onConfirmAccept(mFoundPile, PileUtils.megerPileToInt(big, small),
					mFoundPile.getNumber());
		}
		return true;
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

	private Pile mFoundPile;
	private int mStartPileNumber;
	private int countdownSeconds;

	private boolean isWattingConfirm = false;

	public void setConfirmInfo(Pile foundPile, int startPileNumber) {
		stopCountdown();
		isWattingConfirm = true;
		countdownSeconds = CommonParams.PILE_CONFIRM_MAX_SECONDS;
		this.mStartPileNumber = startPileNumber;
		this.mFoundPile = foundPile;
		//
		mStartPileTv.setText(PileUtils.toPileString(mStartPileNumber));
		mBigPileEd.setText(String.valueOf(PileUtils.getBigPileFromInt(mFoundPile.getNumber())));
		mSmallPileEd.setText(String.valueOf(PileUtils.getSmallPileFromInt(mFoundPile.getNumber())));
		mPilePreview.setText(PileUtils.toPileString(mFoundPile.getNumber()));
	}

	@Override
	public void show() {
		super.show();
		okBtn.removeCallbacks(countdownRunnable);
		okBtn.post(countdownRunnable);
	}

	private final Runnable countdownRunnable = new Runnable() {
		@Override
		public void run() {
			if (countdownSeconds > 0) {
				okBtn.setText(String.format("确认录入(%d)", countdownSeconds));
				countdownSeconds--;
				okBtn.removeCallbacks(this);
				okBtn.postDelayed(this, 1000);
			} else {
				stopCountdown();
				okBtn.setText("确认录入");
				makePileConfirmFinish();
			}
		}
	};

	public interface PileConfirmCallback {
		public void onConfirmAccept(Pile pile, int newPileNumber, int olderPileNumber);

		public void onConfirmCancled(Pile pile);
	}
}
