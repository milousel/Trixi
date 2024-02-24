package com.trixi.demo.repository;

import com.trixi.demo.model.entity.GeometryPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeometryPointRepository extends JpaRepository<GeometryPoint, Integer> {
}
