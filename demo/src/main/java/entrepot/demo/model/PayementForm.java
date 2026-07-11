package entrepot.demo.model;

import java.util.ArrayList;
import java.util.List;

public class PayementForm {
    
    List<Payement> payements = new ArrayList<>();
    
    public List<Payement> getPayements() {
        return payements;
    }

    public void setPayements(List<Payement> payements) {
        this.payements = payements;
    }
    
}
