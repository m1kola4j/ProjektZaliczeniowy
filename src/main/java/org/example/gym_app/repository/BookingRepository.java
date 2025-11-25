package org.example.gym_app.repository;

import org.example.gym_app.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ile rezerwacji jest na dane zajęcia (po id klasy)
    int countByWorkoutClass_Id(Long workoutClassId);

    // czy dany user jest już zapisany na dane zajęcia
    boolean existsByUser_IdAndWorkoutClass_Id(Long userId, Long workoutClassId);

    // wszystkie rezerwacje danego usera
    List<Booking> findByUser_Id(Long userId);
}
