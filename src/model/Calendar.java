package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of ICalendar interface.
 * Main Calendar class representing the MODEL component in MVC architecture.
 * Manages all calendar operations including event creation, editing, and querying.
 * Enhanced with better duplicate detection and proper MVC separation.
 * Fixed to work with the original EventSeries class.
 *
 * @author Calendar Application Team
 * @version 1.0
 */
public class Calendar implements ICalendar {
  /**
   * Set of all events in the calendar.
   */
  private Set<Event> events;

  /**
   * Map of event series indexed by series ID.
   */
  private Map<String, EventSeries> eventSeries;

  /**
   * Counter for generating unique series IDs.
   */
  private int seriesCounter;

  /**
   * Creates a new empty calendar.
   * Initializes internal data structures for events and event series.
   */
  public Calendar() {
    this.events = new HashSet<>();
    this.eventSeries = new HashMap<>();
    this.seriesCounter = 0;
  }

  @Override
  public boolean createEvent(String subject, LocalDateTime startDateTime,
                             LocalDateTime endDateTime, String description,
                             String location, EventStatus status) {
    Event event = new Event(subject, startDateTime, endDateTime, description, location, status);

    for (Event existing : events) {
      if (existing.conflictsWith(event)) {
        return false;
      }
    }

    events.add(event);
    return true;
  }

  @Override
  public boolean createEvent(String subject, LocalDateTime startDateTime,
                             LocalDateTime endDateTime) {
    return createEvent(subject, startDateTime, endDateTime, null, null, EventStatus.PUBLIC);
  }

  @Override
  public boolean createAllDayEvent(String subject, LocalDate date, String description,
                                   String location, EventStatus status) {
    Event event = new Event(subject, date, description, location, status);

    for (Event existing : events) {
      if (existing.conflictsWith(event)) {
        return false;
      }
    }

    events.add(event);
    return true;
  }

  @Override
  public boolean createAllDayEvent(String subject, LocalDate date) {
    return createAllDayEvent(subject, date, null, null, EventStatus.PUBLIC);
  }

