package com.example.joke;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MessageService extends IntentService {
    //Declare a constant KEY to pass a message from the Main Activity to the service
    public static final String EXTRA_MESSAGE = "MESSAGE";
    private static final String CHANNEL_ID = "3";
    private Handler handler;
    public static final int NOTIFICATION_ID = 1;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        handler = new Handler();
        return super.onStartCommand(intent, flags, startId);
    }

    public MessageService() {
        super("MessageService");
        //the constructor is required, don't delete it
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //Don't delete this method,
        // this method will contains the code we want to run when the service receives an intent
        synchronized (this) {
            // synchronized() method is Java code which allows us to lock a particular block of code from access by other threads
            try {
                //wait for 10 seconds t
                wait(10000);
            } catch (InterruptedException error) {
                error.printStackTrace();
            }
            //try..catch is Java syntax which allows us to perform code actions on the try block , and catch error exceptions in the the catch block , hence making us able to trace the line of code which has errors during debugging
        }
        //get the text from the intent
        String text = intent.getStringExtra(EXTRA_MESSAGE);
        //call showText method
        showText(text);
    }
    private void showText(final String text) {
        Log.v("DelayedMessageService", "What is the secret of comedy?:" + text);
        // the above line of code logs a piece of text so that we can see it in the logcat
        //post the Toast code to the main thread using the handler post method
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent =new Intent(this,MainActivity.class);

        /////////////////////////////
            // NotificationChannel,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel;
                channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(text);
                // Register the channel with the system;
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
                // Create an explicit intent for an Activity in your app
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_joke_round)
                        .setContentTitle("What is the secret of comedy?")
                        .setContentText("Timing my friend !!!")
                        .setPriority(Notification.PRIORITY_HIGH)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
            //normal notification
            else {
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(intent);
        /*create Pending intentNext, we get the pending intent from the TaskStackBuilder using its
        getPendingIntent() method.
        The getPendingIntent() method takes two int parameters, a request code that can be
        used to identify the intent and a flag that specifies the pending intent’s behavior.
        */
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
 /*Build the Notification
 You create a notification using a notification builder to create a new
Notification object.
 The notification builder allows you to create a notification with a specific
set of features, without writing too much code.
 Each notification must include a small icon, a title, and some text.*/
                Notification notification = new Notification.Builder(this)
                        //this displays a small notification icon-in this case the mipmap called ic_joke_round
                        .setSmallIcon(R.mipmap.ic_joke_round)
                        //set the title as your application name
                        .setContentTitle(getString(R.string.app_name))
                        //set the content text
                        .setContentText(text)
                        //make the notification disappear when clicked
                        .setAutoCancel(true)
                        //give it a maximum priority to allow peeking
                        .setPriority(Notification.PRIORITY_MAX)
                        //set it to vibrate to get a large heads-up notification
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        //open main activity on clicking the notification
                        .setContentIntent(pendingIntent)
                        .build();
                //display the notification using the Android notification service
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //Issue the notification
                assert notificationManager != null;
                notificationManager.notify(NOTIFICATION_ID, notification);

            }
    }

}
