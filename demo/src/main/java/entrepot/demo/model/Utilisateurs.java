package entrepot.demo.model;


import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
// @Getter
// @Setter
public class Utilisateurs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String user_email;

    @Column(name = "mot_de_passe_hash")
    private String motDePasseHash;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    // Getters et Setters
}