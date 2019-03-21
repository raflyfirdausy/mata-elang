package com.firdausy.rafly.mataelang.Fcm;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/*
 * Created by Mahmoud on 3/13/2017.
 */

public class InstanceIdService extends FirebaseInstanceIdService {
    private  String user = "";
    private  String instanceId = "";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        instanceId = FirebaseInstanceId.getInstance().getToken();
        Log.d("@@@@", "onTokenRefresh: " + instanceId);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = firebaseUser.getUid().toString();
        if (firebaseUser != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child("admin")
                    .child(firebaseUser.getUid())
                    .child("instanceId")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child("admin")
                                        .child(user)
                                        .child("instanceId")
                                .setValue(instanceId);
                            }else {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child("admin")
                                        .child(user)
                                        .child("instanceId")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("users")
                                                            .child("admin")
                                                            .child(user)
                                                            .child("instanceId")
                                                            .setValue(instanceId);
                                                }else {

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }
    }
}
