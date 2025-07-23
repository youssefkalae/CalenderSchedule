package view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.Event;

/**
 * Implementation of ICalendarView interface.
 * VIEW component - handles all output formatting and display.
 * Separates presentation logic from business logic (MVC pattern).
 */
public class CalendarView implements ICalendarView {
  private static final DateTimeFormatter DATE_FORMATTER
          = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATETIME_FORMATTER
          = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  @Override
  public String formatEventsOnDate(LocalDate date, List<Event> events) {
    if (events.isEmpty()) {
      return "No events on " + date.format(DATE_FORMATTER) + ".";
    }

    StringBuilder result = new StringBuilder("Events on " + date.format(DATE_FORMATTER) + ":\n");
    for (Event event : events) {
      result.append(event.toString()).append("\n");
    }
    return result.toString().trim();
  }

  @Override
  public String formatEventsInRange(LocalDateTime start, LocalDateTime end, List<Event> events) {
    if (events.isEmpty()) {
      return "No events in the specified range.";
    }

    StringBuilder result = new StringBuilder("Events from " + start.format(DATETIME_FORMATTER)
            + " to " + end.format(DATETIME_FORMATTER) + ":\n");
    for (Event event : events) {
      result.append(event.toString()).append("\n");
    }
    return result.toString().trim();
  }

  @Override
  public String formatStatus(boolean busy) {
    return busy ? "busy" : "available";
  }

  @Override
  public String formatSuccess(String operation) {
    return operation + " successfully.";
  }

  @Override
  public String formatError(String message) {
    return "Error: " + message;
  }

  @Override
  public String formatDuplicateError(String context) {
    return "Error: " + context + " already exist(s).";
  }

  @Override
  public String getWelcomeMessage() {
    return "Calendar Application - Interactive Mode\nType 'exit' to quit.";
  }

  @Override
  public String getGoodbyeMessage() {
    return "Goodbye!";
  }
}