package com.jsti.pile.collector.model;

import java.io.Serializable;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 桩号
 */
public class Pile implements Parcelable, Serializable {
	private static final long serialVersionUID = 6511433183872943004L;
	public static final int TYPE_EXACT_PILE = 1;// 服务器接口状态值修改
	public static final int TYPE_NOT_EXACT_PILE = 3;//轨迹
	@SerializedName("n")
	private int n;// 桩号（100150=k100+150）
	@SerializedName("lo")
	private double lo;// longitude经度
	@SerializedName("la")
	private double la;// latitude维度
	@SerializedName("t")
	private long t;// 采的时间戳
	@SerializedName("pt")
	private int pt;// 0:精准桩号，1:非精准桩号
	@SerializedName("cd")
	private String cd;

	public Pile() {
	}

	private Pile(Parcel in) {
		readFromParcel(in);
	}

	public Pile(int pileNumber) {
		n = pileNumber;
	}

	public void readFromParcel(Parcel in) {
		n = in.readInt();
		lo = in.readDouble();
		la = in.readDouble();
		t = in.readLong();
		pt = in.readInt();
		cd = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(n);
		dest.writeDouble(lo);
		dest.writeDouble(la);
		dest.writeLong(t);
		dest.writeInt(pt);
		dest.writeString(cd);
	}

	public void copy(Pile p) {
		this.n = p.n;
		this.lo = p.lo;
		this.la = p.la;
		this.t = p.t;
		this.pt = p.pt;
		this.cd = p.cd;
	}

	public int getNumber() {
		return n;
	}

	public void setNumber(int number) {
		this.n = number;
	}

	public double getLongitude() {
		return lo;
	}

	public void setLongitude(double longitude) {
		this.lo = longitude;
	}

	public double getLatitude() {
		return la;
	}

	public void setLatitude(double latitude) {
		this.la = latitude;
	}

	public long getCollectTime() {
		return t;
	}

	public void setCollectTime(long collectTime) {
		this.t = collectTime;
	}

	public int getPileType() {
		return pt;
	}

	public void setPileType(int pileType) {
		this.pt = pileType;
	}

	public int getBigPile() {
		return n / 1000;
	}

	public int getSmallPile() {
		return n % 1000;
	}

	public String getCollectDirection() {
		return cd;
	}

	public void setCollectDirection(String collectDirection) {
		this.cd = collectDirection;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String toString() {
		return "Pile [n=" + n + ", lo=" + lo + ", la=" + la + ", t=" + t + ", pt=" + pt + ", cd=" + cd + "]";
	}

	// ---------------
	public static final Parcelable.Creator<Pile> CREATOR = new Parcelable.Creator<Pile>() {
		public Pile createFromParcel(Parcel in) {
			return new Pile(in);
		}

		public Pile[] newArray(int size) {
			return new Pile[size];
		}
	};

	public static String formatSimpleString(Pile p) {
		return formatSimpleString(p.getBigPile(), p.getSmallPile());
	}

	public static String formatSimpleString(int bigPile, int smallPile) {
		return String.format(Locale.getDefault(), "K%d + %03d", bigPile, smallPile);
	}
}
