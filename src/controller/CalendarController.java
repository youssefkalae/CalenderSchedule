package controller;

import model.ICalendar;
import view.ICalendarView;

/**
 * CONTROLLER component - coordinates between Model (ICalendar) and View (ICalendarView).
 * Processes commands and delegates to appropriate model methods.
 * Implements ICalendarController interface for proper contract compliance.
 *
 * <p>Now properly uses interfaces instead of concrete classes and supports dependency injection
 * to reduce coupling and improve testability.
 */
public class CalendarController implements ICalendarController {
  private ICalendar calendar;
  private ICalendarView view;
  private ICommandProcessor commandProcessor;
  private Readable input;

  /**
   * Creates a new calendar controller with injected dependencies.
   * This constructor allows for proper dependency injection and loose coupling.
   *
   * @param calendar the calendar model (interface)
   * @param view the calendar view (interface)
   * @param input the input source for commands
   */
  public CalendarController(ICalendar calendar, ICalendarView view, Readable input) {
    this.calendar = calendar;
    this.view = view;
    this.input = input;
    this.commandProcessor = new CommandProcessor(calendar, view);
  }

  /**
   * Creates a new calendar controller with injected dependencies (without input).
   * This constructor is useful for headless mode or when input is handled externally.
   *
   * @param calendar the calendar model (interface)
   * @param view the calendar view (interface)
   */
  public CalendarController(ICalendar calendar, ICalendarView view) {
    this(calendar, view, null);
  }

  /**
   * Creates a new calendar controller with default components (for backward compatibility).
   * This constructor creates default implementations but still uses interfaces.
   */
  public CalendarController() {
    this.calendar = new model.Calendar();
    this.view = new view.CalendarView();
    this.input = null;
    this.commandProcessor = new CommandProcessor(calendar, view);
  }

  /**
   * Processes a command and returns formatted response.
   * Implementation of ICalendarController interface.
   *
   * @param command the command to process
   * @return formatted response string or "EXIT" to terminate
   */
  @Override
  public String processCommand(String command) {
    return commandProcessor.processCommand(command);
  }

  /**
   * Gets the calendar model (for testing).
   * Implementation of ICalendarController interface.
   *
   * @return the calendar instance
   */
  @Override
  public ICalendar getCalendar() {
    return calendar;
  }

  /**
   * Gets the view (for testing).
   * Implementation of ICalendarController interface.
   *
   * @return the view instance
   */
  @Override
  public ICalendarView getView() {
    return view;
  }

  /**
   * Gets the input source.
   *
   * @return the input source
   */
  public Readable getInput() {
    return input;
  }

  /**
   * Sets the input source.
   *
   * @param input the new input source
   */
  public void setInput(Readable input) {
    this.input = input;
  }
}