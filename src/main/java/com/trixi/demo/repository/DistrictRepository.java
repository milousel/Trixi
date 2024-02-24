package com.trixi.demo.repository;

import com.trixi.demo.model.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Integer> {
}
