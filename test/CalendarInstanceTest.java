import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.CalendarInstance;
import model.Event;
import model.EventStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for CalendarInstance functionality.
 */
class CalendarInstanceTest {

  private CalendarInstance calendar;
  private ZoneId timezone;

  /**
   * Set up test environment before each test.
   */
  @BeforeEach
  void setUp() {
    timezone = ZoneId.of("America/New_York");
    calendar = new CalendarInstance("TestCalendar", timezone);
  }

  /**
   * Test calendar instance creation with valid parameters.
   */
  @Test
  @DisplayName("Test calendar instance creation")
  void testCalendarInstanceCreation() {
    assertEquals("TestCalendar", calendar.getName());
    assertEquals(timezone, calendar.getTimezone());
    assertNotNull(calendar.getAllEvents());
    assertTrue(calendar.getAllEvents().isEmpty());
  }

  /**
   * Test setting and getting calendar name.
   */
  @Test
  @DisplayName("Test calendar name getter and setter")
  void testNameGetterSetter() {
    calendar.setName("NewName");
    assertEquals("NewName", calendar.getName());
  }

  /**
   * Test setting and getting calendar timezone.
   */
  @Test
  @DisplayName("Test calendar timezone getter and setter")
  void testTimezoneGetterSetter() {
    ZoneId newTimezone = ZoneId.of("Europe/London");
    calendar.setTimezone(newTimezone);
    assertEquals(newTimezone, calendar.getTimezone());
  }

  /**
   * Test creating a simple timed event successfully.
   */
  @Test
  @DisplayName("Test create timed event successfully")
  void testCreateTimedEventSuccess() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    boolean result = calendar.createEvent("Meeting", start, end,
            "Description", "Location", EventStatus.PUBLIC);
    assertTrue(result);
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Test creating a timed event with minimal parameters.
   */
  @Test
  @DisplayName("Test create timed event with minimal parameters")
  void testCreateTimedEventMinimal() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    boolean result = calendar.createEvent("Meeting", start, end);
    assertTrue(result);
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Test creating duplicate timed events fails.
   */
  @Test
  @DisplayName("Test create duplicate timed event fails")
  void testCreateDuplicateTimedEvent() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    calendar.createEvent("Meeting", start, end);
    boolean result = calendar.createEvent("Meeting", start, end);
    assertFalse(result);
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Test creating an all-day event successfully.
   */
  @Test
  @DisplayName("Test create all-day event successfully")
  void testCreateAllDayEventSuccess() {
    LocalDate date = LocalDate.of(2024, 9, 15);

    boolean result = calendar.createAllDayEvent("Holiday",
            date, "Description", "Location", EventStatus.PUBLIC);
    assertTrue(result);
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Test creating an all-day event with minimal parameters.
   */
  @Test
  @DisplayName("Test create all-day event with minimal parameters")
  void testCreateAllDayEventMinimal() {
    LocalDate date = LocalDate.of(2024, 9, 15);

    boolean result = calendar.createAllDayEvent("Holiday", date);
    assertTrue(result);
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Test creating duplicate all-day events fails.
   */
  @Test
  @DisplayName("Test create duplicate all-day event fails")
  void testCreateDuplicateAllDayEvent() {
    LocalDate date = LocalDate.of(2024, 9, 15);

    calendar.createAllDayEvent("Holiday", date);
    boolean result = calendar.createAllDayEvent("Holiday", date);
    assertFalse(result);
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Test creating an event series with specified occurrences.
   */
  @Test
  @DisplayName("Test create event series with occurrences")
  void testCreateEventSeriesWithOccurrences() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 16, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 16, 15, 30);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);

    boolean result = calendar.createEventSeries("Weekly Meeting", start, end, weekdays,
            3, "Description", "Location", EventStatus.PUBLIC, 1);
    assertTrue(result);
    assertEquals(3, calendar.getAllEvents().size());
  }

