package com.nbicocchi.bff.dto;

import lombok.*;

import java.time.LocalDate;

@Data
public class UserDTO {
    @NonNull
    @EqualsAndHashCode.Include private String userUUID;
    @NonNull private String nickname;
    @NonNull private LocalDate birthDate;
}
