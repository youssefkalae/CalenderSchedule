import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;

import model.CalendarManager;
import model.Event;
import view.CalendarGUI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for CalendarGUI component.
 */
public class CalendarGUITest {

  /**
   * The main GUI component being tested.
   */
  private CalendarGUI gui;

  /**
   * Calendar manager instance used for testing.
   */
  private CalendarManager calendarManager;

  /**
   * Test frame reference for cleanup operations.
   */
  private JFrame testFrame;

  /**
   * Sets up the test environment before each test method.
   */
  @BeforeEach
  public void setUp() {
    SwingUtilities.invokeLater(() -> {
      calendarManager = new CalendarManager();

      calendarManager.createCalendar("TestCalendar", ZoneId.systemDefault());
      calendarManager.useCalendar("TestCalendar");

      gui = new CalendarGUI(calendarManager);
      testFrame = gui;
    });

    try {
      SwingUtilities.invokeAndWait(() -> {
      });
    } catch (Exception e) {
      fail("Failed to initialize GUI: " + e.getMessage());
    }
  }

  /**
   * Cleans up resources after each test method.
   */
  @AfterEach
  public void tearDown() {
    SwingUtilities.invokeLater(() -> {
      if (gui != null) {
        gui.dispose();
      }
    });
  }

  /**
   * Tests the basic initialization of the CalendarGUI component.
   */
  @Test
  public void testGUIInitialization() {
    assertNotNull(gui, "GUI should be initialized");
    assertEquals("Calendar Application - GUI Mode", gui.getTitle(),
            "GUI title should be correct");
    assertTrue(gui.getSize().width > 0, "GUI width should be positive");
    assertTrue(gui.getSize().height > 0, "GUI height should be positive");
  }

  /**
   * Tests the automatic creation of a default calendar when none exists.
   */
  @Test
  public void testDefaultCalendarCreation() {
    CalendarManager emptyManager = new CalendarManager();
    CalendarGUI testGui = new CalendarGUI(emptyManager);

    assertFalse(emptyManager.getCalendarNames().isEmpty(),
            "GUI should create a default calendar when none exists");
    assertTrue(emptyManager.getCalendarNames().contains("Default"),
            "Default calendar should be named 'Default'");

    testGui.dispose();
  }

  /**
   * Tests the schedule view update functionality with multiple events.
   */
  @Test
  public void testScheduleViewUpdate() {
    LocalDateTime now = LocalDateTime.now();
    calendarManager.createEvent("Test Event 1", now.plusHours(1), now.plusHours(2));
    calendarManager.createEvent("Test Event 2", now.plusDays(1),
            now.plusDays(1).plusHours(1));
    calendarManager.createAllDayEvent("All Day Event", now.toLocalDate().plusDays(2));

    gui.updateScheduleView();

    try {
      Field scheduleDisplayField = CalendarGUI.class.getDeclaredField("scheduleDisplay");
      scheduleDisplayField.setAccessible(true);
      JTextArea scheduleDisplay = (JTextArea) scheduleDisplayField.get(gui);

      String scheduleText = scheduleDisplay.getText();
      assertNotNull(scheduleText, "Schedule text should not be null");
      assertTrue(scheduleText.contains("Test Event 1"),
              "Schedule should contain Test Event 1");
      assertTrue(scheduleText.contains("Test Event 2"),
              "Schedule should contain Test Event 2");
      assertTrue(scheduleText.contains("All Day Event"),
              "Schedule should contain All Day Event");
      assertTrue(scheduleText.contains("TestCalendar"),
              "Schedule should show current calendar name");

    } catch (Exception e) {
      fail("Failed to access schedule display: " + e.getMessage());
    }
  }

  /**
   * Tests the display of empty schedule when no events exist.
   */
  @Test
  public void testEmptyScheduleDisplay() {
    gui.updateScheduleView();

    try {
      Field scheduleDisplayField = CalendarGUI.class.getDeclaredField("scheduleDisplay");
      scheduleDisplayField.setAccessible(true);
      JTextArea scheduleDisplay = (JTextArea) scheduleDisplayField.get(gui);

      String scheduleText = scheduleDisplay.getText();
      assertTrue(scheduleText.contains("No events scheduled"),
              "Empty schedule should show 'No events scheduled' message");

    } catch (Exception e) {
      fail("Failed to access schedule display: " + e.getMessage());
    }
  }

