package com.trixi.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "village")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Village {

    @Id
    @GeneratedValue
    private int id;
    private String villageId;
    private String code;
    private String name;
    private int status;
    private String region;
    private String pui;
    private String validFrom;
    private String transactionId;
    private String globalChangeProposalId;
    @OneToMany(mappedBy = "village")
    private List<LinguisticCharacteristic> linguisticCharacteristics;
    private String nutsLau;
    @OneToOne()
    private Geometry geometry;
}
