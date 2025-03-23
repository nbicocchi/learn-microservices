package com.nbicocchi.bff.dto;

import java.time.LocalDate;
import java.util.HashSet;

public record UserOutDTO(String nickname, LocalDate birthDate, HashSet<PostOutDTO> posts) {
}
