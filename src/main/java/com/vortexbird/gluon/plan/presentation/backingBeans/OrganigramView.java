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
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortexbird.gluon.plan.modelo.ElementosPlan;
import com.vortexbird.gluon.plan.modelo.GluoAnoFiscal;
import com.vortexbird.gluon.plan.modelo.GluoDetalleProyecto;
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
import java.util.Iterator;
import java.util.List;
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
	
	private int contPlan;
	private int contDimension;
	private int contObjetivo;
	private int contPrograma;
	private int contSubPrograma;
	// Fin Variables para la creación de los entity

	// Variables de configuración para el organigrama
	private boolean zoom = true;
	private String style = "width: 100%";
	private int leafNodeConnectorHeight = 0;
	// Fin Variables de configuración para el organigrama

	private List<SelectItem> losAnosFiscalItem;
	// HashMaps para almacenar los nodos creados en la base de datos
	private HashMap<OrganigramNode, GluoSectorEjeDimension> dimensionHash = new HashMap<OrganigramNode, GluoSectorEjeDimension>();
	private HashMap<OrganigramNode, GluoObjetivo> objetivoHash = new HashMap<OrganigramNode, GluoObjetivo>();
	private HashMap<OrganigramNode, GluoPrograma> programaHash = new HashMap<OrganigramNode, GluoPrograma>();
	private HashMap<OrganigramNode, GluoSubprograma> subprogramaHash = new HashMap<OrganigramNode, GluoSubprograma>();
	private HashMap<OrganigramNode, GluoProyecto> proyectoHash = new HashMap<OrganigramNode, GluoProyecto>();
	private HashMap<OrganigramNode, GluoDetalleProyecto> detalleProyectoHash = new HashMap<OrganigramNode, GluoDetalleProyecto>();
	
	
	private Map<String, ElementosPlan> dimensionMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> objetivoMap = new HashMap<String, ElementosPlan>();
	private Map<String, ElementosPlan> programaMap = new HashMap<String, ElementosPlan>();
	// Fin HashMaps para almacenar los nodos creados en la base de datos

	// HashMaps para modificar los nodos
	private HashMap<OrganigramNode, GluoSectorEjeDimension> dimensionUpdateHash = new HashMap<OrganigramNode, GluoSectorEjeDimension>();
	private HashMap<OrganigramNode, GluoObjetivo> objetivoUpdateHash = new HashMap<OrganigramNode, GluoObjetivo>();
	private HashMap<OrganigramNode, GluoPrograma> programaUpdateHash = new HashMap<OrganigramNode, GluoPrograma>();
	private HashMap<OrganigramNode, GluoSubprograma> subprogramaUpdateHash = new HashMap<OrganigramNode, GluoSubprograma>();
	private HashMap<OrganigramNode, GluoProyecto> proyectoUpdateHash = new HashMap<OrganigramNode, GluoProyecto>();
	private HashMap<OrganigramNode, GluoDetalleProyecto> detalleProyectoUpdateHash = new HashMap<OrganigramNode, GluoDetalleProyecto>();
	// Fin HashMaps para modificar los nodos

	// Variables de dialogos
	private InputTextarea txtAreaDescripcionPlan;
	private InputTextarea txtAreaEsloganPlan;
	private InputText txtNombreAlcaldePlan;
	private Calendar txtAnoFinPlan;
	private Calendar txtAnoInicioPlan;
	private InputText txtDimension;
	private SelectOneMenu somDimensionObjetivo;
	private InputTextarea txtAreaDescObjetivo;
	private InputTextarea txtAreaDesPrograma;
	private InputTextarea txtAreaDescSubPrograma;
	private InputTextarea txtAreaDescProyecto;
	private SelectOneMenu somAnoFiscal;
	private InputNumber numValorPresupuesto;
	private String[] selectedDimension;
	private CommandButton btnAnadirPlan;
	private CommandButton btnAnadirDimension;
	private CommandButton btnAnadirObjetivo;
	private CommandButton btnAnadirPrograma;
	// Fin Variables de dialogos

	// Variables para los dialogos de modificar
	private InputTextarea txtAreaModPlanDescripcion;
	private InputTextarea txtAreaModPlanEslogan;
	private InputText txtModPlanNombreAlcaldePlan;
	private Calendar txtModPlanAnoFinPlan;
	private Calendar txtModPlanAnoInicioPlan;
	private SelectBooleanCheckbox sbcModPlanActivo;

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
	private InputText txtDPValorTotalPresupuesto;
	// Fin Variables para los dialogos de modificar

	@PostConstruct
	public void init() {
		selection = new DefaultOrganigramNode(null, "plan", null);
		selection.setRowKey("selectionNode");

		RequestContext.getCurrentInstance().execute("PF('dlgAnadirPlan').show()");

	}

	public void nodeSelectListener(OrganigramNodeSelectEvent event) {
		FacesUtils.addInfoMessage(event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData()
				+ "' seleccionado. RowKey: " + event.getOrganigramNode().getRowKey()+" - "+event.getBehavior().toString()+ " Selection: "+selection.getRowKey());
	}

	public void nodeCollapseListener(OrganigramNodeCollapseEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary(
				event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData() + "' colapsado.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeExpandListener(OrganigramNodeExpandEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary(
				event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData() + "' expandido.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
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
		// re-evaluate selection - might be a differenct object instance if viewstate
		// serialization is enabled
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
		currentSelection.getParent().getChildren().remove(currentSelection);
	}

	public void eliminarDimension() {

	}

	public void eliminarObjetivo() {

	}

	public void eliminarPrograma() {

	}

	public void eliminarSubPrograma() {

	}

	public void eliminarProyecto() {

	}

	public void eliminarDetalleProyecto() {

	}

	public String actionLimpiarAnadirDimension() {

		return "";
	}

	public String actionGuardar() {

		try {
			GluoSectorEjeDimension gsed = new GluoSectorEjeDimension();
			GluoObjetivo gob = new GluoObjetivo();
			GluoPrograma gpr = new GluoPrograma();
			GluoSubprograma gsub = new GluoSubprograma();
			GluoProyecto gproy = new GluoProyecto();
			GluoDetalleProyecto gdpro = new GluoDetalleProyecto();

			businessDelegatorView.saveGluoPlanDesarrollo(plan);

			for (Entry<OrganigramNode, GluoSectorEjeDimension> entry : dimensionUpdateHash.entrySet()) {
				gsed = entry.getValue();
				log.info("WOW------------ " + gsed.getDescripcion());
				businessDelegatorView.saveGluoSectorEjeDimension(gsed);
			}

			for (Entry<OrganigramNode, GluoObjetivo> entry : objetivoUpdateHash.entrySet()) {
				gob = entry.getValue();
				log.info("AGBC------------ " + gob.getDescripcion());
				businessDelegatorView.saveGluoObjetivo(gob);
			}

			for (Entry<OrganigramNode, GluoPrograma> entry : programaUpdateHash.entrySet()) {
				gpr = entry.getValue();
				log.info("AGBC------------ " + gpr.getDescripcion());
				businessDelegatorView.saveGluoPrograma(gpr);
			}
			////////////////////
			for (Entry<OrganigramNode, GluoSubprograma> entry : subprogramaUpdateHash.entrySet()) {
				gsub = entry.getValue();
				log.info("AGBC------------ " + gsub.getDescripcion());
				businessDelegatorView.saveGluoSubprograma(gsub);
			}

			for (Entry<OrganigramNode, GluoProyecto> entry : proyectoUpdateHash.entrySet()) {
				gproy = entry.getValue();
				log.info("AGBC------------ " +gproy.getDescripcion());
				businessDelegatorView.saveGluoProyecto(gproy);
			}

			for (Entry<OrganigramNode, GluoDetalleProyecto> entry : detalleProyectoUpdateHash.entrySet()) {
				gdpro = entry.getValue();
				log.info("AGBC------------ " + gdpro.getFechaModificacion());
				businessDelegatorView.saveGluoDetalleProyecto(gdpro);
			}

			FacesUtils.addInfoMessage("Se ha guardado el plan");

		} catch (Exception e) {
			// TODO Auto-generated catch block
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

			log.info("RowKey: " + selection.getRowKey());

			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection.getRowKey());
			eje = new GluoSectorEjeDimension();
			eje.setActivo("S");
			eje.setDescripcion(txtDimension.getValue().toString().trim());
			eje.setFechaCreacion(new Date());
			eje.setUsuCreador((int) 0);
			eje.setGluoPlanDesarrollo(plan);

			businessDelegatorView.evaluarGluoSectorEjeDimension(eje);

			String rowKey = "dim" + contDimension;
			contDimension++;

			sector = new DefaultOrganigramNode("dimension", "("+rowKey+") - "+eje.getDescripcion(), currentSelection);
			sector.setSelectable(true);
			
			elementoPlan = new ElementosPlan(sector, eje);
			
			dimensionHash.put(sector, eje);
			dimensionUpdateHash.put(currentSelection, eje);
			txtDimension.resetValue();
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void modificarDimensionAction() {
		log.info("modificarDimensionAction");
		try {

			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			eje = new GluoSectorEjeDimension();
			log.info("" + sbcModEjeActivo.getValue().toString());
			if (sbcModEjeActivo.getValue().toString().trim().equals("true")) {
				eje.setActivo("S");
			} else {
				eje.setActivo("N");
			}
			// Falta configurar
			String descripcion = txtAreaModObjDesc.getValue().toString().trim();
			objetivo.setDescripcion(descripcion);

			log.info("" + selection.getData());

			OrganigramNode nodoObjetivo = new DefaultOrganigramNode("eje", descripcion, currentSelection);
			nodoObjetivo.setSelectable(true);
			dimensionUpdateHash.replace(currentSelection, eje);

			txtAreaDescObjetivo.resetValue();
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void anadirObjetivoAction() {
		log.info("anadirObjetivoAction");
		try {

			log.info("RowKey: " + selection.getRowKey());

			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection.getRowKey());
			objetivo = new GluoObjetivo();
			objetivo.setActivo("S");
			objetivo.setFechaCreacion(new Date());
			String descripcion = txtAreaDescObjetivo.getValue().toString().trim();
			objetivo.setDescripcion(descripcion);
			objetivo.setUsuCreador(1);
			objetivo.setGluoSectorEjeDimension(dimensionHash.get(currentSelection));

			businessDelegatorView.evaluarGluoObjetivo(objetivo);

			String rowKey = "obj" + contObjetivo;
			contObjetivo++;
			

			nodoObjetivo = new DefaultOrganigramNode("objetivo", "("+rowKey+") - "+descripcion, currentSelection);
			nodoObjetivo.setSelectable(true);
			
			objetivoHash.put(nodoObjetivo, objetivo);
			objetivoUpdateHash.put(selection, objetivo);

			txtAreaDescObjetivo.resetValue();
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void modificarObjetivoAction() {
		log.info("modificarObjetivoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			GluoObjetivo objetivo = objetivoUpdateHash.get(selection);
			if (objetivo == null) {
				throw new Exception("El valor en el hash es nulo");
			}
			log.info(objetivo.getActivo());
			log.info(objetivo.getDescripcion());
			log.info("" + sbcModObjActivo.getValue().toString());
			if (sbcModObjActivo.getValue().toString().trim().equals("true")) {
				objetivo.setActivo("S");
			} else {
				objetivo.setActivo("N");
			}
			String descripcion = txtAreaModObjDesc.getValue().toString().trim();
			objetivo.setDescripcion(descripcion);
			objetivoUpdateHash.replace(currentSelection, objetivo);
			selection.setData(descripcion);
			log.info("Desc - " + programa.getDescripcion());
			log.info("slctData - " + selection.getData());
			log.info("objUpdDta - " + objetivoUpdateHash.get(currentSelection).getDescripcion());

			txtAreaDescObjetivo.resetValue();
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void anadirProgramaAction() {
		log.info("anadirProgramaAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection.getRowKey());
			programa = new GluoPrograma();
			programa.setActivo("S");
			String descripcion = txtAreaDesPrograma.getValue().toString().trim();
			programa.setDescripcion(descripcion);
			programa.setFechaCreacion(new Date());
			programa.setUsuCreador((int) 0);
			programa.setGluoObjetivo(objetivoHash.get(currentSelection));

			businessDelegatorView.evaluarGluoPrograma(programa);

			String rowKey = "prog" + contPrograma;
			contPrograma++;

			OrganigramNode nodoPrograma = new DefaultOrganigramNode("programa",
					"("+rowKey+") - "+descripcion, currentSelection);
			nodoPrograma.setSelectable(true);
			programaHash.put(nodoPrograma, programa);
			programaUpdateHash.put(currentSelection, programa);

		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}

	public void modificarProgramaAction() {
		log.info("modificarProgramaAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			programa = new GluoPrograma();
			log.info("" + sbcModObjActivo.getValue().toString());
			if (sbcModProgActivo.getValue().toString().trim().equals("true")) {
				programa.setActivo("S");
			} else {
				programa.setActivo("N");
			}
			String descripcion = txtAreaModProgDesc.getValue().toString().trim();
			programa.setDescripcion(descripcion);

			objetivoUpdateHash.replace(currentSelection, objetivo);
			selection.setData(descripcion);
			log.info("Desc - " + programa.getDescripcion());
			log.info("slctData - " + selection.getData());
			log.info("objUpdDta - " + objetivoUpdateHash.get(currentSelection).getDescripcion());

			txtAreaDescObjetivo.resetValue();
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}

	}

	public void anadirSubProgramaAction() {
		log.info("anadirSubProgramaAction");
		try {

			log.info("RowKey: " + selection.getRowKey());

			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection.getRowKey());
			subprograma = new GluoSubprograma();
			subprograma.setActivo("S");
			subprograma.setDescripcion(txtAreaDescSubPrograma.getValue().toString().trim());
			subprograma.setFechaCreacion(new Date());
			subprograma.setUsuCreador((int) 0);
			subprograma.setGluoPrograma(programaHash.get(currentSelection));

			businessDelegatorView.evaluarGluoSubprograma(subprograma);

			OrganigramNode nodoSubPrograma = new DefaultOrganigramNode("subprograma",
					txtAreaDescSubPrograma.getValue().toString().trim(), currentSelection);
			nodoSubPrograma.setSelectable(true);
			subprogramaHash.put(nodoSubPrograma, subprograma);
			subprogramaUpdateHash.put(currentSelection, subprograma);

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
			proyecto.setDescripcion(txtAreaDescProyecto.getValue().toString().trim());
			proyecto.setFechaCreacion(new Date());
			proyecto.setUsuCreador((int) 0);
			proyecto.setGluoSubprograma(subprogramaHash.get(currentSelection));

			businessDelegatorView.evaluarGluoProyecto(proyecto);

			OrganigramNode nodoProyecto = new DefaultOrganigramNode("proyecto",
					txtAreaDescProyecto.getValue().toString().trim(), currentSelection);
			nodoProyecto.setSelectable(true);
			proyectoHash.put(nodoProyecto, proyecto);
			proyectoUpdateHash.put(currentSelection, proyecto);

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
			detalleProyecto.setGluoProyecto(proyectoHash.get(currentSelection));

			GluoAnoFiscal anoFiscal = businessDelegatorView
					.getGluoAnoFiscal(Integer.parseInt(somAnoFiscal.getValue().toString()));
			detalleProyecto.setGluoAnoFiscal(anoFiscal);

			businessDelegatorView.evaluarGluoDetalleProyecto(detalleProyecto);

			OrganigramNode nodoDetalleProyecto = new DefaultOrganigramNode("detalleProyecto",
					detalleProyecto.getGluoAnoFiscal().getAnoFiscal(), currentSelection);
			detalleProyectoHash.put(nodoDetalleProyecto, detalleProyecto);
			detalleProyectoUpdateHash.put(currentSelection, detalleProyecto);

		} catch (Exception e) {

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

	public List<SelectItem> getLosAnosFiscalItem() throws Exception {
		if (losAnosFiscalItem == null) {
			losAnosFiscalItem = new ArrayList<SelectItem>();
			List<GluoAnoFiscalDTO> losAnosFiscal = businessDelegatorView.getDataGluoAnoFiscal();
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

	public void setTxtAreaEsloganPlan(InputTextarea txbatAreaEsloganPlan) {
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

	public SelectOneMenu getSomDimensionObjetivo() {
		return somDimensionObjetivo;
	}

	public void setSomDimensionObjetivo(SelectOneMenu somDimensionObjetivo) {
		this.somDimensionObjetivo = somDimensionObjetivo;
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

	public String[] getSelectedDimension() {
		return selectedDimension;
	}

	public void setSelectedDimension(String[] selectedDimension) {
		this.selectedDimension = selectedDimension;
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

	public HashMap<OrganigramNode, GluoSectorEjeDimension> getDimensionHash() {
		return dimensionHash;
	}

	public void setDimensionHash(HashMap<OrganigramNode, GluoSectorEjeDimension> dimensionHash) {
		this.dimensionHash = dimensionHash;
	}

	public HashMap<OrganigramNode, GluoObjetivo> getObjetivoHash() {
		return objetivoHash;
	}

	public void setObjetivoHash(HashMap<OrganigramNode, GluoObjetivo> objetivoHash) {
		this.objetivoHash = objetivoHash;
	}

	public HashMap<OrganigramNode, GluoPrograma> getProgramaHash() {
		return programaHash;
	}

	public void setProgramaHash(HashMap<OrganigramNode, GluoPrograma> programaHash) {
		this.programaHash = programaHash;
	}

	public HashMap<OrganigramNode, GluoSubprograma> getSubprogramaHash() {
		return subprogramaHash;
	}

	public void setSubprogramaHash(HashMap<OrganigramNode, GluoSubprograma> subprogramaHash) {
		this.subprogramaHash = subprogramaHash;
	}

	public HashMap<OrganigramNode, GluoProyecto> getProyectoHash() {
		return proyectoHash;
	}

	public void setProyectoHash(HashMap<OrganigramNode, GluoProyecto> proyectoHash) {
		this.proyectoHash = proyectoHash;
	}

	public HashMap<OrganigramNode, GluoDetalleProyecto> getDetalleProyectoHash() {
		return detalleProyectoHash;
	}

	public void setDetalleProyectoHash(HashMap<OrganigramNode, GluoDetalleProyecto> detalleProyectoHash) {
		this.detalleProyectoHash = detalleProyectoHash;
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

	// Fin Setters and Getters Variables para los dialogos de modificar
}