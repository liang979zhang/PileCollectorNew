package com.jsti.pile.collector.json;

import com.google.gson.annotations.SerializedName;

import android.text.TextUtils;

import java.io.Serializable;

public class BaseResult implements Serializable {
	@SerializedName("result")
	private Result result;

	public boolean isSuccess() {
		return result != null && result.isSuccess();
	}

	public String getMessage() {
		if (result == null) {
			return null;
		}
		return result.getMessage();
	}

	public Result getResult() {
		return result;
	}

	public static class Result {
		@SerializedName("code")
		private int code;
		@SerializedName("message")
		private String message;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public boolean isSuccess() {
			return code == 0;
		}
	}
}
