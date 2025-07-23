package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.EventParticipant;
import com.universityproject.webapp.foodstore.entity.Events;

import java.util.List;

public interface EventService {
    Events createEvent(Events event);
    List<Events> getAllEvents();
    EventParticipant participateInEvent(EventParticipant participant);
    List<EventParticipant> getParticipantsByEventId(int eventId);
    List<EventParticipant> getParticipantsByType(int eventId, EventParticipant.ParticipationType type);
}


