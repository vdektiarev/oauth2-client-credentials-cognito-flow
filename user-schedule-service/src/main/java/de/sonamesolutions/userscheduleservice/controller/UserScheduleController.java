package de.sonamesolutions.userscheduleservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-schedule")
public class UserScheduleController {

    @GetMapping("/schedule")
    public String getUserSchedule(@RequestParam String userName) {
        return userName + " is available!";
    }

    @PutMapping("/schedule/{id}")
    public String updateSchedule(@PathVariable String id) {
        return id + " has been updated!";
    }
}
