package com.universityproject.webapp.foodstore.controller;

import com.universityproject.webapp.foodstore.entity.*;
import com.universityproject.webapp.foodstore.service.EventService;
import com.universityproject.webapp.foodstore.service.NotificationService;
import com.universityproject.webapp.foodstore.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private NotificationService notificationService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Events> createEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody EventDTO dto
    ) {
        Users creator = usersService.getUserByEmail(userDetails.getUsername());

        Events event = new Events();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setCreatedBy(creator);

        Charities charity = new Charities();
        charity.setCharityId(dto.getCharityId());
        event.setTargetCharity(charity);

        Events createdEvent = eventService.createEvent(event);

        List<Users> allUsers = usersService.getAllUsers();

        String msg = String.format(
                "📍 %s\n\n📝 Description: %s\n📅 Date: %s\n📌 Location: %s",
                createdEvent.getTitle(),
                createdEvent.getDescription(),
                createdEvent.getEventDate().toString(),
                createdEvent.getLocation()
        );


        for (Users user : allUsers) {
            notificationService.createNotification(
                    new Notification(
                            user,
                            "📢 New Event Launched!",
                            msg,
                            Notification.NotificationType.PROMO
                    )
            );
        }


        return ResponseEntity.ok(createdEvent);




    }


    @GetMapping
    public ResponseEntity<List<Events>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping("/participate")
    public ResponseEntity<EventParticipant> joinEvent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ParticipationDTO dto
    ) {
        Users user = usersService.getUserByEmail(userDetails.getUsername());

        EventParticipant participant = new EventParticipant();
        participant.setUser(user);

        Events event = new Events();
        event.setEventId(dto.getEventId());
        participant.setEvent(event);

        participant.setParticipationType(
                EventParticipant.ParticipationType.valueOf(dto.getParticipationType().toUpperCase())
        );

        return ResponseEntity.ok(eventService.participateInEvent(participant));
    }


    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<ParticipantViewDTO>> getAllParticipants(@PathVariable int eventId) {
        List<EventParticipant> list = eventService.getParticipantsByEventId(eventId);
        List<ParticipantViewDTO> dtoList = list.stream().map(p ->
                new ParticipantViewDTO(p.getUser().getUserName(), p.getParticipationType().name(), p.getJoinedAt())
        ).toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{eventId}/participants/{type}")
    public ResponseEntity<List<ParticipantViewDTO>> getParticipantsByType(@PathVariable int eventId, @PathVariable String type) {
        EventParticipant.ParticipationType participationType = EventParticipant.ParticipationType.valueOf(type.toUpperCase());
        List<EventParticipant> list = eventService.getParticipantsByType(eventId, participationType);
        List<ParticipantViewDTO> dtoList = list.stream().map(p ->
                new ParticipantViewDTO(p.getUser().getUserName(), p.getParticipationType().name(), p.getJoinedAt())
        ).toList();
        return ResponseEntity.ok(dtoList);
    }

    public static class EventDTO {
        private String title;
        private String description;
        private String location;
        private Date eventDate;
        private int charityId;
        // Getters and Setters

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Date getEventDate() {
            return eventDate;
        }

        public void setEventDate(Date eventDate) {
            this.eventDate = eventDate;
        }
        public int getCharityId() {
            return charityId;
        }

        public void setCharityId(int charityId) {
            this.charityId = charityId;
        }
    }

    public static class ParticipationDTO {
        private int eventId;
        private String participationType;
        public int getEventId() {
            return eventId;
        }

        public void setEventId(int eventId) {
            this.eventId = eventId;
        }

        public String getParticipationType() {
            return participationType;
        }

        public void setParticipationType(String participationType) {
            this.participationType = participationType;
        }
    }

    public static class ParticipantViewDTO {
        private String userName;
        private String participationType;
        private Timestamp joinedAt;

        public ParticipantViewDTO(String userName, String participationType, Timestamp joinedAt) {
            this.userName = userName;
            this.participationType = participationType;
            this.joinedAt = joinedAt;
        }

        // Getters

        public String getUserName() {
            return userName;
        }

        public String getParticipationType() {
            return participationType;
        }

        public Timestamp getJoinedAt() {
            return joinedAt;
        }
    }
}

