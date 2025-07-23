package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Interface for Event operations.
 * Defines the contract for calendar event objects including
 * properties, validation, and comparison methods.
 */
public interface IEvent {

  // Getters
  String getSubject();

  LocalDateTime getStartDateTime();

  LocalDateTime getEndDateTime();

  String getDescription();

  String getLocation();

  EventStatus getStatus();

  boolean isAllDay();

  String getSeriesId();

  // Setters
  void setSubject(String subject);

  void setStartDateTime(LocalDateTime startDateTime);

  void setEndDateTime(LocalDateTime endDateTime);

  void setDescription(String description);

  void setLocation(String location);

  void setStatus(EventStatus status);

  void setStatus(String status);

  void setAllDay(boolean allDay);

  void setSeriesId(String seriesId);

  /**
   * Check if this event conflicts with another event.
   * Events conflict if they have the same subject, start time, and end time.
   *
   * @param other the other event to check against
   * @return true if events conflict, false otherwise
   */
  boolean conflictsWith(IEvent other);

  /**
   * Check if event occurs on a specific date.
   *
   * @param date the date to check
   * @return true if event occurs on the date, false otherwise
   */
  boolean occursOnDate(LocalDate date);

  /**
   * Check if event is active at a specific date/time.
   *
   * @param dateTime the date and time to check
   * @return true if event is active at the time, false otherwise
   */
  boolean isActiveAt(LocalDateTime dateTime);
}