package com.example.zujoblogzone;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPass;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView signUpTxtView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpTxtView = (TextView)findViewById(R.id.signUpTxtView);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        loginEmail = (EditText)findViewById(R.id.login_email);
        loginPass = (EditText)findViewById(R.id.login_password);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        signUpTxtView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(regIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Processing...", Toast.LENGTH_LONG).show();
                String email = loginEmail.getText().toString().trim();
                String password = loginPass.getText().toString().trim();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                checkUserExistense();
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Couldn't Login, User Not Found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "Complete All Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkUserExistense(){

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(user_id)){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else{
                    Toast.makeText(LoginActivity.this, "User Not Registered!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}