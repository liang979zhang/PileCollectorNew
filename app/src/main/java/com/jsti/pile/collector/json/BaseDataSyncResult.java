package com.jsti.pile.collector.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.jsti.pile.collector.model.Expressway;

public class BaseDataSyncResult extends BaseResult {
	@SerializedName("expressways")
	private List<Expressway> expressways;
	@SerializedName("ver")
	private String ver;

	public List<Expressway> getExpressways() {
		return expressways;
	}

	public void setExpressways(List<Expressway> expressways) {
		this.expressways = expressways;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}
}
