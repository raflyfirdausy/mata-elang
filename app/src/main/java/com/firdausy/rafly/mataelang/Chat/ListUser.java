package com.firdausy.rafly.mataelang.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListUser extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton fabnewchat;

    Context context;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<String> listchat = new ArrayList<>();
    private AdapterListChat adapter;
    private String owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        context = ListUser.this;
        recyclerView = findViewById(R.id.rv);
        fabnewchat = findViewById(R.id.fab_new_chat);

        owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chat");

        final Intent intent = getIntent();

        datachat();

        fabnewchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,DaftarContact.class).putExtra("level",intent.getStringExtra("level")));
                finish();
            }
        });

    }

    private void datachat() {
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference.child(owner)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        listchat.add(data.getKey().toString());
                    }
                }else {

                }
                adapter = new AdapterListChat(context,owner,listchat);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
