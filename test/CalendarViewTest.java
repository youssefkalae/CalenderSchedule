import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.Event;
import model.EventStatus;
import view.CalendarView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Test suite for the CalendarView class.
 * Tests all formatting methods for proper output generation.
 */
public class CalendarViewTest {

  private CalendarView view;
  private LocalDate testDate;
  private LocalDateTime testDateTime;
  private LocalDateTime testDateTime2;

  @Before
  public void setUp() {
    view = new CalendarView();
    testDate = LocalDate.of(2025, 6, 5);
    testDateTime = LocalDateTime.of(2025, 6, 5, 10, 0);
    testDateTime2 = LocalDateTime.of(2025, 6, 5, 12, 0);
  }

  /**
   * Tests formatting events on a date when events exist.
   */
  @Test
  public void testFormatEventsOnDateWithEvents() {
    List<Event> events = new ArrayList<>();
    events.add(new Event("Meeting", testDateTime, testDateTime.plusHours(1)));
    events.add(new Event("Lunch", testDate));

    String result = view.formatEventsOnDate(testDate, events);

    assertTrue("Should contain date", result.contains("2025-06-05"));
    assertTrue("Should contain Meeting", result.contains("Meeting"));
    assertTrue("Should contain Lunch", result.contains("Lunch"));
    assertTrue("Should start with 'Events on'", result.startsWith("Events on"));
  }

  /**
   * Tests formatting events on a date when no events exist.
   */
  @Test
  public void testFormatEventsOnDate_NoEvents() {
    List<Event> emptyEvents = new ArrayList<>();
    String result = view.formatEventsOnDate(testDate, emptyEvents);

    assertEquals("No events on 2025-06-05.", result);
  }

  /**
   * Tests that events are formatted with proper bullet points and details.
   */
  @Test
  public void testFormatEventsOnDateEventFormatting() {
    List<Event> events = new ArrayList<>();
    Event timedEvent = new Event("Meeting", testDateTime, testDateTime.plusHours(1),
            "Important meeting", "Room 101", EventStatus.PUBLIC);
    events.add(timedEvent);

    String result = view.formatEventsOnDate(testDate, events);

    assertTrue("Should contain bullet point", result.contains("•"));
    assertTrue("Should contain time range", result.contains("10:00 - 11:00"));
    assertTrue("Should contain location", result.contains("Room 101"));
  }

  /**
   * Tests formatting all-day events shows "All Day" instead of times.
   */
  @Test
  public void testFormatEventsOnDateAllDayEvent() {
    List<Event> events = new ArrayList<>();
    events.add(new Event("Holiday", testDate));

    String result = view.formatEventsOnDate(testDate, events);

    assertTrue("Should contain 'All Day'", result.contains("All Day"));
    assertTrue("Should contain Holiday", result.contains("Holiday"));
  }

  /**
   * Tests formatting events in a date/time range when events exist.
   */
  @Test
  public void testFormatEventsInRangeWithEvents() {
    List<Event> events = new ArrayList<>();
    events.add(new Event("Event1", testDateTime, testDateTime.plusHours(1)));
    events.add(new Event("Event2", testDateTime2, testDateTime2.plusHours(1)));

    String result = view.formatEventsInRange(testDateTime, testDateTime2.plusHours(2), events);

    assertTrue("Should contain 'Events from'", result.startsWith("Events from"));
    assertTrue("Should contain start datetime", result.contains("2025-06-05 10:00"));
    assertTrue("Should contain end datetime", result.contains("2025-06-05 14:00"));
    assertTrue("Should contain Event1", result.contains("Event1"));
    assertTrue("Should contain Event2", result.contains("Event2"));
  }

  /**
   * Tests formatting events in a range when no events exist.
   */
  @Test
  public void testFormatEventsInRange_NoEvents() {
    List<Event> emptyEvents = new ArrayList<>();
    String result = view.formatEventsInRange(testDateTime, testDateTime2, emptyEvents);

    assertEquals("No events in the specified range.", result);
  }

