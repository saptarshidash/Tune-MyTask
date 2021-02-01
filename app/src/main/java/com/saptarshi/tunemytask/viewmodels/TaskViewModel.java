package com.saptarshi.tunemytask.viewmodels;

import android.app.Application;
import android.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.saptarshi.tunemytask.adapters.FirebaseDataChangeListener;
import com.saptarshi.tunemytask.models.Task;
import com.saptarshi.tunemytask.models.repositories.TaskRepository;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;


public class TaskViewModel extends AndroidViewModel {

  private final TaskRepository taskRepository;

  public TaskViewModel(Application application) {
    super(application);
    taskRepository = new TaskRepository(application);
  }

  /**
   * This method is called to pass an OnTaskDataListener object from AddTaskActivity to
   * TaskRepository class so that it can setup the listener to notify the AddTaskActivity about
   * various task data CRUD events..
   *
   * @param listener Holds OnTaskDataListener passed from AddTaskActivity.
   */
  public void setOnDataChangeListener(FirebaseDataChangeListener.OnDataChangeListener listener) {
    taskRepository.setOnDataChangeListener(listener);
  }

  /**
   * This method is called from HomeActivity in order to fetch the dates from TaskRepository so that
   * they can be marked in the Calendar view.
   *
   * @param month Holds the Integer month index for which the dates need to be fetched.
   * @return Returns a Livedata object which is observed by the HomeActivity.
   */
  public MutableLiveData<ArrayList<String>> markCalendarDates(int month) {
    String date = new DateFormatSymbols().getMonths()[month];
    date = StringUtils.left(date, 3);
    return taskRepository.getCalendarDates(date);
  }

  /**
   * This method is invoked by HomeActivity to fetch the task data from TaskRepository.
   *
   * @param date It holds a month day integer pair which is converted to string and passed to
   *             TaskRepository.
   * @return Returns a livedata object which is observed by HomeActivity to get notification about
   * data change and update the UI accordingly.
   */
  public MutableLiveData<ArrayList<Task>> getCalendarTasks(Pair<Integer, Integer> date) {
    String month = new DateFormatSymbols().getMonths()[date.first];
    month = StringUtils.left(month, 3); // trims month eg. jan feb mar
    String day = String.valueOf(date.second);
    return taskRepository.getCalendarTasks(new Pair<String, String>(month, day));
  }


}
