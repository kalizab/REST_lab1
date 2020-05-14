package com.company.enroller.controllers;


import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {

        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
        Meeting foundMeeting = meetingService.findById(meeting.getId());
        if (foundMeeting != null) {
            return new ResponseEntity(
                    "Unable to create. A meeting with id " + meeting.getId() + " already exist.",
                    HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@RequestBody Meeting meeting) {
        Meeting foundMeetingToUpdate = meetingService.findById(meeting.getId());
        if (foundMeetingToUpdate == null) {
            return new ResponseEntity(
                    "Unable to update. A meeting with id " + meeting.getId() + " not exist.",
                    HttpStatus.NOT_FOUND);
        }
        else {
            foundMeetingToUpdate.setTitle(meeting.getTitle());
            foundMeetingToUpdate.setDescription(meeting.getDescription());
            foundMeetingToUpdate.setDate(meeting.getDate());
            return new ResponseEntity<Meeting>(foundMeetingToUpdate, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/participants/{meetingId}", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@RequestBody Participant participant, @PathVariable("meetingId") long meetingId) {
        Meeting foundMeeting = meetingService.findById(meetingId);
        if (foundMeeting == null) {
            return new ResponseEntity(
                    "Unable to add participant to meeting. A meeting with id " + meetingId + " not exist.",
                    HttpStatus.NOT_FOUND);
        }
        else {
            foundMeeting.addParticipant(participant);
            return new ResponseEntity<Participant>(participant, HttpStatus.CREATED);
        }
    }

    @RequestMapping(value = "/participants/{meetingId}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipant(@PathVariable long meetingId) {
        Meeting foundMeeting = meetingService.findById(meetingId);
        if (foundMeeting == null) {
            return new ResponseEntity(
                    "A meeting with id " + meetingId + " not exist.",
                    HttpStatus.NOT_FOUND);
        }
        else {
            Collection<Participant> participants = foundMeeting.getParticipants();
            return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{meetingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("meetingId") long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/participants/{meetingId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipantFromMeeting(@RequestBody Participant participant,
                                                          @PathVariable("meetingId") long meetingId) {
        Meeting foundMeeting = meetingService.findById(meetingId);
        if (foundMeeting == null) {
            return new ResponseEntity(
                    "Unable to delete participant from meeting. A meeting with id " + meetingId + " not exist.",
                    HttpStatus.NOT_FOUND);
        } else {
            foundMeeting.removeParticipant(participant);
            return new ResponseEntity<Participant>(participant, HttpStatus.OK);
        }
    }
}
