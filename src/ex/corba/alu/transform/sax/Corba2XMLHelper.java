package ex.corba.alu.transform.sax;

import ex.corba.CorbaConstants;
import globaldefs.ProcessingFailureException;
import managedElement.ManagedElement_T;

public class Corba2XMLHelper {
	public Corba2XMLHandler handler;

	public Corba2XMLHelper(Corba2XMLHandler handler) {
		this.handler = handler;
	}

	public Corba2XMLContainer getManagedElementParams(
			ManagedElement_T managedElement) throws ProcessingFailureException {
		Corba2XMLContainer container = new Corba2XMLContainer(
				Corba2XMLStructure.NETWORK_ELEMENTS);

		container.setFieldValue(CorbaConstants.NE_ID_STR, handler
				.getValueByName(managedElement.name,
						CorbaConstants.MANAGED_ELEMENT_STR));
		container.setFieldValue(CorbaConstants.USER_LABEL_STR,
				managedElement.userLabel);
		container.setFieldValue(CorbaConstants.NE_NAME_STR,
				managedElement.nativeEMSName);
		container.setFieldValue(CorbaConstants.OWNER_STR, managedElement.owner);
		container.setFieldValue(CorbaConstants.LOCATION_STR,
				managedElement.location);
		container.setFieldValue(CorbaConstants.VERSION_STR,
				managedElement.version);
		container.setFieldValue(CorbaConstants.PRODUCT_NAME_STR,
				managedElement.productName);
		container.setFieldValue(CorbaConstants.COMMUNICATION_STATE_STR,
				String.valueOf(managedElement.communicationState.value()));
		container.setFieldValue(CorbaConstants.EMS_INSYNC_STATE_STR,
				String.valueOf(managedElement.emsInSyncState));
		container
				.setFieldValue(CorbaConstants.SUPPORTED_RATES_STR, handler
						.convertShortArrayToString(
								managedElement.supportedRates, ", "));
		container.setFieldValue(CorbaConstants.GATEWAYS_STR, handler
				.getValueByName(managedElement.additionalInfo, "GateWay"));

		return container;
	}
}
