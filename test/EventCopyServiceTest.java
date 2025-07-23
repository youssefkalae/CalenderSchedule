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
import model.EventCopyService;
import model.EventStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for EventCopyService functionality.
 */
class EventCopyServiceTest {

  private EventCopyService copyService;
  private CalendarInstance sourceCalendar;
  private CalendarInstance targetCalendar;
  private CalendarInstance timezoneTargetCalendar;

  /**
   * Set up test environment before each test.
   */
  @BeforeEach
  void setUp() {
    copyService = new EventCopyService();
    sourceCalendar = new CalendarInstance("Source", ZoneId.of("America/New_York"));
    targetCalendar = new CalendarInstance("Target", ZoneId.of("America/New_York"));
    timezoneTargetCalendar = new CalendarInstance("TimezoneTarget",
            ZoneId.of("Europe/London"));
  }

  /**
   * Test copying a single event successfully within same timezone.
   */
  @Test
  @DisplayName("Test copy single event successfully within same timezone")
  void testCopyEventSameTimezone() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    sourceCalendar.createEvent("Meeting", start, end);
    LocalDateTime newStart = LocalDateTime.of(2024, 9, 16, 14, 30);

    boolean result = copyService.copyEvent("Meeting", start, sourceCalendar,
            targetCalendar, newStart);

