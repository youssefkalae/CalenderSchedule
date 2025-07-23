package controller;

import model.CalendarManager;
import view.ICalendarGUI;

/**
 * Interface for the Calendar GUI Controller.
 * Defines the contract for managing GUI-based calendar operations.
 * Follows MVC principles by providing a clear separation between view and model coordination.
 */
public interface ICalendarGUIController {

  /**
   * Gets the calendar manager associated with this controller.
   *
   * @return the calendar manager instance
   */
  CalendarManager getCalendarManager();

  /**
   * Gets the GUI view associated with this controller.
   *
   * @return the GUI view instance
   */
  ICalendarGUI getGUI();


  /**
   * Refreshes the GUI display to reflect current model state.
   * Should be called when the underlying data changes.
   */
  void refreshDisplay();
}