package com.medreminder.medreminder_server.infrastructure.entity.users;


import com.medreminder.medreminder_server.infrastructure.entity.medications.MedicationProfileEntity;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "PROFILES")
public class ProfileEntity {

    @Id
    @GeneratedValue()
    @UuidGenerator
    private String id;

    private String name;

    private String relation;

    @Column(name = "is_self")
    private boolean isSelf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @OneToMany(
            mappedBy = "profile",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("createdAt ASC")
    private final List<MedicationProfileEntity> medicationProfile = new ArrayList<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ProfileEntity() {
    }

    public ProfileEntity(String id,
                         String name,
                         String relation,
                         boolean isSelf) {
        this.id = id;
        this.name = name;
        this.relation = relation;
        this.isSelf = isSelf;
    }

    public ProfileEntity(String id,
                         String name,
                         String relation,
                         boolean isSelf,
                         UserEntity user) {
        this(id, name, relation, isSelf);
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRelation() {
        return relation;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public UserEntity getUser() {
        return user;
    }

    public List<MedicationProfileEntity> getMedicationProfile() {
        return medicationProfile;
    }
}