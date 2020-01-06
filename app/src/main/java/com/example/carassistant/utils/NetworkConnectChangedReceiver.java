package com.example.carassistant.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

/**
 * 检查网络状态切换 - 广播接受器
 *立
 */
public class NetworkConnectChangedReceiver extends BroadcastReceiver {

@SuppressLint("ResourceAsColor")
@Override
public void onReceive(Context context, Intent intent) {

        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
        Log.e("TAG", "wifiState:" + wifiState);
        switch (wifiState) {
        case WifiManager.WIFI_STATE_DISABLED:
        break;
        case WifiManager.WIFI_STATE_DISABLING:
        break;
        }
        }
        // 监听wifi的连接状态即是否连上了一个有效无线路由
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
        Parcelable parcelableExtra = intent
        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (null != parcelableExtra) {
        // 获取联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
        //获取的State对象则代表着连接成功与否等状态
        NetworkInfo.State state = networkInfo.getState();
        //判断网络是否已经连接
        boolean isConnected = state == NetworkInfo.State.CONNECTED;
        Log.e("TAG", "isConnected:" + isConnected);
        if (isConnected) {
        //Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show();
        } else {
        Toast.makeText(context, "连接失败", Toast.LENGTH_SHORT).show();
        }
        }
        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
        //获取联网状态的NetworkInfo对象
        NetworkInfo info = intent
        .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        if (info != null) {
        //如果当前的网络连接成功并且网络连接可用
        if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
        if (info.getType() == ConnectivityManager.TYPE_WIFI
        || info.getType() == ConnectivityManager.TYPE_MOBILE) {
        // Log.i("TAG", getConnectionType(info.getType()) + "连上");
                //Toast.makeText(context, "网络连接上", Toast.LENGTH_SHORT).show();
        }
        } else {
        // Log.i("TAG", getConnectionType(info.getType()) + "断开");
               // Toast.makeText(context, "网络已断开", Toast.LENGTH_SHORT).show();
        }

        }

        }
        }

}
