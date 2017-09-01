package com.vortexbird.gluon.plan.presentation.backingBeans;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.inputmask.InputMask;
import org.primefaces.component.inputnumber.InputNumber;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.organigram.OrganigramHelper;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	//Nodo raiz (rootNode) y nodo que guarda el nodo seleccionado (selection)
	private OrganigramNode rootNode;
	private OrganigramNode selection;

	//Variables para la creación de los entity
	private GluoPlanDesarrollo plan;
	private GluoSectorEjeDimension eje;
	private GluoObjetivo objetivo;
	private GluoPrograma programa;
	private GluoSubprograma subprograma;
	private GluoProyecto proyecto;
	private GluoDetalleProyecto detalleProyecto;
	//Fin Variables para la creación de los entity
	
	//Variables de configuración para el organigrama
	private boolean zoom = true;
	private String style = "width: 100%";
	private int leafNodeConnectorHeight = 0;
	//Fin Variables de configuración para el organigrama
	
	private List<SelectItem> losAnosFiscalItem;
	
	private HashMap<OrganigramNode, GluoSectorEjeDimension> dimensionHash = new HashMap<OrganigramNode, GluoSectorEjeDimension>();
	private HashMap<OrganigramNode, GluoObjetivo> objetivoHash = new HashMap<OrganigramNode, GluoObjetivo>();
	private HashMap<OrganigramNode, GluoPrograma> programaHash = new HashMap<OrganigramNode, GluoPrograma>();

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

	@PostConstruct
	public void init() {
		selection = new DefaultOrganigramNode(null, "plan", null);
		
		RequestContext.getCurrentInstance().execute("PF('dlgAnadirPlan').show()");
		
	}

	public void nodeSelectListener(OrganigramNodeSelectEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary(event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData() + "' seleccionado");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeCollapseListener(OrganigramNodeCollapseEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary(event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData() + "' colapsado.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void nodeExpandListener(OrganigramNodeExpandEvent event) {
		FacesMessage message = new FacesMessage();
		message.setSummary(event.getOrganigramNode().getType() + " '" + event.getOrganigramNode().getData() + "' expandido.");
		message.setSeverity(FacesMessage.SEVERITY_INFO);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	 public void listener_txtAnoFin() {
	        Date inputDate = (Date) txtAnoFinPlan.getValue();
	        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        FacesContext.getCurrentInstance()
	                    .addMessage("",
	            new FacesMessage("Fecha seleccionada " + dateFormat.format(inputDate)));
	    }

	    public void listener_txtAnoInicio() {
	        Date inputDate = (Date) txtAnoInicioPlan.getValue();
	        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        FacesContext.getCurrentInstance()
	                    .addMessage("",
	            new FacesMessage("Fecha seleccionada " + dateFormat.format(inputDate)));
	    }

	public void removeEmployee() {
		// re-evaluate selection - might be a differenct object instance if viewstate
		// serialization is enabled
		OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
		currentSelection.getParent().getChildren().remove(currentSelection);
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
	
	public String actionGuardar(){
		
		try {
			GluoSectorEjeDimension gsed = new GluoSectorEjeDimension();
			GluoObjetivo gob = new GluoObjetivo();
			GluoPrograma gpr = new GluoPrograma();
			
			businessDelegatorView.saveGluoPlanDesarrollo(plan);
			
			for(Entry<OrganigramNode, GluoSectorEjeDimension> entry: dimensionHash.entrySet()) {
				gsed = entry.getValue();
				log.info("WOW------------ "+gsed.getDescripcion());
			    businessDelegatorView.saveGluoSectorEjeDimension(gsed);
			}
			
			for(Entry<OrganigramNode, GluoObjetivo> entry: objetivoHash.entrySet()) {
				gob = entry.getValue();
				log.info("WOW------------ "+gob.getDescripcion());
			    businessDelegatorView.saveGluoObjetivo(gob);
			}
			
			for(Entry<OrganigramNode, GluoPrograma> entry: programaHash.entrySet()) {
				gpr = entry.getValue();
				log.info("WOW------------ "+gpr.getDescripcion());
			    businessDelegatorView.saveGluoPrograma(gpr);
			}
			
			FacesUtils.addInfoMessage("Se ha guardado el plan");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			FacesUtils.addErrorMessage(e.getMessage());
		}
		
		return "";
	}
	
	/*Código transferido a los Seters de los HashMaps
	 * 
	 * public HashMap<OrganigramNode, GluoSectorEjeDimension> guardarDimension(OrganigramNode sector, GluoSectorEjeDimension gluoSector){
		HashMap<OrganigramNode, GluoSectorEjeDimension> sectorEjeDimensionHash = new HashMap<OrganigramNode, GluoSectorEjeDimension>();
		sectorEjeDimensionHash.put(sector, gluoSector);
		return sectorEjeDimensionHash;
	}
	
	public HashMap<OrganigramNode, GluoObjetivo> guardarObjetivo(OrganigramNode objetivo, GluoObjetivo gluoObjetivo){
		HashMap<OrganigramNode, GluoObjetivo> objetivoHash = new HashMap<OrganigramNode, GluoObjetivo>();
		objetivoHash.put(objetivo, gluoObjetivo);	
		return objetivoHash;
	}
	
	public HashMap<OrganigramNode, GluoPrograma> guardarPrograma(OrganigramNode programa, GluoPrograma gluoPrograma){
		HashMap<OrganigramNode, GluoPrograma> programaHash = new HashMap<OrganigramNode, GluoPrograma>();
		programaHash.put(programa, gluoPrograma);
		return programaHash;
	}*/
	
	public String anadirPlanAction() {
        try {
        	plan = new GluoPlanDesarrollo();

            //plan.setPlanId(1);valorTotalPresupuesto
            plan.setActivo("S");
            plan.setAnoFin(FacesUtils.checkDate(txtAnoFinPlan));
            plan.setAnoInicio(FacesUtils.checkDate(txtAnoInicioPlan));
            plan.setDescripcion(FacesUtils.checkString(txtAreaDescripcionPlan));
            plan.setEslogan(FacesUtils.checkString(txtAreaEsloganPlan));
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
	
	public void anadirDimensionAction() {
		log.info("anadirDimensionAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			eje = new GluoSectorEjeDimension();
			eje.setActivo("S");
			eje.setDescripcion(txtDimension.getValue().toString().trim());
			eje.setFechaCreacion(new Date());
			eje.setUsuCreador((int)0);
			eje.setGluoPlanDesarrollo(plan);
			
			businessDelegatorView.evaluarGluoSectorEjeDimension(eje);
			
			log.info(""+selection.getData());
			OrganigramNode sector = new DefaultOrganigramNode("dimension", eje.getDescripcion(), currentSelection);
		
			sector.setSelectable(true);
			dimensionHash.put(sector, eje);
			txtDimension.resetValue();
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
			objetivo.setGluoSectorEjeDimension(dimensionHash.get(currentSelection));
			
			businessDelegatorView.evaluarGluoObjetivo(objetivo);
			
			log.info(""+selection.getData());
			OrganigramNode nodoObjetivo = new DefaultOrganigramNode("objetivo", descripcion, currentSelection);
			nodoObjetivo.setSelectable(true);
			objetivoHash.put(nodoObjetivo, objetivo);
			
			txtAreaDescObjetivo.resetValue();
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
			programa.setDescripcion(txtAreaDesPrograma.getValue().toString().trim());
			programa.setFechaCreacion(new Date());
			programa.setUsuCreador((int)0);
			programa.setGluoObjetivo(objetivoHash.get(currentSelection));
			
			businessDelegatorView.evaluarGluoPrograma(programa);
			
			OrganigramNode nodoPrograma = new DefaultOrganigramNode("programa", txtAreaDesPrograma.getValue().toString().trim(), currentSelection);
			nodoPrograma.setSelectable(true);
			programaHash.put(nodoPrograma, programa);
			
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
			subprograma.setDescripcion(txtAreaDescSubPrograma.getValue().toString().trim());
			subprograma.setFechaCreacion(new Date());
			subprograma.setUsuCreador((int)0);
			
			// Anadir el seteo del programa Padre
			//Añadir la evaluación del subprograma
			
			OrganigramNode nodoSubPrograma = new DefaultOrganigramNode("subprograma", txtAreaDescSubPrograma.getValue().toString().trim(), currentSelection);
			nodoSubPrograma.setSelectable(true);
			//programaHash.put(nodoSubPrograma, subprograma);
			
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
			proyecto.setUsuCreador((int)0);
			
			// Anadir el seteo del programa Padre
			//Añadir la evaluación del subprograma
			
			OrganigramNode nodoProyecto = new DefaultOrganigramNode("proyecto", txtAreaDescProyecto.getValue().toString().trim(), currentSelection);
			nodoProyecto.setSelectable(true);
			//programaHash.put(nodoProyecto, proyecto);
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}
	
	public void anadirDetalleProyectoAction() {
		log.info("anadirDetalleProyectoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			detalleProyecto = new GluoDetalleProyecto();
			if(numValorPresupuesto.getValue()==null) {
				throw new Exception("Ingrese un valor para el presupuesto");
			}
			detalleProyecto.setValorTotalPresupuesto(Utilities.convertirStringADouble(numValorPresupuesto.getValue().toString()));
			detalleProyecto.setActivo("S");
			detalleProyecto.setFechaCreacion(new Date());
			detalleProyecto.setUsuCreador(1);
			
			GluoAnoFiscal anoFiscal = businessDelegatorView.getGluoAnoFiscal(Integer.parseInt(somAnoFiscal.getValue().toString()));
			detalleProyecto.setGluoAnoFiscal(anoFiscal);
			
			OrganigramNode nodoDetalleProyecto = new DefaultOrganigramNode("detalleProyecto", detalleProyecto.getGluoAnoFiscal(), currentSelection);

			
		} catch (Exception e) {
			
		}
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
		if(losAnosFiscalItem==null){
			losAnosFiscalItem=new ArrayList<SelectItem>();
			List<GluoAnoFiscalDTO> losAnosFiscal = businessDelegatorView.getDataGluoAnoFiscal();
			for (GluoAnoFiscalDTO anosFiscal : losAnosFiscal) {
				losAnosFiscalItem.add(new SelectItem(anosFiscal.getAnofId(), String.valueOf(anosFiscal.getAnoFiscal())));
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
	
	
	
	// Fin Setters and Getters variables de dialogos


}