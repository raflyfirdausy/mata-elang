package com.firdausy.rafly.mataelang.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.firdausy.rafly.mataelang.Activity.MainActivity;
import com.firdausy.rafly.mataelang.Activity.admin.PengaturanActivity;
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
import java.util.Objects;

public class ListUser extends AppCompatActivity {
    Context context;
    private RecyclerView recyclerView;
    private FloatingActionButton fabnewchat;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<String> listchat = new ArrayList<>();
    private ArrayList<ListModel> listchat2 = new ArrayList<>();
    private AdapterListChat adapter;
    private String owner;
    private RelativeLayout rl_belumChatSiapapun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.mata_elang);
        getSupportActionBar().setSubtitle("Chat Kader dan Ibu");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        context = ListUser.this;
        recyclerView = findViewById(R.id.rv);
        fabnewchat = findViewById(R.id.fab_new_chat);
        rl_belumChatSiapapun = findViewById(R.id.rl_belumChatSiapapun);
        rl_belumChatSiapapun.setVisibility(View.GONE);
        owner = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("chat");

        final Intent intent = getIntent();

        datachat();

        fabnewchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DaftarContact.class)
                        .putExtra("level", intent.getStringExtra("level")));
                finish();
            }
        });

    }

    private void datachat() {
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference.child(owner)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            rl_belumChatSiapapun.setVisibility(View.GONE);
                            for (final DataSnapshot data : dataSnapshot.getChildren()) {
                                listchat.add(data.getKey());
                            }
                        } else {
                            rl_belumChatSiapapun.setVisibility(View.VISIBLE);
                        }

                        adapter = new AdapterListChat(context, owner, listchat);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(context, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
