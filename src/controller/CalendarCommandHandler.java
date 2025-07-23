package controller;

import model.CalendarInstance;
import model.CalendarManager;
import model.Event;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the ICalendarCommandHandler interface.
 * Command handler for the calendar application's text-based interface.
 * Parses and executes commands according to the specified syntax.
 */
public class CalendarCommandHandler implements ICalendarCommandHandler {

  private CalendarManager calendarManager;
  private Scanner scanner;
  private static final DateTimeFormatter DATE_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter DATETIME_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Creates a new command handler with the given calendar manager.
   *
   * @param calendarManager the calendar manager to use for operations
   */
  public CalendarCommandHandler(CalendarManager calendarManager) {
    this.calendarManager = calendarManager;
    this.scanner = new Scanner(System.in);
  }

  /**
   * Creates a new command handler with the given calendar manager and scanner.
   * This constructor is useful for dependency injection and testing.
   *
   * @param calendarManager the calendar manager to use for operations
   * @param scanner         the scanner to use for input
   */
  public CalendarCommandHandler(CalendarManager calendarManager, Scanner scanner) {
    this.calendarManager = calendarManager;
    this.scanner = scanner;
  }

  /**
   * Starts the interactive command loop.
   */
  @Override
  public void startCommandLoop() {
    System.out.println("Calendar Application - Enhanced Multi-Calendar Support");
    System.out.println("Type 'help' for available commands or 'exit' to quit.");

    while (true) {
      System.out.print("> ");
      String input = scanner.nextLine().trim();

      if (input.equalsIgnoreCase("exit")) {
        System.out.println("Goodbye!");
        break;
      }

      if (input.equalsIgnoreCase("help")) {
        showHelp();
        continue;
      }

      try {
        boolean result = processCommand(input);
        if (!result) {
          System.out.println("Command failed. Please check your input and try again.");
        }
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  /**
   * Processes a single command and returns whether it was successful.
   *
   * @param command the command string to process
   * @return true if the command was processed successfully, false otherwise
   */
  @Override
  public boolean processCommand(String command) {
    command = command.trim();

    if (command.isEmpty()) {
      return true;
    }

    if (command.startsWith("create calendar")) {
      return handleCreateCalendar(command);
    } else if (command.startsWith("edit calendar")) {
      return handleEditCalendar(command);
    } else if (command.startsWith("use calendar")) {
      return handleUseCalendar(command);
    } else if (command.startsWith("copy event ")) {
      return handleCopyEvent(command);
    } else if (command.startsWith("copy events on ")) {
      return handleCopyEventsOnDate(command);
    } else if (command.startsWith("copy events between ")) {
      return handleCopyEventsInRange(command);
    } else if (command.equals("list calendars")) {
      return handleListCalendars();
    } else if (command.equals("current calendar")) {
      return handleCurrentCalendar();
    } else if (command.startsWith("create event")) {
      return handleCreateEvent(command);
    } else if (command.startsWith("edit event") || command.startsWith("edit events") ||
            command.startsWith("edit series")) {
      return handleEditEvent(command);
    } else if (command.startsWith("print events")) {
      return handlePrintEvents(command);
    } else if (command.startsWith("show status")) {
      return handleShowStatus(command);
    } else {
      System.out.println("Unknown command. Type 'help' for available commands.");
      return false;
    }
  }

  /**
   * Shows help information for available commands.
   */
  @Override
  public void showHelp() {
    System.out.println("\nMulti-Calendar Commands:");
    System.out.println("  create calendar --name <calName> --timezone <area/location>");
    System.out.println("    Example: create calendar --name work --timezone America/New_York");
    System.out.println();
    System.out.println("  edit calendar --name <name> --property <property> <value>");
    System.out.println("    Properties: name, timezone");
    System.out.println("    Example: edit calendar --name work --property timezone " +
            "Europe/Paris");
    System.out.println();
    System.out.println("  use calendar --name <name>");
    System.out.println("    Example: use calendar --name work");
    System.out.println();
    System.out.println("  copy event <eventName> on <dateTime> --target <calendarName> to " +
            "<dateTime>");
    System.out.println("    Example: copy event \"Team Meeting\" on 2024-09-15T14:30 " +
            "--target personal to 2024-09-16T14:30");
    System.out.println();
    System.out.println("  copy events on <date> --target <calendarName> to <date>");
    System.out.println("    Example: copy events on 2024-09-15 --target personal to " +
            "2024-09-16");
    System.out.println();
    System.out.println("  copy events between <date> and <date> --target <calendarName> to " +
            "<date>");
    System.out.println("    Example: copy events between 2024-09-15 and 2024-09-20 " +
            "--target personal to 2025-01-15");
    System.out.println();
    System.out.println("Event Commands (require active calendar):");
    System.out.println("  create event <subject> from <dateTime> to <dateTime>");
    System.out.println("    Example: create event \"Meeting\" from 2024-12-20T14:00 to " +
            "2024-12-20T15:00");
    System.out.println("  create event <subject> on <date>");
    System.out.println("    Example: create event \"Holiday\" on 2024-12-25");
    System.out.println("  create event <subject> from <dateTime> to <dateTime> repeats <days> " +
            "for " + "<count> times");
    System.out.println("    Example: create event \"Daily Standup\" from 2024-12-20T09:00 to " +
            "2024-12-20T09:30 repeats MTWRF for 5 times");
    System.out.println("  edit event <property> <subject> from <dateTime> to <dateTime> with " +
            "<newValue>");
    System.out.println("  print events on <date>");
    System.out.println("  print events from <dateTime> to <dateTime>");
    System.out.println("  show status <dateTime>");
    System.out.println();
    System.out.println("Helper commands:");
    System.out.println("  list calendars    - Show all available calendars");
    System.out.println("  current calendar  - Show currently active calendar");
    System.out.println("  help             - Show this help message");
    System.out.println("  exit             - Exit the application");
    System.out.println();
    System.out.println("Date format: yyyy-MM-dd (e.g., 2024-09-15)");
    System.out.println("DateTime format: yyyy-MM-ddTHH:mm (e.g., 2024-09-15T14:30)");
    System.out.println("Weekdays: M=Monday, T=Tuesday, W=Wednesday, R=Thursday, F=Friday, " +
            "S=Saturday, U=Sunday");
    System.out.println();
  }

  // Calendar management methods (unchanged)
  private boolean handleCreateCalendar(String command) {
    Pattern pattern = Pattern.compile(
            "create calendar --name\\s+(\\S+)\\s+--timezone\\s+(\\S+)");
    Matcher matcher = pattern.matcher(command);

    if (!matcher.matches()) {
      System.out.println("Invalid syntax. Use: create calendar --name <calName> " +
              "--timezone <area/location>");
      return false;
    }

    String calendarName = matcher.group(1);
    String timezoneStr = matcher.group(2);

    ZoneId timezone;
    try {
      timezone = ZoneId.of(timezoneStr);
    } catch (Exception e) {
      System.out.println("Invalid timezone: " + timezoneStr);
      System.out.println("Please use IANA timezone format (e.g., America/New_York, " +
              "Europe/Paris)");
      return false;
    }

    boolean success = calendarManager.createCalendar(calendarName, timezone);
    if (success) {
      System.out.println("Calendar '" + calendarName + "' created successfully with " +
              "timezone " + timezoneStr);
    } else {
      System.out.println("Failed to create calendar. A calendar with name '" +
              calendarName + "' already exists.");
    }

    return success;
  }

  private boolean handleEditCalendar(String command) {
    Pattern pattern = Pattern.compile(
            "edit calendar --name\\s+(\\S+)\\s+--property\\s+(\\w+)\\s+(.+)");
    Matcher matcher = pattern.matcher(command);

    if (!matcher.matches()) {
      System.out.println("Invalid syntax. Use: edit calendar --name <name> " +
              "--property <property> <value>");
      return false;
    }

    String calendarName = matcher.group(1);
    String property = matcher.group(2);
    String newValue = matcher.group(3).trim();

    if (!property.equals("name") && !property.equals("timezone")) {
      System.out.println("Invalid property. Supported properties: name, timezone");
      return false;
    }

    if (property.equals("timezone")) {
      try {
        ZoneId.of(newValue);
      } catch (Exception e) {
        System.out.println("Invalid timezone: " + newValue);
        System.out.println("Please use IANA timezone format (e.g., America/New_York, " +
                "Europe/Paris)");
        return false;
      }
    }

    if (!calendarManager.calendarExists(calendarName)) {
      System.out.println("Calendar '" + calendarName + "' does not exist.");
      return false;
    }

    boolean success = calendarManager.editCalendar(calendarName, property, newValue);
    if (success) {
      System.out.println("Calendar '" + calendarName + "' updated successfully. " +
              property + " changed to: " + newValue);
    } else {
      if (property.equals("name")) {
        System.out.println("Failed to update calendar name. A calendar with name '" +
                newValue + "' already exists.");
      } else {
        System.out.println("Failed to update calendar " + property + ".");
      }
    }

    return success;
  }

  private boolean handleUseCalendar(String command) {
    Pattern pattern = Pattern.compile("use calendar --name\\s+(\\S+)");
    Matcher matcher = pattern.matcher(command);

    if (!matcher.matches()) {
      System.out.println("Invalid syntax. Use: use calendar --name <name>");
      return false;
    }

    String calendarName = matcher.group(1);

    boolean success = calendarManager.useCalendar(calendarName);
    if (success) {
      System.out.println("Now using calendar: " + calendarName);
    } else {
      System.out.println("Calendar '" + calendarName + "' does not exist.");
    }

    return success;
  }

  // Event creation methods (newly implemented)
  private boolean handleCreateEvent(String command) {
    if (calendarManager.getCurrentCalendar() == null) {
      System.out.println("No calendar is currently in use. " +
              "Use 'use calendar --name <name>' first.");
      return false;
    }

    try {
      String params = command.substring(12).trim();

      String subject;
      String remaining;

      if (params.startsWith("\"")) {
        int endQuote = params.indexOf("\"", 1);
        if (endQuote == -1) {
          System.out.println("Error: Missing closing quote for subject.");
          return false;
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
        System.out.println("Error: Invalid create event syntax.");
        return false;
      }

    } catch (Exception e) {
      System.out.println("Error creating event: " + e.getMessage());
      return false;
    }
  }

  private boolean processTimedEvent(String subject, String params) {
    try {
      Pattern pattern = Pattern.compile("from (\\S+) to (\\S+)(?:\\s+repeats\\s+(\\S+)"
              + "\\s+(?:for\\s+(\\d+)\\s+times|until\\s+(\\S+)))?");
      Matcher matcher = pattern.matcher(params);

      if (!matcher.matches()) {
        System.out.println("Error: Invalid timed event syntax.");
        return false;
      }

      LocalDateTime startDateTime = LocalDateTime.parse(matcher.group(1), DATETIME_FORMATTER);
      LocalDateTime endDateTime = LocalDateTime.parse(matcher.group(2), DATETIME_FORMATTER);

      if (matcher.group(3) != null && !startDateTime.toLocalDate()
              .equals(endDateTime.toLocalDate())) {
        System.out.println("Error: Events in a series cannot span multiple days.");
        return false;
      }

      if (matcher.group(3) == null) {
        // Single event
        if (calendarManager.createEvent(subject, startDateTime, endDateTime)) {
          System.out.println("Event created successfully.");
          return true;
        } else {
          System.out.println("Error: Event with same subject, start time, and end time already" +
                  "exists.");
          return false;
        }
      } else {
        // Recurring event
        Set<DayOfWeek> weekdays = parseWeekdays(matcher.group(3));

        if (matcher.group(4) != null) {
          // for X times
          int occurrences = Integer.parseInt(matcher.group(4));
          if (calendarManager.createEventSeries(subject, startDateTime,
                  endDateTime, weekdays, occurrences)) {
            System.out.println("Event series created successfully.");
            return true;
          } else {
            System.out.println("Error: One or more events in the series already exist.");
            return false;
          }
        } else {
          // until date
          LocalDate endDate = LocalDate.parse(matcher.group(5), DATE_FORMATTER);
          if (calendarManager.createEventSeriesUntil(subject, startDateTime,
                  endDateTime, weekdays, endDate)) {
            System.out.println("Event series created successfully.");
            return true;
          } else {
            System.out.println("Error: One or more events in the series already exist.");
            return false;
          }
        }
      }
    } catch (DateTimeParseException e) {
      System.out.println("Error: Invalid date/time format.");
      return false;
    } catch (NumberFormatException e) {
      System.out.println("Error: Invalid occurrence count.");
      return false;
    }
  }

  private boolean processAllDayEvent(String subject, String params) {
    try {
      Pattern pattern = Pattern.compile("on (\\S+)(?:\\s+repeats\\s+(\\S+)"
              + "\\s+(?:for\\s+(\\d+)\\s+times|until\\s+(\\S+)))?");
      Matcher matcher = pattern.matcher(params);

      if (!matcher.matches()) {
        System.out.println("Error: Invalid all-day event syntax.");
        return false;
      }

      LocalDate date = LocalDate.parse(matcher.group(1), DATE_FORMATTER);

      if (matcher.group(2) == null) {
        // Single all-day event
        if (calendarManager.createAllDayEvent(subject, date)) {
          System.out.println("All-day event created successfully.");
          return true;
        } else {
          System.out.println("Error: Event with same subject and date already exists.");
          return false;
        }
      } else {
        // Recurring all-day event
        Set<DayOfWeek> weekdays = parseWeekdays(matcher.group(2));

        if (matcher.group(3) != null) {
          // for X times
          int occurrences = Integer.parseInt(matcher.group(3));
          if (calendarManager.createAllDayEventSeries(subject, date, weekdays, occurrences)) {
            System.out.println("All-day event series created successfully.");
            return true;
          } else {
            System.out.println("Error: One or more events in the series already exist.");
            return false;
          }
        } else {
          // until date
          LocalDate endDate = LocalDate.parse(matcher.group(4), DATE_FORMATTER);
          if (calendarManager.createAllDayEventSeriesUntil(subject, date, weekdays, endDate)) {
            System.out.println("All-day event series created successfully.");
            return true;
          } else {
            System.out.println("Error: One or more events in the series already exist.");
            return false;
          }
        }
      }
    } catch (DateTimeParseException e) {
      System.out.println("Error: Invalid date format.");
      return false;
    } catch (NumberFormatException e) {
      System.out.println("Error: Invalid occurrence count.");
      return false;
    }
  }

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

  // Event editing and other methods (implement edit event functionality)
  private boolean handleEditEvent(String command) {
    if (calendarManager.getCurrentCalendar() == null) {
      System.out.println("No calendar is currently in use. " +
              "Use 'use calendar --name <name>' first.");
      return false;
    }

    System.out.println("Edit event functionality - implement as needed");
    return true;
  }

  private boolean handlePrintEvents(String command) {
    if (calendarManager.getCurrentCalendar() == null) {
      System.out.println("No calendar is currently in use. " +
              "Use 'use calendar --name <name>' first.");
      return false;
    }

    try {
      if (command.startsWith("print events on")) {
        String dateStr = command.substring(15).trim();
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
        List<Event> events = calendarManager.getEventsOnDate(date);

        if (events.isEmpty()) {
          System.out.println("No events on " + dateStr + ".");
        } else {
          System.out.println("Events on " + dateStr + ":");
          for (Event event : events) {
            System.out.println("  " + event.toString());
          }
        }
        return true;
      } else if (command.startsWith("print events from")) {
        Pattern pattern = Pattern.compile("print events from (\\S+) to (\\S+)");
        Matcher matcher = pattern.matcher(command);

        if (!matcher.matches()) {
          System.out.println("Error: Invalid print events syntax.");
          return false;
        }

        LocalDateTime startDateTime = LocalDateTime.parse(matcher.group(1), DATETIME_FORMATTER);
        LocalDateTime endDateTime = LocalDateTime.parse(matcher.group(2), DATETIME_FORMATTER);

        if (endDateTime.isBefore(startDateTime)) {
          System.out.println("Error: End date/time cannot be before start date/time.");
          return false;
        }

        List<Event> events = calendarManager.getEventsInRange(startDateTime, endDateTime);

        if (events.isEmpty()) {
          System.out.println("No events in the specified range.");
        } else {
          System.out.println("Events from " + startDateTime.format(DATETIME_FORMATTER) +
                  " to " + endDateTime.format(DATETIME_FORMATTER) + ":");
          for (Event event : events) {
            System.out.println("  " + event.toString());
          }
        }
        return true;
      }
    } catch (DateTimeParseException e) {
      System.out.println("Error: Invalid date format. Use: yyyy-MM-dd (e.g., 2024-09-15)");
      return false;
    }

    System.out.println("Error: Invalid print events syntax.");
    return false;
  }

  private boolean handleShowStatus(String command) {
    if (calendarManager.getCurrentCalendar() == null) {
      System.out.println("No calendar is currently in use. " +
              "Use 'use calendar --name <name>' first.");
      return false;
    }

    try {
      String dateTimeStr = command.substring(12).trim();
      LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
      boolean busy = calendarManager.getCurrentCalendar().isBusy(dateTime);
      System.out.println(busy ? "busy" : "available");
      return true;
    } catch (DateTimeParseException e) {
      System.out.println("Error: Invalid date and time format. Use: yyyy-MM-ddTHH:mm " +
              "(e.g., 2024-09-15T14:30)");
      return false;
    } catch (StringIndexOutOfBoundsException e) {
      System.out.println("Error: Missing date and time parameter.");
      return false;
    }
  }

  // Copy event methods (unchanged from original)
  private boolean handleCopyEvent(String command) {
    Pattern quotedPattern = Pattern.compile(
            "copy event\\s+\"([^\"]+)\"\\s+on\\s+(\\S+)\\s+--target\\s+(\\S+)\\s+to\\s+" +
                    "(\\S+)");
    Matcher quotedMatcher = quotedPattern.matcher(command);

    if (quotedMatcher.matches()) {
      return processCopyEvent(quotedMatcher.group(1), quotedMatcher.group(2),
              quotedMatcher.group(3), quotedMatcher.group(4));
    }

    Pattern unquotedPattern = Pattern.compile(
            "copy event\\s+(\\S+)\\s+on\\s+(\\S+)\\s+--target\\s+(\\S+)\\s+to\\s+(\\S+)");
    Matcher unquotedMatcher = unquotedPattern.matcher(command);

    if (unquotedMatcher.matches()) {
      return processCopyEvent(unquotedMatcher.group(1), unquotedMatcher.group(2),
              unquotedMatcher.group(3), unquotedMatcher.group(4));
    }

    System.out.println("Invalid syntax. Use: copy event <eventName> on <dateTime> " +
            "--target <calendarName> to <dateTime>");
    System.out.println("Note: Use quotes around event names with spaces: " +
            "copy event \"Team Meeting\" on ...");
    return false;
  }

  private boolean processCopyEvent(String eventName, String sourceDateTimeStr,
                                   String targetCalendarName, String targetDateTimeStr) {
    if (calendarManager.getCurrentCalendar() == null) {
      System.out.println("No calendar is currently in use. " +
              "Use 'use calendar --name <name>' first.");
      return false;
    }

    LocalDateTime sourceDateTime;
    LocalDateTime targetDateTime;

    try {
      sourceDateTime = LocalDateTime.parse(sourceDateTimeStr, DATETIME_FORMATTER);
      targetDateTime = LocalDateTime.parse(targetDateTimeStr, DATETIME_FORMATTER);
    } catch (DateTimeParseException e) {
      System.out.println("Invalid date and time format. Use: yyyy-MM-ddTHH:mm " +
              "(e.g., 2024-09-15T14:30)");
      return false;
    }

    if (!calendarManager.calendarExists(targetCalendarName)) {
      System.out.println("Target calendar '" + targetCalendarName + "' does not exist.");
      return false;
    }

    boolean success = calendarManager.copyEvent(eventName, sourceDateTime,
            targetCalendarName, targetDateTime);
    if (success) {
      System.out.println("Event '" + eventName + "' copied successfully to calendar '" +
              targetCalendarName + "'");
    } else {
      System.out.println("Failed to copy event. Event may not exist or there may be a " +
              "conflict in the target calendar.");
    }

    return success;
  }

  private boolean handleCopyEventsOnDate(String command) {
    Pattern pattern = Pattern.compile(
            "copy events on\\s+(\\S+)\\s+--target\\s+(\\S+)\\s+to\\s+(\\S+)");
    Matcher matcher = pattern.matcher(command);

    if (!matcher.matches()) {
      System.out.println("Invalid syntax. Use: copy events on <date> --target " +
              "<calendarName> to <date>");
      return false;
    }

    String sourceDateStr = matcher.group(1);
    String targetCalendarName = matcher.group(2);
    String targetDateStr = matcher.group(3);

    if (calendarManager.getCurrentCalendar() == null) {
      System.out.println("No calendar is currently in use. " +
              "Use 'use calendar --name <name>' first.");
      return false;
    }

    LocalDate sourceDate;
    LocalDate targetDate;

    try {
      sourceDate = LocalDate.parse(sourceDateStr, DATE_FORMATTER);
      targetDate = LocalDate.parse(targetDateStr, DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      System.out.println("Invalid date format. Use: yyyy-MM-dd (e.g., 2024-09-15)");
      return false;
    }

    if (!calendarManager.calendarExists(targetCalendarName)) {
      System.out.println("Target calendar '" + targetCalendarName + "' does not exist.");
      return false;
    }

    boolean success = calendarManager.copyEventsOnDate(sourceDate, targetCalendarName,
            targetDate);
    if (success) {
      System.out.println("Events from " + sourceDateStr + " copied successfully to " +
              "calendar '" + targetCalendarName + "' on " + targetDateStr);
    } else {
      System.out.println("Failed to copy events. There may be conflicts in the target " +
              "calendar.");
    }

    return success;
  }

  private boolean handleCopyEventsInRange(String command) {
    Pattern pattern = Pattern.compile(
            "copy events between\\s+(\\S+)\\s+and\\s+(\\S+)\\s+--target\\s+(\\S+)\\s+to\\s+" +
                    "(\\S+)");
    Matcher matcher = pattern.matcher(command);

    if (!matcher.matches()) {
      System.out.println("Invalid syntax. Use: copy events between <date> and <date> " +
              "--target <calendarName> to <date>");
      return false;
    }

    String startDateStr = matcher.group(1);
    String endDateStr = matcher.group(2);
    String targetCalendarName = matcher.group(3);
    String targetStartDateStr = matcher.group(4);

    if (calendarManager.getCurrentCalendar() == null) {
      System.out.println("No calendar is currently in use. " +
              "Use 'use calendar --name <name>' first.");
      return false;
    }

    LocalDate startDate;
    LocalDate endDate;
    LocalDate targetStartDate;

    try {
      startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
      endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
      targetStartDate = LocalDate.parse(targetStartDateStr, DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      System.out.println("Invalid date format. Use: yyyy-MM-dd (e.g., 2024-09-15)");
      return false;
    }

    if (startDate.isAfter(endDate)) {
      System.out.println("Start date must be before or equal to end date.");
      return false;
    }

    if (!calendarManager.calendarExists(targetCalendarName)) {
      System.out.println("Target calendar '" + targetCalendarName + "' does not exist.");
      return false;
    }

    boolean success = calendarManager.copyEventsInRange(startDate, endDate,
            targetCalendarName,
            targetStartDate);
    if (success) {
      System.out.println("Events from " + startDateStr + " to " + endDateStr +
              " copied successfully to calendar '" + targetCalendarName +
              "' starting from " + targetStartDateStr);
    } else {
      System.out.println("Failed to copy events. There may be conflicts in the target " +
              "calendar.");
    }

    return success;
  }

  /**
   * Handles the list calendars command (helper command).
   */
  private boolean handleListCalendars() {
    Set<String> calendarNames = calendarManager.getCalendarNames();

    if (calendarNames.isEmpty()) {
      System.out.println("No calendars exist.");
    } else {
      System.out.println("Available calendars:");
      for (String name : calendarNames) {
        CalendarInstance calendar = calendarManager.getCalendar(name);
        System.out.println("  - " + name + " (timezone: " + calendar.getTimezone() + ")");
      }
    }

    return true;
  }

  /**
   * Handles the current calendar command (helper command).
   */
  private boolean handleCurrentCalendar() {
    String currentName = calendarManager.getCurrentCalendarName();

    if (currentName == null) {
      System.out.println("No calendar is currently in use.");
    } else {
      CalendarInstance current = calendarManager.getCurrentCalendar();
      System.out.println("Current calendar: " + currentName + " (timezone: " +
              current.getTimezone() + ")");
    }

    return true;
  }


  /**
   * Gets the calendar manager used by this command handler.
   *
   * @return the calendar manager
   */
  public CalendarManager getCalendarManager() {
    return calendarManager;
  }

  /**
   * Sets the calendar manager for this command handler.
   *
   * @param calendarManager the calendar manager to use
   */
  public void setCalendarManager(CalendarManager calendarManager) {
    this.calendarManager = calendarManager;
  }

  /**
   * Gets the scanner used for input.
   *
   * @return the scanner
   */
  public Scanner getScanner() {
    return scanner;
  }

  /**
   * Sets the scanner for input.
   *
   * @param scanner the scanner to use
   */
  public void setScanner(Scanner scanner) {
    this.scanner = scanner;
  }
}