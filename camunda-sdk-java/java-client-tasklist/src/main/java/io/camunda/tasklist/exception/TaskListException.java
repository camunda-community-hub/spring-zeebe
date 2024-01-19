package io.camunda.tasklist.exception;

public class TaskListException extends Exception {
  private static final long serialVersionUID = -7593616210087047797L;

  public TaskListException() {
    super();
  }

  public TaskListException(Exception e) {
    super(e);
  }

  public TaskListException(String message) {
    super(message);
  }

  public TaskListException(String message, Exception e) {
    super(message, e);
  }
}
