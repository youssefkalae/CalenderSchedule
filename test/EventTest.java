import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import model.Event;
import model.EventStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive test suite for the Event class.
 * Tests all constructors, getters, setters, and business logic methods.
 *
 * @author Calendar Application Team
 * @version 1.0
 */
public class EventTest {

  private Event timedEvent;
  private Event allDayEvent;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private LocalDate testDate;

  /**
   * Sets up test fixtures before each test method.
   */
  @Before
  public void setUp() {
    startTime = LocalDateTime.of(2025, 5, 5, 10, 0);
    endTime = LocalDateTime.of(2025, 5, 5, 11, 0);
    testDate = LocalDate.of(2025, 5, 5);

    timedEvent = new Event("Meeting", startTime, endTime, "Project discussion",
            "Conference Room A", EventStatus.PUBLIC);
    allDayEvent = new Event("Holiday", testDate, "National Holiday",
            "Nationwide", EventStatus.PRIVATE);
  }

  /**
   * Tests the full constructor for timed events.
   */
  @Test
  public void testTimedEventFullConstructor() {
    assertEquals("Meeting", timedEvent.getSubject());
    assertEquals(startTime, timedEvent.getStartDateTime());
    assertEquals(endTime, timedEvent.getEndDateTime());
    assertEquals("Project discussion", timedEvent.getDescription());
    assertEquals("Conference Room A", timedEvent.getLocation());
    assertEquals(EventStatus.PUBLIC, timedEvent.getStatus());
    assertFalse(timedEvent.isAllDay());
    assertNull(timedEvent.getSeriesId());
  }

  /**
   * Tests the minimal constructor for timed events.
   */
  @Test
  public void testTimedEventMinimalConstructor() {
    Event simpleEvent = new Event("Simple Meeting", startTime, endTime);

    assertEquals("Simple Meeting", simpleEvent.getSubject());
    assertEquals(startTime, simpleEvent.getStartDateTime());
    assertEquals(endTime, simpleEvent.getEndDateTime());
    assertNull(simpleEvent.getDescription());
    assertNull(simpleEvent.getLocation());
    assertEquals(EventStatus.PUBLIC, simpleEvent.getStatus());
    assertFalse(simpleEvent.isAllDay());
  }

  /**
   * Tests the full constructor for all-day events.
   */
  @Test
  public void testAllDayEventFullConstructor() {
    assertEquals("Holiday", allDayEvent.getSubject());
    assertEquals(testDate.atTime(8, 0), allDayEvent.getStartDateTime());
    assertEquals(testDate.atTime(17, 0), allDayEvent.getEndDateTime());
    assertEquals("National Holiday", allDayEvent.getDescription());
    assertEquals("Nationwide", allDayEvent.getLocation());
    assertEquals(EventStatus.PRIVATE, allDayEvent.getStatus());
    assertTrue(allDayEvent.isAllDay());
  }

  /**
   * Tests the minimal constructor for all-day events.
   */
  @Test
  public void testAllDayEventMinimalConstructor() {
    Event simpleAllDay = new Event("Simple Holiday", testDate);

    assertEquals("Simple Holiday", simpleAllDay.getSubject());
    assertEquals(testDate.atTime(8, 0), simpleAllDay.getStartDateTime());
    assertEquals(testDate.atTime(17, 0), simpleAllDay.getEndDateTime());
    assertNull(simpleAllDay.getDescription());
    assertNull(simpleAllDay.getLocation());
    assertEquals(EventStatus.PUBLIC, simpleAllDay.getStatus());
    assertTrue(simpleAllDay.isAllDay());
  }

