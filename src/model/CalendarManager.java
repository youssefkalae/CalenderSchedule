package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CalendarManager class that manages multiple calendar instances.
 * Handles calendar creation, selection, and cross-calendar operations.
 * This is the main coordinator for the multi-calendar system.
 */
public class CalendarManager implements ICalendarManager {

  /**
   * Map of calendar names to CalendarInstance objects.
   */
  private Map<String, CalendarInstance> calendars;

  /**
   * The currently active/selected calendar.
   */
  private CalendarInstance currentCalendar;

  /**
   * Name of the currently active calendar.
   */
  private String currentCalendarName;

  /**
   * Service for copying events between calendars.
   */
  private IEventCopyService eventCopyService;

  /**
   * Global series counter to ensure unique series IDs across all calendars.
   */
  private int globalSeriesCounter;

  /**
   * Creates a new CalendarManager with no calendars.
   * Uses default EventCopyService implementation.
   */
  public CalendarManager() {
    this.calendars = new HashMap<>();
    this.currentCalendar = null;
    this.currentCalendarName = null;
    this.eventCopyService = new EventCopyService();
    this.globalSeriesCounter = 0;
  }

  /**
   * Creates a new CalendarManager with a custom EventCopyService.
   * This constructor allows for dependency injection.
   *
   * @param eventCopyService the event copy service to use
   */
  public CalendarManager(IEventCopyService eventCopyService) {
    this.calendars = new HashMap<>();
    this.currentCalendar = null;
    this.currentCalendarName = null;
    this.eventCopyService = eventCopyService;
    this.globalSeriesCounter = 0;
  }

  /**
   * Creates a new calendar with the specified name and timezone.
   *
   * @param name     the unique name for the calendar
   * @param timezone the timezone for the calendar
   * @return true if calendar was created successfully, false if name already exists
   */
  @Override
  public boolean createCalendar(String name, ZoneId timezone) {
    if (calendars.containsKey(name)) {
      return false;
    }

    CalendarInstance calendar = new CalendarInstance(name, timezone);
    calendars.put(name, calendar);
    return true;
  }

  /**
   * Edits a property of an existing calendar.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to edit ("name" or "timezone")
   * @param newValue     the new value for the property
   * @return true if the property was edited successfully
   */
  @Override
  public boolean editCalendar(String calendarName, String property, String newValue) {
    CalendarInstance calendar = calendars.get(calendarName);
    if (calendar == null) {
      return false;
    }

    switch (property.toLowerCase()) {
      case "name":
        return editCalendarName(calendarName, newValue);
      case "timezone":
        try {
          ZoneId newTimezone = ZoneId.of(newValue);
          calendar.setTimezone(newTimezone);
          return true;
        } catch (Exception e) {
          return false;
        }
      default:
        return false;
    }
  }

  /**
   * Sets the currently active calendar.
   *
   * @param calendarName the name of the calendar to use
   * @return true if the calendar was set successfully, false if calendar doesn't exist
   */
  @Override
  public boolean useCalendar(String calendarName) {
    CalendarInstance calendar = calendars.get(calendarName);
    if (calendar == null) {
      return false;
    }

    this.currentCalendar = calendar;
    this.currentCalendarName = calendarName;
    return true;
  }

  /**
   * Gets the currently active calendar.
   *
   * @return the current CalendarInstance, or null if none is set
   */
  @Override
  public CalendarInstance getCurrentCalendar() {
    return currentCalendar;
  }

  /**
   * Gets the name of the currently active calendar.
   *
   * @return the current calendar name, or null if none is set
   */
  @Override
  public String getCurrentCalendarName() {
    return currentCalendarName;
  }

  /**
   * Gets a calendar by name.
   *
   * @param name the calendar name
   * @return the CalendarInstance, or null if not found
   */
  @Override
  public CalendarInstance getCalendar(String name) {
    return calendars.get(name);
  }

  /**
   * Gets all calendar names.
   *
   * @return set of all calendar names
   */
  @Override
  public Set<String> getCalendarNames() {
    return new HashSet<>(calendars.keySet());
  }

  /**
   * Checks if a calendar exists.
   *
   * @param name the calendar name to check
   * @return true if the calendar exists
   */
  @Override
  public boolean calendarExists(String name) {
    return calendars.containsKey(name);
  }

