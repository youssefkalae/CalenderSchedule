import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import model.EventSeries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive test suite for the EventSeries class. Tests behavior for all constructors,
 * getters, and business logic methods.
 */
public class EventSeriesTest {

  private EventSeries timedSeries;
  private EventSeries allDaySeries;
  private Set<DayOfWeek> weekdays;
  private LocalTime startTime;
  private LocalTime endTime;

  @Before
  public void setUp() {
    weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);
    weekdays.add(DayOfWeek.WEDNESDAY);
    weekdays.add(DayOfWeek.FRIDAY);

    startTime = LocalTime.of(10, 0);
    endTime = LocalTime.of(11, 0);

    timedSeries = new EventSeries("series_1", weekdays, startTime, endTime, false);
    allDaySeries = new EventSeries("series_2", weekdays, LocalTime.of(8, 0),
            LocalTime.of(17, 0), true);
  }

  /**
   * Tests constructor creates timed series with correct properties.
   */
  @Test
  public void testConstructorTimedSeries() {
    assertEquals("series_1", timedSeries.getSeriesId());
    assertEquals(weekdays, timedSeries.getWeekdays());
    assertEquals(startTime, timedSeries.getStartTime());
    assertEquals(endTime, timedSeries.getEndTime());
    assertFalse(timedSeries.isAllDay());
  }

  /**
   * Tests constructor creates all-day series with correct properties.
   */
  @Test
  public void testConstructorAllDaySeries() {
    assertEquals("series_2", allDaySeries.getSeriesId());
    assertEquals(weekdays, allDaySeries.getWeekdays());
    assertEquals(LocalTime.of(8, 0), allDaySeries.getStartTime());
    assertEquals(LocalTime.of(17, 0), allDaySeries.getEndTime());
    assertTrue(allDaySeries.isAllDay());
  }

  /**
   * Tests constructor handles empty weekdays set correctly.
   */
  @Test
  public void testConstructorEmptyWeekdays() {
    Set<DayOfWeek> emptyWeekdays = new HashSet<>();
    EventSeries series = new EventSeries("empty_series", emptyWeekdays,
            startTime, endTime, false);

    assertEquals("empty_series", series.getSeriesId());
    assertTrue("Weekdays should be empty", series.getWeekdays().isEmpty());
    assertEquals(startTime, series.getStartTime());
    assertEquals(endTime, series.getEndTime());
    assertFalse(series.isAllDay());
  }

  /**
   * Tests constructor with a single weekday.
   */
  @Test
  public void testConstructor_SingleWeekday() {
    Set<DayOfWeek> singleDay = new HashSet<>();
    singleDay.add(DayOfWeek.TUESDAY);

    EventSeries series = new EventSeries("tuesday_series", singleDay,
            startTime, endTime, false);

    assertEquals(1, series.getWeekdays().size());
    assertTrue("Should contain Tuesday", series.getWeekdays().contains(DayOfWeek.TUESDAY));
  }

  /**
   * Tests constructor with all seven weekdays.
   */
  @Test
  public void testConstructor_AllWeekdays() {
    Set<DayOfWeek> allDays = new HashSet<>();
    Collections.addAll(allDays, DayOfWeek.values());

    EventSeries series = new EventSeries("daily_series", allDays,
            startTime, endTime, false);

    assertEquals(7, series.getWeekdays().size());
    for (DayOfWeek day : DayOfWeek.values()) {
      assertTrue("Should contain " + day, series.getWeekdays().contains(day));
    }
  }

  /**
   * Tests getSeriesId returns correct series identifier.
   */
  @Test
  public void testGetSeriesId() {
    assertEquals("series_1", timedSeries.getSeriesId());
    assertEquals("series_2", allDaySeries.getSeriesId());
  }

  /**
   * Tests getWeekdays returns defensive copy that cannot modify original.
   */
  @Test
  public void testGetWeekdays() {
    Set<DayOfWeek> returnedWeekdays = timedSeries.getWeekdays();
    returnedWeekdays.add(DayOfWeek.SATURDAY);
    assertFalse("Original weekdays should not be modified",
            timedSeries.getWeekdays().contains(DayOfWeek.SATURDAY));
    assertEquals("Original should still have 3 days", 3, timedSeries
            .getWeekdays().size());
  }

  /**
   * Tests getWeekdays returns correct weekday content.
   */
  @Test
  public void testGetWeekdaysContent() {
    Set<DayOfWeek> returned = timedSeries.getWeekdays();

    assertTrue("Should contain Monday", returned.contains(DayOfWeek.MONDAY));
    assertTrue("Should contain Wednesday", returned.contains(DayOfWeek.WEDNESDAY));
    assertTrue("Should contain Friday", returned.contains(DayOfWeek.FRIDAY));
    assertFalse("Should not contain Tuesday", returned.contains(DayOfWeek.TUESDAY));
    assertFalse("Should not contain Thursday", returned.contains(DayOfWeek.THURSDAY));
    assertFalse("Should not contain Saturday", returned.contains(DayOfWeek.SATURDAY));
    assertFalse("Should not contain Sunday", returned.contains(DayOfWeek.SUNDAY));
  }

  /**
   * Tests getStartTime returns correct start time.
   */
  @Test
  public void testGetStartTime() {
    assertEquals(startTime, timedSeries.getStartTime());
    assertEquals(LocalTime.of(8, 0), allDaySeries.getStartTime());
  }

  /**
   * Tests getEndTime returns correct end time.
   */
  @Test
  public void testGetEndTime() {
    assertEquals(endTime, timedSeries.getEndTime());
    assertEquals(LocalTime.of(17, 0), allDaySeries.getEndTime());
  }

  /**
   * Tests isAllDay returns correct all-day status.
   */
  @Test
  public void testIsAllDay() {
    assertFalse("Timed series should not be all-day", timedSeries.isAllDay());
    assertTrue("All-day series should be all-day", allDaySeries.isAllDay());
  }

  /**
   * Tests creating series with midnight start and end times.
   */
  @Test
  public void testMidnightTimes() {
    LocalTime midnight = LocalTime.of(0, 0);
    LocalTime almostMidnight = LocalTime.of(23, 59);

    EventSeries midnightSeries = new EventSeries("midnight_series", weekdays,
            midnight, almostMidnight, false);

    assertEquals(midnight, midnightSeries.getStartTime());
    assertEquals(almostMidnight, midnightSeries.getEndTime());
  }

  /**
   * Tests creating series with noon-time hours.
   */
  @Test
  public void testNoonTimes() {
    LocalTime noon = LocalTime.of(12, 0);
    LocalTime afternoon = LocalTime.of(13, 30);

    EventSeries noonSeries = new EventSeries("noon_series", weekdays,
            noon, afternoon, false);

    assertEquals(noon, noonSeries.getStartTime());
    assertEquals(afternoon, noonSeries.getEndTime());
  }

  /**
   * Tests equals method returns true for objects with same series ID.
   */
  @Test
  public void testEquals_SameSeriesId() {
    Set<DayOfWeek> differentWeekdays = new HashSet<>();
    differentWeekdays.add(DayOfWeek.SATURDAY);

    EventSeries series1 = new EventSeries("same_id", weekdays, startTime, endTime, false);
    EventSeries series2 = new EventSeries("same_id", differentWeekdays,
            LocalTime.of(14, 0), LocalTime.of(15, 0), true);

    assertEquals("Should be equal with same series ID", series1, series2);
  }

  /**
   * Tests equals method returns false for objects with different series IDs.
   */
  @Test
  public void testEquals_DifferentSeriesId() {
    EventSeries series1 = new EventSeries("id_1", weekdays, startTime, endTime, false);
    EventSeries series2 = new EventSeries("id_2", weekdays, startTime, endTime, false);

    assertNotEquals("Should not be equal with different series IDs", series1, series2);
  }

  /**
   * Tests equals method returns false when compared to null.
   */
  @Test
  public void testEquals_NullObject() {
    assertNotEquals("Should not equal null", timedSeries, null);
  }

  /**
   * Tests equals method returns false when compared to different class.
   */
  @Test
  public void testEquals_DifferentClass() {
    String notAnEventSeries = "not an event series";
    assertNotEquals("Should not equal different class", timedSeries, notAnEventSeries);
  }

  /**
   * Tests equals method returns true when object compared to itself.
   */
  @Test
  public void testEquals_SameObject() {
    assertEquals("Should equal itself", timedSeries, timedSeries);
  }

  /**
   * Tests hashCode is consistent with equals method.
   */
  @Test
  public void testHashCode_ConsistentWithEquals() {
    EventSeries series1 = new EventSeries("test_id", weekdays, startTime, endTime,
            false);
    EventSeries series2 = new EventSeries("test_id", new HashSet<>(),
            LocalTime.of(1, 0), LocalTime.of(2, 0), true);

    assertEquals("Equal objects should have equal hash codes",
            series1.hashCode(), series2.hashCode());
  }

  /**
   * Tests hashCode produces different values for different series IDs.
   */
  @Test
  public void testHashCode_DifferentIds() {
    EventSeries series1 = new EventSeries("id_1", weekdays, startTime, endTime, false);
    EventSeries series2 = new EventSeries("id_2", weekdays, startTime, endTime, false);

    assertNotEquals("Different IDs should have different hash codes",
            series1.hashCode(), series2.hashCode());
  }

  /**
   * Tests toString contains all relevant field information.
   */
  @Test
  public void testToString_ContainsAllFields() {
    String result = timedSeries.toString();

    assertTrue("Should contain series ID", result.contains("series_1"));
    assertTrue("Should contain weekdays", result.contains("weekdays"));
    assertTrue("Should contain start time", result.contains("10:00"));
    assertTrue("Should contain end time", result.contains("11:00"));
    assertTrue("Should contain isAllDay", result.contains("isAllDay=false"));
  }

  /**
   * Tests toString format for all-day series.
   */
  @Test
  public void testToString_AllDaySeries() {
    String result = allDaySeries.toString();

    assertTrue("Should contain series ID", result.contains("series_2"));
    assertTrue("Should contain isAllDay=true", result.contains("isAllDay=true"));
    assertTrue("Should contain 08:00", result.contains("08:00"));
    assertTrue("Should contain 17:00", result.contains("17:00"));
  }

  /**
   * Tests toString handles empty weekdays correctly.
   */
  @Test
  public void testToString_EmptyWeekdays() {
    Set<DayOfWeek> emptyWeekdays = new HashSet<>();
    EventSeries emptySeries = new EventSeries("empty", emptyWeekdays,
            startTime, endTime, false);

    String result = emptySeries.toString();

    assertTrue("Should contain empty weekdays", result.contains("weekdays=[]"));
    assertTrue("Should contain series ID", result.contains("empty"));
  }

  /**
   * Tests that weekdays are properly encapsulated and immutable.
   */
  @Test
  public void testWeekdaysImmutability() {
    Set<DayOfWeek> originalWeekdays = new HashSet<>();
    originalWeekdays.add(DayOfWeek.MONDAY);

    EventSeries series = new EventSeries("test", originalWeekdays, startTime, endTime,
            false);
    originalWeekdays.add(DayOfWeek.TUESDAY);
    assertEquals("Series should only have Monday", 1, series.getWeekdays()
            .size());
    assertTrue("Series should still contain only Monday",
            series.getWeekdays().contains(DayOfWeek.MONDAY));
    assertFalse("Series should not contain Tuesday",
            series.getWeekdays().contains(DayOfWeek.TUESDAY));
  }

  /**
   * Tests creating series with business days only.
   */
  @Test
  public void testBusinessDaysOnly() {
    Set<DayOfWeek> businessDays = new HashSet<>();
    businessDays.add(DayOfWeek.MONDAY);
    businessDays.add(DayOfWeek.TUESDAY);
    businessDays.add(DayOfWeek.WEDNESDAY);
    businessDays.add(DayOfWeek.THURSDAY);
    businessDays.add(DayOfWeek.FRIDAY);

    EventSeries businessSeries = new EventSeries("business", businessDays,
            LocalTime.of(9, 0), LocalTime.of(17, 0), false);

    assertEquals(5, businessSeries.getWeekdays().size());
    assertFalse("Should not contain Saturday",
            businessSeries.getWeekdays().contains(DayOfWeek.SATURDAY));
    assertFalse("Should not contain Sunday",
            businessSeries.getWeekdays().contains(DayOfWeek.SUNDAY));
  }

  /**
   * Tests creating series with weekends only.
   */
  @Test
  public void testWeekendsOnly() {
    Set<DayOfWeek> weekends = new HashSet<>();
    weekends.add(DayOfWeek.SATURDAY);
    weekends.add(DayOfWeek.SUNDAY);

    EventSeries weekendSeries = new EventSeries("weekend", weekends,
            LocalTime.of(10, 0), LocalTime.of(18, 0), false);

    assertEquals(2, weekendSeries.getWeekdays().size());
    assertTrue("Should contain Saturday",
            weekendSeries.getWeekdays().contains(DayOfWeek.SATURDAY));
    assertTrue("Should contain Sunday",
            weekendSeries.getWeekdays().contains(DayOfWeek.SUNDAY));
  }

  /**
   * Tests handling of very long series ID strings.
   */
  @Test
  public void testVeryLongSeriesId() {
    String longId = "a".repeat(1000);
    EventSeries series = new EventSeries(longId, weekdays, startTime, endTime, false);

    assertEquals(longId, series.getSeriesId());
    assertEquals(longId.hashCode(), series.hashCode());
  }

  /**
   * Tests handling of special characters in series ID.
   */
  @Test
  public void testSpecialCharactersInSeriesId() {
    String specialId = "series_!@#$%^&*()_+-=[]{}|;:,.<>?";
    EventSeries series = new EventSeries(specialId, weekdays, startTime, endTime, false);

    assertEquals(specialId, series.getSeriesId());
    assertTrue("ToString should contain special characters",
            series.toString().contains(specialId));
  }

  /**
   * Tests creating series where start and end times are identical.
   */
  @Test
  public void testSameStartAndEndTime() {
    LocalTime sameTime = LocalTime.of(12, 0);
    EventSeries zeroLengthSeries = new EventSeries("zero_length", weekdays,
            sameTime, sameTime, false);

    assertEquals(sameTime, zeroLengthSeries.getStartTime());
    assertEquals(sameTime, zeroLengthSeries.getEndTime());
  }
}