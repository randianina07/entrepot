package com.gestion.entrepot.dto;

import java.time.LocalDateTime;

public class MouvementFiltreDTO {
    
    private String type;
    private String statut;
    private Long clientId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    
    public MouvementFiltreDTO() {
    }
    
    public MouvementFiltreDTO(String type, String statut, Long clientId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        this.type = type;
        this.statut = statut;
        this.clientId = clientId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDateTime getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }
}
