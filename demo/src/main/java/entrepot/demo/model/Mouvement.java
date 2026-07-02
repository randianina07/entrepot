package entrepot.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "mouvements")
public class Mouvement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 30)
    private String code;
    
    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_mouvement_id", nullable = false)
    private TypeMouvement typeMouvement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statut_mouvement_id", nullable = false)
    private StatutMouvement statutMouvement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Utilisateur client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
    
    private String notes;
    
    @OneToMany(mappedBy = "mouvement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneMouvement> lignes = new ArrayList<>();
    
    public Mouvement() {
    }
    
    public Mouvement(Long id, String code, LocalDateTime dateMouvement, TypeMouvement typeMouvement, StatutMouvement statutMouvement, Utilisateur client, Utilisateur utilisateur, String notes, List<LigneMouvement> lignes) {
        this.id = id;
        this.code = code;
        this.dateMouvement = dateMouvement;
        this.typeMouvement = typeMouvement;
        this.statutMouvement = statutMouvement;
        this.client = client;
        this.utilisateur = utilisateur;
        this.notes = notes;
        this.lignes = lignes != null ? lignes : new ArrayList<>();
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
    
    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }
    
    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }
    
    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }
    
    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }
    
    public StatutMouvement getStatutMouvement() {
        return statutMouvement;
    }
    
    public void setStatutMouvement(StatutMouvement statutMouvement) {
        this.statutMouvement = statutMouvement;
    }
    
    public Utilisateur getClient() {
        return client;
    }
    
    public void setClient(Utilisateur client) {
        this.client = client;
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<LigneMouvement> getLignes() {
        return lignes;
    }
    
    public void setLignes(List<LigneMouvement> lignes) {
        this.lignes = lignes != null ? lignes : new ArrayList<>();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mouvement mouvement = (Mouvement) o;
        return Objects.equals(id, mouvement.id) && Objects.equals(code, mouvement.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
    
    @Override
    public String toString() {
        return "Mouvement{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", dateMouvement=" + dateMouvement +
                ", statutMouvement=" + statutMouvement +
                '}';
    }
}
