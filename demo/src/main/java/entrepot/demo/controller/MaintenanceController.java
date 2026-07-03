package entrepot.demo.controller;

import entrepot.demo.entity.Maintenance;
import entrepot.demo.service.MaintenanceService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/maintenances")
public class MaintenanceController {

	private final MaintenanceService maintenanceService;

	public MaintenanceController(MaintenanceService maintenanceService) {
		this.maintenanceService = maintenanceService;
	}

	@GetMapping({"", "/", "/historique", "/historique/"})
	public String historiqueMaintenances(Model model) {
		List<Maintenance> maintenances = maintenanceService.getHistoriqueMaintenances();
		model.addAttribute("maintenances", maintenances);
		return "historiqueMaintenances";
	}

	@GetMapping({"/ajouter", "/ajouter/"})
	public String showAddForm(Model model) {
		model.addAttribute("vehicules", maintenanceService.getVehicules());
		model.addAttribute("typesMaintenances", maintenanceService.getTypesMaintenance());
		return "formulaireAjoutMaintenances";
	}

	@PostMapping({"/ajouter", "/ajouter/"})
	public String addMaintenance(
			@RequestParam Long vehiculeId,
			@RequestParam Long typeMaintenanceId,
			@RequestParam LocalDate dateMaintenance,
			@RequestParam(required = false) BigDecimal kilometrage,
			@RequestParam(required = false) BigDecimal cout,
			@RequestParam(required = false) String description,
			@RequestParam(required = false) LocalDate prochaineMaintenance
	) {
		Maintenance maintenance = new Maintenance(
				vehiculeId,
				typeMaintenanceId,
				dateMaintenance,
				kilometrage,
				cout,
				description,
				prochaineMaintenance
		);
		maintenanceService.addMaintenance(maintenance);
		return "redirect:/maintenances";
	}

}
