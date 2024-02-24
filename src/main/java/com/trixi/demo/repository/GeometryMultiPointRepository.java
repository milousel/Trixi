package com.trixi.demo.repository;

import com.trixi.demo.model.entity.GeometryMultiPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeometryMultiPointRepository extends JpaRepository<GeometryMultiPoint, Integer> {
}
