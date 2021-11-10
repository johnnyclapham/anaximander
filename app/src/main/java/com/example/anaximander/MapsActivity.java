package com.example.anaximander;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
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
    private FusedLocationProviderClient fusedLocationClient;
    private double currLat,currLong;
    private LatLng currentLocationLatLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                System.out.println("Permissions are not granted, now requesting permissions.");
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            } else {
                System.out.println("Permissions are already granted. Continuing.");
            }

            //Note: We create a toast in anticipation of our success
            Toast toastLocationSuccess = Toast.makeText(this, "User Located!", Toast.LENGTH_SHORT);

            //Note: We need a cancellation token for our getCurrentLocation function
            CancellationTokenSource source = new CancellationTokenSource();
            CancellationToken token = source.getToken();

            //Note: Create our locationTask that retrieves CURRENT location from our fusedLocationClient
            Task<Location> locationTask = fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token);
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

                    //Note: Add a new marker to the map, and pan camera toward it
                    mMap.addMarker(new MarkerOptions().position(currentLocationLatLng).title("Current Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 18.0f));

                    //Note: Toast to our success
                    toastLocationSuccess.show();


                }
            });

        }
        if(view.getId() == R.id.zoomIn_button){
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
        if(view.getId() == R.id.zoomOut_button){
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }

    }
}