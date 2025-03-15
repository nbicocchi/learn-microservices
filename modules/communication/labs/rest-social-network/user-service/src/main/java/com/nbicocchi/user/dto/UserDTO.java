package com.nbicocchi.user.controller.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class UserDTO {
    @NonNull @EqualsAndHashCode.Include private String userUUID;
    @NonNull private String nickname;
    @NonNull private LocalDate birthDate;
    private Set<PostDTO> posts = new HashSet<>();
}

