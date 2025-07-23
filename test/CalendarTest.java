import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Calendar;
import model.Event;
import model.EventStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive test suite for the Calendar class.
 * Tests all event creation, editing, and query functionality.
 */
public class CalendarTest {

  private Calendar calendar;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private LocalDate testDate;

  @Before
  public void setUp() {
    calendar = new Calendar();
    startTime = LocalDateTime.of(2025, 6, 5, 10, 0);
    endTime = LocalDateTime.of(2025, 6, 5, 11, 0);
    testDate = LocalDate.of(2025, 6, 5);
  }

  /**
   * Tests successful creation of a basic timed event.
   */
  @Test
  public void testCreateEvent_Success() {
    assertTrue(calendar.createEvent("Meeting", startTime, endTime));
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Tests that duplicate events are rejected.
   */
  @Test
  public void testCreateEventDuplicate() {
    assertTrue(calendar.createEvent("Meeting", startTime, endTime));
    assertFalse(calendar.createEvent("Meeting", startTime, endTime));
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Tests creating an event with all optional properties.
   */
  @Test
  public void testCreateEvent_FullConstructor() {
    assertTrue(calendar.createEvent("Meeting", startTime, endTime,
            "Important meeting", "Room 101", EventStatus.PRIVATE));
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Tests successful creation of an all-day event.
   */
  @Test
  public void testCreateAllDayEventSuccess() {
    assertTrue(calendar.createAllDayEvent("Holiday", testDate));
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Tests that duplicate all-day events are rejected.
   */
  @Test
  public void testCreateAllDayEvent_Duplicate() {
    assertTrue(calendar.createAllDayEvent("Holiday", testDate));
    assertFalse(calendar.createAllDayEvent("Holiday", testDate));
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Tests creating an all-day event with all optional properties.
   */
  @Test
  public void testCreateAllDayEventFullConstructor() {
    assertTrue(calendar.createAllDayEvent("Holiday", testDate,
            "National holiday", "Nationwide", EventStatus.PUBLIC));
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Tests creating an event series with zero occurrences.
   */
  @Test
  public void testCreateEventSeriesZeroOccurrences() {
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.THURSDAY);
    assertTrue(calendar.createEventSeries("Test", startTime, endTime, weekdays, 0));
    assertEquals(0, calendar.getAllEvents().size());
  }

  /**
   * Tests creating an event series with multiple occurrences.
   */
  @Test
  public void testCreateEventSeries_MultipleOccurrences() {
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);

    LocalDateTime mondayStart = LocalDateTime.of(2025, 6, 2, 10, 0);
    LocalDateTime mondayEnd = LocalDateTime.of(2025, 6, 2, 11, 0);

    assertTrue(calendar.createEventSeries("Meeting", mondayStart, mondayEnd, weekdays, 4));
    assertEquals(4, calendar.getAllEvents().size());
  }

  /**
   * Tests creating an event series until a specific end date.
   */
  @Test
  public void testCreateEventSeriesUntil() {
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    LocalDateTime mondayStart = LocalDateTime.of(2025, 6, 2, 10, 0);
    LocalDateTime mondayEnd = LocalDateTime.of(2025, 6, 2, 11, 0);
    LocalDate endDate = LocalDate.of(2025, 6, 16);

    assertTrue(calendar.createEventSeriesUntil("Weekly Meeting", mondayStart, mondayEnd,
            weekdays, endDate));
    assertEquals(3, calendar.getAllEvents().size());
  }

  /**
   * Tests creating an all-day event series.
   */
  @Test
  public void testCreateAllDayEventSeries() {
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.FRIDAY);

    LocalDate fridayDate = LocalDate.of(2025, 6, 6);

    assertTrue(calendar.createAllDayEventSeries("Weekend Prep", fridayDate, weekdays, 3));
    assertEquals(3, calendar.getAllEvents().size());
  }

  /**
   * Tests finding an event by exact criteria.
   */
  @Test
  public void testFindEvent_Success() {
    calendar.createEvent("Meeting", startTime, endTime);

    Event found = calendar.findEvent("Meeting", startTime, endTime);
    assertNotNull(found);
    assertEquals("Meeting", found.getSubject());
  }

  /**
   * Tests finding an event when no match exists.
   */
  @Test
  public void testFindEvent_NotFound() {
    calendar.createEvent("Meeting", startTime, endTime);

    Event found = calendar.findEvent("Different Meeting", startTime, endTime);
    assertNull(found);
  }

  /**
   * Tests finding an event by subject and start time.
   */
  @Test
  public void testFindEventBySubjectAndStart() {
    calendar.createEvent("Meeting", startTime, endTime);

    Event found = calendar.findEventBySubjectAndStart("Meeting", startTime);
    assertNotNull(found);
    assertEquals("Meeting", found.getSubject());
  }

  /**
   * Tests editing a single event's subject.
   */
  @Test
  public void testEditEvent_Subject() {
    calendar.createEvent("Meeting", startTime, endTime);

    assertTrue(calendar.editEvent("subject", "Meeting", startTime, endTime, "Team Meeting"));

    Event found = calendar.findEvent("Team Meeting", startTime, endTime);
    assertNotNull(found);
  }

  /**
   * Tests editing a single event's location.
   */
  @Test
  public void testEditEvent_Location() {
    calendar.createEvent("Meeting", startTime, endTime);

    assertTrue(calendar.editEvent("location", "Meeting", startTime, endTime, "Room 101"));

    Event found = calendar.findEvent("Meeting", startTime, endTime);
    assertNotNull(found);
    assertEquals("Room 101", found.getLocation());
  }

  /**
   * Tests editing an event that doesn't exist.
   */
  @Test
  public void testEditEvent_NotFound() {
    assertFalse(calendar.editEvent("subject", "NonExistent", startTime, endTime, "New Subject"));
  }

  /**
   * Tests editing events from a specific date in a series.
   */
  @Test
  public void testEditEventsFromDate() {
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    LocalDateTime mondayStart = LocalDateTime.of(2025, 6, 2, 10, 0);
    LocalDateTime mondayEnd = LocalDateTime.of(2025, 6, 2, 11, 0);

    calendar.createEventSeries("Meeting", mondayStart, mondayEnd, weekdays, 3);

    assertTrue(calendar.editEventsFromDate("subject", "Meeting", mondayStart, "Team Meeting"));
  }

  /**
   * Tests editing an entire event series.
   */
  @Test
  public void testEditEntireSeries() {
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    LocalDateTime mondayStart = LocalDateTime.of(2025, 6, 2, 10, 0);
    LocalDateTime mondayEnd = LocalDateTime.of(2025, 6, 2, 11, 0);

    calendar.createEventSeries("Meeting", mondayStart, mondayEnd, weekdays, 3);

    assertTrue(calendar.editEntireSeries("location", "Meeting", mondayStart, "Conference Room"));
  }

  /**
   * Tests getting events on a specific date.
   */
  @Test
  public void testGetEventsOnDate_WithEvents() {
    calendar.createEvent("Meeting", startTime, endTime);
    calendar.createAllDayEvent("Holiday", testDate);

    List<Event> events = calendar.getEventsOnDate(testDate);
    assertEquals(2, events.size());
  }

  /**
   * Tests getting events on a date with no events.
   */
  @Test
  public void testGetEventsOnDate_NoEvents() {
    List<Event> events = calendar.getEventsOnDate(testDate);
    assertEquals(0, events.size());
  }

  /**
   * Tests getting events in a specific date/time range.
   */
  @Test
  public void testGetEventsInRange() {
    calendar.createEvent("Meeting", startTime, endTime);

    LocalDateTime rangeStart = startTime.minusHours(1);
    LocalDateTime rangeEnd = endTime.plusHours(1);

    List<Event> events = calendar.getEventsInRange(rangeStart, rangeEnd);
    assertEquals(1, events.size());
  }

  /**
   * Tests getting events in a range with no overlapping events.
   */
  @Test
  public void testGetEventsInRange_NoEvents() {
    calendar.createEvent("Meeting", startTime, endTime);

    LocalDateTime rangeStart = endTime.plusHours(1);
    LocalDateTime rangeEnd = endTime.plusHours(2);

    List<Event> events = calendar.getEventsInRange(rangeStart, rangeEnd);
    assertEquals(0, events.size());
  }

  /**
   * Tests checking busy status when an event is active.
   */
  @Test
  public void testIsBusy_True() {
    calendar.createEvent("Meeting", startTime, endTime);

    LocalDateTime duringMeeting = startTime.plusMinutes(30);
    assertTrue(calendar.isBusy(duringMeeting));
  }

  /**
   * Tests checking busy status when no events are active.
   */
  @Test
  public void testIsBusy_False() {
    calendar.createEvent("Meeting", startTime, endTime);

    LocalDateTime afterMeeting = endTime.plusMinutes(30);
    assertFalse(calendar.isBusy(afterMeeting));
  }

  /**
   * Tests getting all events returns a defensive copy.
   */
  @Test
  public void testGetAllEvents() {
    calendar.createEvent("Meeting", startTime, endTime);
    calendar.createAllDayEvent("Holiday", testDate);

    Set<Event> allEvents = calendar.getAllEvents();
    assertEquals(2, allEvents.size());

    allEvents.clear();
    assertEquals(2, calendar.getAllEvents().size());
  }

  /**
   * Tests creating conflicting events in a series.
   */
  @Test
  public void testCreateEventSeries_WithConflict() {
    calendar.createEvent("Meeting", startTime, endTime);

    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.THURSDAY);

    assertFalse(calendar.createEventSeries("Meeting", startTime, endTime, weekdays, 2));
    assertEquals(1, calendar.getAllEvents().size());
  }

  /**
   * Tests creating an all-day event series until a specific date.
   */
  @Test
  public void testCreateAllDayEventSeriesUntil() {
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.FRIDAY);

    LocalDate fridayDate = LocalDate.of(2025, 6, 6);
    LocalDate endDate = LocalDate.of(2025, 6, 20);

    assertTrue(calendar.createAllDayEventSeriesUntil("Weekend Prep", fridayDate,
            weekdays, endDate));
    assertEquals(3, calendar.getAllEvents().size());
  }
}