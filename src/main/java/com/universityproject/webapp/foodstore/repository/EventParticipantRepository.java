package com.universityproject.webapp.foodstore.repository;

import com.universityproject.webapp.foodstore.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Integer> {
    List<EventParticipant> findByEvent_EventId(int eventId);
    List<EventParticipant> findByEvent_EventIdAndParticipationType(int eventId, EventParticipant.ParticipationType type);

}


