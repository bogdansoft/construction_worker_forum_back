package com.construction_worker_forum_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@Table(name = "keywords")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keyword implements Serializable {
    @Serial
    private static final long serialVersionUID = -6470090944414208496L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