  /**
   * Tests the calendar dropdown update when new calendars are added.
   */
  @Test
  public void testCalendarDropdownUpdate() {
    calendarManager.createCalendar("WorkCalendar", ZoneId.systemDefault());

    try {
      Field dropdownField = CalendarGUI.class.getDeclaredField("calendarDropdown");
      dropdownField.setAccessible(true);
      @SuppressWarnings("unchecked")
      JComboBox<String> dropdown = (JComboBox<String>) dropdownField.get(gui);

      Method updateMethod = CalendarGUI.class.getDeclaredMethod("updateCalendarDropdown");
      updateMethod.setAccessible(true);
      updateMethod.invoke(gui);

      assertEquals(2, dropdown.getItemCount(), "Dropdown should contain 2 calendars");
      assertTrue(containsItem(dropdown, "TestCalendar"),
              "Dropdown should contain TestCalendar");
      assertTrue(containsItem(dropdown, "WorkCalendar"),
              "Dropdown should contain WorkCalendar");

    } catch (Exception e) {
      fail("Failed to test calendar dropdown: " + e.getMessage());
    }
  }

  /**
   * Tests the date navigation functionality for moving forward and backward.
   */
  @Test
  public void testDateNavigation() {
    try {
      Field currentStartDateField = CalendarGUI.class.getDeclaredField("currentStartDate");
      currentStartDateField.setAccessible(true);
      LocalDate originalDate = (LocalDate) currentStartDateField.get(gui);

      Method navigateMethod = CalendarGUI.class.getDeclaredMethod("navigateDate", int.class);
      navigateMethod.setAccessible(true);

      navigateMethod.invoke(gui, 7);
      LocalDate newDate = (LocalDate) currentStartDateField.get(gui);

      assertEquals(originalDate.plusDays(7), newDate,
              "Date should advance by 7 days when navigating forward");

      navigateMethod.invoke(gui, -14);
      LocalDate backDate = (LocalDate) currentStartDateField.get(gui);

      assertEquals(originalDate.minusDays(7), backDate,
              "Date should go back 7 days from original when navigating backward");

    } catch (Exception e) {
      fail("Failed to test date navigation: " + e.getMessage());
    }
  }

  /**
   * Tests the date navigation functionality with proper date verification.
   */
  @Test
  public void testDateNavigationWithMultipleSteps() {
    try {
      Field currentStartDateField = CalendarGUI.class.getDeclaredField("currentStartDate");
      currentStartDateField.setAccessible(true);
      LocalDate originalDate = (LocalDate) currentStartDateField.get(gui);

      Method navigateMethod = CalendarGUI.class.getDeclaredMethod("navigateDate", int.class);
      navigateMethod.setAccessible(true);

      navigateMethod.invoke(gui, 7);
      LocalDate afterOneWeek = (LocalDate) currentStartDateField.get(gui);
      assertEquals(originalDate.plusDays(7), afterOneWeek,
              "Date should advance by 7 days");

      navigateMethod.invoke(gui, 14);
      LocalDate afterThreeWeeks = (LocalDate) currentStartDateField.get(gui);
      assertEquals(originalDate.plusDays(21), afterThreeWeeks,
              "Date should advance by 21 days total");

      navigateMethod.invoke(gui, -28);
      LocalDate backOneWeek = (LocalDate) currentStartDateField.get(gui);
      assertEquals(originalDate.minusDays(7), backOneWeek,
              "Date should be 7 days before original");

    } catch (Exception e) {
      fail("Failed to test date navigation: " + e.getMessage());
    }
  }

  /**
   * Tests the "go to today" functionality.
   */
  @Test
  public void testGoToTodayFunctionality() {
    try {
      Field currentStartDateField = CalendarGUI.class.getDeclaredField("currentStartDate");
      currentStartDateField.setAccessible(true);

      Method navigateMethod = CalendarGUI.class.getDeclaredMethod("navigateDate", int.class);
      navigateMethod.setAccessible(true);

      Method goToTodayMethod = CalendarGUI.class.getDeclaredMethod("goToToday");
      goToTodayMethod.setAccessible(true);

      navigateMethod.invoke(gui, 30);
      LocalDate afterNavigation = (LocalDate) currentStartDateField.get(gui);
      assertNotEquals(LocalDate.now(), afterNavigation,
              "Date should be different from today after navigation");

      goToTodayMethod.invoke(gui);
      LocalDate afterGoToToday = (LocalDate) currentStartDateField.get(gui);
      assertEquals(LocalDate.now(), afterGoToToday,
              "Date should be today after calling goToToday");

    } catch (Exception e) {
      fail("Failed to test go to today functionality: " + e.getMessage());
    }
  }

