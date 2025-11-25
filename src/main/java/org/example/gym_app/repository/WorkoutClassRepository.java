package org.example.gym_app.repository;

import org.example.gym_app.model.WorkoutClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutClassRepository extends JpaRepository<WorkoutClass, Long> {
}
