package com.vortexbird.gluon.plan.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

import com.vortexbird.gluon.plan.modelo.SegRol;
import com.vortexbird.gluon.plan.modelo.SegRolUsuario;
import com.vortexbird.gluon.plan.modelo.SegUsuario;
import com.vortexbird.gluon.plan.presentation.backingBeans.RegistroVista;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;
import com.vortexbird.gluon.plan.utilities.FacesUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zathura Code Generator http://code.google.com/p/zathura/
 *         www.zathuracode.org
 *
 */
@Scope("singleton")
@Component("zathuraCodeAuthenticationProvider")
public class ZathuraCodeAuthenticationProvider implements AuthenticationProvider {
	/**
	 * Security Implementation
	 */

	@Autowired
	private IBusinessDelegatorView businessDelegator;
	
	private static final Logger log = LoggerFactory.getLogger(ZathuraCodeAuthenticationProvider.class);

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		try {
			
			Object[] variables = new Object[4];
			variables[0] = "usuLogin";
			variables[1] = true;
			variables[2] = name;
			variables[3] = "=";
			
			List<SegUsuario> usuarios = businessDelegator.findByCriteriaInSegUsuario(variables, null, null);
			
			if(usuarios==null || usuarios.isEmpty()) {
				throw new Exception("Usuario o contraseña invalidos");
			}
			
			boolean existe = false;
			SegUsuario user = null;
			
			for(SegUsuario usuario : usuarios) {
				if(usuario.getUsuPassword().equals(password)) {
					user = usuario;
					FacesUtils.putinSession("usuario", user);
					existe=true;
				}
			}
			
			if(existe!=true) {
				throw new Exception("Usuario o contraseña invalidos");
			}
			
			final List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
			
			Object[] variablesRol = new Object[4];
			variablesRol[0] = "segUsuario";
			variablesRol[1] = false;
			variablesRol[2] = user.getUsuId();
			variablesRol[3] = "=";
			
			List<SegRolUsuario> rolesUsuario = businessDelegator.findByCriteriaInSegRolUsuario(variablesRol, null, null);
			
			List<SegRol> roles = new ArrayList<SegRol>();
			
			for(SegRolUsuario rolUsuario : rolesUsuario) {
				SegRol rol = businessDelegator.getSegRol(rolUsuario.getSegRol().getRolId());
				roles.add(rol);
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_"+rol.getNombre()));
			}			
			
			FacesUtils.putinSession("roles", roles);
			
			final UserDetails principal = new User(name, password, grantedAuths);
			final Authentication auth = new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);

			return auth;
		} catch (Exception e) {
			log.info(e.getMessage());
			throw new BadCredentialsException(e.getMessage());
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
