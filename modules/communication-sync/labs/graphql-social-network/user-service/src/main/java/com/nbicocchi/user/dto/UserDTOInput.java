package com.nbicocchi.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTOInput {
    @NonNull @EqualsAndHashCode.Include private String userUUID;
    @NonNull private String nickname;
    private String birthDate;
}

