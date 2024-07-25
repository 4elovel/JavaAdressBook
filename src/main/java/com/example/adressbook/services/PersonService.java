package com.example.adressbook.services;

import com.example.adressbook.DTOs.PersonInsertDTO;
import com.example.adressbook.entities.Person;
import com.example.adressbook.repositories.PersonRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public Page<Person> getAllPersons(Pageable pageable) {
        return personRepository.findAll(pageable);
    }

    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    public Person savePerson(PersonInsertDTO personDTO) {
        Person person = new Person();
        person.setFullName(personDTO.getFullName());
        person.setPhone(personDTO.getPhone());
        person.setEmail(personDTO.getEmail());
        person.setBlogUrl(personDTO.getBlogUrl());
        person.setNotes(personDTO.getNotes());
        return personRepository.save(person);
    }

    public Person updatePerson(Long id, PersonInsertDTO personDTO) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            person.setFullName(personDTO.getFullName());
            person.setPhone(personDTO.getPhone());
            person.setEmail(personDTO.getEmail());
            person.setBlogUrl(personDTO.getBlogUrl());
            person.setNotes(personDTO.getNotes());
            return personRepository.save(person);
        }
        return null;
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public Page<Person> searchPersons(String fullName, Pageable pageable) {
        return personRepository.findByFullNameContainingIgnoreCase(fullName, pageable);
    }
}
