package com.trixi.demo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "geometryPoint")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeometryPoint {
    @Id
    @GeneratedValue
    private int id;
    private String pointId;
    private String pos;
    private String name;
    private String dimension;
    @ManyToOne
    private GeometryMultiPoint multiPoint;
}