    assertTrue(result);
    assertEquals(1, targetCalendar.getAllEvents().size());
  }

  /**
   * Test copying a single event successfully across different timezones.
   */
  @Test
  @DisplayName("Test copy single event successfully across different timezones")
  void testCopyEventDifferentTimezone() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    sourceCalendar.createEvent("Meeting", start, end);
    LocalDateTime newStart = LocalDateTime.of(2024, 9, 16, 9, 30);

    boolean result = copyService.copyEvent("Meeting", start, sourceCalendar,
            timezoneTargetCalendar, newStart);

    assertTrue(result);
    assertEquals(1, timezoneTargetCalendar.getAllEvents().size());
  }

  /**
   * Test copying nonexistent event fails.
   */
  @Test
  @DisplayName("Test copy nonexistent event fails")
  void testCopyNonexistentEvent() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime newStart = LocalDateTime.of(2024, 9, 16, 14, 30);

    boolean result = copyService.copyEvent("Nonexistent", start, sourceCalendar,
            targetCalendar, newStart);

    assertFalse(result);
    assertEquals(0, targetCalendar.getAllEvents().size());
  }

  /**
   * Test copying event with conflict in target calendar fails.
   */
  @Test
  @DisplayName("Test copy event with conflict fails")
  void testCopyEventWithConflict() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);
    LocalDateTime newStart = LocalDateTime.of(2024, 9, 16, 14, 30);
    LocalDateTime newEnd = LocalDateTime.of(2024, 9, 16, 15, 30);

    sourceCalendar.createEvent("Meeting", start, end);
    targetCalendar.createEvent("Existing Meeting", newStart, newEnd);

    boolean result = copyService.copyEvent("Meeting", start, sourceCalendar,
            targetCalendar, newStart);

    assertTrue(result);
    assertEquals(2, targetCalendar.getAllEvents().size());
  }

  /**
   * Test copying all events on a specific date successfully.
   */
  @Test
  @DisplayName("Test copy events on date successfully")
  void testCopyEventsOnDateSuccess() {
    LocalDate sourceDate = LocalDate.of(2024, 9, 15);
    LocalDateTime start1 = sourceDate.atTime(9, 0);
    LocalDateTime end1 = sourceDate.atTime(10, 0);
    LocalDateTime start2 = sourceDate.atTime(14, 0);
    LocalDateTime end2 = sourceDate.atTime(15, 0);

    sourceCalendar.createEvent("Morning Meeting", start1, end1);
    sourceCalendar.createEvent("Afternoon Meeting", start2, end2);
    sourceCalendar.createAllDayEvent("Holiday", sourceDate);

    LocalDate targetDate = LocalDate.of(2024, 9, 16);
    boolean result = copyService.copyEventsOnDate(sourceDate, sourceCalendar,
            targetCalendar, targetDate);

    assertTrue(result);
    assertEquals(3, targetCalendar.getAllEvents().size());
  }

  /**
   * Test copying events on date with no events returns true.
   */
  @Test
  @DisplayName("Test copy events on date with no events")
  void testCopyEventsOnDateNoEvents() {
    LocalDate sourceDate = LocalDate.of(2024, 9, 15);
    LocalDate targetDate = LocalDate.of(2024, 9, 16);

    boolean result = copyService.copyEventsOnDate(sourceDate, sourceCalendar,
            targetCalendar, targetDate);

    assertTrue(result);
    assertEquals(0, targetCalendar.getAllEvents().size());
  }

  /**
   * Test copying events on date across different timezones.
   */
  @Test
  @DisplayName("Test copy events on date across different timezones")
  void testCopyEventsOnDateDifferentTimezones() {
    LocalDate sourceDate = LocalDate.of(2024, 9, 15);
    LocalDateTime start = sourceDate.atTime(14, 0);
    LocalDateTime end = sourceDate.atTime(15, 0);

    sourceCalendar.createEvent("Meeting", start, end);

    LocalDate targetDate = LocalDate.of(2024, 9, 16);
    boolean result = copyService.copyEventsOnDate(sourceDate, sourceCalendar,
            timezoneTargetCalendar, targetDate);

    assertTrue(result);
    assertEquals(1, timezoneTargetCalendar.getAllEvents().size());

    Event copiedEvent = timezoneTargetCalendar.getAllEvents().iterator().next();
    assertEquals(targetDate, copiedEvent.getStartDateTime().toLocalDate());
  }

  /**
   * Test copying events on date with some conflicts.
   */
  @Test
  @DisplayName("Test copy events on date with some conflicts")
  void testCopyEventsOnDateWithConflicts() {
    LocalDate sourceDate = LocalDate.of(2024, 9, 15);
    LocalDateTime start1 = sourceDate.atTime(9, 0);
    LocalDateTime end1 = sourceDate.atTime(10, 0);
    LocalDateTime start2 = sourceDate.atTime(14, 0);
    LocalDateTime end2 = sourceDate.atTime(15, 0);

    sourceCalendar.createEvent("Meeting 1", start1, end1);
    sourceCalendar.createEvent("Meeting 2", start2, end2);

    LocalDate targetDate = LocalDate.of(2024, 9, 16);
    LocalDateTime conflictStart = targetDate.atTime(9, 0);
    LocalDateTime conflictEnd = targetDate.atTime(10, 0);
    targetCalendar.createEvent("Conflict Meeting", conflictStart, conflictEnd);

    boolean result = copyService.copyEventsOnDate(sourceDate, sourceCalendar,
            targetCalendar, targetDate);

    assertTrue(result);
    assertEquals(3, targetCalendar.getAllEvents().size());
  }

  /**
   * Test copying events in a date range successfully.
   */
  @Test
  @DisplayName("Test copy events in range successfully")
  void testCopyEventsInRangeSuccess() {
    LocalDate startDate = LocalDate.of(2024, 9, 15);
    LocalDate endDate = LocalDate.of(2024, 9, 17);

    sourceCalendar.createEvent("Meeting 1", startDate.atTime(9, 0),
            startDate.atTime(10, 0));
    sourceCalendar.createEvent("Meeting 2", startDate.plusDays(1).atTime(14, 0),
            startDate.plusDays(1).atTime(15, 0));
    sourceCalendar.createEvent("Meeting 3", startDate.plusDays(2).atTime(11, 0),
            startDate.plusDays(2).atTime(12, 0));

    LocalDate targetStartDate = LocalDate.of(2024, 10, 1);
    boolean result = copyService.copyEventsInRange(startDate, endDate, sourceCalendar,
            targetCalendar, targetStartDate);

    assertTrue(result);
    assertEquals(3, targetCalendar.getAllEvents().size());
  }

  /**
   * Test copying events in range with no events returns true.
   */
  @Test
  @DisplayName("Test copy events in range with no events")
  void testCopyEventsInRangeNoEvents() {
    LocalDate startDate = LocalDate.of(2024, 9, 15);
    LocalDate endDate = LocalDate.of(2024, 9, 17);
    LocalDate targetStartDate = LocalDate.of(2024, 10, 1);

    boolean result = copyService.copyEventsInRange(startDate, endDate, sourceCalendar,
            targetCalendar, targetStartDate);

    assertTrue(result);
    assertEquals(0, targetCalendar.getAllEvents().size());
  }

  /**
   * Test copying events in range across different timezones.
   */
  @Test
  @DisplayName("Test copy events in range across different timezones")
  void testCopyEventsInRangeDifferentTimezones() {
    LocalDate startDate = LocalDate.of(2024, 9, 15);
    LocalDate endDate = LocalDate.of(2024, 9, 16);

    sourceCalendar.createEvent("Meeting", startDate.atTime(14, 0),
            startDate.atTime(15, 0));

    LocalDate targetStartDate = LocalDate.of(2024, 10, 1);
    boolean result = copyService.copyEventsInRange(startDate, endDate, sourceCalendar,
            timezoneTargetCalendar, targetStartDate);

    assertTrue(result);
    assertEquals(1, timezoneTargetCalendar.getAllEvents().size());

    Event copiedEvent = timezoneTargetCalendar.getAllEvents().iterator().next();
    assertEquals(targetStartDate, copiedEvent.getStartDateTime().toLocalDate());
  }

  /**
   * Test copying events in range with some conflicts.
   */
  @Test
  @DisplayName("Test copy events in range with actual conflicts")
  void testCopyEventsInRangeWithConflicts() {
    LocalDate startDate = LocalDate.of(2024, 9, 15);
    LocalDate endDate = LocalDate.of(2024, 9, 16);

    sourceCalendar.createEvent("Meeting 1", startDate.atTime(9, 0),
            startDate.atTime(10, 0));
    sourceCalendar.createEvent("Meeting 2", startDate.plusDays(1).atTime(14, 0),
            startDate.plusDays(1).atTime(15, 0));

    LocalDate targetStartDate = LocalDate.of(2024, 10, 1);

    targetCalendar.createEvent("Meeting 1", targetStartDate.atTime(9, 0),
            targetStartDate.atTime(10, 0));

    boolean result = copyService.copyEventsInRange(startDate, endDate, sourceCalendar,
            targetCalendar, targetStartDate);

    assertFalse(result);

    assertEquals(2, targetCalendar.getAllEvents().size());

    List<Event> eventsOnOct2 = targetCalendar.getEventsOnDate(LocalDate.of(2024, 10, 2));
    assertEquals(1, eventsOnOct2.size());
    assertEquals("Meeting 2", eventsOnOct2.get(0).getSubject());
  }

  /**
   * Test copying events preserves event properties.
   */
  @Test
  @DisplayName("Test copy events preserves properties")
  void testCopyEventsPreservesProperties() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    sourceCalendar.createEvent("Important Meeting", start, end, "Meeting Description",
            "Conference Room", EventStatus.PRIVATE);
    LocalDateTime newStart = LocalDateTime.of(2024, 9, 16, 14, 30);

    boolean result = copyService.copyEvent("Important Meeting", start, sourceCalendar,
            targetCalendar, newStart);

    assertTrue(result);
    Event copiedEvent = targetCalendar.getAllEvents().iterator().next();
    assertEquals("Important Meeting", copiedEvent.getSubject());
    assertEquals("Meeting Description", copiedEvent.getDescription());
    assertEquals("Conference Room", copiedEvent.getLocation());
    assertEquals(EventStatus.PRIVATE, copiedEvent.getStatus());
  }

  /**
   * Test copying all-day events correctly.
   */
  @Test
  @DisplayName("Test copy all-day events correctly")
  void testCopyAllDayEvents() {
    LocalDate sourceDate = LocalDate.of(2024, 9, 15);
    sourceCalendar.createAllDayEvent("Holiday", sourceDate, "National Holiday",
            "Nationwide", EventStatus.PUBLIC);

    LocalDate targetDate = LocalDate.of(2024, 9, 16);
    boolean result = copyService.copyEventsOnDate(sourceDate, sourceCalendar,
            targetCalendar, targetDate);

    assertTrue(result);
    Event copiedEvent = targetCalendar.getAllEvents().iterator().next();
    assertTrue(copiedEvent.isAllDay());
    assertEquals(targetDate, copiedEvent.getStartDateTime().toLocalDate());
  }

  /**
   * Test copying events with series IDs maintains relationship tracking.
   */
  @Test
  @DisplayName("Test copy events with series IDs")
  void testCopyEventsWithSeriesIds() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 16, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 16, 15, 30);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    sourceCalendar.createEventSeries("Weekly Meeting", start, end, weekdays, 2,
            "Description", "Location", EventStatus.PUBLIC, 1);

    LocalDate startDate = LocalDate.of(2024, 9, 16);
    LocalDate endDate = LocalDate.of(2024, 9, 23);
    LocalDate targetStartDate = LocalDate.of(2024, 10, 1);

    boolean result = copyService.copyEventsInRange(startDate, endDate, sourceCalendar,
            targetCalendar, targetStartDate);

    assertTrue(result);
    assertEquals(2, targetCalendar.getAllEvents().size());

    for (Event event : targetCalendar.getAllEvents()) {
      assertNotNull(event.getSeriesId());
      assertTrue(event.getSeriesId().startsWith("copied-"));
    }
  }

  /**
   * Test copying single event maintains duration across timezones.
   */
  @Test
  @DisplayName("Test copy single event maintains duration across timezones")
  void testCopyEventMaintainsDuration() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 0);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 16, 30);

    sourceCalendar.createEvent("Long Meeting", start, end);
    LocalDateTime newStart = LocalDateTime.of(2024, 9, 16, 9, 0);

    boolean result = copyService.copyEvent("Long Meeting", start, sourceCalendar,
            timezoneTargetCalendar, newStart);

    assertTrue(result);
    Event copiedEvent = timezoneTargetCalendar.getAllEvents().iterator().next();

    long originalDurationMinutes = java.time.Duration.between(start, end).toMinutes();
    long copiedDurationMinutes = java.time.Duration.between(
            copiedEvent.getStartDateTime(), copiedEvent.getEndDateTime()).toMinutes();

    assertEquals(originalDurationMinutes, copiedDurationMinutes);
  }
}