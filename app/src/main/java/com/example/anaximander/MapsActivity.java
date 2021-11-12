package com.example.anaximander;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.anaximander.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private double currLat,currLong;
    private LatLng currentLocationLatLng;
    private int rssi;


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
        mMap.setMapType(2); //https://developers.google.com/android/reference/com/google/android/gms/maps/GoogleMap?hl=en#setMapType(int)
    }

    public void onMapButtonClick(View view) {
        if (view.getId() == R.id.ping_button) {
            Context context = getApplicationContext();
            //Note: Create our locationTask that retrieves CURRENT location from a fusedLocationClient
            Task<Location> locationTask = Utility.startLocationTask(this,context);
            //Note: We have to wait until the task is completed before operating on it
            locationTask.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    //Note: Retrieve our results
                    Location currentLocation = (Location) locationTask.getResult();
                    //Note: Store results in our current Lat & Long variables
                    currLat = currentLocation.getLatitude();
                    currLong= currentLocation.getLongitude();
                    //Note: Update our currentLocationLatLng variable
                    currentLocationLatLng = new LatLng(currLat, currLong);
                    //Note: fetch signal strength information
                    rssi = Utility.getSignalStrength(MapsActivity.this,context);
                    //Note: Add a new marker to the map, and pan camera toward it
                    mMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title
                            ("rssi: " + rssi +  "dBm || " + currentLocationLatLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 18.0f));
                    //Note: Toast to our success
                    Utility.bakeShortToast("Ping Successful" , context);
                }
            });

        }else if(view.getId() == R.id.zoomIn_button){
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }else if(view.getId() == R.id.zoomOut_button){
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }

    }
}