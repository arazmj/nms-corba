package ex.corba.ciena.transform.sax;

import org.xml.sax.SAXException;

import com.ciena.oc.equipment.EquipmentHolder_T;
import com.ciena.oc.equipment.EquipmentOrHolder_T;
import com.ciena.oc.equipment.Equipment_T;
import com.ciena.oc.equipment.HolderState_T;
import com.ciena.oc.globaldefs.NameAndStringValue_T;
import com.ciena.oc.globaldefs.ProcessingFailureException;
import com.ciena.oc.managedElement.ManagedElement_T;
import com.ciena.oc.subnetworkConnection.SubnetworkConnection_T;
import com.ciena.oc.terminationPoint.Directionality_T;
import com.ciena.oc.terminationPoint.TPConnectionState_T;
import com.ciena.oc.terminationPoint.TPProtectionAssociation_T;
import com.ciena.oc.terminationPoint.TPType_T;
import com.ciena.oc.terminationPoint.TerminationMode_T;
import com.ciena.oc.terminationPoint.TerminationPoint_T;

import ex.corba.CorbaConstants;

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

	public void printEquipmentOrHolder(EquipmentOrHolder_T eoh)
			throws ProcessingFailureException, SAXException {
		if (eoh.discriminator().value() == 1) {
			handler.printStructure(getHolderParams(eoh.holder()));
		} else {
			handler.printStructure(getEquipmentParams(eoh.equip()));
		}
	}

	public Corba2XMLContainer getEquipmentParams(Equipment_T equipment)
			throws ProcessingFailureException {
		String holder = handler.getValueByName(equipment.name,
				CorbaConstants.EQUIPMENT_HOLDER_STR);
		int slotNumberStart = holder.indexOf("slot=") + 5;
		int slotNumberEnd = holder.lastIndexOf("/sub_slot=");
		if (slotNumberEnd == -1) {
			slotNumberEnd = holder.length();
		}

		Corba2XMLContainer container = new Corba2XMLContainer(
				Corba2XMLStructure.CARDS);
		container.setFieldValue(CorbaConstants.NE_ID_STR, handler
				.getValueByName(equipment.name,
						CorbaConstants.MANAGED_ELEMENT_STR));
		container.setFieldValue(CorbaConstants.HOLDER_STR, holder);
		container.setFieldValue(CorbaConstants.SLOT_N_STR,
				holder.substring(slotNumberStart, slotNumberEnd));
		container.setFieldValue(CorbaConstants.USER_LABEL_STR,
				equipment.userLabel);
		container.setFieldValue(CorbaConstants.NATIVE_EMS_NAME_STR,
				equipment.nativeEMSName);
		container.setFieldValue(CorbaConstants.OWNER_STR, equipment.owner);
		container.setFieldValue(CorbaConstants.ALARM_REPORT_INDIC_STR,
				String.valueOf(equipment.alarmReportingIndicator));
		container.setFieldValue(CorbaConstants.SERVICE_STATE_STR,
				String.valueOf(equipment.serviceState.value()));
		container.setFieldValue(CorbaConstants.EXP_EQUIP_OBJ_TYPE_STR,
				equipment.expectedEquipmentObjectType);
		container.setFieldValue(CorbaConstants.INST_EQUIP_OBJ_TYPE_STR,
				equipment.installedEquipmentObjectType);
		container.setFieldValue(CorbaConstants.INST_PART_NUMBER_STR,
				equipment.installedPartNumber);
		container.setFieldValue(CorbaConstants.INST_VERSION_STR,
				equipment.installedVersion);
		container.setFieldValue(CorbaConstants.INST_SERIAL_NUMBER_STR,
				equipment.installedSerialNumber);
		container.setFieldValue(CorbaConstants.ADDITIONAL_INFO_STR, handler
				.convertNameAndStringValueToString(equipment.additionalInfo));

		return container;
	}

	public Corba2XMLContainer getHolderParams(EquipmentHolder_T eqh)
			throws ProcessingFailureException {
		String state;
		switch (eqh.holderState.value()) {
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

		Corba2XMLContainer container = new Corba2XMLContainer(
				Corba2XMLStructure.HOLDERS);
		container.setFieldValue(CorbaConstants.NE_ID_STR, handler
				.getValueByName(eqh.name, CorbaConstants.MANAGED_ELEMENT_STR));
		container.setFieldValue("HOLDER", handler.getValueByName(eqh.name,
				CorbaConstants.EQUIPMENT_HOLDER_STR));
		container.setFieldValue(CorbaConstants.USER_LABEL_STR, eqh.userLabel);
		container.setFieldValue(CorbaConstants.NATIVE_EMS_NAME_STR,
				eqh.nativeEMSName);
		container.setFieldValue(CorbaConstants.OWNER_STR, eqh.owner);
		container.setFieldValue("ALARM_REPORT_INDIC",
				String.valueOf(eqh.alarmReportingIndicator));
		container.setFieldValue(CorbaConstants.HOLDER_TYPE_STR, eqh.holderType);
		container
				.setFieldValue(
						CorbaConstants.EXP_INST_EQUIPMENT_STR,
						handler.convertNameAndStringValueToString(eqh.expectedOrInstalledEquipment));
		container.setFieldValue(CorbaConstants.ACCEPT_EQUIPMENT_STR,
				handler.parseStringArray(eqh.acceptableEquipmentTypeList));
		container.setFieldValue(CorbaConstants.STATE_STR, state);
		container.setFieldValue(CorbaConstants.ADDITIONAL_INFO_STR,
				handler.convertNameAndStringValueToString(eqh.additionalInfo));
		return container;
	}

	public void printTerminationPoint(TerminationPoint_T terminationPoint)
			throws ProcessingFailureException, SAXException {
		handler.printStructure(getTerminationPointParams(terminationPoint));
	}

	public Corba2XMLContainer getTerminationPointParams(
			TerminationPoint_T terminationPoint)
			throws ProcessingFailureException {

		String type = null;
		switch (terminationPoint.type.value()) {
		case TPType_T._TPT_CTP:
			type = "TPT_CTP";
			break;
		case TPType_T._TPT_PTP:
			type = "TPT_PTP";
			break;
		case TPType_T._TPT_TPPool:
			type = "TPT_TPPool";
			break;
		default:
			type = "";
			break;
		}

		String connectionState = null;
		switch (terminationPoint.connectionState.value()) {
		case TPConnectionState_T._TPCS_NA:
			connectionState = "TPCS_NA";
			break;
		case TPConnectionState_T._TPCS_BI_CONNECTED:
			connectionState = "TPCS_BI_CONNECTED";
			break;
		case TPConnectionState_T._TPCS_NOT_CONNECTED:
			connectionState = "TPCS_NOT_CONNECTED";
			break;
		case TPConnectionState_T._TPCS_SINK_CONNECTED:
			connectionState = "TPCS_SINK_CONNECTED";
			break;
		case TPConnectionState_T._TPCS_SOURCE_CONNECTED:
			connectionState = "TPCS_SOURCE_CONNECTED";
			break;
		default:
			connectionState = "";
			break;
		}

		String tpMappingMode = null;
		switch (terminationPoint.tpMappingMode.value()) {
		case TerminationMode_T._TM_NA:
			tpMappingMode = "TM_NA";
			break;
		case TerminationMode_T._TM_NEITHER_TERMINATED_NOR_AVAILABLE_FOR_MAPPING:
			tpMappingMode = "TM_NEITHER_TERMINATED_NOR_AVAILABLE_FOR_MAPPING";
			break;
		case TerminationMode_T._TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING:
			tpMappingMode = "TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING";
			break;
		default:
			tpMappingMode = "";
			break;
		}

		String direction = null;
		switch (terminationPoint.direction.value()) {
		case Directionality_T._D_BIDIRECTIONAL:
			direction = "BIDIRECTIONAL";
			break;
		case Directionality_T._D_NA:
			direction = "NA";
			break;
		case Directionality_T._D_SINK:
			direction = "SINK";
			break;
		case Directionality_T._D_SOURCE:
			direction = "SOURCE";
			break;
		default:
			direction = "";
			break;
		}

		String tpProtectionAssociation = null;
		if (terminationPoint.tpProtectionAssociation.value() == TPProtectionAssociation_T._TPPA_PSR_RELATED) {
			tpProtectionAssociation = "TPPA_PSR_RELATED";
		} else if (terminationPoint.tpProtectionAssociation.value() == TPProtectionAssociation_T._TPPA_NA) {
			tpProtectionAssociation = "TPPA_NA";
		}

		boolean isPTPExists = false;
		boolean isFTPExists = false;
		boolean isCTPExists = false;

		String ptpValue = null;
		String ftpValue = null;
		String ctpValue = null;

		for (NameAndStringValue_T eachNameAndStringValue : terminationPoint.name) {
			if (eachNameAndStringValue != null
					&& CorbaConstants.CTP_STR
							.equals(eachNameAndStringValue.name)) {
				isCTPExists = true;
				ctpValue = eachNameAndStringValue.value;
			}
			if (eachNameAndStringValue != null
					&& CorbaConstants.FTP_STR
							.equals(eachNameAndStringValue.name)) {
				isFTPExists = true;
				ftpValue = eachNameAndStringValue.value;
			}
			if (eachNameAndStringValue != null
					&& CorbaConstants.PTP_STR
							.equals(eachNameAndStringValue.name)) {
				isPTPExists = true;
				ptpValue = eachNameAndStringValue.value;
			}
		}

		Corba2XMLContainer container = new Corba2XMLContainer(
				Corba2XMLStructure.PTPS);
		container.setFieldValue(CorbaConstants.NE_ID_STR, handler
				.getValueByName(terminationPoint.name,
						CorbaConstants.MANAGED_ELEMENT_STR));
		container.setFieldValue(CorbaConstants.USER_LABEL_STR,
				terminationPoint.userLabel);
		container.setFieldValue(CorbaConstants.NATIVE_EMS_NAME_STR,
				terminationPoint.nativeEMSName);
		container.setFieldValue(CorbaConstants.OWNER_STR,
				terminationPoint.owner);
		container
				.setFieldValue(
						CorbaConstants.IN_TRAFFIC_DES_NAME_STR,
						handler.convertNameAndStringValueToString(terminationPoint.ingressTrafficDescriptorName));
		container
				.setFieldValue(
						CorbaConstants.EG_TRAFFIC_DES_NAME_STR,
						handler.convertNameAndStringValueToString(terminationPoint.egressTrafficDescriptorName));
		if (isFTPExists) {
			container.setFieldValue(CorbaConstants.FTP_STR, ftpValue);
		}

		if (isPTPExists) {
			container.setFieldValue(CorbaConstants.PTP_STR, ptpValue);
		}

		if (isCTPExists) {
			container.setFieldValue(CorbaConstants.CTP_STR, ctpValue);
		}

		container.setFieldValue(CorbaConstants.TYPE_STR, type);
		container.setFieldValue(CorbaConstants.CONNECTION_STATE_STR,
				connectionState);
		container.setFieldValue(CorbaConstants.TP_MAPPING_MODE_STR,
				tpMappingMode);
		container.setFieldValue(CorbaConstants.DIRECTION_STR, direction);
		container
				.setFieldValue(
						CorbaConstants.TRANSMISSION_PARAMS_STR,
						handler.convertLayeredParametersToString(terminationPoint.transmissionParams));
		container.setFieldValue(CorbaConstants.TP_PROTECTION_ASSOCIATION_STR,
				tpProtectionAssociation);
		container.setFieldValue(CorbaConstants.EDGE_POINT_STR,
				terminationPoint.edgePoint ? "true" : "false");
		container
				.setFieldValue(
						CorbaConstants.ADDITIONAL_INFO_STR,
						handler.convertNameAndStringValueToString(terminationPoint.additionalInfo));
		return container;
	}

	public Corba2XMLContainer getSubnetworkConnectionParams(
			SubnetworkConnection_T snc) throws ProcessingFailureException {
		Corba2XMLContainer container = new Corba2XMLContainer(
				Corba2XMLStructure.SNCS);
		container.setFieldValue(CorbaConstants.SNS_ID_STR, handler
				.getValueByName(snc.name,
						CorbaConstants.SUBNETWORK_CONNECTION_STR));
		container.setFieldValue(CorbaConstants.USER_LABEL_STR, snc.userLabel);
		container.setFieldValue(CorbaConstants.NATIVE_EMS_NAME_STR,
				snc.nativeEMSName);
		container.setFieldValue(CorbaConstants.OWNER_STR, snc.owner);
		container.setFieldValue(CorbaConstants.SNC_STATE_STR,
				String.valueOf(snc.sncState.value()));
		container.setFieldValue(CorbaConstants.DIRECTION_STR,
				String.valueOf(snc.direction.value()));
		container.setFieldValue(CorbaConstants.RATE_STR,
				String.valueOf(snc.rate));
		container.setFieldValue(CorbaConstants.STATIC_PROTECTION_LEVEL_STR,
				String.valueOf(snc.staticProtectionLevel.value()));
		container.setFieldValue(CorbaConstants.SNC_TYPE_STR,
				String.valueOf(snc.sncType.value()));

		if (snc.aEnd.length > 0) {
			container.setFieldValue(CorbaConstants.A1_TPNAME_NE_STR, handler
					.getValueByName(snc.aEnd[0].tpName,
							CorbaConstants.MANAGED_ELEMENT_STR));
			container
					.setFieldValue(CorbaConstants.A1_TPNAME_PTP_STR, handler
							.getValueByName(snc.aEnd[0].tpName,
									CorbaConstants.PTP_STR));
			container
					.setFieldValue(CorbaConstants.A1_TPNAME_CTP_STR, handler
							.getValueByName(snc.aEnd[0].tpName,
									CorbaConstants.CTP_STR));
			container.setFieldValue(CorbaConstants.A1_TPMAPPING_MODE_STR,
					String.valueOf(snc.aEnd[0].tpMappingMode.value()));

			if (snc.aEnd.length > 1) {
				container.setFieldValue(CorbaConstants.A2_TPNAME_NE_STR,
						handler.getValueByName(snc.aEnd[1].tpName,
								CorbaConstants.MANAGED_ELEMENT_STR));
				container.setFieldValue(CorbaConstants.A2_TPNAME_PTP_STR,
						handler.getValueByName(snc.aEnd[1].tpName,
								CorbaConstants.PTP_STR));
				container.setFieldValue(CorbaConstants.A2_TPNAME_CTP_STR,
						handler.getValueByName(snc.aEnd[1].tpName,
								CorbaConstants.CTP_STR));
				container.setFieldValue(CorbaConstants.A2_TPMAPPING_MODE_STR,
						String.valueOf(snc.aEnd[1].tpMappingMode.value()));
			}
		}

		if (snc.zEnd.length > 0) {
			container.setFieldValue(CorbaConstants.Z1_TPNAME_NE_STR, handler
					.getValueByName(snc.zEnd[0].tpName,
							CorbaConstants.MANAGED_ELEMENT_STR));
			container
					.setFieldValue(CorbaConstants.Z1_TPNAME_PTP_STR, handler
							.getValueByName(snc.zEnd[0].tpName,
									CorbaConstants.PTP_STR));
			container
					.setFieldValue(CorbaConstants.Z1_TPNAME_CTP_STR, handler
							.getValueByName(snc.zEnd[0].tpName,
									CorbaConstants.CTP_STR));
			container.setFieldValue(CorbaConstants.Z1_TPMAPPING_MODE_STR,
					String.valueOf(snc.zEnd[0].tpMappingMode.value()));

			if (snc.zEnd.length > 1) {
				container.setFieldValue(CorbaConstants.Z2_TPNAME_NE_STR,
						handler.getValueByName(snc.zEnd[1].tpName,
								CorbaConstants.MANAGED_ELEMENT_STR));
				container.setFieldValue(CorbaConstants.Z2_TPNAME_PTP_STR,
						handler.getValueByName(snc.zEnd[1].tpName,
								CorbaConstants.PTP_STR));
				container.setFieldValue(CorbaConstants.Z2_TPNAME_CTP_STR,
						handler.getValueByName(snc.zEnd[1].tpName,
								CorbaConstants.CTP_STR));
				container.setFieldValue(CorbaConstants.Z2_TPMAPPING_MODE_STR,
						String.valueOf(snc.zEnd[1].tpMappingMode.value()));
			}
		}

		container.setFieldValue(CorbaConstants.REROUTEALLOWED_STR,
				String.valueOf(snc.rerouteAllowed.value()));
		container.setFieldValue(CorbaConstants.NETWORKREROUTED_STR,
				String.valueOf(snc.networkRouted.value()));
		container.setFieldValue(CorbaConstants.ADDITIONAL_INFO_STR,
				handler.convertNameAndStringValueToString(snc.additionalInfo));

		return container;
	}

}
