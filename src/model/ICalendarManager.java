package model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * Interface for managing multiple calendar instances.
 * Defines the contract for calendar creation, selection, and cross-calendar operations.
 */
public interface ICalendarManager {

  /**
   * Creates a new calendar with the specified name and timezone.
   *
   * @param name     the unique name for the calendar
   * @param timezone the timezone for the calendar
   * @return true if calendar was created successfully, false if name already exists
   */
  boolean createCalendar(String name, ZoneId timezone);

  /**
   * Edits a property of an existing calendar.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to edit ("name" or "timezone")
   * @param newValue     the new value for the property
   * @return true if the property was edited successfully
   */
  boolean editCalendar(String calendarName, String property, String newValue);

  /**
   * Sets the currently active calendar.
   *
   * @param calendarName the name of the calendar to use
   * @return true if the calendar was set successfully, false if calendar doesn't exist
   */
  boolean useCalendar(String calendarName);

  /**
   * Gets the currently active calendar.
   *
   * @return the current CalendarInstance, or null if none is set
   */
  CalendarInstance getCurrentCalendar();

  /**
   * Gets the name of the currently active calendar.
   *
   * @return the current calendar name, or null if none is set
   */
  String getCurrentCalendarName();

  /**
   * Gets a calendar by name.
   *
   * @param name the calendar name
   * @return the CalendarInstance, or null if not found
   */
  CalendarInstance getCalendar(String name);

  /**
   * Gets all calendar names.
   *
   * @return set of all calendar names
   */
  Set<String> getCalendarNames();

  /**
   * Checks if a calendar exists.
   *
   * @param name the calendar name to check
   * @return true if the calendar exists
   */
  boolean calendarExists(String name);

  /**
   * Copies a single event from current calendar to target calendar.
   *
   * @param eventName          the name of the event to copy
   * @param eventStartTime     the start time of the event
   * @param targetCalendarName the target calendar name
   * @param newStartTime       the new start time in target calendar
   * @return true if the event was copied successfully
   */
  boolean copyEvent(String eventName, LocalDateTime eventStartTime,
                    String targetCalendarName, LocalDateTime newStartTime);

  /**
   * Copies all events from a specific date from current calendar to target calendar.
   *
   * @param sourceDate         the date to copy events from
   * @param targetCalendarName the target calendar name
   * @param targetDate         the target date
   * @return true if events were copied successfully
   */
  boolean copyEventsOnDate(LocalDate sourceDate, String targetCalendarName, LocalDate targetDate);

  /**
   * Copies events within a date range from current calendar to target calendar.
   *
   * @param startDate          the start date of the range
   * @param endDate            the end date of the range
   * @param targetCalendarName the target calendar name
   * @param targetStartDate    the target start date
   * @return true if events were copied successfully
   */
  boolean copyEventsInRange(LocalDate startDate, LocalDate endDate,
                            String targetCalendarName, LocalDate targetStartDate);

  /**
   * Creates an event in the current calendar.
   * Delegates to the current CalendarInstance.
   *
   * @param subject       the event subject
   * @param startDateTime the start date and time
   * @param endDateTime   the end date and time
   * @param description   the event description
   * @param location      the event location
   * @param status        the event status
   * @return true if the event was created successfully
   */
  boolean createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                      String description, String location, EventStatus status);

  /**
   * Gets events on a date from the current calendar.
   * Delegates to the current CalendarInstance.
   *
   * @param date the date to get events for
   * @return list of events on the specified date
   */
  List<Event> getEventsOnDate(LocalDate date);

  /**
   * Edits an event in the current calendar.
   * Delegates to the current CalendarInstance.
   *
   * @param property      the property to edit
   * @param subject       the event subject
   * @param startDateTime the event start time
   * @param endDateTime   the event end time
   * @param newValue      the new value for the property
   * @return true if the event was edited successfully
   */
  boolean editEvent(String property, String subject, LocalDateTime startDateTime,
                    LocalDateTime endDateTime, String newValue);
}