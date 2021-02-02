package com.saptarshi.tunemytask.adapters;

import static com.saptarshi.tunemytask.utils.ViewUtilsKt.toast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.saptarshi.tunemytask.R;
import com.saptarshi.tunemytask.models.Task;
import com.saptarshi.tunemytask.utils.Constants;
import com.saptarshi.tunemytask.views.home.HomeActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>
    implements FirebaseDataChangeListener.OnDataChangeListener {

  private static int menuPos;
  ArrayList<Task> taskList;
  SimpleDateFormat dateFormat;
  Context context;
  TaskViewHolder menuHolder;
  FirebaseDataChangeListener.OnDataChangeListener dataListener;
  FirebaseDataChangeListener firebaseDataChangeListener;


  public TaskAdapter(Context context, ArrayList<Task> taskList) {
    this.context = context;
    this.taskList = taskList;
    dateFormat = new SimpleDateFormat("hh:mm a");
    dataListener = this;
  }

  @NonNull
  @Override
  public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View view = layoutInflater.inflate(R.layout.tasklist_view, parent, false);
    return new TaskViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

    if (taskList.get(position).getCompletionStat()) {
      holder.markerLine.setVisibility(View.VISIBLE);
    }

    String title = taskList.get(position).getTaskTitle();
    String priority = taskList.get(position).getTaskPriority();
    Date date = taskList.get(position).getDate();
    String time = dateFormat.format(date).toUpperCase();
    String titleDesc = taskList.get(position).getTaskDescription();
    boolean alarmStatus = taskList.get(position).getAlarmStatus();
    holder.taskDesc.setText(titleDesc);
    holder.taskTitle.setText(title);
    holder.taskPriority.setText(priority);
    holder.taskTime.setText(time);

    if (alarmStatus) {
      holder.taskAlarm.setText("On");
    } else {
      holder.taskAlarm.setText("Off");
    }

    holder.taskRecyclerMenu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        initMenu(holder, position);
      }
    });

  }

  /**
   * This method is invoked when the menu {@link R.menu#recyler_taskmenu} in a Recyclerview item is
   * clicked.
   *
   * @param holder It holds all the views of the Recyclerview.
   * @param pos    It holds the position of the particular Recyclerview item which  maybe clicked or
   *               in focus.
   */
  private void initMenu(TaskViewHolder holder, int pos) {
    menuPos = pos;
    menuHolder = holder;
    PopupMenu popupMenu = new PopupMenu(context, holder.taskRecyclerMenu);
    popupMenu.inflate(R.menu.recyler_taskmenu);
    Menu menu = popupMenu.getMenu();
    if (taskList.size() != 0) {
      boolean isMarked = taskList.get(pos).getCompletionStat();
      if (isMarked) {
        menu.getItem(1).setTitle("Mark as incomplete");
      } else {
        menu.getItem(1).setTitle("Mark as complete");
      }
    }
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        firebaseDataChangeListener = new FirebaseDataChangeListener(
            context,
            taskList.get(pos)
        );

        switch (item.getItemId()) {
          case R.id.action_task_edit:
            firebaseDataChangeListener.setOnDataChangeListener(
                dataListener,
                Constants.TASK_EDIT);
            break;
          case R.id.action_marking_status:
            firebaseDataChangeListener.setOnDataChangeListener(
                dataListener,
                Constants.TASK_MARK);
            break;
          case R.id.action_task_delete:
            firebaseDataChangeListener.setOnDataChangeListener(
                dataListener,
                Constants.TASK_DELETE);
            break;
        }
        return false;
      }
    });
    popupMenu.show();
  }

  @Override
  public int getItemCount() {
    return taskList.size();
  }

  @Override
  public void onTaskMarked(String status) {
    menuHolder.markerLine.setVisibility(View.VISIBLE);
    toast(context, status);
  }

  @Override
  public void onTaskUnMarked(String status) {
    menuHolder.markerLine.setVisibility(View.INVISIBLE);
    toast(context, status);
  }

  @Override
  public void onTaskDeleted(String message) {
    if (taskList.size() != 0) {
//      taskList.remove(menuPos);
      notifyDataSetChanged();
      toast(context, message);
    } else {
      notifyDataSetChanged();
      toast(context, message);
      HomeActivity.noTaskTextView.setVisibility(View.VISIBLE);
      HomeActivity.noTaskImageView.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onTaskCreated(boolean isSubmitted, String taskId) {
  }

  public class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView taskTitle, taskTime, taskPriority, taskRecyclerMenu, taskAlarm;
    View markerLine;
    ExpandableTextView taskDesc;

    public TaskViewHolder(@NonNull View itemView) {
      super(itemView);
      taskTitle = itemView.findViewById(R.id.task_title);
      taskTime = itemView.findViewById(R.id.task_time);
      taskPriority = itemView.findViewById(R.id.task_priority);
      taskDesc = itemView.findViewById(R.id.expand_text_view);
      taskAlarm = itemView.findViewById(R.id.textview_alarm_status);
      markerLine = itemView.findViewById(R.id.marker_line);
      taskRecyclerMenu = itemView.findViewById(R.id.task_recycler_menu);
    }
  }
}
