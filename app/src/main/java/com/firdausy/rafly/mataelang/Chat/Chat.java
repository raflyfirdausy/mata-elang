package com.firdausy.rafly.mataelang.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.firdausy.rafly.mataelang.Activity.MainActivity;
import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Chat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextInputEditText inputEditText;
    private FloatingActionButton send;
    private DatabaseReference databaseReference;
    private AdapterChat adapterChat;
    private User user;
    private FirebaseUser firebaseUser;
    final ArrayList<Message> messages = new ArrayList<>();

    private String Useruid;
    private String owner;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra("nama"));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("email"));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        context = Chat.this;
        recyclerView = findViewById(R.id.activity_thread_messages_recycler);
        send = findViewById(R.id.activity_thread_send_fab);
        inputEditText = findViewById(R.id.activity_thread_input_edit_text);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Useruid = intent.getStringExtra("uid");


        inputEditText.requestFocus();
        inisiasi_chat();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirim();
            }
        });
    }

    private void kirim(){
        long timestamp = new Date().getTime();
        long daytimestamp = getDayTimestamp(timestamp);
        String body = inputEditText.getText().toString().trim();

        Message message =new Message(timestamp, -timestamp,daytimestamp,body,owner,Useruid);

        databaseReference
                .child("chat")
                .child(Useruid)
                .child(owner)
                .push()
                .setValue(message);
        databaseReference
                .child("notifications")
                .child("messages")
                .push()
                .setValue(message);
        if (!owner.equals(Useruid))
        {
            databaseReference
                    .child("chat")
                    .child(owner)
                    .child(Useruid)
                    .push()
                    .setValue(message);
        }
        inputEditText.setText("");
        inisiasi_chat();

    }
    private long getDayTimestamp(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        return calendar.getTimeInMillis();
    }

    private void inisiasi_chat() {
        databaseReference
                .child("chat")
                .child(owner)
                .child(Useruid)
                .orderByChild("negatedTimestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Message chat;
                        if (dataSnapshot.exists()){
                            messages.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()){
                                chat = data.getValue(Message.class);
                                messages.add(chat);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        databaseReference
                .child("chat")
                .child(owner)
                .child(Useruid)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        adapterChat.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        adapterChat = new AdapterChat(context,owner,messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,true));
        recyclerView.setAdapter(adapterChat);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, ListUser.class).putExtra("level", getIntent().getStringExtra("level")));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(context, ListUser.class).putExtra("level", getIntent().getStringExtra("level")));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
