package com.trixi.demo.repository;

import com.trixi.demo.model.entity.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeometryRepository extends JpaRepository<Geometry, Integer> {
}
