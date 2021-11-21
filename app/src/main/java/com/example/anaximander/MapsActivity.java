package com.example.anaximander;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.view.View;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private ActivityMapsBinding binding;
    public Activity act;
    private int length;
    private double[] trioToSubmit;
    private double latitude, longitude;
    private List<WeightedLatLng> latLngsToPlot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    }

    public void onMapButtonClick(View view) {
        Context context = getApplicationContext();
        if (view.getId() == R.id.ping_button) {
            act = this;
            //Note: retrieve our Lat,Long, and rssi
            trioToSubmit=Utility.fetchPlotStoreUserData(this,act,mMap);

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