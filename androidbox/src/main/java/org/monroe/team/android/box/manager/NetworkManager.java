package org.monroe.team.android.box.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.monroe.team.android.box.Closure;


/*
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 */
public class NetworkManager {

    private final Context context;

    public static Closure<NetworkInfo, Boolean> CHECK_CONNECTION = new Closure<NetworkInfo, Boolean>() {
        @Override
        public Boolean execute(NetworkInfo networkInfo) {
            return networkInfo.isConnected();
        }
    };

    public static Closure<NetworkInfo, Boolean> CHECK_AVAILABILITY = new Closure<NetworkInfo, Boolean>() {
        @Override
        public Boolean execute(NetworkInfo networkInfo) {
            return networkInfo.isAvailable();
        }
    };

    public static Closure<NetworkInfo, Boolean> CHECK_IF_WIFI_CONNECTION = new Closure<NetworkInfo, Boolean>() {
        @Override
        public Boolean execute(NetworkInfo networkInfo) {
            return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
    };

    public NetworkManager(Context context) {
        this.context = context;
    }

    public boolean isUsingWifi(){
        return isInterface(activeInterface(), false,
                CHECK_IF_WIFI_CONNECTION,
                CHECK_CONNECTION);
    }

    public boolean isInterface(int connectivityManagerType, boolean atLeastOne, Closure<NetworkInfo, Boolean>... checks){
        final NetworkInfo interFace = networkInterface(connectivityManagerType);
        return isInterface(interFace, atLeastOne, checks);
    }

    public boolean isInterface(NetworkInfo interFace, boolean atLeastOne, Closure<NetworkInfo, Boolean>... checks) {
        if (interFace == null) return false;
        for (Closure<NetworkInfo, Boolean> check : checks) {
            boolean checkResult = check.execute(interFace);
            if (checkResult && atLeastOne) return true;
            if (!checkResult && !atLeastOne) return false;
        }
        return !atLeastOne;
    }

    public NetworkInfo activeInterface(){
        return getConnectivityManager().getActiveNetworkInfo();
    }

    private NetworkInfo networkInterface(int connectivityManagerType) {
        final ConnectivityManager connMgr = getConnectivityManager();
        return connMgr.getNetworkInfo(connectivityManagerType);
    }

    private ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

}
