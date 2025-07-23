import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.time.ZoneId;
import java.util.Scanner;

import controller.CalendarCommandHandler;
import model.CalendarManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Working test class for CalendarCommandHandler functionality.
 * These tests should all pass with the fixed CalendarCommandHandler implementation.
 */
class CalendarCommandHandlerTest {

  private CalendarCommandHandler commandHandler;
  private CalendarManager calendarManager;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  /**
   * Set up test environment before each test.
   */
  @BeforeEach
  void setUp() {
    calendarManager = new CalendarManager();
    commandHandler = new CalendarCommandHandler(calendarManager);
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  /**
   * Clean up test environment after each test.
   */
  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  /**
   * Test successful calendar creation with valid name and timezone.
   */
  @Test
  @DisplayName("Test create calendar command with valid parameters")
  void testCreateCalendarSuccess() {
    boolean result = commandHandler.processCommand(
            "create calendar --name work --timezone America/New_York");
    assertTrue(result, "Calendar creation should succeed");
    assertTrue(calendarManager.calendarExists("work"), "Calendar should exist after creation");

    String output = outputStream.toString();
    assertTrue(output.contains("Calendar 'work' created successfully"),
            "Should show success message");
  }

  /**
   * Test calendar creation failure with invalid timezone.
   */
  @Test
  @DisplayName("Test create calendar command with invalid timezone")
  void testCreateCalendarInvalidTimezone() {
    boolean result = commandHandler.processCommand(
            "create calendar --name work --timezone Invalid/Timezone");
    assertFalse(result, "Calendar creation should fail with invalid timezone");
    assertFalse(calendarManager.calendarExists("work"),
            "Calendar should not exist after failed creation");

    String output = outputStream.toString();
    assertTrue(output.contains("Invalid timezone"), "Should show invalid timezone error");
  }

  /**
   * Test calendar creation failure with duplicate name.
   */
  @Test
  @DisplayName("Test create calendar command with duplicate name")
  void testCreateCalendarDuplicateName() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "create calendar --name work --timezone Europe/London");
    assertFalse(result, "Duplicate calendar creation should fail");

