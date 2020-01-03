package com.vignesh.chatapplication.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vignesh.chatapplication.DashboardActivity;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FirebaseMessaging extends FirebaseMessagingService {


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        token = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser!=null)
        {
            sendRegistrationToServer(token);

        }
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);

        String savedCurrentUser = sp.getString("Current_USERID","None");

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(fuser != null && sent.equals(fuser.getUid())){
            if(!savedCurrentUser.equals(user)){
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    sendOAndAboveNotification(remoteMessage);
                }
                else {
                    sendNormalNotification(remoteMessage);
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNormalNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");


        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, DashboardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid",user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText("Time to check me:")
                .setContentTitle("Burma kutty.")
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j = 0;
        if(i>0){
            j = i;
        }
        notificationManager.notify(j,builder.build());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");


        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent = new Intent(this, DashboardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid",user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getONotificcation("Time to check me:","Burma Kutty",pendingIntent,defSoundUri,icon);

        int j = 0;
        if(i>0){
            j = i;
        }
        notification1.getManager().notify(j,builder.build());

    }
    private void sendRegistrationToServer(String tokenre) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(tokenre);
        ref.child(user.getUid()).setValue(token1);
    }
}
