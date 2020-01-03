package com.vignesh.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vignesh.chatapplication.notification.Token;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView mprofile;
    ActionBar actionBar;
    String myUid;
    String newToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        firebaseAuth = FirebaseAuth.getInstance();


//        mprofile =(TextView) findViewById(R.id.profile);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);


        actionBar.setTitle("Profile");
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content,profileFragment,"");
        fragmentTransaction.commit();
        checkUserStatus();

//        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(DashboardActivity.this, new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                newToken = instanceIdResult.getToken();
//            }
//        });

//        String tokenval = FirebaseInstanceId.getInstance().getToken();
//        if(myUid==null){
//            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
//        }
//        else
//        {
//        updateToken(FirebaseInstanceId.getInstance().getToken());
//        }

    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token){
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        dref.child(myUid).setValue(mToken);
    }



    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.content,profileFragment,"");
                    fragmentTransaction1.commit();

                    return true;
                case R.id.nav_user:
                    actionBar.setTitle("Users");
                    UserFragment userFragment = new UserFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content,userFragment,"");
                    fragmentTransaction2.commit();
                    return true;
//                case R.id.nav_chat:
//                    actionBar.setTitle("Chat");
//                    ChatListFragment chatListFragment = new ChatListFragment();
//                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction3.replace(R.id.content,chatListFragment,"");
//                    fragmentTransaction3.commit();
//                    return true;
            }
            return false;
        }
    };

    private void checkUserStatus()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
//            mprofile.setText(user.getEmail());
            myUid = user.getUid();

            SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID",myUid);
            editor.apply();
            updateToken(FirebaseInstanceId.getInstance().getToken());

        }
        else {
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
