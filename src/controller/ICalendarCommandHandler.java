package controller;

/**
 * Interface for handling calendar application commands.
 * Defines the contract for command processing and interactive command loop operations.
 */
public interface ICalendarCommandHandler {

  /**
   * Starts the interactive command loop.
   * This method should handle user input and process commands until the user exits.
   */
  void startCommandLoop();

  /**
   * Processes a single command and returns whether it was successful.
   * This method parses and executes the given command string.
   *
   * @param command the command string to process
   * @return true if the command was processed successfully, false otherwise
   */
  boolean processCommand(String command);

  /**
   * Shows help information for available commands.
   * This method should display all available commands and their syntax.
   */
  void showHelp();
}