package com.jsti.pile.collector.json;

import java.lang.reflect.Type;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson
 */
public class Json {
//	private static GsonBuilder gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
//
//		@Override
//		public boolean shouldSkipField(FieldAttributes f) {
//			return f.getAnnotation(ServerJsonSkip.class) != null;
//		}
//
//		@Override
//		public boolean shouldSkipClass(Class<?> clazz) {
//			return false;
//		}
//	}).create();

	private static Gson gson = new GsonBuilder().serializeNulls().create();


	private Json() {
	}

	public static <T> T parse(String json, Class<T> clz) {
		return gson.fromJson(json, clz);
	}

	public static <T> T parse(String json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}

	public static <T> String toJson(T data, Class<T> clz) {
		return gson.toJson(data, clz);
	}
}