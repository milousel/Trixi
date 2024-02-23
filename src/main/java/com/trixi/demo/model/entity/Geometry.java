package com.trixi.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "geometry")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Geometry {

    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    private GeoemtryMultiPoint multiPoint;
}
