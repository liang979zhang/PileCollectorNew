package com.jsti.pile.collector.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User  implements Serializable {
	@SerializedName("uid")
	private String uid;
	@SerializedName("userName")
	private String username;
	@SerializedName("loginName")
	private String loginName;
	@SerializedName("role")
	private int role;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}


	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
}
