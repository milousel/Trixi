package com.trixi.demo.repository;

import com.trixi.demo.model.entity.LinguisticCharacteristic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinguisticCharacteristicRepository extends JpaRepository<LinguisticCharacteristic, Integer> {
}
