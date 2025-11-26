package org.example.gym_app.repository;

import org.example.gym_app.model.JournalEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findAllByUser_IdOrderBySessionDateDesc(Long userId);

    List<JournalEntry> findAllByUser_Id(Long userId, Sort sort);
}

