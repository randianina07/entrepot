package entrepot.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "maintenances_vehicule")
public class Maintenance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "vehicule_id", nullable = false)
	private Long vehiculeId;

	@Column(name = "type_maintenance_id", nullable = false)
	private Long typeMaintenanceId;

	@Column(name = "date_maintenance", nullable = false)
	private LocalDate dateMaintenance;

	private BigDecimal kilometrage;

	private BigDecimal cout;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "prochaine_maintenance")
	private LocalDate prochaineMaintenance;

	public Maintenance() {
	}

	public Maintenance(Long vehiculeId, Long typeMaintenanceId, LocalDate dateMaintenance, BigDecimal kilometrage, BigDecimal cout, String description, LocalDate prochaineMaintenance) {
		this.vehiculeId = vehiculeId;
		this.typeMaintenanceId = typeMaintenanceId;
		this.dateMaintenance = dateMaintenance;
		this.kilometrage = kilometrage;
		this.cout = cout;
		this.description = description;
		this.prochaineMaintenance = prochaineMaintenance;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVehiculeId() {
		return vehiculeId;
	}

	public void setVehiculeId(Long vehiculeId) {
		this.vehiculeId = vehiculeId;
	}

	public Long getTypeMaintenanceId() {
		return typeMaintenanceId;
	}

	public void setTypeMaintenanceId(Long typeMaintenanceId) {
		this.typeMaintenanceId = typeMaintenanceId;
	}

	public LocalDate getDateMaintenance() {
		return dateMaintenance;
	}

	public void setDateMaintenance(LocalDate dateMaintenance) {
		this.dateMaintenance = dateMaintenance;
	}

	public BigDecimal getKilometrage() {
		return kilometrage;
	}

	public void setKilometrage(BigDecimal kilometrage) {
		this.kilometrage = kilometrage;
	}

	public BigDecimal getCout() {
		return cout;
	}

	public void setCout(BigDecimal cout) {
		this.cout = cout;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getProchaineMaintenance() {
		return prochaineMaintenance;
	}

	public void setProchaineMaintenance(LocalDate prochaineMaintenance) {
		this.prochaineMaintenance = prochaineMaintenance;
	}
}
