package entrepot.demo.service;

import entrepot.demo.entity.Maintenance;
import entrepot.demo.entity.TypeMaintenance;
import entrepot.demo.model.Vehicule;
import entrepot.demo.repository.MaintenanceRepository;
import entrepot.demo.repository.TypeMaintenanceRepository;
import entrepot.demo.repository.Vehicule_repository;
// import entrepot.demo.repository.Vehicule_repository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceService {

	private final MaintenanceRepository maintenanceRepository;
	private final Vehicule_repository vehiculeRepository;
	private final TypeMaintenanceRepository typeMaintenanceRepository;

	public MaintenanceService(
			MaintenanceRepository maintenanceRepository,
			Vehicule_repository vehiculeRepository,
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

	public List<TypeMaintenance> getTypesMaintenance() {
		return typeMaintenanceRepository.findAll();
	}
	
	public Maintenance addMaintenance(Maintenance maintenance){
		return maintenanceRepository.save(maintenance);
	}

}
