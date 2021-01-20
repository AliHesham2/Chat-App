package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {
    private EditText Email;
    private TextView error;
    private String email;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Email = findViewById(R.id.LinputEmail);
        error = findViewById(R.id.LError);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void Reset(View view) {
        if(check_validation()){
            check_Reset();
        }else{
            Toast.makeText(this,"Check Fields again",Toast.LENGTH_SHORT).show();
        }
    }

    private void check_Reset() {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ResetPassword.this,"Check Email please !",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ResetPassword.this,Login.class));
                    finish();
                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(ResetPassword.this,error,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean check_validation() {
        email = Email.getText().toString();
        if(email.isEmpty()){
            if(email.isEmpty()){
                error.setVisibility(View.VISIBLE);
                error.setText("Email Required");
            }else{error.setVisibility(View.GONE);}
            return false;
        }
        error.setVisibility(View.GONE);
        return true;

    }
}