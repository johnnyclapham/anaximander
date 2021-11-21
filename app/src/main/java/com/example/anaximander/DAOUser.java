package com.example.anaximander;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.List;

public class DAOUser {

    private DatabaseReference databaseReference;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(User.class.getSimpleName());
    }

    public Task<Void> add(User user) {
        return databaseReference.push().setValue(user);
    }

    public Task<Void> addWithTimeStampChildName(User user, String child) {

        return databaseReference.child(child).setValue(user);
    }

    public void clearAllFirebaseDatafromToday(String mmddyy) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query thirtyQuery = ref.child("User").orderByChild("dateString").equalTo(mmddyy);

        thirtyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    public List<WeightedLatLng> getAllFireBaseLatLngs(){
        List<WeightedLatLng> WeightedLatLng = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query thirtyQuery = ref.child("User");
        thirtyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int yam = 0;
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {

                    double lat = appleSnapshot.child("latitude").getValue(double.class);
                    double lng = appleSnapshot.child("longitude").getValue(double.class);
                    //Note: Make rss positive so we can work more easily with the value
                    double rssi = -1 * appleSnapshot.child("rssi").getValue(double.class);
                    //Note: Normalize rss (30 to 120) to a (0 to 1) scale
                    double rssiNormalized = (rssi-29)/(120-29);
                    //Note: For our map, we must invert for visuals to make sense
                    //      30 should be strongest, 120 should be weakest
                    //      (remember we are 0-1 scale now)
                    double invertedRssiNormalized = 1-rssiNormalized;
                    WeightedLatLng.add(new WeightedLatLng(new LatLng(lat, lng),invertedRssiNormalized));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        System.out.println(WeightedLatLng);
        return WeightedLatLng;
    }
}
