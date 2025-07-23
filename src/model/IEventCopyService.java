package model;


import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Interface for copying events between calendar instances.
 * Defines the contract for event copying operations including timezone conversions
 * and maintaining event series relationships.
 */
public interface IEventCopyService {

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
  boolean copyEvent(String eventName, LocalDateTime eventStartTime,
                    CalendarInstance sourceCalendar, CalendarInstance targetCalendar,
                    LocalDateTime newStartTime);

  /**
   * Copies all events scheduled on a specific date from source to target calendar.
   *
   * @param sourceDate     the date to copy events from (in source calendar's timezone)
   * @param sourceCalendar the source calendar instance
   * @param targetCalendar the target calendar instance
   * @param targetDate     the target date (in target calendar's timezone)
   * @return true if all events were copied successfully, false if any failed
   */
  boolean copyEventsOnDate(LocalDate sourceDate, CalendarInstance sourceCalendar,
                           CalendarInstance targetCalendar, LocalDate targetDate);

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
  boolean copyEventsInRange(LocalDate startDate, LocalDate endDate,
                            CalendarInstance sourceCalendar, CalendarInstance targetCalendar,
                            LocalDate targetStartDate);
}