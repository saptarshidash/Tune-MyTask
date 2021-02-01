package com.saptarshi.tunemytask.views.home;

import static com.saptarshi.tunemytask.utils.IntentUtil.intent;
import static com.saptarshi.tunemytask.utils.ViewUtilsKt.toast;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.saptarshi.tunemytask.R;
import com.saptarshi.tunemytask.adapters.TaskAdapter;
import com.saptarshi.tunemytask.viewmodels.AuthViewmodel;
import com.saptarshi.tunemytask.viewmodels.TaskViewModel;
import com.saptarshi.tunemytask.views.addtask.CreateTaskActivity;
import com.saptarshi.tunemytask.views.auth.AuthListeners;
import com.saptarshi.tunemytask.views.auth.LoginActivity;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity implements
    AuthListeners.AuthListener,
    CollapsibleCalendar.CalendarListener {

  public static TextView noTaskTextView, displayNameTextView;
  public static ImageView noTaskImageView;
  private Toolbar toolbar;
  private CollapsibleCalendar collapsibleCalendar;
  private FloatingActionButton createTaskButton;
  private TaskViewModel taskViewModel;
  private AuthViewmodel authViewmodel;
  private RecyclerView taskRecyclerView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    initViews();
    initToolBar();
    taskRecyclerView.setAdapter(new TaskAdapter(this, new ArrayList<>()));
    setEventListeners();
    int _month = collapsibleCalendar.getMonth();
    int _day = collapsibleCalendar.getSelectedDay().getDay();
    Pair<Integer, Integer> _date = new Pair<>(_month, _day);
    // subscribe to observers
    subscribeCalendarMarkerObserver(_month);
    subscribeCalendarTaskObserver(_date);
    authViewmodel.hasUserLoggedIn();
    taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
  }

  private void setEventListeners() {
    authViewmodel.setAuthListeners(this);
    collapsibleCalendar.setCalendarListener(this);
  }

  /**
   * This method is invoked when this Activity is created . It initializes all the view objects
   * along with some other objects like viewmodels recyclerviews etc.
   */
  private void initViews() {
    // init views
    noTaskTextView = findViewById(R.id.textview_nothing_todo);
    displayNameTextView = findViewById(R.id.textview_display_name);
    toolbar = findViewById(R.id.toolbar_home_activity);
    collapsibleCalendar = findViewById(R.id.collapsible_calendar);
    createTaskButton = findViewById(R.id.actionbutton_create_newtask);
    noTaskImageView = findViewById(R.id.imageview_home_bg);
    //init viewmodels
    taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
    authViewmodel = new ViewModelProvider(this).get(AuthViewmodel.class);
    // init recyclerview
    taskRecyclerView = findViewById(R.id.recyclerview_task_list);
  }

  private void initToolBar() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
  }

  /**
   * This method sets an Observer to observe the Task data list for a particular date. It observes
   * the data changes for that date and updates the Task Recyclerview accordingly.
   *
   * @param _date This pair holds a month and date which is passed to TaskViewmodel inorder to get
   *              the task list for that particular date.
   */
  private void subscribeCalendarTaskObserver(Pair<Integer, Integer> _date) {
    taskViewModel.getCalendarTasks(_date).observe(this, taskList -> {
      taskRecyclerView.setAdapter(new TaskAdapter(this, taskList));
      if (taskList.size() == 0) {
        noTaskImageView.setVisibility(View.VISIBLE);
        noTaskTextView.setVisibility(View.VISIBLE);
      } else {
        noTaskImageView.setVisibility(View.INVISIBLE);
        noTaskTextView.setVisibility(View.INVISIBLE);
      }
      taskViewModel.getCalendarTasks(_date).removeObservers(this);
    });
  }

  /**
   * This method sets an Observer to observe the date list for a particular month. After any changes
   * made to date list(new date added on which a user has created a task) it marks the calendar
   * dates accordingly.
   *
   * @param _month Holds the month for which the date list is observed.
   */
  private void subscribeCalendarMarkerObserver(int _month) {
    taskViewModel.markCalendarDates(_month).observe(this, dateList -> {
      for (String dates : dateList) {
        collapsibleCalendar.addEventTag(
            collapsibleCalendar.getYear(),
            collapsibleCalendar.getMonth(),
            Integer.parseInt(dates),
            Color.WHITE
        );
      }
      taskViewModel.markCalendarDates(_month).removeObservers(this);
    });

  }

  /**
   * This method is called when {@link R.id#actionbutton_create_newtask} button is clicked.
   *
   * @param view
   */
  public void onCreateTaskButtonClicked(View view) {
    intent(HomeActivity.this, CreateTaskActivity.class);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.homemenu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.actionLogout:
        authViewmodel.logoutUser();
        return true;
      case R.id.actionAccount:
        return true;
      default:
        return false;
    }
  }

  @Override
  public void onAuthSuccess(String message, FirebaseUser user) {
    String displayName = user.getDisplayName();
    displayNameTextView.setText("Hi, " + displayName);
  }

  @Override
  public void onAuthLogout(String message) {
    toast(getApplicationContext(), message);
    intent(HomeActivity.this, LoginActivity.class);
    finish();
  }

  @Override
  public void onAuthFailure(String error) {
    intent(HomeActivity.this, LoginActivity.class);
    toast(getApplicationContext(), error);
    finish();
  }

  @Override
  public void onClickListener() {
  }

  @Override
  public void onDataUpdate() {
  }

  @Override
  public void onDayChanged() {
  }

  @Override
  public void onDaySelect() {
    int date = collapsibleCalendar.getSelectedDay().getDay();
    int month = collapsibleCalendar.getMonth();
    Pair<Integer, Integer> timeStamp = new Pair<>(month, date);
    subscribeCalendarTaskObserver(timeStamp);
  }

  @Override
  public void onItemClick(@NotNull View view) {

  }

  @Override
  public void onMonthChange() {
    int month = collapsibleCalendar.getMonth();
    Pair<Integer, Integer> timeStamp = new Pair<>(month, 1); // default date 1 passed
    subscribeCalendarTaskObserver(timeStamp);
    subscribeCalendarMarkerObserver(month);
  }

  @Override
  public void onWeekChange(int i) {

  }
}