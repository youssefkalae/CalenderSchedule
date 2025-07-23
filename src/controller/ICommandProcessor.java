package controller;

/**
 * Interface for command processing operations.
 * Defines the contract for parsing and executing calendar commands.
 * This component handles the translation between user input and calendar operations.
 */
public interface ICommandProcessor {

  /**
   * Processes a command string and returns the result.
   *
   * @param command the command string to process
   * @return the result message or "EXIT" for exit commands
   */
  String processCommand(String command);
}