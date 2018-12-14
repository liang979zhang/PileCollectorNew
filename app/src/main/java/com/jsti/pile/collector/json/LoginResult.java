package com.jsti.pile.collector.json;

import com.google.gson.annotations.SerializedName;
import com.jsti.pile.collector.model.User;

public class LoginResult extends BaseResult {

	@SerializedName("user")
	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
