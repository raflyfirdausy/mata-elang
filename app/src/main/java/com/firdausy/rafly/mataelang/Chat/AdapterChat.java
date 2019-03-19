package com.firdausy.rafly.mataelang.Chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firdausy.rafly.mataelang.R;


import java.util.ArrayList;
import java.util.List;


public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyViewHolder> {
            Context context;
    private final List<Message> messageList;
    private final int ME = 1;
    private final int FROM = 2;
    private final String owner;
    public AdapterChat(Context context, String owner, ArrayList<Message> messageList){
        this.context = context;
        this.messageList = messageList;
        this.owner = owner;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getFrom().equals(owner)){
            return ME;
        }else {
            return FROM;
        }

    }

    @Override
    public AdapterChat.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        MyViewHolder viewHolder = null;

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewtype){
            case ME:
                View view1 = inflater.inflate(R.layout.item_message_sent,viewGroup,false);
                viewHolder = new MyViewHolder(view1);
                break;
            case FROM:
                View view2 = inflater.inflate(R.layout.item_message_received,viewGroup,false);
                viewHolder = new MyViewHolder(view2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterChat.MyViewHolder myViewHolder, int i) {
        switch (myViewHolder.getItemViewType()){
            case ME:
                MyViewHolder myViewHolder1 = (MyViewHolder) myViewHolder;
                myViewHolder.konfigurasi1(myViewHolder1,i);
                break;
            case FROM:
                MyViewHolder myViewHolder2 = (MyViewHolder) myViewHolder;
                myViewHolder.konfigurasi2(myViewHolder2,i);
                break;
        }
    }



    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView itemMessageDateTextView,itemMessageBodyTextView;
        LinearLayout itemMessageParent;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemMessageDateTextView = itemView.findViewById(R.id.item_message_date_text_view);
            itemMessageBodyTextView = itemView.findViewById(R.id.item_message_body_text_view);
            itemMessageParent = itemView.findViewById(R.id.item_message_parent);

        }

        void konfigurasi1(AdapterChat.MyViewHolder vh, int position ) {
        Message message = messageList.get(position);
            itemMessageBodyTextView.setText(message.getBody());
        }
        void konfigurasi2(AdapterChat.MyViewHolder vh, int position){
        Message message = messageList.get(position);
        itemMessageBodyTextView.setText(message.getBody());


        }
    }

}
