package com.example.anaximander;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.anaximander.databinding.ActivityMapsBinding;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private int passiveFlag = 0;

    public GoogleMap mMap;
    private ActivityMapsBinding binding;
    public Activity act;
    private int length;
    private double[] trioToSubmit;
    private double latitude, longitude;
    private List<WeightedLatLng> latLngsToPlot;
//    TextView rssiTextView = (TextView) findViewById(R.id.rssiTextView);
    private FusedLocationProviderClient fusedLocationClient;
    private Runnable runnable;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future longRunningTaskFuture;
    private int stopFlag;
    private Handler handler = new Handler();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        TextView rssiTextView = (TextView) findViewById(R.id.rssiTextView);
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng home = new LatLng(37.2714638, -76.7129266);
        mMap.addMarker(new MarkerOptions().position(home).title("Chancellors Hall"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(home));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(home, 17.0f));
        mMap.setMapType(2); //style

        //new

    }



    public void onMapButtonClick(View view) {
//        int passiveFlag = 0;
        Context context = getApplicationContext();
        if (view.getId() == R.id.ping_button) {

            act = this;
            //Note: retrieve our Lat,Long, and rssi
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            trioToSubmit=Utility.fetchPlotStoreUserData(context,act,mMap,locationManager);
            trioToSubmit=Utility.fetchPlotStoreUserDataNew(context,act,mMap,locationManager);


            //Note: update the textview
            TextView rssiTextView = findViewById(R.id.rssiTextView);
            int signalStrength = Utility.getSignalStrength(act, context);
            rssiTextView.setText("RSSI: "+signalStrength+"dBm");

        }else if(view.getId() == R.id.passive_ping_button){
            if(passiveFlag==0){
//                passiveFlag=1;
//                Handler handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
//                        while(1==1){
//                        for(int i=0;i<100;i++){
                        // repeat every 2 seconds
                        handler.postDelayed(runnable, 2*1000);
                        act = MapsActivity.this;
                        //Note: retrieve our Lat,Long, and rssi
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        //            trioToSubmit=Utility.fetchPlotStoreUserData(context,act,mMap,locationManager);
                        trioToSubmit=Utility.fetchPlotStoreUserDataNew(context,act,mMap,locationManager);
                        //Note: update the textview
                        TextView rssiTextView = findViewById(R.id.rssiTextView);
                        int signalStrength = Utility.getSignalStrength(act, context);
                        rssiTextView.setText("RSSI: "+signalStrength+"dBm");
                        Button passive_button = (Button)findViewById(R.id.passive_ping_button);
                        passive_button.setText("Passive Ping Enabled");
                        passive_button.setBackgroundColor(getResources().getColor(R.color.green));

                    }
                };
                runnable.run();
                longRunningTaskFuture = executorService.submit(runnable);



            }
//            act = this;
//            //Note: retrieve our Lat,Long, and rssi
//            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
////            trioToSubmit=Utility.fetchPlotStoreUserData(context,act,mMap,locationManager);
//            trioToSubmit=Utility.fetchPlotStoreUserDataNew(context,act,mMap,locationManager);
//            //Note: update the textview
//            TextView rssiTextView = findViewById(R.id.rssiTextView);
//            int signalStrength = Utility.getSignalStrength(act, context);
//            rssiTextView.setText("RSSI:\n           "+signalStrength+"dBm");
//            Button passive_button = (Button)findViewById(R.id.passive_ping_button);
//            passive_button.setText("Passive Ping Enabled");
//            passive_button.setBackgroundColor(getResources().getColor(R.color.green));
        }else if(view.getId() == R.id.zoomIn_button){
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        } else if(view.getId() == R.id.zoomOut_button){
            mMap.animateCamera(CameraUpdateFactory.zoomOut());

        }else if(view.getId() == R.id.pull_button){
            // Note: Pull coordinates of all ping Users in Firebase
            // We will use these coordinates to plot our map
            latLngsToPlot = Utility.addHeatMapCoords(context,mMap);
        } else if(view.getId() == R.id.map_button){
            // Note: Create out heatmap from our pulled User coordinates
            System.out.println(latLngsToPlot);
            Utility.drawHeat(latLngsToPlot,mMap,context);
        }else if(view.getId() == R.id.plotBuildingCoords_button){
            //Note: Read coords text file & return array of entries
            String[] coordsArraySet = Utility.readCoordsFile(context);
            length=coordsArraySet.length;
            //Note: Now we have verbose entries for each coordinate
            //      Now, extract the coordinates from each entry &
            //      make a marker on the map for each
            //Note: We pass an array twice the size of the verbose array
            //      This is because there are LatLong pairs for each
            Utility.extractAndPlotCoords(context,coordsArraySet,mMap);
        } else if(view.getId() == R.id.clear_button){
            mMap.clear();
        //} else if(view.getId() == R.id.store_button){
            //Note: Ping now stores automatically. No need to call this
            //DEPRECIATED Note: Submit out Lat, Long, & rssi to database
            //DEPRECIATED Utility.submitPing(trioToSubmit,context);
        } else if(view.getId() == R.id.resetDatabase_button){
            //Note: get formatted date that we use to identify children to delete
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
            String format = sdf.format(Calendar.getInstance().getTime());
            //Note: Delete all entries with today's date
            Utility.resetDataFromToday(context,format);
        }


    }
}