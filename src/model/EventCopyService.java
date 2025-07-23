package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the IEventCopyService interface.
 * Handles timezone conversions and maintains event series relationships.
 */
public class EventCopyService implements IEventCopyService {

  /**
   * Copies a single event from source calendar to target calendar at the specified time.
   *
   * @param eventName      the name/subject of the event to copy
   * @param eventStartTime the start time of the event in source calendar
   * @param sourceCalendar the source calendar instance
   * @param targetCalendar the target calendar instance
   * @param newStartTime   the new start time in target calendar's timezone
   * @return true if the event was copied successfully, false otherwise
   */
  @Override
  public boolean copyEvent(String eventName, LocalDateTime eventStartTime,
                           CalendarInstance sourceCalendar, CalendarInstance targetCalendar,
                           LocalDateTime newStartTime) {

    Event sourceEvent = sourceCalendar.findEventBySubjectAndStart(eventName, eventStartTime);
    if (sourceEvent == null) {
      return false;
    }

    Duration eventDuration = Duration.between(sourceEvent.getStartDateTime(),
            sourceEvent.getEndDateTime());

    LocalDateTime convertedStartTime = convertTimeBetweenTimezones(
            newStartTime,
            sourceCalendar.getTimezone(),
            targetCalendar.getTimezone(),
            newStartTime.toLocalDate()
    );

    LocalDateTime convertedEndTime = convertedStartTime.plus(eventDuration);

    Event newEvent = createEventCopy(sourceEvent, convertedStartTime, convertedEndTime);

    return targetCalendar.addEvent(newEvent);
  }

  /**
   * Copies all events scheduled on a specific date from source to target calendar.
   *
   * @param sourceDate     the date to copy events from (in source calendar's timezone)
   * @param sourceCalendar the source calendar instance
   * @param targetCalendar the target calendar instance
   * @param targetDate     the target date (in target calendar's timezone)
   * @return true if all events were copied successfully, false if any failed
   */
  @Override
  public boolean copyEventsOnDate(LocalDate sourceDate, CalendarInstance sourceCalendar,
                                  CalendarInstance targetCalendar, LocalDate targetDate) {

    List<Event> eventsOnDate = sourceCalendar.getEventsOnDate(sourceDate);
    if (eventsOnDate.isEmpty()) {
      return true;
    }

    boolean allSuccessful = true;

    for (Event sourceEvent : eventsOnDate) {
      LocalDateTime newStart = convertTimeBetweenTimezones(
              sourceEvent.getStartDateTime(),
              sourceCalendar.getTimezone(),
              targetCalendar.getTimezone(),
              targetDate
      );

      LocalDateTime newEnd = convertTimeBetweenTimezones(
              sourceEvent.getEndDateTime(),
              sourceCalendar.getTimezone(),
              targetCalendar.getTimezone(),
              targetDate
      );

      Event newEvent = createEventCopy(sourceEvent, newStart, newEnd);

      if (!targetCalendar.addEvent(newEvent)) {
        allSuccessful = false;
      }
    }

    return allSuccessful;
  }

  /**
   * Copies all events within a date range from source to target calendar.
   *
   * @param startDate       the start date of the range (inclusive, in source calendar's timezone)
   * @param endDate         the end date of the range (inclusive, in source calendar's timezone)
   * @param sourceCalendar  the source calendar instance
   * @param targetCalendar  the target calendar instance
   * @param targetStartDate the target start date (in target calendar's timezone)
   * @return true if all events were copied successfully, false if any failed
   */
  @Override
  public boolean copyEventsInRange(LocalDate startDate, LocalDate endDate,
                                   CalendarInstance sourceCalendar,
                                   CalendarInstance targetCalendar,
                                   LocalDate targetStartDate) {
    LocalDateTime rangeStart = startDate.atStartOfDay();
    LocalDateTime rangeEnd = endDate.plusDays(1).atStartOfDay();
    List<Event> eventsInRange = sourceCalendar.getEventsInRange(rangeStart, rangeEnd);
    if (eventsInRange.isEmpty()) {
      return true;
    }

    List<Event> eventsToProcess = eventsInRange.stream()
            .filter(event -> {
              LocalDate eventDate = event.getStartDateTime().toLocalDate();
              return !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate);
            })
            .collect(Collectors.toList());

    long dayOffset = ChronoUnit.DAYS.between(startDate, targetStartDate);
    boolean allSuccessful = true;
    Map<String, String> seriesIdMapping = new HashMap<>();

    for (Event sourceEvent : eventsToProcess) {
      LocalDateTime newStart = sourceEvent.getStartDateTime().plusDays(dayOffset);
      LocalDateTime newEnd = sourceEvent.getEndDateTime().plusDays(dayOffset);

      newStart = convertTimeBetweenTimezones(newStart, sourceCalendar.getTimezone(),
              targetCalendar.getTimezone(), newStart.toLocalDate());
      newEnd = convertTimeBetweenTimezones(newEnd, sourceCalendar.getTimezone(),
              targetCalendar.getTimezone(), newEnd.toLocalDate());

      Event newEvent = createEventCopy(sourceEvent, newStart, newEnd);

      if (sourceEvent.getSeriesId() != null) {
        String oldSeriesId = sourceEvent.getSeriesId();
        String newSeriesId = seriesIdMapping.computeIfAbsent(oldSeriesId,  k -> "copied-" +
                k + "-" + System.currentTimeMillis());
        newEvent.setSeriesId(newSeriesId);
      }

      if (!targetCalendar.addEvent(newEvent)) {
        allSuccessful = false;
      }
    }

    return allSuccessful;
  }


  /**
   * Creates a copy of an event with new start and end times.
   *
   * @param sourceEvent  the event to copy
   * @param newStartTime the new start time
   * @param newEndTime   the new end time
   * @return a new Event object with copied properties
   */
  private Event createEventCopy(Event sourceEvent, LocalDateTime newStartTime,
                                LocalDateTime newEndTime) {
    Event newEvent;

    if (sourceEvent.isAllDay()) {
      newEvent = new Event(sourceEvent.getSubject(), newStartTime.toLocalDate(),
              sourceEvent.getDescription(), sourceEvent.getLocation(),
              sourceEvent.getStatus());
    } else {
      newEvent = new Event(sourceEvent.getSubject(), newStartTime, newEndTime,
              sourceEvent.getDescription(), sourceEvent.getLocation(),
              sourceEvent.getStatus());
    }

    if (sourceEvent.getSeriesId() != null) {
      newEvent.setSeriesId(sourceEvent.getSeriesId());
    }

    return newEvent;
  }

  /**
   * Converts a time from one timezone to another, preserving the date context.
   *
   * <p>Example: 2pm EST event copied to PST calendar becomes 11am PST
   *
   * @param originalTime   the original time in source timezone
   * @param sourceTimezone the source calendar's timezone
   * @param targetTimezone the target calendar's timezone
   * @param targetDate     the target date context
   * @return the converted time in target timezone
   */
  private LocalDateTime convertTimeBetweenTimezones(LocalDateTime originalTime,
                                                    ZoneId sourceTimezone,
                                                    ZoneId targetTimezone,
                                                    LocalDate targetDate) {
    if (sourceTimezone.equals(targetTimezone)) {
      return targetDate.atTime(originalTime.toLocalTime());
    }

    ZonedDateTime sourceZoned = originalTime.atZone(sourceTimezone);
    ZonedDateTime targetZoned = sourceZoned.withZoneSameInstant(targetTimezone);

    return targetDate.atTime(targetZoned.toLocalTime());
  }
}