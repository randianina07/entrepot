package entrepot.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import entrepot.demo.repositories.TypeZoneRepository;
import entrepot.demo.model.TypeZone;

@Service
public class TypeZoneService {
    
    @Autowired
    private TypeZoneRepository typeZoneRepository;

    public List<TypeZone> getAll() {
        List<TypeZone> listeTypeZone = new ArrayList<>();
        listeTypeZone = typeZoneRepository.findAll();
        if (listeTypeZone != null) {
            return listeTypeZone;
        } else {
            return new ArrayList<>();
        }
    }
}
