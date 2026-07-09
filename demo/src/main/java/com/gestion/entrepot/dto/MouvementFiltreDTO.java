<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/dto/MouvementFiltreDTO.java
package com.gestion.entrepot.dto;
========
package com.entrepot.gestion.dto;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/dto/MouvementFiltreDTO.java

import java.time.LocalDateTime;

public class MouvementFiltreDTO {
    
    private String type;
    private String statut;
    private Long clientId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    
    // For frontend compatibility with String dates
    private String dateDebutStr;
    private String dateFinStr;
    
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
    
    public String getDateDebutStr() {
        return dateDebutStr;
    }
    
    public void setDateDebutStr(String dateDebutStr) {
        this.dateDebutStr = dateDebutStr;
    }
    
    public String getDateFinStr() {
        return dateFinStr;
    }
    
    public void setDateFinStr(String dateFinStr) {
        this.dateFinStr = dateFinStr;
    }
}
