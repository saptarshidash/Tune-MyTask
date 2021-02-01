package com.saptarshi.tunemytask.services;

import static com.saptarshi.tunemytask.utils.ViewUtilsKt.toast;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.saptarshi.tunemytask.AppConfig;
import com.saptarshi.tunemytask.utils.Constants;
import com.saptarshi.tunemytask.views.home.HomeActivity;


public class AlarmPlayingService extends Service {

  private static final String TAG = AlarmPlayingService.class.getSimpleName();
  private static final String URI_BASE = AlarmPlayingService.class.getName() + ".";
  public static final String ACTION_DISMISS = URI_BASE + "ACTION_DISMISS";
  static Ringtone ringtone;
  static int id;
  FirebaseUser user;
  DatabaseReference rootRef, uidRef, childRef;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String action = intent.getAction();
    String[] taskData = intent.getStringArrayExtra(Constants.EXTRA_TASK_DATA_FOR_NOTIFICATION);
    if (ACTION_DISMISS.equals(action)) {
      if (childRef != null) {
        childRef.setValue(false);
      }
      dismissRingtone(id);
    } else {
      Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
      ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
      //playing sound alarm
      ringtone.play();
      ringtone.setLooping(true);
      id = Integer.parseInt(taskData[3]);
      toast(getApplicationContext(), "" + id);
      changeAlarmStatusInFirebase(taskData);
    }
    return START_NOT_STICKY;
  }

  private void changeAlarmStatusInFirebase(String[] taskData) {
    user = AppConfig.FIREBASE_USER_AUTH.getCurrentUser();
    if (user != null) {
      rootRef = AppConfig.FIREBASE_ROOT_REF;
      uidRef = rootRef.child(user.getUid());
      childRef = uidRef.child("Taskdata").child(taskData[1]).
          child(taskData[2]).child(taskData[0]).child("alarmStatus");
    }
  }

  public void dismissRingtone(int id) {
    // stop the alarm rigntone
    Intent i = new Intent(this, AlarmPlayingService.class);
    stopService(i);
    // move to HomeActivity
    Intent fireIntent = new Intent(this, HomeActivity.class);
    fireIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(fireIntent);
    // Canceling the current notification
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
    notificationManager.cancel(id);
  }

  @Override
  public void onDestroy() {
    Log.d("Service alarm:", "stopped");
    ringtone.stop();
  }
}
