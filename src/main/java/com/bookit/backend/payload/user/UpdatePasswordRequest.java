package com.bookit.backend.payload.user;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {
    @Size(min = 8)
    private String currentPassword;

    @Size(min = 8)
    private String newPassword;
}
