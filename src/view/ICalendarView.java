package view;

import model.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for Calendar view operations.
 * Defines the contract for formatting and displaying calendar information.
 * Follows the VIEW component responsibilities in MVC architecture.
 */
public interface ICalendarView {

  /**
   * Formats events for display on a specific date.
   *
   * @param date   the target date
   * @param events list of events on that date
   * @return formatted string representation
   */
  String formatEventsOnDate(LocalDate date, List<Event> events);

  /**
   * Formats events for display in a date range.
   *
   * @param start  start date and time of range
   * @param end    end date and time of range
   * @param events list of events in the range
   * @return formatted string representation
   */
  String formatEventsInRange(LocalDateTime start, LocalDateTime end, List<Event> events);

  /**
   * Formats busy/available status.
   *
   * @param busy true if busy, false if available
   * @return formatted status string
   */
  String formatStatus(boolean busy);

  /**
   * Formats success messages.
   *
   * @param operation the operation that succeeded
   * @return formatted success message
   */
  String formatSuccess(String operation);

  /**
   * Formats error messages.
   *
   * @param message the error message
   * @return formatted error message
   */
  String formatError(String message);

  /**
   * Formats duplicate event error messages.
   *
   * @param context context of the duplication
   * @return formatted duplicate error message
   */
  String formatDuplicateError(String context);

  /**
   * Gets welcome message for interactive mode.
   *
   * @return welcome message
   */
  String getWelcomeMessage();

  /**
   * Gets goodbye message.
   *
   * @return goodbye message
   */
  String getGoodbyeMessage();
}