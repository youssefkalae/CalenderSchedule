USEME - GUI Usage Guide
How to Launch GUI Mode 
- Swing GUI 

# Run it 
- Run the JAR file without arguments: java -jar Calendar.jar
- Or explicitly specify GUI mode: java -jar Calendar.jar --mode gui
- The GUI window will open automatically

# Calendar Management Operations
- Creating a New Calendar

# To do 
- Click the "Create Calendar" button in the main toolbar
- Enter the calendar name in the text field
- Select timezone from the dropdown menu (e.g., America/New_York, America/Los_Angeles)
- Click "Create" to confirm
- The new calendar will appear in the calendar list

# Switching Between Calendars

- Use the calendar dropdown menu in the top panel
- Select the desired calendar from the list
- The current calendar name will be displayed in the title bar
- All subsequent operations will apply to the selected calendar

# Editing Calendar Properties

- Right-click on a calendar in the calendar list
- Select "Edit Calendar" from the context menu
- Choose to edit either:

- Name: Enter new calendar name
- Timezone: Select new timezone from dropdown

- Click "Save Changes" to apply

# Viewing All Calendars

- The calendar sidebar shows all created calendars
- Each calendar displays:

- Calendar name
- Current timezone
- Number of events


# Event Creation Operations 
- Creating a Single Event

- Click "New Event" button or double-click on a date
- Fill in the event form:

- Subject: Event title (required)
- Start Date/Time: Use date picker and time selector
- End Date/Time: Use date picker and time selector
- Description: Optional event details
- Location: Optional venue information
- Status: Select from Public/Private/Busy dropdown


- Click "Create Event" to save

- Creating an All-Day Event

- Click "New All-Day Event" button
- Fill in the event form:

- Subject: Event title (required)
- Date: Use date picker
- Description: Optional event details
- Location: Optional venue information
- Status: Select from dropdown


- Event will automatically span 8:00 AM to 5:00 PM
- Click "Create Event" to save
  Creating Recurring Events


# Event Management Operations
Viewing Events

Schedule Display: Shows events in chronological order
Date Range: View events for current time period
Event Format: "Subject (Date, Time) @ Location"
Navigation: Use Previous/Next buttons to change date range

Event Information Display

Each event shows: Subject, date, time, location
All-day events marked with "(All Day)"
Events sorted chronologically
Clear separation between different dates

# Date and Time Navigation
Navigating the Calendar

# Date Display Format

Dates: YYYY-MM-DD format
Times: HH:MM format (24-hour)
All-day events: No time shown, marked as "(All Day)"


# Dropdown menu shows all created calendars
Click to select different calendar
View switches to show events from selected calendar
All operations apply to currently selected calendar

# Cross-Calendar Features

Create events in any selected calendar
Switch between calendars to view different event sets
Each calendar maintains separate event storage

# Control Panel Buttons
Available Operations

Create Calendar: Add new calendar
Create Event: Add new event to current calendar
Today: Navigate to current date
Previous/Next Week: Navigate through time

# Button Layout

Main control buttons arranged horizontally
Clear labeling for each operation
Buttons grouped by function type

# Error Handling and Feedback
Success Messages

- "Calendar created successfully"
- "Event created successfully"
Clear confirmation for all operations

# Error Messages

Invalid date format warnings
Missing required field alerts
Clear error descriptions with corrective guidance

# Input Validation

Date format checking (YYYY-MM-DD)
Time format checking (HH:MM)
Required field validation
Logical time checking (start before end)

Display Features
Schedule View

# Calendar Information

Current calendar name in header
Date range being displayed
Event count information
Navigation status

# Keyboard and Mouse Operations
Basic Interactions

# Click buttons to perform operations
Type in text fields for input
Use dropdown menus for selection
Standard window controls (minimize, close)
