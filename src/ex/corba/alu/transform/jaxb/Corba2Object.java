package ex.corba.alu.transform.jaxb;

import managedElement.ManagedElement_T;
import equipment.EquipmentHolder_T;
import equipment.HolderState_T;
import ex.corba.CorbaConstants;
import ex.corba.xml.EquipmentHolder;
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

	public static EquipmentHolder getEquipmentHolder(EquipmentHolder_T aHolder) {
		String state;
		switch (aHolder.holderState.value()) {
		case HolderState_T._EMPTY:
			state = "EMPTY";
			break;
		case HolderState_T._EXPECTED_AND_NOT_INSTALLED:
			state = "EXPECTED_AND_NOT_INSTALLED";
			break;
		case HolderState_T._INSTALLED_AND_EXPECTED:
			state = "INSTALLED_AND_EXPECTED";
			break;
		case HolderState_T._INSTALLED_AND_NOT_EXPECTED:
			state = "INSTALLED_AND_NOT_EXPECTED:";
			break;
		case HolderState_T._MISMATCH_OF_INSTALLED_AND_EXPECTED:
			state = "MISMATCH_OF_INSTALLED_AND_EXPECTED";
			break;
		case HolderState_T._UNAVAILABLE:
			state = "UNAVAILABLE";
			break;
		case HolderState_T._UNKNOWN:
			state = CorbaConstants.UNKNOWN_STR;
			break;
		default:
			state = CorbaConstants.UNKNOWN_STR;
		}

		EquipmentHolder holder = new EquipmentHolder();

		holder.setNeID(getValueByName(aHolder.name,
				CorbaConstants.MANAGED_ELEMENT_STR));
		holder.setHolderName(getValueByName(aHolder.name,
				CorbaConstants.EQUIPMENT_HOLDER_STR));
		holder.setUserLabel(aHolder.userLabel);
		holder.setNativeEMSName(aHolder.nativeEMSName);
		holder.setOwner(aHolder.owner);
		holder.setAlarmReportingIndicator(aHolder.alarmReportingIndicator);
		holder.setHolderType(aHolder.holderType);
		holder.setExpectedOrInstalledEquipment(convertNameAndStringValueToString(aHolder.expectedOrInstalledEquipment));
		holder.setAcceptableEquipmentTypeList(aHolder.acceptableEquipmentTypeList);
		holder.setHolderState(state);
		holder.setAdditionalInfo(convertNameAndStringValueToString(aHolder.additionalInfo));

		return holder;
	}

	public static String getValueByName(NameAndStringValue_T[] nasv, String name) {
		for (int i = 0; i < nasv.length; i++) {
			if (nasv[i].name.equals(name)) {
				return nasv[i].value;
			}
		}

		return "";
	}

	public static String convertNameAndStringValueToString(
			NameAndStringValue_T[] nameAndValue) {
		if (nameAndValue == null) {
			return "";
		}

		StringBuffer nameAndValuesList = new StringBuffer();

		for (NameAndStringValue_T nv : nameAndValue) {
			nameAndValuesList.append(nv.name).append('=').append(nv.value)
					.append(';');
		}

		return nameAndValuesList.toString();
	}
}
