package com.trixi.demo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "linguisticCharacteristics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinguisticCharacteristic {

    @Id
    @GeneratedValue
    private int id;
    private String value;
    @ManyToOne
    private Village village;
}
