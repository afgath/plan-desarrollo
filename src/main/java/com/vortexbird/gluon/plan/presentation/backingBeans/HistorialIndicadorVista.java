package com.vortexbird.gluon.plan.presentation.backingBeans;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortexbird.gluon.plan.modelo.GluoHistorialIndicador;
import com.vortexbird.gluon.plan.modelo.GluoIndicador;
import com.vortexbird.gluon.plan.modelo.dto.GluoPlanDesarrolloDTO;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;

import static java.lang.Math.toIntExact;

@ManagedBean
@ViewScoped
public class HistorialIndicadorVista {

	@ManagedProperty(value = "#{BusinessDelegatorView}")
	private IBusinessDelegatorView businessDelegatorView;

	private static final Logger log = LoggerFactory.getLogger(HistorialIndicadorVista.class);

	private List<GluoIndicador> losIndicadores;
	private List<GluoHistorialIndicador> losHistorialesIndicadores;
	private List<GluoPlanDesarrolloDTO> data;
	private Integer selectedGluoPlanDesarrollo;

	public void cargarIndicador(ActionEvent evt) {
		selectedGluoPlanDesarrollo = (Integer) (evt.getComponent().getAttributes().get("selectedGluoPlanDesarrollo"));
		
		String query = "SELECT * FROM gluo_indicador WHERE proy_id IN (\n" + 
				"	SELECT proy_id FROM gluo_proyecto  WHERE subp_id IN (\n" + 
				"		SELECT subp_id FROM gluo_subprograma  WHERE prog_id IN (\n" + 
				"			SELECT prog_id FROM gluo_programa  WHERE obje_id IN (\n" + 
				"				SELECT obje_id FROM gluo_objetivo  WHERE sedi_id IN (\n" + 
				"					SELECT sedi_id FROM gluo_sector_eje_dimension WHERE plan_id = "+selectedGluoPlanDesarrollo+")))));";
		
		String query2 = "SELECT ind FROM GluoIndicador ind WHERE proyId IN (\n" + 
				"	SELECT proy.proyId FROM GluoProyecto proy WHERE subpId IN (\n" + 
				"		SELECT subPrg.subpId FROM GluoSubprograma subPrg WHERE progId IN (\n" + 
				"			SELECT prg.progId FROM GluoPrograma prg WHERE objeId IN (\n" + 
				"				SELECT obj.objeId FROM GluoObjetivo obj WHERE sediId IN (\n" + 
				"					SELECT eje.sediId FROM GluoSectorEjeDimension eje WHERE planId = "+selectedGluoPlanDesarrollo+")))));";
		
		losIndicadores = (List<GluoIndicador>) businessDelegatorView.findBySqlGluoIndicador(query2);
		
		log.info("Plan seleccionado: " + selectedGluoPlanDesarrollo);

	}

	public IBusinessDelegatorView getBusinessDelegatorView() {
		return businessDelegatorView;
	}

	public void setBusinessDelegatorView(IBusinessDelegatorView businessDelegatorView) {
		this.businessDelegatorView = businessDelegatorView;
	}

	public List<GluoIndicador> getLosIndicadores() {
		return losIndicadores;
	}

	public void setLosIndicadores(List<GluoIndicador> losIndicadores) {
		this.losIndicadores = losIndicadores;
	}

	public List<GluoPlanDesarrolloDTO> getData() {
		try {
			if (data == null) {
				data = businessDelegatorView.getDataGluoPlanDesarrollo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public void setData(List<GluoPlanDesarrolloDTO> gluoPlanDesarrolloDTO) {
		this.data = gluoPlanDesarrolloDTO;
	}

	public Integer getSelectedGluoPlanDesarrollo() {
		return selectedGluoPlanDesarrollo;
	}

	public void setSelectedGluoPlanDesarrollo(Integer selectedGluoPlanDesarrollo) {
		this.selectedGluoPlanDesarrollo = selectedGluoPlanDesarrollo;
	}

	public List<GluoHistorialIndicador> getLosHistorialesIndicadores() {
		return losHistorialesIndicadores;
	}

	public void setLosHistorialesIndicadores(List<GluoHistorialIndicador> losHistorialesIndicadores) {
		this.losHistorialesIndicadores = losHistorialesIndicadores;
	}
}
