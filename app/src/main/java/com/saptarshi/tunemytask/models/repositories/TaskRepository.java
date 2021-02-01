package com.saptarshi.tunemytask.models.repositories;

import android.app.Application;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.saptarshi.tunemytask.AppConfig;
import com.saptarshi.tunemytask.adapters.FirebaseDataChangeListener;
import com.saptarshi.tunemytask.models.Task;
import java.util.ArrayList;

public class TaskRepository {

  private static final String TAG = TaskRepository.class.getName();
  private final FirebaseUser currentUser;
  private final DatabaseReference dbRootReference;
  Application application;
  private DatabaseReference dbTaskDataRef;
  private DatabaseReference dbMonthRef;
  private DatabaseReference dbDateRef;
  private DatabaseReference dbUserIdRef;
  private ArrayList<String> datesList;
  private MutableLiveData<ArrayList<String>> calendarDatesLiveData;
  private MutableLiveData<ArrayList<Task>> calendarTaskLiveData;
  private FirebaseDataChangeListener.OnDataChangeListener listener;

  public TaskRepository() {
    currentUser = AppConfig.FIREBASE_USER_AUTH.getCurrentUser(); // get current user
    dbRootReference = AppConfig.FIREBASE_ROOT_REF; // root ref
    if (currentUser != null) {
      dbUserIdRef = dbRootReference.child(currentUser.getUid()); // current uid reference
      dbTaskDataRef = dbUserIdRef.child("Taskdata"); // Child Taskdata reference
    }
  }

  public TaskRepository(Application application) {
    this.application = application;
    currentUser = AppConfig.FIREBASE_USER_AUTH.getCurrentUser();
    dbRootReference = AppConfig.FIREBASE_ROOT_REF;// get current user
    if (currentUser != null) {
      dbUserIdRef = dbRootReference.child(currentUser.getUid());
      dbTaskDataRef = dbUserIdRef.child("Taskdata");// current uid reference
    }
  }

  public void setOnDataChangeListener(FirebaseDataChangeListener.OnDataChangeListener listener) {
    this.listener = listener;
  }

  /**
   * This method is called from TaskViewmodel. On successful execution this method saves the task
   * data in Firebase realtime db and then fires a callback.
   *
   * @param taskData Holds the task details of an user.
   */
  public void saveTaskDataInFirebase(Task taskData) {
    // get ref of child "Taskdata"
    if (taskData.getTaskId() == null) { // if user is creating a new task than setting an task id
      taskData.setTaskId(dbTaskDataRef.push().getKey());
    }
    String _refChildTaskId = taskData.getTaskId();
    String _refChildDay = taskData.getDayStamp();
    String _refChildMonth = taskData.getMonthStamp();
    dbTaskDataRef.child(_refChildMonth).child(_refChildDay)
        .child(_refChildTaskId).setValue(taskData, (error, ref) -> {
      if (error != null) {
        String _msg = "Task " + _refChildTaskId + " submission failed due to " + error;
        Log.d(TAG + ":TaskSubmission :", _msg);
      } else {
        String _msg = "Task " + _refChildTaskId + " submitted successfully";
        Log.d(TAG + ":TaskSubmission :", _msg);
        listener.onTaskCreated(true, _refChildTaskId);
      }
    });
  }

  /**
   * This method is called to delete a user task from database. On successful execution it fires a
   * callback to notify the recyclerview for ui change.
   *
   * @param taskData Holds the task which user wants to delete.
   */
  public void deleteTaskData(Task taskData) {
    String _refChildTaskId = taskData.getTaskId();
    String _refChildDay = taskData.getDayStamp();
    String _refChildMonth = taskData.getMonthStamp();
    dbTaskDataRef.child(_refChildMonth).child(_refChildDay)
        .child(_refChildTaskId).removeValue((error, ref) -> { // Completion listener
      if (error != null) {
        String _msg = "Unable to delete due to ";
        Log.d(TAG + ":TaskDeletion: ", _msg + error);
      } else {
        String _msg = "Task deleted successfully";
        Log.d(TAG + ":TaskDeletion: ", _msg);
        listener.onTaskDeleted(_msg);
      }
    });

  }

  public void changeTaskCompletionStatus(Task taskData) {
    String _refChildTaskId = taskData.getTaskId();
    String _refChildDay = taskData.getDayStamp();
    String _refChildMonth = taskData.getMonthStamp();
    boolean isCompleted = !taskData.getCompletionStat();
    taskData.setCompletionStat(isCompleted);
    dbTaskDataRef.child(_refChildMonth).child(_refChildDay)
        .child(_refChildTaskId).setValue(taskData);
    if (isCompleted) {
      listener.onTaskMarked("Task marked as complete");
    } else {
      listener.onTaskUnMarked("Task marked as incomplete");
    }
  }

  /**
   * This method is called to fetch the dates from firebase db on which the user has tasks to do.
   *
   * @param _month The month on which the dates need to be fetched.
   * @return This method returns a observable livedata to TaskViewmodel.
   */
  public MutableLiveData<ArrayList<String>> getCalendarDates(String _month) {
    loadCalendarDates(_month);
    return calendarDatesLiveData;
  }

  private void loadCalendarDates(String _month) {
    calendarDatesLiveData = new MutableLiveData<>();
    datesList = new ArrayList<>();
    dbMonthRef = dbTaskDataRef.child(_month);
    dbMonthRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot snap : snapshot.getChildren()) {
          datesList.add(snap.getKey());
          Log.d("Calanderdata", "" + snap.getKey());
        }
        calendarDatesLiveData.postValue(datesList);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d("TaskCount", "" + error.getMessage());
      }
    });
  }

  /**
   * This method is called to get the tasks of a user for a particular calendar date.
   *
   * @param date This Pair class object holds two strings month and day which is used to refer a
   *             child in firebase db.
   * @return This method returns a livedata to TaskViewmodel.
   */
  public MutableLiveData<ArrayList<Task>> getCalendarTasks(Pair<String, String> date) {
    String month = date.first;
    String day = date.second;
    loadCalendarTasks(month, day);
    return calendarTaskLiveData;
  }

  private void loadCalendarTasks(String month, String day) {
    calendarTaskLiveData = new MutableLiveData<>();
    ArrayList<Task> taskList = new ArrayList<>();
    dbDateRef = dbTaskDataRef.child(month).child(day);
    dbDateRef.orderByChild("timeStamp").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        taskList.clear();
        for (DataSnapshot taskSnap : snapshot.getChildren()) {
          taskList.add(taskSnap.getValue(Task.class));
        }
        calendarTaskLiveData.postValue(taskList);
        if (taskList.size() != 0) {
          Log.d("onTaskListFetched", "" + taskList);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Log.d("onTaskListFetched", error.getMessage());
      }
    });

  }

}
