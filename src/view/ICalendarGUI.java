package view;

/**
 * Interface for the Calendar GUI view component.
 * Defines the contract for graphical user interface operations in the calendar application.
 * Follows MVC principles by separating view concerns from business logic.
 */
public interface ICalendarGUI {

  /**
   * Updates the schedule view to reflect current calendar state.
   * This method should refresh the display with the latest events and information.
   */
  void updateScheduleView();

  /**
   * Shows the create event dialog.
   * Allows users to create new events through a graphical form.
   */
  void showCreateEventDialog();

  /**
   * Shows the create calendar dialog.
   * Allows users to create new calendars through a graphical form.
   */
  void showCreateCalendarDialog();

  /**
   * Shows the edit event dialog.
   * Allows users to modify existing events through a graphical form.
   */
  void showEditEventDialog();

  /**
   * Shows an error message to the user.
   *
   * @param title   the title of the error dialog
   * @param message the error message to display
   */
  void showErrorMessage(String title, String message);

  /**
   * Shows a success message to the user.
   *
   * @param title   the title of the success dialog
   * @param message the success message to display
   */
  void showSuccessMessage(String title, String message);

  /**
   * Shows an informational message to the user.
   *
   * @param title   the title of the info dialog
   * @param message the informational message to display
   */
  void showInfoMessage(String title, String message);

  /**
   * Makes the GUI visible to the user.
   */
  void show();

  /**
   * Hides the GUI from the user.
   */
  void hide();

  /**
   * Sets the visibility of the GUI.
   * @param visible true to show the GUI, false to hide it
   */
  void setVisible(boolean visible);
}