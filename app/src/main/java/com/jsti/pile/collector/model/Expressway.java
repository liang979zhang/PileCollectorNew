package com.jsti.pile.collector.model;

import com.google.gson.annotations.SerializedName;

public class Expressway {
	@SerializedName("id")
	private String id;// 高速公路id

	@SerializedName("code")
	private String code;// 高速代号

	@SerializedName("name")
	private String name;// 高速公路名称

	@SerializedName("scity")
	private String scity;// 高速开始的城市

	@SerializedName("ecity")
	private String ecity;// 高速结束的城市

	@SerializedName("upDirection")
	private String upDirection;

	@SerializedName("downDirection")
	private String downDirection;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUpDirection() {
		return upDirection;
	}

	public void setUpDirection(String upDirection) {
		this.upDirection = upDirection;
	}

	public String getDownDirection() {
		return downDirection;
	}

	public void setDownDirection(String downDirection) {
		this.downDirection = downDirection;
	}
}
