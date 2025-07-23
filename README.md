# Calendar Application
A virtual calendar application that mimics features found in widely used calendar apps like Google Calendar or Apple's iCalendar app.

# Design Changes

# 1. Multi-Calendar Architecture Implementation
- Change: Added `CalendarManager`, `CalendarInstance`, and `ICalendarManager` interfaces
- Justification: Enables users to manage multiple named calendars with different timezones, supporting real-world use cases where users need separate work, personal, and project calendars

# 2. Enhanced Event Copy Service
- Change: Created `EventCopyService` and `IEventCopyService` with timezone conversion capabilities
- Justification: Allows copying events between calendars with different timezones while maintaining proper time conversions and series relationships

# 3. Interface-Based Design Pattern
- Change: Introduced interfaces `ICalendar`, `ICalendarInstance`, `IEvent`, `IEventSeries`, `ICalendarView`, `ICalendarController`
- Justification: Improves testability, enables dependency injection, and follows SOLID principles for better maintainability and extensibility

# 4. Command Handler Separation
- Change: Split command processing into `CalendarCommandHandler` for multi-calendar commands and `CommandProcessor` for single-calendar operations
- Justification: Separates concerns between calendar management operations and event management operations, improving code organization

# 5. Enhanced Error Handling and Validation
- Change: Added comprehensive input validation, timezone validation, and conflict detection across calendar boundaries
- Justification: Provides better user experience with clear error messages and prevents data corruption through robust validation

# 6. Dual Application Entry Points
- Change: Created both `CalendarApp.java` (original single-calendar mode) and `CalendarMain.java` (new multi-calendar mode)
- Justification: Maintains backward compatibility while providing enhanced multi-calendar functionality for different use cases

# 7. Event Series Management Enhancement
- Change: Improved series ID generation with global counters and better conflict detection within series creation
- Justification: Ensures unique series identification across multiple calendars and prevents conflicts during batch event creation

# How to Run the Program

# Prerequisites
- Java 8 or higher installed
- Terminal/command prompt access on the hw6 file 

# Multi-Calendar Mode GUI
- java -jar Calendar.jar 

# Run interactive mode 
- java -jar Calendar.jar --mode interactive
- will show help/exit (if you click help it will give you command ideas to put)
  - explained more in the valid commands file (a walkthrough guide)
  - a basic set up in the commands.txt which is used

# Run path-of-script file mode
- java -jar Calendar.jar --mode headless commands.txt

# Features Status

# Working Features

# Multi-Calendar Management
- Create multiple named calendars with timezones
- Switch between calendars
- Edit calendar properties (name, timezone)
- List all available calendars
- View current active calendar

# Event Creation (All Types)
- Single timed events with start/end times
- All-day events (8am-5pm default)
- Event series with recurrence patterns
- Weekday pattern support (M, T, W, R, F, S, U)
- Series with occurrence count or end date
- Support for all event properties (subject, description, location, status)

# Event Editing
- Edit single events by property
- Edit future events in a series from specific date
- Edit entire event series
- All properties editable: subject, start, end, description, location, status
- Proper series splitting when start times change

# Event Copying Between Calendars
- Copy single events with timezone conversion
- Copy all events from a specific date
- Copy events within date ranges
- Maintain event series relationships during copying
- Automatic timezone conversion between source and target calendars

# Calendar Queries
- View events on specific dates
- View events in date/time ranges
- Check busy/available status at specific times
- Proper sorting by start time

# Command Interface
- Full command parsing as per specification
- Support for quoted subjects with spaces
- Comprehensive error handling and validation
- Help system with command examples

# Execution Modes
- Interactive mode with real-time command processing
- Headless mode with file-based command execution
- Multi-calendar interactive mode
- Proper exit command handling

# Known Limitations

# Performance Considerations
- Large numbers of recurring events may impact performance
- Calendar switching with many events could be slow

# Advanced Features Not Implemented
- Event reminders/notifications
- Event categories/colors
- Calendar sharing between users
- Import/export functionality (iCal, CSV)
- Advanced recurrence patterns (monthly, yearly)

# Supported Commands

# Multi-Calendar Commands

# Calendar Management
create calendar --name <calName> --timezone <area/location>
edit calendar --name <name> --property <property> <value>
use calendar --name <name>
list calendars
current calendar

# Event Copying
copy event <eventName> on <dateTime> --target <calendarName> to <dateTime>
copy events on <date> --target <calendarName> to <date>
copy events between <date> and <date> --target <calendarName> to <date>


#Event Management Commands (Require Active Calendar)
# Creating Events
create event <subject> from <YYYY-MM-DDTHH:MM> to <YYYY-MM-DDTHH:MM>
create event <subject> on <YYYY-MM-DD>
create event <subject> from <YYYY-MM-DDTHH:MM> to <YYYY-MM-DDTHH:MM> repeats <weekdays> for <N> times
create event <subject> from <YYYY-MM-DDTHH:MM> to <YYYY-MM-DDTHH:MM> repeats <weekdays> until <YYYY-MM-DD>
create event <subject> on <YYYY-MM-DD> repeats <weekdays> for <N> times
create event <subject> on <YYYY-MM-DD> repeats <weekdays> until <YYYY-MM-DD>

# Editing Events
edit event <property> <subject> from <YYYY-MM-DDTHH:MM> to <YYYY-MM-DDTHH:MM> with <newValue>
edit events <property> <subject> from <YYYY-MM-DDTHH:MM> with <newValue>
edit series <property> <subject> from <YYYY-MM-DDTHH:MM> with <newValue>

