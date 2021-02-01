package com.saptarshi.tunemytask.adapters;

import static com.saptarshi.tunemytask.utils.IntentUtil.intent;

import android.app.Activity;
import android.content.Context;
import com.saptarshi.tunemytask.models.Task;
import com.saptarshi.tunemytask.models.repositories.TaskRepository;
import com.saptarshi.tunemytask.utils.Constants;
import com.saptarshi.tunemytask.views.addtask.CreateTaskActivity;

public class FirebaseDataChangeListener {

  private final Task taskData;
  private final TaskRepository taskRepository;
  private final Context context;

  /**
   * This Constructor will be invoked when user wants to perform some task CRUD operations
   *
   * @param taskData
   */
  public FirebaseDataChangeListener(Context context, Task taskData) {
    this.context = context;
    this.taskData = taskData;
    taskRepository = new TaskRepository();
  }

  /**
   * Sets a listener when user wants to perform a task modification.
   *
   * @param listener  the listener to notify
   * @param CRUD_TYPE the task modification type eg: mark, un mark, edit, delete
   */
  public void setOnDataChangeListener(OnDataChangeListener listener, int CRUD_TYPE) {
    taskRepository.setOnDataChangeListener(listener);
    if (CRUD_TYPE == Constants.TASK_MARK) {
      taskRepository.changeTaskCompletionStatus(taskData);
    } else if (CRUD_TYPE == Constants.TASK_DELETE) {
      taskRepository.deleteTaskData(taskData);
    } else if (CRUD_TYPE == Constants.TASK_EDIT) {
      intent(
          (Activity) context,
          CreateTaskActivity.class,
          Constants.EXTRA_EDITABLE_TASK, taskData
      ); // pass task data to AddTaskActivity
    } else if (CRUD_TYPE == Constants.TASK_CREATE) {
      taskRepository.saveTaskDataInFirebase(taskData);
    }
  }


  /**
   * Interface responsible for receiving task modification events for eg. Task marked, unmarked,
   * deleted, edited
   */
  public interface OnDataChangeListener {

    /**
     * Called when a Task item in the Recyclerview is marked.
     *
     * @param message This variable holds the success message.
     */
    void onTaskMarked(String message);

    /**
     * Called when a Task item in the Recyclerview gets unmarked.
     *
     * @param message Holds the success message.
     */
    void onTaskUnMarked(String message);

    /**
     * Called when a Task gets deleted.
     *
     * @param message Holds the success message.
     */
    void onTaskDeleted(String message);

    void onTaskCreated(boolean isSubmitted, String taskId);
  }

}
