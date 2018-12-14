package com.jsti.pile.collector.common;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;

import android.content.Context;
import android.util.Log;

/**
 * 定位管理
 *
 */
public class LocationManager {
	private static final String TAG = "LocationManager";
	private Context mApplication;
	private LocationClient mLocationClient;
	private final LocationListener mLocationListener;

	public LocationManager(Context context, LocationListener l) {
		mLocationListener = l;
		mApplication = context.getApplicationContext();
		SDKInitializer.initialize(mApplication);
		mLocationClient = new LocationClient(mApplication);
		initLoctionClient(mLocationClient);
		mLocationClient.registerLocationListener(mBDLocationListener);
	}

	private void initLoctionClient(LocationClient lc) {
		LocationClientOption option = new LocationClientOption();
		// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType(CommonParams.GPS_TYPE);// 可选，默认gcj02，设置返回的定位结果坐标系，
		option.setScanSpan(1000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(false);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		option.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setNeedDeviceDirect(false);// 可选，设置是否需要设备方向结果
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setIsNeedAltitude(false);// 可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
		lc.setLocOption(option);
	}

	private BDLocationListener mBDLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			handlerNewLocation(location);
		}
	};
	private boolean startFinish = false;
	private boolean findError = false;

	/**
	 * 处理新定位的位置
	 * 
	 * @param loc
	 */
	private void handlerNewLocation(BDLocation loc) {
		boolean locSuccess = false;
		String msg = null;
		switch (loc.getLocType()) {
		case BDLocation.TypeGpsLocation:// GPS定位结果
		case BDLocation.TypeNetWorkLocation:// 网络定位结果
		case BDLocation.TypeOffLineLocation:// 离线定位结果
			// that's ok
			locSuccess = true;
			break;
		case BDLocation.TypeServerError:
			msg = "Error:定位服务出现错误";
			break;
		case BDLocation.TypeNetWorkException:
			// 网络不同导致定位失败，请检查网络是否通畅
			msg = "Error:网络不同导致定位失败，请检查网络是否通畅";
			break;
		case BDLocation.TypeCriteriaException:
			// 无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机
			msg = "Error:请确认手机网络通常，且不是飞行模式";
			break;
		}
		if (CommonParams.DEBUG) {
			Log.d(TAG, "newLocation:(" + loc.getLatitude() + "," + loc.getLongitude() + "), success:" + locSuccess);
		}
		findError = !locSuccess;
		if (locSuccess) {
			if (!startFinish) {
				startFinish = true;
				return;
			}
			mLocationListener.onNewLatLng(loc.getLongitude(), loc.getLatitude());
		} else {
			mLocationListener.onFindError(msg);
		}
	}

	public interface LocationListener {
		public void onNewLatLng(double longitude, double latitude);

		public void onFindError(String msg);
	}

	public boolean isRunning() {
		return mLocationClient.isStarted();
	}

	public void start() {
		synchronized (this) {
			if (mLocationClient != null && !mLocationClient.isStarted()) {
				mLocationClient.start();
			}
		}
	}

	public void stop() {
		synchronized (this) {
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.stop();
			}
		}
	}

	public boolean haveError() {
		return findError;
	}
}
