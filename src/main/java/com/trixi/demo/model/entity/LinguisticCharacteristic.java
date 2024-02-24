package com.trixi.demo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "linguisticCharacteristics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class LinguisticCharacteristic {

    @Id
    @GeneratedValue
    private int id;
    private String value;
    @ManyToOne
    private Village village;
    @ManyToOne
    private District district;
}
