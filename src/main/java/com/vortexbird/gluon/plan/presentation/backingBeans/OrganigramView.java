package com.vortexbird.gluon.plan.presentation.backingBeans;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.organigram.OrganigramHelper;
import org.primefaces.context.RequestContext;
import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vortexbird.gluon.plan.modelo.GluoObjetivo;
import com.vortexbird.gluon.plan.modelo.GluoPlanDesarrollo;
import com.vortexbird.gluon.plan.modelo.dto.GluoSectorEjeDimensionDTO;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;
import com.vortexbird.gluon.plan.utilities.FacesUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class OrganigramView implements Serializable {
	
	@ManagedProperty(value = "#{BusinessDelegatorView}")
	private IBusinessDelegatorView businessDelegatorView;

	private static final Logger log = LoggerFactory.getLogger(OrganigramView.class);
	
	private OrganigramNode rootNode;
	private OrganigramNode selection;

	private GluoPlanDesarrollo plan;
	
	private boolean zoom = true;
	private String style = "width: 100%";
	private int leafNodeConnectorHeight = 0;
	private boolean autoScrollToSelection = false;

	private String employeeName;
	
	private List<OrganigramNode> nodosObjetivos;
	
	private List<Object[]> objetivosAnadidos;
	private List<Object[]> dimensionessAnadidos;
	private List<Object[]> programasAnadidos;

	// Variables de dialogos
	private InputText txtDescripcionPlan;
    private InputText txtEsloganPlan;
    private InputText txtNombreAlcaldePlan;
    private Calendar txtAnoFinPlan;
    private Calendar txtAnoInicioPlan;
	private List<GluoSectorEjeDimensionDTO> dimensiones;
	private String[] selectedDimension;
	private InputTextarea txtDescripcionObjetivo;
	private CommandButton btnAnadirPlan;
	private CommandButton btnAnadirDimension;
	private CommandButton btnAnadirObjetivo;
	private CommandButton btnAnadirPrograma;
	// Fin Variables de dialogos

	@PostConstruct
	public void init() {
		selection = new DefaultOrganigramNode(null, "Ridvan Agar", null);
		
		RequestContext.getCurrentInstance().execute("PF('dlgAnadirPlan').show()");
//		rootNode.setCollapsible(false);
//		rootNode.setDroppable(true);

//		OrganigramNode softwareDevelopment = addDivision(rootNode, "Software Development", "Ridvan Agar");
//
//		OrganigramNode teamJavaEE = addDivision(softwareDevelopment, "Team JavaEE");
//		addDivision(teamJavaEE, "JSF", "Thomas Andraschko");
//		addDivision(teamJavaEE, "Backend", "Marie Louise");
//
//		OrganigramNode teamMobile = addDivision(softwareDevelopment, "Team Mobile");
//		addDivision(teamMobile, "Android", "Andy Ruby");
//		addDivision(teamMobile, "iOS", "Stevan Jobs");
//
//		addDivision(rootNode, "Managed Services", "Thorsten Schultze", "Sandra Becker");
//
//		OrganigramNode marketing = addDivision(rootNode, "Marketing");
//		addDivision(marketing, "Social Media", "Ali Mente", "Susanne Muster");
//		addDivision(marketing, "Press", "Manfred Mustermann", "Hans Peter");
//
//		addDivision(rootNode, "Management", "Hassan El Manfalouty");
		
	}

	protected OrganigramNode addNode(String type, OrganigramNode parent, String name) throws Exception {
		if(name.isEmpty()) {
			throw new Exception("El nombre del nodo no puede estar vacio");
		}
		if(parent==null) {
			throw new Exception("El nodo padre no puede ser nulo o el tipo de nodo es incorrecto");
		}
		if(type=="dimension" || type=="objetivo" || type=="programa") {
			OrganigramNode node = new DefaultOrganigramNode(type, name, parent);
			node.setDroppable(true);
			node.setSelectable(true);
			return node;
		}else {
			throw new Exception("El tipo de nodo es incorrecto");
		}
	}

//	public void nodeDragDropListener(OrganigramNodeDragDropEvent event) {
//		FacesMessage message = new FacesMessage();
//		message.setSummary("Node '" + event.getOrganigramNode().getData() + "' moved from "
//				+ event.getSourceOrganigramNode().getData() + " to '" + event.getTargetOrganigramNode().getData()
//				+ "'");
//		message.setSeverity(FacesMessage.SEVERITY_INFO);
//
//		FacesContext.getCurrentInstance().addMessage(null, message);
//	}

	public void nodeSelectListener(OrganigramNodeSelectEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary("Node '" + event.getOrganigramNode().getData() + "' selected.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeCollapseListener(OrganigramNodeCollapseEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary("Node '" + event.getOrganigramNode().getData() + "' collapsed.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeExpandListener(OrganigramNodeExpandEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary("Node '" + event.getOrganigramNode().getData() + "' expanded.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	 public void listener_txtAnoFin() {
	        Date inputDate = (Date) txtAnoFinPlan.getValue();
	        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        FacesContext.getCurrentInstance()
	                    .addMessage("",
	            new FacesMessage("Selected Date " + dateFormat.format(inputDate)));
	    }

	    public void listener_txtAnoInicio() {
	        Date inputDate = (Date) txtAnoInicioPlan.getValue();
	        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        FacesContext.getCurrentInstance()
	                    .addMessage("",
	            new FacesMessage("Selected Date " + dateFormat.format(inputDate)));
	    }

	public void removeDivision() {
		// re-evaluate selection - might be a differenct object instance if viewstate
		// serialization is enabled
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
		setMessage(currentSelection.getData() + " will entfernt werden.", FacesMessage.SEVERITY_INFO);
	}

	public void removeEmployee() {
		// re-evaluate selection - might be a differenct object instance if viewstate
		// serialization is enabled
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
		currentSelection.getParent().getChildren().remove(currentSelection);
	}

	public void addEmployee() {
		// re-evaluate selection - might be a differenct object instance if viewstate
		// serialization is enabled
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);

		OrganigramNode employee = new DefaultOrganigramNode("employee", employeeName, currentSelection);
		employee.setDraggable(true);
		employee.setSelectable(true);
	}

	private void setMessage(String msg, FacesMessage.Severity severity) {
		FacesMessage message = new FacesMessage();
		message.setSummary(msg);
		message.setSeverity(severity);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public String actionLimpiarAnadirDimension() {

		return "";
	}
	
	public String actionCerrarDialogAnadirDimension() {

		actionLimpiarAnadirDimension();
		return "";
	}
	
	public String anadirPlanAction() {
        try {
        	plan = new GluoPlanDesarrollo();

            //plan.setPlanId(1);
            plan.setActivo("S");
            plan.setAnoFin(FacesUtils.checkDate(txtAnoFinPlan));
            plan.setAnoInicio(FacesUtils.checkDate(txtAnoInicioPlan));
            plan.setDescripcion(FacesUtils.checkString(txtDescripcionPlan));
            plan.setEslogan(FacesUtils.checkString(txtEsloganPlan));
            plan.setFechaCreacion(new Date());
            plan.setNombreAlcalde(FacesUtils.checkString(txtNombreAlcaldePlan));
            plan.setUsuCreador((int) 0);
            
            businessDelegatorView.evaluarGluoPlanDesarrollo(plan);
            
            rootNode = new DefaultOrganigramNode("root", plan.getDescripcion(), null);
    		rootNode.setSelectable(true);
    		RequestContext.getCurrentInstance().update("formModal");
    		RequestContext.getCurrentInstance().update("form");
    		FacesUtils.addInfoMessage("Plan añadido correctamente");
        } catch (Exception e) {
            FacesUtils.addErrorMessage(e.getMessage());
        }

        return "";
    }
	
	public String anadirDimensionAction() {
		log.info("anadirDimensionAction");
		try {
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
		return "";
	}
	
	public String anadirObjetivoAction() {
		log.info("anadirObjetivoAction");
		try {
			GluoObjetivo objetivo = new GluoObjetivo();
			objetivo.setActivo("S");
			objetivo.setFechaCreacion(new Date());
			String descripcion = txtDescripcionObjetivo.getValue().toString().trim();
			objetivo.setDescripcion(descripcion);
			objetivo.setUsuCreador(1);
			OrganigramNode softwareDevelopment = addNode("objetivo", rootNode, txtDescripcionObjetivo.getValue().toString().trim());
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
		return "";
	}
	
	public String anadirProgramaAction() {
		log.info("anadirProgramaAction");
		try {
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
		return "";
	}
	
	public OrganigramNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(OrganigramNode rootNode) {
		this.rootNode = rootNode;
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

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
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

	public boolean isAutoScrollToSelection() {
		return autoScrollToSelection;
	}

	public void setAutoScrollToSelection(boolean autoScrollToSelection) {
		this.autoScrollToSelection = autoScrollToSelection;
	}

	public List<OrganigramNode> getNodosObjetivos() {
		return nodosObjetivos;
	}

	public void setNodosObjetivos(List<OrganigramNode> nodosObjetivos) {
		this.nodosObjetivos = nodosObjetivos;
	}
	
	public IBusinessDelegatorView getBusinessDelegatorView() {
		return businessDelegatorView;
	}

	public void setBusinessDelegatorView(IBusinessDelegatorView businessDelegatorView) {
		this.businessDelegatorView = businessDelegatorView;
	}
	
	// Setters and Getters variables de dialogos
	public InputText getTxtDescripcionPlan() {
		return txtDescripcionPlan;
	}

	public void setTxtDescripcionPlan(InputText txtDescripcionPlan) {
		this.txtDescripcionPlan = txtDescripcionPlan;
	}

	public InputText getTxtEsloganPlan() {
		return txtEsloganPlan;
	}

	public void setTxtEsloganPlan(InputText txtEsloganPlan) {
		this.txtEsloganPlan = txtEsloganPlan;
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
	
	public List<GluoSectorEjeDimensionDTO> getDimensiones() throws Exception {
		if (dimensiones == null) {
			dimensiones = businessDelegatorView.getDataGluoSectorEjeDimension();
			log.info("----"+dimensiones.get(0).getDescripcion());
		}
		return dimensiones;
	}

	public void setDimensiones(List<GluoSectorEjeDimensionDTO> dimensiones) {
		this.dimensiones = dimensiones;
	}

	
	public String[] getSelectedDimension() {
		return selectedDimension;
	}

	public void setSelectedDimension(String[] selectedDimension) {
		this.selectedDimension = selectedDimension;
	}
	
	public InputTextarea getTxtDescripcionObjetivo() {
		return txtDescripcionObjetivo;
	}

	public void setTxtDescripcionObjetivo(InputTextarea txtDescripcionObjetivo) {
		this.txtDescripcionObjetivo = txtDescripcionObjetivo;
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
}