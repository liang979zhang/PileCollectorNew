package com.jsti.pile.collector.common;

import java.util.ArrayList;
import java.util.List;

import com.jsti.pile.collector.json.Json;
import com.jsti.pile.collector.model.Expressway;
import com.jsti.pile.collector.model.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * 系统属性信息本地化工具
 * 
 */
public class AppProfiles {
	private final static String PROFILE_FILE_NAME = "system_sp";
	private SharedPreferences sp;

	private AppProfiles(Context context) {
		if (sp == null) {
			sp = context.getSharedPreferences(PROFILE_FILE_NAME, 0);
			sp.registerOnSharedPreferenceChangeListener(share_pre_Listener);
		}
	}

	private static AppProfiles instance;

	public static synchronized AppProfiles getInstance(Context context) {
		if (instance == null) {
			instance = new AppProfiles(context);
		}
		return instance;
	}

	private List<PreferencesObserver> observers = new ArrayList<PreferencesObserver>();
	private OnSharedPreferenceChangeListener share_pre_Listener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			for (PreferencesObserver ob : observers) {
				ob.onPreferencesChanged(sp, key);
			}
		}
	};

	public interface PreferencesObserver {
		public void onPreferencesChanged(SharedPreferences sp, String key);
	}

	// --------------------------------------------------------------------
	/**
	 * 请小心使用该方法，必须配合unregister在不需要的时候取消注册，否则会出现内存泄露
	 * 
	 * @param observer
	 */
	public void registerObserver(PreferencesObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void unregisterObserver(PreferencesObserver observer) {
		if (observer != null) {
			observers.remove(observer);
		}
	}

	// ----------------------------------------------------------------------
	public static final String KEY_LAST_LOGIN_USER = "KEY_LAST_LOGIN_USER";
	public static final String KEY_LAST_LOGIN_USER_PWD = "KEY_USER_TOKEN";
	public static final String KEY_LAST_LOGIN_USER_ACOUNT = "KEY_LAST_LOGIN_USER_ACOUNT";
	public static final String KEY_LAST_LOGIN_USER_ENTITY = "KEY_LAST_LOGIN_USER_ENTITY";
	public static final String KEY_MY_ANSWER_COUNT = "KEY_MY_ANSWER_COUNT";
	public static final String KEY_MY_QUESTION_COUNT = "KEY_MY_QUESTION_COUNT";
	public static final String KEY_MY_ANSWER_TASK_COUNT = "KEY_MY_ANSWER_TASK_COUNT";
	public static final String KEY_ROAD_PATROL_TASK_JSON = "KEY_ROAD_PATROL_TASK_JSON";
	public static final String KEY_EXPRESSWAY_LIST = "KEY_EXPRESSWAY_LIST";

	public User getLoginUser() {
		String json = sp.getString(KEY_LAST_LOGIN_USER, null);
		if (json == null) {
			return null;
		}
		return Json.parse(json, User.class);
	}

	public void logout() {
		sp.edit().remove(KEY_LAST_LOGIN_USER).commit();
	}

	public void saveLoginUser(User user) {
		String userJson = null;
		if (user != null) {
			userJson = Json.toJson(user, User.class);
		}
		sp.edit().putString(KEY_LAST_LOGIN_USER, userJson).commit();
	}

	public String getLoginAcount() {
		return sp.getString(KEY_LAST_LOGIN_USER_ACOUNT, null);
	}

	public String getLoginPassword() {
		return sp.getString(KEY_LAST_LOGIN_USER_PWD, null);
	}

	public void updateLastLoginInfo(String account, String pwd) {
		sp.edit().putString(KEY_LAST_LOGIN_USER_ACOUNT, account).putString(KEY_LAST_LOGIN_USER_PWD, pwd).commit();
	}

	class ExpresswayData {
		public List<Expressway> data;
	}

	public void setExpresswayData(List<Expressway> data) {
		ExpresswayData ed = new ExpresswayData();
		ed.data = data;
		String jsonStr = Json.toJson(ed, ExpresswayData.class);
		sp.edit().putString(KEY_EXPRESSWAY_LIST, jsonStr).commit();
	}

	public List<Expressway> getExpresswayData() {
		String jsonStr = sp.getString(KEY_EXPRESSWAY_LIST, null);
		if (jsonStr == null || jsonStr.length() <= 0) {
			return null;
		}
		ExpresswayData ed = null;
		try {
			ed = Json.parse(jsonStr, ExpresswayData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ed == null) {
			return null;
		}
		return ed.data;
	}
}