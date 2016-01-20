package ex.corba;

public class CorbaConstants {

	// Common Constants used in NameAndStringValue_T
	public static final String EMS_STR = "EMS";
	public static final String MANAGED_ELEMENT_STR = "ManagedElement";
	public static final String MULTILAYER_SUBNETWORK_STR = "MultiLayerSubnetwork";
	public static final String SUBNETWORK_CONNECTION_STR = "SubnetworkConnection";
	public static final String UNKNOWN_STR = "UNKNOWN";

	// ManagedElement_T
	public static final String NE_ID_STR = "NE_ID";
	public static final String USER_LABEL_STR = "USER_LABEL";
	public static final String NATIVE_EMS_NAME_STR = "NATIVE_EMS_NAME";
	public static final String NE_NAME_STR = "NE_NAME";
	public static final String OWNER_STR = "OWNER";
	public static final String LOCATION_STR = "LOCATION";
	public static final String VERSION_STR = "VERSION";
	public static final String PRODUCT_NAME_STR = "PRODUCT_NAME";
	public static final String COMMUNICATION_STATE_STR = "COMMUNICATION_STATE";
	public static final String EMS_INSYNC_STATE_STR = "EMS_INSYNC_STATE";
	public static final String SUPPORTED_RATES_STR = "SUPPORTED_RATES";
	public static final String GATEWAYS_STR = "GATEWAYS";
	public static final String SOURCE_TIME_STAMP_STR = "SOURCE_TIME_STAMP";

	// Equipment_T
	public static final String EQUIPMENT_HOLDER_STR = "EquipmentHolder";
	public static final String HOLDER_STR = "HOLDER";
	public static final String ALARM_REPORT_INDIC_STR = "ALARM_REPORT_INDIC";
	public static final String SLOT_N_STR = "SLOT_N";
	public static final String SERVICE_STATE_STR = "SERVICE_STATE";
	public static final String EXP_EQUIP_OBJ_TYPE_STR = "EXP_EQUIP_OBJ_TYPE";
	public static final String INST_EQUIP_OBJ_TYPE_STR = "INST_EQUIP_OBJ_TYPE";
	public static final String INST_PART_NUMBER_STR = "INST_PART_NUMBER";
	public static final String INST_VERSION_STR = "INST_VERSION";
	public static final String INST_SERIAL_NUMBER_STR = "INST_SERIAL_NUMBER";
	public static final String ADDITIONAL_INFO_STR = "ADDITIONAL_INFO";

	// EquipmentHolder_T
	public static final String HOLDER_TYPE_STR = "HOLDER_TYPE";
	public static final String EXP_INST_EQUIPMENT_STR = "EXP_INST_EQUIPMENT";
	public static final String ACCEPT_EQUIPMENT_STR = "ACCEPT_EQUIPMENT";
	public static final String STATE_STR = "STATE";

	// TerminationPoint_T
	public static final String IN_TRAFFIC_DES_NAME_STR = "IN_TRAFFIC_DES_NAME";
	public static final String EG_TRAFFIC_DES_NAME_STR = "EG_TRAFFIC_DES_NAME";
	public static final String DIRECTION_STR = "DIRECTION";
	public static final String PTP_STR = "PTP";
	public static final String FTP_STR = "FTP";
	public static final String CTP_STR = "CTP";
	public static final String TYPE_STR = "TYPE";
	public static final String CONNECTION_STATE_STR = "CONNECTION_STATE";
	public static final String TP_MAPPING_MODE_STR = "TP_MAPPING_MODE";
	public static final String TRANSMISSION_PARAMS_STR = "TRANSMISSION_PARAMS";
	public static final String TP_PROTECTION_ASSOCIATION_STR = "TP_PROTECTION_ASSOCIATION";
	public static final String EDGE_POINT_STR = "EDGE_POINT";
	public static final String PTPS_STR = "PTPS";

	// GTP_T
	public static final String GTP_STR = "GTP";
	public static final String TP_STR = "TP";

	// TopologicalLink_T
	public static final String TL_ID_STR = "TL_ID";
	public static final String RATE_STR = "RATE";
	public static final String A_END_NE_STR = "A_END_NE";
	public static final String A_END_TP_STR = "A_END_TP";
	public static final String Z_END_TP_STR = "Z_END_TP";
	public static final String Z_END_NE_STR = "Z_END_NE";
	public static final String A_TRANSMISSION_PARAMS_STR = "A_TRANSMISSION_PARAMS";
	public static final String Z_TRANSMISSION_PARAMS_STR = "Z_TRANSMISSION_PARAMS";

