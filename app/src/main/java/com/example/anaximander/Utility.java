package com.example.anaximander;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Utility extends MapsActivity {
    //Note: Create our obj for transporting lat, long & rssi
    private static double[] trioToSubmit = new double[3];


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

    public static void extractAndPlotCoords(Context context,String[] coordsArraySet,GoogleMap mMap) {

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

            //Note: For less data, take every ith test entry
            int i = 3;
            if (index % i == 0){
                //Note: Using these coords for heatmap testing
                //So we need to create a user for each of these coords.
                //double randomRssi = -1 * Math.random() * (120 - 30) + 30;

                Random r = new Random();
                int low = 30;
                int high = 120;
                int randomRssi = (r.nextInt(high-low) + low) * -1;

                //randomRssi = Math.floor(randomRssi);
                submitUserTestBatch(context,APlat,APlong,randomRssi,index);
            }



        }
    }

    public static double[] fetchPlotStoreUserData(Context context,Activity act,GoogleMap mMap){
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
                //Note: Update our lat, long & rssi trio obj
                trioToSubmit[0]=currLat;
                trioToSubmit[1]=currLong;
                trioToSubmit[2]=currentRssi;
                submitUser(context,currLat,currLong,currentRssi);
            }
        });
        return(trioToSubmit);
    }

    public static void submitUser(Context context, double latitude, double longitude, double rssi){
        Date date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String format = sdf.format(Calendar.getInstance().getTime());
        DAOUser dao = new DAOUser();
        User userForStoring = new User(latitude,longitude,rssi,android.os.Build.MODEL, date,format);
        dao.addWithTimeStampChildName(userForStoring,date.toString()).addOnSuccessListener(suc ->{
            bakeShortToast("Record is inserted",context);
        }).addOnFailureListener(er->{
            bakeShortToast(""+er.getMessage(),context);
        });
    }

    public static void submitUserTestBatch(Context context, double latitude, double longitude, double rssi,int index){
        Date date = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String format = sdf.format(Calendar.getInstance().getTime());
        DAOUser dao = new DAOUser();
        User userForStoring = new User(latitude,longitude,rssi,android.os.Build.MODEL, date,format);
        dao.addWithTimeStampChildName(userForStoring,Integer.toString(index)).addOnSuccessListener(suc ->{
            System.out.println("Record is inserted");
            //bakeShortToast("Record is inserted",context);
        }).addOnFailureListener(er->{
            bakeShortToast(""+er.getMessage(),context);
        });
    }

    public static void submitPing(double[] trioToSubmit,Context context){
        Date date = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        String format = sdf.format(Calendar.getInstance().getTime());

        //do something
        DAOUser dao = new DAOUser();
        //Note: Submit our new User data to the database
        User user = new User(trioToSubmit[0],trioToSubmit[1],trioToSubmit[2],android.os.Build.MODEL, date,format);
        //Add with generated key
//        dao.add(user).addOnSuccessListener(suc ->{
//            bakeShortToast("Record is inserted",context);
//        }).addOnFailureListener(er->{
//            bakeShortToast(""+er.getMessage(),context);
//        });
        //Add with custom key
        dao.addWithTimeStampChildName(user,date.toString()).addOnSuccessListener(suc ->{
            bakeShortToast("Record is inserted",context);
        }).addOnFailureListener(er->{
            bakeShortToast(""+er.getMessage(),context);
        });

    }

    public static void resetDataFromToday(Context context,String mmddyy){
        //Note: Deletes all data from today format "MM/dd/yy
        DAOUser dao = new DAOUser();
        dao.clearAllFirebaseDatafromToday(mmddyy);
        bakeShortToast("Firebase Destroyed Successfully",context);
    }

    public static List<WeightedLatLng> addHeatMapCoords(Context context, GoogleMap map) {
        List<WeightedLatLng> latLngs;

        // Get the data: latitude/longitude positions of point list.
        DAOUser dao = new DAOUser();
        latLngs = dao.getAllFireBaseLatLngs();
        bakeShortToast("Data to Plot: Updated Successfully",context);
        return latLngs;
    }

    public static void drawHeat(List<WeightedLatLng> WightedLatLngs,GoogleMap map,Context context){
        if (WightedLatLngs!=null){
            //Note: Please see link:
            //https://developers.google.com/maps/documentation/android-sdk/utility/heatmap
            // Create the gradient.
            //Note: "A gradient is created using two arrays:"
            //"an integer array containing the colors"
            int[] colors = {
                    Color.rgb(255, 0, 0), //red
                    Color.rgb(255, 164, 0), //orange
                    Color.rgb(0, 255, 62), //green
                    Color.rgb(0, 135, 255), //blue
                    Color.rgb(162, 0, 255) //violet
            };
            //"and a float array indicating the starting point for each color"
            //"given as a percentage of the maximum intensity,"
            //"and expressed as a fraction from 0 to 1"
            float[] startPoints = {
                    0.005f,0.40f,0.7f,0.85f,0.97f
            };
            Gradient gradient = new Gradient(colors, startPoints);

            // Create a heat map tile provider, pass it our coords
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .weightedData(WightedLatLngs)
                    .gradient(gradient)
                    .radius(50)
                    .build();
            provider.setMaxIntensity(1);
            // Add the tile overlay to the map.
            TileOverlay tileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
            provider.setOpacity(0.25);
            tileOverlay.clearTileCache();
        } else {
            bakeShortToast("No Data to Map!",context);
        }
    }
}