    String output = outputStream.toString();
    assertTrue(output.contains("already exists"), "Should show duplicate error message");
  }

  /**
   * Test calendar creation with invalid command syntax.
   */
  @Test
  @DisplayName("Test create calendar command with invalid syntax")
  void testCreateCalendarInvalidSyntax() {
    boolean result = commandHandler.processCommand("create calendar work America/New_York");
    assertFalse(result, "Invalid syntax should fail");

    String output = outputStream.toString();
    assertTrue(output.contains("Invalid syntax"), "Should show syntax error");
  }

  /**
   * Test successful calendar editing to change timezone.
   */
  @Test
  @DisplayName("Test edit calendar timezone successfully")
  void testEditCalendarTimezone() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "edit calendar --name work --property timezone Europe/London");
    assertTrue(result, "Timezone edit should succeed");
    assertEquals(ZoneId.of("Europe/London"), calendarManager.getCalendar("work").getTimezone(),
            "Timezone should be updated");

    String output = outputStream.toString();
    assertTrue(output.contains("updated successfully"), "Should show success message");
  }

  /**
   * Test successful calendar name editing.
   */
  @Test
  @DisplayName("Test edit calendar name successfully")
  void testEditCalendarName() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "edit calendar --name work --property name personal");
    assertTrue(result, "Name edit should succeed");
    assertTrue(calendarManager.calendarExists("personal"), "New name should exist");
    assertFalse(calendarManager.calendarExists("work"), "Old name should not exist");

    String output = outputStream.toString();
    assertTrue(output.contains("updated successfully"), "Should show success message");
  }

  /**
   * Test calendar editing failure with nonexistent calendar.
   */
  @Test
  @DisplayName("Test edit nonexistent calendar")
  void testEditNonexistentCalendar() {
    boolean result = commandHandler.processCommand(
            "edit calendar --name nonexistent --property timezone Europe/London");
    assertFalse(result, "Edit of nonexistent calendar should fail");

    String output = outputStream.toString();
    assertTrue(output.contains("does not exist"), "Should show calendar not found error");
  }

  /**
   * Test calendar editing failure with invalid property.
   */
  @Test
  @DisplayName("Test edit calendar with invalid property")
  void testEditCalendarInvalidProperty() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "edit calendar --name work --property invalidprop newvalue");
    assertFalse(result, "Invalid property edit should fail");

    String output = outputStream.toString();
    assertTrue(output.contains("Invalid property"), "Should show invalid property error");
  }

  /**
   * Test successful calendar selection with use command.
   */
  @Test
  @DisplayName("Test use calendar command successfully")
  void testUseCalendarSuccess() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");

    outputStream.reset();

    boolean result = commandHandler.processCommand("use calendar --name work");
    assertTrue(result, "Use calendar should succeed");
    assertEquals("work", calendarManager.getCurrentCalendarName(),
            "Current calendar should be set");

    String output = outputStream.toString();
    assertTrue(output.contains("Now using calendar: work"), "Should show success message");
  }

  /**
   * Test calendar selection failure with nonexistent calendar.
   */
  @Test
  @DisplayName("Test use nonexistent calendar")
  void testUseNonexistentCalendar() {
    boolean result = commandHandler.processCommand("use calendar --name nonexistent");
    assertFalse(result, "Use nonexistent calendar should fail");
    assertNull(calendarManager.getCurrentCalendarName(), "No calendar should be current");

    String output = outputStream.toString();
    assertTrue(output.contains("does not exist"), "Should show calendar not found error");
  }

  /**
   * Test list calendars command with no calendars.
   */
  @Test
  @DisplayName("Test list calendars when none exist")
  void testListCalendarsEmpty() {
    boolean result = commandHandler.processCommand("list calendars");
    assertTrue(result, "List calendars should always succeed");

    String output = outputStream.toString();
    assertTrue(output.contains("No calendars exist"), "Should show no calendars message");
  }

  /**
   * Test list calendars command with existing calendars.
   */
  @Test
  @DisplayName("Test list calendars with existing calendars")
  void testListCalendarsWithCalendars() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");
    commandHandler.processCommand("create calendar --name personal --timezone Europe/London");

    outputStream.reset();

    boolean result = commandHandler.processCommand("list calendars");
    assertTrue(result, "List calendars should succeed");

    String output = outputStream.toString();
    assertTrue(output.contains("Available calendars:"), "Should show header");
    assertTrue(output.contains("work"), "Should contain work calendar");
    assertTrue(output.contains("personal"), "Should contain personal calendar");
  }

  /**
   * Test current calendar command with no active calendar.
   */
  @Test
  @DisplayName("Test current calendar command with no active calendar")
  void testCurrentCalendarNoneActive() {
    boolean result = commandHandler.processCommand("current calendar");
    assertTrue(result, "Current calendar command should always succeed");

    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"),
            "Should show no current calendar message");
  }

  /**
   * Test current calendar command with active calendar.
   */
  @Test
  @DisplayName("Test current calendar command with active calendar")
  void testCurrentCalendarWithActive() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");
    commandHandler.processCommand("use calendar --name work");

    outputStream.reset();

    boolean result = commandHandler.processCommand("current calendar");
    assertTrue(result, "Current calendar command should succeed");

    String output = outputStream.toString();
    assertTrue(output.contains("Current calendar: work"), "Should show current calendar name");
  }

  /**
   * Test copy event command without active calendar.
   */
  @Test
  @DisplayName("Test copy event command without active calendar")
  void testCopyEventNoActiveCalendar() {
    commandHandler.processCommand("create calendar --name personal --timezone America/New_York");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "copy event Meeting on 2024-09-15T14:30 --target personal to 2024-09-16T14:30");
    assertFalse(result, "Copy event should fail without active calendar");

    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"),
            "Should show no active calendar error");
  }

  /**
   * Test copy events on date command without active calendar.
   */
  @Test
  @DisplayName("Test copy events on date command without active calendar")
  void testCopyEventsOnDateNoActiveCalendar() {
    commandHandler.processCommand("create calendar --name personal --timezone America/New_York");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "copy events on 2024-09-15 --target personal to 2024-09-16");
    assertFalse(result, "Copy events should fail without active calendar");

    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"),
            "Should show no active calendar error");
  }

  /**
   * Test copy events in range command without active calendar.
   */
  @Test
  @DisplayName("Test copy events in range command without active calendar")
  void testCopyEventsInRangeNoActiveCalendar() {
    commandHandler.processCommand("create calendar --name personal --timezone America/New_York");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "copy events between 2024-09-15 and 2024-09-20 --target personal to 2025-01-15");
    assertFalse(result, "Copy events in range should fail without active calendar");

    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"),
            "Should show no active calendar error");
  }

  /**
   * Test print events command without active calendar.
   */
  @Test
  @DisplayName("Test print events command without active calendar")
  void testPrintEventsNoActiveCalendar() {
    boolean result = commandHandler.processCommand("print events on 2024-09-15");
    assertFalse(result, "Print events should fail without active calendar");

    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"),
            "Should show no active calendar error");
  }

  /**
   * Test print events command with active calendar.
   */
  @Test
  @DisplayName("Test print events command with active calendar")
  void testPrintEventsWithActiveCalendar() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");
    commandHandler.processCommand("use calendar --name work");

    outputStream.reset();

    boolean result = commandHandler.processCommand("print events on 2024-09-15");
    assertTrue(result, "Print events should succeed with active calendar");

    String output = outputStream.toString();
    assertTrue(output.contains("No events on 2024-09-15"),
            "Should show no events message for empty calendar");
  }

  /**
   * Test show status command without active calendar.
   */
  @Test
  @DisplayName("Test show status command without active calendar")
  void testShowStatusNoActiveCalendar() {
    boolean result = commandHandler.processCommand("show status 2024-09-15T14:30");
    assertFalse(result, "Show status should fail without active calendar");

    String output = outputStream.toString();
    assertTrue(output.contains("No calendar is currently in use"),
            "Should show no active calendar error");
  }

  /**
   * Test show status command with active calendar.
   */
  @Test
  @DisplayName("Test show status command with active calendar showing available")
  void testShowStatusAvailable() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");
    commandHandler.processCommand("use calendar --name work");

    outputStream.reset();

    boolean result = commandHandler.processCommand("show status 2024-09-15T14:30");
    assertTrue(result, "Show status should succeed with active calendar");

    String output = outputStream.toString();
    assertTrue(output.contains("available"),
            "Should show available status for empty calendar");
  }

  /**
   * Test invalid command handling.
   */
  @Test
  @DisplayName("Test invalid command handling")
  void testInvalidCommand() {
    boolean result = commandHandler.processCommand("invalid command here");
    assertFalse(result, "Invalid command should return false");

    String output = outputStream.toString();
    assertTrue(output.contains("Unknown command"), "Should show unknown command error");
  }

  /**
   * Test empty command handling.
   */
  @Test
  @DisplayName("Test empty command handling")
  void testEmptyCommand() {
    boolean result = commandHandler.processCommand("");
    assertTrue(result, "Empty command should return true");
  }

  /**
   * Test whitespace command handling.
   */
  @Test
  @DisplayName("Test whitespace command handling")
  void testWhitespaceCommand() {
    boolean result = commandHandler.processCommand("   ");
    assertTrue(result, "Whitespace command should return true");
  }

  /**
   * Test help command execution.
   */
  @Test
  @DisplayName("Test help command execution")
  void testHelpCommand() {
    commandHandler.showHelp();
    String output = outputStream.toString();
    assertTrue(output.contains("Multi-Calendar Commands"), "Should show help header");
    assertTrue(output.contains("create calendar"), "Should show create calendar command");
    assertTrue(output.contains("Date format:"), "Should show date format info");
  }

  /**
   * Test constructor with scanner parameter.
   */
  @Test
  @DisplayName("Test constructor with scanner parameter")
  void testConstructorWithScanner() {
    Scanner testScanner = new Scanner(new StringReader("test input"));
    CalendarCommandHandler handler = new CalendarCommandHandler(calendarManager, testScanner);

    assertNotNull(handler.getCalendarManager(), "Calendar manager should not be null");
    assertNotNull(handler.getScanner(), "Scanner should not be null");
    assertEquals(calendarManager, handler.getCalendarManager(),
            "Should use provided calendar manager");
    assertEquals(testScanner, handler.getScanner(), "Should use provided scanner");
  }

  /**
   * Test getter and setter methods.
   */
  @Test
  @DisplayName("Test getter and setter methods")
  void testGettersAndSetters() {
    CalendarManager newManager = new CalendarManager();
    Scanner newScanner = new Scanner(new StringReader("test"));

    commandHandler.setCalendarManager(newManager);
    commandHandler.setScanner(newScanner);

    assertEquals(newManager, commandHandler.getCalendarManager(),
            "Should return set calendar manager");
    assertEquals(newScanner, commandHandler.getScanner(), "Should return set scanner");
  }

  /**
   * Test copy event with nonexistent target calendar.
   */
  @Test
  @DisplayName("Test copy event to nonexistent target calendar")
  void testCopyEventNonexistentTarget() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");
    commandHandler.processCommand("use calendar --name work");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "copy event Meeting on 2024-09-15T14:30 --target nonexistent to 2024-09-16T14:30");
    assertFalse(result, "Copy event should fail with nonexistent target");

    String output = outputStream.toString();
    assertTrue(output.contains("Target calendar 'nonexistent' does not exist"),
            "Should show target not found error");
  }

  /**
   * Test copy event with invalid date format.
   */
  @Test
  @DisplayName("Test copy event with invalid date format")
  void testCopyEventInvalidDateFormat() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");
    commandHandler.processCommand("create calendar --name personal --timezone America/New_York");
    commandHandler.processCommand("use calendar --name work");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "copy event Meeting on bad-date --target personal to 2024-09-16T14:30");
    assertFalse(result, "Copy event should fail with invalid date format");

    String output = outputStream.toString();
    assertTrue(output.contains("Invalid date and time format"),
            "Should show date format error");
  }

  /**
   * Test successful copy events with proper setup.
   */
  @Test
  @DisplayName("Test copy events with proper calendar setup")
  void testCopyEventsSuccess() {
    commandHandler.processCommand("create calendar --name work --timezone America/New_York");
    commandHandler.processCommand("create calendar --name personal --timezone America/New_York");
    commandHandler.processCommand("use calendar --name work");

    outputStream.reset();

    boolean result = commandHandler.processCommand(
            "copy events on 2024-09-15 --target personal to 2024-09-16");
    assertTrue(result, "Copy events should succeed with proper setup");

    String output = outputStream.toString();
    assertTrue(output.contains("copied successfully"), "Should show success message");
  }
}