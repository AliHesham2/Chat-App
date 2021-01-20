package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.chat.Fragment.Chat_fragment;
import com.example.chat.Fragment.Profile;
import com.example.chat.Fragment.User_fragment;
import com.example.chat.Model.Chat;
import com.example.chat.Model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashBoard extends AppCompatActivity {
private FirebaseUser firebaseUser;
private CircleImageView profile_image;
private TextView username;
private ViewPager viewPager;
private ViewPagerAdapter viewPagerAdapter;
private TabLayout tabLayout;
DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
         tabLayout = findViewById(R.id.tablayout);
        viewPager=findViewById(R.id.viewpager);
         profile_image=findViewById(R.id.profileimage);
         username = findViewById(R.id.username);
         firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
         reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
          reference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {
                  User user = snapshot.getValue(User.class);
                  username.setText(user.getUsername());
                  if(user.getImageurl().equals("default")){
                      profile_image.setImageResource(R.mipmap.ic_launcher);
                  }else{
                      Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_image);
                  }

              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });


        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread=0;
                for(DataSnapshot snap:snapshot.getChildren()){
                    Chat chat =  snap.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }
                if(unread == 0){
                    viewPagerAdapter.addFragment(new Chat_fragment(),"Chats");
                }else{
                    viewPagerAdapter.addFragment(new Chat_fragment(),"("+unread+")Chats");
                }
                viewPagerAdapter.addFragment(new User_fragment(),"Users");
                viewPagerAdapter.addFragment(new Profile(),"Profile");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,MainActivity.class));
                return true;
        }
        return false;
    }
   private class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        ViewPagerAdapter(FragmentManager fm){
        super(fm);
        this.fragments= new ArrayList<>();
        this.titles=new ArrayList<>();
       }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

       @Nullable
       @Override
       public CharSequence getPageTitle(int position) {
           return titles.get(position);
       }
   }
   private void status(String status){
        reference =  FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
       HashMap<String,Object> hashMap=new HashMap<>();
       hashMap.put("status",status);
       reference.updateChildren(hashMap);
   }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}