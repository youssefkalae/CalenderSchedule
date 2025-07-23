package model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

/**
 * Interface for EventSeries operations.
 * Defines the contract for managing recurring event series metadata.
 * This includes timing, recurrence patterns, and series identification.
 */
public interface IEventSeries {

  /**
   * Gets the unique series identifier.
   *
   * @return the series ID
   */
  String getSeriesId();

  /**
   * Gets the weekdays for recurrence.
   * Should return a defensive copy to prevent external modification.
   *
   * @return set of weekdays when events should occur
   */
  Set<DayOfWeek> getWeekdays();

  /**
   * Gets the start time for events in this series.
   *
   * @return start time
   */
  LocalTime getStartTime();

  /**
   * Gets the end time for events in this series.
   *
   * @return end time
   */
  LocalTime getEndTime();

  /**
   * Checks if events in this series are all-day events.
   *
   * @return true if all-day events, false otherwise
   */
  boolean isAllDay();
}