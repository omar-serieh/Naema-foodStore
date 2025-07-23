package com.universityproject.webapp.foodstore.service;

import com.universityproject.webapp.foodstore.entity.EventParticipant;
import com.universityproject.webapp.foodstore.entity.Events;
import com.universityproject.webapp.foodstore.repository.CharitiesRepository;
import com.universityproject.webapp.foodstore.repository.EventParticipantRepository;
import com.universityproject.webapp.foodstore.repository.EventRepository;
import com.universityproject.webapp.foodstore.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipantRepository participantRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CharitiesRepository charitiesRepository;

    @Override
    public Events createEvent(Events event) {
        event.setCreatedBy(usersRepository.findById(event.getCreatedBy().getUserId()).orElseThrow());
        event.setTargetCharity(charitiesRepository.findById(event.getTargetCharity().getCharityId()).orElseThrow());
        return eventRepository.save(event);
    }

    @Override
    public List<Events> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public EventParticipant participateInEvent(EventParticipant participant) {
        participant.setUser(usersRepository.findById(participant.getUser().getUserId()).orElseThrow());
        participant.setEvent(eventRepository.findById(participant.getEvent().getEventId()).orElseThrow());
        participant.setJoinedAt(new Timestamp(System.currentTimeMillis()));
        return participantRepository.save(participant);
    }

    @Override
    public List<EventParticipant> getParticipantsByEventId(int eventId) {
        return participantRepository.findByEvent_EventId(eventId);
    }

    @Override
    public List<EventParticipant> getParticipantsByType(int eventId, EventParticipant.ParticipationType type) {
        return participantRepository.findByEvent_EventIdAndParticipationType(eventId, type);
    }
}


