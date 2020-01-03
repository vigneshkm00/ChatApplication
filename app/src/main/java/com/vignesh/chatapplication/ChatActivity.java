package com.vignesh.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vignesh.chatapplication.adapters.AdapterChat;
import com.vignesh.chatapplication.models.ModelChat;
import com.vignesh.chatapplication.models.Modeluser;
import com.vignesh.chatapplication.notification.APIService;
import com.vignesh.chatapplication.notification.Client;
import com.vignesh.chatapplication.notification.Data;
import com.vignesh.chatapplication.notification.Response;
import com.vignesh.chatapplication.notification.Sender;
import com.vignesh.chatapplication.notification.Token;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;


public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profView;
    TextView unametv,uststusev;
    EditText message;
    ImageButton sendbtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    ValueEventListener seeListenter;
    DatabaseReference databaseReference_forseen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;


    String hisUid;
    String myUid;

    String hisImg;
    APIService apiService;
    boolean notify= false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_rec_view);
        profView = findViewById(R.id.profileiv);
        unametv = findViewById(R.id.uname);
        uststusev = findViewById(R.id.ustatus);
        message = findViewById(R.id.messagev);
        sendbtn = findViewById(R.id.send);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        //create APi
        apiService = Client.getRetrofit("https://fcm.googleapis.com").create(APIService.class);

        Intent i  = getIntent();
        hisUid = i.getStringExtra("hisUid");

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Users");

        Query query = reference.orderByChild("uid").equalTo(hisUid);
        System.out.println(hisUid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for( DataSnapshot ds:dataSnapshot.getChildren()){
                    String name = ""+ds.child("name").getValue();
                    hisImg = ""+ds.child("image").getValue();
                    String typingstatus = ""+ds.child("typingTo").getValue();

                    if(typingstatus.equals(myUid)){
                        uststusev.setText("Typing...");
                    }
                    else {
                        String onliestatus = ""+ds.child("onlineStatus").getValue();

                        unametv.setText(name);
                        if(onliestatus.equals("online")){
                            uststusev.setText(onliestatus);
                        }
                        else {
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onliestatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            uststusev.setText("Last Seen at:"+dateTime);

                        }
                    }


                    try {
                        Picasso.get().load(hisImg).placeholder(R.drawable.ic_photo_black).into(profView);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_photo_black).into(profView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = message.getText().toString().trim();
                if (TextUtils.isEmpty(mess)){
                    Toast.makeText(ChatActivity.this,"Cannot send empty message",Toast.LENGTH_SHORT).show();

                }
                else {
                    sendMessage(mess);
                    notifyifoffline();
                }
                message.setText("");
            }
        });
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length() == 0){
                    checkTypigStatus("noOne");
                }
                else {
                    checkTypigStatus(hisUid);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readMessage();
        seenMessage();
    }

    private void notifyifoffline() {
        DatabaseReference notref = FirebaseDatabase.getInstance().getReference("Users");
        Query query = notref.orderByChild("uid").equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for( DataSnapshot ds:dataSnapshot.getChildren()) {
                    String onliestatus = "" + ds.child("onlineStatus").getValue();
                    notify = !onliestatus.equals("online");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    private void seenMessage() {
        databaseReference_forseen = FirebaseDatabase.getInstance().getReference("Chats");
        seeListenter = databaseReference_forseen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String, Object> hasSeenHashmap = new HashMap<>();
                        hasSeenHashmap.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeenHashmap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            chatList.clear();
            for (DataSnapshot ds: dataSnapshot.getChildren()){
                ModelChat chat = ds.getValue(ModelChat.class);
                String recev = chat.getReceiver();
                String sende = chat.getSender();
                 if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                        chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                    chatList.add(chat);
                }
                adapterChat = new AdapterChat(ChatActivity.this,chatList,hisImg);
                adapterChat.notifyDataSetChanged();
                recyclerView.setAdapter(adapterChat);
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String mess) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",mess);
        hashMap.put("time",timestamp);
        hashMap.put("isSeen",false);
        reference.child("Chats").push().setValue(hashMap);

        String msg = mess;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Modeluser user = dataSnapshot.getValue(Modeluser.class);
                if(notify){
                    senNotification(hisUid,user.getName(),mess);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void senNotification(final String hisUid, final String name, final String mess) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid,name+":"+mess,"New Message", hisUid,R.drawable.ic_notify);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(ChatActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkUserStatus()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
//            mprofile.setText(user.getEmail());
            myUid = user.getUid();

        }
        else {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    public void checkonlineStatus(String status)
    {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        dbref.updateChildren(hashMap);
    }

    public void checkTypigStatus(String typing)
    {
        DatabaseReference dbref1 = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("typingTo",typing);
        dbref1.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkonlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkonlineStatus(timestamp);
        checkTypigStatus("noOne");
        databaseReference_forseen.removeEventListener(seeListenter);
    }

    @Override
    protected void onResume() {
        checkonlineStatus("online");
        notify = false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkonlineStatus(timestamp);
        checkTypigStatus("noOne");
        databaseReference_forseen.removeEventListener(seeListenter);
        super.onDestroy();
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
