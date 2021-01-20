package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.chat.Adapter.MessageAdapter;
import com.example.chat.Fragment.ApiService;
import com.example.chat.Model.Chat;
import com.example.chat.Model.User;
import com.example.chat.notification.Client;
import com.example.chat.notification.Data;
import com.example.chat.notification.MyResponse;
import com.example.chat.notification.Sender;
import com.example.chat.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Message extends AppCompatActivity {
    private CircleImageView profileimage;
    private TextView username;
    private FirebaseUser firebaseUser;
    private String userid;
    private DatabaseReference databaseReference;
    private Intent intent;
    private ImageButton imageButton;
    private EditText message;
    private MessageAdapter messageAdapter;
    private List<Chat> chat;
    private RecyclerView recyclerView;
    private ValueEventListener seenListener;
    private ApiService apiService;
    private String takemsg;
    private boolean notify = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Message.this,MainActivity.class));
            }
        });
        apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
        profileimage=findViewById(R.id.profileimage);
        username=findViewById(R.id.username);
        imageButton=findViewById(R.id.btnsend);
        message=findViewById(R.id.message);
        // to show message
        recyclerView=findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //  Load data of user in toolbar
        intent = getIntent();
        userid = intent.getStringExtra("id");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=  snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageurl().equals("default")){
                    profileimage.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(profileimage);
                }
                readmessage(firebaseUser.getUid(),userid,user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage();
    }
    // user online or not
    private void status(String status){
        databaseReference =  FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }
    // check if the message seen or not
    private void seenMessage(){
        databaseReference =  FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    Chat chat = snap.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snap.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // to send message
    public void sendMessage(View view) {
        notify = true;
        takemsg = message.getText().toString();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",firebaseUser.getUid());
        hashMap.put("receiver",userid);
        hashMap.put("message",takemsg);
        hashMap.put("isseen",false);
        reference.child("Chats").push().setValue(hashMap);
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final String msg = takemsg;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify) {
                    sendNotification(userid, user.getUsername(), msg);
                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        message.setText("");
        message.setHint("Type a message....");
    }
    private void sendNotification(String receiver,String username,String message){
         DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    Token token = snap.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(),R.mipmap.ic_launcher,username+": "+message,"New Message",userid);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code() == 200){
                                if(response.body().success != 1){
                                    Toast.makeText(Message.this,"Failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

// to read message
    private void readmessage(String myid,String userid,String imageurl){
        chat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chat.clear();
                for(DataSnapshot snap: snapshot.getChildren()){
                    Chat chatt = snap.getValue(Chat.class);
                    if ( chatt.getReceiver().equals(myid) && chatt.getSender().equals(userid) ||
                            chatt.getSender().equals(myid) && chatt.getReceiver().equals(userid) ){
                       chat.add(chatt);
                    }
                    messageAdapter = new MessageAdapter(Message.this,chat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void Currentuser(String userid){
        SharedPreferences.Editor  editor = getSharedPreferences("User",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        Currentuser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
        status("offline");
        Currentuser("None");
    }
}