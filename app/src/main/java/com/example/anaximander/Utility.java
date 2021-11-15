package com.example.anaximander;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.InputStream;
import java.util.Scanner;

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
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 1);
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

    public static int getSignalStrength(Activity activity,Context context){
        CheckPermissionsTest(activity,context);
        WifiManager manager=(WifiManager)activity.getSystemService(Context.WIFI_SERVICE);
        //See documentation: https://developer.android.com/reference/android/net/wifi/WifiInfo
        int rssi = manager.getConnectionInfo().getRssi();
        return rssi;
    }

    public static String[] readCoordsFile(Context context)
    {
        String[] coordsArray;
        //Note: Retrieves text file and converts to string
        InputStream inputStream = context.getResources().openRawResource(R.raw.coords_clean);
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        coordsArray = getCoordsVerbose(result);
        return coordsArray;
    }

//    public static Double[] getCoords(String coordsText){
    public static String[] getCoordsVerbose(String coordsText){
        int index = 0;
        String noPara= "";
        String parsedString=coordsText;
        String[] parsedCoordsArray;

        //format: (1,1,37.27640915,-76.70839691)
        parsedString=parsedString.replaceAll("[()]"," ");
        parsedString=parsedString.replaceAll(" , ","  ");
        parsedString=parsedString.replaceAll(","," ");
        parsedCoordsArray = parsedString.split("\\s\\s+");

        return parsedCoordsArray;
    }

    public static void extractAndPlotCoords(String[] coordsArraySet,GoogleMap mMap) {

        int longIndex = 0;
        double APlat, APlong;
        String subject;
        String[] extractedCoords = new String[coordsArraySet.length*2];

        // iterating over our array
        for (int index = 0; index < coordsArraySet.length; index++) {

            //Note: Fetch a subject string
            subject = coordsArraySet[index];
            //Note: Add first Longitude to position 0
            extractedCoords[longIndex] = subject.substring(subject.length() - 11, subject.length());
            //Note: Add first Latitude to position 1
            extractedCoords[longIndex + 1] = subject.substring(subject.length() - 24, subject.length() - 13);
            //Note: Fetch and store in string vars
            String longitude = extractedCoords[longIndex];
            String latitude = extractedCoords[longIndex + 1];
            //Note: Convert string values to decimals
            APlat = Double.parseDouble(latitude);
            APlong = Double.parseDouble(longitude) * -1;
            //Note create our latLong obj for map
            LatLng home = new LatLng(APlat, APlong);
            //Note: Add the marker for the coord pair
            mMap.addMarker(new MarkerOptions().position(home).title("AP"));
            longIndex += 2; //Incriment our index var
        }
    }

    public static void fetchAndPlotUserLocation(Context context,Activity act,GoogleMap mMap){
        //Note: Create our locationTask that retrieves CURRENT location from a fusedLocationClient
        Task<Location> locationTask = Utility.startLocationTask(act,context);
        //Note: We have to wait until the task is completed before operating on it
        locationTask.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                double currLat,currLong;
                LatLng currentLocationLatLng;
                int currentRssi;
                MarkerOptions marker;
                //Note: Retrieve our results
                Location currentLocation = (Location) locationTask.getResult();
                //Note: Store results in our current Lat & Long variables
                currLat = currentLocation.getLatitude();
                currLong= currentLocation.getLongitude();
                //Note: Update our currentLocationLatLng variable
                currentLocationLatLng = new LatLng(currLat, currLong);
                //Note: fetch signal strength information
                currentRssi = getSignalStrength(act,context);

                //Note: Add a new marker to the map, and pan camera toward it
                marker = new MarkerOptions().position(currentLocationLatLng).title
                        ("rssi: " + currentRssi +  "dBm || " + currentLocationLatLng);
                //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location));
                //Note: Add a new marker to the map, and pan camera toward it
                mMap.addMarker(marker);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 18.0f));

                //Note: Toast to our success
                Utility.bakeShortToast("Ping Successful" , context);
            }
        });
    }
}