  /**
   * Tests that multiple events in a range are displayed in proper order.
   */
  @Test
  public void testFormatEventsInRangeMultipleEvents() {
    List<Event> events = new ArrayList<>();
    for (int i = 1; i <= 3; i++) {
      events.add(new Event("Event" + i, testDateTime.plusHours(i),
              testDateTime.plusHours(i + 1)));
    }

    String result = view.formatEventsInRange(testDateTime, testDateTime.plusHours(5), events);

    assertTrue("Should contain Event1", result.contains("Event1"));
    assertTrue("Should contain Event2", result.contains("Event2"));
    assertTrue("Should contain Event3", result.contains("Event3"));

    int event1Pos = result.indexOf("Event1");
    int event2Pos = result.indexOf("Event2");
    int event3Pos = result.indexOf("Event3");
    assertTrue("Events should be in order", event1Pos < event2Pos && event2Pos <
            event3Pos);
  }

  /**
   * Tests formatting busy status returns "busy".
   */
  @Test
  public void testFormatStatus_Busy() {
    String result = view.formatStatus(true);
    assertEquals("busy", result);
  }

  /**
   * Tests formatting available status returns "available".
   */
  @Test
  public void testFormatStatus_Available() {
    String result = view.formatStatus(false);
    assertEquals("available", result);
  }

  /**
   * Tests formatting success messages with "successfully" suffix.
   */
  @Test
  public void testFormatSuccess() {
    String result = view.formatSuccess("Event created");
    assertEquals("Event created successfully.", result);
  }

  /**
   * Tests formatting success messages for different operations.
   */
  @Test
  public void testFormatSuccessDifferentOperations() {
    assertEquals("Event edited successfully.",
            view.formatSuccess("Event edited"));
    assertEquals("Event deleted successfully.",
            view.formatSuccess("Event deleted"));
    assertEquals("Series created successfully.",
            view.formatSuccess("Series created"));
  }

  /**
   * Tests formatting error messages with "Error:" prefix.
   */
  @Test
  public void testFormatError() {
    String result = view.formatError("Invalid command");
    assertEquals("Error: Invalid command", result);
  }

  /**
   * Tests formatting different types of error messages.
   */
  @Test
  public void testFormatErrorDifferentMessages() {
    assertEquals("Error: Event not found", view.formatError("Event not found"));
    assertEquals("Error: Invalid date format", view.formatError("Invalid date format"));
    assertEquals("Error: Duplicate event", view.formatError("Duplicate event"));
  }

  /**
   * Tests formatting duplicate error messages with proper context.
   */
  @Test
  public void testFormatDuplicateError() {
    String result = view.formatDuplicateError("Event with same subject, start time, and " +
            "end " + "time");
    assertEquals("Error: Event with same subject, start time, and end time already " +
            "exist(s).", result);
  }

  /**
   * Tests formatting duplicate errors for different contexts.
   */
  @Test
  public void testFormatDuplicateErrorDifferentContexts() {
    assertEquals("Error: One or more events in the series already exist(s).",
            view.formatDuplicateError("One or more events in the series"));
    assertEquals("Error: Event with same properties already exist(s).",
            view.formatDuplicateError("Event with same properties"));
  }

  /**
   * Tests the welcome message contains required elements.
   */
  @Test
  public void testGetWelcomeMessage() {
    String result = view.getWelcomeMessage();
    assertTrue("Should contain 'Calendar Application'", result.contains(
            "Calendar Application"));
    assertTrue("Should contain 'Interactive Mode'", result.contains(
            "Interactive Mode"));
    assertTrue("Should mention exit command", result.contains("exit"));
  }

  /**
   * Tests the goodbye message format.
   */
  @Test
  public void testGetGoodbyeMessage() {
    String result = view.getGoodbyeMessage();
    assertEquals("Goodbye!", result);
  }

