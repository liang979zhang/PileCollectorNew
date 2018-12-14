package com.jsti.pile.collector.utils;

public class PileUtils {

	private PileUtils() {
	}

	public static int megerPileToInt(int bigPile, int smallPile) {
		return bigPile * 1000 + smallPile;
	}

	public static int getBigPileFromInt(int pile) {
		return pile / 1000;
	}

	public static int getSmallPileFromInt(int pile) {
		return pile % 1000;
	}

	public static String toPileString(int bigPile, int smallPile) {
		return "K" + bigPile + "+" + smallPile;
	}

	public static String toPileString(int pile) {
		return toPileString(getBigPileFromInt(pile), getSmallPileFromInt(pile));
	}
}
