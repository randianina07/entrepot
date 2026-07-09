<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/dto/MouvementCreateDTO.java
package com.gestion.entrepot.dto;
========
package com.entrepot.gestion.dto;

import java.util.ArrayList;
import java.util.List;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/dto/MouvementCreateDTO.java

public class MouvementCreateDTO {
    
    private Long typeMouvementId;
    private Long clientId;
    private String notes;
    private List<LigneMouvementDTO> lignes = new ArrayList<>();
    
    public MouvementCreateDTO() {
    }
    
    public MouvementCreateDTO(Long typeMouvementId, Long clientId, String notes) {
        this.typeMouvementId = typeMouvementId;
        this.clientId = clientId;
        this.notes = notes;
    }
    
    public Long getTypeMouvementId() {
        return typeMouvementId;
    }
    
    public void setTypeMouvementId(Long typeMouvementId) {
        this.typeMouvementId = typeMouvementId;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<LigneMouvementDTO> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneMouvementDTO> lignes) {
        this.lignes = lignes != null ? lignes : new ArrayList<>();
    }
}
