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

        // 1. sprawdź czy user istnieje
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik o id " + userId + " nie istnieje"));

        // 2. sprawdź czy zajęcia istnieją
        WorkoutClass workoutClass = workoutClassRepository.findById(workoutClassId)
                .orElseThrow(() -> new RuntimeException("Zajęcia o id " + workoutClassId + " nie istnieją"));

        // 3. sprawdź, czy są wolne miejsca
        int count = bookingRepository.countByWorkoutClass_Id(workoutClassId);
        if (count >= workoutClass.getCapacity()) {
            throw new RuntimeException("Brak wolnych miejsc na te zajęcia!");
        }

        // 4. sprawdź, czy user nie jest już zapisany
        if (bookingRepository.existsByUser_IdAndWorkoutClass_Id(userId, workoutClassId)) {
            throw new RuntimeException("Użytkownik jest już zapisany na te zajęcia!");
        }

        // 5. utwórz rezerwację
        Booking booking = new Booking();
        booking.setUser(user);                // <-- tu ustawiamy obiekt User
        booking.setWorkoutClass(workoutClass); // <-- i obiekt WorkoutClass

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }
}
