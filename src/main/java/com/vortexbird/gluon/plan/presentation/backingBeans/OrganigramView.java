package com.vortexbird.gluon.plan.presentation.backingBeans;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.inputmask.InputMask;
import org.primefaces.component.inputnumber.InputNumber;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.organigram.OrganigramHelper;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeDragDropEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortexbird.gluon.plan.modelo.ElementosPlan;
import com.vortexbird.gluon.plan.modelo.GluoAnoFiscal;
import com.vortexbird.gluon.plan.modelo.GluoDetalleProyecto;
import com.vortexbird.gluon.plan.modelo.GluoHistorialIndicador;
import com.vortexbird.gluon.plan.modelo.GluoIndicador;
import com.vortexbird.gluon.plan.modelo.GluoObjetivo;
import com.vortexbird.gluon.plan.modelo.GluoPlanDesarrollo;
import com.vortexbird.gluon.plan.modelo.GluoPrograma;
import com.vortexbird.gluon.plan.modelo.GluoProyecto;
import com.vortexbird.gluon.plan.modelo.GluoSectorEjeDimension;
import com.vortexbird.gluon.plan.modelo.GluoSubprograma;
import com.vortexbird.gluon.plan.modelo.dto.GluoAnoFiscalDTO;
import com.vortexbird.gluon.plan.modelo.dto.GluoSectorEjeDimensionDTO;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;
import com.vortexbird.gluon.plan.utilities.FacesUtils;
import com.vortexbird.gluon.plan.utilities.Utilities;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class OrganigramView implements Serializable {

	@ManagedProperty(value = "#{BusinessDelegatorView}")
	private IBusinessDelegatorView businessDelegatorView;

	private static final Logger log = LoggerFactory.getLogger(OrganigramView.class);

	// Nodo raiz (rootNode) y nodo que guarda el nodo seleccionado (selection)
	private OrganigramNode rootNode;
	private OrganigramNode sector;
	private OrganigramNode nodoObjetivo;
	private OrganigramNode selection;

	// Variables para la creación de los entity
	private GluoPlanDesarrollo plan;
	private GluoSectorEjeDimension eje;
	private GluoObjetivo objetivo;
	private GluoPrograma programa;
	private GluoSubprograma subprograma;
	private GluoProyecto proyecto;
	private GluoDetalleProyecto detalleProyecto;
	private ElementosPlan elementoPlan;
	private GluoIndicador indicador;
	private GluoHistorialIndicador historialIndicador;

	private int contPlan;
	private int contDimension;
	private int contObjetivo;
	private int contPrograma;
	private int contSubPrograma;
	private int contProyecto;
	private int contDetalleProyecto;
	private int contIndicador;
	private int contHistorialIndicador;
	// Fin Variables para la creación de los entity

	// Variables de configuración para el organigrama
	private boolean zoom = true;
	private String style = "width: 100%";
	private int leafNodeConnectorHeight = 0;
	// Fin Variables de configuración para el organigrama

	private List<SelectItem> losAnosFiscalItem;

	// HashMaps para almacenar los nodos creados en la base de datos
	private Map<String, ElementosPlan> dimensionMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> objetivoMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> programaMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> subProgramaMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> proyectoMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> detalleProyectoMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> indicadorMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> historialIndicadorMap = new HashMap<String, ElementosPlan>();
	// Fin HashMaps para almacenar los nodos creados en la base de datos

	// Variables de dialogos
	private InputTextarea txtAreaDescripcionPlan;
	private InputTextarea txtAreaEsloganPlan;
	private InputText txtNombreAlcaldePlan;
	private Calendar txtAnoFinPlan;
	private Calendar txtAnoInicioPlan;
	private InputText txtDimension;
	private InputTextarea txtAreaDescObjetivo;
	private InputTextarea txtAreaDesPrograma;
	private InputTextarea txtAreaDescSubPrograma;
	private InputTextarea txtAreaDescProyecto;
	private SelectOneMenu somAnoFiscal;
	private InputNumber numValorPresupuesto;
	private InputTextarea txtDescIndicador;
	private InputTextarea txtDescLineaBase;
	private InputTextarea txtDescMeta;
	private InputNumber numValorMeta;
	private Calendar txtHIAno;
	private InputNumber numHIValorReal;
	private CommandButton btnAnadirPlan;
	private CommandButton btnAnadirDimension;
	private CommandButton btnAnadirObjetivo;
	private CommandButton btnAnadirPrograma;
	// Fin Variables de dialogos

	// Variables para los dialogos de modificar
	private InputTextarea txtModAreaDescripcionPlan;
	private InputTextarea txtModAreaEsloganPlan;
	private InputText txtModNombreAlcaldePlan;
	private Calendar txtModAnoFinPlan;
	private Calendar txtModAnoInicioPlan;
	private InputTextarea txtAreaModPlanDescripcion;
	private InputTextarea txtAreaModPlanEslogan;
	private InputText txtModPlanNombreAlcaldePlan;
	private Calendar txtModPlanAnoFinPlan;
	private Calendar txtModPlanAnoInicioPlan;
	private SelectBooleanCheckbox sbcModPlanActivo;

	private InputText txtModDimension;
	private SelectBooleanCheckbox sbcModEjeActivo;

	private InputTextarea txtAreaModObjDesc;
	private SelectBooleanCheckbox sbcModObjActivo;

	private InputTextarea txtAreaModProgDesc;
	private SelectBooleanCheckbox sbcModProgActivo;

	private InputTextarea txtAreaModSubProgDesc;
	private SelectBooleanCheckbox sbcModSubProgActivo;

	private InputTextarea txtAreaModProyDesc;
	private SelectBooleanCheckbox sbcModProyActivo;

	private InputNumber numModDPValorPresupuesto;
	private SelectBooleanCheckbox sbcModDPActivo;
	private SelectOneMenu somModAnoFiscal;
	private InputText txtDPValorTotalPresupuesto;

	private InputText txtModDescIndicador;
	private InputText txtModDescLineaBase;
	private InputText txtModDescMeta;
	private InputNumber numModValorMeta;
	private SelectBooleanCheckbox sbcModIndicadorActivo;

	private Calendar txtModHIAno;
	private InputNumber numModHIValorReal;
	private SelectBooleanCheckbox sbcModHistorialIndicadorActivo;
	// Fin Variables para los dialogos de modificar

	@PostConstruct
	public void init() {
		selection = new DefaultOrganigramNode(null, "plan", null);
		selection.setRowKey("selectionNode");

		RequestContext.getCurrentInstance().execute("PF('dlgAnadirPlan').show()");

	}

	public void nodeSelectListener(OrganigramNodeSelectEvent event) {
		FacesUtils.addInfoMessage(
				event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData() + "' seleccionado.");
	}

	public void nodeCollapseListener(OrganigramNodeCollapseEvent event) {
		FacesUtils.addInfoMessage(
				event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData() + "' colapsado.");
	}

	public void nodeExpandListener(OrganigramNodeExpandEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary(
				event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData() + "' expandido.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeDragDropListener(OrganigramNodeDragDropEvent event) {
		try {
			OrganigramNode nodo = event.getOrganigramNode();
			OrganigramNode nodoPadre = event.getTargetOrganigramNode();
			OrganigramNode nodoAntiguoPadre = event.getSourceOrganigramNode();
			
			String tipoOrigen = nodoAntiguoPadre.getType();
			String tipoDestino = nodoPadre.getType();
			
			sePuedeMover(tipoOrigen, tipoDestino);

			FacesUtils.addInfoMessage("Nodo '" + event.getOrganigramNode().getData() + "' movido de "
					+ event.getSourceOrganigramNode().getData() + " a '" + event.getTargetOrganigramNode().getData()
					+ "'");

			modficarEntity(nodo, nodoPadre, nodoAntiguoPadre);

		} catch (Exception e) {
			event.getOrganigramNode().setParent(event.getSourceOrganigramNode());
			event.getTargetOrganigramNode().getChildren().remove(event.getOrganigramNode());
			FacesUtils.addErrorMessage(e.getMessage());
			RequestContext.getCurrentInstance().update("form:organigram");
		}
	}

	public boolean sePuedeMover(String tipoOrigen, String tipoDestino) throws Exception {
		if (!tipoOrigen.equals(tipoDestino)) {
			throw new Exception("El nodo no se puede mover");
		}
		return true;
	}

	private void modficarEntity(OrganigramNode nodo, OrganigramNode nodoPadre, OrganigramNode nodoAntiguoPadre) {
		log.info("modificarEntity");
		try {
			String tipo = nodo.getType();
			String key = "";
			String keyPadre = "";
			String keyAntiguoPadre = "";

			switch (tipo) {
			case "detalleProyecto":
				key = (String) nodo.getData();
				keyPadre = (String) nodoPadre.getData();
				keyAntiguoPadre = (String) nodoAntiguoPadre.getData();

				proyecto = (GluoProyecto) proyectoMap.get(keyPadre).getEntity();
				detalleProyecto = (GluoDetalleProyecto) detalleProyectoMap.get(key).getEntity();
				GluoProyecto antiguoProyecto = (GluoProyecto) subProgramaMap.get(keyAntiguoPadre).getEntity();
				Set<GluoDetalleProyecto> gluoDetalleProyectos = antiguoProyecto.getGluoDetalleProyectos();

				for (GluoDetalleProyecto detProy : gluoDetalleProyectos ) {
					if(detProy.equals(detalleProyecto)) {
						log.info("Lo encontré");
						gluoDetalleProyectos.remove(detalleProyecto);
						Set<GluoDetalleProyecto> gluoDetalleProyectos2 = proyecto.getGluoDetalleProyectos();
						gluoDetalleProyectos2.add(detalleProyecto);
						proyecto.setGluoDetalleProyectos(gluoDetalleProyectos2);;
					}
				}

				detalleProyecto.setGluoProyecto(proyecto);

				detalleProyectoMap.get(key).setEntity(detalleProyecto);
				break;

			case "proyecto":
				key = (String) nodo.getData();
				keyPadre = (String) nodoPadre.getData();
				keyAntiguoPadre = (String) nodoAntiguoPadre.getData();

				subprograma = (GluoSubprograma) subProgramaMap.get(keyPadre).getEntity();
				proyecto = (GluoProyecto) proyectoMap.get(key).getEntity();
				GluoSubprograma antiguoSubPrograma = (GluoSubprograma) subProgramaMap.get(keyAntiguoPadre).getEntity();
				Set<GluoProyecto> gluoProyectos = antiguoSubPrograma.getGluoProyectos();

				for (GluoProyecto proy : gluoProyectos ) {
					if(proy.equals(proyecto)) {
						log.info("Lo encontré");
						gluoProyectos.remove(proyecto);
						Set<GluoProyecto> gluoProyectos2 = subprograma.getGluoProyectos();
						gluoProyectos2.add(proyecto);
						subprograma.setGluoProyectos(gluoProyectos2);
					}
				}

				proyecto.setGluoSubprograma(subprograma);

				proyectoMap.get(key).setEntity(proyecto);
				break;

			case "subprograma":
				key = (String) nodo.getData();
				keyPadre = (String) nodoPadre.getData();
				keyAntiguoPadre = (String) nodoAntiguoPadre.getData();

				programa = (GluoPrograma) programaMap.get(keyPadre).getEntity();
				subprograma = (GluoSubprograma) subProgramaMap.get(key).getEntity();
				GluoPrograma antiguoPrograma = (GluoPrograma) programaMap.get(keyAntiguoPadre).getEntity();
				Set<GluoSubprograma> gluoSubProgramas = antiguoPrograma.getGluoSubprogramas();

				for (GluoSubprograma subProg : gluoSubProgramas ) {
					if(subProg.equals(subprograma)) {
						log.info("Lo encontré");
						gluoSubProgramas.remove(subprograma);
						Set<GluoSubprograma> gluoSubProgramas2 = programa.getGluoSubprogramas();
						gluoSubProgramas2.add(subprograma);
						programa.setGluoSubprogramas(gluoSubProgramas2);
					}
				}

				subprograma.setGluoPrograma(programa);

				subProgramaMap.get(key).setEntity(subprograma);
				break;

			case "programa":
				key = (String) nodo.getData();
				keyPadre = (String) nodoPadre.getData();
				keyAntiguoPadre = (String) nodoAntiguoPadre.getData();

				objetivo = (GluoObjetivo) objetivoMap.get(keyPadre).getEntity();
				programa = (GluoPrograma) programaMap.get(key).getEntity();
				GluoObjetivo antiguoObjetivo = (GluoObjetivo) objetivoMap.get(keyAntiguoPadre).getEntity();
				Set<GluoPrograma> gluoProgramas = antiguoObjetivo.getGluoProgramas();

				for (GluoPrograma prog : gluoProgramas ) {
					if(prog.equals(programa)) {
						log.info("Lo encontré");
						gluoProgramas.remove(programa);
						Set<GluoPrograma> gluoProgramas2 = objetivo.getGluoProgramas();
						gluoProgramas2.add(programa);
						objetivo.setGluoProgramas(gluoProgramas2);
					}
				}

				programa.setGluoObjetivo(objetivo);

				programaMap.get(key).setEntity(programa);
				break;

			case "objetivo":

				key = (String) nodo.getData();
				keyPadre = (String) nodoPadre.getData();
				keyAntiguoPadre = (String) nodoAntiguoPadre.getData();

				eje = (GluoSectorEjeDimension) dimensionMap.get(keyPadre).getEntity();
				objetivo = (GluoObjetivo) objetivoMap.get(key).getEntity();
				GluoSectorEjeDimension antiguoEje = (GluoSectorEjeDimension) dimensionMap.get(keyAntiguoPadre).getEntity();
				Set<GluoObjetivo> gluoObjetivos = antiguoEje.getGluoObjetivos();

				for (GluoObjetivo obj : gluoObjetivos ) {
					if(obj.equals(objetivo)) {
						log.info("Lo encontré");
						gluoObjetivos.remove(objetivo);
						Set<GluoObjetivo> gluoObjetivos2 = eje.getGluoObjetivos();
						gluoObjetivos2.add(objetivo);
						eje.setGluoObjetivos(gluoObjetivos2);
					}
				}
				
				objetivo.setGluoSectorEjeDimension(eje);

				objetivoMap.get(key).setEntity(objetivo);
				break;
			}
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void listener_txtAnoFin() {
		Date inputDate = (Date) txtAnoFinPlan.getValue();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		FacesContext.getCurrentInstance().addMessage("",
				new FacesMessage("Fecha seleccionada " + dateFormat.format(inputDate)));
	}

	public void listener_txtAnoInicio() {
		Date inputDate = (Date) txtAnoInicioPlan.getValue();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		FacesContext.getCurrentInstance().addMessage("",
				new FacesMessage("Fecha seleccionada " + dateFormat.format(inputDate)));
	}

	public void removeEmployee() {
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
		currentSelection.getParent().getChildren().remove(currentSelection);
	}
	
	public void eliminarNodoAction() {
		log.info("EliminarNodoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			String tipo = currentSelection.getType();
			String key = null;
			
			switch (tipo) {
			
			case "historialIndicador":
				
				key = (String) currentSelection.getData();
				
				historialIndicadorMap.remove(key);
				
				currentSelection.getParent().getChildren().remove(currentSelection);
				
				//elementoPlan = (ElementosPlan) historialIndicadorMap.get(key).getEntity();
								
				
				log.info("Elemento: "+ elementoPlan.getRowKey());
				
				break;
			
			case "inidicador":
				
				break;
				
			case "detalleProyecto":
				
				key = (String) currentSelection.getData();
				detalleProyectoMap.remove(key);
				
				break;

			case "proyecto":
				
				key = (String) currentSelection.getData();
				proyectoMap.remove(key);
				
				break;

			case "subprograma":
				
				break;

			case "programa":
				
				break;

			case "objetivo":
				
				break;

			case "dimension":
				
				key = (String) currentSelection.getData();
				eje = (GluoSectorEjeDimension) dimensionMap.get(key).getEntity();
				Set<GluoObjetivo> gluoObjetivos = eje.getGluoObjetivos();
				dimensionMap.remove(key);
				eje = null;
				
				for(GluoObjetivo obj : gluoObjetivos) {
					Set<GluoPrograma> gluoProgramas = obj.getGluoProgramas();
					Iterator itObjMap = objetivoMap.keySet().iterator();
					while (itObjMap.hasNext()) {
						key = (String) itObjMap.next();
						if(objetivoMap.get(key).getEntity().equals(obj)) {
							objetivoMap.remove(key);
						}
					}
					itObjMap=null;
					
					for(GluoPrograma prog : gluoProgramas) {
						Set<GluoSubprograma> gluoSubprogrmas = prog.getGluoSubprogramas();
						Iterator itPrgMap = programaMap.keySet().iterator();
						while (itPrgMap.hasNext()) {
							key = (String) itPrgMap.next();
							if (programaMap.get(key).getEntity().equals(prog)) {
								programaMap.remove(key);
							}
						}
						itPrgMap = null;
						
						for(GluoSubprograma subProg : gluoSubprogrmas) {
							Set<GluoProyecto> gluoProyetos = subProg.getGluoProyectos();
							Iterator itSubPrgMap = subProgramaMap.keySet().iterator();
							while (itSubPrgMap.hasNext()) {
								key = (String) itSubPrgMap.next();
								if (subProgramaMap.get(key).getEntity().equals(subProg)) {
									subProgramaMap.remove(key);
								}
							}
							itPrgMap = null;
							
							for(GluoProyecto proy : gluoProyetos) {
								Iterator itProyMap = proyectoMap.keySet().iterator();
								while (itProyMap.hasNext()) {
									key = (String) itProyMap.next();
									if(proyectoMap.get(key).getEntity().equals(proy)) {
										proyectoMap.remove(key);
									}
								}
								itProyMap = null;
								
							}
						}
					}
//					for (Map.Entry<String, ElementosPlan> entry : programaMap.entrySet()) {
//					    System.out.println("clave=" + entry.getKey() + ", valor=" + entry.getValue().getEntity());
//					}
				}
				
				
				break;
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public String actionLimpiarAnadirDimension() {

		return "";
	}

	public String actionGuardar() {

		try {

			businessDelegatorView.saveGluoPlanDesarrollo(plan);

			Iterator itDimKey = dimensionMap.keySet().iterator();
			while (itDimKey.hasNext()) {
				String key = (String) itDimKey.next();
				businessDelegatorView
						.saveGluoSectorEjeDimension((GluoSectorEjeDimension) dimensionMap.get(key).getEntity());
			}

			Iterator itObjMap = objetivoMap.keySet().iterator();
			while (itObjMap.hasNext()) {
				String key = (String) itObjMap.next();
				businessDelegatorView.saveGluoObjetivo((GluoObjetivo) objetivoMap.get(key).getEntity());
			}

			Iterator itPrgMap = programaMap.keySet().iterator();
			while (itPrgMap.hasNext()) {
				String key = (String) itPrgMap.next();
				businessDelegatorView.saveGluoPrograma((GluoPrograma) programaMap.get(key).getEntity());
			}

			Iterator itSubPrgMap = subProgramaMap.keySet().iterator();
			while (itSubPrgMap.hasNext()) {
				String key = (String) itSubPrgMap.next();
				businessDelegatorView.saveGluoSubprograma((GluoSubprograma) subProgramaMap.get(key).getEntity());
			}

			Iterator itProyMap = proyectoMap.keySet().iterator();
			while (itProyMap.hasNext()) {
				String key = (String) itProyMap.next();
				businessDelegatorView.saveGluoProyecto((GluoProyecto) proyectoMap.get(key).getEntity());
			}

			Iterator itDetProyMap = detalleProyectoMap.keySet().iterator();
			while (itDetProyMap.hasNext()) {
				String key = (String) itDetProyMap.next();
				businessDelegatorView
						.saveGluoDetalleProyecto((GluoDetalleProyecto) detalleProyectoMap.get(key).getEntity());
			}

			Iterator itInticadorMap = indicadorMap.keySet().iterator();
			while (itInticadorMap.hasNext()) {
				String key = (String) itInticadorMap.next();
				businessDelegatorView.saveGluoIndicador((GluoIndicador) indicadorMap.get(key).getEntity());
			}

			Iterator itHistorialInticadorMap = historialIndicadorMap.keySet().iterator();
			while (itHistorialInticadorMap.hasNext()) {
				String key = (String) itHistorialInticadorMap.next();
				businessDelegatorView.saveGluoHistorialIndicador(
						(GluoHistorialIndicador) historialIndicadorMap.get(key).getEntity());
			}

			FacesUtils.addInfoMessage("El plan se ha guardado exitosamente!!");

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

		return "";
	}

	public String anadirPlanAction() {
		try {
			plan = new GluoPlanDesarrollo();

			// plan.setPlanId(1);valorTotalPresupuesto
			plan.setActivo("S");
			plan.setAnoFin(FacesUtils.checkDate(txtAnoFinPlan));
			plan.setAnoInicio(FacesUtils.checkDate(txtAnoInicioPlan));
			plan.setDescripcion(FacesUtils.checkString(txtAreaDescripcionPlan));
			plan.setEslogan(FacesUtils.checkString(txtAreaEsloganPlan));
			log.info("Eslogan: " + FacesUtils.checkString(txtAreaEsloganPlan));
			plan.setFechaCreacion(new Date());
			plan.setNombreAlcalde(FacesUtils.checkString(txtNombreAlcaldePlan));
			plan.setUsuCreador((int) 0);

			businessDelegatorView.evaluarGluoPlanDesarrollo(plan);

			String rowKey = "plan" + contPlan;

			rootNode = new DefaultOrganigramNode("root", plan.getDescripcion(), null);
			rootNode.setRowKey(rowKey);
			rootNode.setSelectable(true);

			RequestContext.getCurrentInstance().update("formModal");
			RequestContext.getCurrentInstance().update("form");
			FacesUtils.addInfoMessage("Plan añadido correctamente");

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

		return "";
	}

	public void anadirDimensionAction() {
		log.info("anadirDimensionAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			eje = new GluoSectorEjeDimension();
			eje.setActivo("S");
			eje.setDescripcion(txtDimension.getValue().toString().trim());
			eje.setFechaCreacion(new Date());
			eje.setUsuCreador((int) 0);
			eje.setGluoPlanDesarrollo(plan);

			businessDelegatorView.evaluarGluoSectorEjeDimension(eje);

			String rowKey = "dim" + contDimension;
			contDimension++;

			String dataNode = "(" + rowKey + ") - " + eje.getDescripcion();
			sector = new DefaultOrganigramNode("dimension", dataNode, currentSelection);
			sector.setSelectable(true);
			sector.setDroppable(true);

			elementoPlan = new ElementosPlan(sector, eje);
			elementoPlan.setRowKey(rowKey);

			dimensionMap.put(dataNode, elementoPlan);
			
			eje = null;
			// txtDimension.resetValue();
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void anadirObjetivoAction() {
		log.info("anadirObjetivoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			objetivo = new GluoObjetivo();
			objetivo.setActivo("S");
			objetivo.setFechaCreacion(new Date());
			String descripcion = txtAreaDescObjetivo.getValue().toString().trim();
			objetivo.setDescripcion(descripcion);
			objetivo.setUsuCreador(1);
			
			eje = (GluoSectorEjeDimension) dimensionMap.get(currentSelection.getData()).getEntity();
			objetivo.setGluoSectorEjeDimension(eje);

			businessDelegatorView.evaluarGluoObjetivo(objetivo);

			String rowKey = "obj" + contObjetivo;
			contObjetivo++;

			String dataNode = "(" + rowKey + ") - " + descripcion;
			nodoObjetivo = new DefaultOrganigramNode("objetivo", dataNode, currentSelection);
			nodoObjetivo.setSelectable(true);
			nodoObjetivo.setDraggable(true);
			nodoObjetivo.setDroppable(true);

			elementoPlan = new ElementosPlan(nodoObjetivo, objetivo);
			elementoPlan.setRowKey(rowKey);

			objetivoMap.put(dataNode, elementoPlan);
			
			Set<GluoObjetivo> gluoObjetivos = eje.getGluoObjetivos();
			gluoObjetivos.add(objetivo);
			
			eje.setGluoObjetivos(gluoObjetivos);
			
			objetivo = null;
			eje = null;

			// txtAreaDescObjetivo.resetValue();
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void anadirProgramaAction() {
		log.info("anadirProgramaAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			programa = new GluoPrograma();
			programa.setActivo("S");
			String descripcion = txtAreaDesPrograma.getValue().toString().trim();
			programa.setDescripcion(descripcion);
			programa.setFechaCreacion(new Date());
			programa.setUsuCreador((int) 0);
			objetivo = (GluoObjetivo) objetivoMap.get(currentSelection.getData()).getEntity();
			programa.setGluoObjetivo(objetivo);

			businessDelegatorView.evaluarGluoPrograma(programa);

			String rowKey = "prog" + contPrograma;
			contPrograma++;

			String dataNode = "(" + rowKey + ") - " + descripcion;
			OrganigramNode nodoPrograma = new DefaultOrganigramNode("programa", dataNode, currentSelection);
			nodoPrograma.setSelectable(true);
			nodoPrograma.setDraggable(true);
			nodoPrograma.setDroppable(true);

			elementoPlan = new ElementosPlan(nodoPrograma, programa);
			elementoPlan.setRowKey(rowKey);

			programaMap.put(dataNode, elementoPlan);
			
			Set<GluoPrograma> gluoProgramas = objetivo.getGluoProgramas();
			gluoProgramas.add(programa);
			
			objetivo.setGluoProgramas(gluoProgramas);
			
			programa = null;
			objetivo = null;
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void anadirSubProgramaAction() {
		log.info("anadirSubProgramaAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			subprograma = new GluoSubprograma();
			subprograma.setActivo("S");
			String descripcion = txtAreaDescSubPrograma.getValue().toString().trim();
			subprograma.setDescripcion(descripcion);
			subprograma.setFechaCreacion(new Date());
			subprograma.setUsuCreador((int) 0);
			programa = (GluoPrograma) programaMap.get(currentSelection.getData()).getEntity();
			subprograma.setGluoPrograma(programa);

			businessDelegatorView.evaluarGluoSubprograma(subprograma);

			String rowKey = "subProg" + contSubPrograma;
			contSubPrograma++;

			String dataNode = "(" + rowKey + ") - " + descripcion;
			OrganigramNode nodoSubPrograma = new DefaultOrganigramNode("subprograma", dataNode, currentSelection);
			nodoSubPrograma.setSelectable(true);
			nodoSubPrograma.setDraggable(true);
			nodoSubPrograma.setDroppable(true);

			elementoPlan = new ElementosPlan(nodoSubPrograma, subprograma);
			elementoPlan.setRowKey(rowKey);
			
			subProgramaMap.put(dataNode, elementoPlan);
			
			Set<GluoSubprograma> gluoSubProgramas = programa.getGluoSubprogramas();
			gluoSubProgramas.add(subprograma);
			
			programa.setGluoSubprogramas(gluoSubProgramas);
			
			programa = null;
			subprograma = null;
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void anadirProyectoAction() {
		log.info("anadirProyectoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			proyecto = new GluoProyecto();
			proyecto.setActivo("S");
			String descripcion = txtAreaDescProyecto.getValue().toString().trim();
			proyecto.setDescripcion(descripcion);
			proyecto.setFechaCreacion(new Date());
			proyecto.setUsuCreador((int) 0);
			subprograma = (GluoSubprograma) subProgramaMap.get(currentSelection.getData()).getEntity();
			proyecto.setGluoSubprograma(subprograma);

			businessDelegatorView.evaluarGluoProyecto(proyecto);

			String rowKey = "proy" + contProyecto;
			contProyecto++;

			String dataNode = "(" + rowKey + ") - " + descripcion;
			OrganigramNode nodoProyecto = new DefaultOrganigramNode("proyecto", dataNode, currentSelection);
			nodoProyecto.setSelectable(true);
			nodoProyecto.setDraggable(true);
			nodoProyecto.setDroppable(true);

			elementoPlan = new ElementosPlan(nodoProyecto, proyecto);
			elementoPlan.setRowKey(rowKey);

			proyectoMap.put(dataNode, elementoPlan);
			
			Set<GluoProyecto> gluoProyectos = subprograma.getGluoProyectos();
			gluoProyectos.add(proyecto);
			
			subprograma.setGluoProyectos(gluoProyectos);;
			
			subprograma = null;
			proyecto = null;

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void anadirDetalleProyectoAction() {
		log.info("anadirDetalleProyectoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			detalleProyecto = new GluoDetalleProyecto();
			if (numValorPresupuesto.getValue() == null) {
				throw new Exception("Ingrese un valor para el presupuesto");
			}
			detalleProyecto.setValorTotalPresupuesto(
					Utilities.convertirStringADouble(numValorPresupuesto.getValue().toString()));
			detalleProyecto.setActivo("S");
			detalleProyecto.setFechaCreacion(new Date());
			detalleProyecto.setUsuCreador(1);
			proyecto = (GluoProyecto) proyectoMap.get(currentSelection.getData()).getEntity();
			detalleProyecto.setGluoProyecto(proyecto);

			GluoAnoFiscal anoFiscal = businessDelegatorView
					.getGluoAnoFiscal(Integer.parseInt(somAnoFiscal.getValue().toString()));
			detalleProyecto.setGluoAnoFiscal(anoFiscal);

			businessDelegatorView.evaluarGluoDetalleProyecto(detalleProyecto);

			String rowKey = "DtProy" + contDetalleProyecto;
			contDetalleProyecto++;

			String dataNode = "(" + rowKey + ") - " + detalleProyecto.getGluoAnoFiscal().getAnoFiscal();
			OrganigramNode nodoDetalleProyecto = new DefaultOrganigramNode("detalleProyecto", dataNode,
					currentSelection);
			nodoDetalleProyecto.setSelectable(true);
			nodoDetalleProyecto.setDraggable(true);
			nodoDetalleProyecto.setDroppable(true);

			elementoPlan = new ElementosPlan(nodoDetalleProyecto, detalleProyecto);
			elementoPlan.setRowKey(rowKey);

			detalleProyectoMap.put(dataNode, elementoPlan);
			
			Set<GluoDetalleProyecto> gluoDetalleProyectos = proyecto.getGluoDetalleProyectos();
			gluoDetalleProyectos.add(detalleProyecto);
			
			proyecto.setGluoDetalleProyectos(gluoDetalleProyectos);
			
			proyecto = null;
			detalleProyecto = null;
			
		} catch (Exception e) {

		}
	}

	public void anadirIndicadorAction() {
		log.info("anadirIndicadorAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			indicador = new GluoIndicador();

			indicador.setActivo("S");
			indicador.setDescripcionIndicador(txtDescIndicador.getValue().toString());
			indicador.setDescripcionLineaBase(txtDescLineaBase.getValue().toString());
			indicador.setDescripcionMeta(txtDescMeta.getValue().toString());
			indicador.setValorMeta(Double.parseDouble(numValorMeta.getValue().toString().trim()));
			indicador.setGluoProyecto((GluoProyecto) proyectoMap.get(currentSelection.getData()).getEntity());
			indicador.setFechaCreacion(new Date());
			indicador.setUsuCreador(1);
			businessDelegatorView.validateGluoIndicador(indicador);

			String rowKey = "Ind" + contIndicador;
			contIndicador++;

			String dataNode = "(" + rowKey + ") - " + indicador.getDescripcionIndicador();
			OrganigramNode nodoIndicador = new DefaultOrganigramNode("indicador", dataNode, currentSelection);
			nodoIndicador.setSelectable(true);
			nodoIndicador.setDraggable(true);

			elementoPlan = new ElementosPlan(nodoIndicador, indicador);
			elementoPlan.setRowKey(rowKey);

			indicadorMap.put(dataNode, elementoPlan);
			
			Set<GluoIndicador> gluoIndicadors = proyecto.getGluoIndicadors();
			gluoIndicadors.add(indicador);
			
			proyecto.setGluoIndicadors(gluoIndicadors);
			
			proyecto = null;
			indicador = null;

		} catch (Exception e) {

		}
	}

	public void anadirHistorialIndicadorAction() {
		log.info("anadirHistorialIndicadorAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			historialIndicador = new GluoHistorialIndicador();

			historialIndicador.setActivo("S");
			historialIndicador.setFecha(FacesUtils.checkDate(txtHIAno));
			historialIndicador.setValorReal(Double.parseDouble(numHIValorReal.getValue().toString()));
			historialIndicador.setFechaCreacion(new Date());
			historialIndicador.setUsuCreador(1);
			historialIndicador
					.setGluoIndicador((GluoIndicador) indicadorMap.get(currentSelection.getData()).getEntity());
			businessDelegatorView.validateGluoHistorialIndicador(historialIndicador);

			String rowKey = "HInd" + contHistorialIndicador;
			contHistorialIndicador++;

			String dataNode = "(" + rowKey + ") - " + historialIndicador.getValorReal();
			OrganigramNode nodoHistorialIndicador = new DefaultOrganigramNode("historialIndicador", dataNode,
					currentSelection);
			nodoHistorialIndicador.setSelectable(true);
			nodoHistorialIndicador.setDraggable(true);

			elementoPlan = new ElementosPlan(nodoHistorialIndicador, historialIndicador);
			elementoPlan.setRowKey(rowKey);

			historialIndicadorMap.put(dataNode, elementoPlan);

		} catch (Exception e) {

		}
	}
	
	
	public void dialogModificarPlan() {
		try {		
			
			GluoPlanDesarrollo plan = (GluoPlanDesarrollo) this.plan;
			
			 txtModNombreAlcaldePlan.setValue(plan.getNombreAlcalde());
			 txtModAreaDescripcionPlan.setValue(plan.getDescripcion());
			 txtModAreaEsloganPlan.setValue(plan.getEslogan());
			 txtModAnoInicioPlan.setValue(plan.getAnoInicio());
			 txtModAnoFinPlan.setValue(plan.getAnoFin());
			 if(plan.getActivo().toLowerCase().trim().equals("s")) {
			 sbcModPlanActivo.setValue(true);
			 }else {
			 sbcModPlanActivo.setValue(false);
			 }
			 plan.setFechaModificacion(new Date());
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void dialogModificarDimension() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			GluoSectorEjeDimension dimension = (GluoSectorEjeDimension) dimensionMap.get(currentSelection.getData())
					.getEntity();

			txtModDimension.setValue(dimension.getDescripcion());
			if (dimension.getActivo().toLowerCase().trim().equals("s")) {
				sbcModEjeActivo.setValue(true);
			} else {
				sbcModEjeActivo.setValue(false);
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void dialogModificarObjetivo() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			GluoObjetivo objetivo = (GluoObjetivo) objetivoMap.get(currentSelection.getData()).getEntity();

			txtAreaModObjDesc.setValue(objetivo.getDescripcion());
			if (objetivo.getActivo().toLowerCase().trim().equals("s")) {
				sbcModObjActivo.setValue(true);
			} else {
				sbcModObjActivo.setValue(false);
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void dialogModificarPrograma() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			GluoPrograma programa = (GluoPrograma) programaMap.get(currentSelection.getData()).getEntity();

			txtAreaModProgDesc.setValue(programa.getDescripcion());
			if (programa.getActivo().toLowerCase().trim().equals("s")) {
				sbcModProgActivo.setValue(true);
			} else {
				sbcModProgActivo.setValue(false);
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void dialogModificarSubPrograma() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			GluoSubprograma subPrograma = (GluoSubprograma) subProgramaMap.get(currentSelection.getData()).getEntity();

			txtAreaModSubProgDesc.setValue(subPrograma.getDescripcion());
			if (subPrograma.getActivo().toLowerCase().trim().equals("s")) {
				sbcModSubProgActivo.setValue(true);
			} else {
				sbcModSubProgActivo.setValue(false);
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void dialogModificarProyecto() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			GluoProyecto proyecto = (GluoProyecto) proyectoMap.get(currentSelection.getData()).getEntity();

			txtAreaModProyDesc.setValue(proyecto.getDescripcion());
			if (proyecto.getActivo().toLowerCase().trim().equals("s")) {
				sbcModProyActivo.setValue(true);
			} else {
				sbcModProyActivo.setValue(false);
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void dialogModificarDetalleProyecto() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			GluoDetalleProyecto detalleProyecto = (GluoDetalleProyecto) detalleProyectoMap
					.get(currentSelection.getData()).getEntity();

			numModDPValorPresupuesto.setValue(detalleProyecto.getValorTotalPresupuesto());
			if (detalleProyecto.getActivo().toLowerCase().trim().equals("s")) {
				sbcModDPActivo.setValue(true);
			} else {
				sbcModDPActivo.setValue(false);
			}
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void dialogModificarIndicador() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			GluoIndicador indicador = (GluoIndicador) indicadorMap.get(currentSelection.getData()).getEntity();

			txtModDescIndicador.setValue(indicador.getDescripcionIndicador());
			txtModDescLineaBase.setValue(indicador.getDescripcionLineaBase());
			txtModDescMeta.setValue(indicador.getDescripcionMeta());
			numModValorMeta.setValue(indicador.getValorMeta());
			if (proyecto.getActivo().toLowerCase().trim().equals("s")) {
				sbcModProyActivo.setValue(true);
			} else {
				sbcModProyActivo.setValue(false);
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void dialogModificarHistorialIndicador() {
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			GluoHistorialIndicador historialIndicador = (GluoHistorialIndicador) historialIndicadorMap
					.get(currentSelection.getData()).getEntity();

			numModHIValorReal.setValue(numHIValorReal);
			txtModHIAno.setValue(txtHIAno);
			if (historialIndicador.getActivo().toLowerCase().trim().equals("s")) {
				sbcModHistorialIndicadorActivo.setValue(true);
			} else {
				sbcModHistorialIndicadorActivo.setValue(false);
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	
	public void modificarPlanAction() {
		log.info("modificarPlanAction");
		try {

			OrganigramNode nodePlan = rootNode;

			plan.setNombreAlcalde(txtModNombreAlcaldePlan.getValue().toString());
			plan.setEslogan(txtModAreaEsloganPlan.getValue().toString());
			plan.setDescripcion(txtModAreaDescripcionPlan.getValue().toString());
			plan.setAnoInicio(FacesUtils.checkDate(txtModPlanAnoInicioPlan.getValue().toString()));
			plan.setAnoFin(FacesUtils.checkDate(txtModPlanAnoFinPlan.getValue().toString()));
			
			nodePlan.setData(
					plan.getDescripcion());
			if (sbcModPlanActivo.isSelected() == true) {
				plan.setActivo("S");
			} else {
				plan.setActivo("N");
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}
	
	
	public void modificarDimensionAction() {
		log.info("modificarDimensionAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			OrganigramNode nodeDimension = dimensionMap.get(currentSelection.getData()).getNodeEntity();
			GluoSectorEjeDimension dimension = (GluoSectorEjeDimension) dimensionMap.get(currentSelection.getData())
					.getEntity();

			dimension.setDescripcion(txtModDimension.getValue().toString());
			nodeDimension.setData("(" + dimensionMap.get(currentSelection.getData()).getRowKey() + ") - "
					+ dimension.getDescripcion());
			if (sbcModEjeActivo.isSelected() == true) {
				dimension.setActivo("S");
			} else {
				dimension.setActivo("N");
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void modificarObjetivoAction() {
		log.info("modificarObjetivoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			OrganigramNode nodeObjetivo = objetivoMap.get(currentSelection.getData()).getNodeEntity();
			GluoObjetivo objetivo = (GluoObjetivo) objetivoMap.get(currentSelection.getData()).getEntity();

			objetivo.setDescripcion(txtAreaModObjDesc.getValue().toString());
			nodeObjetivo.setData(
					"(" + objetivoMap.get(currentSelection.getData()).getRowKey() + ") - " + objetivo.getDescripcion());
			if (sbcModObjActivo.isSelected() == true) {
				objetivo.setActivo("S");
			} else {
				objetivo.setActivo("N");
			}
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void modificarProgramaAction() {
		log.info("modificarProgramaAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			OrganigramNode nodoPrograma = programaMap.get(currentSelection.getData()).getNodeEntity();
			GluoPrograma programa = (GluoPrograma) programaMap.get(currentSelection.getData()).getEntity();

			programa.setDescripcion(txtAreaModProgDesc.getValue().toString());
			nodoPrograma.setData(
					"(" + programaMap.get(currentSelection.getData()).getRowKey() + ") - " + programa.getDescripcion());
			if (sbcModProgActivo.isSelected() == true) {
				programa.setActivo("S");
			} else {
				programa.setActivo("N");
			}
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void modificarSubProgramaAction() {
		log.info("modificarSubProgramaAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			OrganigramNode nodoSubPrograma = subProgramaMap.get(currentSelection.getData()).getNodeEntity();
			GluoSubprograma subPrograma = (GluoSubprograma) subProgramaMap.get(currentSelection.getData()).getEntity();

			subPrograma.setDescripcion(txtAreaModSubProgDesc.getValue().toString());
			nodoSubPrograma.setData("(" + subProgramaMap.get(currentSelection.getData()).getRowKey() + ") - "
					+ subPrograma.getDescripcion());
			if (sbcModSubProgActivo.isSelected() == true) {
				subPrograma.setActivo("S");
			} else {
				subPrograma.setActivo("N");
			}
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void modificarProyectoAction() {
		log.info("modificarProyectoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			OrganigramNode nodoProyecto = proyectoMap.get(currentSelection.getData()).getNodeEntity();
			GluoProyecto proyecto = (GluoProyecto) proyectoMap.get(currentSelection.getData()).getEntity();

			proyecto.setDescripcion(txtAreaModProyDesc.getValue().toString());
			nodoProyecto.setData(
					"(" + proyectoMap.get(currentSelection.getData()).getRowKey() + ") - " + proyecto.getDescripcion());
			if (sbcModProyActivo.isSelected() == true) {
				proyecto.setActivo("S");
			} else {
				proyecto.setActivo("N");
			}
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void modificarDetalleProyectoAction() {
		log.info("modificarDetalleProyectoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

			OrganigramNode nodoDetalleProyecto = detalleProyectoMap.get(currentSelection.getData()).getNodeEntity();
			GluoDetalleProyecto detalleProyecto = (GluoDetalleProyecto) detalleProyectoMap
					.get(currentSelection.getData()).getEntity();

			GluoAnoFiscal anoFiscal = businessDelegatorView
					.getGluoAnoFiscal(Integer.parseInt(somAnoFiscal.getValue().toString()));
			detalleProyecto.setGluoAnoFiscal(anoFiscal);

			detalleProyecto
					.setValorTotalPresupuesto(Double.parseDouble(numModDPValorPresupuesto.getValue().toString()));
			nodoDetalleProyecto.setData("(" + detalleProyectoMap.get(currentSelection.getData()).getRowKey() + ") - "
					+ detalleProyecto.getGluoAnoFiscal().getAnoFiscal());
			if (sbcModDPActivo.isSelected() == true) {
				detalleProyecto.setActivo("S");
			} else {
				detalleProyecto.setActivo("N");
			}

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void modificarIndicadorAction() {
		log.info("modificarIndicdorAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			OrganigramNode nodoIndicador = indicadorMap.get(currentSelection.getData()).getNodeEntity();
			GluoIndicador indicador = (GluoIndicador) indicadorMap.get(currentSelection.getData()).getEntity();

			indicador.setDescripcionIndicador(txtModDescIndicador.getValue().toString());
			nodoIndicador.setData("(" + indicadorMap.get(currentSelection.getData()).getRowKey() + ") - "
					+ indicador.getDescripcionIndicador());
			if (sbcModIndicadorActivo.isSelected() == true) {
				indicador.setActivo("S");
			} else {
				indicador.setActivo("N");
			}
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void modificarHistorialIndicadorAction() {
		log.info("modificarHistorialIndicadorAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			OrganigramNode nodoHistorialIndicador = historialIndicadorMap.get(currentSelection.getData())
					.getNodeEntity();
			GluoHistorialIndicador historialIndicador = (GluoHistorialIndicador) historialIndicadorMap
					.get(currentSelection.getData()).getEntity();

			historialIndicador.setValorReal(Double.parseDouble(numModHIValorReal.getValue().toString()));
			nodoHistorialIndicador.setData("(" + historialIndicadorMap.get(currentSelection.getData()).getRowKey()
					+ ") - " + historialIndicador.getValorReal());
			if (sbcModHistorialIndicadorActivo.isSelected() == true) {
				historialIndicador.setActivo("S");
			} else {
				historialIndicador.setActivo("N");
			}
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public OrganigramNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(OrganigramNode rootNode) {
		this.rootNode = rootNode;
	}

	public OrganigramNode getSector() {
		return sector;
	}

	public void setSector(OrganigramNode sector) {
		this.sector = sector;
	}

	public OrganigramNode getSelection() {
		return selection;
	}

	public void setSelection(OrganigramNode selection) {
		this.selection = selection;
	}

	public boolean isZoom() {
		return zoom;
	}

	public void setZoom(boolean zoom) {
		this.zoom = zoom;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public int getLeafNodeConnectorHeight() {
		return leafNodeConnectorHeight;
	}

	public void setLeafNodeConnectorHeight(int leafNodeConnectorHeight) {
		this.leafNodeConnectorHeight = leafNodeConnectorHeight;
	}

	public IBusinessDelegatorView getBusinessDelegatorView() {
		return businessDelegatorView;
	}

	public void setBusinessDelegatorView(IBusinessDelegatorView businessDelegatorView) {
		this.businessDelegatorView = businessDelegatorView;
	}

	public SelectBooleanCheckbox getSbcModHistorialIndicadorActivo() {
		return sbcModHistorialIndicadorActivo;
	}

	public void setSbcModHistorialIndicadorActivo(SelectBooleanCheckbox sbcModHistorialIndicadorActivo) {
		this.sbcModHistorialIndicadorActivo = sbcModHistorialIndicadorActivo;
	}

	public List<SelectItem> getLosAnosFiscalItem() throws Exception {
		if (losAnosFiscalItem == null) {
			losAnosFiscalItem = new ArrayList<SelectItem>();
			List<GluoAnoFiscalDTO> losAnosFiscal = businessDelegatorView.getDataGluoAnoFiscal();
			log.info("Sí carga el wey SelectItem");
			for (GluoAnoFiscalDTO anosFiscal : losAnosFiscal) {
				losAnosFiscalItem
						.add(new SelectItem(anosFiscal.getAnofId(), String.valueOf(anosFiscal.getAnoFiscal())));
			}
		}
		return losAnosFiscalItem;
	}

	public void setLosAnosFiscalItem(List<SelectItem> losAnosFiscalItem) {
		this.losAnosFiscalItem = losAnosFiscalItem;
	}

	// -----------------------------------------
	// Setters and Getters variables de dialogos
	// -----------------------------------------
	public InputTextarea getTxtAreaDescripcionPlan() {
		return txtAreaDescripcionPlan;
	}

	public void setTxtAreaDescripcionPlan(InputTextarea txtDescripcionPlan) {
		this.txtAreaDescripcionPlan = txtDescripcionPlan;
	}

	public InputTextarea getTxtAreaEsloganPlan() {
		return txtAreaEsloganPlan;
	}

	public void setTxtAreaEsloganPlan(InputTextarea txtAreaEsloganPlan) {
		this.txtAreaEsloganPlan = txtAreaEsloganPlan;
	}

	public InputText getTxtNombreAlcaldePlan() {
		return txtNombreAlcaldePlan;
	}

	public void setTxtNombreAlcaldePlan(InputText txtNombreAlcaldePlan) {
		this.txtNombreAlcaldePlan = txtNombreAlcaldePlan;
	}

	public Calendar getTxtAnoFinPlan() {
		return txtAnoFinPlan;
	}

	public void setTxtAnoFinPlan(Calendar txtAnoFinPlan) {
		this.txtAnoFinPlan = txtAnoFinPlan;
	}

	public Calendar getTxtAnoInicioPlan() {
		return txtAnoInicioPlan;
	}

	public void setTxtAnoInicioPlan(Calendar txtAnoInicioPlan) {
		this.txtAnoInicioPlan = txtAnoInicioPlan;
	}

	public InputText getTxtDimension() {
		return txtDimension;
	}

	public void setTxtDimension(InputText txtDimension) {
		this.txtDimension = txtDimension;
	}

	public InputTextarea getTxtAreaDescObjetivo() {
		return txtAreaDescObjetivo;
	}

	public void setTxtAreaDescObjetivo(InputTextarea txtAreaDescObjetivo) {
		this.txtAreaDescObjetivo = txtAreaDescObjetivo;
	}

	public InputTextarea getTxtAreaDesPrograma() {
		return txtAreaDesPrograma;
	}

	public void setTxtAreaDesPrograma(InputTextarea txtAreaDesPrograma) {
		this.txtAreaDesPrograma = txtAreaDesPrograma;
	}

	public InputTextarea getTxtAreaDescSubPrograma() {
		return txtAreaDescSubPrograma;
	}

	public void setTxtAreaDescSubPrograma(InputTextarea txtAreaDescSubPrograma) {
		this.txtAreaDescSubPrograma = txtAreaDescSubPrograma;
	}

	public InputTextarea getTxtAreaDescProyecto() {
		return txtAreaDescProyecto;
	}

	public void setTxtAreaDescProyecto(InputTextarea txtAreaDescProyecto) {
		this.txtAreaDescProyecto = txtAreaDescProyecto;
	}

	public SelectOneMenu getSomAnoFiscal() {
		return somAnoFiscal;
	}

	public void setSomAnoFiscal(SelectOneMenu somAnoFiscal) {
		this.somAnoFiscal = somAnoFiscal;
	}

	public InputNumber getNumValorPresupuesto() {
		return numValorPresupuesto;
	}

	public void setNumValorPresupuesto(InputNumber numValorPresupuesto) {
		this.numValorPresupuesto = numValorPresupuesto;
	}

	public CommandButton getBtnAnadirPlan() {
		return btnAnadirPlan;
	}

	public void setBtnAnadirPlan(CommandButton btnAnadirPlan) {
		this.btnAnadirPlan = btnAnadirPlan;
	}

	public CommandButton getBtnAnadirDimension() {
		return btnAnadirDimension;
	}

	public void setBtnAnadirDimension(CommandButton btnAnadirDimension) {
		this.btnAnadirDimension = btnAnadirDimension;
	}

	public CommandButton getBtnAnadirObjetivo() {
		return btnAnadirObjetivo;
	}

	public void setBtnAnadirObjetivo(CommandButton btnAnadirObjetivo) {
		this.btnAnadirObjetivo = btnAnadirObjetivo;
	}

	public CommandButton getBtnAnadirPrograma() {
		return btnAnadirPrograma;
	}

	public void setBtnAnadirPrograma(CommandButton btnAnadirPrograma) {
		this.btnAnadirPrograma = btnAnadirPrograma;
	}
	// Fin Setters and Getters variables de dialogos

	// Setters and Getters Variables para los dialogos de modificar

	public InputTextarea getTxtAreaModPlanDescripcion() {
		return txtAreaModPlanDescripcion;
	}

	public void setTxtAreaModPlanDescripcion(InputTextarea txtAreaModPlanDescripcion) {
		this.txtAreaModPlanDescripcion = txtAreaModPlanDescripcion;
	}

	public InputTextarea getTxtAreaModPlanEslogan() {
		return txtAreaModPlanEslogan;
	}

	public void setTxtAreaModPlanEslogan(InputTextarea txtAreaModPlanEslogan) {
		this.txtAreaModPlanEslogan = txtAreaModPlanEslogan;
	}

	public InputText getTxtModPlanNombreAlcaldePlan() {
		return txtModPlanNombreAlcaldePlan;
	}

	public void setTxtModPlanNombreAlcaldePlan(InputText txtModPlanNombreAlcaldePlan) {
		this.txtModPlanNombreAlcaldePlan = txtModPlanNombreAlcaldePlan;
	}

	public Calendar getTxtModPlanAnoFinPlan() {
		return txtModPlanAnoFinPlan;
	}

	public void setTxtModPlanAnoFinPlan(Calendar txtModPlanAnoFinPlan) {
		this.txtModPlanAnoFinPlan = txtModPlanAnoFinPlan;
	}

	public Calendar getTxtHIAno() {
		return txtHIAno;
	}

	public void setTxtHIAno(Calendar txtHIAno) {
		this.txtHIAno = txtHIAno;
	}

	public InputNumber getNumHIValorReal() {
		return numHIValorReal;
	}

	public void setNumHIValorReal(InputNumber numHIValorReal) {
		this.numHIValorReal = numHIValorReal;
	}

	public Calendar getTxtModPlanAnoInicioPlan() {
		return txtModPlanAnoInicioPlan;
	}

	public void setTxtModPlanAnoInicioPlan(Calendar txtModPlanAnoInicioPlan) {
		this.txtModPlanAnoInicioPlan = txtModPlanAnoInicioPlan;
	}

	public SelectBooleanCheckbox getSbcModPlanActivo() {
		return sbcModPlanActivo;
	}

	public void setSbcModPlanActivo(SelectBooleanCheckbox sbcModPlanActivo) {
		this.sbcModPlanActivo = sbcModPlanActivo;
	}

	public SelectBooleanCheckbox getSbcModEjeActivo() {
		return sbcModEjeActivo;
	}

	public void setSbcModEjeActivo(SelectBooleanCheckbox sbcModEjeActivo) {
		this.sbcModEjeActivo = sbcModEjeActivo;
	}

	public InputTextarea getTxtAreaModObjDesc() {
		return txtAreaModObjDesc;
	}

	public void setTxtAreaModObjDesc(InputTextarea txtAreaModObjDesc) {
		this.txtAreaModObjDesc = txtAreaModObjDesc;
	}

	public SelectBooleanCheckbox getSbcModObjActivo() {
		return sbcModObjActivo;
	}

	public void setSbcModObjActivo(SelectBooleanCheckbox sbcModObjActivo) {
		this.sbcModObjActivo = sbcModObjActivo;
	}

	public InputTextarea getTxtAreaModProgDesc() {
		return txtAreaModProgDesc;
	}

	public void setTxtAreaModProgDesc(InputTextarea txtAreaModProgDesc) {
		this.txtAreaModProgDesc = txtAreaModProgDesc;
	}

	public SelectBooleanCheckbox getSbcModProgActivo() {
		return sbcModProgActivo;
	}

	public void setSbcModProgActivo(SelectBooleanCheckbox sbcModProgActivo) {
		this.sbcModProgActivo = sbcModProgActivo;
	}

	public InputTextarea getTxtAreaModSubProgDesc() {
		return txtAreaModSubProgDesc;
	}

	public void setTxtAreaModSubProgDesc(InputTextarea txtAreaModSubProgDesc) {
		this.txtAreaModSubProgDesc = txtAreaModSubProgDesc;
	}

	public SelectBooleanCheckbox getSbcModSubProgActivo() {
		return sbcModSubProgActivo;
	}

	public void setSbcModSubProgActivo(SelectBooleanCheckbox sbcModSubProgActivo) {
		this.sbcModSubProgActivo = sbcModSubProgActivo;
	}

	public InputTextarea getTxtAreaModProyDesc() {
		return txtAreaModProyDesc;
	}

	public void setTxtAreaModProyDesc(InputTextarea txtAreaModProyDesc) {
		this.txtAreaModProyDesc = txtAreaModProyDesc;
	}

	public SelectBooleanCheckbox getSbcModProyActivo() {
		return sbcModProyActivo;
	}

	public void setSbcModProyActivo(SelectBooleanCheckbox sbcModProyActivo) {
		this.sbcModProyActivo = sbcModProyActivo;
	}

	public InputNumber getNumModDPValorPresupuesto() {
		return numModDPValorPresupuesto;
	}

	public void setNumModDPValorPresupuesto(InputNumber numModDPValorPresupuesto) {
		this.numModDPValorPresupuesto = numModDPValorPresupuesto;
	}

	public SelectBooleanCheckbox getSbcModDPActivo() {
		return sbcModDPActivo;
	}

	public void setSbcModDPActivo(SelectBooleanCheckbox sbcModDPActivo) {
		this.sbcModDPActivo = sbcModDPActivo;
	}

	public InputText getTxtDPValorTotalPresupuesto() {
		return txtDPValorTotalPresupuesto;
	}

	public void setTxtDPValorTotalPresupuesto(InputText txtDPValorTotalPresupuesto) {
		this.txtDPValorTotalPresupuesto = txtDPValorTotalPresupuesto;
	}

	public GluoIndicador getIndicador() {
		return indicador;
	}

	public void setIndicador(GluoIndicador indicador) {
		this.indicador = indicador;
	}

	public InputTextarea getTxtModAreaDescripcionPlan() {
		return txtModAreaDescripcionPlan;
	}

	public void setTxtModAreaDescripcionPlan(InputTextarea txtModAreaDescripcionPlan) {
		this.txtModAreaDescripcionPlan = txtModAreaDescripcionPlan;
	}

	public InputTextarea getTxtModAreaEsloganPlan() {
		return txtModAreaEsloganPlan;
	}

	public void setTxtModAreaEsloganPlan(InputTextarea txtModAreaEsloganPlan) {
		this.txtModAreaEsloganPlan = txtModAreaEsloganPlan;
	}

	public InputText getTxtModNombreAlcaldePlan() {
		return txtModNombreAlcaldePlan;
	}

	public void setTxtModNombreAlcaldePlan(InputText txtModNombreAlcaldePlan) {
		this.txtModNombreAlcaldePlan = txtModNombreAlcaldePlan;
	}

	public Calendar getTxtModAnoFinPlan() {
		return txtModAnoFinPlan;
	}

	public void setTxtModAnoFinPlan(Calendar txtModAnoFinPlan) {
		this.txtModAnoFinPlan = txtModAnoFinPlan;
	}

	public Calendar getTxtModAnoInicioPlan() {
		return txtModAnoInicioPlan;
	}

	public void setTxtModAnoInicioPlan(Calendar txtModAnoInicioPlan) {
		this.txtModAnoInicioPlan = txtModAnoInicioPlan;
	}

	public InputTextarea getTxtDescIndicador() {
		return txtDescIndicador;
	}

	public void setTxtDescIndicador(InputTextarea txtDescIndicador) {
		this.txtDescIndicador = txtDescIndicador;
	}

	public InputTextarea getTxtDescLineaBase() {
		return txtDescLineaBase;
	}

	public void setTxtDescLineaBase(InputTextarea txtDescLineaBase) {
		this.txtDescLineaBase = txtDescLineaBase;
	}

	public InputTextarea getTxtDescMeta() {
		return txtDescMeta;
	}

	public void setTxtDescMeta(InputTextarea txtDescMeta) {
		this.txtDescMeta = txtDescMeta;
	}

	public InputNumber getNumValorMeta() {
		return numValorMeta;
	}

	public void setNumValorMeta(InputNumber numValorMeta) {
		this.numValorMeta = numValorMeta;
	}

	public InputText getTxtModDescIndicador() {
		return txtModDescIndicador;
	}

	public void setTxtModDescIndicador(InputText txtModDescIndicador) {
		this.txtModDescIndicador = txtModDescIndicador;
	}

	public InputText getTxtModDescLineaBase() {
		return txtModDescLineaBase;
	}

	public void setTxtModDescLineaBase(InputText txtModDescLineaBase) {
		this.txtModDescLineaBase = txtModDescLineaBase;
	}

	public InputText getTxtModDescMeta() {
		return txtModDescMeta;
	}

	public void setTxtModDescMeta(InputText txtModDescMeta) {
		this.txtModDescMeta = txtModDescMeta;
	}

	public InputNumber getNumModValorMeta() {
		return numModValorMeta;
	}

	public void setNumModValorMeta(InputNumber numModValorMeta) {
		this.numModValorMeta = numModValorMeta;
	}

	public SelectBooleanCheckbox getSbcModIndicadorActivo() {
		return sbcModIndicadorActivo;
	}

	public void setSbcModIndicadorActivo(SelectBooleanCheckbox sbcModIndicadorActivo) {
		this.sbcModIndicadorActivo = sbcModIndicadorActivo;
	}

	public Calendar getTxtModHIAno() {
		return txtModHIAno;
	}

	public void setTxtModHIAno(Calendar txtModHIAno) {
		this.txtModHIAno = txtModHIAno;
	}

	public InputNumber getNumModHIValorReal() {
		return numModHIValorReal;
	}

	public void setNumModHIValorReal(InputNumber numModHIValorReal) {
		this.numModHIValorReal = numModHIValorReal;
	}

	public InputText getTxtModDimension() {
		return txtModDimension;
	}

	public void setTxtModDimension(InputText txtModDimension) {
		this.txtModDimension = txtModDimension;
	}

	public SelectOneMenu getSomModAnoFiscal() {
		return somModAnoFiscal;
	}

	public void setSomModAnoFiscal(SelectOneMenu somModAnoFiscal) {
		this.somModAnoFiscal = somModAnoFiscal;
	}

	// Fin Setters and Getters Variables para los dialogos de modificar
}