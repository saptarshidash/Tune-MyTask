package com.saptarshi.tunemytask;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.saptarshi.tunemytask.services.AlarmPlayingService;

public class AppConfig extends Application {

  public static final String CHANNEL_1_ID = "Task Notification";
  public static FirebaseAuth FIREBASE_USER_AUTH;
  public static DatabaseReference FIREBASE_ROOT_REF;

  @Override
  public void onCreate() {
    super.onCreate();
    createTaskNotificationChannel();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    FirebaseApp.initializeApp(this);
    FIREBASE_USER_AUTH = FirebaseAuth.getInstance();
    FIREBASE_ROOT_REF = FirebaseDatabase.getInstance().getReference("Users");
    Intent intent = new Intent(getApplicationContext(), AlarmPlayingService.class);
    stopService(intent);
  }

  /**
   * This method is used to create a notification channel on App startup .
   */
  private void createTaskNotificationChannel() {
    NotificationManager notificationManager = (NotificationManager) getSystemService(
        Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel notificationChannel = new NotificationChannel(
          CHANNEL_1_ID,
          "Task Notification",
          NotificationManager.IMPORTANCE_HIGH
      );
      notificationChannel.setDescription("This is Task notification channel");
      notificationChannel.setName("Task Notifications");
      assert notificationManager != null;
      notificationManager.createNotificationChannel(notificationChannel);
    }
  }

}
