import org.junit.Before;
import org.junit.Test;

import controller.CommandProcessor;
import model.Calendar;
import model.ICalendar;
import view.CalendarView;
import view.ICalendarView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive test suite for the CommandProcessor class.
 * Tests all command parsing, validation, and execution logic.
 */
public class CommandProcessorTest {

  private CommandProcessor processor;

  @Before
  public void setUp() {
    ICalendar calendar = new Calendar();
    ICalendarView view = new CalendarView();
    processor = new CommandProcessor(calendar, view);
  }

  /**
   * Tests processing an empty command string.
   */
  @Test
  public void testProcessCommand_EmptyCommand() {
    String result = processor.processCommand("");
    assertEquals("Error: Empty command.", result);
  }

  /**
   * Tests processing a command with only whitespace.
   */
  @Test
  public void testProcessCommand_WhitespaceOnly() {
    String result = processor.processCommand("   ");
    assertEquals("Error: Empty command.", result);
  }

  /**
   * Tests processing an invalid command returns proper error message.
   */
  @Test
  public void testProcessCommand_InvalidCommand() {
    String result = processor.processCommand("invalid command");
    assertEquals("Error: Invalid command: invalid command", result);
  }

  /**
   * Tests processing the exit command returns EXIT signal.
   */
  @Test
  public void testProcessCommand_ExitCommand() {
    String result = processor.processCommand("exit");
    assertEquals("EXIT", result);
  }

  /**
   * Tests that exit command is case insensitive.
   */
  @Test
  public void testProcessCommand_ExitCommandCaseInsensitive() {
    String result = processor.processCommand("EXIT");
    assertEquals("EXIT", result);

    result = processor.processCommand("Exit");
    assertEquals("EXIT", result);
  }

  /**
   * Tests creating a timed event with start and end times.
   */
  @Test
  public void testProcessCreateEventCommand_TimedEvent_Success() {
    String command = "create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00";
    String result = processor.processCommand(command);
    assertEquals("Event created successfully.", result);
  }

  /**
   * Tests creating a timed event with quoted subject containing spaces.
   */
  @Test
  public void testProcessCreateEventCommand_TimedEvent_QuotedSubject() {
    String command = "create event \"Team Meeting\" from 2025-06-05T10:00 to 2025-06-05T11:00";
    String result = processor.processCommand(command);
    assertEquals("Event created successfully.", result);
  }

  /**
   * Tests error handling when creating duplicate timed events.
   */
  @Test
  public void testProcessCreateEventCommand_TimedEvent_Duplicate() {
    String command = "create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00";
    processor.processCommand(command);
    String result = processor.processCommand(command);
    assertEquals("Error: Event with same subject, start time, and end time already " +
            "exist(s).", result);
  }

  /**
   * Tests creating an all-day event on a specific date.
   */
  @Test
  public void testProcessCreateEventCommand_AllDayEvent_Success() {
    String command = "create event Holiday on 2025-06-05";
    String result = processor.processCommand(command);
    assertEquals("All-day event created successfully.", result);
  }

  /**
   * Tests creating an all-day event with quoted subject.
   */
  @Test
  public void testProcessCreateEventCommand_AllDayEvent_QuotedSubject() {
    String command = "create event \"Independence Day\" on 2025-07-04";
    String result = processor.processCommand(command);
    assertEquals("All-day event created successfully.", result);
  }

  /**
   * Tests error handling when creating duplicate all-day events.
   */
  @Test
  public void testProcessCreateEventCommand_AllDayEvent_Duplicate() {
    String command = "create event Holiday on 2025-06-05";
    processor.processCommand(command);
    String result = processor.processCommand(command);
    assertEquals("Error: Event with same subject and date already exist(s).", result);
  }

  /**
   * Tests creating a timed event series with specific occurrence count.
   */
  @Test
  public void testProcessCreateEventCommand_TimedSeries_ForTimes() {
    String command = "create event \"Daily Standup\" from 2025-06-02T09:00 to 2025-06-02T09:30 " +
            "repeats MTWRF for 5 times";
    String result = processor.processCommand(command);
    assertEquals("Event series created successfully.", result);
  }

  /**
   * Tests creating a timed event series until a specific end date.
   */
  @Test
  public void testProcessCreateEventCommand_TimedSeries_UntilDate() {
    String command = "create event \"Weekly Review\" from 2025-06-06T14:00 to 2025-06-06T15:00 " +
            "repeats F until 2025-06-27";
    String result = processor.processCommand(command);
    assertEquals("Event series created successfully.", result);
  }