# Querying Calendar
print events on <YYYY-MM-DD>
print events from <YYYY-MM-DDTHH:MM> to <YYYY-MM-DDTHH:MM>
show status <YYYY-MM-DDTHH:MM>

# System Commands
help
exit

# Command Examples

# Multi-Calendar Workflow
# Create calendars
create calendar --name work --timezone America/New_York
create calendar --name personal --timezone America/Los_Angeles

# Switch to work calendar
use calendar --name work

# Create work events
create event "Team Meeting" from 2024-09-15T14:30 to 2024-09-15T15:30

# Copy to personal calendar with timezone conversion
copy event "Team Meeting" on 2024-09-15T14:30 --target personal to 2024-09-15T11:30

# Event Series Creation
# Weekly recurring meeting
create event "Weekly Standup" from 2024-09-15T09:00 to 2024-09-15T09:30 repeats MTW for 10 times

# All-day events until end date
create event "Vacation" on 2024-12-20 repeats MTWRF until 2024-12-31

# Architecture Features

# MVC Design Pattern
- Model: `Calendar`, `CalendarManager`, `CalendarInstance` classes handle business logic
- View: `CalendarView` class handles output formatting
- Controller: `CalendarController`, `CalendarCommandHandler` coordinate between model and view

# SOLID Principles Implementation
- Single Responsibility: Each class has one clear purpose
- Open/Close*: Extensible design for new features through interfaces
- Liskov Substitution: Implementations can be substituted without breaking functionality
- Interface Segregation: Clean, focused interfaces with specific responsibilities
- Dependency Inversion: Controllers depend on abstractions, not concrete classes

# Interface Architecture
The application uses comprehensive interface-based design for maximum flexibility:

# Model Layer Interfaces
- `ICalendar`: Core calendar operations (event creation, editing, querying)
- `ICalendarInstance`: Individual calendar with timezone support
- `ICalendarManager`: Multi-calendar management and coordination
- `IEvent*: Event properties and behavior contracts
- `IEventSeries`: Recurring event series metadata
- `IEventCopyService`: Cross-calendar event copying with timezone conversion

# View Layer Interfaces
- `ICalendarView`: Output formatting and display contracts

# Controller Layer Interfaces
- `ICalendarController`: MVC coordination for single-calendar operations
- `ICalendarCommandHandler`: Interactive command processing for multi-calendar mode
- `ICommandProcessor`: Command parsing and execution contracts

# Design Patterns Used
- Strategy Pattern: `IEventCopyService` for different copying strategies
- Factory Pattern: Event creation through calendar instances
- Command Pattern: Command processing with validation and execution separation

# File Structure
src/
├── model/
│   ├── Calendar.java              # Original single calendar implementation
│   ├── CalendarManager.java       # Multi-calendar coordinator
│   ├── CalendarInstance.java      # Individual calendar with timezone
│   ├── Event.java                 # Event data model
│   ├── EventSeries.java          # Event series metadata
│   ├── EventCopyService.java     # Cross-calendar event copying
│   ├── EventStatus.java          # Event status enumeration
│   ├── ICalendar.java             # Calendar interface
│   ├── ICalendarInstance.java     # Calendar instance interface
│   ├── ICalendarManager.java      # Calendar manager interface
│   ├── IEvent.java               # Event interface
│   ├── IEventSeries.java         # Event series interface
│   └── IEventCopyService.java    # Event copy service interface
├── view/
│   ├── CalendarView.java          # Output formatting
│   └── ICalendarView.java         # View interface
├── controller/
│   ├── CalendarController.java    # Single-calendar MVC controller
│   ├── CalendarCommandHandler.java # Multi-calendar command handler
│   ├── CommandProcessor.java      # Command parsing and validation
│   ├── ICalendarController.java   # Calendar controller interface
│   ├── ICalendarCommandHandler.java # Command handler interface
│   └── ICommandProcessor.java     # Command processor interface
├── CalendarApp.java               # Single-calendar application entry
└── CalendarMain.java              # Multi-calendar application entry


# Date/Time Formats
- Date: `YYYY-MM-DD` (e.g., `2024-09-15`)
- DateTime: `YYYY-MM-DDTHH:MM` (e.g., `2024-09-15T14:30`)
- Weekdays: `MTWRFSU` (Monday through Sunday)
- Timezones: IANA format (e.g., `America/New_York`, `Europe/Paris`)

# Error Handling
- Comprehensive input validation for all commands
- Proper duplicate detection preventing conflicts
- Date/time format validation with clear error messages
- Timezone validation with helpful suggestions
- Graceful handling of missing calendars or events

# Testing
The application includes comprehensive test coverage for:
- Calendar management operations
- Event creation and editing
- Command parsing and validation
- Cross-calendar event copying
- Timezone conversion accuracy
- MVC component integration

# Distributions
Jacob:
Calendar.java, CalendarApp.java, CalendarController.java, EventStatus.java,
CalendarTest, CalendarAppTest, CalendarControllerTest, CalendarView.Test,
Valid Commands, README.md, CalendarManager.java, EventCopyService.java,
CalendarMain.java

Youssef:
CalendarView.java, CommandProcessor.java, Event.java, EventSeries.java,
CommandProcessorTest, EventTest, EventSeriesTest,
Invalid Commands, No Exit Commands, CalendarInstance.java, CalendarCommandHandler.java, 
USEME.md, Jar and editing configurations on CalendarApp

- We Split up the Interfaces and also checked each others work and made edits when needed to