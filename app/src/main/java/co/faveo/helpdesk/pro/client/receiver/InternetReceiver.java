package co.faveo.helpdesk.pro.client.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.greenrobot.eventbus.EventBus;

import co.faveo.helpdesk.pro.client.application.FaveoApplication;
import co.faveo.helpdesk.pro.client.application.MessageEvent;

/**
 * Created by narendra on 13/06/16.
 * This class is for checking whether internet
 * is available or not,it will also check if there is
 * any network changes or not.
 */
public class InternetReceiver extends BroadcastReceiver {

    //public static InternetReceiverListener internetReceiverListener;

    public InternetReceiver() {
        super();
    }

    /**
     * Function for checking Internet is available or not.
     *
     * @return
     */
    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) FaveoApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        EventBus.getDefault().post(new MessageEvent(isConnected));

//        if (internetReceiverListener != null) {
//            internetReceiverListener.onNetworkConnectionChanged(isConnected);
//        }
    }

//    public interface InternetReceiverListener {
//        void onNetworkConnectionChanged(boolean isConnected);
//    }

}
