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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText Email;
    private EditText Password;
    private TextView error;
    private TextView error2;
    private String email;
    private String password;
    private String authpassword;
    private String authemail,username;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Email = findViewById(R.id.LinputEmail);
        Password  = findViewById(R.id.LinputPassword);
        error = findViewById(R.id.LError);
        error2=findViewById(R.id.LError2);
        auth = FirebaseAuth.getInstance();
    }


    public void SignUp(View view) {
        startActivity(new Intent(this,SignUp.class));
        finish();
    }

    public void Login(View view) {
        if(check_validation()){
            check_Login();
        }else{
            Toast.makeText(this,"Check Fields again",Toast.LENGTH_SHORT).show();
        }
    }
    // Just for validation  checks ..
    private boolean check_validation() {
        email = Email.getText().toString();
        password = Password.getText().toString();
        if(email.isEmpty() || password.isEmpty()  ){
            if(email.isEmpty()){
                error.setVisibility(View.VISIBLE);
                error.setText("Email Required");
            }else{error.setVisibility(View.GONE);}if(password.isEmpty()) {
                error2.setVisibility(View.VISIBLE);
                error2.setText("Password Required");
            }else{error2.setVisibility(View.GONE);}
            return false;
        }
        return true;

    }
    private void check_Login(){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(Login.this, DashBoard.class));
                    finish();
                }else{
                    Toast.makeText(Login.this,"Wrong email or Password !",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Forgetpassword(View view) {
        startActivity(new Intent(Login.this, ResetPassword.class));
        finish();
    }
}