  /**
   * Tests formatting events without location doesn't show location details.
   */
  @Test
  public void testFormatEventsOnDateEventsWithoutLocation() {
    List<Event> events = new ArrayList<>();
    Event eventWithoutLocation = new Event("Meeting", testDateTime, testDateTime.
            plusHours(1));
    events.add(eventWithoutLocation);

    String result = view.formatEventsOnDate(testDate, events);

    assertTrue("Should contain event subject", result.contains("Meeting"));
    assertTrue("Should contain time", result.contains("10:00"));
    assertFalse("Should not contain 'at' when no location", result.contains(" at "));
  }

  /**
   * Tests formatting events with empty location doesn't show location details.
   */
  @Test
  public void testFormatEventsOnDateEventsWithEmptyLocation() {
    List<Event> events = new ArrayList<>();
    Event eventWithEmptyLocation = new Event("Meeting", testDateTime, testDateTime.
            plusHours(1),
            null, "", EventStatus.PUBLIC);
    events.add(eventWithEmptyLocation);

    String result = view.formatEventsOnDate(testDate, events);

    assertFalse("Should not contain 'at' when location is empty", result.contains(" at "));
  }

  /**
   * Tests that date/time formatting in ranges uses proper format.
   */
  @Test
  public void testFormatEventsInRangeDateTimeFormatting() {
    List<Event> events = new ArrayList<>();
    events.add(new Event("Test", testDateTime, testDateTime.plusHours(1)));

    LocalDateTime start = LocalDateTime.of(2025, 6, 5, 9, 30);
    LocalDateTime end = LocalDateTime.of(2025, 6, 5, 17, 45);
    String result = view.formatEventsInRange(start, end, events);

    assertTrue("Should format start time correctly", result.contains("2025-06-05 09:30"));
    assertTrue("Should format end time correctly", result.contains("2025-06-05 17:45"));
  }

  /**
   * Tests integration with Event toString method for proper display.
   */
  @Test
  public void testEventToStringIntegration() {
    List<Event> events = new ArrayList<>();

    events.add(new Event("Team Meeting", testDateTime, testDateTime.plusHours(1),
            "Weekly sync", "Conference Room A", EventStatus.PUBLIC));

    events.add(new Event("Company Retreat", testDate, "Annual retreat",
            "Mountain Resort", EventStatus.PRIVATE));

    String result = view.formatEventsOnDate(testDate, events);

    assertTrue("Should contain bullet points", result.contains("•"));

    assertTrue("Should show timed event location", result.contains("at Conference Room A"));
    assertTrue("Should show all-day event location", result.contains("at Mountain Resort"));

    assertTrue("Should show time range for timed event", result.contains("10:00 - 11:00"));
    assertTrue("Should show 'All Day' for all-day event", result.contains("All Day"));
  }

  /**
   * Tests that formatted output doesn't have trailing whitespace.
   */
  @Test
  public void testNoTrailingWhitespace() {
    List<Event> events = new ArrayList<>();
    events.add(new Event("Test", testDateTime, testDateTime.plusHours(1)));

    String result = view.formatEventsOnDate(testDate, events);

    assertFalse("Should not end with newline", result.endsWith("\n"));
    assertFalse("Should not end with space", result.endsWith(" "));
  }

  /**
   * Tests that multiple events are formatted with consistent line breaks.
   */
  @Test
  public void testConsistentNewlines() {
    List<Event> events = new ArrayList<>();
    events.add(new Event("Event1", testDateTime, testDateTime.plusHours(1)));
    events.add(new Event("Event2", testDateTime.plusHours(2), testDateTime.plusHours(3)));

    String result = view.formatEventsOnDate(testDate, events);

    String[] lines = result.split("\n");
    assertTrue("Should have multiple lines", lines.length >= 3);

    for (String line : lines) {
      assertFalse("No line should be empty", line.trim().isEmpty());
    }
  }
}