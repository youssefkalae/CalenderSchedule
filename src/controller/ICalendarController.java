package controller;

import model.ICalendar;
import view.ICalendarView;

/**
 * Interface for Calendar controller operations.
 * Defines the contract for the CONTROLLER component in MVC architecture.
 * Coordinates between the Model (Calendar) and View (CalendarView).
 */
public interface ICalendarController {

  /**
   * Processes a command string and returns the result.
   *
   * @param command the command to process
   * @return formatted response string or "EXIT" to terminate
   */
  String processCommand(String command);

  /**
   * Gets the calendar model for testing purposes.
   *
   * @return the calendar instance
   */
  ICalendar getCalendar();

  /**
   * Gets the view for testing purposes.
   *
   * @return the view instance
   */
  ICalendarView getView();
}