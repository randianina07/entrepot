package entrepot.demo.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "types_mouvement")
public class TypeMouvement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 30)
    private String code;
    
    @Column(nullable = false, length = 100)
    private String libelle;
    
    @Column(nullable = false, length = 10)
    private String sens;
    
    public TypeMouvement() {
    }
    
    public TypeMouvement(Long id, String code, String libelle, String sens) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.sens = sens;
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
    
    public String getSens() {
        return sens;
    }
    
    public void setSens(String sens) {
        this.sens = sens;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeMouvement that = (TypeMouvement) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
    
    @Override
    public String toString() {
        return "TypeMouvement{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", libelle='" + libelle + '\'' +
                ", sens='" + sens + '\'' +
                '}';
    }
}
