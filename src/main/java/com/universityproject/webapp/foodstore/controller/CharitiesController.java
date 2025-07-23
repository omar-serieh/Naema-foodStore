package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.Charities;
import com.universityproject.webapp.foodstore.repository.CharitiesRepository;
import com.universityproject.webapp.foodstore.service.CharitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/charities")
public class CharitiesController {
    @Autowired
    private CharitiesService charitiesService;
    @Autowired
    private CharitiesRepository charitiesRepository;


    @GetMapping
    public List<Charities> getAllCharities() {
        return charitiesService.getAllCharities();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Charities> getCharitiesById(@PathVariable int id) {
        Optional<Charities> charities = charitiesRepository.findById(id);
        return ResponseEntity.ok(charities.get());
    }


    @PostMapping
    public Charities createCharity(@RequestBody Charities charity) {
        return charitiesService.createCharity(charity);
    }

    @DeleteMapping("/{charityId}")
    public ResponseEntity<String> deleteCharityById(@PathVariable int charityId) {
        try {
            charitiesService.deleteCharityById(charityId);
            return ResponseEntity.ok("Charity with ID " + charityId + " has been deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    public static class DeleteCharityRequest {
        private int id;

        public DeleteCharityRequest() {
        }

        public DeleteCharityRequest(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
