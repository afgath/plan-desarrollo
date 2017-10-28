package com.vortexbird.gluon.plan.dto.mapper;

import com.vortexbird.gluon.plan.modelo.*;
import com.vortexbird.gluon.plan.modelo.SegUsuario;
import com.vortexbird.gluon.plan.modelo.control.*;
import com.vortexbird.gluon.plan.modelo.dto.SegUsuarioDTO;
import com.vortexbird.gluon.plan.presentation.businessDelegate.BusinessDelegatorView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
* @author Zathura Code Generator http://zathuracode.org/
* www.zathuracode.org
*
*/
@Component
@Scope("singleton")
public class SegUsuarioMapper implements ISegUsuarioMapper {
    private static final Logger log = LoggerFactory.getLogger(SegUsuarioMapper.class);

    @Autowired
    private ISegUsuarioLogic segUsuarioLogic;
    
    @Transactional(readOnly = true)
    public SegUsuarioDTO segUsuarioToSegUsuarioDTO(SegUsuario segUsuario)
        throws Exception {
        try {
            SegUsuarioDTO segUsuarioDTO = new SegUsuarioDTO();

            segUsuarioDTO.setUsuId(segUsuario.getUsuId());
            segUsuarioDTO.setActivo((segUsuario.getActivo() != null)
                ? segUsuario.getActivo() : null);
            
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd");
            
            segUsuarioDTO.setFechaCreacion(dt.format(segUsuario.getFechaCreacion()));
                      
            segUsuarioDTO.setFechaModificacion((segUsuario.getFechaModificacion()!=null) ? dt.format(segUsuario.getFechaModificacion()) : null);
            
            SegUsuario usuCreador = segUsuarioLogic.getSegUsuario(segUsuario.getUsuCreador());
            
            segUsuarioDTO.setUsuCreador((usuCreador != null)
                ? usuCreador.getUsuLogin() : null);
            segUsuarioDTO.setUsuLogin((segUsuario.getUsuLogin() != null)
                ? segUsuario.getUsuLogin() : null);
            
            SegUsuario usuModificador;
            if(segUsuario.getUsuModificador()==null) {
            	usuModificador=null;
            }else {
            	usuModificador = segUsuarioLogic.getSegUsuario(segUsuario.getUsuModificador());
            }
            
            segUsuarioDTO.setUsuModificador((usuModificador != null)
                ? usuModificador.getUsuLogin() : null);
            segUsuarioDTO.setUsuPassword((segUsuario.getUsuPassword() != null)
                ? segUsuario.getUsuPassword() : null);

            return segUsuarioDTO;
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public SegUsuario segUsuarioDTOToSegUsuario(SegUsuarioDTO segUsuarioDTO)
        throws Exception {
        try {
            SegUsuario segUsuario = new SegUsuario();
            
            boolean existeUsuCreador = false;
            boolean existeUsuModificador = false;
            boolean existeFechaCreacion = false;
            boolean existeFechaModificacion = false;
            
            SegUsuario usuCreador = null;
            SegUsuario usuModificador = null;

            segUsuario.setUsuId(segUsuarioDTO.getUsuId());
            segUsuario.setActivo((segUsuarioDTO.getActivo() != null)
                ? segUsuarioDTO.getActivo() : null);
            
            SimpleDateFormat dt = new SimpleDateFormat("yyyyy-mm-dd hh:mm:ss");
            
            segUsuario.setFechaCreacion(dt.parse(segUsuarioDTO.getFechaCreacion()));
            segUsuario.setFechaModificacion(dt.parse(segUsuarioDTO.getFechaModificacion()));
            
            if(segUsuarioDTO.getUsuCreador().length()!=0) {
            	existeUsuCreador = true;
            }
            
            if(existeUsuCreador) {
            	Object[] variables = new Object[4];
                variables[0]="usuLogin";
                variables[1]=true;
                variables[2]=segUsuarioDTO.getUsuCreador();
                variables[3]="=";
                
                usuCreador = segUsuarioLogic.findByCriteria(variables, null, null).get(0);
            }
            
            segUsuario.setUsuCreador((!existeUsuCreador)
                ? usuCreador.getUsuId() : null);
            segUsuario.setUsuLogin((segUsuarioDTO.getUsuLogin() != null)
                ? segUsuarioDTO.getUsuLogin() : null);
            
            if(segUsuarioDTO.getUsuModificador().length()!=0) {
            	existeUsuModificador = true;
            }
            
            if(existeUsuModificador) {
            	Object[] variables = new Object[4];
                variables[0]="usuLogin";
                variables[1]=true;
                variables[2]=segUsuarioDTO.getUsuCreador();
                variables[3]="=";
                
                usuCreador = segUsuarioLogic.findByCriteria(variables, null, null).get(0);
            }
            
            segUsuario.setUsuModificador((!existeUsuModificador)
                    ? usuModificador.getUsuId() : null);
            segUsuario.setUsuPassword((segUsuarioDTO.getUsuPassword() != null)
                ? segUsuarioDTO.getUsuPassword() : null);

            return segUsuario;
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<SegUsuarioDTO> listSegUsuarioToListSegUsuarioDTO(
        List<SegUsuario> listSegUsuario) throws Exception {
        try {
            List<SegUsuarioDTO> segUsuarioDTOs = new ArrayList<SegUsuarioDTO>();

            for (SegUsuario segUsuario : listSegUsuario) {
                SegUsuarioDTO segUsuarioDTO = segUsuarioToSegUsuarioDTO(segUsuario);

                segUsuarioDTOs.add(segUsuarioDTO);
            }

            return segUsuarioDTOs;
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<SegUsuario> listSegUsuarioDTOToListSegUsuario(
        List<SegUsuarioDTO> listSegUsuarioDTO) throws Exception {
        try {
            List<SegUsuario> listSegUsuario = new ArrayList<SegUsuario>();

            for (SegUsuarioDTO segUsuarioDTO : listSegUsuarioDTO) {
                SegUsuario segUsuario = segUsuarioDTOToSegUsuario(segUsuarioDTO);

                listSegUsuario.add(segUsuario);
            }

            return listSegUsuario;
        } catch (Exception e) {
            throw e;
        }
    }
}
