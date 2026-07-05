package com.gestion.statistique.model;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FluxJournalierDto {
    private LocalDate  jour;
    private BigDecimal volumeEntreesM3;
    private BigDecimal volumeSortiesM3;
    private Long       nbEntrees;
    private Long       nbSorties;
    private BigDecimal qteEntrees;
    private BigDecimal qteSorties;

    // Getters / Setters
    public LocalDate getJour() {
        return jour;
    }
    public void setJour(LocalDate jour) {
        this.jour = jour;
    }
    public BigDecimal getVolumeEntreesM3() {
        return volumeEntreesM3;
    }
    public void setVolumeEntreesM3(BigDecimal volumeEntreesM3) {
        this.volumeEntreesM3 = volumeEntreesM3;
    }
    public BigDecimal getVolumeSortiesM3() {
        return volumeSortiesM3;
    }
    public void setVolumeSortiesM3(BigDecimal volumeSortiesM3) {
        this.volumeSortiesM3 = volumeSortiesM3;
    }
    public Long getNbEntrees() {
        return nbEntrees;
    }
    public void setNbEntrees(Long nbEntrees) {
        this.nbEntrees = nbEntrees;
    }
    public Long getNbSorties() {
        return nbSorties;
    }
    public void setNbSorties(Long nbSorties) {
        this.nbSorties = nbSorties;
    }
    public BigDecimal getQteEntrees() {
        return qteEntrees;
    }
    public void setQteEntrees(BigDecimal qteEntrees) {
        this.qteEntrees = qteEntrees;
    }
    public BigDecimal getQteSorties() {
        return qteSorties;
    }
    public void setQteSorties(BigDecimal qteSorties) {
        this.qteSorties = qteSorties;
    }
}
