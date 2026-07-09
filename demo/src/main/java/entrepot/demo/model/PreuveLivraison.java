package entrepot.demo.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "preuves_livraison")
public class PreuveLivraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "livraison_id")
    private Livraison livraison;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "signature_client")
    private String signatureClient;

    @Column(name = "photo_colis")
    private String photoColis;

    @Column(name = "commentaire")
    private String commentaire;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Livraison getLivraison() {
        return livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
    }

    public LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }

    public String getSignatureClient() {
        return signatureClient;
    }

    public void setSignatureClient(String signatureClient) {
        this.signatureClient = signatureClient;
    }

    public String getPhotoColis() {
        return photoColis;
    }

    public void setPhotoColis(String photoColis) {
        this.photoColis = photoColis;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    // Getters et Setters
}
