package com.gestion.entrepot.dto;

import java.time.LocalDateTime;

public class MouvementListDTO {
    
    private Long id;
    private String code;
    private LocalDateTime dateMouvement;
    private String typeMouvementCode;
    private String typeMouvementLibelle;
    private String statutMouvementCode;
    private String statutMouvementLibelle;
    private String clientEmail;
    private String utilisateurEmail;
    
    public MouvementListDTO() {
    }
    
    public MouvementListDTO(Long id, String code, LocalDateTime dateMouvement, String typeMouvementCode, String typeMouvementLibelle, String statutMouvementCode, String statutMouvementLibelle, String clientEmail, String utilisateurEmail) {
        this.id = id;
        this.code = code;
        this.dateMouvement = dateMouvement;
        this.typeMouvementCode = typeMouvementCode;
        this.typeMouvementLibelle = typeMouvementLibelle;
        this.statutMouvementCode = statutMouvementCode;
        this.statutMouvementLibelle = statutMouvementLibelle;
        this.clientEmail = clientEmail;
        this.utilisateurEmail = utilisateurEmail;
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
    
    public String getClientEmail() {
        return clientEmail;
    }
    
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    
    public String getUtilisateurEmail() {
        return utilisateurEmail;
    }
    
    public void setUtilisateurEmail(String utilisateurEmail) {
        this.utilisateurEmail = utilisateurEmail;
    }
}
