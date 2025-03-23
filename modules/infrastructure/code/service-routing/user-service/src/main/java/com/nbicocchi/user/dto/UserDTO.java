package com.nbicocchi.user.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    @NonNull
    @EqualsAndHashCode.Include private String userUUID;
    @NonNull private String nickname;
    @NonNull private LocalDate birthDate;
}

