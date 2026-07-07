package entrepot.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "types_produits")
public class TypeProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "code", nullable = false, unique = true, length = 40)
    private String code;


    @Column(name = "libelle", nullable = false, length = 150)
    private String libelle;


    public TypeProduit() {
    }


    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
}