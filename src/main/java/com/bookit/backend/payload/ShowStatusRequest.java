package com.bookit.backend.payload;

import com.bookit.backend.model.ShowStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowStatusRequest {

    @NotNull
    private ShowStatus status;
}