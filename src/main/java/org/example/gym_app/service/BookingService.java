package org.example.gym_app.service;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.Booking;
import org.example.gym_app.model.User;
import org.example.gym_app.model.WorkoutClass;
import org.example.gym_app.repository.BookingRepository;
import org.example.gym_app.repository.UserRepository;
import org.example.gym_app.repository.WorkoutClassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final WorkoutClassRepository workoutClassRepository;

    @Transactional
    public Booking createBooking(Long userId, Long workoutClassId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        WorkoutClass workoutClass = workoutClassRepository.findById(workoutClassId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono zajęć"));

        // 1) sprawdź czy user nie jest już zapisany
        if (bookingRepository.existsByUser_IdAndWorkoutClass_Id(userId, workoutClassId)) {
            throw new RuntimeException("Już jesteś zapisany na te zajęcia!");
        }

        // 2) sprawdź limit miejsc
        int count = bookingRepository.countByWorkoutClass_Id(workoutClassId);
        if (count >= workoutClass.getCapacity()) {
            throw new RuntimeException("Brak wolnych miejsc na te zajęcia!");
        }

        // 3) zapisz rezerwację
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setWorkoutClass(workoutClass);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    // anulowanie rezerwacji
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji."));

        // zabezpieczenie: user może anulować tylko swoją rezerwację
        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nie możesz anulować cudzej rezerwacji.");
        }

        bookingRepository.delete(booking);
    }
}
