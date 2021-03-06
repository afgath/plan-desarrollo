package com.vortexbird.gluon.plan.modelo.control;

import com.vortexbird.gluon.plan.dataaccess.dao.*;
import com.vortexbird.gluon.plan.dto.mapper.IGluoSectorEjeDimensionMapper;
import com.vortexbird.gluon.plan.exceptions.*;
import com.vortexbird.gluon.plan.exceptions.ZMessManager.NullEntityExcepcion;
import com.vortexbird.gluon.plan.modelo.*;
import com.vortexbird.gluon.plan.modelo.dto.GluoSectorEjeDimensionDTO;
import com.vortexbird.gluon.plan.utilities.Utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;


/**
* @author Zathura Code Generator http://zathuracode.org/
* www.zathuracode.org
*
*/
@Scope("singleton")
@Service("GluoSectorEjeDimensionLogic")
public class GluoSectorEjeDimensionLogic implements IGluoSectorEjeDimensionLogic {
    private static final Logger log = LoggerFactory.getLogger(GluoSectorEjeDimensionLogic.class);

    /**
     * DAO injected by Spring that manages GluoSectorEjeDimension entities
     *
     */
    @Autowired
    private IGluoSectorEjeDimensionDAO gluoSectorEjeDimensionDAO;
    @Autowired
    private IGluoSectorEjeDimensionMapper gluoSectorEjeDimensionMapper;
    @Autowired
    private Validator validator;

    /**
    * DAO injected by Spring that manages GluoObjetivo entities
    *
    */
    @Autowired
    private IGluoObjetivoDAO gluoObjetivoDAO;

    /**
    * Logic injected by Spring that manages GluoPlanDesarrollo entities
    *
    */
    @Autowired
    IGluoPlanDesarrolloLogic logicGluoPlanDesarrollo1;

    public void validateGluoSectorEjeDimension(
        GluoSectorEjeDimension gluoSectorEjeDimension)
        throws Exception {
        try {
            Set<ConstraintViolation<GluoSectorEjeDimension>> constraintViolations =
                validator.validate(gluoSectorEjeDimension);

            if (constraintViolations.size() > 0) {
                StringBuilder strMessage = new StringBuilder();

                for (ConstraintViolation<GluoSectorEjeDimension> constraintViolation : constraintViolations) {
                    strMessage.append(constraintViolation.getPropertyPath()
                                                         .toString());
                    strMessage.append(" - ");
                    strMessage.append(constraintViolation.getMessage());
                    strMessage.append(". \n");
                }

                throw new Exception(strMessage.toString());
            }
        } catch (Exception e) {
            throw e;
        }
    }
    
