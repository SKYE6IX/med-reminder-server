package com.medreminder.medreminder_server.domain.models;

public class Profile {

    private String id;
    private String name;
    private Relation relation;
    private boolean isSelf;

    public Profile(String name) {
        this.name = name;
        this.isSelf = true;
        this.relation = Relation.SELF;
    }

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
}
