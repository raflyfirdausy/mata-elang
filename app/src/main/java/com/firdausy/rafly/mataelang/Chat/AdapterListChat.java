package com.firdausy.rafly.mataelang.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.Helper.Bantuan;
import com.firdausy.rafly.mataelang.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterListChat extends RecyclerView.Adapter<AdapterListChat.MyViewHolder> {
    private Context context;
    private List<String> userlist;
    private List<ListModel> userlist2;
    private String owner;
    private String position;
    private DatabaseReference databaseReference;

    AdapterListChat(Context context, String owner, ArrayList<String> userlist) {
        this.context = context;
        this.owner = owner;
        this.userlist = userlist;
    }

    AdapterListChat(Context context, String owner, ArrayList<String> userlist, ArrayList<ListModel> userlist2) {
        this.context = context;
        this.owner = owner;
        this.userlist = userlist;
        this.userlist2 = userlist2;
    }


    @NonNull
    @Override
    public AdapterListChat.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_chat, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterListChat.MyViewHolder myViewHolder, final int i) {
        position = userlist.get(i);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("user")
                .child("ibu")
                .child(position)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            myViewHolder.tvNama.setText(dataSnapshot.child("namaLengkap").getValue(String.class));
                            myViewHolder.tvIsi.setText(dataSnapshot.child("email").getValue(String.class));
                        } else {
                            new Bantuan(context).alertDialogPeringatan(context.getString(R.string.tidak_ditemukan));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        new Bantuan(context).alertDialogPeringatan(databaseError.getMessage());
                    }
                });

//        myViewHolder.tvNama.setText(userlist2.get(i).getNama());
//        myViewHolder.tvNama.setText(userlist2.get(i).getEmail());

        myViewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("uid", position);
                intent.putExtra("nama",  myViewHolder.tvNama.getText());
                intent.putExtra("email",  myViewHolder.tvIsi.getText());
                context.startActivity(intent);
//                context.startActivity(new Intent(context, Chat.class).putExtra("uid", userlist2.get(i).getKey()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUser;
        TextView tvNama, tvIsi;
        LinearLayout parent;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUser = itemView.findViewById(R.id.iv_user);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvIsi = itemView.findViewById(R.id.tv_isi);
            parent = itemView.findViewById(R.id.parent_item);
        }
    }
}
