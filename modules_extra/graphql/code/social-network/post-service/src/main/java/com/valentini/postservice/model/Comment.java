package com.valentini.postservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private Long id;
    private User user;
    private String content;
}