  /**
   * Tests all setter methods.
   */
  @Test
  public void testSetters() {
    Event event = new Event("Test", startTime, endTime);

    event.setSubject("Updated Subject");
    assertEquals("Updated Subject", event.getSubject());

    LocalDateTime newStart = startTime.plusHours(1);
    event.setStartDateTime(newStart);
    assertEquals(newStart, event.getStartDateTime());

    LocalDateTime newEnd = endTime.plusHours(1);
    event.setEndDateTime(newEnd);
    assertEquals(newEnd, event.getEndDateTime());

    event.setDescription("New description");
    assertEquals("New description", event.getDescription());

    event.setLocation("New location");
    assertEquals("New location", event.getLocation());

    event.setStatus(EventStatus.PRIVATE);
    assertEquals(EventStatus.PRIVATE, event.getStatus());

    event.setStatus("public");
    assertEquals(EventStatus.PUBLIC, event.getStatus());

    event.setStatus("private");
    assertEquals(EventStatus.PRIVATE, event.getStatus());

    event.setAllDay(true);
    assertTrue(event.isAllDay());

    event.setSeriesId("series123");
    assertEquals("series123", event.getSeriesId());
  }

  /**
   * Tests the conflictsWith method.
   */
  @Test
  public void testConflictsWith() {
    Event event1 = new Event("Meeting", startTime, endTime);
    Event event2 = new Event("Meeting", startTime, endTime);
    Event event3 = new Event("Different Meeting", startTime, endTime);
    Event event4 = new Event("Meeting", startTime.plusHours(1), endTime.plusHours(1));

    assertTrue(event1.conflictsWith(event2));
    assertFalse(event1.conflictsWith(event3));
    assertFalse(event1.conflictsWith(event4));
  }

  /**
   * Tests the occursOnDate method.
   */
  @Test
  public void testOccursOnDate() {
    assertTrue(timedEvent.occursOnDate(testDate));
    assertFalse(timedEvent.occursOnDate(testDate.plusDays(1)));

    assertTrue(allDayEvent.occursOnDate(testDate));
    assertFalse(allDayEvent.occursOnDate(testDate.minusDays(1)));
  }

  /**
   * Tests the isActiveAt method.
   */
  @Test
  public void testIsActiveAt() {
    LocalDateTime duringEvent = startTime.plusMinutes(30);
    LocalDateTime beforeEvent = startTime.minusMinutes(30);
    LocalDateTime afterEvent = endTime.plusMinutes(30);

    assertTrue(timedEvent.isActiveAt(duringEvent));
    assertFalse(timedEvent.isActiveAt(beforeEvent));
    assertFalse(timedEvent.isActiveAt(afterEvent));
    assertTrue(timedEvent.isActiveAt(startTime));
    assertFalse(timedEvent.isActiveAt(endTime));
  }

  /**
   * Tests the equals method.
   */
  @Test
  public void testEquals() {
    Event event1 = new Event("Meeting", startTime, endTime);
    Event event2 = new Event("Meeting", startTime, endTime);
    Event event3 = new Event("Different", startTime, endTime);

    assertEquals(event1, event2);
    assertNotEquals(event1, event3);
    assertNotEquals(event1, null);
    assertNotEquals(event1, "not an event");
  }

  /**
   * Tests the hashCode method.
   */
  @Test
  public void testHashCode() {
    Event event1 = new Event("Meeting", startTime, endTime);
    Event event2 = new Event("Meeting", startTime, endTime);

    assertEquals(event1.hashCode(), event2.hashCode());
  }

  /**
   * Tests the toString method.
   */
  @Test
  public void testToString() {
    String result = timedEvent.toString();
    assertTrue(result.contains("Meeting"));
    assertTrue(result.contains("10:00"));
    assertTrue(result.contains("11:00"));
    assertTrue(result.contains("Conference Room A"));

    String allDayResult = allDayEvent.toString();
    assertTrue(allDayResult.contains("Holiday"));
    assertTrue(allDayResult.contains("All Day"));
    assertTrue(allDayResult.contains("Nationwide"));
  }

