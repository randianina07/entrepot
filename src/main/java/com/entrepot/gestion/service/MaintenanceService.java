package com.entrepot.gestion.service;


// import com.entrepot.gestion.repository.Vehicule_repository;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Type_maintenance;
import com.entrepot.gestion.model.Vehicule;
import com.entrepot.gestion.repository.Maintenance_vehicule_repository;
import com.entrepot.gestion.repository.TypeMaintenanceRepository;
import com.entrepot.gestion.repository.VehiculeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceService {

	private final MaintenanceRepository maintenanceRepository;
	private final VehiculeRepository vehiculeRepository;
	private final TypeMaintenanceRepository typeMaintenanceRepository;

	public MaintenanceService(
			MaintenanceRepository maintenanceRepository,
			VehiculeRepository vehiculeRepository,
			TypeMaintenanceRepository typeMaintenanceRepository) {
		this.maintenanceRepository = maintenanceRepository;
		this.vehiculeRepository = vehiculeRepository;
		this.typeMaintenanceRepository = typeMaintenanceRepository;
	}

	public List<Maintenance> getHistoriqueMaintenances() {
		return maintenanceRepository.findAll();
	}

	public List<Maintenance> getHistoriqueMaintenances(LocalDate dateDebutMaintenance, LocalDate dateFinMaintenance) {
		List<Maintenance> maintenances = maintenanceRepository.findAll();

		if (dateDebutMaintenance != null && dateFinMaintenance != null) {
			maintenances = maintenances.stream()
					.filter(maintenance -> maintenance.getDateMaintenance() != null)
					.filter(maintenance ->
							!maintenance.getDateMaintenance().isBefore(dateDebutMaintenance)
							&& !maintenance.getDateMaintenance().isAfter(dateFinMaintenance))
					.collect(Collectors.toList());
		}

		return maintenances;
	}

	public List<Maintenance> getHistoriqueMaintenances(
			LocalDate dateDebutMaintenance,
			LocalDate dateFinMaintenance,
			Long typeMaintenanceId
	) {
		List<Maintenance> maintenances = getHistoriqueMaintenances(dateDebutMaintenance, dateFinMaintenance);

		if (typeMaintenanceId != null) {
			maintenances = maintenances.stream()
					.filter(maintenance -> typeMaintenanceId.equals(maintenance.getTypeMaintenanceId()))
					.toList();
		}

		return maintenances;
	}

	public List<Vehicule> getVehicules() {
		return vehiculeRepository.findAll();
	}

	public List<Type_maintenance> getTypesMaintenance() {
		return typeMaintenanceRepository.findAll();
	}
	
	public Maintenance addMaintenance(Maintenance maintenance){
		return maintenanceRepository.save(maintenance);
	}

}
