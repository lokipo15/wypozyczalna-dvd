package com.studia.wypozyczalnia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studia.wypozyczalnia.domain.Title;

public interface TitleRepository extends JpaRepository<Title, Long> {

    Optional<Title> findByNameIgnoreCase(String name);

    List<Title> findByNameContainingIgnoreCase(String query);
}
