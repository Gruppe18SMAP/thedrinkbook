package com.example.thedrinkbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
// Implements View.OnClickListener to override the onClick method

    FirebaseAuth mAuth;
    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseUsers = drinkDatabase.child("Users");

    EditText etMail, etPassword;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialize objects
        InitializeObjects();
    }

    private void InitializeObjects() {
        etMail = findViewById(R.id.txtUsername);
        etPassword = findViewById(R.id.txtPassword);
        btn_login = findViewById(R.id.bntLogin);

        btn_login.setOnClickListener(LoginActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void signIn(String mail, String password){
        if(!validateForm()){
            return;
        }

        mAuth.signInWithEmailAndPassword(mail,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);


                            /*String userUid = user.getUid();

                            if(databaseUsers.child(userUid).toString() == userUid) {
                                startActivity(new Intent(LoginActivity.this, BuyActivity.class));
                            }
                            else{
                                startActivity(new Intent(LoginActivity.this, AdminstratorOverViewActivity.class));
                            }*/
                        }

                        else{
                            Toast.makeText(LoginActivity.this,"Authentication failed.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String mail = etMail.getText().toString();
        if(TextUtils.isEmpty(mail)){
            etMail.setError("Required");
            valid = false;
        }
        else{
            etMail.setError(null);
        }

        String password = etPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            etPassword.setError("Required");
            valid = false;
        }
        else{
            etPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null) {
            String userUid = currentUser.getUid();
            DatabaseReference dbKey = databaseUsers.child(userUid);
            String key = dbKey.getKey();
            if (databaseUsers.child(userUid).getKey().equals(userUid)) {
                startActivity(new Intent(LoginActivity.this, AdminstratorOverViewActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, selectActivity.class));
            }
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.bntLogin){
            signIn(etMail.getText().toString(), etPassword.getText().toString());
        }
    }
}