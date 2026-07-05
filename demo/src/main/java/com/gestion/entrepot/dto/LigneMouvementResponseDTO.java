package com.gestion.entrepot.dto;

import java.math.BigDecimal;

public class LigneMouvementResponseDTO {
    
    private Long id;
    private Long produitId;
    private String produitNom;
    private Long emplacementSourceId;
    private String emplacementSourceCode;
    private Long emplacementDestId;
    private String emplacementDestCode;
    private BigDecimal quantite;
    
    public LigneMouvementResponseDTO() {
    }
    
    public LigneMouvementResponseDTO(Long id, Long produitId, String produitNom, Long emplacementSourceId, String emplacementSourceCode, Long emplacementDestId, String emplacementDestCode, BigDecimal quantite) {
        this.id = id;
        this.produitId = produitId;
        this.produitNom = produitNom;
        this.emplacementSourceId = emplacementSourceId;
        this.emplacementSourceCode = emplacementSourceCode;
        this.emplacementDestId = emplacementDestId;
        this.emplacementDestCode = emplacementDestCode;
        this.quantite = quantite;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProduitId() {
        return produitId;
    }
    
    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }
    
    public String getProduitNom() {
        return produitNom;
    }
    
    public void setProduitNom(String produitNom) {
        this.produitNom = produitNom;
    }
    
    public Long getEmplacementSourceId() {
        return emplacementSourceId;
    }
    
    public void setEmplacementSourceId(Long emplacementSourceId) {
        this.emplacementSourceId = emplacementSourceId;
    }
    
    public String getEmplacementSourceCode() {
        return emplacementSourceCode;
    }
    
    public void setEmplacementSourceCode(String emplacementSourceCode) {
        this.emplacementSourceCode = emplacementSourceCode;
    }
    
    public Long getEmplacementDestId() {
        return emplacementDestId;
    }
    
    public void setEmplacementDestId(Long emplacementDestId) {
        this.emplacementDestId = emplacementDestId;
    }
    
    public String getEmplacementDestCode() {
        return emplacementDestCode;
    }
    
    public void setEmplacementDestCode(String emplacementDestCode) {
        this.emplacementDestCode = emplacementDestCode;
    }
    
    public BigDecimal getQuantite() {
        return quantite;
    }
    
    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }
}
