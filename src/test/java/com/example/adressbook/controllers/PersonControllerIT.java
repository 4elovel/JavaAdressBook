package com.example.adressbook.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.adressbook.DTOs.PersonInsertDTO;
import com.example.adressbook.entities.Person;
import com.example.adressbook.repositories.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long person1Id;
    private Long person2Id;

    @BeforeEach
    public void setup() {
        // Очищаємо базу даних перед кожним тестом
        personRepository.deleteAll();

        // Додаємо тестові дані
        Person person1 = new Person();
        person1.setFullName("John Doe");
        person1.setPhone("1234567890");
        person1.setEmail("john.doe@example.com");
        person1.setBlogUrl("https://johndoe.com");
        person1.setNotes("Some notes about John");
        person1Id = personRepository.save(person1).getId();

        Person person2 = new Person();
        person2.setFullName("Jane Smith");
        person2.setPhone("0987654321");
        person2.setEmail("jane.smith@example.com");
        person2.setBlogUrl("https://janesmith.com");
        person2.setNotes("Some notes about Jane");
        person2Id = personRepository.save(person2).getId();
    }

    @Test
    public void getAllPersons_ShouldReturnCorrectData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/persons")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(person1Id))
                .andExpect(jsonPath("$.content[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].phone").value("1234567890"))
                .andExpect(jsonPath("$.content[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.content[0].blogUrl").value("https://johndoe.com"))
                .andExpect(jsonPath("$.content[0].notes").value("Some notes about John"))
                .andExpect(jsonPath("$.content[1].id").value(person2Id))
                .andExpect(jsonPath("$.content[1].fullName").value("Jane Smith"))
                .andExpect(jsonPath("$.content[1].phone").value("0987654321"))
                .andExpect(jsonPath("$.content[1].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.content[1].blogUrl").value("https://janesmith.com"))
                .andExpect(jsonPath("$.content[1].notes").value("Some notes about Jane"));
    }

    @Test
    public void getPersonById_ShouldReturnCorrectPerson_WhenPersonExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/persons/" + person1Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person1Id))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.blogUrl").value("https://johndoe.com"))
                .andExpect(jsonPath("$.notes").value("Some notes about John"));
    }

    @Test
    public void getPersonById_ShouldReturnNotFound_WhenPersonDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/persons/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createPerson_ShouldReturnCreatedPersonWithCorrectData() throws Exception {
        PersonInsertDTO personDTO = new PersonInsertDTO();
        personDTO.setFullName("Alice Johnson");
        personDTO.setPhone("1112223333");
        personDTO.setEmail("alice.johnson@example.com");
        personDTO.setBlogUrl("https://alicejohnson.com");
        personDTO.setNotes("Some notes about Alice");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Alice Johnson"))
                .andExpect(jsonPath("$.phone").value("1112223333"))
                .andExpect(jsonPath("$.email").value("alice.johnson@example.com"))
                .andExpect(jsonPath("$.blogUrl").value("https://alicejohnson.com"))
                .andExpect(jsonPath("$.notes").value("Some notes about Alice"))
                .andReturn();

        // Отримуємо ID створеної особи з відповіді
        String responseBody = result.getResponse().getContentAsString();
        Person createdPerson = objectMapper.readValue(responseBody, Person.class);
        Long createdPersonId = createdPerson.getId();

        // Переконуємось, що ID був згенерований
        assertThat(createdPersonId).isNotNull();

        // Перевіряємо, що запис існує в базі даних
        Person personFromDb = personRepository.findById(createdPersonId).orElse(null);
        assertThat(personFromDb).isNotNull();
        assertThat(personFromDb.getFullName()).isEqualTo("Alice Johnson");
    }

    @Test
    public void updatePerson_ShouldReturnUpdatedPersonWithCorrectData() throws Exception {
        PersonInsertDTO personDTO = new PersonInsertDTO();
        personDTO.setFullName("John Doe Updated");
        personDTO.setPhone("9876543210");
        personDTO.setEmail("john.updated@example.com");
        personDTO.setBlogUrl("https://johnupdated.com");
        personDTO.setNotes("Updated notes about John");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1.0/persons/" + person1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(person1Id))
                .andExpect(jsonPath("$.fullName").value("John Doe Updated"))
                .andExpect(jsonPath("$.phone").value("9876543210"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.blogUrl").value("https://johnupdated.com"))
                .andExpect(jsonPath("$.notes").value("Updated notes about John"));

        // Перевіряємо оновлені дані в базі даних
        Person personFromDb = personRepository.findById(person1Id).orElse(null);
        assertThat(personFromDb).isNotNull();
        assertThat(personFromDb.getFullName()).isEqualTo("John Doe Updated");
        assertThat(personFromDb.getPhone()).isEqualTo("9876543210");
        assertThat(personFromDb.getEmail()).isEqualTo("john.updated@example.com");
    }

    @Test
    public void updatePerson_ShouldReturnNotFound_WhenPersonDoesNotExist() throws Exception {
        PersonInsertDTO personDTO = new PersonInsertDTO();
        personDTO.setFullName("Nonexistent Person");
        personDTO.setPhone("1111111111");
        personDTO.setEmail("nonexistent@example.com");
        personDTO.setBlogUrl("https://nonexistent.com");
        personDTO.setNotes("Notes about a nonexistent person");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1.0/persons/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePerson_ShouldReturnNoContent_WhenPersonExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1.0/persons/" + person1Id))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/persons/" + person1Id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePerson_ShouldReturnNotFound_WhenPersonDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1.0/persons/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchPersons_ShouldReturnCorrectResults() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/persons/search")
                        .param("fullName", "John")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(person1Id))
                .andExpect(jsonPath("$.content[0].fullName").value("John Doe"));
    }
}
