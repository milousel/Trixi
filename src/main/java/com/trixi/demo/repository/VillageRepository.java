package com.trixi.demo.repository;

import com.trixi.demo.model.entity.Village;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VillageRepository extends JpaRepository<Village, Integer> {
}
