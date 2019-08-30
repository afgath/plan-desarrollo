package com.vortexbird.gluon.plan.presentation.backingBeans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.component.checkbox.Checkbox;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.password.Password;
import org.primefaces.component.selectcheckboxmenu.SelectCheckboxMenu;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vortexbird.gluon.plan.modelo.SegOpcion;
import com.vortexbird.gluon.plan.modelo.SegPermiso;
import com.vortexbird.gluon.plan.modelo.SegRol;
import com.vortexbird.gluon.plan.modelo.SegRolUsuario;
import com.vortexbird.gluon.plan.modelo.SegUsuario;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;
import com.vortexbird.gluon.plan.utilities.FacesUtils;

import static java.lang.Math.toIntExact;

@ManagedBean
@ViewScoped
public class RolesVista {

	@ManagedProperty(value = "#{BusinessDelegatorView}")
	private IBusinessDelegatorView businessDelegatorView;

	private static final Logger log = LoggerFactory.getLogger(RolesVista.class);

	private List<SegRol> losRoles;
	private SegRol selectRol;
	private List<SegOpcion> opciones;
	private String[] selectedOpciones;
	private List<SelectItem> lasOpcionesItem;

	private void setearOpciones(int idRol) throws Exception {

		try {
			Object[] variables = new Object[4];

			variables[0] = "rolId";
			variables[1] = false;
			variables[2] = idRol;
			variables[3] = "=";

			List<SegPermiso> permisos = businessDelegatorView.findByCriteriaInSegPermiso(variables, null, null);
			
			opciones =  new ArrayList<SegOpcion>();

			for (SegPermiso permiso : permisos) {

				opciones.add(businessDelegatorView.getSegOpcion(permiso.getSegOpcion().getOpcnId()));
			}

			this.lasOpcionesItem = new ArrayList<SelectItem>();
			for (SegOpcion opcion : opciones) {
				this.lasOpcionesItem.add(new SelectItem(opcion.getOpcnId(), opcion.getNombre()));
			}
			
			log.info("Tamaño:   "+this.lasOpcionesItem.size());

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public List<SegOpcion> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<SegOpcion> opciones) {
		this.opciones = opciones;
	}

	public String[] getSelectedOpciones() {
		return selectedOpciones;
	}

	public void setSelectedOpciones(String[] selectedOpciones) {
		this.selectedOpciones = selectedOpciones;
	}

	public List<SelectItem> getLasOpcionesItem() {
		return this.lasOpcionesItem;
	}

	public IBusinessDelegatorView getBusinessDelegatorView() {
		return businessDelegatorView;
	}

	public void setBusinessDelegatorView(IBusinessDelegatorView businessDelegatorView) {
		this.businessDelegatorView = businessDelegatorView;
	}

	public List<SegRol> getLosRoles() throws Exception {
		if (losRoles == null) {
			losRoles = businessDelegatorView.getSegRol();
		}
		return losRoles;
	}

	public void setLosRoles(List<SegRol> losRoles) {
		this.losRoles = losRoles;
	}

	public SegRol getSelectRol() {
		return selectRol;
	}

	public void setSelectRol(SegRol selectRol) throws Exception {
		log.info("seleccionado "+selectRol.getRolId());
		this.selectRol = selectRol;
		this.setearOpciones(selectRol.getRolId());
	}

}
