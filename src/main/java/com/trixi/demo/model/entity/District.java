package com.trixi.demo.model.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "district")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class District {

    @Id
    @GeneratedValue
    private int id;
    private String districtId;
    private String code;
    private String name;
    private String villageCode;
    private String validFrom;
    private String transactionId;
    private String globalChangeProposalId;
    @OneToMany(mappedBy = "district")
    private List<LinguisticCharacteristic> linguisticCharacteristics;
    @OneToOne()
    private Geometry geometry;
}
