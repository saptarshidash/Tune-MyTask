package com.saptarshi.tunemytask.views.addtask;

import static com.saptarshi.tunemytask.utils.IntentUtil.getIntentData;
import static com.saptarshi.tunemytask.utils.IntentUtil.intent;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.saptarshi.tunemytask.R;
import com.saptarshi.tunemytask.adapters.FirebaseDataChangeListener;
import com.saptarshi.tunemytask.models.Task;
import com.saptarshi.tunemytask.services.NotificationReceiver;
import com.saptarshi.tunemytask.utils.Constants;
import com.saptarshi.tunemytask.viewmodels.TaskViewModel;
import com.saptarshi.tunemytask.views.home.HomeActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class CreateTaskActivity extends AppCompatActivity
    implements AdapterView.OnItemSelectedListener, FirebaseDataChangeListener.OnDataChangeListener,
    CompoundButton.OnCheckedChangeListener {

  NotificationManagerCompat notificationManager;
  private EditText taskTitleEdText, taskDescEdText;
  private TextView selectTimingTextView;
  private SwitchMaterial notificationSwitch;
  private SimpleDateFormat simpleDateFormat;
  private SimpleDateFormat month, day;
  private SmartMaterialSpinner materialSpinner;
  private Task taskData, intentTaskData;
  private TaskViewModel taskViewModel;
  private List<String> taskPriorityList;
  private FirebaseDataChangeListener dataChangeListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_task);
    initViews();
    initSpinner();
    initEventListeners();
    getIntentDataFromRecyclerView();
  }

  private void initViews() {
    selectTimingTextView = findViewById(R.id.textview_select_timing);
    taskTitleEdText = findViewById(R.id.add_tasktitle);
    taskDescEdText = findViewById(R.id.task_short_detail);
    materialSpinner = findViewById(R.id.spinner_select_priority);
    notificationSwitch = findViewById(R.id.switch_task_notification);
    simpleDateFormat = new SimpleDateFormat("EEE d MMM hh:mm a", Locale.getDefault());
    month = new SimpleDateFormat("MMM", Locale.getDefault());
    day = new SimpleDateFormat("d", Locale.getDefault());
    taskData = new Task();
    taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    notificationManager = NotificationManagerCompat.from(this);
  }

  private void initSpinner() {
    taskPriorityList = new ArrayList<>();
    taskPriorityList.add("Very high");
    taskPriorityList.add("High");
    taskPriorityList.add("Medium");
    taskPriorityList.add("Low");
    materialSpinner.setItem(taskPriorityList);

  }

  /**
   * This method is called to get the intent task data from the Recyclerview when the user is trying
   * to edit a task. The task details are fetched and set to the views so that user can edit the
   * task details efficiently. If the user is creating a new task thn the intent data will be null
   * .
   */
  private void getIntentDataFromRecyclerView() {
    intentTaskData = getIntentData(this, Constants.EXTRA_EDITABLE_TASK);
    if (intentTaskData != null) {
      taskTitleEdText.setText(intentTaskData.getTaskTitle());
      taskDescEdText.setText(intentTaskData.getTaskDescription());
      selectTimingTextView.setText(simpleDateFormat.format(intentTaskData.getDate()));
      taskData = intentTaskData;
      taskData.setAlarmStatus(false); // default alarm status
    }
  }

  private void initEventListeners() {
    materialSpinner.setOnItemSelectedListener(this);
    notificationSwitch.setOnCheckedChangeListener(this);
    taskViewModel.setOnDataChangeListener(this);
  }


  /**
   * This method is invoked when user taps on {@link R.id#textview_select_timing} TextView
   *
   * @param view
   */
  public void onSelectTimingTextViewClicked(View view) {
    new SingleDateAndTimePickerDialog.Builder(this)
        .setTimeZone(TimeZone.getDefault())
        .displayMinutes(true)
        .displayHours(true)
        .displayDays(true)
        .minutesStep(1)
        .mainColor(Color.parseColor("#e91e63"))
        .title("Choose timing")
        .displayListener(picker -> {
        })
        .listener(new SingleDateAndTimePickerDialog.Listener() {
          @Override
          public void onDateSelected(Date date) {
            selectTimingTextView.setText(simpleDateFormat.format(date));
            taskData.setDate(date);
            long timestamp = date.getTime();
            taskData.setTimeStamp(timestamp);
            taskData.setMonthStamp(month.format(date));
            taskData.setDayStamp(day.format(date));
          }
        })
        .display();
  }

  /**
   * This method is invoked when user taps on Save button {@link R.id#save_button} . Task details
   * are wrapped into Task Object and data change listener is set to listen for create task event.
   *
   * @param view
   */
  public void onSaveButtonClicked(View view) {
    taskData.setTaskTitle(taskTitleEdText.getText().toString());
    taskData.setTaskDescription(taskDescEdText.getText().toString());
    dataChangeListener = new FirebaseDataChangeListener(
        getApplicationContext(),
        taskData
    );

    dataChangeListener.setOnDataChangeListener(this, Constants.TASK_CREATE);
  }

  private void setAlarmNotification(Task taskData) {
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

    Intent intent = new Intent(
        CreateTaskActivity.this,
        NotificationReceiver.class);
    String _title = taskData.getTaskTitle();
    String _desc = taskData.getTaskDescription();
    String _taskId = taskData.getTaskId();
    String _monthKey = taskData.getMonthStamp();
    String _dayKey = taskData.getDayStamp();
    String[] data = {_title, _desc, _taskId, _monthKey, _dayKey};
    intent.putExtra(Constants.EXTRA_TASK_DATA_FOR_NOTIFICATION, data);

    PendingIntent pendingIntent = PendingIntent.getBroadcast(
        getApplicationContext(),
        (int) taskData.getTimeStamp(),
        intent,
        0);
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, taskData.getTimeStamp(), pendingIntent);
  }


  @Override
  public void onTaskCreated(boolean isSubmitted, String taskId) {
    if (isSubmitted) {
      if (taskData.getAlarmStatus()) {
        setAlarmNotification(taskData);
      }
      intent(CreateTaskActivity.this, HomeActivity.class);
    }
  }

  @Override
  public void onTaskMarked(String message) {
  }

  @Override
  public void onTaskUnMarked(String message) {
  }

  @Override
  public void onTaskDeleted(String message) {
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    taskData.setTaskPriority(taskPriorityList.get(position));
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }


  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      taskData.setAlarmStatus(isChecked);
  }
}