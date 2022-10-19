package com.construction_worker_forum_back.model.dto;

import com.construction_worker_forum_back.model.entity.Comment;
import com.construction_worker_forum_back.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private User user;
    private List<Comment> comments;
}
