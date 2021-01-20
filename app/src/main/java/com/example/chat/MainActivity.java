package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
private ImageView imageView;
private AnimatedVectorDrawable avd;
private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);
        avd = (AnimatedVectorDrawable) getDrawable(R.drawable.avd_android_design);
        imageView.setImageDrawable(avd);
        avd.start();
        }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            startActivity(new Intent(MainActivity.this, DashBoard.class));
        }
    }

    public void Login(View view) {
        startActivity(new Intent(MainActivity.this, Login.class));
    }

    public void SignUp(View view) {
        startActivity(new Intent(MainActivity.this, SignUp.class));
    }

}