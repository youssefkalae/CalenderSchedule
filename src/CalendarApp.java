import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import controller.CalendarCommandHandler;
import controller.CalendarGUIController;
import model.CalendarManager;
import javax.swing.SwingUtilities;

/**
 * Enhanced Main application class for the Calendar Application.
 * Now supports interactive, headless, and GUI modes with proper MVC architecture.
 * Uses dependency injection and interface-based design for better testability.
 */
public class CalendarApp {

  /**
   * Main method to run the Calendar Application.
   * Supports three modes: GUI (default), interactive, and headless.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      runGUIMode();
      return;
    }

    if (args.length >= 2 && args[0].equalsIgnoreCase("--mode")) {
      String mode = args[1].toLowerCase();

      switch (mode) {
        case "interactive":
          if (args.length != 2) {
            printUsageAndExit();
          }
          runInteractiveMode();
          break;
        case "headless":
          if (args.length != 3) {
            printUsageAndExit();
          }
          runHeadlessMode(args[2]);
          break;
        case "gui":
          if (args.length != 2) {
            printUsageAndExit();
          }
          runGUIMode();
          break;
        default:
          printUsageAndExit();
      }
    } else {
      printUsageAndExit();
    }
  }

  /**
   * Prints usage information and exits the program.
   */
  private static void printUsageAndExit() {
    System.err.println("Usage:");
    System.err.println("  java -jar Calendar.jar                           " +
            "- Launch GUI mode");
    System.err.println("  java -jar Calendar.jar --mode interactive        " +
            "- Launch interactive text mode");
    System.err.println("  java -jar Calendar.jar --mode headless <script>  " +
            "- Run headless mode with script file");
    System.err.println("  java -jar Calendar.jar --mode gui                " +
            "- Launch GUI mode");
    System.exit(1);
  }

  /**
   * Runs the application in interactive mode using the original MVC architecture.
   */
  private static void runInteractiveMode() {
    CalendarManager calendarManager = new CalendarManager();
    CalendarCommandHandler commandHandler = new CalendarCommandHandler(calendarManager);

    commandHandler.startCommandLoop();
  }

  /**
   * Runs the application in headless mode using the original MVC architecture.
   *
   * @param filename commands file name
   */
  private static void runHeadlessMode(String filename) {
    CalendarManager calendarManager = new CalendarManager();
    CalendarCommandHandler commandHandler = new CalendarCommandHandler(calendarManager);

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      boolean foundExit = false;

      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
          continue;
        }

        // Check for exit BEFORE processing the command
        if (line.equalsIgnoreCase("exit")) {
          foundExit = true;
          System.out.println("Goodbye!");
          break;
        }

        boolean result = commandHandler.processCommand(line);
        if (!result) {
          System.err.println("Command failed: " + line);
        }
      }

      if (!foundExit) {
        System.err.println("Error: Commands file must end with 'exit' command.");
        System.exit(1);
      }

    } catch (IOException e) {
      System.err.println("Error reading commands file: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Runs the application in GUI mode using the new multi-calendar architecture.
   * Uses SwingUtilities.invokeLater to ensure thread safety with Swing components.
   */
  private static void runGUIMode() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
          javax.swing.UIManager.setLookAndFeel(
                  javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
          System.err.println("Warning: Could not set system look and feel: " +
                  e.getMessage());
        }

        CalendarManager calendarManager = new CalendarManager();

        CalendarGUIController guiController = new CalendarGUIController(calendarManager);
        guiController.startGUI();

        System.out.println("Calendar GUI started successfully.");
        System.out.println("Close the GUI window to exit the application.");
      }
    });
  }
}