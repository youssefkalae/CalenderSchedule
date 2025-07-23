package controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.ICalendar;
import model.Event;
import view.ICalendarView;

/**
 * Enhanced Command processor - now properly separated from model.
 * Uses ICalendar (model interface) and ICalendarView (view interface) for proper MVC separation.
 * Implements ICommandProcessor interface.
 */
public class CommandProcessor implements ICommandProcessor {
  private ICalendar calendar;
  private ICalendarView view;

  private static final DateTimeFormatter DATE_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATETIME_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Creates a new command processor with MVC components.
   * FIXED: Constructor parameters use interfaces
   */
  public CommandProcessor(ICalendar calendar, ICalendarView view) {
    this.calendar = calendar;
    this.view = view;
  }

  /**
   * Processes a command string.
   *
   * @param command command to process
   * @return result message
   */
  @Override
  public String processCommand(String command) {
    try {
      command = command.trim();
      if (command.isEmpty()) {
        return view.formatError("Empty command.");
      }

      if (command.equalsIgnoreCase("exit")) {
        return "EXIT";
      }

      if (command.startsWith("create event")) {
        return processCreateEventCommand(command);
      }

      if (command.startsWith("edit event") || command.startsWith("edit events")
              || command.startsWith("edit series")) {
        return processEditEventCommand(command);
      }

      if (command.startsWith("print events")) {
        return processPrintEventsCommand(command);
      }

      if (command.startsWith("show status")) {
        return processShowStatusCommand(command);
      }

      return view.formatError("Invalid command: " + command);

    } catch (Exception e) {
      return view.formatError("Error processing command: " + e.getMessage());
    }
  }

  /**
   * Enhanced create event command processing with better error handling.
   */
  private String processCreateEventCommand(String command) {
    try {
      String params = command.substring(12).trim();

      String subject;
      String remaining;

      if (params.startsWith("\"")) {
        int endQuote = params.indexOf("\"", 1);
        if (endQuote == -1) {
          return view.formatError("Missing closing quote for subject.");
        }
        subject = params.substring(1, endQuote);
        remaining = params.substring(endQuote + 1).trim();
      } else {
        String[] parts = params.split("\\s+", 2);
        subject = parts[0];
        remaining = parts.length > 1 ? parts[1] : "";
      }

      if (remaining.startsWith("from")) {
        return processTimedEvent(subject, remaining);
      } else if (remaining.startsWith("on")) {
        return processAllDayEvent(subject, remaining);
      } else {
        return view.formatError("Invalid create event syntax.");
      }

    } catch (Exception e) {
      return view.formatError("Error creating event: " + e.getMessage());
    }
  }

  /**
   * Enhanced timed event processing.
   */
  private String processTimedEvent(String subject, String params) {
    try {
      Pattern pattern = Pattern.compile("from (\\S+) to (\\S+)(?:\\s+repeats\\s+(\\S+)"
              + "\\s+(?:for\\s+(\\d+)\\s+times|until\\s+(\\S+)))?");
      Matcher matcher = pattern.matcher(params);

      if (!matcher.matches()) {
        return view.formatError("Invalid timed event syntax.");
      }

      LocalDateTime startDateTime = LocalDateTime.parse(matcher.group(1), DATETIME_FORMATTER);
      LocalDateTime endDateTime = LocalDateTime.parse(matcher.group(2), DATETIME_FORMATTER);

      if (matcher.group(3) != null && !startDateTime.toLocalDate()
              .equals(endDateTime.toLocalDate())) {
        return view.formatError("Events in a series cannot span multiple days.");
      }

      if (matcher.group(3) == null) {
        if (calendar.createEvent(subject, startDateTime, endDateTime)) {
          return view.formatSuccess("Event created");
        } else {
          return view.formatDuplicateError("Event with same subject, start time, and end time");
        }
      } else {
        Set<DayOfWeek> weekdays = parseWeekdays(matcher.group(3));

        if (matcher.group(4) != null) {
          int occurrences = Integer.parseInt(matcher.group(4));
          if (calendar.createEventSeries(subject, startDateTime,
                  endDateTime, weekdays, occurrences)) {
            return view.formatSuccess("Event series created");
          } else {
            return view.formatDuplicateError("One or more events in the series");
          }
        } else {
          LocalDate endDate = LocalDate.parse(matcher.group(5), DATE_FORMATTER);
          if (calendar.createEventSeriesUntil(subject, startDateTime,
                  endDateTime, weekdays, endDate)) {
            return view.formatSuccess("Event series created");
          } else {
            return view.formatDuplicateError("One or more events in the series");
          }
        }
      }
    } catch (DateTimeParseException e) {
      return view.formatError("Invalid date/time format.");
    } catch (NumberFormatException e) {
      return view.formatError("Invalid occurrence count.");
    }
  }