    public void evaluarGluoSectorEjeDimension(GluoSectorEjeDimension entity) throws Exception {
    	log.debug("Validando GluoSectorEjeDimension instace");
    	try {
    		if (entity == null) {
                throw new ZMessManager().new NullEntityExcepcion(
                    "GluoSectorEjeDimension");
            }
            
    		validateGluoSectorEjeDimension(entity);

            
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
    }

    @Transactional(readOnly = true)
    public List<GluoSectorEjeDimension> getGluoSectorEjeDimension()
        throws Exception {
        log.debug("finding all GluoSectorEjeDimension instances");

        List<GluoSectorEjeDimension> list = new ArrayList<GluoSectorEjeDimension>();

        try {
            list = gluoSectorEjeDimensionDAO.findAll();
        } catch (Exception e) {
            log.error("finding all GluoSectorEjeDimension failed", e);
            throw new ZMessManager().new GettingException(ZMessManager.ALL +
                "GluoSectorEjeDimension");
        } finally {
        }

        return list;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void saveGluoSectorEjeDimension(GluoSectorEjeDimension entity)
        throws Exception {
        log.debug("saving GluoSectorEjeDimension instance");

        try {
            if (entity == null) {
                throw new ZMessManager().new NullEntityExcepcion(
                    "GluoSectorEjeDimension");
            }

            evaluarGluoSectorEjeDimension(entity);

            gluoSectorEjeDimensionDAO.save(entity);

            log.debug("save GluoSectorEjeDimension successful");
        } catch (Exception e) {
            log.error("save GluoSectorEjeDimension failed", e);
            throw e;
        } finally {
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteGluoSectorEjeDimension(GluoSectorEjeDimension entity)
        throws Exception {
        log.debug("deleting GluoSectorEjeDimension instance");

        if (entity == null) {
            throw new ZMessManager().new NullEntityExcepcion(
                "GluoSectorEjeDimension");
        }

        if (entity.getSediId() == null) {
            throw new ZMessManager().new EmptyFieldException("sediId");
        }

        List<GluoObjetivo> gluoObjetivos = null;

        try {
            gluoObjetivos = gluoObjetivoDAO.findByProperty("gluoSectorEjeDimension.sediId",
                    entity.getSediId());

            if (Utilities.validationsList(gluoObjetivos) == true) {
                throw new ZMessManager().new DeletingException("gluoObjetivos");
            }

            gluoSectorEjeDimensionDAO.delete(entity);

            log.debug("delete GluoSectorEjeDimension successful");
        } catch (Exception e) {
            log.error("delete GluoSectorEjeDimension failed", e);
            throw e;
        } finally {
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void updateGluoSectorEjeDimension(GluoSectorEjeDimension entity)
        throws Exception {
        log.debug("updating GluoSectorEjeDimension instance");

        try {
            if (entity == null) {
                throw new ZMessManager().new NullEntityExcepcion(
                    "GluoSectorEjeDimension");
            }

            validateGluoSectorEjeDimension(entity);

            gluoSectorEjeDimensionDAO.update(entity);

            log.debug("update GluoSectorEjeDimension successful");
        } catch (Exception e) {
            log.error("update GluoSectorEjeDimension failed", e);
            throw e;
        } finally {
        }
    }

    @Transactional(readOnly = true)
    public List<GluoSectorEjeDimensionDTO> getDataGluoSectorEjeDimension()
        throws Exception {
        try {
            List<GluoSectorEjeDimension> gluoSectorEjeDimension = gluoSectorEjeDimensionDAO.findAll();

            List<GluoSectorEjeDimensionDTO> gluoSectorEjeDimensionDTO = new ArrayList<GluoSectorEjeDimensionDTO>();

            for (GluoSectorEjeDimension gluoSectorEjeDimensionTmp : gluoSectorEjeDimension) {
                GluoSectorEjeDimensionDTO gluoSectorEjeDimensionDTO2 = gluoSectorEjeDimensionMapper.gluoSectorEjeDimensionToGluoSectorEjeDimensionDTO(gluoSectorEjeDimensionTmp);
                gluoSectorEjeDimensionDTO.add(gluoSectorEjeDimensionDTO2);
            }

            return gluoSectorEjeDimensionDTO;
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public GluoSectorEjeDimension getGluoSectorEjeDimension(Integer sediId)
        throws Exception {
        log.debug("getting GluoSectorEjeDimension instance");

        GluoSectorEjeDimension entity = null;

        try {
            entity = gluoSectorEjeDimensionDAO.findById(sediId);
        } catch (Exception e) {
            log.error("get GluoSectorEjeDimension failed", e);
            throw new ZMessManager().new FindingException(
                "GluoSectorEjeDimension");
        } finally {
        }

        return entity;
    }

    @Transactional(readOnly = true)
    public List<GluoSectorEjeDimension> findPageGluoSectorEjeDimension(
        String sortColumnName, boolean sortAscending, int startRow,
        int maxResults) throws Exception {
        List<GluoSectorEjeDimension> entity = null;

        try {
            entity = gluoSectorEjeDimensionDAO.findPage(sortColumnName,
                    sortAscending, startRow, maxResults);
        } catch (Exception e) {
            throw new ZMessManager().new FindingException(
                "GluoSectorEjeDimension Count");
        } finally {
        }

        return entity;
    }

    @Transactional(readOnly = true)
    public Long findTotalNumberGluoSectorEjeDimension()
        throws Exception {
        Long entity = null;

        try {
            entity = gluoSectorEjeDimensionDAO.count();
        } catch (Exception e) {
            throw new ZMessManager().new FindingException(
                "GluoSectorEjeDimension Count");
        } finally {
        }

        return entity;
    }

    /**
    *
    * @param varibles
    *            este arreglo debera tener:
    *
    * [0] = String variable = (String) varibles[i]; representa como se llama la
    * variable en el pojo
    *
    * [1] = Boolean booVariable = (Boolean) varibles[i + 1]; representa si el
    * valor necesita o no ''(comillas simples)usado para campos de tipo string
    *
    * [2] = Object value = varibles[i + 2]; representa el valor que se va a
    * buscar en la BD
    *
    * [3] = String comparator = (String) varibles[i + 3]; representa que tipo
    * de busqueda voy a hacer.., ejemplo: where nombre=william o where nombre<>william,
        * en este campo iria el tipo de comparador que quiero si es = o <>
            *
            * Se itera de 4 en 4..., entonces 4 registros del arreglo representan 1
            * busqueda en un campo, si se ponen mas pues el continuara buscando en lo
            * que se le ingresen en los otros 4
            *
            *
            * @param variablesBetween
            *
            * la diferencia son estas dos posiciones
            *
            * [0] = String variable = (String) varibles[j]; la variable ne la BD que va
            * a ser buscada en un rango
            *
            * [1] = Object value = varibles[j + 1]; valor 1 para buscar en un rango
            *
            * [2] = Object value2 = varibles[j + 2]; valor 2 para buscar en un rango
            * ejempolo: a > 1 and a < 5 --> 1 seria value y 5 seria value2
                *
                * [3] = String comparator1 = (String) varibles[j + 3]; comparador 1
                * ejemplo: a comparator1 1 and a < 5
                    *
                    * [4] = String comparator2 = (String) varibles[j + 4]; comparador 2
                    * ejemplo: a comparador1>1  and a comparador2<5  (el original: a > 1 and a <
                            * 5) *
                            * @param variablesBetweenDates(en
                            *            este caso solo para mysql)
                            *  [0] = String variable = (String) varibles[k]; el nombre de la variable que hace referencia a
                            *            una fecha
                            *
                            * [1] = Object object1 = varibles[k + 2]; fecha 1 a comparar(deben ser
                            * dates)
                            *
                            * [2] = Object object2 = varibles[k + 3]; fecha 2 a comparar(deben ser
                            * dates)
                            *
                            * esto hace un between entre las dos fechas.
                            *
                            * @return lista con los objetos que se necesiten
                            * @throws Exception
                            */
    @Transactional(readOnly = true)
    public List<GluoSectorEjeDimension> findByCriteria(Object[] variables,
        Object[] variablesBetween, Object[] variablesBetweenDates)
        throws Exception {
        List<GluoSectorEjeDimension> list = new ArrayList<GluoSectorEjeDimension>();
        String where = new String();
        String tempWhere = new String();

        if (variables != null) {
            for (int i = 0; i < variables.length; i++) {
                if ((variables[i] != null) && (variables[i + 1] != null) &&
                        (variables[i + 2] != null) &&
                        (variables[i + 3] != null)) {
                    String variable = (String) variables[i];
                    Boolean booVariable = (Boolean) variables[i + 1];
                    Object value = variables[i + 2];
                    String comparator = (String) variables[i + 3];

                    if (booVariable.booleanValue()) {
                        tempWhere = (tempWhere.length() == 0)
                            ? ("(model." + variable + " " + comparator + " \'" +
                            value + "\' )")
                            : (tempWhere + " AND (model." + variable + " " +
                            comparator + " \'" + value + "\' )");
                    } else {
                        tempWhere = (tempWhere.length() == 0)
                            ? ("(model." + variable + " " + comparator + " " +
                            value + " )")
                            : (tempWhere + " AND (model." + variable + " " +
                            comparator + " " + value + " )");
                    }
                }

                i = i + 3;
            }
        }

        if (variablesBetween != null) {
            for (int j = 0; j < variablesBetween.length; j++) {
                if ((variablesBetween[j] != null) &&
                        (variablesBetween[j + 1] != null) &&
                        (variablesBetween[j + 2] != null) &&
                        (variablesBetween[j + 3] != null) &&
                        (variablesBetween[j + 4] != null)) {
                    String variable = (String) variablesBetween[j];
                    Object value = variablesBetween[j + 1];
                    Object value2 = variablesBetween[j + 2];
                    String comparator1 = (String) variablesBetween[j + 3];
                    String comparator2 = (String) variablesBetween[j + 4];
                    tempWhere = (tempWhere.length() == 0)
                        ? ("(" + value + " " + comparator1 + " " + variable +
                        " and " + variable + " " + comparator2 + " " + value2 +
                        " )")
                        : (tempWhere + " AND (" + value + " " + comparator1 +
                        " " + variable + " and " + variable + " " +
                        comparator2 + " " + value2 + " )");
                }

                j = j + 4;
            }
        }

        if (variablesBetweenDates != null) {
            for (int k = 0; k < variablesBetweenDates.length; k++) {
                if ((variablesBetweenDates[k] != null) &&
                        (variablesBetweenDates[k + 1] != null) &&
                        (variablesBetweenDates[k + 2] != null)) {
                    String variable = (String) variablesBetweenDates[k];
                    Object object1 = variablesBetweenDates[k + 1];
                    Object object2 = variablesBetweenDates[k + 2];
                    String value = null;
                    String value2 = null;

                    try {
                        Date date1 = (Date) object1;
                        Date date2 = (Date) object2;
                        value = Utilities.formatDateWithoutTimeInAStringForBetweenWhere(date1);
                        value2 = Utilities.formatDateWithoutTimeInAStringForBetweenWhere(date2);
                    } catch (Exception e) {
                        list = null;
                        throw e;
                    }

                    tempWhere = (tempWhere.length() == 0)
                        ? ("(model." + variable + " between " + value +
                        " and " + value2 + ")")
                        : (tempWhere + " AND (model." + variable + " between " +
                        value + " and " + value2 + ")");
                }

                k = k + 2;
            }
        }

        if (tempWhere.length() == 0) {
            where = null;
        } else {
            where = "(" + tempWhere + ")";
        }

        try {
            list = gluoSectorEjeDimensionDAO.findByCriteria(where);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
        }

        return list;
    }
}
