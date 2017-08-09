package reloadonreconnect;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.net.NetworkInfo;
import java.lang.Runnable;

public class ReloadOnReconnect extends CordovaPlugin {
    boolean isConnected;
    BroadcastReceiver receiver;
    
    public void initialize (CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        
        final ConnectivityManager sockMan = (ConnectivityManager)cordova.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = sockMan.getActiveNetworkInfo();
        
        if (info != null)
            isConnected = info.isConnected();
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        
        if (this.receiver == null) {
            final CordovaWebView wv = webView;
            
            this.receiver = new BroadcastReceiver() {
                @Override
                public void onReceive (Context context, Intent intent) {
                    NetworkInfo info = sockMan.getActiveNetworkInfo();
                    
                    if (info != null && info.isConnected()) {
                        if (!isConnected) {
                            isConnected = true;
                            
                            wv.getView().post(new Runnable() {
                                @Override
                                public void run () {
                                    ((WebView)wv.getView()).reload();
                                }
                            });
                        }
                    } else {
                        isConnected = false;
                    }
                }
            };
            
            webView.getContext().registerReceiver(this.receiver, intentFilter);
        }
    }
    
    public void onDestroy () {
        if (this.receiver != null) {
            try {
                webView.getContext().unregisterReceiver(this.receiver);
            } catch (Exception e) {
            } finally {
                this.receiver = null;
            }
        }
    }
}