  /**
   * Enhanced all-day event processing.
   */
  private String processAllDayEvent(String subject, String params) {
    try {
      Pattern pattern = Pattern.compile("on (\\S+)(?:\\s+repeats\\s+(\\S+)"
              + "\\s+(?:for\\s+(\\d+)\\s+times|until\\s+(\\S+)))?");
      Matcher matcher = pattern.matcher(params);

      if (!matcher.matches()) {
        return view.formatError("Invalid all-day event syntax.");
      }

      LocalDate date = LocalDate.parse(matcher.group(1), DATE_FORMATTER);

      if (matcher.group(2) == null) {
        if (calendar.createAllDayEvent(subject, date)) {
          return view.formatSuccess("All-day event created");
        } else {
          return view.formatDuplicateError("Event with same subject and date");
        }
      } else {
        Set<DayOfWeek> weekdays = parseWeekdays(matcher.group(2));

        if (matcher.group(3) != null) {
          int occurrences = Integer.parseInt(matcher.group(3));
          if (calendar.createAllDayEventSeries(subject, date, weekdays, occurrences)) {
            return view.formatSuccess("All-day event series created");
          } else {
            return view.formatDuplicateError("One or more events in the series");
          }
        } else {
          LocalDate endDate = LocalDate.parse(matcher.group(4), DATE_FORMATTER);
          if (calendar.createAllDayEventSeriesUntil(subject, date, weekdays, endDate)) {
            return view.formatSuccess("All-day event series created");
          } else {
            return view.formatDuplicateError("One or more events in the series");
          }
        }
      }
    } catch (DateTimeParseException e) {
      return view.formatError("Invalid date format.");
    } catch (NumberFormatException e) {
      return view.formatError("Invalid occurrence count.");
    }
  }

  /**
   * Refactored edit command processing - now broken into smaller methods.
   */
  private String processEditEventCommand(String command) {
    try {
      EditCommandParams params = parseEditCommand(command);
      if (params == null) {
        return view.formatError("Invalid edit command syntax.");
      }

      String validationError = validateEditParameters(params);
      if (validationError != null) {
        return validationError;
      }

      boolean success = executeEditCommand(params);
      return success ? view.formatSuccess("Event(s) edited")
              : view.formatError("Could not find or edit the specified event(s).");

    } catch (DateTimeParseException e) {
      return view.formatError("Invalid date/time format.");
    } catch (Exception e) {
      return view.formatError("Invalid edit command syntax.");
    }
  }

  /**
   * Helper method to parse edit command parameters.
   */
  private EditCommandParams parseEditCommand(String command) {
    String[] parts = command.split("\\s+", 3);
    if (parts.length < 3) {
      return null;
    }

    String editType = parts[1];
    String remaining = parts[2];

    String[] propertyParts = remaining.split("\\s+", 2);
    if (propertyParts.length < 2) {
      return null;
    }

    String property = propertyParts[0];
    remaining = propertyParts[1];

    String subject = parseSubject(remaining);
    if (subject == null) {
      return null;
    }

    remaining = removeSubjectFromString(remaining, subject);

    Pattern pattern = Pattern.compile("from (\\S+)(?:\\s+to\\s+(\\S+))?\\s+with\\s+(.+)");
    Matcher matcher = pattern.matcher(remaining);

    if (!matcher.matches()) {
      return null;
    }

    try {
      LocalDateTime startDateTime = LocalDateTime.parse(matcher.group(1), DATETIME_FORMATTER);
      LocalDateTime endDateTime = null;
      if (matcher.group(2) != null) {
        endDateTime = LocalDateTime.parse(matcher.group(2), DATETIME_FORMATTER);
      }
      String newValue = matcher.group(3);

      return new EditCommandParams(editType, property, subject, startDateTime,
              endDateTime, newValue);
    } catch (DateTimeParseException e) {
      return null;
    }
  }

  /**
   * Helper method to parse subject from command string.
   */
  private String parseSubject(String remaining) {
    if (remaining.startsWith("\"")) {
      int endQuote = remaining.indexOf("\"", 1);
      if (endQuote == -1) {
        return null;
      }
      return remaining.substring(1, endQuote);
    } else {
      String[] subjectParts = remaining.split("\\s+", 2);
      return subjectParts[0];
    }
  }

  /**
   * Helper method to remove subject from string after parsing.
   */
  private String removeSubjectFromString(String remaining, String subject) {
    if (remaining.startsWith("\"")) {
      int endQuote = remaining.indexOf("\"", 1);
      return remaining.substring(endQuote + 1).trim();
    } else {
      String[] subjectParts = remaining.split("\\s+", 2);
      return subjectParts.length > 1 ? subjectParts[1] : "";
    }
  }

