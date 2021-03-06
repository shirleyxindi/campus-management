package nl.tudelft.oopp.group39.room.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import nl.tudelft.oopp.group39.booking.model.Booking;
import nl.tudelft.oopp.group39.building.model.Building;
import nl.tudelft.oopp.group39.event.model.Event;
import nl.tudelft.oopp.group39.room.model.Room;
import nl.tudelft.oopp.group39.server.communication.ServerCommunication;
import nl.tudelft.oopp.group39.server.controller.AbstractSceneController;
import nl.tudelft.oopp.group39.server.views.MainDisplay;


public class RoomReservationController extends AbstractSceneController {
    @FXML private DatePicker date;
    @FXML private Button reserveButton;
    @FXML private ComboBox<String> fromTime;
    @FXML private ComboBox<String> toTime;
    @FXML private Label roomName;
    @FXML private Label roomDetails;
    @FXML private Label titleLabel;
    @FXML private Label onlyStaff;
    @FXML private Label errormsg;
    private Building building;
    private Room room;

    /**
     * Sets the entire scene up, so loading the timeslots, room information and sets up the buttons.
     *
     * @param room     the room you've selected
     * @param building the building of the room you've selected
     * @throws JsonProcessingException if the JSON could not be processed
     */
    public void setup(Room room, Building building) throws JsonProcessingException {
        this.building = building;
        this.room = room;
        titleLabel.setText(room.getName());
        loadTimeslots();
        loadRoom(room);
        reserveButton.setOnAction(event -> {
            try {
                reserveRoom(room);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Reserves a room and shows a confirmed alert if the action was successful.
     */
    @FXML
    private void reserveRoom(Room room) throws IOException {
        LocalDate bookingDate = date.getValue();
        String bookingStart = fromTime.getValue() + ":00";
        String bookingEnd = toTime.getValue() + ":00";
        if (loggedIn) {
            String dateString = date.getValue()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (checkDate(dateString, room.getId())) {
                if (checkEmpty(date, fromTime, toTime)) {
                    long roomId = room.getId();
                    String roomIdString = "" + roomId;
                    String username = AbstractSceneController.user.getUsername();
                    ServerCommunication.addBooking(
                        dateString,
                        bookingStart,
                        bookingEnd, username,
                        roomIdString
                    );

                    createAlert("Reservation successful.");
                    System.out.println(dateString
                        + bookingStart + bookingEnd
                        + username + roomIdString);

                    System.out.println(bookingDate);
                    System.out.println(bookingStart + "\n" + bookingEnd);
                    backToRoom();
                }
            }
        } else {
            errormsg.setText("Please log in to book a room");
        }
    }

    /**
     * Checks if the date, start and end fields are empty.
     * If that's the case, the method will generate an error message and return false.
     *
     * @param date  the date of the reservation
     * @param start start time of reservation
     * @param end   end time of reservation
     * @return true or false depending on whether or not the date, start and end fields are filled.
     */
    public boolean checkEmpty(DatePicker date, ComboBox<String> start, ComboBox<String> end) {
        if (date.getValue() == null
            || start.getSelectionModel().isEmpty()
            || end.getSelectionModel().isEmpty()
        ) {
            errormsg.setText("Please fill in all the fields");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Retrieves times when room is booked filtered on date.
     *
     * @param date selected date
     * @return List of integers
     * @throws JsonProcessingException when there is something wrong with processing
     */
    public List<Integer> getBookedTimes(String date) throws JsonProcessingException {
        String roomDate = "room=" + room.getId() + "&date=" + date;
        Booking[] bookingsList = getBookings(roomDate);
        List<Integer> bookedTimes = new ArrayList<>();
        for (Booking booking : bookingsList) {
            int startTime = Integer.parseInt(booking.getStartTime().split(":")[0]);
            bookedTimes.add(startTime);
            int endTime = Integer.parseInt(booking.getEndTime().split(":")[0]);
            bookedTimes.add(endTime);
        }
        if (loggedIn) {
            String userDate = "user=" + user.getUsername() + "&date=" + date;
            Booking[] userBookingsList = getBookings(userDate);
            if (userBookingsList.length != 0) {
                for (Booking userBooking : userBookingsList) {
                    int startTime = Integer.parseInt(userBooking.getStartTime().split(":")[0]);
                    bookedTimes.add(startTime);
                    int endTime = Integer.parseInt(userBooking.getEndTime().split(":")[0]);
                    bookedTimes.add(endTime);
                }
            }
            String eventFilters = "user=" + user.getUsername()
                + "&date=" + date
                + "&isGlobal=false";
            Set<Event> events = getEvents(eventFilters);
            if (events.size() != 0) {
                for (Event event : events) {
                    if (event.getStartTime().toLocalDate()
                        .isEqual(event.getEndTime().toLocalDate())
                    ) {
                        int start = event.getStartTime().getHour();
                        int end = event.getEndTime().getHour();
                        bookedTimes.add(start);
                        bookedTimes.add(end);
                    } else if (event.getStartTime().toLocalDate().isEqual(LocalDate.parse(date))) {
                        int start = event.getStartTime().getHour();
                        bookedTimes.add(start);
                        bookedTimes.add(23);
                    } else if (event.getEndTime().toLocalDate().isEqual(LocalDate.parse(date))) {
                        int end = event.getEndTime().getHour();
                        bookedTimes.add(0);
                        bookedTimes.add(end);
                    }
                }
            }
        }
        System.out.println(bookedTimes);
        return bookedTimes;
    }

    /**
     * Initiates the timeslots for the ComboBoxes to load.
     *
     * @return a list with LocalTimes from 00:00 to 23:00
     */
    private List<String> initiateTimeslots(String date) throws JsonProcessingException {
        List<String> times = new ArrayList<>();
        int open = Integer.parseInt(building.getOpen().toString().split(":")[0]);
        int closed = Integer.parseInt(building.getClosed().toString().split(":")[0]);
        List<Integer> bookedTimes = getBookedTimes(date);
        for (int i = open; i < closed; i++) {
            String time;
            if (i < 10) {
                time = "0" + i + ":00";
            } else {
                time = i + ":00";
            }
            times.add(time);
        }
        if (bookedTimes.size() != 0) {
            for (int j = 0; j < bookedTimes.size(); j = j + 2) {
                for (int i = open; i < closed; i++) {
                    if (i >= bookedTimes.get(j) && i < bookedTimes.get(j + 1)) {
                        String time;
                        if (i < 10) {
                            time = "0" + i + ":00";
                        } else {
                            time = i + ":00";
                        }
                        times.remove(time);
                    }
                }
            }
        }
        return times;
    }

    /**
     * Updates the end timeslots based on selected date and from time.
     *
     * @param date selected date
     * @param time selected time
     * @throws JsonProcessingException when there is something wrong with processing
     */
    public void updateEndSlots(String date, String time) throws JsonProcessingException {
        int timeAsInt = Integer.parseInt(time.split(":")[0]);
        List<Integer> bookedTimes = getBookedTimes(date);
        toTime.getItems().clear();
        int closed = Integer.parseInt(building.getClosed().toString().split(":")[0]);
        List<Integer> times = new ArrayList<>();
        for (int i = timeAsInt + 1; i < timeAsInt + 5; i++) {
            if (i <= closed) {
                times.add(i);
            }
        }
        if (bookedTimes.size() != 0) {
            for (int j = 0; j < bookedTimes.size(); j = j + 2) {
                for (int i = timeAsInt + 1; i < timeAsInt + 5; i++) {
                    if (i > bookedTimes.get(j) && i <= bookedTimes.get(j + 1)) {
                        times.remove((Integer) i);
                    }
                }

            }
        }
        for (int t = timeAsInt + 2; t < timeAsInt + 5; t++) {
            if (!times.contains(t - 1)) {
                Integer remove = t;
                times.remove(remove);
            }
        }
        for (int i : times) {
            String timeSlot;
            if (i <= closed) {
                if (i < 10) {
                    timeSlot = "0" + i + ":00";
                } else {
                    timeSlot = i + ":00";
                }
                toTime.getItems().add(timeSlot);
            }
        }
    }

    /**
     * Updates the timeslots for choosing start time according to booked times on selected date.
     *
     * @param date the selected date
     * @throws JsonProcessingException when there is a processing exception
     */
    public void updateStartSlots(String date) throws JsonProcessingException {
        fromTime.getItems().clear();
        fromTime.getItems().addAll(initiateTimeslots(date));
    }


    /**
     * Loads the room into the VBox containing the room information.
     */
    private void loadRoom(Room room) {
        String name = room.getName();
        String roomDescription = room.getDescription();
        int roomCapacity = room.getCapacity();
        String roomFacilities = room.facilitiesToString();
        roomName.setText(name);
        roomDetails.setText(roomDescription
            + "\n" + "Capacity: " + roomCapacity
            + "\n" + "Facilities: " + roomFacilities);
        if (room.isOnlyStaff()) {
            onlyStaff.setText("This room can only booked by a staff member");
            reserveButton.setDisable(true);
            if (loggedIn) {
                if (!user.getRole().equals("STUDENT")) {
                    reserveButton.setDisable(false);
                }
            }
        }
    }

    /**
     * Loads the timeslots into the ComboBoxes.
     */
    private void loadTimeslots() throws JsonProcessingException {
        date.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });
        date.setValue(LocalDate.now());
        updateStartSlots(date.getValue().format(dateFormatter));
        updateEndSlots(date.getValue().format(dateFormatter), building.getOpen().toString());
        date.setOnAction(event -> {
            try {
                updateStartSlots(date.getValue().format(dateFormatter));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        fromTime.setOnAction(event -> {
            try {
                String fromValue;
                if (fromTime.getSelectionModel().isEmpty()) {
                    fromValue = building.getOpen().toString();
                } else {
                    fromValue = fromTime.getValue();
                }
                updateEndSlots(
                    date.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    fromValue
                );
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Returns the user back to the room page when the back button is clicked.
     */
    @FXML
    private void backToRoom() {
        MainDisplay.backToPrevious();
    }
}
