package com.example.anaximander;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

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
}
