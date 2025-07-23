package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Event class representing a single calendar event.
 * Enhanced to support all required properties and better duplicate detection.
 * Implements IEvent interface for proper contract compliance.
 */
public class Event implements IEvent {
  private String subject;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String description;
  private String location;
  private EventStatus status;
  private boolean isAllDay;
  private String seriesId; // null if not part of a series

  /**
   * Creates a timed event with all properties.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @param description   event description (can be null)
   * @param location      event location (can be null)
   * @param status        event status (public/private)
   */
  public Event(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
               String description, String location, EventStatus status) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = description;
    this.location = location;
    this.status = status != null ? status : EventStatus.PRIVATE;
    this.isAllDay = false;
  }

  /**
   * Creates a timed event with minimal properties.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   */
  public Event(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this(subject, startDateTime, endDateTime, null, null, EventStatus.PUBLIC);
  }

  /**
   * Creates an all-day event with all properties.
   *
   * @param subject     event subject
   * @param date        event date
   * @param description event description (can be null)
   * @param location    event location (can be null)
   * @param status      event status (public/private)
   */
  public Event(String subject, LocalDate date, String description,
               String location, EventStatus status) {
    this.subject = subject;
    this.startDateTime = date.atTime(8, 0);
    this.endDateTime = date.atTime(17, 0);
    this.description = description;
    this.location = location;
    this.status = status != null ? status : EventStatus.PUBLIC;
    this.isAllDay = true;
  }

  /**
   * Creates an all-day event with minimal properties.
   *
   * @param subject event subject
   * @param date    event date
   */
  public Event(String subject, LocalDate date) {
    this(subject, date, null, null, EventStatus.PUBLIC);
  }

  // Getters - Implementation of IEvent interface
  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  @Override
  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public EventStatus getStatus() {
    return status;
  }

  @Override
  public boolean isAllDay() {
    return isAllDay;
  }

  @Override
  public String getSeriesId() {
    return seriesId;
  }

  // Setters - Implementation of IEvent interface
  @Override
  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Override
  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  @Override
  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setLocation(String location) {
    this.location = location;
  }

  @Override
  public void setStatus(EventStatus status) {
    this.status = status;
  }

  @Override
  public void setStatus(String status) {
    this.status = "public".equalsIgnoreCase(status) ? EventStatus.PUBLIC : EventStatus.PRIVATE;
  }

  @Override
  public void setAllDay(boolean allDay) {
    this.isAllDay = allDay;
  }

  @Override
  public void setSeriesId(String seriesId) {
    this.seriesId = seriesId;
  }

  /**
   * Check if this event conflicts with another (same subject, start, and end).
   * This is the proper duplicate detection logic per assignment requirements.
   * Updated to accept IEvent interface for better polymorphism.
   */
  @Override
  public boolean conflictsWith(IEvent other) {
    return Objects.equals(this.subject, other.getSubject())
            && Objects.equals(this.startDateTime, other.getStartDateTime())
            && Objects.equals(this.endDateTime, other.getEndDateTime());
  }

  /**
   * Convenience method for backward compatibility with Event class.
   */
  public boolean conflictsWith(Event other) {
    return conflictsWith((IEvent) other);
  }

  /**
   * Check if event occurs on a specific date.
   */
  @Override
  public boolean occursOnDate(LocalDate date) {
    LocalDate startDate = startDateTime.toLocalDate();
    LocalDate endDate = endDateTime.toLocalDate();
    return !date.isBefore(startDate) && !date.isAfter(endDate);
  }

  /**
   * Check if event is active at a specific date/time.
   */
  @Override
  public boolean isActiveAt(LocalDateTime dateTime) {
    return !dateTime.isBefore(startDateTime) && dateTime.isBefore(endDateTime);
  }

  /**
   * Checks if this event is equal to another object.
   * Two events are considered equal if their subject, start time,
   * and end time are all equal (assignment requirement).
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Event event = (Event) obj;
    return Objects.equals(subject, event.subject)
            && Objects.equals(startDateTime, event.startDateTime)
            && Objects.equals(endDateTime, event.endDateTime);
  }

  /**
   * Returns the hash code for this event.
   * The hash code is based on the subject, start time, and end time.
   */
  @Override
  public int hashCode() {
    return Objects.hash(subject, startDateTime, endDateTime);
  }

  /**
   * Returns a string representation of this event.
   * Enhanced formatting for better display.
   */
  @Override
  public String toString() {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

    String dateStr = startDateTime.format(dateFormatter);

    String timeStr;
    if (isAllDay) {
      timeStr = "All Day";
    } else {
      timeStr = startDateTime.format(timeFormatter) + " - " + endDateTime.format(timeFormatter);
    }

    String locationStr = (location != null && !location.trim().isEmpty()) ? " at " + location : "";

    return "• " + subject + " (" + dateStr + ", " + timeStr + ")" + locationStr;
  }
}