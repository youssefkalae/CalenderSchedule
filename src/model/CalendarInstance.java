package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CalendarInstance class representing a single named calendar with timezone support.
 * This class manages events within a specific calendar context and provides
 * all the core calendar functionality that was previously in the main Calendar class.
 *
 * <p>Each CalendarInstance has its own name, timezone, and collection of events.
 * This enables the multi-calendar support in the enhanced Calendar system.
 */
public class CalendarInstance implements ICalendarInstance {
  /**
   * The name of this calendar instance.
   */
  private String name;

  /**
   * The timezone for this calendar instance.
   */
  private ZoneId timezone;

  /**
   * Set of all events in this calendar instance.
   */
  private Set<Event> events;

  /**
   * Map of series IDs to EventSeries objects for managing recurring events.
   */
  private Map<String, EventSeries> eventSeries;

  /**
   * Date/time formatter for parsing.
   */
  private static final DateTimeFormatter DATETIME_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * Creates a new calendar instance with the specified name and timezone.
   *
   * @param name     the name of the calendar
   * @param timezone the timezone for the calendar
   */
  public CalendarInstance(String name, ZoneId timezone) {
    this.name = name;
    this.timezone = timezone;
    this.events = new HashSet<>();
    this.eventSeries = new HashMap<>();
  }

  /**
   * Gets the name of this calendar instance.
   *
   * @return the calendar name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this calendar instance.
   *
   * @param name the new calendar name
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the timezone of this calendar instance.
   *
   * @return the calendar timezone
   */
  @Override
  public ZoneId getTimezone() {
    return timezone;
  }

  /**
   * Sets the timezone of this calendar instance.
   *
   * @param timezone the new timezone
   */
  @Override
  public void setTimezone(ZoneId timezone) {
    this.timezone = timezone;
  }

  /**
   * Adds an event to this calendar instance with proper conflict detection.
   *
   * @param event the event to add
   * @return true if the event was added successfully, false if a duplicate exists
   */
  @Override
  public boolean addEvent(Event event) {
    for (Event existing : events) {
      if (existing.conflictsWith(event)) {
        return false;
      }
    }
    return events.add(event);
  }

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
  @Override
  public boolean createEvent(String subject, LocalDateTime startDateTime,
                             LocalDateTime endDateTime, String description, String location,
                             EventStatus status) {
    Event event = new Event(subject, startDateTime, endDateTime, description, location, status);
    return addEvent(event);
  }

  /**
   * Creates a timed event with minimal properties.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @return true if the event was created successfully
   */
  @Override
  public boolean createEvent(String subject, LocalDateTime startDateTime,
                             LocalDateTime endDateTime) {
    return createEvent(subject, startDateTime, endDateTime, null, null, EventStatus.PUBLIC);
  }

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
  @Override
  public boolean createAllDayEvent(String subject, LocalDate date, String description,
                                   String location, EventStatus status) {
    Event event = new Event(subject, date, description, location, status);
    return addEvent(event);
  }

  /**
   * Creates an all-day event with minimal properties.
   *
   * @param subject event subject
   * @param date    event date
   * @return true if the event was created successfully
   */
  @Override
  public boolean createAllDayEvent(String subject, LocalDate date) {
    return createAllDayEvent(subject, date, null, null, EventStatus.PUBLIC);
  }