  /**
   * Tests creating an all-day event series with specific occurrence count.
   */
  @Test
  public void testProcessCreateEventCommand_AllDaySeries_ForTimes() {
    String command = "create event Training on 2025-06-02 repeats MTWRF for 3 times";
    String result = processor.processCommand(command);
    assertEquals("All-day event series created successfully.", result);
  }

  /**
   * Tests creating an all-day event series until a specific end date.
   */
  @Test
  public void testProcessCreateEventCommand_AllDaySeries_UntilDate() {
    String command = "create event Vacation on 2025-06-02 repeats MTWRF until 2025-06-06";
    String result = processor.processCommand(command);
    assertEquals("All-day event series created successfully.", result);
  }

  /**
   * Tests error handling when creating a series that conflicts with existing events.
   */
  @Test
  public void testProcessCreateEventCommand_Series_WithConflict() {
    processor.processCommand("create event Conflict from 2025-06-02T09:00 to 2025-06-02T09:30");

    String command = "create event Conflict from 2025-06-02T09:00 "
            + "to 2025-06-02T09:30 repeats M for 2 times";
    String result = processor.processCommand(command);

    assertEquals("Error: One or more events in the series already exist(s).", result);
  }

  /**
   * Tests weekday parsing for all seven days of the week.
   */
  @Test
  public void testWeekdayParsing_AllDays() {
    String command = "create event Daily from 2025-06-02T10:00 " +
            "to 2025-06-02T11:00 repeats MTWRFSU " +
            "for 7 times";
    String result = processor.processCommand(command);
    assertEquals("Event series created successfully.", result);
  }

  /**
   * Tests weekday parsing for business days only.
   */
  @Test
  public void testWeekdayParsing_BusinessDays() {
    String command = "create event Work from 2025-06-02T09:00 to 2025-06-02T17:00 repeats MTWRF " +
            "for 5 times";
    String result = processor.processCommand(command);
    assertEquals("Event series created successfully.", result);
  }

  /**
   * Tests weekday parsing for weekends only.
   */
  @Test
  public void testWeekdayParsingWeekends() {
    String command = "create event Weekend from 2025-06-01T10:00 to 2025-06-01T11:00 repeats " +
            "SU for 2 times";
    String result = processor.processCommand(command);
    assertEquals("Event series created successfully.", result);
  }

  /**
   * Tests weekday parsing for a single day of the week.
   */
  @Test
  public void testWeekdayParsingSingleDay() {
    String command = "create event Monday from 2025-06-02T10:00 to 2025-06-02T11:00 " +
            "repeats M for 3 times";
    String result = processor.processCommand(command);
    assertEquals("Event series created successfully.", result);
  }

  /**
   * Tests error handling for invalid timed event syntax.
   */
  @Test
  public void testProcessCreateEventCommand_InvalidTimedSyntax() {
    String command = "create event Meeting invalid syntax";
    String result = processor.processCommand(command);
    assertTrue("Should return error", result.startsWith("Error:"));
  }

  /**
   * Tests error handling for invalid all-day event syntax.
   */
  @Test
  public void testProcessCreateEventCommandInvalidAllDaySyntax() {
    String command = "create event Holiday invalid syntax";
    String result = processor.processCommand(command);
    assertTrue("Should return error", result.startsWith("Error:"));
  }

  /**
   * Tests error handling when closing quote is missing from subject.
   */
  @Test
  public void testProcessCreateEventCommand_MissingClosingQuote() {
    String command = "create event \"Missing quote from 2025-06-05T10:00 to 2025-06-05T11:00";
    String result = processor.processCommand(command);
    assertEquals("Error: Missing closing quote for subject.", result);
  }

  /**
   * Tests error handling for invalid date/time format.
   */
  @Test
  public void testProcessCreateEventCommandInvalidDateFormat() {
    String command = "create event Meeting from 2025-13-32T25:00 to 2025-06-05T11:00";
    String result = processor.processCommand(command);
    assertEquals("Error: Invalid date/time format.", result);
  }

  /**
   * Tests error handling when trying to create multi-day series events.
   */
  @Test
  public void testProcessCreateEventCommandMultiDaySeriesEvent() {
    String command = "create event Meeting from 2025-06-05T10:00 to 2025-06-06T11:00 repeats " +
            "M for 2 times";
    String result = processor.processCommand(command);
    assertEquals("Error: Events in a series cannot span multiple days.", result);
  }

