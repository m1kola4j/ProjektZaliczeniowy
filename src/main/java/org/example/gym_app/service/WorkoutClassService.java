package org.example.gym_app.service;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.WorkoutClass;
import org.example.gym_app.repository.WorkoutClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutClassService {

    private final WorkoutClassRepository workoutClassRepository;

    public List<WorkoutClass> getAllClasses() {
        return workoutClassRepository.findAll();
    }

    public WorkoutClass addClass(WorkoutClass workoutClass) {
        return workoutClassRepository.save(workoutClass);
    }

    public WorkoutClass getClassById(Long id) {
        return workoutClassRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zajęcia nie istnieją."));
    }

    public void deleteClass(Long id) {
        workoutClassRepository.deleteById(id);
    }
}
