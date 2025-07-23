package model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * EventSeries class for managing recurring events.
 * Represents a series of events that repeat on specific days of the week
 * with consistent timing and properties.
 * Implements IEventSeries interface for proper contract compliance.
 *
 * <p>This class stores the metadata for a recurring event series, including
 * which days of the week events should occur, what times they should run,
 * and whether they are all-day events.
 *
 * @author Calendar Application Team
 * @version 1.0
 */
public class EventSeries implements IEventSeries {
  /**
   * Unique identifier for this event series.
   */
  private String seriesId;

  /**
   * Set of weekdays when events in this series should occur.
   */
  private Set<DayOfWeek> weekdays;

  /**
   * Start time for events in this series.
   */
  private LocalTime startTime;

  /**
   * End time for events in this series.
   */
  private LocalTime endTime;

  /**
   * Whether events in this series are all-day events.
   */
  private boolean isAllDay;

  /**
   * Creates a new event series with specified parameters.
   *
   * @param seriesId  unique series identifier (required)
   * @param weekdays  days of week for recurrence (required)
   * @param startTime start time for events (required)
   * @param endTime   end time for events (required)
   * @param isAllDay  true if events are all-day events
   */
  public EventSeries(String seriesId, Set<DayOfWeek> weekdays, LocalTime startTime,
                     LocalTime endTime, boolean isAllDay) {
    this.seriesId = seriesId;
    this.weekdays = new HashSet<>(weekdays);
    this.startTime = startTime;
    this.endTime = endTime;
    this.isAllDay = isAllDay;
  }

  /**
   * Gets the unique series identifier.
   * Implementation of IEventSeries interface.
   *
   * @return the series ID
   */
  @Override
  public String getSeriesId() {
    return seriesId;
  }

  /**
   * Gets the weekdays for recurrence.
   * Returns a defensive copy to prevent external modification.
   * Implementation of IEventSeries interface.
   *
   * @return set of weekdays when events should occur
   */
  @Override
  public Set<DayOfWeek> getWeekdays() {
    return new HashSet<>(weekdays);
  }

  /**
   * Gets the start time for events in this series.
   * Implementation of IEventSeries interface.
   *
   * @return start time
   */
  @Override
  public LocalTime getStartTime() {
    return startTime;
  }

  /**
   * Gets the end time for events in this series.
   * Implementation of IEventSeries interface.
   *
   * @return end time
   */
  @Override
  public LocalTime getEndTime() {
    return endTime;
  }

  /**
   * Checks if events in this series are all-day events.
   * Implementation of IEventSeries interface.
   *
   * @return true if all-day events, false otherwise
   */
  @Override
  public boolean isAllDay() {
    return isAllDay;
  }

  /**
   * Returns a string representation of this event series.
   * Includes the series ID, weekdays, and timing information.
   *
   * @return formatted string representation
   */
  @Override
  public String toString() {
    return String.format("EventSeries{seriesId='%s', weekdays=%s, " +
                    "startTime=%s, endTime=%s, isAllDay=%s}",
            seriesId, weekdays, startTime, endTime, isAllDay);
  }

  /**
   * Checks if this event series is equal to another object.
   * Two event series are equal if they have the same series ID.
   *
   * @param obj the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    EventSeries that = (EventSeries) obj;
    return seriesId.equals(that.seriesId);
  }

  /**
   * Returns the hash code for this event series.
   * Based on the series ID for consistency with equals method.
   *
   * @return hash code of the event series
   */
  @Override
  public int hashCode() {
    return seriesId.hashCode();
  }
}