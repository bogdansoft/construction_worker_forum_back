package com.construction_worker_forum_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Table(name = "keywords")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Keyword {

    @Id
    private String name;
}