	// SubnetworkConnection_T
	public static final String SNC_ID_STR = "SNC_ID";
	public static final String SNC_STATE_STR = "SNC_STATE";
	public static final String STATIC_PROTECTION_LEVEL_STR = "STATIC_PROTECTION_LEVEL";
	public static final String SNC_TYPE_STR = "SNC_TYPE";
	public static final String A1_TPNAME_NE_STR = "A1_TPNAME_NE";
	public static final String A1_TPNAME_PTP_STR = "A1_TPNAME_PTP";
	public static final String A1_TPNAME_CTP_STR = "A1_TPNAME_CTP";
	public static final String A1_TPMAPPING_MODE_STR = "A1_TPMAPPING_MODE";
	public static final String A2_TPNAME_NE_STR = "A2_TPNAME_NE";
	public static final String A2_TPNAME_PTP_STR = "A2_TPNAME_PTP";
	public static final String A2_TPNAME_CTP_STR = "A2_TPNAME_CTP";
	public static final String A2_TPMAPPING_MODE_STR = "A2_TPMAPPING_MODE";
	public static final String Z1_TPNAME_NE_STR = "Z1_TPNAME_NE";
	public static final String Z1_TPNAME_PTP_STR = "Z1_TPNAME_PTP";
	public static final String Z1_TPNAME_CTP_STR = "Z1_TPNAME_CTP";
	public static final String Z1_TPMAPPING_MODE_STR = "Z1_TPMAPPING_MODE";
	public static final String Z2_TPNAME_NE_STR = "Z2_TPNAME_NE";
	public static final String Z2_TPNAME_PTP_STR = "Z2_TPNAME_PTP";
	public static final String Z2_TPNAME_CTP_STR = "Z2_TPNAME_CTP";
	public static final String Z2_TPMAPPING_MODE_STR = "Z2_TPMAPPING_MODE";
	public static final String REROUTE_ALLOWED_STR = "REROUTE_ALLOWED";
	public static final String NETWORK_REROUTED_STR = "NETWORK_REROUTED";
	public static final String FREQ_A_STR = "FREQ_A";
	public static final String FREQ_Z_STR = "FREQ_Z";

	// CrossConnect_T
	public static final String ACTIVE_STR = "ACTIVE";
	public static final String CC_TYPE_STR = "CC_TYPE";
	public static final String AI_DIRECTION_STR = "AI_DIRECTION";
	public static final String PRT_ROLE_STR = "PRT_ROLE";
	public static final String A1_NE_STR = "A1_NE";
	public static final String A1_PTP_STR = "A1_PTP";
	public static final String A1_CTP_STR = "A1_CTP";
	public static final String A2_NE_STR = "A2_NE";
	public static final String A2_PTP_STR = "A2_PTP";
	public static final String A2_CTP_STR = "A2_CTP";
	public static final String Z1_NE_STR = "Z1_NE";
	public static final String Z1_PTP_STR = "Z1_PTP";
	public static final String Z1_CTP_STR = "Z1_CTP";
	public static final String Z2_NE_STR = "Z2_NE";
	public static final String Z2_PTP_STR = "Z2_PTP";
	public static final String Z2_CTP_STR = "Z2_CTP";

	// ProtectionGroup_T
	public static final String PROTECTION_GROUP_TYPE_STR = "PROTECTION_GROUP_TYPE";
	public static final String PROTECTION_SCHEMA_STATE_STR = "PROTECTION_SCHEMA_STATE";
	public static final String REVERSION_MODE_STR = "REVERSION_MODE";
	public static final String PGP_TP_LIST_STR = "PGP_TP_LIST";
	public static final String PGP_PARAMETERS_STR = "PGP_PARAMETERS";

	// HW_MSTPEndPoint_T
	public static final String NAME_STR = "NAME";

	// HW_RPRNode_T
	public static final String ACTIVE_STATE_STR = "ACTIVE_STATE";
	public static final String SERVICE_TYPE_STR = "SERVICE_TYPE";
	public static final String A_END_PTP_STR = "A_END_PTP";
	public static final String Z_END_PTP_STR = "Z_END_PTP";
	public static final String A_END_VLAN_STR = "A_END_VLAN";
	public static final String Z_END_VLAN_STR = "Z_END_VLAN";
	public static final String A_END_VC_STR = "A_END_VC";
	public static final String Z_END_VC_STR = "Z_END_VC";
	public static final String A_END_TUNEL_STR = "A_END_TUNEL";
	public static final String Z_END_TUNEL_STR = "Z_END_TUNEL";

	// HW_MSTPBindingPath_T
	public static final String VCTRUNK_NE_STR = "VCTRUNK_NE";
	public static final String VCTRUNK_PTP_STR = "VCTRUNK_PTP";
	public static final String ALL_PATH_LIST_STR = "ALL_PATH_LIST";
	public static final String USED_PATH_LIST_STR = "USED_PATH_LIST";

	// FlowDomainFragment_T
	public static final String NETWORK_ACCESS_DOMAIN_STR = "NETWORK_ACCESS_DOMAIN";
	public static final String A_END_NAME_LIST_STR = "A_END_NAME_LIST";
	public static final String Z_END_NAME_LIST_STR = "Z_END_NAME_LIST";
	public static final String ADMINISTRATIVE_STATE_STR = "ADMINISTRATIVE_STATE";
	public static final String FLEXIBLE_STR = "FLEXIBLE";
	public static final String FDFR_TYPE_STR = "FDFR_TYPE";
	public static final String FDFR_STATE_STR = "FDFR_STATE";
	public static final String A_END_STR = "A_END";
	public static final String Z_END_STR = "Z_END";

	// ContainedInUseTPs
	public static final String IN_USE_TPS_STR = "IN_USE_TPS";

	// EquipmentConfiguration
	public static final String EQUIPMENT_TYPE_STR = "EQUIPMENT_TYPE";
	public static final String CONFIG_PARAMETERS = "CONFIG_PARAMS";

	// ContainedPotentialTPs
	public static final String IN_PTP_STR = "IN_PTP";
	public static final String IN_CTP_STR = "IN_CTP";
	public static final String OUT_CTP_STR = "OUT_CTP";
}
