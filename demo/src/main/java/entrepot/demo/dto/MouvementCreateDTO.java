package entrepot.demo.dto;

public class MouvementCreateDTO {
    
    private Long typeMouvementId;
    private Long clientId;
    private String notes;
    
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
}
