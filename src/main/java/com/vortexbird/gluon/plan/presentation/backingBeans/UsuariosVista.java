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

import com.vortexbird.gluon.plan.dto.mapper.ISegUsuarioMapper;
import com.vortexbird.gluon.plan.dto.mapper.SegUsuarioMapper;
import com.vortexbird.gluon.plan.modelo.SegOpcion;
import com.vortexbird.gluon.plan.modelo.SegRol;
import com.vortexbird.gluon.plan.modelo.SegRolUsuario;
import com.vortexbird.gluon.plan.modelo.SegUsuario;
import com.vortexbird.gluon.plan.modelo.dto.SegUsuarioDTO;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;
import com.vortexbird.gluon.plan.utilities.FacesUtils;

import static java.lang.Math.toIntExact;

@ManagedBean
@ViewScoped
public class UsuariosVista {
	
	@ManagedProperty(value = "#{BusinessDelegatorView}")
	private IBusinessDelegatorView businessDelegatorView;
	
	private static final Logger log = LoggerFactory.getLogger(UsuariosVista.class);
	
	private List<SegUsuarioDTO> losUsuarios;
	
	public String redirectToRegistro() {
		return "registro.xhtml";
	}
	
	public IBusinessDelegatorView getBusinessDelegatorView() {
		return businessDelegatorView;
	}

	public void setBusinessDelegatorView(IBusinessDelegatorView businessDelegatorView) {
		this.businessDelegatorView = businessDelegatorView;
	}

	public List<SegUsuarioDTO> getLosUsuarios() throws Exception {
		if(losUsuarios==null) {
			
			losUsuarios=businessDelegatorView.getDataSegUsuario();
			log.info("Despues del mapeado");
		}
		return losUsuarios;
	}

	public void setLosUsuarios(List<SegUsuarioDTO> losUsuarios) {
		this.losUsuarios = losUsuarios;
	}
}
