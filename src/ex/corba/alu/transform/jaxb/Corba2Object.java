package ex.corba.alu.transform.jaxb;

import managedElement.ManagedElement_T;
import ex.corba.CorbaConstants;
import ex.corba.xml.ManagedElement;
import globaldefs.NameAndStringValue_T;

public class Corba2Object {

	public static ManagedElement getManagedElement(
			ManagedElement_T managedElement) {
		ManagedElement meObject = new ManagedElement();

		meObject.setNeID(getValueByName(managedElement.name,
				CorbaConstants.MANAGED_ELEMENT_STR));
		meObject.setNeName(managedElement.nativeEMSName);
		meObject.setUserLabel(managedElement.userLabel);
		meObject.setOwner(managedElement.owner);
		meObject.setLocation(managedElement.location);
		meObject.setProductName(managedElement.productName);
		meObject.setCommunicationState(String
				.valueOf(managedElement.communicationState.value()));
		meObject.setEmsSyncState(managedElement.emsInSyncState);
		meObject.setSupportedRate(managedElement.supportedRates);
		meObject.setGateway(getValueByName(managedElement.additionalInfo,
				"GateWay"));

		return meObject;
	}

	public static String getValueByName(NameAndStringValue_T[] nasv, String name) {
		for (int i = 0; i < nasv.length; i++) {
			if (nasv[i].name.equals(name)) {
				return nasv[i].value;
			}
		}
		return "";
	}
}
