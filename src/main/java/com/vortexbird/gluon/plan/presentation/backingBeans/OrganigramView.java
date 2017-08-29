package com.vortexbird.gluon.plan.presentation.backingBeans;

import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.commandbutton.CommandButton;
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

import com.vortexbird.gluon.plan.modelo.GluoObjetivo;
import com.vortexbird.gluon.plan.modelo.GluoPlanDesarrollo;
import com.vortexbird.gluon.plan.modelo.GluoPrograma;
import com.vortexbird.gluon.plan.modelo.GluoSectorEjeDimension;
import com.vortexbird.gluon.plan.modelo.dto.GluoSectorEjeDimensionDTO;
import com.vortexbird.gluon.plan.presentation.businessDelegate.IBusinessDelegatorView;
import com.vortexbird.gluon.plan.utilities.FacesUtils;

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

@ManagedBean
@ViewScoped
public class OrganigramView implements Serializable {
	
	@ManagedProperty(value = "#{BusinessDelegatorView}")
	private IBusinessDelegatorView businessDelegatorView;

	private static final Logger log = LoggerFactory.getLogger(OrganigramView.class);
	
	private OrganigramNode rootNode;
	private OrganigramNode selection;

	private GluoPlanDesarrollo plan;
	private GluoSectorEjeDimension eje;
	private GluoObjetivo objetivo;
	
	private boolean zoom = true;
	private String style = "width: 100%";
	private int leafNodeConnectorHeight = 0;
	private boolean autoScrollToSelection = false;
	
	private List<OrganigramNode> nodosObjetivos;
	
	private List<Object[]> objetivosAnadidos;
	private List<OrganigramNode> nodosDimension;
	private List<GluoSectorEjeDimension> entitysDimension;
	private List<Object[]> dimensionesAnadidos = new ArrayList<Object[]>();
	private List<String> dataDimensiones;
	private List<Object[]> programasAnadidos;
	
	private HashMap<OrganigramNode, GluoSectorEjeDimension> dimensionHash;
	private HashMap<OrganigramNode, GluoObjetivo> objetivoHash;
	private HashMap<OrganigramNode, GluoPrograma> programaHash;

	// Variables de dialogos
	private InputText txtDescripcionPlan;
    private InputText txtEsloganPlan;
    private InputText txtNombreAlcaldePlan;
    private Calendar txtAnoFinPlan;
    private Calendar txtAnoInicioPlan;
    private InputText txtDimension;
    private SelectOneMenu somDimensionObjetivo;
    private InputTextarea txtAreaDescObjetivo;
    private InputTextarea txtAreaDescPrograma;
	private List<GluoSectorEjeDimensionDTO> dimensiones;
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
		Iterator<Entry<OrganigramNode, GluoSectorEjeDimension>> iDimension = getDimensionHash().entrySet().iterator();
		Iterator<Entry<OrganigramNode, GluoObjetivo>> iObjetivo = getObjetivoHash().entrySet().iterator();
		Iterator<Entry<OrganigramNode, GluoPrograma>> iPrograma = getProgramaHash().entrySet().iterator();
		
		try {
			businessDelegatorView.saveGluoPlanDesarrollo(plan);
			
			while(iDimension.hasNext()){
				Map.Entry<OrganigramNode, GluoSectorEjeDimension> e = (Entry<OrganigramNode, GluoSectorEjeDimension>)iDimension.next();
				businessDelegatorView.saveGluoSectorEjeDimension(e.getValue());
			}
			
			while(iDimension.hasNext()){
				Map.Entry<OrganigramNode, GluoObjetivo> e = (Entry<OrganigramNode, GluoObjetivo>)iObjetivo.next();
				businessDelegatorView.saveGluoObjetivo(e.getValue());
			}
			
			while(iPrograma.hasNext()){
				Map.Entry<OrganigramNode, GluoPrograma> e = (Entry<OrganigramNode, GluoPrograma>)iPrograma.next();
				businessDelegatorView.saveGluoPrograma(e.getValue());
			}
			

			
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
	
	public void anadirDimensionAction() {
		log.info("anadirDimensionAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			HashMap<OrganigramNode, GluoSectorEjeDimension> dataHash = new HashMap<OrganigramNode, GluoSectorEjeDimension>();
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
			dataHash.put(sector, eje);
			setDimensionHash(dataHash);
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
	}
	
	public void anadirObjetivoAction() {
		log.info("anadirObjetivoAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			HashMap<OrganigramNode, GluoObjetivo> dataHash = new HashMap<OrganigramNode, GluoObjetivo>();
			GluoObjetivo objetivo = new GluoObjetivo();
			objetivo.setActivo("S");
			objetivo.setFechaCreacion(new Date());
			String descripcion = txtAreaDescObjetivo.getValue().toString().trim();
			objetivo.setDescripcion(descripcion);
			objetivo.setUsuCreador(1);
			objetivo.setGluoSectorEjeDimension(getDimensionHash().get(currentSelection));
			
			businessDelegatorView.evaluarGluoObjetivo(objetivo);
			
			log.info(""+selection.getData());
			OrganigramNode nodoObjetivo = new DefaultOrganigramNode("objetivo", descripcion, currentSelection);
			nodoObjetivo.setSelectable(true);
			dataHash.put(nodoObjetivo, objetivo);
			setObjetivoHash(dataHash);
			
			
		} catch (Exception e) {
			FacesUtils.addErrorMessage(e.getMessage());
		}
		
	}
	
	public void anadirProgramaAction() {
		log.info("anadirProgramaAction");
		try {
			OrganigramNode currentSelection = OrganigramHelper.findTreeNode(rootNode, selection);
			HashMap<OrganigramNode, GluoPrograma> dataHash = new HashMap<OrganigramNode, GluoPrograma>();
			log.info(""+selection.getData());
			GluoPrograma programa = new GluoPrograma();
			programa.setActivo("S");
			programa.setDescripcion(txtAreaDescPrograma.getValue().toString().trim());
			programa.setFechaCreacion(new Date());
			programa.setUsuCreador((int)0);
			programa.setGluoObjetivo(objetivoHash.get(currentSelection));
			
			businessDelegatorView.evaluarGluoPrograma(programa);
			
			OrganigramNode nodoPrograma = new DefaultOrganigramNode("programa", txtAreaDescPrograma.getValue().toString().trim(), currentSelection);
			dataHash.put(nodoPrograma, programa);
			setProgramaHash(dataHash);
			
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
	
	public List<String> getDataDimensiones() {
		return dataDimensiones;
	}

	public void setDataDimensiones(List<String> dataDimensiones) {
		this.dataDimensiones = dataDimensiones;
	}

	// -----------------------------------------
	// Setters and Getters variables de dialogos
	// -----------------------------------------
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
	
	public InputTextarea getTxtAreaDescPrograma() {
		return txtAreaDescPrograma;
	}

	public void setTxtAreaDescPrograma(InputTextarea txtAreaDescPrograma) {
		this.txtAreaDescPrograma = txtAreaDescPrograma;
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