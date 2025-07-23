package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * Interface for calendar operations and management.
 * Defines the contract for calendar implementations that support
 * multi-calendar functionality with timezone support.
 */
public interface ICalendarInstance {

  /**
   * Gets the name of this calendar.
   *
   * @return the calendar name
   */
  String getName();

  /**
   * Sets the name of this calendar.
   *
   * @param name the new calendar name
   */
  void setName(String name);

  /**
   * Gets the timezone of this calendar.
   *
   * @return the calendar timezone
   */
  ZoneId getTimezone();

  /**
   * Sets the timezone of this calendar.
   *
   * @param timezone the new timezone
   */
  void setTimezone(ZoneId timezone);

  /**
   * Adds an event to this calendar.
   *
   * @param event the event to add
   * @return true if the event was added successfully, false if a duplicate exists
   */
  boolean addEvent(Event event);

  /**
   * Gets all events in this calendar.
   *
   * @return set of all events
   */
  Set<Event> getAllEvents();

  /**
   * Creates a timed event with all properties.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @param description   event description
   * @param location      event location
   * @param status        event status
   * @return true if the event was created successfully
   */
  boolean createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                      String description, String location, EventStatus status);

  /**
   * Creates a timed event with minimal properties.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @return true if the event was created successfully
   */
  boolean createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Creates an all-day event with all properties.
   *
   * @param subject     event subject
   * @param date        event date
   * @param description event description
   * @param location    event location
   * @param status      event status
   * @return true if the event was created successfully
   */
  boolean createAllDayEvent(String subject, LocalDate date, String description,
                            String location, EventStatus status);

  /**
   * Creates an all-day event with minimal properties.
   *
   * @param subject event subject
   * @param date    event date
   * @return true if the event was created successfully
   */
  boolean createAllDayEvent(String subject, LocalDate date);

  /**
   * Creates a series of timed events.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @param weekdays      days of the week for recurrence
   * @param occurrences   number of occurrences
   * @param description   event description
   * @param location      event location
   * @param status        event status
   * @param seriesId      unique series identifier
   * @return true if the series was created successfully
   */
  boolean createEventSeries(String subject, LocalDateTime startDateTime,
                            LocalDateTime endDateTime, Set<DayOfWeek> weekdays, int occurrences,
                            String description, String location, EventStatus status,
                            int seriesId);

  /**
   * Creates a series of timed events until a specific end date.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @param weekdays      days of the week for recurrence
   * @param endDate       end date for the series
   * @param description   event description
   * @param location      event location
   * @param status        event status
   * @param seriesId      unique series identifier
   * @return true if the series was created successfully
   */
  boolean createEventSeriesUntil(String subject, LocalDateTime startDateTime,
                                 LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                 LocalDate endDate, String description, String location,
                                 EventStatus status, int seriesId);

  /**
   * Creates a series of all-day events.
   *
   * @param subject     event subject
   * @param startDate   start date
   * @param weekdays    days of the week for recurrence
   * @param occurrences number of occurrences
   * @param description event description
   * @param location    event location
   * @param status      event status
   * @param seriesId    unique series identifier
   * @return true if the series was created successfully
   */
  boolean createAllDayEventSeries(String subject, LocalDate startDate, Set<DayOfWeek> weekdays,
                                  int occurrences, String description, String location,
                                  EventStatus status, int seriesId);

  /**
   * Creates a series of all-day events until a specific end date.
   *
   * @param subject     event subject
   * @param startDate   start date
   * @param weekdays    days of the week for recurrence
   * @param endDate     end date for the series
   * @param description event description
   * @param location    event location
   * @param status      event status
   * @param seriesId    unique series identifier
   * @return true if the series was created successfully
   */
  boolean createAllDayEventSeriesUntil(String subject, LocalDate startDate,
                                       Set<DayOfWeek> weekdays, LocalDate endDate,
                                       String description, String location, EventStatus status,
                                       int seriesId);

  /**
   * Finds an event by subject, start time, and end time.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @return the event if found, null otherwise
   */
  Event findEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Finds an event by subject and start time.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @return the event if found, null otherwise
   */
  Event findEventBySubjectAndStart(String subject, LocalDateTime startDateTime);

  /**
   * Gets all events occurring on a specific date.
   *
   * @param date the date to search for
   * @return list of events on the specified date
   */
  List<Event> getEventsOnDate(LocalDate date);

  /**
   * Gets all events within a specific date/time range.
   *
   * @param startDateTime start of the range
   * @param endDateTime   end of the range
   * @return list of events in the specified range
   */
  List<Event> getEventsInRange(LocalDateTime startDateTime, LocalDateTime endDateTime);

  /**
   * Checks if the calendar is busy at a specific date/time.
   *
   * @param dateTime the date/time to check
   * @return true if there are active events at the specified time
   */
  boolean isBusy(LocalDateTime dateTime);

  /**
   * Edits a specific event.
   *
   * @param property      the property to edit
   * @param subject       event subject
   * @param startDateTime event start time
   * @param endDateTime   event end time
   * @param newValue      the new value for the property
   * @return true if the event was edited successfully
   */
  boolean editEvent(String property, String subject, LocalDateTime startDateTime,
                    LocalDateTime endDateTime, String newValue);

  /**
   * Edits all events in a series starting from a specific date.
   *
   * @param property      the property to edit
   * @param subject       event subject
   * @param startDateTime starting date/time
   * @param newValue      the new value for the property
   * @return true if events were edited successfully
   */
  boolean editEventsFromDate(String property, String subject, LocalDateTime startDateTime,
                             String newValue);

  /**
   * Edits all events in an entire series.
   *
   * @param property      the property to edit
   * @param subject       event subject
   * @param startDateTime any event's start time in the series
   * @param newValue      the new value for the property
   * @return true if events were edited successfully
   */
  boolean editEntireSeries(String property, String subject, LocalDateTime startDateTime,
                           String newValue);
}