package com.example.anaximander;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;

public class Utility extends MapsActivity {

    public static void CheckPermissionsTest(Activity activity, Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("Permissions are not granted, now requesting permissions.");
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        } else {
            System.out.println("Permissions are already granted. Continuing.");
        }

    }

    public static CancellationToken getToken(){
        //Note: We need a cancellation token for our getCurrentLocation function
        CancellationTokenSource source = new CancellationTokenSource();
        CancellationToken token = source.getToken();
        return(token);
    }

    public static Task<Location> startLocationTask(Activity activity, Context context){
        //Note: We need a cancellation token for our getCurrentLocation function
        CancellationToken token = getToken();
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(activity);
        //Note: Create our locationTask that retrieves CURRENT location from our fusedLocationClient
        CheckPermissionsTest(activity,context);
        @SuppressLint("MissingPermission") Task<Location> locationTask = client.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token);
        return locationTask;
    }

    public static void bakeShortToast(String toastText,Context context){
        //Note: We create a toast in anticipation of our success
        Toast toastLocationSuccess = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
        //Note: Toast to our success
        toastLocationSuccess.show();

    }

}
