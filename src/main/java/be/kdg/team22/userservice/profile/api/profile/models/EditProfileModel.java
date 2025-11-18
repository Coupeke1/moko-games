package be.kdg.team22.userservice.profile.api.profile.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditProfileModel(
        @NotBlank
        @Size(min = 2, max = 50)
        String username,

        @NotBlank
        @Email
        String email
) {
}
