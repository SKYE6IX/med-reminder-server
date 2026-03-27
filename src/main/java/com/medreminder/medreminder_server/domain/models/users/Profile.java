package com.medreminder.medreminder_server.domain.models.users;

import com.medreminder.medreminder_server.domain.models.medication.MedicationProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Profile {

    private final String id;
    private final String name;
    private final Relation relation;
    private final boolean isSelf;
    private User user;
    private final List<MedicationProfile> medicationProfiles = new ArrayList<>();

    public Profile(String id, String name, Relation relation, boolean isSelf) {
        this.id = id;
        this.name = name;
        this.relation = relation;
        this.isSelf = isSelf;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Relation getRelation() {
        return relation;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public User getUser() {
        return user;
    }

    public List<MedicationProfile> getMedicationProfiles() {
        return medicationProfiles;
    }

    void setUser(User user) {
        this.user = user;
    }

    public void addMedicationProfile(MedicationProfile medicationProfile) {
        medicationProfiles.add(medicationProfile);
        medicationProfile.setProfile(this);
    }

    public void removeMedicationProfile(MedicationProfile medicationProfile) {
        medicationProfiles.remove(medicationProfile);
        medicationProfile.setProfile(null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || getClass() != obj.getClass()){
            return false;
        }
        Profile that = (Profile) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
