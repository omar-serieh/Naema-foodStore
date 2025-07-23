package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.EventParticipant;
import com.universityproject.webapp.foodstore.entity.Users;
import com.universityproject.webapp.foodstore.repository.EventParticipantRepository;
import com.universityproject.webapp.foodstore.repository.EventRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.Timestamp;

@RestController
@RequestMapping("/participants")
public class EventParticipantController {

    @Autowired
    private EventParticipantRepository participantRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/me")
    public ResponseEntity<List<ParticipantViewDTO>> getMyParticipationHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        Users user = usersRepository.findByEmail(userDetails.getUsername());
        List<EventParticipant> list = participantRepository.findAll().stream()
                .filter(p -> p.getUser().getUserId() == user.getUserId())
                .collect(Collectors.toList());

        List<ParticipantViewDTO> dtoList = list.stream()
                .map(p -> new ParticipantViewDTO(
                        p.getEvent().getEventId(),
                        p.getEvent().getTitle(),
                        p.getParticipationType().name(),
                        p.getJoinedAt()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
    @DeleteMapping("/leave/{eventId}")
    public ResponseEntity<Void> leaveEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable int eventId
    ) {
        Users user = usersRepository.findByEmail(userDetails.getUsername());

        List<EventParticipant> all = participantRepository.findAll();

        EventParticipant match = all.stream()
                .filter(p -> p.getUser().getUserId() == user.getUserId() &&
                        p.getEvent().getEventId() == eventId)
                .findFirst()
                .orElse(null);

        if (match == null) {
            return ResponseEntity.notFound().build();
        }

        participantRepository.delete(match);
        return ResponseEntity.noContent().build();
    }



    public static class ParticipantViewDTO {
        private  int eventId;
        private String eventTitle;
        private String participationType;
        private Timestamp joinedAt;

        public ParticipantViewDTO(int eventId,String eventTitle, String participationType, Timestamp joinedAt) {
            this.eventId = eventId;
            this.eventTitle = eventTitle;
            this.participationType = participationType;
            this.joinedAt = joinedAt;
        }

        // Getters

        public int getEventId() {
            return eventId;
        }
        public String getEventTitle() {
            return eventTitle;
        }

        public String getParticipationType() {
            return participationType;
        }

        public Timestamp getJoinedAt() {
            return joinedAt;
        }
    }
}
