package com.saptarshi.tunemytask.models;

import com.google.firebase.database.Exclude;
import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {

  private String taskTitle;
  private String taskDescription;
  private Date date;
  private long timeStamp;
  private String taskId;
  private boolean completionStat;
  private boolean alarmStatus;
  @Exclude
  private String monthStampKey, dateStampKey;
  private String taskPriority;

  public boolean getAlarmStatus() {
    return alarmStatus;
  }

  public void setAlarmStatus(boolean isAlarmSet) {
    this.alarmStatus = isAlarmSet;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public boolean getCompletionStat() {
    return completionStat;
  }

  public void setCompletionStat(boolean completionStat) {
    this.completionStat = completionStat;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  public String getMonthStamp() {
    return monthStampKey;
  }

  public void setMonthStamp(String monthStampKey) {
    this.monthStampKey = monthStampKey;
  }

  public String getDayStamp() {
    return dateStampKey;
  }

  public void setDayStamp(String dateStampKey) {
    this.dateStampKey = dateStampKey;
  }

  public String getTaskTitle() {
    return taskTitle;
  }

  public void setTaskTitle(String taskTitle) {
    this.taskTitle = taskTitle;
  }

  public String getTaskDescription() {
    return taskDescription;
  }

  public void setTaskDescription(String taskDescription) {
    this.taskDescription = taskDescription;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getTaskPriority() {
    return taskPriority;
  }

  public void setTaskPriority(String taskPriority) {
    this.taskPriority = taskPriority;
  }


}
