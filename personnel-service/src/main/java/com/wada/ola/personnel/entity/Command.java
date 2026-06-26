package com.wada.ola.personnel.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.wada.ola.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "commands")
public class Command extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommandType type;

    // Cuts serialization at one level — frontend only needs immediate parent's id/name/type,
    // and Jackson can't serialize a HibernateProxy two levels deep without the hibernate-jackson module.
    @JsonIgnoreProperties("parent")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Command parent;

    public enum CommandType {
        CHIEF, ZONE, REGION, UNIT
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CommandType getType() { return type; }
    public void setType(CommandType type) { this.type = type; }

    public Command getParent() { return parent; }
    public void setParent(Command parent) { this.parent = parent; }
}
