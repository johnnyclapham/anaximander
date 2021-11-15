package com.example.anaximander;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DAOUser {

    private DatabaseReference databaseReference;
    public DAOUser(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(User.class.getSimpleName());
    }
    public Task<Void> add(User user){

        return databaseReference.push().setValue(user);
    }

    public Task<Void> addWithTimeStampChildName(User user, String child){

        return databaseReference.child(child).setValue(user);
    }

    public void clearAllFirebaseDatafromToday(String mmddyy){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query thirtyQuery = ref.child("User").orderByChild("dateString").equalTo(mmddyy);

        thirtyQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}
