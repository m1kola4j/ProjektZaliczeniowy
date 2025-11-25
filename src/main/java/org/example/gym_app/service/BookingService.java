package org.example.gym_app.service;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.Booking;
import org.example.gym_app.model.User;
import org.example.gym_app.model.WorkoutClass;
import org.example.gym_app.repository.BookingRepository;
import org.example.gym_app.repository.UserRepository;
import org.example.gym_app.repository.WorkoutClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final WorkoutClassRepository workoutClassRepository;

    public Booking createBooking(Long userId, Long workoutClassId) {

        // 1) czy już zapisany na te zajęcia?
        if (bookingRepository.existsByUser_IdAndWorkoutClass_Id(userId, workoutClassId)) {
            throw new RuntimeException("Już jesteś zapisany na te zajęcia!");
        }

        // 2) czy zajęcia istnieją?
        WorkoutClass workoutClass = workoutClassRepository.findById(workoutClassId)
                .orElseThrow(() -> new RuntimeException("Wybrane zajęcia nie istnieją."));

        // 3) limit miejsc
        int currentCount = bookingRepository.countByWorkoutClass_Id(workoutClassId);
        if (currentCount >= workoutClass.getCapacity()) {
            throw new RuntimeException("Brak wolnych miejsc na te zajęcia!");
        }

        // 4) czy user istnieje?
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje."));

        // 5) zapis rezerwacji
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setWorkoutClass(workoutClass);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    // Przyda się później do "Anuluj rezerwację"
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Taka rezerwacja nie istnieje."));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nie możesz usunąć cudzej rezerwacji.");
        }

        bookingRepository.delete(booking);
    }
}
