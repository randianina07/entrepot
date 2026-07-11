package entrepot.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entrepot.demo.model.Payement;
import entrepot.demo.repositories.Payement_repository;

@Service
public class Payement_service {
    
    @Autowired
    Payement_repository payement_repository;

    public void insert_payement(Payement payement) {

        payement_repository.save(payement);

    }

}
