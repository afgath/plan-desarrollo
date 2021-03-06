package com.vortexbird.gluon.plan.modelo.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import java.sql.*;

import java.util.Date;


/**
*
* @author Zathura Code Generator http://zathuracode.org/
* www.zathuracode.org
*
*/
public class SegRolUsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SegRolUsuarioDTO.class);
    private String activo;
    private Date fechaCreacion;
    private Date fechaModificacion;
    private Integer rlusId;
    private Integer usuCreador;
    private Integer usuModificador;
    private Integer rolId_SegRol;
    private Integer usuId_SegUsuario;

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Integer getRlusId() {
        return rlusId;
    }

    public void setRlusId(Integer rlusId) {
        this.rlusId = rlusId;
    }

    public Integer getUsuCreador() {
        return usuCreador;
    }

    public void setUsuCreador(Integer usuCreador) {
        this.usuCreador = usuCreador;
    }

    public Integer getUsuModificador() {
        return usuModificador;
    }

    public void setUsuModificador(Integer usuModificador) {
        this.usuModificador = usuModificador;
    }

    public Integer getRolId_SegRol() {
        return rolId_SegRol;
    }

    public void setRolId_SegRol(Integer rolId_SegRol) {
        this.rolId_SegRol = rolId_SegRol;
    }

    public Integer getUsuId_SegUsuario() {
        return usuId_SegUsuario;
    }

    public void setUsuId_SegUsuario(Integer usuId_SegUsuario) {
        this.usuId_SegUsuario = usuId_SegUsuario;
    }
}