  /**
   * Copies a single event from current calendar to target calendar.
   *
   * @param eventName          the name of the event to copy
   * @param eventStartTime     the start time of the event
   * @param targetCalendarName the target calendar name
   * @param newStartTime       the new start time in target calendar
   * @return true if the event was copied successfully
   */
  @Override
  public boolean copyEvent(String eventName, LocalDateTime eventStartTime,
                           String targetCalendarName, LocalDateTime newStartTime) {
    if (currentCalendar == null) {
      return false;
    }

    CalendarInstance targetCalendar = calendars.get(targetCalendarName);
    if (targetCalendar == null) {
      return false;
    }

    return eventCopyService.copyEvent(eventName, eventStartTime,
            currentCalendar, targetCalendar, newStartTime);
  }

  /**
   * Copies all events from a specific date from current calendar to target calendar.
   *
   * @param sourceDate         the date to copy events from
   * @param targetCalendarName the target calendar name
   * @param targetDate         the target date
   * @return true if events were copied successfully
   */
  @Override
  public boolean copyEventsOnDate(LocalDate sourceDate, String targetCalendarName,
                                  LocalDate targetDate) {
    if (currentCalendar == null) {
      return false;
    }

    CalendarInstance targetCalendar = calendars.get(targetCalendarName);
    if (targetCalendar == null) {
      return false;
    }

    return eventCopyService.copyEventsOnDate(sourceDate, currentCalendar,
            targetCalendar, targetDate);
  }

  /**
   * Copies events within a date range from current calendar to target calendar.
   *
   * @param startDate          the start date of the range
   * @param endDate            the end date of the range
   * @param targetCalendarName the target calendar name
   * @param targetStartDate    the target start date
   * @return true if events were copied successfully
   */
  @Override
  public boolean copyEventsInRange(LocalDate startDate, LocalDate endDate,
                                   String targetCalendarName, LocalDate targetStartDate) {
    if (currentCalendar == null) {
      return false;
    }

    CalendarInstance targetCalendar = calendars.get(targetCalendarName);
    if (targetCalendar == null) {
      return false;
    }

    return eventCopyService.copyEventsInRange(startDate, endDate, currentCalendar,
            targetCalendar, targetStartDate);
  }

