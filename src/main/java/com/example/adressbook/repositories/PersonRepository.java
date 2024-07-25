package com.example.adressbook.repositories;

import com.example.adressbook.entities.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Page<Person> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}