  /**
   * Tests the calendar switching functionality with multiple calendars.
   */
  @Test
  public void testCalendarSwitchingFunctionality() {
    calendarManager.createCalendar("Work", ZoneId.systemDefault());
    calendarManager.createCalendar("Personal", ZoneId.systemDefault());

    try {
      Field dropdownField = CalendarGUI.class.getDeclaredField("calendarDropdown");
      dropdownField.setAccessible(true);
      @SuppressWarnings("unchecked")
      JComboBox<String> dropdown = (JComboBox<String>) dropdownField.get(gui);

      Method updateMethod = CalendarGUI.class.getDeclaredMethod("updateCalendarDropdown");
      updateMethod.setAccessible(true);
      updateMethod.invoke(gui);

      assertEquals(3, dropdown.getItemCount(),
              "Dropdown should contain 3 calendars");

      Method switchMethod = CalendarGUI.class.getDeclaredMethod("switchCalendar");
      switchMethod.setAccessible(true);

      dropdown.setSelectedItem("Work");
      switchMethod.invoke(gui);
      assertEquals("Work", calendarManager.getCurrentCalendarName(),
              "Current calendar should be Work");

      dropdown.setSelectedItem("Personal");
      switchMethod.invoke(gui);
      assertEquals("Personal", calendarManager.getCurrentCalendarName(),
              "Current calendar should be Personal");

    } catch (Exception e) {
      fail("Failed to test calendar switching: " + e.getMessage());
    }
  }

  /**
   * Tests the retrieval of upcoming events within a specified date range.
   */
  @Test
  public void testUpcomingEventsRetrieval() {
    LocalDate today = LocalDate.now();
    calendarManager.createEvent("Today Event",
            today.atTime(10, 0), today.atTime(11, 0));
    calendarManager.createEvent("Tomorrow Event",
            today.plusDays(1).atTime(14, 0), today.plusDays(1).atTime(15, 0));
    calendarManager.createEvent("Next Week Event",
            today.plusDays(7).atTime(9, 0), today.plusDays(7).atTime(10, 0));

    try {
      Method getUpcomingEventsMethod = CalendarGUI.class.getDeclaredMethod(
              "getUpcomingEvents", LocalDate.class, int.class);
      getUpcomingEventsMethod.setAccessible(true);

      @SuppressWarnings("unchecked")
      List<Event> upcomingEvents = (List<Event>) getUpcomingEventsMethod.invoke(
              gui, today, 5);

      assertEquals(3, upcomingEvents.size(), "Should retrieve all 3 events");

      assertTrue(upcomingEvents.get(0).getStartDateTime().isBefore(
                      upcomingEvents.get(1).getStartDateTime()),
              "Events should be ordered by start time");

    } catch (Exception e) {
      fail("Failed to test upcoming events retrieval: " + e.getMessage());
    }
  }

  /**
   * Tests the handling of scenarios when no calendar is initially selected.
   */
  @Test
  public void testNoCalendarSelectedScenario() {
    CalendarManager emptyManager = new CalendarManager();

    CalendarGUI testGui = new CalendarGUI(emptyManager);
    testGui.updateScheduleView();

    try {
      Field scheduleDisplayField = CalendarGUI.class.getDeclaredField("scheduleDisplay");
      scheduleDisplayField.setAccessible(true);
      JTextArea scheduleDisplay = (JTextArea) scheduleDisplayField.get(testGui);

      String scheduleText = scheduleDisplay.getText();
      assertTrue(scheduleText.contains("Default") || scheduleText.contains("No events"),
              "Should handle no calendar scenario gracefully");

    } catch (Exception e) {
      fail("Failed to test no calendar scenario: " + e.getMessage());
    }

    testGui.dispose();
  }

  /**
   * Test to check if a JComboBox contains a specific item.
   */
  private boolean containsItem(JComboBox<String> comboBox, String item) {
    for (int i = 0; i < comboBox.getItemCount(); i++) {
      if (item.equals(comboBox.getItemAt(i))) {
        return true;
      }
    }
    return false;
  }
}