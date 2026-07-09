<<<<<<<< HEAD:demo/src/main/java/com/gestion/entrepot/dto/LigneMouvementDTO.java
package com.gestion.entrepot.dto;
========
package com.entrepot.gestion.dto;
>>>>>>>> 679f4dd59ceb46a21b28b61111443acba658d085:src/main/java/com/entrepot/gestion/dto/LigneMouvementDTO.java

import java.math.BigDecimal;

public class LigneMouvementDTO {
    
    private Long produitId;
    private Long emplacementSourceId;
    private Long emplacementDestId;
    private BigDecimal quantite;
    
    public LigneMouvementDTO() {
    }
    
    public LigneMouvementDTO(Long produitId, Long emplacementSourceId, Long emplacementDestId, BigDecimal quantite) {
        this.produitId = produitId;
        this.emplacementSourceId = emplacementSourceId;
        this.emplacementDestId = emplacementDestId;
        this.quantite = quantite;
    }
    
    public Long getProduitId() {
        return produitId;
    }
    
    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }
    
    public Long getEmplacementSourceId() {
        return emplacementSourceId;
    }
    
    public void setEmplacementSourceId(Long emplacementSourceId) {
        this.emplacementSourceId = emplacementSourceId;
    }
    
    public Long getEmplacementDestId() {
        return emplacementDestId;
    }
    
    public void setEmplacementDestId(Long emplacementDestId) {
        this.emplacementDestId = emplacementDestId;
    }
    
    public BigDecimal getQuantite() {
        return quantite;
    }
    
    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }
}
