package com.example.chat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chat.Message;
import com.example.chat.Model.Chat;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mcontext;
    private List<User> muser;
    private boolean ischat;
    private String thelastmsg;

    public UserAdapter(Context context, List<User> muser, boolean ischat) {
        this.mcontext = context;
        this.muser = muser;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.useritem,parent,false);
        return  new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       User user =muser.get(position);
       holder.username.setText(user.getUsername());
       if(user.getImageurl().equals("default")){
           holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }else{
           Glide.with(mcontext).load(user.getImageurl()).into(holder.imageView);
       }
       if(ischat){
           lastmessage(user.getId(),holder.lastmsg);
       }else{
           holder.lastmsg.setVisibility(View.GONE);
       }
       if(ischat){
           if(user.getStatus().equals("online")){
               holder.img_on.setVisibility(View.VISIBLE);
               holder.img_off.setVisibility(View.GONE);
           }else{
               holder.img_on.setVisibility(View.GONE);
               holder.img_off.setVisibility(View.VISIBLE);
           }
       }else {
           holder.img_on.setVisibility(View.GONE);
           holder.img_off.setVisibility(View.GONE);
       }
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(mcontext, Message.class);
               intent.putExtra("id",user.getId());
               mcontext.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return muser.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView username,lastmsg;
        private ImageView imageView,img_on,img_off;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            imageView=itemView.findViewById(R.id.profileimage);
            img_off=itemView.findViewById(R.id.img_off);
            img_on= itemView.findViewById(R.id.img_on);
            lastmsg = itemView.findViewById(R.id.lastmsg);
        }
    }
    private void lastmessage(String userid,TextView lastmsg){
        thelastmsg = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    Chat chat = snap.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                            ||chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()) ){
                        thelastmsg = chat.getMessage();
                    }
                }
                switch (thelastmsg){
                    case "default" :
                        lastmsg.setText("No Message");
                        break;
                    default:
                        lastmsg.setText(thelastmsg);
                        break;
                }
                thelastmsg = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
