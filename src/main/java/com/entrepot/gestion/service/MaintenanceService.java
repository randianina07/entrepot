package com.entrepot.gestion.service;


// import com.entrepot.gestion.repository.Vehicule_repository;

import org.springframework.stereotype.Service;

import com.entrepot.gestion.model.Maintenance_vehicule;
import com.entrepot.gestion.model.Type_maintenance;
import com.entrepot.gestion.model.Vehicule;
import com.entrepot.gestion.repository.MaintenanceRepository;
import com.entrepot.gestion.repository.Maintenance_vehicule_repository;
import com.entrepot.gestion.repository.TypeMaintenanceRepository;
import com.entrepot.gestion.repository.VehiculeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceService {

	private final Maintenance_vehicule_repository maintenanceRepository;
	private final VehiculeRepository vehiculeRepository;
	private final TypeMaintenanceRepository typeMaintenanceRepository;

	public MaintenanceService(
			Maintenance_vehicule_repository maintenanceRepository,
			VehiculeRepository vehiculeRepository,
			TypeMaintenanceRepository typeMaintenanceRepository) {
		this.maintenanceRepository = maintenanceRepository;
		this.vehiculeRepository = vehiculeRepository;
		this.typeMaintenanceRepository = typeMaintenanceRepository;
	}

	public List<Maintenance_vehicule> getHistoriqueMaintenances() {
		return maintenanceRepository.findAll();
	}

	public List<Maintenance_vehicule> getHistoriqueMaintenances(LocalDate dateDebutMaintenance, LocalDate dateFinMaintenance) {
		List<Maintenance_vehicule> maintenances = maintenanceRepository.findAll();

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

	public List<Maintenance_vehicule> getHistoriqueMaintenances(
			LocalDate dateDebutMaintenance,
			LocalDate dateFinMaintenance,
			Long typeMaintenanceId
	) {
		List<Maintenance_vehicule> maintenances = getHistoriqueMaintenances(dateDebutMaintenance, dateFinMaintenance);

		if (typeMaintenanceId != null) {
			maintenances = maintenances.stream()
					.filter(maintenance -> typeMaintenanceId.equals(maintenance.getTypeMaintenance()))
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
	
	public Maintenance_vehicule addMaintenance(Maintenance_vehicule maintenance){
		return maintenanceRepository.save(maintenance);
	}

}
