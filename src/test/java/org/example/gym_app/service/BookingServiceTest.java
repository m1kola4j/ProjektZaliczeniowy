package org.example.gym_app.service;

import org.example.gym_app.model.Booking;
import org.example.gym_app.model.User;
import org.example.gym_app.model.WorkoutClass;
import org.example.gym_app.repository.BookingRepository;
import org.example.gym_app.repository.UserRepository;
import org.example.gym_app.repository.WorkoutClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutClassRepository workoutClassRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private WorkoutClass workoutClass;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        workoutClassRepository.deleteAll();
        userRepository.deleteAll();

        User baseUser = new User();
        baseUser.setUsername("anna");
        baseUser.setPassword("secret");
        baseUser.setRole("USER");
        user = userRepository.save(baseUser);

        WorkoutClass baseClass = new WorkoutClass();
        baseClass.setName("Yoga");
        baseClass.setDescription("Stretch & relax");
        baseClass.setCapacity(2);
        baseClass.setStartTime(LocalDateTime.now().plusDays(1));
        workoutClass = workoutClassRepository.save(baseClass);
    }

    @Test
    @DisplayName("Użytkownik może zapisać się na zajęcia gdy są wolne miejsca")
    void shouldCreateBookingWhenSlotAvailable() {
        Booking booking = bookingService.createBooking(user.getId(), workoutClass.getId());
        Long bookingId = booking.getId();

        assertThat(bookingId).isNotNull();
        assertThat(bookingRepository.countByWorkoutClass_Id(workoutClass.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("Nie można dodać duplikatu rezerwacji dla tego samego użytkownika i zajęć")
    void shouldRejectDuplicateBooking() {
        bookingService.createBooking(user.getId(), workoutClass.getId());

        assertThatThrownBy(() -> bookingService.createBooking(user.getId(), workoutClass.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Już jesteś zapisany");
    }

    @Test
    @DisplayName("Nie można zapisać więcej osób niż pojemność zajęć")
    void shouldRejectWhenCapacityExceeded() {
        User secondUser = new User();
        secondUser.setUsername("ewa");
        secondUser.setPassword("secret");
        secondUser.setRole("USER");
        secondUser = userRepository.save(secondUser);

        bookingService.createBooking(user.getId(), workoutClass.getId());
        bookingService.createBooking(secondUser.getId(), workoutClass.getId());

        User thirdUser = new User();
        thirdUser.setUsername("ola");
        thirdUser.setPassword("secret");
        thirdUser.setRole("USER");
        thirdUser = userRepository.save(thirdUser);
        Long thirdUserId = thirdUser.getId();
        Long workoutClassId = workoutClass.getId();

        assertThatThrownBy(() -> bookingService.createBooking(thirdUserId, workoutClassId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Brak wolnych miejsc");
    }

    @Test
    @DisplayName("Użytkownik widzi tylko swoje rezerwacje i może je anulować")
    void shouldListAndCancelOwnBookings() {
        Booking booking = bookingService.createBooking(user.getId(), workoutClass.getId());
        Long bookingId = booking.getId();

        List<Booking> bookings = bookingService.getBookingsForUser(user.getId());
        assertThat(bookings).hasSize(1);

        bookingService.cancelBooking(bookingId, user.getId());
        assertThat(bookingRepository.count()).isZero();
    }

    @Test
    @DisplayName("Nie można anulować rezerwacji innego użytkownika")
    void shouldNotCancelOtherUsersBooking() {
        Booking booking = bookingService.createBooking(user.getId(), workoutClass.getId());
        Long victimBookingId = booking.getId();

        User attacker = new User();
        attacker.setUsername("intruder");
        attacker.setPassword("secret");
        attacker.setRole("USER");
        attacker = userRepository.save(attacker);
        Long attackerId = attacker.getId();

        assertThatThrownBy(() -> bookingService.cancelBooking(victimBookingId, attackerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Nie możesz anulować");
    }
}

