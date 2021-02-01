package com.saptarshi.tunemytask.utils;

import android.app.Activity;
import android.content.Intent;
import com.saptarshi.tunemytask.models.Task;

public class IntentUtil {

  public IntentUtil(Activity ctx, Class<?> cls) {

  }

  public static void intent(Activity activity, Class<?> cls) {
    Intent intent = new Intent(activity, cls);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    activity.startActivity(intent);
  }


  public static void intent(Activity activity, Class<?> cls, String key, Task taskData) {
    Intent intent = new Intent(activity, cls);
    intent.putExtra(key, taskData);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    activity.startActivity(intent);
  }


  public static Task getIntentData(Activity activity, String key) {
    Intent intent = activity.getIntent();
    Task taskData = (Task) intent.getSerializableExtra(key);
    return taskData;
  }

}
