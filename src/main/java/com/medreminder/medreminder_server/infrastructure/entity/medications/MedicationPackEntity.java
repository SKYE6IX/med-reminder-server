package com.medreminder.medreminder_server.infrastructure.entity.medications;


import com.medreminder.medreminder_server.domain.models.medication.MedicationProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity(name = "MEDICATION_PACKS")
public class MedicationPackEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    @Column(name = "total_quantity")
    private double totalQuantity;

    @Column(name = "current_quantity")
    private double currentQuantity;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

//    private MedicationProfile medicationProfile;
}
