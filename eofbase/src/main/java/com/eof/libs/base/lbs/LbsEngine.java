package com.eof.libs.base.lbs;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.eof.libs.base.GlobalData;

import java.util.List;

/**
 * Created by guodanyang on 2017/7/7.
 */

public class LbsEngine {

    private LocationManager mLocationManager;
    private String mLocationProvider;
    private Location mLocation;

    private LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            // 如果位置发生变化,重新显示
//            showLocation(location);

        }
    };

    private static class SingletonContainer {
        public static LbsEngine mSingleInstance = new LbsEngine();
    }

    public static LbsEngine getInstance() {
        return LbsEngine.SingletonContainer.mSingleInstance;
    }

    public Location getLocation() {
        return mLocation;
    }

    private LbsEngine() {
        mLocationManager = (LocationManager) GlobalData.mApplicationContext.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            // 如果是GPS
            mLocationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            // 如果是Network
            mLocationProvider = LocationManager.NETWORK_PROVIDER;
        }

        // 获取Location
        mLocation = mLocationManager.getLastKnownLocation(mLocationProvider);
//        // 监视地理位置变化
//        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
    }

}
