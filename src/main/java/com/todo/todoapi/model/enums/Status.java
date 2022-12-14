package com.todo.todoapi.model.enums;

public enum Status {

  PENDING("Pending"),
  COMPLETED("Completed");

  private final String text;

  /**
   * @param text
   */
  Status(final String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return text;
  }

}
