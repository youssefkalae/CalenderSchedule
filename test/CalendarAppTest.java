import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import controller.CalendarController;


/**
 * Test suite for the CalendarApp class.
 * Tests the core functionality through integration with CalendarController.
 * Focuses on testing the controller delegation and command processing logic.
 */
public class CalendarAppTest {

  private CalendarController controller;

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  /**
   * Tests that the controller properly initializes all MVC components.
   */
  @Test
  public void testControllerInitialization() {
    assertNotNull("Controller should be initialized", controller);
    assertNotNull("Calendar should be initialized", controller.getCalendar());
    assertNotNull("View should be initialized", controller.getView());
  }

  /**
   * Tests creating a single timed event with start and end times.
   */
  @Test
  public void testCreateEventCommand() {
    String command = "create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00";
    String result = controller.processCommand(command);

    assertEquals("Event created successfully.", result);
    assertEquals("Calendar should have 1 event", 1,
            controller.getCalendar().getAllEvents().size());
  }

  /**
   * Tests creating an all-day event on a specific date.
   */
  @Test
  public void testCreateAllDayEventCommand() {
    String command = "create event Holiday on 2025-06-05";
    String result = controller.processCommand(command);

    assertEquals("All-day event created successfully.", result);
    assertEquals("Calendar should have 1 event", 1,
            controller.getCalendar().getAllEvents().size());
  }

  /**
   * Tests creating a recurring event series with specific weekdays and occurrence count.
   */
  @Test
  public void testCreateEventSeriesCommand() {
    String command = "create event Meeting from 2025-06-05T10:00 "
            + "to 2025-06-05T11:00 repeats MW for 3 times";
    String result = controller.processCommand(command);

    assertEquals("Event series created successfully.", result);
    assertEquals("Calendar should have 3 events", 3,
            controller.getCalendar().getAllEvents().size());
  }

  /**
   * Tests printing all events occurring on a specific date.
   */
  @Test
  public void testPrintEventsOnDateCommand() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "print events on 2025-06-05";
    String result = controller.processCommand(command);

    assertTrue("Should contain event details", result.contains("Meeting"));
    assertTrue("Should contain date", result.contains("2025-06-05"));
  }

  /**
   * Tests printing events within a specific date/time range.
   */
  @Test
  public void testPrintEventsInRangeCommand() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "print events from 2025-06-05T09:00 to 2025-06-05T12:00";
    String result = controller.processCommand(command);

    assertTrue("Should contain event details", result.contains("Meeting"));
  }

  /**
   * Tests showing busy status when there is an active event at the specified time.
   */
  @Test
  public void testShowStatusBusyCommand() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "show status 2025-06-05T10:30";
    String result = controller.processCommand(command);

    assertEquals("busy", result);
  }

  /**
   * Tests showing available status when there are no events at the specified time.
   */
  @Test
  public void testShowStatusAvailableCommand() {
    String command = "show status 2025-06-05T14:00";
    String result = controller.processCommand(command);

    assertEquals("available", result);
  }

  /**
   * Tests editing the location property of a single event.
   */
  @Test
  public void testEditEventCommand() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "edit event location Meeting from 2025-06-05T10:00 to "
            + "2025-06-05T11:00 with Room101";
    String result = controller.processCommand(command);

    assertEquals("Event(s) edited successfully.", result);
  }

  /**
   * Tests editing the subject property of a single event.
   */
  @Test
  public void testEditSingleEventSubject() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "edit event subject Meeting from 2025-06-05T10:00 "
            + "to 2025-06-05T11:00 with TeamMeeting";
    String result = controller.processCommand(command);

    assertEquals("Event(s) edited successfully.", result);
  }

  /**
   * Tests editing the location property of a single event with a different value.
   */
  @Test
  public void testEditSingleEventLocation() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "edit event location Meeting from 2025-06-05T10:00 "
            + "to 2025-06-05T11:00 with Conference";
    String result = controller.processCommand(command);

    assertEquals("Event(s) edited successfully.", result);
  }

  /**
   * Tests error handling for invalid command syntax.
   */
  @Test
  public void testInvalidCommand() {
    String command = "invalid command";
    String result = controller.processCommand(command);

    assertTrue("Should contain error message", result.contains("Error: Invalid command"));
  }

  /**
   * Tests error handling for empty command input.
   */
  @Test
  public void testEmptyCommand() {
    String command = "";
    String result = controller.processCommand(command);

    assertTrue("Should contain error message", result.contains("Error: Empty command"));
  }

  /**
   * Tests the exit command returns the expected termination signal.
   */
  @Test
  public void testExitCommand() {
    String command = "exit";
    String result = controller.processCommand(command);

    assertEquals("EXIT", result);
  }

  /**
   * Tests error handling when attempting to create duplicate events.
   */
  @Test
  public void testDuplicateEventCreation() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00";
    String result = controller.processCommand(command);

    assertTrue("Should contain duplicate error",
            result.contains("Error: Event with same subject, start time, and end time already"));
  }

  /**
   * Tests printing events when no events exist on the specified date.
   */
  @Test
  public void testPrintEventsNoEventsFound() {
    String command = "print events on 2025-06-05";
    String result = controller.processCommand(command);

    assertTrue("Should indicate no events", result.contains("No events on 2025-06-05"));
  }

  /**
   * Tests printing multiple events that occur on the same date.
   */
  @Test
  public void testMultipleEventsOnSameDay() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");
    controller.processCommand("create event Lunch from 2025-06-05T12:00 to 2025-06-05T13:00");

    String command = "print events on 2025-06-05";
    String result = controller.processCommand(command);

    assertTrue("Should contain first event", result.contains("Meeting"));
    assertTrue("Should contain second event", result.contains("Lunch"));
  }

  /**
   * Tests creating an event with a quoted subject containing spaces.
   */
  @Test
  public void testQuotedEventSubject() {
    String command = "create event \"Team Meeting\" from 2025-06-05T10:00 to 2025-06-05T11:00";
    String result = controller.processCommand(command);

    assertEquals("Event created successfully.", result);
    assertEquals("Calendar should have 1 event", 1,
            controller.getCalendar().getAllEvents().size());
  }
}