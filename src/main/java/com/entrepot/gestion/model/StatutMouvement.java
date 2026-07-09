package com.entrepot.gestion.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "statuts_mouvement")
public class StatutMouvement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String code;
    
    @Column(nullable = false, length = 50)
    private String libelle;
    
    @Column(nullable = false)
    private Integer ordre;
    
    public StatutMouvement() {
    }
    
    public StatutMouvement(Long id, String code, String libelle, Integer ordre) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.ordre = ordre;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public Integer getOrdre() {
        return ordre;
    }
    
    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatutMouvement that = (StatutMouvement) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
    
    @Override
    public String toString() {
        return "StatutMouvement{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", libelle='" + libelle + '\'' +
                ", ordre=" + ordre +
                '}';
    }
}
