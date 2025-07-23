import org.junit.Before;
import org.junit.Test;

import controller.CalendarController;
import model.ICalendar;
import view.ICalendarView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test suite for the CalendarController class.
 * Tests MVC integration and proper delegation between components.
 */
public class CalendarControllerTest {

  private CalendarController controller;

  @Before
  public void setUp() {
    controller = new CalendarController();
  }

  /**
   * Tests that the controller constructor properly initializes all MVC components.
   */
  @Test
  public void testConstructor_InitializesComponents() {
    assertNotNull("Calendar should be initialized", controller.getCalendar());
    assertNotNull("View should be initialized", controller.getView());
  }

  /**
   * Tests that getCalendar returns a valid ICalendar instance.
   */
  @Test
  public void testGetCalendar_ReturnsCalendarInstance() {
    ICalendar calendar = controller.getCalendar();
    assertNotNull(calendar);
    assertTrue("Should return ICalendar instance", calendar instanceof ICalendar);
  }

  /**
   * Tests that getView returns a valid ICalendarView instance.
   */
  @Test
  public void testGetView_ReturnsViewInstance() {
    ICalendarView view = controller.getView();
    assertNotNull(view);
    assertTrue("Should return ICalendarView instance", view instanceof ICalendarView);
  }

  /**
   * Tests processing a create event command with start and end times.
   */
  @Test
  public void testProcessCommand_CreateEvent_Success() {
    String command = "create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00";
    String result = controller.processCommand(command);

    assertEquals("Event created successfully.", result);
    assertEquals("Calendar should have 1 event", 1, controller.getCalendar()
            .getAllEvents().size());
  }

  /**
   * Tests processing a create all-day event command.
   */
  @Test
  public void testProcessCommandCreateAllDayEventSuccess() {
    String command = "create event Holiday on 2025-06-05";
    String result = controller.processCommand(command);

    assertEquals("All-day event created successfully.", result);
    assertEquals("Calendar should have 1 event", 1, controller.getCalendar().
            getAllEvents().size());
  }

  /**
   * Tests processing an edit event command to modify event properties.
   */
  @Test
  public void testProcessCommandEditEventSuccess() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "edit event location Meeting from 2025-06-05T10:00 to 2025-06-05T11:00 with " +
            "Room101";
    String result = controller.processCommand(command);

    assertEquals("Event(s) edited successfully.", result);
  }

  /**
   * Tests processing a print events command for a specific date.
   */
  @Test
  public void testProcessCommand_PrintEvents_Success() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "print events on 2025-06-05";
    String result = controller.processCommand(command);

    assertTrue("Should contain event details", result.contains("Meeting"));
    assertTrue("Should contain date", result.contains("2025-06-05"));
  }

  /**
   * Tests processing a print events command for a date/time range.
   */
  @Test
  public void testProcessCommand_PrintEventsRange_Success() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "print events from 2025-06-05T09:00 to 2025-06-05T12:00";
    String result = controller.processCommand(command);

    assertTrue("Should contain event details", result.contains("Meeting"));
  }

  /**
   * Tests processing a show status command to check calendar availability.
   */
  @Test
  public void testProcessCommand_ShowStatus_Success() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "show status 2025-06-05T10:30";
    String result = controller.processCommand(command);

    assertEquals("busy", result);
  }

  /**
   * Tests error handling for invalid command syntax.
   */
  @Test
  public void testProcessCommand_InvalidCommand_ReturnsError() {
    String command = "invalid command";
    String result = controller.processCommand(command);

    assertTrue("Should contain error message", result.contains("Error"));
  }

  /**
   * Tests error handling for empty command input.
   */
  @Test
  public void testProcessCommand_EmptyCommand_ReturnsError() {
    String command = "";
    String result = controller.processCommand(command);

    assertTrue("Should contain error message", result.contains("Error"));
  }

  /**
   * Tests processing the exit command returns the expected termination signal.
   */
  @Test
  public void testProcessCommand_Exit_ReturnsExitSignal() {
    String command = "exit";
    String result = controller.processCommand(command);

    assertEquals("EXIT", result);
  }

  /**
   * Tests processing a create event series command with recurring events.
   */
  @Test
  public void testProcessCommand_CreateEventSeries_Success() {
    String command = "create event Meeting from 2025-06-05T10:00 "
            + "to 2025-06-05T11:00 repeats MW for 3 times";
    String result = controller.processCommand(command);

    assertEquals("Event series created successfully.", result);
    assertEquals("Calendar should have 3 events", 3,
            controller.getCalendar().getAllEvents().size());
  }

  /**
   * Tests error handling when attempting to create duplicate events.
   */
  @Test
  public void testProcessCommand_DuplicateEvent_ReturnsError() {
    controller.processCommand("create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00");

    String command = "create event Meeting from 2025-06-05T10:00 to 2025-06-05T11:00";
    String result = controller.processCommand(command);

    assertTrue("Should contain duplicate error", result.contains("Error"));
  }
}