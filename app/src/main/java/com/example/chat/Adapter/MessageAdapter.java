package com.example.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Model.Chat;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT= 0;
    public static final int MSG_TYPE_RIGHT= 1;
    private Context mcontext;
    private List<Chat> mchat;
    private String imageurl;
    private FirebaseUser fuser;
    private View view;

    public MessageAdapter(Context context, List<Chat> chat,String imageurl) {
        this.mcontext = context;
        this.mchat = chat;
        this.imageurl=imageurl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
             view = LayoutInflater.from(mcontext).inflate(R.layout.chat_right, parent, false);
        }else{
             view = LayoutInflater.from(mcontext).inflate(R.layout.chat_left, parent, false);
        }
        return  new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
Chat chat=mchat.get(position);
holder.show_message.setText(chat.getMessage());
        if(imageurl.equals("default")){
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mcontext).load(imageurl).into(holder.imageView);
        }
        if(position == mchat.size()-1){
            if(chat.isIsseen()){
                holder.seen.setText("Seen");
            }else{
                holder.seen.setText("Delivered");
            }
        }else{
            holder.seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView show_message,seen;
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.showmessage);
            imageView=itemView.findViewById(R.id.profileimage);
            seen=itemView.findViewById(R.id.seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mchat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}