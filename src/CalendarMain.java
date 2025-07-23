import controller.CalendarCommandHandler;
import model.CalendarManager;

/**
 * Main class for the enhanced calendar application with multi-calendar support.
 * This class initializes the calendar manager and starts the command-line interface.
 */
public class CalendarMain {

  /**
   * Entry point for the calendar application.
   *
   * <p>Initializes the calendar management system by creating a {@link CalendarManager}
   * instance to handle multiple calendars and a {@link CalendarCommandHandler} to process
   * user commands. The command handler is then started to begin accepting user input
   * through a command-line interface.
   */

  public static void main(String[] args) {
    CalendarManager calendarManager = new CalendarManager();

    CalendarCommandHandler commandHandler = new CalendarCommandHandler(calendarManager);

    commandHandler.startCommandLoop();
  }
}