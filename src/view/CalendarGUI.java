package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import model.CalendarManager;
import model.Event;
import model.EventStatus;

/**
 * GUI implementation of the Calendar Application using Java Swing.
 * Provides a graphical interface for calendar operations following MVC principles.
 * Supports schedule view, event creation, and calendar management.
 */
public class CalendarGUI extends JFrame implements ICalendarGUI {

  private static final DateTimeFormatter DATE_FORMATTER =
          DateTimeFormatter.ofPattern("MMM dd, yyyy");
  private static final DateTimeFormatter TIME_FORMATTER =
          DateTimeFormatter.ofPattern("HH:mm");

  private final CalendarManager calendarManager;
  private final Map<String, Color> calendarColors;
  private JLabel currentDateLabel;
  private JComboBox<String> calendarDropdown;
  private JTextArea scheduleDisplay;
  private LocalDate currentStartDate;

  /**
   * Creates a new Calendar GUI with the specified calendar manager.
   *
   * @param calendarManager the calendar manager to use
   */
  public CalendarGUI(CalendarManager calendarManager) {
    this.calendarManager = calendarManager;
    this.currentStartDate = LocalDate.now();
    this.calendarColors = new HashMap<>();

    initializeDefaultCalendar();
    initializeGUI();
    updateScheduleView();
  }

  /**
   * Initializes the default calendar if none exists.
   */
  private void initializeDefaultCalendar() {
    if (calendarManager.getCalendarNames().isEmpty()) {
      calendarManager.createCalendar("Default", java.time.ZoneId.systemDefault());
      calendarManager.useCalendar("Default");
      calendarColors.put("Default", Color.BLUE);
    }
  }

  /**
   * Initializes the GUI components and layout.
   */
  private void initializeGUI() {
    setTitle("Calendar Application - GUI Mode");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    createHeaderPanel(mainPanel);
    createSchedulePanel(mainPanel);
    createControlPanel(mainPanel);

    add(mainPanel);

    updateCalendarDropdown();
  }

  /**
   * Creates the header panel with navigation and calendar selection.
   */
  private void createHeaderPanel(JPanel mainPanel) {
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

    JPanel dateNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JButton prevDateButton = new JButton("◄ Previous Week");
    JButton nextDateButton = new JButton("Next Week ►");
    currentDateLabel = new JLabel();

    prevDateButton.addActionListener(e -> navigateDate(-7));
    nextDateButton.addActionListener(e -> navigateDate(7));

    dateNavPanel.add(prevDateButton);
    dateNavPanel.add(currentDateLabel);
    dateNavPanel.add(nextDateButton);

    JPanel calendarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    calendarPanel.add(new JLabel("Calendar: "));
    calendarDropdown = new JComboBox<>();
    calendarDropdown.addActionListener(e -> switchCalendar());
    calendarPanel.add(calendarDropdown);

    headerPanel.add(dateNavPanel, BorderLayout.WEST);
    headerPanel.add(calendarPanel, BorderLayout.EAST);

    mainPanel.add(headerPanel, BorderLayout.NORTH);
    updateCurrentDateLabel();
  }

  /**
   * Creates the main schedule display panel.
   */
  private void createSchedulePanel(JPanel mainPanel) {
    JPanel schedulePanel = new JPanel(new BorderLayout());
    schedulePanel.setBorder(BorderFactory.createTitledBorder("Schedule View"));

    scheduleDisplay = new JTextArea();
    scheduleDisplay.setEditable(false);
    scheduleDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    scheduleDisplay.setBackground(Color.WHITE);

    JScrollPane scheduleScrollPane = new JScrollPane(scheduleDisplay);
    scheduleScrollPane.setPreferredSize(new Dimension(750, 400));

    schedulePanel.add(scheduleScrollPane, BorderLayout.CENTER);
    mainPanel.add(schedulePanel, BorderLayout.CENTER);
  }

  /**
   * Creates the control panel with action buttons.
   */
  private void createControlPanel(JPanel mainPanel) {
    JPanel controlPanel = new JPanel(new FlowLayout());
    controlPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

    JButton createEventButton = new JButton("Create Event");
    JButton createCalendarButton = new JButton("Create Calendar");
    JButton todayButton = new JButton("Today");
    JButton editEventButton = new JButton("Edit Event");

    createEventButton.addActionListener(e -> showCreateEventDialog());
    createCalendarButton.addActionListener(e -> showCreateCalendarDialog());
    todayButton.addActionListener(e -> goToToday());
    editEventButton.addActionListener(e -> showEditEventDialog());

    controlPanel.add(createEventButton);
    controlPanel.add(createCalendarButton);
    controlPanel.add(todayButton);
    controlPanel.add(editEventButton);

    mainPanel.add(controlPanel, BorderLayout.SOUTH);
  }