  /**
   * Helper method to validate edit command parameters.
   */
  private String validateEditParameters(EditCommandParams params) {
    if (!isValidProperty(params.property)) {
      return view.formatError("Invalid property: " + params.property
              + ". Valid properties are: subject, start, end, description, location, status");
    }

    if ("status".equals(params.property)) {
      if (!"public".equalsIgnoreCase(params.newValue) &&
              !"private".equalsIgnoreCase(params.newValue)) {
        return view.formatError("Status must be 'public' or 'private'.");
      }
    }

    if ("event".equals(params.editType) && params.endDateTime == null) {
      return view.formatError("'edit event' requires both start and end times.");
    }

    return null;
  }

  /**
   * Helper method to execute the edit command based on type.
   */
  private boolean executeEditCommand(EditCommandParams params) {
    switch (params.editType) {
      case "event":
        return calendar.editEvent(params.property, params.subject,
                params.startDateTime, params.endDateTime, params.newValue);
      case "events":
        return calendar.editEventsFromDate(params.property, params.subject,
                params.startDateTime, params.newValue);
      case "series":
        return calendar.editEntireSeries(params.property, params.subject,
                params.startDateTime, params.newValue);
      default:
        return false;
    }
  }

  /**
   * Enhanced print events command processing.
   */
  private String processPrintEventsCommand(String command) {
    try {
      if (command.startsWith("print events on")) {
        String dateStr = command.substring(15).trim();
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
        List<Event> events = calendar.getEventsOnDate(date);
        return view.formatEventsOnDate(date, events);

      } else if (command.startsWith("print events from")) {
        Pattern pattern = Pattern.compile("print events from (\\S+) to (\\S+)");
        Matcher matcher = pattern.matcher(command);

        if (!matcher.matches()) {
          return view.formatError("Invalid print events syntax.");
        }

        LocalDateTime startDateTime = LocalDateTime.parse(matcher.group(1), DATETIME_FORMATTER);
        LocalDateTime endDateTime = LocalDateTime.parse(matcher.group(2), DATETIME_FORMATTER);

        if (endDateTime.isBefore(startDateTime)) {
          return view.formatError("End date/time cannot be before start date/time.");
        }

        List<Event> events = calendar.getEventsInRange(startDateTime, endDateTime);
        return view.formatEventsInRange(startDateTime, endDateTime, events);
      }

      return view.formatError("Invalid print events syntax.");

    } catch (DateTimeParseException e) {
      return view.formatError("Invalid date/time format.");
    }
  }

  /**
   * Enhanced show status command processing.
   */
  private String processShowStatusCommand(String command) {
    try {
      String dateTimeStr = command.substring(12).trim();
      LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
      boolean busy = calendar.isBusy(dateTime);
      return view.formatStatus(busy);

    } catch (DateTimeParseException e) {
      return view.formatError("Invalid date/time format.");
    } catch (StringIndexOutOfBoundsException e) {
      return view.formatError("Missing date/time parameter.");
    }
  }

  /**
   * Validates property names.
   */
  private boolean isValidProperty(String property) {
    return "subject".equals(property) ||
            "start".equals(property) ||
            "end".equals(property) ||
            "description".equals(property) ||
            "location".equals(property) ||
            "status".equals(property);
  }

  /**
   * Enhanced weekday parsing with validation.
   */
  private Set<DayOfWeek> parseWeekdays(String weekdayStr) {
    Set<DayOfWeek> weekdays = new HashSet<>();
    for (char c : weekdayStr.toCharArray()) {
      switch (c) {
        case 'M':
          weekdays.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          weekdays.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          weekdays.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          weekdays.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          weekdays.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          weekdays.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          weekdays.add(DayOfWeek.SUNDAY);
          break;
        default:
          break;
      }
    }
    return weekdays;
  }

  /**
   * Inner class to hold edit command parameters.
   * This class encapsulates all the parameters needed for edit commands.
   */
  private static class EditCommandParams {
    final String editType;
    final String property;
    final String subject;
    final LocalDateTime startDateTime;
    final LocalDateTime endDateTime;
    final String newValue;

    /**
     * Constructor for EditCommandParams.
     *
     * @param editType      the type of edit (event, events, series)
     * @param property      the property to edit
     * @param subject       the event subject
     * @param startDateTime the start date/time
     * @param endDateTime   the end date/time (can be null)
     * @param newValue      the new value for the property
     */
    EditCommandParams(String editType, String property, String subject,
                      LocalDateTime startDateTime, LocalDateTime endDateTime, String newValue) {
      this.editType = editType;
      this.property = property;
      this.subject = subject;
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
      this.newValue = newValue;
    }
  }
}