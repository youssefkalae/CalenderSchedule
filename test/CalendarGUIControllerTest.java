import model.CalendarManager;
import model.EventStatus;
import model.Event;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Comprehensive test class for GUI Controller functionality.
 * Tests all the business logic and operations that the GUI controller coordinates
 * without directly instantiating problematic GUI components.
 */
public class CalendarGUIControllerTest {

  private CalendarManager calendarManager;

  @Before
  public void setUp() {
    calendarManager = new CalendarManager();

    calendarManager.createCalendar("PersonalCalendar", ZoneId.systemDefault());
    calendarManager.createCalendar("WorkCalendar", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("ProjectCalendar", ZoneId.of("Europe/London"));
    calendarManager.useCalendar("PersonalCalendar");
  }

  @After
  public void tearDown() {
    calendarManager = null;
  }

  /**
   * Tests basic calendar management operations that GUI controller coordinates.
   * Verifies calendar creation, switching, and basic state management.
   */
  @Test
  public void testBasicCalendarManagement() {
    boolean created = calendarManager.createCalendar("TestCalendar",
            ZoneId.systemDefault());
    assertTrue("Should create new calendar", created);

    boolean duplicate = calendarManager.createCalendar("TestCalendar",
            ZoneId.systemDefault());
    assertFalse("Should not create duplicate calendar", duplicate);

    boolean switched = calendarManager.useCalendar("WorkCalendar");
    assertTrue("Should switch to WorkCalendar", switched);
    assertEquals("Should be using WorkCalendar", "WorkCalendar",
            calendarManager.getCurrentCalendarName());

    Set<String> calendars = calendarManager.getCalendarNames();
    assertTrue("Should contain PersonalCalendar", calendars.contains("PersonalCalendar"));
    assertTrue("Should contain WorkCalendar", calendars.contains("WorkCalendar"));
    assertTrue("Should contain ProjectCalendar", calendars.contains("ProjectCalendar"));
    assertTrue("Should contain TestCalendar", calendars.contains("TestCalendar"));
  }

  /**
   * Tests event creation and management across multiple calendars.
   * Verifies the GUI controller's event coordination functionality.
   */
  @Test
  public void testEventManagementAcrossCalendars() {
    LocalDateTime now = LocalDateTime.now();

    calendarManager.useCalendar("PersonalCalendar");
    boolean personalEvent = calendarManager.createEvent("Personal Meeting",
            now.plusHours(1), now.plusHours(2), "Personal description",
            "Home", EventStatus.PRIVATE);
    assertTrue("Should create personal event", personalEvent);

    calendarManager.useCalendar("WorkCalendar");
    boolean workEvent = calendarManager.createEvent("Work Meeting",
            now.plusHours(3), now.plusHours(4), "Work description",
            "Office", EventStatus.PUBLIC);
    assertTrue("Should create work event", workEvent);

    calendarManager.useCalendar("PersonalCalendar");
    List<Event> personalEvents = calendarManager.getEventsOnDate(now.toLocalDate());
    assertEquals("Personal calendar should have 1 event", 1, personalEvents.size());
    assertEquals("Should be personal event", "Personal Meeting",
            personalEvents.get(0).getSubject());

    calendarManager.useCalendar("WorkCalendar");
    List<Event> workEvents = calendarManager.getEventsOnDate(now.toLocalDate());
    assertEquals("Work calendar should have 1 event", 1, workEvents.size());
    assertEquals("Should be work event", "Work Meeting",
            workEvents.get(0).getSubject());
  }

  /**
   * Tests event copying functionality between calendars.
   * Verifies single event copying with proper timezone handling.
   */
  @Test
  public void testSingleEventCopying() {
    LocalDateTime eventTime = LocalDateTime.now().plusDays(1);

    calendarManager.useCalendar("PersonalCalendar");
    boolean created = calendarManager.createEvent("Copyable Event",
            eventTime, eventTime.plusHours(1), "Original event",
            "Original location", EventStatus.PUBLIC);
    assertTrue("Should create original event", created);

    LocalDateTime newTime = eventTime.plusHours(2);
    boolean copied = calendarManager.copyEvent("Copyable Event", eventTime,
            "WorkCalendar", newTime);
    assertTrue("Should copy event successfully", copied);

    List<Event> originalEvents = calendarManager.getEventsOnDate(eventTime.toLocalDate());
    assertEquals("Original calendar should still have event", 1, originalEvents.size());

    calendarManager.useCalendar("WorkCalendar");
    List<Event> copiedEvents = calendarManager.getEventsOnDate(newTime.toLocalDate());
    assertEquals("Target calendar should have copied event", 1, copiedEvents.size());
    assertEquals("Copied event should have same subject", "Copyable Event",
            copiedEvents.get(0).getSubject());
  }

  /**
   * Tests bulk event copying operations.
   * Verifies copying multiple events on a single date.
   */
  @Test
  public void testBulkEventCopying() {
    LocalDate sourceDate = LocalDate.now().plusDays(2);
    LocalDateTime morning = sourceDate.atTime(9, 0);
    LocalDateTime afternoon = sourceDate.atTime(14, 0);
    LocalDateTime evening = sourceDate.atTime(18, 0);

    calendarManager.useCalendar("PersonalCalendar");
    calendarManager.createEvent("Morning Event", morning, morning.plusHours(1));
    calendarManager.createEvent("Afternoon Event", afternoon, afternoon.plusHours(1));
    calendarManager.createEvent("Evening Event", evening, evening.plusHours(1));

    LocalDate targetDate = sourceDate.plusDays(1);
    boolean copied = calendarManager.copyEventsOnDate(sourceDate, "WorkCalendar",
            targetDate);
    assertTrue("Should copy all events on date", copied);

    calendarManager.useCalendar("WorkCalendar");
    List<Event> copiedEvents = calendarManager.getEventsOnDate(targetDate);
    assertEquals("Should have copied all 3 events", 3, copiedEvents.size());

    boolean hasMorning = copiedEvents.stream().anyMatch(e ->
            e.getSubject().equals("Morning Event"));
    boolean hasAfternoon = copiedEvents.stream().anyMatch(e ->
            e.getSubject().equals("Afternoon Event"));
    boolean hasEvening = copiedEvents.stream().anyMatch(e ->
            e.getSubject().equals("Evening Event"));

    assertTrue("Should have copied Morning Event", hasMorning);
    assertTrue("Should have copied Afternoon Event", hasAfternoon);
    assertTrue("Should have copied Evening Event", hasEvening);
  }

  /**
   * Tests event range copying functionality.
   * Verifies copying events across multiple dates.
   */
  @Test
  public void testEventRangeCopying() {
    LocalDate startDate = LocalDate.now().plusDays(3);
    LocalDate endDate = startDate.plusDays(2);

    calendarManager.useCalendar("PersonalCalendar");
    calendarManager.createEvent("Day 1 Event", startDate.atTime(10, 0),
            startDate.atTime(11, 0));
    calendarManager.createEvent("Day 2 Event", startDate.plusDays(1).atTime(10, 0),
            startDate.plusDays(1).atTime(11, 0));
    calendarManager.createEvent("Day 3 Event", endDate.atTime(10, 0),
            endDate.atTime(11, 0));

    LocalDate targetStart = startDate.plusWeeks(1);
    boolean copied = calendarManager.copyEventsInRange(startDate, endDate,
            "ProjectCalendar", targetStart);
    assertTrue("Should copy events in range", copied);

    calendarManager.useCalendar("ProjectCalendar");

    List<Event> day1Events = calendarManager.getEventsOnDate(targetStart);
    assertEquals("Should have Day 1 event", 1, day1Events.size());
    assertEquals("Should be Day 1 Event", "Day 1 Event", day1Events.get(0).getSubject());

    List<Event> day2Events = calendarManager.getEventsOnDate(targetStart.plusDays(1));
    assertEquals("Should have Day 2 event", 1, day2Events.size());
    assertEquals("Should be Day 2 Event", "Day 2 Event", day2Events.get(0).getSubject());

    List<Event> day3Events = calendarManager.getEventsOnDate(targetStart.plusDays(2));
    assertEquals("Should have Day 3 event", 1, day3Events.size());
    assertEquals("Should be Day 3 Event", "Day 3 Event", day3Events.get(0).getSubject());
  }

  /**
   * Tests event editing operations across calendars.
   * Verifies event modification functionality.
   */
  @Test
  public void testEventEditingOperations() {
    LocalDateTime eventTime = LocalDateTime.now().plusDays(4);

    calendarManager.useCalendar("PersonalCalendar");
    calendarManager.createEvent("Original Event", eventTime, eventTime.plusHours(1),
            "Original description", "Original location", EventStatus.PRIVATE);

    boolean subjectEdited = calendarManager.editEvent("subject", "Original Event",
            eventTime, eventTime.plusHours(1), "Updated Event");
    assertTrue("Should edit event subject", subjectEdited);

    boolean locationEdited = calendarManager.editEvent("location", "Updated Event",
            eventTime, eventTime.plusHours(1), "Updated location");
    assertTrue("Should edit event location", locationEdited);

    boolean statusEdited = calendarManager.editEvent("status", "Updated Event",
            eventTime, eventTime.plusHours(1), "public");
    assertTrue("Should edit event status", statusEdited);

    List<Event> events = calendarManager.getEventsOnDate(eventTime.toLocalDate());
    assertEquals("Should have 1 event", 1, events.size());
    Event editedEvent = events.get(0);
    assertEquals("Subject should be updated", "Updated Event", editedEvent.getSubject());
    assertEquals("Location should be updated", "Updated location",
            editedEvent.getLocation());
    assertEquals("Status should be updated", EventStatus.PUBLIC, editedEvent.getStatus());
  }

  /**
   * Tests calendar state management and switching.
   * Verifies proper state isolation and consistency.
   */
  @Test
  public void testCalendarStateManagement() {
    assertEquals("Should start with PersonalCalendar", "PersonalCalendar",
            calendarManager.getCurrentCalendarName());

    LocalDateTime now = LocalDateTime.now();

    calendarManager.useCalendar("PersonalCalendar");
    calendarManager.createEvent("Personal Event 1", now, now.plusHours(1));
    calendarManager.createEvent("Personal Event 2", now.plusHours(2), now.plusHours(3));

    calendarManager.useCalendar("WorkCalendar");
    calendarManager.createEvent("Work Event 1", now, now.plusHours(1));

    calendarManager.useCalendar("ProjectCalendar");
    calendarManager.createEvent("Project Event 1", now, now.plusHours(1));
    calendarManager.createEvent("Project Event 2", now.plusHours(2), now.plusHours(3));
    calendarManager.createEvent("Project Event 3", now.plusHours(4), now.plusHours(5));

    calendarManager.useCalendar("PersonalCalendar");
    assertEquals("PersonalCalendar should have 2 events", 2,
            calendarManager.getEventsOnDate(now.toLocalDate()).size());

    calendarManager.useCalendar("WorkCalendar");
    assertEquals("WorkCalendar should have 1 event", 1,
            calendarManager.getEventsOnDate(now.toLocalDate()).size());

    calendarManager.useCalendar("ProjectCalendar");
    assertEquals("ProjectCalendar should have 3 events", 3,
            calendarManager.getEventsOnDate(now.toLocalDate()).size());
  }

  /**
   * Tests busy/free status checking across calendars.
   * Verifies schedule conflict detection.
   */
  @Test
  public void testScheduleStatusChecking() {
    LocalDateTime busyTime = LocalDateTime.now().plusDays(5).withHour(10).withMinute(0);
    LocalDateTime freeTime = busyTime.plusHours(5);

    calendarManager.useCalendar("PersonalCalendar");
    calendarManager.createEvent("Busy Event", busyTime, busyTime.plusHours(1));

    assertTrue("Should be busy during event time",
            calendarManager.isBusy(busyTime.plusMinutes(30)));

    assertTrue("Should be busy at exact start time",
            calendarManager.isBusy(busyTime));

    assertFalse("Should be free at exact end time",
            calendarManager.isBusy(busyTime.plusHours(1)));

    assertFalse("Should be free at different time",
            calendarManager.isBusy(freeTime));

    assertFalse("Should be free before event",
            calendarManager.isBusy(busyTime.minusMinutes(1)));
  }

  /**
   * Tests error handling and edge cases.
   * Verifies robust operation under various conditions.
   */
  @Test
  public void testErrorHandlingAndEdgeCases() {
    boolean invalidSwitch = calendarManager.useCalendar("NonExistentCalendar");
    assertFalse("Should not switch to non-existent calendar", invalidSwitch);

    LocalDateTime now = LocalDateTime.now();
    calendarManager.useCalendar("PersonalCalendar");
    calendarManager.createEvent("Test Event", now, now.plusHours(1));

    boolean invalidCopy = calendarManager.copyEvent("Test Event", now,
            "NonExistentCalendar", now.plusDays(1));
    assertFalse("Should not copy to non-existent calendar", invalidCopy);

    boolean invalidEventCopy = calendarManager.copyEvent("Non-existent Event", now,
            "WorkCalendar", now.plusDays(1));
    assertFalse("Should not copy non-existent event", invalidEventCopy);

    boolean invalidEdit = calendarManager.editEvent("subject", "Non-existent Event",
            now, now.plusHours(1), "New Subject");
    assertFalse("Should not edit non-existent event", invalidEdit);

    CalendarManager emptyManager = new CalendarManager();
    List<Event> emptyEvents = emptyManager.getEventsOnDate(now.toLocalDate());
    assertTrue("Should return empty list for no current calendar", emptyEvents.isEmpty());
  }

  /**
   * Tests complex multi-step operations.
   * Verifies coordination of multiple operations in sequence.
   */
  @Test
  public void testComplexMultiStepOperations() {
    LocalDateTime baseTime = LocalDateTime.now().plusWeeks(1);

    calendarManager.useCalendar("PersonalCalendar");
    calendarManager.createEvent("Morning Workout", baseTime.withHour(7),
            baseTime.withHour(8));
    calendarManager.createEvent("Team Meeting", baseTime.withHour(9),
            baseTime.withHour(10));
    calendarManager.createEvent("Lunch Break", baseTime.withHour(12),
            baseTime.withHour(13));
    calendarManager.createEvent("Project Review", baseTime.withHour(15),
            baseTime.withHour(16));

    calendarManager.copyEvent("Team Meeting", baseTime.withHour(9),
            "WorkCalendar", baseTime.plusDays(1).withHour(9));
    calendarManager.copyEvent("Project Review", baseTime.withHour(15),
            "WorkCalendar", baseTime.plusDays(1).withHour(15));

    calendarManager.editEvent("location", "Team Meeting",
            baseTime.withHour(9), baseTime.withHour(10), "Conference Room A");

    calendarManager.useCalendar("WorkCalendar");
    calendarManager.editEvent("subject", "Team Meeting",
            baseTime.plusDays(1).withHour(9), baseTime.plusDays(1).withHour(10),
            "Work Team Meeting");

    calendarManager.useCalendar("PersonalCalendar");
    calendarManager.createEvent("Evening Event", baseTime.withHour(19),
            baseTime.withHour(20));

    calendarManager.copyEventsOnDate(baseTime.toLocalDate(), "ProjectCalendar",
            baseTime.plusWeeks(1).toLocalDate());

    calendarManager.useCalendar("PersonalCalendar");
    List<Event> personalEvents = calendarManager.getEventsOnDate(baseTime.toLocalDate());
    assertEquals("PersonalCalendar should have 5 events", 5, personalEvents.size());

    calendarManager.useCalendar("WorkCalendar");
    List<Event> workEvents = calendarManager.getEventsOnDate(baseTime.plusDays(1)
            .toLocalDate());
    assertEquals("WorkCalendar should have 2 events", 2, workEvents.size());

    calendarManager.useCalendar("ProjectCalendar");
    List<Event> projectEvents = calendarManager.getEventsOnDate(baseTime.plusWeeks(1)
            .toLocalDate());
    assertEquals("ProjectCalendar should have 5 copied events", 5, projectEvents.size());

    Event modifiedTeamMeeting = personalEvents.stream()
            .filter(e -> e.getSubject().equals("Team Meeting"))
            .findFirst().orElse(null);
    assertNotNull("Should find Team Meeting", modifiedTeamMeeting);
    assertEquals("Should have updated location", "Conference Room A",
            modifiedTeamMeeting.getLocation());
  }
}