  /**
   * Updates the calendar dropdown with available calendars.
   */
  private void updateCalendarDropdown() {
    calendarDropdown.removeAllItems();
    Set<String> calendarNames = calendarManager.getCalendarNames();

    for (String name : calendarNames) {
      calendarDropdown.addItem(name);
      if (!calendarColors.containsKey(name)) {
        calendarColors.put(name, generateColorForCalendar(name));
      }
    }

    String currentCalendar = calendarManager.getCurrentCalendarName();
    if (currentCalendar != null) {
      calendarDropdown.setSelectedItem(currentCalendar);
    }
  }

  /**
   * Generates a color for a calendar based on its name.
   */
  private Color generateColorForCalendar(String name) {
    Color[] colors = {Color.BLUE, Color.GREEN, Color.RED,
                      Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK
    };
    return colors[Math.abs(name.hashCode()) % colors.length];
  }


  /**
   * Updates the schedule view with events from the current date range.
   */
  @Override
  public void updateScheduleView() {
    if (calendarManager.getCurrentCalendar() == null) {
      scheduleDisplay.setText("No calendar selected.\nPlease create or select a calendar.");
      return;
    }

    StringBuilder schedule = new StringBuilder();
    schedule.append("Schedule View - Starting from ")
            .append(currentStartDate.format(DATE_FORMATTER)).append("\n");
    schedule.append("Calendar: ").append(calendarManager.getCurrentCalendarName())
            .append("\n");
    schedule.append("─".repeat(60)).append("\n\n");

    List<Event> upcomingEvents = getUpcomingEvents(currentStartDate, 10);

    if (upcomingEvents.isEmpty()) {
      schedule.append("No events scheduled.\n");
    } else {
      LocalDate lastDate = null;
      for (Event event : upcomingEvents) {
        LocalDate eventDate = event.getStartDateTime().toLocalDate();

        if (!eventDate.equals(lastDate)) {
          if (lastDate != null) {
            schedule.append("\n");
          }
          schedule.append("Date: ").append(eventDate.format(DATE_FORMATTER))
                  .append("\n");
          lastDate = eventDate;
        }

        schedule.append("  • ").append(event.getSubject());

        if (!event.isAllDay()) {
          schedule.append(" (").append(event.getStartDateTime().format(TIME_FORMATTER))
                  .append(" - ").append(event.getEndDateTime().format(TIME_FORMATTER))
                  .append(")");
        } else {
          schedule.append(" (All Day)");
        }

        if (event.getLocation() != null && !event.getLocation().trim().isEmpty()) {
          schedule.append(" @ ").append(event.getLocation());
        }

        schedule.append("\n");
      }
    }

    scheduleDisplay.setText(schedule.toString());
    scheduleDisplay.setCaretPosition(0);
  }

  /**
   * Gets upcoming events starting from a specific date.
   */
  private List<Event> getUpcomingEvents(LocalDate startDate, int maxEvents) {
    List<Event> upcomingEvents = new ArrayList<>();
    LocalDate currentDate = startDate;

    for (int i = 0; i < 30 && upcomingEvents.size() < maxEvents; i++) {
      List<Event> dayEvents = calendarManager.getEventsOnDate(currentDate);
      upcomingEvents.addAll(dayEvents);
      currentDate = currentDate.plusDays(1);
    }

    upcomingEvents.sort((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()));

    if (upcomingEvents.size() > maxEvents) {
      upcomingEvents = upcomingEvents.subList(0, maxEvents);
    }

    return upcomingEvents;
  }

  /**
   * Navigates the current date by the specified number of days.
   */
  private void navigateDate(int days) {
    currentStartDate = currentStartDate.plusDays(days);
    updateCurrentDateLabel();
    updateScheduleView();
  }

  /**
   * Updates the current date label.
   */
  private void updateCurrentDateLabel() {
    currentDateLabel.setText("Week of " + currentStartDate.format(DATE_FORMATTER));
  }

  /**
   * Switches to the selected calendar.
   */
  private void switchCalendar() {
    String selectedCalendar = (String) calendarDropdown.getSelectedItem();
    if (selectedCalendar != null) {
      calendarManager.useCalendar(selectedCalendar);
      updateScheduleView();
    }
  }

  /**
   * Navigates to today's date.
   */
  private void goToToday() {
    currentStartDate = LocalDate.now();
    updateCurrentDateLabel();
    updateScheduleView();
  }

