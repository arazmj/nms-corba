package ex.corba.ciena.transform.sax;

import java.util.Hashtable;
import java.util.Map;

import com.netcracker.ciena.oncenter.v11.globaldefs.ExceptionType_T;
import com.netcracker.ciena.oncenter.v11.globaldefs.ProcessingFailureException;

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