  /**
   * Tests null handling in constructors.
   */
  @Test
  public void testNullHandling() {
    Event eventWithNullSubject = new Event(null, startTime, endTime);
    assertNull("Subject should be null", eventWithNullSubject.getSubject());

    Event eventWithNullStart = new Event("Test", null, endTime);
    assertNull("Start time should be null", eventWithNullStart.getStartDateTime());

    Event eventWithNullEnd = new Event("Test", startTime, null);
    assertNull("End time should be null", eventWithNullEnd.getEndDateTime());

    Event eventWithNulls = new Event("Test", startTime, endTime, null, null, null);
    assertNull("Description should be null", eventWithNulls.getDescription());
    assertNull("Location should be null", eventWithNulls.getLocation());
    assertEquals("Status should default to PRIVATE when null",
            EventStatus.PRIVATE, eventWithNulls.getStatus());
  }

  /**
   * Tests events that span multiple days.
   */
  @Test
  public void testMultiDayEvent() {
    LocalDateTime start = LocalDateTime.of(2025, 5, 5, 23, 0);
    LocalDateTime end = LocalDateTime.of(2025, 5, 6, 1, 0);
    Event multiDayEvent = new Event("Late Meeting", start, end);

    assertTrue("Should occur on start date", multiDayEvent.occursOnDate(LocalDate.of(2025, 5, 5)));
    assertTrue("Should occur on end date", multiDayEvent.occursOnDate(LocalDate.of(2025, 5, 6)));
  }

  /**
   * Tests events with same start and end time.
   */
  @Test
  public void testZeroDurationEvent() {
    Event instantEvent = new Event("Announcement", startTime, startTime);

    assertFalse("Zero duration event should not be active at start time",
            instantEvent.isActiveAt(startTime));
    assertTrue("Should occur on the date", instantEvent.occursOnDate(testDate));
  }

  /**
   * Tests status string conversion with different cases.
   */
  @Test
  public void testStatusStringConversion() {
    Event event = new Event("Test", startTime, endTime);

    event.setStatus("PUBLIC");
    assertEquals(EventStatus.PUBLIC, event.getStatus());

    event.setStatus("Private");
    assertEquals(EventStatus.PRIVATE, event.getStatus());

    event.setStatus("invalid");
    assertEquals(EventStatus.PRIVATE, event.getStatus());
  }

  /**
   * Tests toString with null or empty fields.
   */
  @Test
  public void testToStringWithNullFields() {
    Event eventWithNulls = new Event("Test", startTime, endTime, null, null, EventStatus.PUBLIC);
    String result = eventWithNulls.toString();

    assertFalse("Should not contain 'at' when location is null", result.contains(" at "));
    assertTrue("Should contain subject", result.contains("Test"));
  }

  /**
   * Tests toString with empty location.
   */
  @Test
  public void testToStringWithEmptyLocation() {
    Event eventWithEmptyLocation = new Event("Test",
            startTime, endTime, "desc", "", EventStatus.PUBLIC);
    String result = eventWithEmptyLocation.toString();

    assertFalse("Should not contain 'at' when location is empty", result.contains(" at "));
  }

  /**
   * Tests series ID functionality.
   */
  @Test
  public void testSeriesIdFunctionality() {
    Event event = new Event("Test", startTime, endTime);
    assertNull("Series ID should initially be null", event.getSeriesId());

    event.setSeriesId("series_123");
    assertEquals("series_123", event.getSeriesId());

    event.setSeriesId(null);
    assertNull("Series ID should be settable to null", event.getSeriesId());
  }

  /**
   * Tests equals with different property combinations.
   */
  @Test
  public void testEqualsWithDifferentProperties() {
    Event base = new Event("Meeting", startTime, endTime);
    Event withDescription = new Event("Meeting", startTime, endTime,
            "desc", null, EventStatus.PUBLIC);
    Event withLocation = new Event("Meeting", startTime, endTime,
            null, "Room", EventStatus.PUBLIC);

    assertEquals("Events with different descriptions should be equal", base, withDescription);
    assertEquals("Events with different locations should be equal", base, withLocation);
  }
}