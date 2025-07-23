package controller;

import model.CalendarManager;
import view.CalendarGUI;
import view.ICalendarGUI;

/**
 * Controller specifically for the GUI mode of the calendar application.
 * Manages the interaction between the GUI view and the calendar model.
 * Follows MVC principles by coordinating between view and model components.
 */
public class CalendarGUIController implements ICalendarGUIController {

  private CalendarManager calendarManager;
  private ICalendarGUI gui;

  /**
   * Creates a new GUI controller with the specified calendar manager.
   *
   * @param calendarManager the calendar manager to use for business logic
   */
  public CalendarGUIController(CalendarManager calendarManager) {
    this.calendarManager = calendarManager;
    this.gui = new CalendarGUI(calendarManager);
  }

  /**
   * Creates a new GUI controller with dependency injection.
   *
   * @param calendarManager the calendar manager to use
   * @param gui            the GUI view to use
   */
  public CalendarGUIController(CalendarManager calendarManager, ICalendarGUI gui) {
    this.calendarManager = calendarManager;
    this.gui = gui;
  }

  /**
   * Starts the GUI application.
   * Makes the GUI visible and ready for user interaction.
   */
  public void startGUI() {
    gui.setVisible(true);
  }

  /**
   * Stops the GUI application.
   * Hides the GUI and performs any necessary cleanup.
   */
  public void stopGUI() {
    gui.setVisible(false);
  }

  /**
   * Gets the calendar manager used by this controller.
   *
   * @return the calendar manager instance
   */
  @Override
  public CalendarManager getCalendarManager() {
    return calendarManager;
  }

  /**
   * Gets the GUI view used by this controller.
   *
   * @return the GUI view instance
   */
  @Override
  public ICalendarGUI getGUI() {
    return gui;
  }

  /**
   * Refreshes the GUI display.
   * Updates all GUI components to reflect the current state of the model.
   */
  @Override
  public void refreshDisplay() {
    gui.updateScheduleView();
  }

  /**
   * Sets a new calendar manager and updates the GUI accordingly.
   *
   * @param calendarManager the new calendar manager to use
   */
  public void setCalendarManager(CalendarManager calendarManager) {
    this.calendarManager = calendarManager;
    refreshDisplay();
  }

  /**
   * Sets a new GUI view.
   *
   * @param gui the new GUI view to use
   */
  public void setGUI(ICalendarGUI gui) {
    this.gui = gui;
  }
}