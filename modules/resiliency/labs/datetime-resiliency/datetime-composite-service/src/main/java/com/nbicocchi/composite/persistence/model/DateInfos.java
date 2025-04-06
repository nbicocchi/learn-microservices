package com.nbicocchi.composite.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(name = "infos")
public class DateInfos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NonNull @EqualsAndHashCode.Include int month;
    @NonNull @EqualsAndHashCode.Include int day;
    @NonNull String info;
}
