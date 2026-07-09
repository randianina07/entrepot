<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/dto/MouvementDetailDTO.java
package com.gestion.entrepot.dto;
========
package com.entrepot.gestion.dto;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/dto/MouvementDetailDTO.java

import java.time.LocalDateTime;
import java.util.List;

public class MouvementDetailDTO {
    
    private Long id;
    private String code;
    private LocalDateTime dateMouvement;
    private Long typeMouvementId;
    private String typeMouvementCode;
    private String typeMouvementLibelle;
    private String sens;
    private Long statutMouvementId;
    private String statutMouvementCode;
    private String statutMouvementLibelle;
    private Long clientId;
    private String clientEmail;
    private Long utilisateurId;
    private String utilisateurEmail;
    private String notes;
    private List<LigneMouvementResponseDTO> lignes;
    
    public MouvementDetailDTO() {
    }
    
    public MouvementDetailDTO(Long id, String code, LocalDateTime dateMouvement, Long typeMouvementId, String typeMouvementCode, String typeMouvementLibelle, String sens, Long statutMouvementId, String statutMouvementCode, String statutMouvementLibelle, Long clientId, String clientEmail, Long utilisateurId, String utilisateurEmail, String notes, List<LigneMouvementResponseDTO> lignes) {
        this.id = id;
        this.code = code;
        this.dateMouvement = dateMouvement;
        this.typeMouvementId = typeMouvementId;
        this.typeMouvementCode = typeMouvementCode;
        this.typeMouvementLibelle = typeMouvementLibelle;
        this.sens = sens;
        this.statutMouvementId = statutMouvementId;
        this.statutMouvementCode = statutMouvementCode;
        this.statutMouvementLibelle = statutMouvementLibelle;
        this.clientId = clientId;
        this.clientEmail = clientEmail;
        this.utilisateurId = utilisateurId;
        this.utilisateurEmail = utilisateurEmail;
        this.notes = notes;
        this.lignes = lignes;
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
    
    public Long getTypeMouvementId() {
        return typeMouvementId;
    }
    
    public void setTypeMouvementId(Long typeMouvementId) {
        this.typeMouvementId = typeMouvementId;
    }
    
    public String getTypeMouvementCode() {
        return typeMouvementCode;
    }
    
    public void setTypeMouvementCode(String typeMouvementCode) {
        this.typeMouvementCode = typeMouvementCode;
    }
    
    public String getTypeMouvementLibelle() {
        return typeMouvementLibelle;
    }
    
    public void setTypeMouvementLibelle(String typeMouvementLibelle) {
        this.typeMouvementLibelle = typeMouvementLibelle;
    }
    
    public String getSens() {
        return sens;
    }
    
    public void setSens(String sens) {
        this.sens = sens;
    }
    
    public Long getStatutMouvementId() {
        return statutMouvementId;
    }
    
    public void setStatutMouvementId(Long statutMouvementId) {
        this.statutMouvementId = statutMouvementId;
    }
    
    public String getStatutMouvementCode() {
        return statutMouvementCode;
    }
    
    public void setStatutMouvementCode(String statutMouvementCode) {
        this.statutMouvementCode = statutMouvementCode;
    }
    
    public String getStatutMouvementLibelle() {
        return statutMouvementLibelle;
    }
    
    public void setStatutMouvementLibelle(String statutMouvementLibelle) {
        this.statutMouvementLibelle = statutMouvementLibelle;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String getClientEmail() {
        return clientEmail;
    }
    
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    
    public Long getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
    
    public String getUtilisateurEmail() {
        return utilisateurEmail;
    }
    
    public void setUtilisateurEmail(String utilisateurEmail) {
        this.utilisateurEmail = utilisateurEmail;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<LigneMouvementResponseDTO> getLignes() {
        return lignes;
    }
    
    public void setLignes(List<LigneMouvementResponseDTO> lignes) {
        this.lignes = lignes;
    }
}
