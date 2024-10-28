package com.valentini.postservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String avatarPath;
}
