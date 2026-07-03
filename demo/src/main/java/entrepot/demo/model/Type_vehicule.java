package entrepot.demo.model;

// import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "types_vehicule")
public class Type_vehicule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String code;
    String libelle;
    String description;

    public long getId(){
        return this.id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getCode(){
        return this.code;
    }

    public void setCode(String code){
        this.code = code;        
    }

    public String getLibelle(){
        return this.libelle;
    }

    public void setLibelle(String libelle){
        this.libelle = libelle;
    }
    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
