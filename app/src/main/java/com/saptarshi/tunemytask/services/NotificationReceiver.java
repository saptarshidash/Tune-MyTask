package com.saptarshi.tunemytask.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.saptarshi.tunemytask.AppConfig;
import com.saptarshi.tunemytask.R;
import com.saptarshi.tunemytask.utils.Constants;
import com.saptarshi.tunemytask.views.home.HomeActivity;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

  NotificationManagerCompat notificationManager;
  PendingIntent contentIntent;
  int notificationId;

  @Override
  public void onReceive(Context context, Intent intent) {
    String[] intentData = intent.getStringArrayExtra(
        Constants.EXTRA_TASK_DATA_FOR_NOTIFICATION);
    int _id = new Random().nextInt(1000000 - 0 + 1000) + 0;
    notificationId = _id;
    String[] taskData = {intentData[2], intentData[3], intentData[4],
        String.valueOf(notificationId)};
    Intent ringtoneIntent = new Intent(context, AlarmPlayingService.class);
    ringtoneIntent.putExtra(Constants.EXTRA_TASK_DATA_FOR_NOTIFICATION, taskData);
    context.startService(ringtoneIntent);
    createNotification(context, intent);

  }

  private void createNotification(Context context, Intent intent) {

    String[] intentData = intent.getStringArrayExtra(
        Constants.EXTRA_TASK_DATA_FOR_NOTIFICATION);
    String _title = intentData[0];
    String _desc = intentData[1];
    notificationManager = NotificationManagerCompat.from(context);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.ic_task_notification)
        .setContentTitle(_title)
        .setContentText(_desc)
        .setChannelId(AppConfig.CHANNEL_1_ID);

    // Alarm dismiss intent
    Intent dismissIntent = new Intent(context, AlarmPlayingService.class);
    dismissIntent.setAction(AlarmPlayingService.ACTION_DISMISS);

    PendingIntent dismissPendingIntent = PendingIntent.getService(
        context,
        notificationId,
        dismissIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    );

    notificationBuilder.addAction(R.mipmap.ic_launcher,
        "Dismiss", dismissPendingIntent);

    Intent activityHome = new Intent(context, HomeActivity.class);
    activityHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    contentIntent = PendingIntent.getActivity(context,
        notificationId, activityHome, 0);
    notificationBuilder.setContentIntent(contentIntent);

    notificationManager.notify(notificationId, notificationBuilder.build());
  }
}