  /**
   * Shows the create event dialog.
   */
  @Override
  public void showCreateEventDialog() {
    if (calendarManager.getCurrentCalendar() == null) {
      showErrorMessage("No calendar selected",
              "Please create or select a calendar first.");
      return;
    }

    JDialog dialog = new JDialog(this, "Create Event", true);
    dialog.setLayout(new BorderLayout());
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(this);

    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(new JLabel("Subject:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField subjectField = new JTextField(20);
    formPanel.add(subjectField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    formPanel.add(new JLabel("Date:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField dateField = new JTextField(LocalDate.now().toString());
    formPanel.add(dateField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.NONE;
    formPanel.add(new JLabel("All Day:"), gbc);
    gbc.gridx = 1;
    JCheckBox allDayBox = new JCheckBox();
    formPanel.add(allDayBox, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    JLabel startLabel = new JLabel("Start Time:");
    formPanel.add(startLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField startTimeField = new JTextField("09:00");
    formPanel.add(startTimeField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.fill = GridBagConstraints.NONE;
    JLabel endLabel = new JLabel("End Time:");
    formPanel.add(endLabel, gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField endTimeField = new JTextField("10:00");
    formPanel.add(endTimeField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.fill = GridBagConstraints.NONE;
    formPanel.add(new JLabel("Location:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField locationField = new JTextField();
    formPanel.add(locationField, gbc);

    allDayBox.addActionListener(e -> {
      boolean isAllDay = allDayBox.isSelected();
      startTimeField.setEnabled(!isAllDay);
      endTimeField.setEnabled(!isAllDay);
      startLabel.setEnabled(!isAllDay);
      endLabel.setEnabled(!isAllDay);
    });

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton createButton = new JButton("Create");
    JButton cancelButton = new JButton("Cancel");

    createButton.addActionListener(e -> {
      try {
        String subject = subjectField.getText().trim();
        if (subject.isEmpty()) {
          showErrorMessage("Invalid Input", "Subject cannot be empty.");
          return;
        }

        LocalDate date = LocalDate.parse(dateField.getText().trim());
        String location = locationField.getText().trim();
        location = location.isEmpty() ? null : location;

        boolean success;
        if (allDayBox.isSelected()) {
          success = calendarManager.createAllDayEvent(subject, date, null,
                  location, EventStatus.PUBLIC);
        } else {
          LocalTime startTime = LocalTime.parse(startTimeField.getText().trim());
          LocalTime endTime = LocalTime.parse(endTimeField.getText().trim());

          if (!startTime.isBefore(endTime)) {
            showErrorMessage("Invalid Time", "Start time must be before end time.");
            return;
          }

          LocalDateTime startDateTime = date.atTime(startTime);
          LocalDateTime endDateTime = date.atTime(endTime);
          success = calendarManager.createEvent(subject, startDateTime, endDateTime,
                  null, location, EventStatus.PUBLIC);
        }

        if (success) {
          updateScheduleView();
          dialog.dispose();
          showSuccessMessage("Event Created",
                  "Event '" + subject + "' created successfully.");
        } else {
          showErrorMessage("Event Creation Failed",
                  "A conflicting event already exists.");
        }

      } catch (DateTimeParseException ex) {
        showErrorMessage("Invalid Input",
                "Please check your date and time formats.\n" +
                        "Use YYYY-MM-DD for date and HH:MM for time.");
      } catch (Exception ex) {
        showErrorMessage("Error", "An error occurred: " + ex.getMessage());
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(createButton);
    buttonPanel.add(cancelButton);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
  }

  /**
   * Shows the create calendar dialog.
   */
  @Override
  public void showCreateCalendarDialog() {
    String calendarName = JOptionPane.showInputDialog(this, "Enter calendar name:",
            "Create Calendar", JOptionPane.QUESTION_MESSAGE);

    if (calendarName != null && !calendarName.trim().isEmpty()) {
      calendarName = calendarName.trim();

      if (calendarManager.calendarExists(calendarName)) {
        showErrorMessage("Calendar Exists",
                "A calendar with this name already exists.");
        return;
      }

      boolean success = calendarManager.createCalendar(calendarName,
              java.time.ZoneId.systemDefault());

      if (success) {
        updateCalendarDropdown();
        calendarManager.useCalendar(calendarName);
        calendarDropdown.setSelectedItem(calendarName);
        updateScheduleView();
        showSuccessMessage("Calendar Created",
                "Calendar '" + calendarName + "' created successfully.");
      } else {
        showErrorMessage("Creation Failed", "Failed to create calendar.");
      }
    }
  }

  /**
   * Shows the edit event dialog.
   */
  @Override
  public void showEditEventDialog() {
    if (calendarManager.getCurrentCalendar() == null) {
      showErrorMessage("No Calendar", "Please select a calendar first.");
      return;
    }

    List<Event> weekEvents = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
      weekEvents.addAll(calendarManager.getEventsOnDate(currentStartDate.plusDays(i)));
    }

    if (weekEvents.isEmpty()) {
      showInfoMessage("No Events", "No events found in the current week to edit.");
      return;
    }

    String[] eventOptions = weekEvents.stream()
            .map(e -> e.getSubject() + " - " +
                    e.getStartDateTime().format(
                            DateTimeFormatter.ofPattern("MMM dd, HH:mm")))
            .toArray(String[]::new);

    String selectedOption = (String) JOptionPane.showInputDialog(
            this,
            "Select an event to edit:",
            "Edit Event",
            JOptionPane.QUESTION_MESSAGE,
            null,
            eventOptions,
            eventOptions[0]
    );

    if (selectedOption != null) {
      int selectedIndex = java.util.Arrays.asList(eventOptions).indexOf(selectedOption);
      Event selectedEvent = weekEvents.get(selectedIndex);
      showEditEventForm(selectedEvent);
    }
  }

  /**
   * Shows the edit form for a specific event.
   */
  private void showEditEventForm(Event event) {
    JDialog dialog = new JDialog(this, "Edit Event", true);
    dialog.setLayout(new BorderLayout());
    dialog.setSize(400, 250);
    dialog.setLocationRelativeTo(this);

    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    formPanel.add(new JLabel("Subject:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField subjectField = new JTextField(event.getSubject(), 20);
    formPanel.add(subjectField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.NONE;
    formPanel.add(new JLabel("Start Time:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField startTimeField = new JTextField(
            event.getStartDateTime().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    formPanel.add(startTimeField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.fill = GridBagConstraints.NONE;
    formPanel.add(new JLabel("End Time:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField endTimeField = new JTextField(
            event.getEndDateTime().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    formPanel.add(endTimeField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.fill = GridBagConstraints.NONE;
    formPanel.add(new JLabel("Location:"), gbc);
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    JTextField locationField = new JTextField(
            event.getLocation() != null ? event.getLocation() : "");
    formPanel.add(locationField, gbc);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

    saveButton.addActionListener(e -> {
      try {
        String newSubject = subjectField.getText().trim();
        if (newSubject.isEmpty()) {
          showErrorMessage("Invalid Input", "Subject cannot be empty.");
          return;
        }

        LocalDateTime newStart = LocalDateTime.parse(startTimeField.getText().trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime newEnd = LocalDateTime.parse(endTimeField.getText().trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String newLocation = locationField.getText().trim();

        if (!newStart.isBefore(newEnd)) {
          showErrorMessage("Invalid Time", "Start time must be before end time.");
          return;
        }

        boolean success = true;
        if (!event.getSubject().equals(newSubject)) {
          success &= calendarManager.editEvent("subject", event.getSubject(),
                  event.getStartDateTime(), event.getEndDateTime(), newSubject);
        }
        if (!event.getStartDateTime().equals(newStart)) {
          success &= calendarManager.editEvent("start", event.getSubject(),
                  event.getStartDateTime(), event.getEndDateTime(),
                  newStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        }
        if (!event.getEndDateTime().equals(newEnd)) {
          success &= calendarManager.editEvent("end", event.getSubject(),
                  event.getStartDateTime(), event.getEndDateTime(),
                  newEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        }
        if (!java.util.Objects.equals(event.getLocation(),
                newLocation.isEmpty() ? null : newLocation)) {
          success &= calendarManager.editEvent("location", event.getSubject(),
                  event.getStartDateTime(), event.getEndDateTime(), newLocation);
        }

        if (success) {
          updateScheduleView();
          dialog.dispose();
          showSuccessMessage("Event Updated", "Event updated successfully.");
        } else {
          showErrorMessage("Update Failed", "Failed to update event.");
        }

      } catch (DateTimeParseException ex) {
        showErrorMessage("Invalid Input", "Please use the format: YYYY-MM-DD HH:MM");
      } catch (Exception ex) {
        showErrorMessage("Error", "An error occurred: " + ex.getMessage());
      }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    buttonPanel.add(saveButton);
    buttonPanel.add(cancelButton);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
  }

  /**
   * Shows an error message dialog.
   */
  @Override
  public void showErrorMessage(String title, String message) {
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Shows a success message dialog.
   */
  @Override
  public void showSuccessMessage(String title, String message) {
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shows an info message dialog.
   */
  @Override
  public void showInfoMessage(String title, String message) {
    JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

}