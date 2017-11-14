package com.vortexbird.gluon.plan.presentation.backingBeans;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortexbird.gluon.plan.modelo.SegUsuario;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;
import com.vortexbird.gluon.plan.utilities.FacesUtils;

@ManagedBean
@ViewScoped
public class DashboardVista {
	
	@ManagedProperty(value = "#{BusinessDelegatorView}")
	private IBusinessDelegatorView businessDelegatorView;

	private static final Logger log = LoggerFactory.getLogger(DashboardVista.class);
	
	private SegUsuario usuario;
	private Long totalUsuarios;
	private Long totalPlanes;
	
	public IBusinessDelegatorView getBusinessDelegatorView() {
		return businessDelegatorView;
	}

	public void setBusinessDelegatorView(IBusinessDelegatorView businessDelegatorView) {
		this.businessDelegatorView = businessDelegatorView;
	}
	
	public SegUsuario getUsuario() {
		if(usuario==null) {
			usuario = (SegUsuario) FacesUtils.getfromSession("usuario");
		}
		return usuario;
	}

	public void setUsuario(SegUsuario usuario) {
		this.usuario = usuario;
	}

	public Long getTotalUsuarios() throws Exception {
		this.totalUsuarios = businessDelegatorView.findTotalNumberSegUsuario();
		return totalUsuarios;
	}

	public void setTotalUsuarios(Long totalUsuarios) {
		this.totalUsuarios = totalUsuarios;
	}

	public Long getTotalPlanes() throws Exception {
		this.totalPlanes = businessDelegatorView.findTotalNumberGluoPlanDesarrollo();
		return totalPlanes;
	}

	public void setTotalPlanes(Long totalPlanes) {
		this.totalPlanes = totalPlanes;
	}
}