  /**
   * Creates a series of timed events with proper conflict checking.
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
  @Override
  public boolean createEventSeries(String subject, LocalDateTime startDateTime,
                                   LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                   int occurrences, String description, String location,
                                   EventStatus status, int seriesId) {
    String seriesIdStr = "series-" + seriesId;

    EventSeries series = new EventSeries(seriesIdStr, weekdays,
            startDateTime.toLocalTime(), endDateTime.toLocalTime(), false);

    List<Event> eventsToAdd = new ArrayList<>();
    LocalDate currentDate = startDateTime.toLocalDate();
    int created = 0;

    while (created < occurrences) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime eventStart = currentDate.atTime(startDateTime.toLocalTime());
        LocalDateTime eventEnd = currentDate.atTime(endDateTime.toLocalTime());
        Event event = new Event(subject, eventStart, eventEnd, description, location, status);
        event.setSeriesId(seriesIdStr);

        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            return false;
          }
        }

        for (Event otherEvent : eventsToAdd) {
          if (otherEvent.conflictsWith(event)) {
            return false;
          }
        }

        eventsToAdd.add(event);
        created++;
      }
      currentDate = currentDate.plusDays(1);
    }

    eventSeries.put(seriesIdStr, series);
    events.addAll(eventsToAdd);
    return true;
  }

  /**
   * Creates a series of timed events until a specific end date with proper conflict checking.
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
  @Override
  public boolean createEventSeriesUntil(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                        LocalDate endDate, String description, String location,
                                        EventStatus status, int seriesId) {
    String seriesIdStr = "series-" + seriesId;

    EventSeries series = new EventSeries(seriesIdStr, weekdays,
            startDateTime.toLocalTime(), endDateTime.toLocalTime(), false);

    List<Event> eventsToAdd = new ArrayList<>();
    LocalDate currentDate = startDateTime.toLocalDate();

    while (!currentDate.isAfter(endDate)) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime eventStart = currentDate.atTime(startDateTime.toLocalTime());
        LocalDateTime eventEnd = currentDate.atTime(endDateTime.toLocalTime());
        Event event = new Event(subject, eventStart, eventEnd, description, location, status);
        event.setSeriesId(seriesIdStr);

        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            return false;
          }
        }

        for (Event otherEvent : eventsToAdd) {
          if (otherEvent.conflictsWith(event)) {
            return false;
          }
        }

        eventsToAdd.add(event);
      }
      currentDate = currentDate.plusDays(1);
    }

    eventSeries.put(seriesIdStr, series);
    events.addAll(eventsToAdd);
    return true;
  }

  /**
   * Creates a series of all-day events with proper conflict checking.
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
  @Override
  public boolean createAllDayEventSeries(String subject, LocalDate startDate,
                                         Set<DayOfWeek> weekdays, int occurrences,
                                         String description, String location,
                                         EventStatus status, int seriesId) {
    String seriesIdStr = "series-" + seriesId;

    EventSeries series = new EventSeries(seriesIdStr, weekdays,
            LocalTime.of(8, 0), LocalTime.of(17, 0), true);

    List<Event> eventsToAdd = new ArrayList<>();
    LocalDate currentDate = startDate;
    int created = 0;

    while (created < occurrences) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        Event event = new Event(subject, currentDate, description, location, status);
        event.setSeriesId(seriesIdStr);

        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            return false;
          }
        }

        for (Event otherEvent : eventsToAdd) {
          if (otherEvent.conflictsWith(event)) {
            return false;
          }
        }

        eventsToAdd.add(event);
        created++;
      }
      currentDate = currentDate.plusDays(1);
    }

    eventSeries.put(seriesIdStr, series);
    events.addAll(eventsToAdd);
    return true;
  }

  /**
   * Creates a series of all-day events until a specific end date with proper conflict checking.
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
  @Override
  public boolean createAllDayEventSeriesUntil(String subject, LocalDate startDate,
                                              Set<DayOfWeek> weekdays, LocalDate endDate,
                                              String description, String location,
                                              EventStatus status, int seriesId) {
    String seriesIdStr = "series-" + seriesId;

    EventSeries series = new EventSeries(seriesIdStr, weekdays,
            LocalTime.of(8, 0), LocalTime.of(17, 0), true);

    List<Event> eventsToAdd = new ArrayList<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        Event event = new Event(subject, currentDate, description, location, status);
        event.setSeriesId(seriesIdStr);

        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            return false;
          }
        }

        for (Event otherEvent : eventsToAdd) {
          if (otherEvent.conflictsWith(event)) {
            return false;
          }
        }

        eventsToAdd.add(event);
      }
      currentDate = currentDate.plusDays(1);
    }

    eventSeries.put(seriesIdStr, series);
    events.addAll(eventsToAdd);
    return true;
  }

  /**
   * Finds an event by subject, start time, and end time.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @return the event if found, null otherwise
   */
  @Override
  public Event findEvent(String subject, LocalDateTime startDateTime,
                         LocalDateTime endDateTime) {
    return events.stream()
            .filter(event -> Objects.equals(event.getSubject(), subject)
                    && Objects.equals(event.getStartDateTime(), startDateTime)
                    && Objects.equals(event.getEndDateTime(), endDateTime))
            .findFirst()
            .orElse(null);
  }

  /**
   * Finds an event by subject and start time.
   *
   * @param subject       event subject
   * @param startDateTime start date and time
   * @return the event if found, null otherwise
   */
  @Override
  public Event findEventBySubjectAndStart(String subject, LocalDateTime startDateTime) {
    return events.stream()
            .filter(event -> Objects.equals(event.getSubject(), subject)
                    && Objects.equals(event.getStartDateTime(), startDateTime))
            .findFirst()
            .orElse(null);
  }

  /**
   * Gets all events occurring on a specific date.
   *
   * @param date the date to search for
   * @return list of events on the specified date
   */
  @Override
  public List<Event> getEventsOnDate(LocalDate date) {
    return events.stream()
            .filter(event -> event.occursOnDate(date))
            .sorted(Comparator.comparing(Event::getStartDateTime))
            .collect(Collectors.toList());
  }

