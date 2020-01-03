package com.vignesh.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vignesh.chatapplication.models.Modeluser;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText memail,mpassword;
    Button mlogin;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("LOGIN");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        memail = findViewById(R.id.emailid_lo);
        mpassword = findViewById(R.id.password_lo);
        mlogin = findViewById(R.id.login);

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = memail.getText().toString().trim();
                String password = mpassword.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    memail.setError("Invalid Email");
                    mpassword.setFocusable(true);
                }
                else{
                    loginuser(email,password);
                }



            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Logging in");
    }

    private void loginuser(String email, String password) {
        pd.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            String email = user.getEmail();
                            String uid = user.getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);
                            DataSnapshot dataSnapshot;
                            String uname = user.getDisplayName();
                            Uri uimage = user.getPhotoUrl();
                            HashMap<Object,String> hashMap = new HashMap<>();

                            hashMap.put("email",email);
                            hashMap.put("uid",uid);
//                            hashMap.put("name",""+uname);
                            hashMap.put("onlineStatus","online");
                            hashMap.put("typingTo","noOne");
                            hashMap.put("phone","");
//                            hashMap.put("image",""+uimage);
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference reference = firebaseDatabase.getReference("Users");
                            reference.child(uid).child("onlineStatus").setValue("online");
                            reference.child(uid).child("typingTo").setValue("noOne");
                            reference.child(uid).child("email").setValue(email);
                            reference.child(uid).child("uid").setValue(uid);


                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();//go to Previous Activity
        return super.onSupportNavigateUp();
    }
}
