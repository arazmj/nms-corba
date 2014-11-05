/*
 This software is the confidential information and copyrighted work of
 NetCracker Technology Corp. ("NetCracker") and/or its suppliers and
 is only distributed under the terms of a separate license agreement
 with NetCracker.
 Use of the software is governed by the terms of the license agreement.
 Any use of this software not in accordance with the license agreement
 is expressly prohibited by law, and may result in severe civil
 and criminal penalties. 

 Copyright (c) 1995-2013 NetCracker Technology Corp.
 
 All Rights Reserved.
 
 */
package ex.corba.alu.transform.sax;

import globaldefs.ExceptionType_T;
import globaldefs.ProcessingFailureException;

import java.util.Hashtable;
import java.util.Map;

public class Corba2XMLContainer {
	private Corba2XMLStructure structure;
	private Map<String, String> data;

	public Corba2XMLContainer(Corba2XMLStructure structure) {
		this.structure = structure;
		data = new Hashtable<String, String>(structure.getFieldsCount());
	}

	public Corba2XMLStructure getStructure() {
		return structure;
	}

	public String getStructureName() {
		return structure.getName();
	}

	public String[] getFields() {
		return structure.getFields();
	}

	public int getFieldsCount() {
		return structure.getFieldsCount();
	}

	public void setFieldValue(String field, String value)
			throws ProcessingFailureException {
		if (structure.isFieldExists(field)) {
			data.put(field, value);
		} else {
			throw new ProcessingFailureException(
					ExceptionType_T.EXCPT_ENTITY_NOT_FOUND, "Field " + field
							+ " is not allowed for structure "
							+ structure.getName());
		}
	}

	public String getValue(String field) {
		return data.get(field) == null ? "" : data.get(field);
	}

	public String getValue(int index) {
		return data.get(structure.getFieldName(index));
	}
}