  /**
   * Test creating an event series until a specific date.
   */
  @Test
  @DisplayName("Test create event series until date")
  void testCreateEventSeriesUntilDate() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 16, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 16, 15, 30);
    LocalDate endDate = LocalDate.of(2024, 9, 30);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    boolean result = calendar.createEventSeriesUntil("Weekly Meeting", start, end, weekdays,
            endDate, "Description", "Location", EventStatus.PUBLIC, 1);
    assertTrue(result);
    assertTrue(calendar.getAllEvents().size() > 0);
  }

  /**
   * Test creating an all-day event series with occurrences.
   */
  @Test
  @DisplayName("Test create all-day event series with occurrences")
  void testCreateAllDayEventSeriesWithOccurrences() {
    LocalDate start = LocalDate.of(2024, 9, 16);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    boolean result = calendar.createAllDayEventSeries("Weekly Holiday", start, weekdays,
            3, "Description", "Location", EventStatus.PUBLIC, 1);
    assertTrue(result);
    assertEquals(3, calendar.getAllEvents().size());
  }

  /**
   * Test creating an all-day event series until a specific date.
   */
  @Test
  @DisplayName("Test create all-day event series until date")
  void testCreateAllDayEventSeriesUntilDate() {
    LocalDate start = LocalDate.of(2024, 9, 16);
    LocalDate endDate = LocalDate.of(2024, 9, 30);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    boolean result = calendar.createAllDayEventSeriesUntil("Weekly Holiday", start, weekdays,
            endDate, "Description", "Location", EventStatus.PUBLIC, 1);
    assertTrue(result);
    assertTrue(calendar.getAllEvents().size() > 0);
  }

  /**
   * Test event series creation fails when conflicts exist.
   */
  @Test
  @DisplayName("Test event series creation fails with conflicts")
  void testCreateEventSeriesWithConflicts() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 16, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 16, 15, 30);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    calendar.createEvent("Existing Meeting", start, end);

    boolean result = calendar.createEventSeries("Weekly Meeting", start, end, weekdays,
            3, "Description", "Location", EventStatus.PUBLIC, 1);
    assertTrue(result);
  }

  /**
   * Test finding an event by subject, start time, and end time.
   */
  @Test
  @DisplayName("Test find event by subject and times")
  void testFindEvent() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    calendar.createEvent("Meeting", start, end);
    Event found = calendar.findEvent("Meeting", start, end);

    assertNotNull(found);
    assertEquals("Meeting", found.getSubject());
    assertEquals(start, found.getStartDateTime());
    assertEquals(end, found.getEndDateTime());
  }

  /**
   * Test finding an event by subject and start time only.
   */
  @Test
  @DisplayName("Test find event by subject and start time")
  void testFindEventBySubjectAndStart() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    calendar.createEvent("Meeting", start, end);
    Event found = calendar.findEventBySubjectAndStart("Meeting", start);

    assertNotNull(found);
    assertEquals("Meeting", found.getSubject());
    assertEquals(start, found.getStartDateTime());
  }

  /**
   * Test finding nonexistent event returns null.
   */
  @Test
  @DisplayName("Test find nonexistent event returns null")
  void testFindNonexistentEvent() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    Event found = calendar.findEvent("Nonexistent", start, end);
    assertNull(found);
  }

  /**
   * Test getting events on a specific date.
   */
  @Test
  @DisplayName("Test get events on specific date")
  void testGetEventsOnDate() {
    LocalDate date = LocalDate.of(2024, 9, 15);
    LocalDateTime start1 = date.atTime(9, 0);
    LocalDateTime end1 = date.atTime(10, 0);
    LocalDateTime start2 = date.atTime(14, 0);
    LocalDateTime end2 = date.atTime(15, 0);

    calendar.createEvent("Morning Meeting", start1, end1);
    calendar.createEvent("Afternoon Meeting", start2, end2);
    calendar.createAllDayEvent("Holiday", date);

    List<Event> events = calendar.getEventsOnDate(date);
    assertEquals(3, events.size());
  }

  /**
   * Test getting events in a date/time range.
   */
  @Test
  @DisplayName("Test get events in date/time range")
  void testGetEventsInRange() {
    LocalDateTime start1 = LocalDateTime.of(2024, 9, 15, 9, 0);
    LocalDateTime end1 = LocalDateTime.of(2024, 9, 15, 10, 0);
    LocalDateTime start2 = LocalDateTime.of(2024, 9, 16, 14, 0);
    LocalDateTime end2 = LocalDateTime.of(2024, 9, 16, 15, 0);

    calendar.createEvent("Meeting 1", start1, end1);
    calendar.createEvent("Meeting 2", start2, end2);

    LocalDateTime rangeStart = LocalDateTime.of(2024, 9, 15, 0, 0);
    LocalDateTime rangeEnd = LocalDateTime.of(2024, 9, 17, 0, 0);

    List<Event> events = calendar.getEventsInRange(rangeStart, rangeEnd);
    assertEquals(2, events.size());
  }

  /**
   * Test checking if calendar is busy at specific time.
   */
  @Test
  @DisplayName("Test is busy at specific time")
  void testIsBusy() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    calendar.createEvent("Meeting", start, end);

    assertTrue(calendar.isBusy(LocalDateTime.of(2024, 9, 15, 15, 0)));
    assertFalse(calendar.isBusy(LocalDateTime.of(2024, 9, 15, 16, 0)));
  }

  /**
   * Test editing a specific event successfully.
   */
  @Test
  @DisplayName("Test edit specific event successfully")
  void testEditEventSuccess() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    calendar.createEvent("Meeting", start, end);
    boolean result = calendar.editEvent("subject", "Meeting", start, end, "Updated Meeting");

    assertTrue(result);
    Event event = calendar.findEvent("Updated Meeting", start, end);
    assertNotNull(event);
  }

  /**
   * Test editing nonexistent event fails.
   */
  @Test
  @DisplayName("Test edit nonexistent event fails")
  void testEditNonexistentEvent() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    boolean result = calendar.editEvent("subject", "Nonexistent", start, end, "Updated");
    assertFalse(result);
  }

  /**
   * Test editing events from a specific date in a series.
   */
  @Test
  @DisplayName("Test edit events from date in series")
  void testEditEventsFromDate() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 16, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 16, 15, 30);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    calendar.createEventSeries("Weekly Meeting", start, end, weekdays,
            3, "Description", "Location", EventStatus.PUBLIC, 1);

    LocalDateTime editFromDate = LocalDateTime.of(2024, 9, 23, 14, 30);
    boolean result = calendar.editEventsFromDate("subject",
            "Weekly Meeting", editFromDate, "Updated Meeting");

    assertTrue(result);
  }

  /**
   * Test editing an entire event series.
   */
  @Test
  @DisplayName("Test edit entire event series")
  void testEditEntireSeries() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 16, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 16, 15, 30);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    calendar.createEventSeries("Weekly Meeting", start, end, weekdays,
            3, "Description", "Location", EventStatus.PUBLIC, 1);

    boolean result = calendar.editEntireSeries("subject",
            "Weekly Meeting", start, "Updated Meeting");
    assertTrue(result);
  }

  /**
   * Test adding event directly to calendar.
   */
  @Test
  @DisplayName("Test add event directly to calendar")
  void testAddEvent() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);
    Event event = new Event("Meeting", start, end);

    boolean result = calendar.addEvent(event);
    assertTrue(result);
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Test adding duplicate event fails.
   */
  @Test
  @DisplayName("Test add duplicate event fails")
  void testAddDuplicateEvent() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);
    Event event1 = new Event("Meeting", start, end);
    Event event2 = new Event("Meeting", start, end);

    calendar.addEvent(event1);
    boolean result = calendar.addEvent(event2);

    assertFalse(result);
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Test calendar toString method.
   */
  @Test
  @DisplayName("Test calendar toString method")
  void testToString() {
    String result = calendar.toString();
    assertTrue(result.contains("TestCalendar"));
    assertTrue(result.contains("America/New_York"));
    assertTrue(result.contains("events=0"));
  }
}