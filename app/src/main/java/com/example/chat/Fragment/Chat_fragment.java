package com.example.chat.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chat.Adapter.UserAdapter;
import com.example.chat.Model.Chatlist;
import com.example.chat.Model.User;
import com.example.chat.R;
import com.example.chat.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;


public class Chat_fragment extends Fragment {
    private RecyclerView recyclerView;
    private List<User> muser;
    private FirebaseUser firebaseUser;
    private DatabaseReference db;
    private List<Chatlist> userList;
    private UserAdapter userAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_chat_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList= new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot snap: snapshot.getChildren()){
                    Chatlist chatlist = snap.getValue(Chatlist.class);
                    userList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        updatetToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }
private void updatetToken(String token){
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference("Tokens");
    Token token1 = new Token(token);
    rf.child(firebaseUser.getUid()).setValue(token1);

}
    private void chatList() {
        muser = new ArrayList<>();
        db = FirebaseDatabase.getInstance().getReference("Users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                muser.clear();
                for(DataSnapshot snap: snapshot.getChildren()){
                    User user = snap.getValue(User.class);
                    for(Chatlist chatlist: userList){
                        if(user.getId().equals(chatlist.getId())){
                            muser.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(),muser,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}