  /**
   * Tests editing an entire event series successfully.
   */
  @Test
  public void testProcessEditEventCommand_EditSeries_Success() {
    processor.processCommand("create event Weekly from 2025-06-06T14:00 to 2025-06-06T15:00 " +
            "repeats F for 3 times");

    String command = "edit series subject Weekly from 2025-06-06T14:00 with NewWeekly";
    String result = processor.processCommand(command);
    assertEquals("Event(s) edited successfully.", result);
  }

  /**
   * Tests editing all possible event properties while tracking state changes.
   */
  @Test
  public void testProcessEditEventCommand_AllProperties() {
    processor.processCommand("create event Test from 2025-06-05T10:00 to 2025-06-05T11:00");

    String currentSubject = "Test";
    String currentStart = "2025-06-05T10:00";
    String currentEnd = "2025-06-05T11:00";

    String command = String.format("edit event subject %s from %s to %s with NewTest",
            currentSubject, currentStart, currentEnd);
    String result = processor.processCommand(command);
    assertEquals("Event(s) edited successfully.", result);
    currentSubject = "NewTest";

    command = String.format("edit event start %s from %s to %s with 2025-06-05T10:30",
            currentSubject, currentStart, currentEnd);
    result = processor.processCommand(command);
    assertEquals("Event(s) edited successfully.", result);
    currentStart = "2025-06-05T10:30";

    command = String.format("edit event end %s from %s to %s with 2025-06-05T11:30",
            currentSubject, currentStart, currentEnd);
    result = processor.processCommand(command);
    assertEquals("Event(s) edited successfully.", result);
    currentEnd = "2025-06-05T11:30";

    String[] properties = {"description", "location", "status"};
    String[] values = {"New desc", "New loc", "private"};

    for (int i = 0; i < properties.length; i++) {
      command = String.format("edit event %s %s from %s to %s with %s",
              properties[i], currentSubject, currentStart, currentEnd, values[i]);
      result = processor.processCommand(command);
      assertEquals("Event(s) edited successfully.", result);
    }
  }

  /**
   * Tests error handling when trying to edit with an invalid property name.
   */
  @Test
  public void testProcessEditEventCommandInvalidProperty() {
    processor.processCommand("create event Test from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "edit event invalid Test from 2025-06-05T10:00 to 2025-06-05T11:00 with value";
    String result = processor.processCommand(command);
    assertTrue("Should contain error about invalid property", result.contains(
            "Invalid property"));
  }

  /**
   * Tests error handling when setting an invalid status value.
   */
  @Test
  public void testProcessEditEventCommandInvalidStatus() {
    processor.processCommand("create event Test from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "edit event status Test from 2025-06-05T10:00 to 2025-06-05T11:00 with " +
            "invalid";
    String result = processor.processCommand(command);
    assertEquals("Error: Status must be 'public' or 'private'.", result);
  }

  /**
   * Tests error handling when trying to edit a non-existent event.
   */
  @Test
  public void testProcessEditEventCommand_EventNotFound() {
    String command = "edit event location Nonexistent from 2025-06-05T10:00 to 2025-06-05T11:00 " +
            "with Room";
    String result = processor.processCommand(command);
    assertEquals("Error: Could not find or edit the specified event(s).", result);
  }

  /**
   * Tests error handling when edit event command is missing end time.
   */
  @Test
  public void testProcessEditEventCommand_MissingEndTime() {
    String command = "edit event location Test from 2025-06-05T10:00 with Room";
    String result = processor.processCommand(command);
    assertEquals("Error: 'edit event' requires both start and end times.", result);
  }

  /**
   * Tests error handling for invalid edit command syntax.
   */
  @Test
  public void testProcessEditEventCommand_InvalidSyntax() {
    String command = "edit event invalid syntax";
    String result = processor.processCommand(command);
    assertTrue("Should return syntax error", result.startsWith("Error:"));
  }

  /**
   * Tests printing events on a specific date when events exist.
   */
  @Test
  public void testProcessPrintEventsCommand_OnDate_WithEvents() {
    processor.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");
    processor.processCommand("create event Lunch on 2025-06-05");

    String command = "print events on 2025-06-05";
    String result = processor.processCommand(command);

    assertTrue("Should contain date", result.contains("2025-06-05"));
    assertTrue("Should contain Meeting", result.contains("Meeting"));
    assertTrue("Should contain Lunch", result.contains("Lunch"));
  }

  /**
   * Tests printing events on a date when no events exist.
   */
  @Test
  public void testProcessPrintEventsCommandOnDateNoEvents() {
    String command = "print events on 2025-12-31";
    String result = processor.processCommand(command);
    assertEquals("No events on 2025-12-31.", result);
  }
}