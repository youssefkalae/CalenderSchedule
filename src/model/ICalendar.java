package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Interface for Calendar operations.
 * Defines the contract for calendar management including event creation,
 * editing, and querying operations.
 */
public interface ICalendar {

  boolean createEvent(String subject, LocalDateTime startDateTime,
                      LocalDateTime endDateTime, String description,
                      String location, EventStatus status);

  boolean createEvent(String subject, LocalDateTime startDateTime,
                      LocalDateTime endDateTime);

  boolean createAllDayEvent(String subject, LocalDate date, String description,
                            String location, EventStatus status);

  boolean createAllDayEvent(String subject, LocalDate date);

  boolean createEventSeries(String subject, LocalDateTime startDateTime,
                            LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                            int occurrences, String description, String location,
                            EventStatus status);

  boolean createEventSeries(String subject, LocalDateTime startDateTime,
                            LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                            int occurrences);

  boolean createEventSeriesUntil(String subject, LocalDateTime startDateTime,
                                 LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                 LocalDate endDate, String description,
                                 String location, EventStatus status);

  boolean createEventSeriesUntil(String subject, LocalDateTime startDateTime,
                                 LocalDateTime endDateTime, Set<DayOfWeek> weekdays,
                                 LocalDate endDate);


  boolean createAllDayEventSeries(String subject, LocalDate startDate,
                                  Set<DayOfWeek> weekdays, int occurrences,
                                  String description, String location,
                                  EventStatus status);

  boolean createAllDayEventSeries(String subject, LocalDate startDate,
                                  Set<DayOfWeek> weekdays, int occurrences);

  boolean createAllDayEventSeriesUntil(String subject, LocalDate startDate,
                                       Set<DayOfWeek> weekdays, LocalDate endDate,
                                       String description, String location,
                                       EventStatus status);

  boolean createAllDayEventSeriesUntil(String subject, LocalDate startDate,
                                       Set<DayOfWeek> weekdays, LocalDate endDate);

  Event findEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime);

  Event findEventBySubjectAndStart(String subject, LocalDateTime startDateTime);

  boolean editEvent(String property, String subject, LocalDateTime startDateTime,
                    LocalDateTime endDateTime, String newValue);

  boolean editEventsFromDate(String property, String subject,
                             LocalDateTime startDateTime, String newValue);

  boolean editEntireSeries(String property, String subject,
                           LocalDateTime startDateTime, String newValue);

  List<Event> getEventsOnDate(LocalDate date);

  List<Event> getEventsInRange(LocalDateTime startDateTime, LocalDateTime endDateTime);

  boolean isBusy(LocalDateTime dateTime);

  Set<Event> getAllEvents();
}