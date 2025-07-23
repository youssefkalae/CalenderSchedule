import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.CalendarInstance;
import model.CalendarManager;
import model.Event;
import model.EventCopyService;
import model.EventStatus;
import model.IEventCopyService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for CalendarManager functionality.
 */
class CalendarManagerTest {

  private CalendarManager calendarManager;
  private IEventCopyService mockCopyService;

  /**
   * Set up test environment before each test.
   */
  @BeforeEach
  void setUp() {
    calendarManager = new CalendarManager();
    mockCopyService = new EventCopyService();
  }

  /**
   * Test calendar manager creation with default constructor.
   */
  @Test
  @DisplayName("Test calendar manager creation with default constructor")
  void testCalendarManagerCreation() {
    assertNotNull(calendarManager);
    assertTrue(calendarManager.getCalendarNames().isEmpty());
    assertNull(calendarManager.getCurrentCalendar());
    assertNull(calendarManager.getCurrentCalendarName());
  }

  /**
   * Test calendar manager creation with custom event copy service.
   */
  @Test
  @DisplayName("Test calendar manager creation with custom event copy service")
  void testCalendarManagerWithCustomCopyService() {
    CalendarManager manager = new CalendarManager(mockCopyService);
    assertNotNull(manager);
    assertEquals(mockCopyService, manager.getEventCopyService());
  }

  /**
   * Test creating a calendar successfully.
   */
  @Test
  @DisplayName("Test create calendar successfully")
  void testCreateCalendarSuccess() {
    ZoneId timezone = ZoneId.of("America/New_York");
    boolean result = calendarManager.createCalendar("work", timezone);

    assertTrue(result);
    assertTrue(calendarManager.calendarExists("work"));
    assertEquals(1, calendarManager.getCalendarNames().size());
  }

  /**
   * Test creating duplicate calendar fails.
   */
  @Test
  @DisplayName("Test create duplicate calendar fails")
  void testCreateDuplicateCalendar() {
    ZoneId timezone = ZoneId.of("America/New_York");
    calendarManager.createCalendar("work", timezone);

    boolean result = calendarManager.createCalendar("work", ZoneId.of("Europe/London"));
    assertFalse(result);
    assertEquals(1, calendarManager.getCalendarNames().size());
  }

  /**
   * Test editing calendar timezone successfully.
   */
  @Test
  @DisplayName("Test edit calendar timezone successfully")
  void testEditCalendarTimezone() {
    ZoneId originalTimezone = ZoneId.of("America/New_York");
    ZoneId newTimezone = ZoneId.of("Europe/London");

    calendarManager.createCalendar("work", originalTimezone);
    boolean result = calendarManager.editCalendar("work", "timezone", "Europe/London");

    assertTrue(result);
    assertEquals(newTimezone, calendarManager.getCalendar("work").getTimezone());
  }

  /**
   * Test editing calendar name successfully.
   */
  @Test
  @DisplayName("Test edit calendar name successfully")
  void testEditCalendarName() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    boolean result = calendarManager.editCalendar("work", "name", "office");

