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

    // Utworzenie rezerwacji z kontrolą duplikatu i limitu miejsc
    @Transactional
    public Booking createBooking(Long userId, Long workoutClassId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje."));

        WorkoutClass workoutClass = workoutClassRepository.findById(workoutClassId)
                .orElseThrow(() -> new RuntimeException("Zajęcia nie istnieją."));

        // 1) Czy user już zapisany?
        if (bookingRepository.existsByUser_IdAndWorkoutClass_Id(userId, workoutClassId)) {
            throw new RuntimeException("Już jesteś zapisany na te zajęcia!");
        }

        // 2) Czy są wolne miejsca?
        int currentCount = bookingRepository.countByWorkoutClass_Id(workoutClassId);
        if (currentCount >= workoutClass.getCapacity()) {
            throw new RuntimeException("Brak wolnych miejsc na te zajęcia.");
        }

        // 3) Tworzymy rezerwację
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setWorkoutClass(workoutClass);

        return bookingRepository.save(booking);
    }

    // Wszystkie rezerwacje danego usera
    @Transactional(readOnly = true)
    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    // Ile wolnych miejsc zostało na danych zajęciach (capacity - istniejące rezerwacje)
    @Transactional(readOnly = true)
    public int getFreeSpotsForClass(Long workoutClassId) {
        WorkoutClass workoutClass = workoutClassRepository.findById(workoutClassId)
                .orElseThrow(() -> new RuntimeException("Zajęcia nie istnieją."));

        int taken = bookingRepository.countByWorkoutClass_Id(workoutClassId);
        return workoutClass.getCapacity() - taken;
    }

    // Anulowanie rezerwacji (sprawdzamy, czy to rezerwacja tego usera)
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Rezerwacja nie istnieje."));

        if (!booking.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nie możesz anulować cudzej rezerwacji.");
        }

        bookingRepository.delete(booking);
    }
}
