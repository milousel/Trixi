package com.trixi.demo.model.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "geometry")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class Geometry {

    @Id
    @GeneratedValue
    private int id;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private GeometryMultiPoint multiPoint;
}
