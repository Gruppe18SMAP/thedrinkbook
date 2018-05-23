package com.example.thedrinkbook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
// Implements View.OnClickListener to override the onClick method

    private static final int LOGIN_REQUEST = 11;

    FirebaseAuth mAuth;
    DatabaseReference drinkDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseUsers = drinkDatabase.child("Users");

    final static String LOG = "LoginActvity";
    EditText etMail, etPassword;
    Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.thedrinkbook", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            Log.d("KeyHash:", e.getMessage());
        }


        setContentView(R.layout.activity_login);

        // initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialize objects
        initializeObjects();

        if(savedInstanceState != null)
        {
            etMail.setText(savedInstanceState.getString("email"));
            etPassword.setText(savedInstanceState.getString("password"));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("email",etMail.getText().toString());
        outState.putString("password", etPassword.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private void initializeObjects() {
        etMail = findViewById(R.id.txtUsername);
        etPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.bntLogin);

        btnLogin.setOnClickListener(LoginActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        checkRole(currentUser);
    }

    private void signIn(String mail, String password){
        if(!validateForm()){
            // Enable button again
            btnLogin.setEnabled(true);
            return;
        }

        String connectStat = NetworkChecker.getNetworkStatus(this);
        if(connectStat == null) {
            mAuth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                checkRole(user);
                            } else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.failed_authentification), Toast.LENGTH_LONG).show();
                                // Enable button again
                                btnLogin.setEnabled(true);
                            }

                        }
                    });
        } else{
            Toast.makeText(LoginActivity.this, connectStat, Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String mail = etMail.getText().toString();
        if(TextUtils.isEmpty(mail)){
            etMail.setError(getResources().getString(R.string.required_field));
            valid = false;
        }
        else{
            etMail.setError(null);
        }

        String password = etPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            etPassword.setError(getResources().getString(R.string.required_field));
            valid = false;
        }
        else{
            etPassword.setError(null);
        }

        return valid;
    }

    // checker om man er en bruger eller en administrator i firebase
    private void checkRole(FirebaseUser currentUser) {
        if(currentUser != null) {
            String userUid = currentUser.getUid();
            Log.d(LOG, "Checking role...");
            DatabaseReference dbRole = databaseUsers.child(userUid).child("Rolle");
            dbRole.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        final String valueRole = dataSnapshot.getValue().toString();
                        updateUI(valueRole);
                    }
                    else{
                        Toast.makeText(LoginActivity.this,(getResources().getString(R.string.failed_authentification)), Toast.LENGTH_LONG).show();
                        // Enable button again
                        btnLogin.setEnabled(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
// viser aktivitet alt efter om man logger ind med administrtor eller bruger
    private void updateUI(String valueRole) {
        if(valueRole.equals("Admin"))
        {
            Log.d(LOG, "Adminstrator login");
            startActivityForResult(new Intent(LoginActivity.this, AdminstratorOverViewActivity.class),LOGIN_REQUEST);
        }
        else if(valueRole.equals("User")){
            Log.d(LOG, "User login ");
            startActivityForResult(new Intent(LoginActivity.this, selectActivity.class), LOGIN_REQUEST);
        }
        btnLogin.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.bntLogin){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
            // Disable the button while checking the input
            btnLogin.setEnabled(false);
            signIn(etMail.getText().toString(), etPassword.getText().toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST){
            if(resultCode == RESULT_CANCELED){
                FirebaseAuth.getInstance().signOut();
            }
        }
    }

    //public void gennemgåListe(){
        //for(int e=0; e < elements; e++) {
            //View listview = lvDrinks.getChildAt(e);
      //  Object data = new Object();
        //ArrayList<Object> dataToSave = new ArrayList<>();
          //  if(etMail.getText().toString() != null){
            //    data.Key = mail;
              //  data.Value = etMail.getText().toString();
                // dataToSave.add(data)
            //}
    //}
}