  /**
   * Gets all events within a specific date/time range.
   *
   * @param startDateTime start of the range
   * @param endDateTime   end of the range
   * @return list of events in the specified range
   */
  @Override
  public List<Event> getEventsInRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return events.stream()
            .filter(event -> !event.getEndDateTime().isBefore(startDateTime)
                    && !event.getStartDateTime().isAfter(endDateTime))
            .sorted(Comparator.comparing(Event::getStartDateTime))
            .collect(Collectors.toList());
  }

  /**
   * Checks if the calendar is busy at a specific date/time.
   *
   * @param dateTime the date/time to check
   * @return true if there are active events at the specified time
   */
  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    return events.stream()
            .anyMatch(event -> event.isActiveAt(dateTime));
  }

  /**
   * Gets all events in this calendar instance.
   *
   * @return set of all events
   */
  @Override
  public Set<Event> getAllEvents() {
    return new HashSet<>(events);
  }

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
  @Override
  public boolean editEvent(String property, String subject, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, String newValue) {
    Event event = findEvent(subject, startDateTime, endDateTime);
    if (event == null) {
      return false;
    }

    return updateEventProperty(event, property, newValue);
  }

  /**
   * Edits all events in a series starting from a specific date.
   *
   * @param property      the property to edit
   * @param subject       event subject
   * @param startDateTime starting date/time
   * @param newValue      the new value for the property
   * @return true if events were edited successfully
   */
  @Override
  public boolean editEventsFromDate(String property, String subject,
                                    LocalDateTime startDateTime, String newValue) {
    Event startEvent = findEventBySubjectAndStart(subject, startDateTime);
    if (startEvent == null || startEvent.getSeriesId() == null) {
      return false;
    }

    String seriesId = startEvent.getSeriesId();
    LocalDate startDate = startDateTime.toLocalDate();

    List<Event> eventsToEdit = events.stream()
            .filter(event -> Objects.equals(event.getSeriesId(), seriesId)
                    && Objects.equals(event.getSubject(), subject)
                    && !event.getStartDateTime().toLocalDate().isBefore(startDate))
            .collect(Collectors.toList());

    for (Event event : eventsToEdit) {
      if (!updateEventProperty(event, property, newValue)) {
        return false;
      }
    }

    return !eventsToEdit.isEmpty();
  }

  /**
   * Edits all events in an entire series.
   *
   * @param property      the property to edit
   * @param subject       event subject
   * @param startDateTime any event's start time in the series
   * @param newValue      the new value for the property
   * @return true if events were edited successfully
   */
  @Override
  public boolean editEntireSeries(String property, String subject,
                                  LocalDateTime startDateTime, String newValue) {
    Event referenceEvent = findEventBySubjectAndStart(subject, startDateTime);
    if (referenceEvent == null || referenceEvent.getSeriesId() == null) {
      return false;
    }

    String seriesId = referenceEvent.getSeriesId();

    List<Event> eventsToEdit = events.stream()
            .filter(event -> Objects.equals(event.getSeriesId(), seriesId)
                    && Objects.equals(event.getSubject(), subject))
            .collect(Collectors.toList());

    for (Event event : eventsToEdit) {
      if (!updateEventProperty(event, property, newValue)) {
        return false;
      }
    }

    return !eventsToEdit.isEmpty();
  }

  /**
   * Updates a specific property of an event with improved datetime parsing.
   *
   * @param event    the event to update
   * @param property the property to update
   * @param newValue the new value
   * @return true if the property was updated successfully
   */
  private boolean updateEventProperty(Event event, String property, String newValue) {
    switch (property.toLowerCase()) {
      case "subject":
        event.setSubject(newValue);
        return true;
      case "start":
        try {
          LocalDateTime newStart = LocalDateTime.parse(newValue, DATETIME_FORMATTER);
          event.setStartDateTime(newStart);
          return true;
        } catch (Exception e) {
          return false;
        }
      case "end":
        try {
          LocalDateTime newEnd = LocalDateTime.parse(newValue, DATETIME_FORMATTER);
          event.setEndDateTime(newEnd);
          return true;
        } catch (Exception e) {
          return false;
        }
      case "description":
        event.setDescription(newValue);
        return true;
      case "location":
        event.setLocation(newValue);
        return true;
      case "status":
        event.setStatus(newValue);
        return true;
      default:
        return false;
    }
  }

  /**
   * Returns a string representation of this calendar instance.
   *
   * @return formatted string with calendar name, timezone, and event count
   */
  @Override
  public String toString() {
    return String.format("CalendarInstance{name='%s', timezone=%s, events=%d}",
            name, timezone, events.size());
  }
}