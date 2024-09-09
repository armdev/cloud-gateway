/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.project.profile.profile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Armen Arzumanyan
 */
@RestController
@RequestMapping("/api/v2/profile")
@Slf4j
public class ProfileController {

    @GetMapping("/fetch")
    public String get(@RequestParam String userId) {
        log.info("USER ID " + userId);
        log.debug("profile debug only");
        log.info("profile info only");
        log.warn("profile warn only");
        log.trace("profile traces only");
        return "profile fetched";
    }

    @PostMapping("/register")
    public Payload post(@RequestBody Payload payload) {
        log.info("payload " + payload.toString());
        log.debug("profile debug only");
        log.info("profile info only");
        log.warn("profile warn only");
        log.trace("profile traces only");
        return payload;
    }

    @GetMapping("/download/me")
    public String fetch() {
        log.info("download access done, need token for access");
        return "download done";
    }

}
