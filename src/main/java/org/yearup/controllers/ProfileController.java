package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
public class ProfileController {

    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    // Create a new profile
    @PreAuthorize("permitAll()")
    @PostMapping
    public Profile create(@RequestBody Profile profile) {
        return profileDao.create(profile);
    }

    // Get profile by userId from the logged-in user's information
    @GetMapping
    public Profile getProfile(Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        // Fetch profile by user ID
        Profile profile = profileDao.getByUserId(user.getId());
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        return profile;
    }

    // Update the profile for the logged-in user
    @PutMapping
    public Profile updateProfile(@RequestBody Profile profile, Principal principal) {
        String username = principal.getName();
        int userId = userDao.getByUserName(username).getId();
        profile.setUserId(userId);  // Make sure the profile belongs to the correct user

        // Update the profile and return the updated profile
        return profileDao.update(profile);
    }
}
