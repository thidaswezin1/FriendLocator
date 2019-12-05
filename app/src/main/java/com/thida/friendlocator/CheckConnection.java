package com.thida.friendlocator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import androidx.annotation.Nullable;


public class CheckConnection extends Service {
    Context context;
    public CheckConnection(Context context){
        this.context = context;
    }

    public boolean checkNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return (haveConnectedWifi || haveConnectedMobile);

    }

    public boolean checkGPS(){
        LocationManager locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
            /*AlertDialog dialog = new AlertDialog.Builder(context).create();

            dialog.setMessage("GPS is disable in your device");
            dialog.setButton(AlertDialog.BUTTON_POSITIVE,"Go to GPS Setting", (dialogInterface,i)->{
                dialog.dismiss();
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            });
            dialog.show();*/

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
