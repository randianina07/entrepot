package entrepot.demo.service;

import entrepot.demo.model.Mode_payement;
import entrepot.demo.repositories.Mode_payement_repository;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mode_payement_service {

    @Autowired
    Mode_payement_repository mode_de_payement_repo;

    public List<Mode_payement> getAllMode_payement () {

        return mode_de_payement_repo.findAll();

    }

}
