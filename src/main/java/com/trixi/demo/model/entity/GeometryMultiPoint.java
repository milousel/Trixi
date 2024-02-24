package com.trixi.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "geometryMultiPoint")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeometryMultiPoint {

    @Id
    @GeneratedValue
    private int id;
    private String multiPointId;
    private String name;
    private String dimension;
    @OneToMany(mappedBy = "multiPoint")
    private List<GeometryPoint> points;

}
