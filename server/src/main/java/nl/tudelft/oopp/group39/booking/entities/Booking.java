package nl.tudelft.oopp.group39.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import nl.tudelft.oopp.group39.booking.dto.BookingDto;
import nl.tudelft.oopp.group39.config.abstracts.AbstractEntity;
import nl.tudelft.oopp.group39.room.entities.Room;
import nl.tudelft.oopp.group39.user.entities.User;

@Entity
@Table(name = Booking.TABLE_NAME)
@JsonIgnoreProperties(allowSetters = true, value = {
    Booking.COL_USER,
    Booking.COL_ROOM
})

public class Booking extends AbstractEntity<Booking, BookingDto> {
    public static final String TABLE_NAME = "bookings";
    public static final String MAPPED_NAME = "booking";
    public static final String COL_DATE = "date";
    public static final String COL_START_TIME = "startTime";
    public static final String COL_END_TIME = "endTime";
    public static final String COL_USER = "user";
    public static final String COL_ROOM = "room";

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = User.MAPPED_NAME)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = Room.MAPPED_NAME)
    private Room room;

    /**
     * Creates a booking.
     */
    public Booking() {
    }

    /**
     * Creates a booking.
     *
     * @param id        id
     * @param date      date
     * @param startTime startTime
     * @param endTime   endTime
     * @param user      user
     * @param room      room
     */
    public Booking(
        Long id,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        User user,
        Room room
    ) {
        setId(id);
        setDate(date);
        setStartTime(startTime);
        setEndTime(endTime);
        setUser(user);
        setRoom(room);
    }

    /**
     * Gets the date of the booking.
     *
     * @return the date of the booking
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Changes the date of the booking.
     *
     * @param date the new date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Gets the starting time.
     *
     * @return the starting time of the booking
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Changes the starting time of the booking.
     *
     * @param startTime the new starting time
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the ending time of the booking.
     *
     * @return the ending time of the booking
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Changes the ending time of the booking.
     *
     * @param endTime the new ending time
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the user who made the booking.
     *
     * @return the user who made the booking
     */
    public User getUser() {
        return user;
    }

    /**
     * Changes the user of the booking.
     *
     * @param user the new user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the room that is booked.
     *
     * @return the room that is booked
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Changes the room that is booked.
     *
     * @param room the new room
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Changes a Booking to a BookingDto object.
     *
     * @return a BookingDto object
     */
    @Override
    public BookingDto toDto() {
        return new BookingDto(
            getId(),
            getDate(),
            getStartTime(),
            getEndTime(),
            user == null ? null : getUser().getUsername(),
            room == null ? null : getRoom().getId()
        );
    }

    /**
     * Checks whether two bookings are equal.
     *
     * @param o the other object to be checked
     * @return true if the bookings are the same, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Booking booking = (Booking) o;
        return Objects.equals(getId(), booking.getId())
            && Objects.equals(getDate(), booking.getDate())
            && Objects.equals(getStartTime(), booking.getStartTime())
            && Objects.equals(getEndTime(), booking.getEndTime())
            && Objects.equals(getUser(), booking.getUser())
            && Objects.equals(getRoom(), booking.getRoom());
    }
}

