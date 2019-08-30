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
import com.vortexbird.gluon.plan.modelo.SegRol;
import com.vortexbird.gluon.plan.modelo.SegRolUsuario;
import com.vortexbird.gluon.plan.modelo.SegUsuario;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;
import com.vortexbird.gluon.plan.utilities.FacesUtils;

import static java.lang.Math.toIntExact;

import java.io.Console;

@ManagedBean
@ViewScoped
public class RegistroVista {
	
	@ManagedProperty(value = "#{BusinessDelegatorView}")
	private IBusinessDelegatorView businessDelegatorView;

	private static final Logger log = LoggerFactory.getLogger(RegistroVista.class);

	//Variables información usuario
	private InputText txtLogin;
	private Password passContraseña;
	private Password passRepetirContraseña;
	
	//Variables permisos
	private SelectCheckboxMenu somRol;
	private List<SelectItem> losRolesItem;
	private String[] selectRoles;
	
	//Variables botones
	private CommandButton btnRegistrar;
	private CommandButton btnLimpiar;
	
	private List<SegOpcion> lasOpciones;
	
	
	public String registrarAction() {
		try {
			SegUsuario usuario = new SegUsuario();
			
			if(!passContraseña.getValue().toString().equals(passRepetirContraseña.getValue().toString())) {
				throw new Exception("Las contraseñas no son iguales");
			}
			
			if ( !userNameDisponible(txtLogin.getValue().toString()) ) {
				throw new Exception("El nombre de usuario ya esta en uso");
			}
			
			usuario.setUsuLogin(txtLogin.getValue().toString());
			usuario.setUsuPassword(passContraseña.getValue().toString());
			usuario.setActivo("A");
			usuario.setFechaCreacion(new Date());
			SegUsuario usuCreador = (SegUsuario) FacesUtils.getfromSession("usuario");
			usuario.setUsuCreador(usuCreador.getUsuId());
			
			businessDelegatorView.saveSegUsuario(usuario);
			
			Long idUsuario = businessDelegatorView.findTotalNumberSegUsuario();
			
			if(selectRoles.length==0) {
				throw new Exception("Debe escoger al menos un rol");
			}
			
			for(int i=0; i<selectRoles.length; i++) {
				
				SegRolUsuario rolUsuario = new SegRolUsuario();
				
				rolUsuario.setActivo("A");
				rolUsuario.setFechaCreacion(new Date());
				rolUsuario.setUsuCreador(usuCreador.getUsuId());
				rolUsuario.setSegUsuario(businessDelegatorView.getSegUsuario(toIntExact(idUsuario)));
				rolUsuario.setSegRol(businessDelegatorView.getSegRol(Integer.parseInt(selectRoles[i])));
				
				businessDelegatorView.saveSegRolUsuario(rolUsuario);
			}
			
			usuario = null;
			selectRoles = null;
			
			FacesUtils.addInfoMessage("El usuario se registro correctamente");
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
		return "";
	}
	
	private boolean userNameDisponible(String userName) throws Exception {
		
		Object[] variables = new Object[4];
		variables[0] = "usuLogin";
		variables[1] = true;
		variables[2] = userName;
		variables[3] = "=";
		
		List<SegUsuario> usuarios = businessDelegatorView.findByCriteriaInSegUsuario(variables, null, null);
		
		if(usuarios==null || usuarios.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public String limpiar() {
		try {
			txtLogin.resetValue();
			passContraseña.resetValue();
			passRepetirContraseña.resetValue();
			
			somRol.resetValue();
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
		
		return "";
	}
	
	public IBusinessDelegatorView getBusinessDelegatorView() {
		return businessDelegatorView;
	}

	public void setBusinessDelegatorView(IBusinessDelegatorView businessDelegatorView) {
		this.businessDelegatorView = businessDelegatorView;
	}
	
	public InputText getTxtLogin() {
		return txtLogin;
	}

	public void setTxtLogin(InputText txtLogin) {
		this.txtLogin = txtLogin;
	}

	public Password getPassContraseña() {
		return passContraseña;
	}

	public void setPassContraseña(Password passContraseña) {
		this.passContraseña = passContraseña;
	}

	public Password getPassRepetirContraseña() {
		return passRepetirContraseña;
	}

	public void setPassRepetirContraseña(Password passRepetirContraseña) {
		this.passRepetirContraseña = passRepetirContraseña;
	}

	public SelectCheckboxMenu  getSomRol() {
		return somRol;
	}

	public void setSomRol(SelectCheckboxMenu somRol) {
		this.somRol = somRol;
	}

	public List<SelectItem> getLosRolesItem() throws Exception {
		if(losRolesItem==null) {
			losRolesItem = new ArrayList<SelectItem>();
			List<SegRol> roles = businessDelegatorView.getSegRol();
			for(SegRol rol : roles) {
				losRolesItem.add(new SelectItem(rol.getRolId(), rol.getNombre()));
			}
		}
		return losRolesItem;
	}

	public void setLosRolesItem(List<SelectItem> losRolesItem) {
		this.losRolesItem = losRolesItem;
	}

	public String[] getSelectRoles() {
		return selectRoles;
	}

	public void setSelectRoles(String[] selectRoles) {
		this.selectRoles = selectRoles;
	}

	public CommandButton getBtnRegistrar() {
		return btnRegistrar;
	}

	public void setBtnRegistrar(CommandButton btnRegistrar) {
		this.btnRegistrar = btnRegistrar;
	}

	public CommandButton getBtnLimpiar() {
		return btnLimpiar;
	}

	public void setBtnLimpiar(CommandButton btnLimpiar) {
		this.btnLimpiar = btnLimpiar;
	}

	public List<SegOpcion> getLasOpciones() throws Exception {
		if(lasOpciones == null) {
			lasOpciones=businessDelegatorView.getSegOpcion();
		}
		return lasOpciones;
	}

	public void setLasOpciones(List<SegOpcion> lasOpciones) {
		this.lasOpciones = lasOpciones;
	}
	
	
}
