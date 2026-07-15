package com.entrepot.gestion.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "allees")
public class Allees {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    long id;
    String code;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

}
