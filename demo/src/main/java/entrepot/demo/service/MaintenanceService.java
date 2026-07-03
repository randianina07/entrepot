package entrepot.demo.service;

import entrepot.demo.entity.Maintenance;
import entrepot.demo.entity.TypeMaintenance;
import entrepot.demo.entity.Vehicule;
import entrepot.demo.repository.MaintenanceRepository;
import entrepot.demo.repository.TypeMaintenanceRepository;
import entrepot.demo.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
