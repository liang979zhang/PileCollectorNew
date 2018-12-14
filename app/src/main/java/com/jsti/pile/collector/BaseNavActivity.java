package com.jsti.pile.collector;

import com.jsti.pile.collector.widgets.NavigationBar;
import com.jsti.pile.collector.widgets.NavigationBar.NavgationListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

public abstract class BaseNavActivity extends BaseActivity implements NavgationListener {
	private NavigationBar mNavigationBar;

	protected NavigationBar getNavigationBar() {
		if (mNavigationBar == null) {
			mNavigationBar = (NavigationBar) findViewById(R.id.nav_bar);
			mNavigationBar.setNavgationListener(this);
		}
		return mNavigationBar;
	}

	protected void checkGPS() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS模块是否开启，如果没有则开启
		if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "请打开GPS", Toast.LENGTH_SHORT).show();
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("请打开GPS");
			dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// 转到手机设置界面，用户设置GPS
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
				}
			}).setNeutralButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			}).show();
		}
	}
}
