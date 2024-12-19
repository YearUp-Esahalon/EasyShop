package org.yearup.data;

import org.yearup.models.Profile;

public interface ProfileDao {

    Profile create(Profile profile);

    Profile getByUserId(int userId);  // Fetch profile by user ID

    Profile update(Profile profile);  // Update an existing profile
}