    assertTrue(result);
    assertTrue(calendarManager.calendarExists("office"));
    assertFalse(calendarManager.calendarExists("work"));
  }

  /**
   * Test editing nonexistent calendar fails.
   */
  @Test
  @DisplayName("Test edit nonexistent calendar fails")
  void testEditNonexistentCalendar() {
    boolean result = calendarManager.editCalendar("nonexistent", "timezone", "Europe/London");
    assertFalse(result);
  }

  /**
   * Test editing calendar with invalid property fails.
   */
  @Test
  @DisplayName("Test edit calendar with invalid property fails")
  void testEditCalendarInvalidProperty() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    boolean result = calendarManager.editCalendar("work", "invalid", "value");
    assertFalse(result);
  }

  /**
   * Test editing calendar with invalid timezone fails.
   */
  @Test
  @DisplayName("Test edit calendar with invalid timezone fails")
  void testEditCalendarInvalidTimezone() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    boolean result = calendarManager.editCalendar("work", "timezone", "Invalid/Timezone");
    assertFalse(result);
  }

  /**
   * Test using calendar successfully.
   */
  @Test
  @DisplayName("Test use calendar successfully")
  void testUseCalendarSuccess() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    boolean result = calendarManager.useCalendar("work");

    assertTrue(result);
    assertEquals("work", calendarManager.getCurrentCalendarName());
    assertNotNull(calendarManager.getCurrentCalendar());
  }

  /**
   * Test using nonexistent calendar fails.
   */
  @Test
  @DisplayName("Test use nonexistent calendar fails")
  void testUseNonexistentCalendar() {
    boolean result = calendarManager.useCalendar("nonexistent");
    assertFalse(result);
    assertNull(calendarManager.getCurrentCalendarName());
    assertNull(calendarManager.getCurrentCalendar());
  }

  /**
   * Test switching between calendars.
   */
  @Test
  @DisplayName("Test switch between calendars")
  void testSwitchBetweenCalendars() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("personal", ZoneId.of("Europe/London"));

    calendarManager.useCalendar("work");
    assertEquals("work", calendarManager.getCurrentCalendarName());

    calendarManager.useCalendar("personal");
    assertEquals("personal", calendarManager.getCurrentCalendarName());
  }

  /**
   * Test getting calendar by name.
   */
  @Test
  @DisplayName("Test get calendar by name")
  void testGetCalendarByName() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    CalendarInstance calendar = calendarManager.getCalendar("work");

    assertNotNull(calendar);
    assertEquals("work", calendar.getName());
    assertEquals(ZoneId.of("America/New_York"), calendar.getTimezone());
  }

  /**
   * Test getting nonexistent calendar returns null.
   */
  @Test
  @DisplayName("Test get nonexistent calendar returns null")
  void testGetNonexistentCalendar() {
    CalendarInstance calendar = calendarManager.getCalendar("nonexistent");
    assertNull(calendar);
  }

  /**
   * Test checking if calendar exists.
   */
  @Test
  @DisplayName("Test calendar exists check")
  void testCalendarExists() {
    assertFalse(calendarManager.calendarExists("work"));

    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    assertTrue(calendarManager.calendarExists("work"));
  }

  /**
   * Test getting all calendar names.
   */
  @Test
  @DisplayName("Test get all calendar names")
  void testGetCalendarNames() {
    assertTrue(calendarManager.getCalendarNames().isEmpty());

    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("personal", ZoneId.of("Europe/London"));

    Set<String> names = calendarManager.getCalendarNames();
    assertEquals(2, names.size());
    assertTrue(names.contains("work"));
    assertTrue(names.contains("personal"));
  }

  /**
   * Test creating event in current calendar.
   */
  @Test
  @DisplayName("Test create event in current calendar")
  void testCreateEventInCurrentCalendar() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    boolean result = calendarManager.createEvent("Meeting", start,
            end, "Description", "Location", EventStatus.PUBLIC);
    assertTrue(result);
    assertEquals(1, calendarManager.getAllEvents().size());
  }

  /**
   * Test creating event without current calendar fails.
   */
  @Test
  @DisplayName("Test create event without current calendar fails")
  void testCreateEventWithoutCurrentCalendar() {
    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    boolean result = calendarManager.createEvent("Meeting", start,
            end, "Description", "Location", EventStatus.PUBLIC);
    assertFalse(result);
  }

  /**
   * Test creating all-day event in current calendar.
   */
  @Test
  @DisplayName("Test create all-day event in current calendar")
  void testCreateAllDayEventInCurrentCalendar() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDate date = LocalDate.of(2024, 9, 15);
    boolean result = calendarManager.createAllDayEvent("Holiday", date,
            "Description", "Location", EventStatus.PUBLIC);

    assertTrue(result);
    assertEquals(1, calendarManager.getAllEvents().size());
  }

  /**
   * Test creating event series in current calendar.
   */
  @Test
  @DisplayName("Test create event series in current calendar")
  void testCreateEventSeriesInCurrentCalendar() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDateTime start = LocalDateTime.of(2024, 9, 16, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 16, 15, 30);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    boolean result = calendarManager.createEventSeries("Weekly Meeting", start, end,
            weekdays, 3, "Description", "Location", EventStatus.PUBLIC);
    assertTrue(result);
    assertEquals(3, calendarManager.getAllEvents().size());
  }

  /**
   * Test creating all-day event series in current calendar.
   */
  @Test
  @DisplayName("Test create all-day event series in current calendar")
  void testCreateAllDayEventSeriesInCurrentCalendar() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDate start = LocalDate.of(2024, 9, 16);
    Set<DayOfWeek> weekdays = new HashSet<>();
    weekdays.add(DayOfWeek.MONDAY);

    boolean result = calendarManager.createAllDayEventSeries("Weekly Holiday",
            start, weekdays, 3, "Description", "Location", EventStatus.PUBLIC);
    assertTrue(result);
    assertEquals(3, calendarManager.getAllEvents().size());
  }

  /**
   * Test getting events on date from current calendar.
   */
  @Test
  @DisplayName("Test get events on date from current calendar")
  void testGetEventsOnDateFromCurrentCalendar() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDate date = LocalDate.of(2024, 9, 15);
    LocalDateTime start = date.atTime(14, 30);
    LocalDateTime end = date.atTime(15, 30);

    calendarManager.createEvent("Meeting", start, end);
    List<Event> events = calendarManager.getEventsOnDate(date);

    assertEquals(1, events.size());
    assertEquals("Meeting", events.get(0).getSubject());
  }

  /**
   * Test getting events on date without current calendar returns empty list.
   */
  @Test
  @DisplayName("Test get events on date without current calendar")
  void testGetEventsOnDateWithoutCurrentCalendar() {
    LocalDate date = LocalDate.of(2024, 9, 15);
    List<Event> events = calendarManager.getEventsOnDate(date);
    assertTrue(events.isEmpty());
  }

  /**
   * Test checking if busy at specific time.
   */
  @Test
  @DisplayName("Test is busy at specific time")
  void testIsBusyAtSpecificTime() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);

    calendarManager.createEvent("Meeting", start, end);

    assertTrue(calendarManager.isBusy(LocalDateTime.of(2024, 9, 15, 15, 0)));
    assertFalse(calendarManager.isBusy(LocalDateTime.of(2024, 9, 15, 16, 0)));
  }

  /**
   * Test copying single event between calendars.
   */
  @Test
  @DisplayName("Test copy single event between calendars")
  void testCopyEventBetweenCalendars() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("personal", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);
    calendarManager.createEvent("Meeting", start, end);

    LocalDateTime newStart = LocalDateTime.of(2024, 9, 16, 14, 30);
    boolean result = calendarManager.copyEvent("Meeting", start, "personal", newStart);

    assertTrue(result);
    assertEquals(1, calendarManager.getCalendar("personal").getAllEvents().size());
  }

  /**
   * Test copying events on date between calendars.
   */
  @Test
  @DisplayName("Test copy events on date between calendars")
  void testCopyEventsOnDateBetweenCalendars() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("personal", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDate sourceDate = LocalDate.of(2024, 9, 15);
    calendarManager.createEvent("Meeting 1", sourceDate.atTime(9, 0), sourceDate.atTime(10, 0));
    calendarManager.createEvent("Meeting 2", sourceDate.atTime(14, 0), sourceDate.atTime(15, 0));

    LocalDate targetDate = LocalDate.of(2024, 9, 16);
    boolean result = calendarManager.copyEventsOnDate(sourceDate, "personal", targetDate);

    assertTrue(result);
    assertEquals(2, calendarManager.getCalendar("personal").getAllEvents().size());
  }

  /**
   * Test copying events in range between calendars.
   */
  @Test
  @DisplayName("Test copy events in range between calendars")
  void testCopyEventsInRangeBetweenCalendars() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.createCalendar("personal", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDate startDate = LocalDate.of(2024, 9, 15);
    LocalDate endDate = LocalDate.of(2024, 9, 17);

    calendarManager.createEvent("Meeting 1", startDate.atTime(9, 0), startDate.atTime(10, 0));
    calendarManager.createEvent("Meeting 2", startDate.plusDays(1).atTime(14, 0),
            startDate.plusDays(1).atTime(15, 0));

    LocalDate targetStartDate = LocalDate.of(2024, 10, 1);
    boolean result = calendarManager.copyEventsInRange(startDate, endDate,
            "personal", targetStartDate);

    assertTrue(result);
    assertEquals(2, calendarManager.getCalendar("personal").getAllEvents().size());
  }

  /**
   * Test copy operations fail without current calendar.
   */
  @Test
  @DisplayName("Test copy operations fail without current calendar")
  void testCopyOperationsFailWithoutCurrentCalendar() {
    calendarManager.createCalendar("personal", ZoneId.of("America/New_York"));

    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime newStart = LocalDateTime.of(2024, 9, 16, 14, 30);

    boolean result1 = calendarManager.copyEvent("Meeting", start, "personal", newStart);
    assertFalse(result1);

    LocalDate sourceDate = LocalDate.of(2024, 9, 15);
    LocalDate targetDate = LocalDate.of(2024, 9, 16);

    boolean result2 = calendarManager.copyEventsOnDate(sourceDate, "personal", targetDate);
    assertFalse(result2);

    LocalDate startDate = LocalDate.of(2024, 9, 15);
    LocalDate endDate = LocalDate.of(2024, 9, 17);
    LocalDate targetStartDate = LocalDate.of(2024, 10, 1);

    boolean result3 = calendarManager.copyEventsInRange(startDate,
            endDate, "personal", targetStartDate);
    assertFalse(result3);
  }

  /**
   * Test editing event in current calendar.
   */
  @Test
  @DisplayName("Test edit event in current calendar")
  void testEditEventInCurrentCalendar() {
    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    LocalDateTime start = LocalDateTime.of(2024, 9, 15, 14, 30);
    LocalDateTime end = LocalDateTime.of(2024, 9, 15, 15, 30);
    calendarManager.createEvent("Meeting", start, end);

    boolean result = calendarManager.editEvent("subject", "Meeting", start, end, "Updated Meeting");
    assertTrue(result);
  }

  /**
   * Test setting and getting event copy service.
   */
  @Test
  @DisplayName("Test set and get event copy service")
  void testSetAndGetEventCopyService() {
    IEventCopyService newService = new EventCopyService();
    calendarManager.setEventCopyService(newService);
    assertEquals(newService, calendarManager.getEventCopyService());
  }

  /**
   * Test calendar manager toString method.
   */
  @Test
  @DisplayName("Test calendar manager toString method")
  void testCalendarManagerToString() {
    String result = calendarManager.toString();
    assertTrue(result.contains("CalendarManager"));
    assertTrue(result.contains("calendars=0"));
    assertTrue(result.contains("current='null'"));

    calendarManager.createCalendar("work", ZoneId.of("America/New_York"));
    calendarManager.useCalendar("work");

    String updatedResult = calendarManager.toString();
    assertTrue(updatedResult.contains("calendars=1"));
    assertTrue(updatedResult.contains("current='work'"));
  }
}