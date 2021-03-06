package com.vortexbird.gluon.plan.modelo;

import org.primefaces.model.OrganigramNode;

public class ElementosPlan {

	private OrganigramNode nodeEntity;
	private Object entity;
	private String rowKey = null;
	
	public ElementosPlan(OrganigramNode nodeEntity, Object entity) {
		this.nodeEntity = nodeEntity;
		this.entity = entity;
	}

	public OrganigramNode getNodeEntity() {
		return nodeEntity;
	}

	public void setNodeEntity(OrganigramNode nodeEntity) {
		this.nodeEntity = nodeEntity;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}
	
}