  @Override
  public boolean createEventSeries(String subject, LocalDateTime startDateTime,
                                   LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                   int occurrences, String description, String location,
                                   EventStatus status) {
    String seriesId = "series_" + (++seriesCounter);
    EventSeries series = new EventSeries(seriesId, weekdays, startDateTime.toLocalTime(),
            endDateTime.toLocalTime(), false);

    List<Event> eventsToAdd = new ArrayList<>();
    LocalDate currentDate = startDateTime.toLocalDate();
    int created = 0;

    while (created < occurrences) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime eventStart = currentDate.atTime(startDateTime.toLocalTime());
        LocalDateTime eventEnd = currentDate.atTime(endDateTime.toLocalTime());
        Event event = new Event(subject, eventStart, eventEnd, description, location, status);
        event.setSeriesId(seriesId);

        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            return false;
          }
        }

        eventsToAdd.add(event);
        created++;
      }
      currentDate = currentDate.plusDays(1);
    }

    eventSeries.put(seriesId, series);
    events.addAll(eventsToAdd);
    return true;
  }

  @Override
  public boolean createEventSeries(String subject, LocalDateTime startDateTime,
                                   LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                   int occurrences) {
    return createEventSeries(subject, startDateTime, endDateTime, weekdays, occurrences,
            null, null, EventStatus.PUBLIC);
  }

  @Override
  public boolean createEventSeriesUntil(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                        LocalDate endDate, String description,
                                        String location, EventStatus status) {
    String seriesId = "series_" + (++seriesCounter);
    EventSeries series = new EventSeries(seriesId, weekdays, startDateTime.toLocalTime(),
            endDateTime.toLocalTime(), false);

    List<Event> eventsToAdd = new ArrayList<>();
    LocalDate currentDate = startDateTime.toLocalDate();

    while (!currentDate.isAfter(endDate)) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        LocalDateTime eventStart = currentDate.atTime(startDateTime.toLocalTime());
        LocalDateTime eventEnd = currentDate.atTime(endDateTime.toLocalTime());
        Event event = new Event(subject, eventStart, eventEnd, description, location, status);
        event.setSeriesId(seriesId);

        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            return false;
          }
        }

        eventsToAdd.add(event);
      }
      currentDate = currentDate.plusDays(1);
    }

    eventSeries.put(seriesId, series);
    events.addAll(eventsToAdd);
    return true;
  }

  @Override
  public boolean createEventSeriesUntil(String subject, LocalDateTime startDateTime,
                                        LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                        LocalDate endDate) {
    return createEventSeriesUntil(subject, startDateTime, endDateTime, weekdays, endDate,
            null, null, EventStatus.PUBLIC);
  }

  @Override
  public boolean createAllDayEventSeries(String subject, LocalDate startDate,
                                         Set<DayOfWeek> weekdays, int occurrences,
                                         String description, String location,
                                         EventStatus status) {
    String seriesId = "series_" + (++seriesCounter);
    EventSeries series = new EventSeries(seriesId, weekdays, LocalTime.of(8, 0),
            LocalTime.of(17, 0), true);

    List<Event> eventsToAdd = new ArrayList<>();
    LocalDate currentDate = startDate;
    int created = 0;

    while (created < occurrences) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        Event event = new Event(subject, currentDate, description, location, status);
        event.setSeriesId(seriesId);

        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            return false;
          }
        }

        eventsToAdd.add(event);
        created++;
      }
      currentDate = currentDate.plusDays(1);
    }

    eventSeries.put(seriesId, series);
    events.addAll(eventsToAdd);
    return true;
  }

  @Override
  public boolean createAllDayEventSeries(String subject, LocalDate startDate,
                                         Set<DayOfWeek> weekdays, int occurrences) {
    return createAllDayEventSeries(subject, startDate, weekdays, occurrences,
            null, null, EventStatus.PUBLIC);
  }

  @Override
  public boolean createAllDayEventSeriesUntil(String subject, LocalDate startDate,
                                              Set<DayOfWeek> weekdays, LocalDate endDate,
                                              String description, String location,
                                              EventStatus status) {
    String seriesId = "series_" + (++seriesCounter);
    EventSeries series = new EventSeries(seriesId, weekdays, LocalTime.of(8, 0),
            LocalTime.of(17, 0), true);

    List<Event> eventsToAdd = new ArrayList<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      if (weekdays.contains(currentDate.getDayOfWeek())) {
        Event event = new Event(subject, currentDate, description, location, status);
        event.setSeriesId(seriesId);

        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            return false;
          }
        }

        eventsToAdd.add(event);
      }
      currentDate = currentDate.plusDays(1);
    }

    eventSeries.put(seriesId, series);
    events.addAll(eventsToAdd);
    return true;
  }

  @Override
  public boolean createAllDayEventSeriesUntil(String subject, LocalDate startDate,
                                              Set<DayOfWeek> weekdays, LocalDate endDate) {
    return createAllDayEventSeriesUntil(subject, startDate, weekdays, endDate,
            null, null, EventStatus.PUBLIC);
  }

  @Override
  public Event findEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    for (Event event : events) {
      if (event.getSubject().equals(subject)
              && event.getStartDateTime().equals(startDateTime)
              && event.getEndDateTime().equals(endDateTime)) {
        return event;
      }
    }
    return null;
  }

  @Override
  public Event findEventBySubjectAndStart(String subject, LocalDateTime startDateTime) {
    for (Event event : events) {
      if (event.getSubject().equals(subject)
              && event.getStartDateTime().equals(startDateTime)) {
        return event;
      }
    }
    return null;
  }

  @Override
  public boolean editEvent(String property, String subject, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, String newValue) {
    Event event = findEvent(subject, startDateTime, endDateTime);
    if (event == null) {
      return false;
    }
    return updateEventProperty(event, property, newValue);
  }

  @Override
  public boolean editEventsFromDate(String property, String subject,
                                    LocalDateTime startDateTime, String newValue) {
    Event targetEvent = findEventBySubjectAndStart(subject, startDateTime);
    if (targetEvent == null) {
      return false;
    }

    if (targetEvent.getSeriesId() == null) {
      return updateEventProperty(targetEvent, property, newValue);
    }

    String seriesId = targetEvent.getSeriesId();
    boolean isTimeChange = "start".equals(property);

    for (Event event : events) {
      if (seriesId.equals(event.getSeriesId())
              && !event.getStartDateTime().isBefore(startDateTime)) {
        updateEventProperty(event, property, newValue);

        if (isTimeChange) {
          event.setSeriesId(null);
        }
      }
    }

    return true;
  }

  @Override
  public boolean editEntireSeries(String property, String subject,
                                  LocalDateTime startDateTime, String newValue) {
    Event targetEvent = findEventBySubjectAndStart(subject, startDateTime);
    if (targetEvent == null) {
      return false;
    }

    if (targetEvent.getSeriesId() == null) {
      return updateEventProperty(targetEvent, property, newValue);
    }

    String seriesId = targetEvent.getSeriesId();
    boolean isTimeChange = "start".equals(property);

    for (Event event : events) {
      if (seriesId.equals(event.getSeriesId())) {
        updateEventProperty(event, property, newValue);

        if (isTimeChange) {
          event.setSeriesId(null);
        }
      }
    }

    return true;
  }

  /**
   * Updates a single property of an event.
   *
   * @param event the event to update (required)
   * @param property the property name to update (required)
   * @param newValue the new value as string (required)
   * @return true if property was updated successfully, false if invalid property or value
   */
  private boolean updateEventProperty(Event event, String property, String newValue) {
    try {
      switch (property.toLowerCase()) {
        case "subject":
          event.setSubject(newValue);
          break;
        case "start":
          event.setStartDateTime(parseDateTime(newValue));
          break;
        case "end":
          event.setEndDateTime(parseDateTime(newValue));
          break;
        case "description":
          event.setDescription(newValue);
          break;
        case "location":
          event.setLocation(newValue);
          break;
        case "status":
          event.setStatus(newValue);
          break;
        default:
          return false;
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public List<Event> getEventsOnDate(LocalDate date) {
    List<Event> dayEvents = new ArrayList<>();
    for (Event event : events) {
      if (event.occursOnDate(date)) {
        dayEvents.add(event);
      }
    }
    dayEvents.sort(Comparator.comparing(Event::getStartDateTime));
    return dayEvents;
  }

  @Override
  public List<Event> getEventsInRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    List<Event> rangeEvents = new ArrayList<>();
    for (Event event : events) {
      if (!(event.getEndDateTime().isBefore(startDateTime)
              || event.getStartDateTime().isAfter(endDateTime))) {
        rangeEvents.add(event);
      }
    }
    rangeEvents.sort(Comparator.comparing(Event::getStartDateTime));
    return rangeEvents;
  }

  @Override
  public boolean isBusy(LocalDateTime dateTime) {
    for (Event event : events) {
      if (event.isActiveAt(dateTime)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Set<Event> getAllEvents() {
    return new HashSet<>(events);
  }

  /**
   * Parses a date-time string in the expected format.
   *
   * @param dateTimeStr the date-time string in format "yyyy-MM-dd'T'HH:mm"
   * @return the parsed LocalDateTime object
   * @throws java.time.format.DateTimeParseException if the string format is invalid
   */
  private LocalDateTime parseDateTime(String dateTimeStr) {
    return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
  }
}