  /**
   * Creates an event in the current calendar.
   * Delegates to the current CalendarInstance.
   */
  @Override
  public boolean createEvent(String subject, LocalDateTime startDateTime,
                             LocalDateTime endDateTime, String description, String location,
                             EventStatus status) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createEvent(subject, startDateTime, endDateTime, description,
            location, status);
  }

  /**
   * Added createEvent overload with minimal parameters.
   */
  public boolean createEvent(String subject, LocalDateTime startDateTime,
                             LocalDateTime endDateTime) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createEvent(subject, startDateTime, endDateTime);
  }

  /**
   * Added createAllDayEvent method.
   */
  public boolean createAllDayEvent(String subject, LocalDate date, String description,
                                   String location, EventStatus status) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createAllDayEvent(subject, date, description, location, status);
  }

  /**
   * Added createAllDayEvent overload with minimal parameters.
   */
  public boolean createAllDayEvent(String subject, LocalDate date) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createAllDayEvent(subject, date);
  }

  /**
   * Added createEventSeries method.
   */
  public boolean createEventSeries(String subject, LocalDateTime startDateTime,
                                   LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                   int occurrences, String description, String location,
                                   EventStatus status) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createEventSeries(subject, startDateTime, endDateTime, weekdays,
            occurrences, description, location, status,
            ++globalSeriesCounter);
  }

  /**
   * Added createEventSeries overload with minimal parameters.
   */
  public boolean createEventSeries(String subject, LocalDateTime startDateTime,
                                   LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                   int occurrences) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createEventSeries(subject, startDateTime, endDateTime, weekdays,
            occurrences, null, null, EventStatus.PUBLIC,
            ++globalSeriesCounter);
  }

  /**
   * Added createEventSeriesUntil method.
   */
  public boolean createEventSeriesUntil(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                        LocalDate endDate, String description, String location,
                                        EventStatus status) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createEventSeriesUntil(subject, startDateTime, endDateTime,
            weekdays, endDate, description, location,
            status, ++globalSeriesCounter);
  }

  /**
   * Added createEventSeriesUntil overload with minimal parameters.
   */
  public boolean createEventSeriesUntil(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                        LocalDate endDate) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createEventSeriesUntil(subject, startDateTime, endDateTime,
            weekdays, endDate, null, null,
            EventStatus.PUBLIC, ++globalSeriesCounter);
  }

  /**
   * Added createAllDayEventSeries method.
   */
  public boolean createAllDayEventSeries(String subject, LocalDate startDate,
                                         Set<DayOfWeek> weekdays, int occurrences,
                                         String description, String location,
                                         EventStatus status) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createAllDayEventSeries(subject, startDate, weekdays, occurrences,
            description, location, status,
            ++globalSeriesCounter);
  }

  /**
   * Added createAllDayEventSeries overload with minimal parameters.
   */
  public boolean createAllDayEventSeries(String subject, LocalDate startDate,
                                         Set<DayOfWeek> weekdays, int occurrences) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createAllDayEventSeries(subject, startDate, weekdays, occurrences,
            null, null, EventStatus.PUBLIC,
            ++globalSeriesCounter);
  }

  /**
   * Added createAllDayEventSeriesUntil method.
   */
  public boolean createAllDayEventSeriesUntil(String subject, LocalDate startDate,
                                              Set<DayOfWeek> weekdays, LocalDate endDate,
                                              String description, String location,
                                              EventStatus status) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createAllDayEventSeriesUntil(subject, startDate, weekdays, endDate,
            description, location, status,
            ++globalSeriesCounter);
  }

  /**
   * Added createAllDayEventSeriesUntil overload with minimal parameters.
   */
  public boolean createAllDayEventSeriesUntil(String subject, LocalDate startDate,
                                              Set<DayOfWeek> weekdays, LocalDate endDate) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.createAllDayEventSeriesUntil(subject, startDate, weekdays, endDate,
            null, null, EventStatus.PUBLIC,
            ++globalSeriesCounter);
  }

  /**
   * Gets events on a date from the current calendar.
   */
  @Override
  public List<Event> getEventsOnDate(LocalDate date) {
    if (currentCalendar == null) {
      return new ArrayList<>();
    }
    return currentCalendar.getEventsOnDate(date);
  }

  /**
   * Added getEventsInRange method.
   */
  public List<Event> getEventsInRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (currentCalendar == null) {
      return new ArrayList<>();
    }
    return currentCalendar.getEventsInRange(startDateTime, endDateTime);
  }

  /**
   * Added isBusy method.
   */
  public boolean isBusy(LocalDateTime dateTime) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.isBusy(dateTime);
  }

  /**
   * Added findEvent method.
   */
  public Event findEvent(String subject, LocalDateTime startDateTime,
                         LocalDateTime endDateTime) {
    if (currentCalendar == null) {
      return null;
    }
    return currentCalendar.findEvent(subject, startDateTime, endDateTime);
  }

  /**
   * Added findEventBySubjectAndStart method.
   */
  public Event findEventBySubjectAndStart(String subject, LocalDateTime startDateTime) {
    if (currentCalendar == null) {
      return null;
    }
    return currentCalendar.findEventBySubjectAndStart(subject, startDateTime);
  }

  /**
   * Edits an event in the current calendar.
   */
  @Override
  public boolean editEvent(String property, String subject, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, String newValue) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.editEvent(property, subject, startDateTime, endDateTime, newValue);
  }

  /**
   * Added editEventsFromDate method.
   */
  public boolean editEventsFromDate(String property, String subject,
                                    LocalDateTime startDateTime, String newValue) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.editEventsFromDate(property, subject, startDateTime, newValue);
  }

  /**
   * Added editEntireSeries method.
   */
  public boolean editEntireSeries(String property, String subject, LocalDateTime startDateTime,
                                  String newValue) {
    if (currentCalendar == null) {
      return false;
    }
    return currentCalendar.editEntireSeries(property, subject, startDateTime, newValue);
  }

  /**
   * Added getAllEvents method.
   */
  public Set<Event> getAllEvents() {
    if (currentCalendar == null) {
      return new HashSet<>();
    }
    return currentCalendar.getAllEvents();
  }

  /**
   * Sets the event copy service. Useful for dependency injection or testing.
   *
   * @param eventCopyService the event copy service to use
   */
  public void setEventCopyService(IEventCopyService eventCopyService) {
    this.eventCopyService = eventCopyService;
  }

  /**
   * Gets the current event copy service.
   *
   * @return the current event copy service
   */
  public IEventCopyService getEventCopyService() {
    return eventCopyService;
  }

  // Helper methods

  /**
   * Edits the name of a calendar and updates internal mappings.
   */
  private boolean editCalendarName(String oldName, String newName) {
    if (calendars.containsKey(newName)) {
      return false;
    }

    CalendarInstance calendar = calendars.remove(oldName);
    if (calendar == null) {
      return false;
    }

    calendar.setName(newName);
    calendars.put(newName, calendar);

    if (oldName.equals(currentCalendarName)) {
      currentCalendarName = newName;
    }

    return true;
  }

  /**
   * Returns a string representation of the calendar manager state.
   */
  @Override
  public String toString() {
    return String.format("CalendarManager{calendars=%d, current='%s'}",
            calendars.size(), currentCalendarName);
  }
}