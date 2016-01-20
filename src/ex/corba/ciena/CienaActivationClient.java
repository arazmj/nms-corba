package ex.corba.ciena;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.ciena.oc.callSNC.CallCreateData_T;
import com.ciena.oc.callSNC.CallEnd_T;
import com.ciena.oc.callSNC.CallParameterProfile_T;
import com.ciena.oc.callSNC.Diversity_T;
import com.ciena.oc.callSNC.LevelofEffort_T;
import com.ciena.oc.emsSession.EmsSession_I;
import com.ciena.oc.equipmentManagerCIENA.EquipmentConfigurationData_T;
import com.ciena.oc.globaldefs.ConnectionDirection_T;
import com.ciena.oc.globaldefs.NameAndStringValue_T;
import com.ciena.oc.globaldefs.ProcessingFailureException;
import com.ciena.oc.multiLayerSubnetwork.EMSFreedomLevel_T;
import com.ciena.oc.subnetworkConnection.CrossConnect_T;
import com.ciena.oc.subnetworkConnection.GradesOfImpact_T;
import com.ciena.oc.subnetworkConnection.NetworkRouted_T;
import com.ciena.oc.subnetworkConnection.ProtectionEffort_T;
import com.ciena.oc.subnetworkConnection.Reroute_T;
import com.ciena.oc.subnetworkConnection.SNCCreateData_T;
import com.ciena.oc.subnetworkConnection.SNCType_T;
import com.ciena.oc.subnetworkConnection.StaticProtectionLevel_T;
import com.ciena.oc.subnetworkConnection.TPDataList_THolder;
import com.ciena.oc.subnetworkConnection.TPData_T;
import com.ciena.oc.terminationPoint.GTPEffort_T;
import com.ciena.oc.terminationPoint.GTP_THolder;
import com.ciena.oc.terminationPoint.TerminationMode_T;
import com.ciena.oc.transmissionParameters.LayeredParameters_T;

import ex.corba.ciena.transform.sax.Corba2XMLHandler;

public class CienaActivationClient extends CienaConnection {
	public static final Logger LOG = LoggerFactory
			.getLogger(CienaActivationClient.class);

	protected static EmsSession_I emsSession;

	public static void main(String[] args) {
		CienaActivationClient main = new CienaActivationClient();

		try {
			emsSession = main.openEmsSession(args);

			/* ILC Services over SDH link */
			// main.createMultiNodeSDHService();
			// main.createMultiNodeSDHWithNEtpInc();

			/* ILC Services over OTN link */
			// main.createMultiNodeSDHOverOTN();
			// main.createMultiNodeSDHOverOTNWithNEtpInc();

			/* E-Line Service over SDH link */
			// main.createMultiNodeEthOverSDH10GE();
			// main.createMultiNodeEthOverSDH10GEWithNEtpInc();

			// main.createEthOverSDH1GEWithVCG();

			// main.createEthOverSDH2GEWithVCG();
			// main.createEthOverSDH2GEWithVCGWithNEtpInc();
			// main.createEthOverSDH2GEWithGTP();

			/* Call using multiple SNCs */
			// main.createEthOverSDH10GEsubrate();
			main.createEthOverSDH10GEsubrateWithNEtpInc();

			/* E-Line Service over OTN link */
			// main.createMultiNodeEthOverOTN10Gbps();
			// main.createMultiNodeEthOverOTN10GbpsWithNEtpInc();

			/* OTN Service */
			// main.createMultiNodeODU1onOTU1();
			// main.createMultiNodeODU1onOTU2();
			// main.createMultiNodeODU2onOTU2();

			/* SNCP Services */
			// SNCP services are applicable for Multi-node SNC only
			// main.createSNCPSDHService();
			// main.createSNCPSDHWithNEtpInc();
			// main.createSNCPSDHWithNEtpInc2();
			// main.createEthOverSDH10GEWithProtection();
			// main.createEthOverSDH10GEsubrateWithProtection();

			/* GTP based SNCs */
			// main.createGTP();
			// main.createGTP1();
			// main.deleteGTP();
			// main.createMultiNodeSDHServiceWithGTP();

			/* Change Port Group */
			// main.configureEquipmentTSLM12();
			// main.configureEquipmentTSLM48();

			/* Change port Client Type */
			// main.setTPData();

			// main.deactivateAndDeleteSNC("NISA-SDHService-1+0-3");
			// main.releaseCall("NISA-MN-10GEsubrateVCG-1");

			// main.getSNC();
		} catch (ProcessingFailureException pfe) {
			LOG.error("errorReason:" + pfe.errorReason);
			LOG.error("message:" + pfe.getMessage());
			pfe.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession(emsSession);
		}
	}

	public void createMultiNodeSDHService() throws ProcessingFailureException {
		String sncID = "NISA-SDHService-1+0-3";
		String userLabel = "NISA-SDHService-1+0-1";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 16;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// VC4
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=7");

		// VC4_4c
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=12/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=1");

		// VC4_64c
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=10/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts192c_vc4_64c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// VC4
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=7");

		// VC4_4c
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=13/sub_slot=2/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=1");

		// VC4_64c
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=10/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts192c_vc4_64c=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		// additionalInfo.put("SNC_DTL_SET_NAME","M-C-W");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO; // yes for 1+r protection
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createSingleNodeSDHService() throws ProcessingFailureException {
		String userLabel = "NISA-SN-SNC-SDHService-1+0-1";
		String sncID = "NISA-SN-SNC-SDHService-1+0-1";
		String owner = new String("");

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=5/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// SDH Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=5/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		// additionalInfo.put("SNC_DTL_SET_NAME","M-C-W");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	/**
	 * nisa 5_3 E-Line: SNCP (Call with single SNC) Ethernet Over SDH ETH-ETH
	 * 10GE-10GE(sub)
	 * 
	 * @throws ProcessingFailureException
	 */
	public void createSingleNodeEthernetServiceWithVCG()
			throws ProcessingFailureException {
		String callID = "NISA-SingleNodeSNC-EthService-1";
		String sncID = "NISA-SingleNodeSNC-EthService-1";
		String userLabel = "NISA-SingleNodeSNC-EthService-1";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB1-ASON-CN-01");

		// GbE Service
		// VCG port
		aEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=1/port_group=7/port=1-A-1-7");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB1-ASON-CN-01");

		// GbE Service
		// VCG port
		zEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=1/port_group=7/port=1-A-1-7");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "Yes");
		// additionalInfo.put("SNC_SNCP_PEER", "CD03_CD01_PROTECT");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		SNCCreateData_T sncs[] = new SNCCreateData_T[1];
		sncs[0] = createData;

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.from_int(1);
		tps.value[0].tpName = zEnd[0];// zEndTp;//ethPort;

		tps.value[0].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];

		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[4];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		tps.value[0].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "20");
		tps.value[0].transmissionParams[1].transmissionParams[3] = new NameAndStringValue_T(
				"DegradeThreshold", "20");

		// Layer 96
		tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		tps.value[0].transmissionParams[2].layer = 96;
		tps.value[0].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[0].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=7/port=1");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.from_int(1);
		tps.value[1].tpName = aEnd[0];// aEndTp;//ethPort;

		tps.value[1].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];

		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[4];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		tps.value[1].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "20");
		tps.value[1].transmissionParams[1].transmissionParams[3] = new NameAndStringValue_T(
				"DegradeThreshold", "20");

		// Layer 96
		tps.value[1].transmissionParams[2] = new LayeredParameters_T();
		tps.value[1].transmissionParams[2].layer = 96;
		tps.value[1].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[1].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=7/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T();
		ccd.callName[0].name = new String("EMS");
		ccd.callName[0].value = this.realEMSName;

		ccd.callName[1] = new NameAndStringValue_T();
		ccd.callName[1].name = new String("Call");
		ccd.callName[1].value = callID;

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.from_int(1),
				LevelofEffort_T.from_int(1), LevelofEffort_T.from_int(1),
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	public void createEthOverSDH1GEWithVCG() throws ProcessingFailureException {
		String callID = "NISA-MN-EthServiceVCG-1";
		String sncID = "NISA-MN-EthServiceVCG-1";
		String userLabel = "NISA-MN-EthServiceVCG-1";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 10085;
		// short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// 1 GbE Service - VCG port
		aEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=15/port_group=16/port=1-A-9-15");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/PROP_sts21c_sts3c_7nc=1,4,7,10,13,16,19");
		// aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// 1 GbE Service - VCG port
		zEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=15/port_group=16/port=1-A-9-15");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/PROP_sts21c_sts3c_7nc=1,4,7,10,13,16,19");
		// zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 4: HKG/CH-SNG/PD 64S004(TGNIA)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute4TGNIA()[0];
		neTpInclusions[2] = this.getRoute4TGNIA()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		// additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		// additionalInfo.put("SNC_SNCP_PEER", "CD03_CD01_PROTECT");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		SNCCreateData_T sncs[] = new SNCCreateData_T[1];
		sncs[0] = createData;

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		// A End
		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.from_int(1);
		tps.value[0].tpName = aEnd[0];

		tps.value[0].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[4];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		tps.value[0].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "7");
		tps.value[0].transmissionParams[1].transmissionParams[3] = new NameAndStringValue_T(
				"DegradeThreshold", "7");

		// Layer 96
		tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		tps.value[0].transmissionParams[2].layer = 96;
		tps.value[0].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[0].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=15/sub_slot=16/port=1");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.from_int(1);
		tps.value[1].tpName = zEnd[0];

		tps.value[1].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[4];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		tps.value[1].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "7");
		tps.value[1].transmissionParams[1].transmissionParams[3] = new NameAndStringValue_T(
				"DegradeThreshold", "7");

		// Layer 96
		tps.value[1].transmissionParams[2] = new LayeredParameters_T();
		tps.value[1].transmissionParams[2].layer = 96;
		tps.value[1].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[1].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.from_int(1),
				LevelofEffort_T.from_int(1), LevelofEffort_T.from_int(1),
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	public void createEthOverSDH2GEWithVCG() throws ProcessingFailureException {
		String callID = "NISA-MN-10GEsubrateVCG-1";
		String sncID = "NISA-MN-10GEsubrateVCG-1";
		String userLabel = "NISA-MN-10GEsubrateVCG-1";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// 2 GbE Service - VCG port
		aEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=1/port_group=12/port=1-A-1-12");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// 2 GbE Service - VCG port
		zEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=1/port_group=7/port=1-A-1-7");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		// additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		SNCCreateData_T sncs[] = new SNCCreateData_T[1];
		sncs[0] = createData;

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		// A End
		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[0].tpName = aEnd[0];

		tps.value[0].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		// tps.value[0].transmissionParams[1].transmissionParams[2] = new
		// NameAndStringValue_T(
		// "AllocatedNumber", "14");
		// tps.value[0].transmissionParams[1].transmissionParams[3] = new
		// NameAndStringValue_T(
		// "DegradeThreshold", "14");

		// Layer 96
		tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		tps.value[0].transmissionParams[2].layer = 96;
		tps.value[0].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[0].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[1].tpName = zEnd[0];

		tps.value[1].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		// tps.value[1].transmissionParams[1].transmissionParams[2] = new
		// NameAndStringValue_T(
		// "AllocatedNumber", "14");
		// tps.value[1].transmissionParams[1].transmissionParams[3] = new
		// NameAndStringValue_T(
		// "DegradeThreshold", "14");

		// Layer 96
		tps.value[1].transmissionParams[2] = new LayeredParameters_T();
		tps.value[1].transmissionParams[2].layer = 96;
		tps.value[1].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[1].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=7/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_BEST_EFFORT,
				LevelofEffort_T.LE_BEST_EFFORT, LevelofEffort_T.LE_BEST_EFFORT,
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	public void createEthOverSDH2GEWithVCGWithNEtpInc()
			throws ProcessingFailureException {
		String callID = "NISA-MN-10GEsubrateVCG-1";
		String sncID = "NISA-MN-10GEsubrateVCG-1";
		String userLabel = "NISA-MN-10GEsubrateVCG-1";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// 2 GbE Service - VCG port
		aEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=1/port_group=12/port=1-A-1-12");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// 2 GbE Service - VCG port
		zEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=1/port_group=7/port=1-A-1-7");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 4: HKG/CH-SNG/PD 64S004(TGNIA)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		neTpInclusions[1] = this.getRoute4TGNIA()[0];
		neTpInclusions[2] = this.getRoute4TGNIA()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		// additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		// additionalInfo.put("SNC_SNCP_PEER", "CD03_CD01_PROTECT");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		SNCCreateData_T sncs[] = new SNCCreateData_T[1];
		sncs[0] = createData;

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		// A End
		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[0].tpName = aEnd[0];

		tps.value[0].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[4];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		tps.value[0].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "14");
		tps.value[0].transmissionParams[1].transmissionParams[3] = new NameAndStringValue_T(
				"DegradeThreshold", "14");

		// Layer 96
		tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		tps.value[0].transmissionParams[2].layer = 96;
		tps.value[0].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[0].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[1].tpName = zEnd[0];

		tps.value[1].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[4];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		tps.value[1].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "14");
		tps.value[1].transmissionParams[1].transmissionParams[3] = new NameAndStringValue_T(
				"DegradeThreshold", "14");

		// Layer 96
		tps.value[1].transmissionParams[2] = new LayeredParameters_T();
		tps.value[1].transmissionParams[2].layer = 96;
		tps.value[1].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[1].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_BEST_EFFORT,
				LevelofEffort_T.LE_BEST_EFFORT, LevelofEffort_T.LE_BEST_EFFORT,
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	public void createEthOverSDH2GEWithGTP() throws ProcessingFailureException {
		String callID = "NISA-MN-10GEsubrateVCG-2";
		String sncID = "NISA-MN-10GEsubrateVCG-2";
		String userLabel = "NISA-MN-10GEsubrateVCG-2";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][3];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// 2 GbE Service - VCG port
		aEnd[0][2] = new NameAndStringValue_T("GTP", "GTP_1434614160167");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][3];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// 2 GbE Service - VCG port
		zEnd[0][2] = new NameAndStringValue_T("GTP", "GTP_1434614092772");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 4: HKG/CH-SNG/PD 64S004(TGNIA)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		neTpInclusions[1] = this.getRoute4TGNIA()[0];
		neTpInclusions[2] = this.getRoute4TGNIA()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		// additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		// additionalInfo.put("SNC_SNCP_PEER", "CD03_CD01_PROTECT");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		SNCCreateData_T sncs[] = new SNCCreateData_T[1];
		sncs[0] = createData;

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		// A End
		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[0].tpName = aEnd[0];

		tps.value[0].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[4];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		tps.value[0].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "14");
		tps.value[0].transmissionParams[1].transmissionParams[3] = new NameAndStringValue_T(
				"DegradeThreshold", "14");

		// Layer 96
		tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		tps.value[0].transmissionParams[2].layer = 96;
		tps.value[0].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[0].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[1].tpName = zEnd[0];

		tps.value[1].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[4];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		tps.value[1].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "14");
		tps.value[1].transmissionParams[1].transmissionParams[3] = new NameAndStringValue_T(
				"DegradeThreshold", "14");

		// Layer 96
		tps.value[1].transmissionParams[2] = new LayeredParameters_T();
		tps.value[1].transmissionParams[2].layer = 96;
		tps.value[1].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[1].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_BEST_EFFORT,
				LevelofEffort_T.LE_BEST_EFFORT, LevelofEffort_T.LE_BEST_EFFORT,
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	// Call using multiple SNCs
	public void createEthOverSDH10GEsubrate() throws ProcessingFailureException {
		String callID = "NISA-MN-10GEsubrateVCG-1";
		String sncID = "NISA-MN-10GEsubrateVCG-";
		String userLabel = "NISA-MN-10GEsubrateVCG-";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		int startChannel = 1;

		// 1GE=7; 2GE=14; 3GE=20; 4GE=27; 5GE=34; 6GE=41; 7GE=48; 8GE=54; 9GE=61
		int noOfVC4 = 14;

		NameAndStringValue_T[][] aEnd = null;
		NameAndStringValue_T[][] zEnd = null;

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		SNCCreateData_T sncs[] = new SNCCreateData_T[noOfVC4];

		for (int i = 0; i < noOfVC4; i++) {
			// A-End
			aEnd = new NameAndStringValue_T[1][4];
			aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
			aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
					"SNG-PPD-ASON-CN-01");
			// aEnd[0][2] = new NameAndStringValue_T("FTP",
			// "/rack=1/shelf=2/slot=4/port_group=3/port=1-A-4-3");
			aEnd[0][2] = new NameAndStringValue_T("FTP",
					"/rack=1/shelf=2/slot=1/port_group=12/port=1-A-1-12");
			aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4="
					+ startChannel);

			// Z-End
			zEnd = new NameAndStringValue_T[1][4];
			zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
			zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
					"HKG-CH-ASON-CN-01");
			// zEnd[0][2] = new NameAndStringValue_T("FTP",
			// "/rack=1/shelf=2/slot=4/port_group=3/port=1-A-4-3");
			zEnd[0][2] = new NameAndStringValue_T("FTP",
					"/rack=1/shelf=2/slot=1/port_group=7/port=1-A-1-7");
			zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4="
					+ startChannel);

			Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
			additionalInfo.put("SNC_NAME", sncID + (i + 1));
			additionalInfo.put("SNC_PRIORITY", "0");
			additionalInfo.put("SNC_SNIC_ENABLED", "No");
			additionalInfo.put("CallName", callID);

			SNCCreateData_T createData = new SNCCreateData_T();

			createData.aEnd = aEnd;
			createData.zEnd = zEnd;
			createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
			createData.neTpInclusions = neTpInclusions;
			createData.neTpSncExclusions = neTpSncExclusions;
			createData.ccInclusions = ccInclusions;
			createData.forceUniqueness = true;
			createData.fullRoute = false;
			createData.layerRate = layerRate;
			createData.networkRouted = NetworkRouted_T.NR_NO;
			createData.rerouteAllowed = Reroute_T.RR_NO;
			createData.direction = ConnectionDirection_T.CD_BI;
			createData.sncType = SNCType_T.ST_SIMPLE;
			createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
			createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
			createData.owner = owner;
			createData.userLabel = userLabel + (i + 1);

			sncs[i] = createData;
			startChannel += 3;
		}

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		// A End
		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		// tps.value[0].tpName = aEnd[0];
		tps.value[0].tpName = new NameAndStringValue_T[3];
		tps.value[0].tpName[0] = aEnd[0][0];
		tps.value[0].tpName[1] = aEnd[0][1];
		tps.value[0].tpName[2] = aEnd[0][2];

		tps.value[0].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		// tps.value[0].transmissionParams[1].transmissionParams[2] = new
		// NameAndStringValue_T(
		// "AllocatedNumber", String.valueOf(noOfVC4));
		// tps.value[0].transmissionParams[1].transmissionParams[3] = new
		// NameAndStringValue_T(
		// "DegradeThreshold", String.valueOf(noOfVC4));

		// Layer 96
		tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		tps.value[0].transmissionParams[2].layer = 96;
		tps.value[0].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		// tps.value[0].transmissionParams[2].transmissionParams[0] = new
		// NameAndStringValue_T(
		// "TunnelEndPoint", "/rack=1/shelf=2/slot=4/sub_slot=3/port=1");
		tps.value[0].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		// tps.value[1].tpName = zEnd[0];
		tps.value[1].tpName = new NameAndStringValue_T[3];
		tps.value[1].tpName[0] = zEnd[0][0];
		tps.value[1].tpName[1] = zEnd[0][1];
		tps.value[1].tpName[2] = zEnd[0][2];

		tps.value[1].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		// tps.value[1].transmissionParams[1].transmissionParams[2] = new
		// NameAndStringValue_T(
		// "AllocatedNumber", String.valueOf(noOfVC4));
		// tps.value[1].transmissionParams[1].transmissionParams[3] = new
		// NameAndStringValue_T(
		// "DegradeThreshold", String.valueOf(noOfVC4));

		// Layer 96
		tps.value[1].transmissionParams[2] = new LayeredParameters_T();
		tps.value[1].transmissionParams[2].layer = 96;
		tps.value[1].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		// tps.value[1].transmissionParams[2].transmissionParams[0] = new
		// NameAndStringValue_T(
		// "TunnelEndPoint", "/rack=1/shelf=2/slot=4/sub_slot=3/port=1");
		tps.value[1].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=7/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_BEST_EFFORT,
				LevelofEffort_T.LE_BEST_EFFORT, LevelofEffort_T.LE_BEST_EFFORT,
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	// Call using multiple SNCs
	public void createEthOverSDH10GEsubrateWithNEtpInc()
			throws ProcessingFailureException {
		String callID = "NISA-MN-10GEsubrateVCG-1";
		String sncID = "NISA-MN-10GEsubrateVCG-";
		String userLabel = "NISA-MN-10GEsubrateVCG-";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		int startChannel = 1;

		// 1GE=7; 2GE=14; 3GE=20; 4GE=27; 5GE=34; 6GE=41; 7GE=48; 8GE=54; 9GE=61
		int noOfVC4 = 2;

		NameAndStringValue_T[][] aEnd = null;
		NameAndStringValue_T[][] zEnd = null;

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		SNCCreateData_T sncs[] = new SNCCreateData_T[noOfVC4];

		for (int i = 0; i < noOfVC4; i++) {
			// A-End
			aEnd = new NameAndStringValue_T[1][4];
			aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
			aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
					"SNG-PPD-ASON-CN-01");
			// aEnd[0][2] = new NameAndStringValue_T("FTP",
			// "/rack=1/shelf=2/slot=4/port_group=3/port=1-A-4-3");
			aEnd[0][2] = new NameAndStringValue_T("FTP",
					"/rack=1/shelf=2/slot=1/port_group=12/port=1-A-1-12");
			// aEnd[0][2] = new NameAndStringValue_T("FTP",
			// "/rack=1/shelf=2/slot=15/port_group=44/port=1-A-9-43");
			aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4="
					+ startChannel);

			// Z-End
			zEnd = new NameAndStringValue_T[1][4];
			zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
			zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
					"HKG-CH-ASON-CN-01");
			// zEnd[0][2] = new NameAndStringValue_T("FTP",
			// "/rack=1/shelf=2/slot=4/port_group=3/port=1-A-4-3");
			zEnd[0][2] = new NameAndStringValue_T("FTP",
					"/rack=1/shelf=2/slot=1/port_group=12/port=1-A-1-12");
			zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4="
					+ startChannel);

			// NE TP Inclusions
			// Route 4: HKG/CH-SNG/PD 64S004(TGNIA)
			NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

			neTpInclusions[0][0] = aEnd[0][0];
			neTpInclusions[0][1] = aEnd[0][1];
			neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
					"/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

			neTpInclusions[1] = this.getRoute4TGNIA()[0];
			neTpInclusions[2] = this.getRoute4TGNIA()[1];

			neTpInclusions[3][0] = zEnd[0][0];
			neTpInclusions[3][1] = zEnd[0][1];
			neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
					"/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

			Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
			additionalInfo.put("SNC_NAME", sncID + (i + 1));
			additionalInfo.put("SNC_PRIORITY", "0");
			additionalInfo.put("SNC_SNIC_ENABLED", "No");
			additionalInfo.put("CallName", callID);

			SNCCreateData_T createData = new SNCCreateData_T();

			createData.aEnd = aEnd;
			createData.zEnd = zEnd;
			createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
			createData.neTpInclusions = neTpInclusions;
			createData.neTpSncExclusions = neTpSncExclusions;
			createData.ccInclusions = ccInclusions;
			createData.forceUniqueness = true;
			createData.fullRoute = true;
			createData.layerRate = layerRate;
			createData.networkRouted = NetworkRouted_T.NR_NO;
			createData.rerouteAllowed = Reroute_T.RR_NO;
			createData.direction = ConnectionDirection_T.CD_BI;
			createData.sncType = SNCType_T.ST_SIMPLE;
			createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
			createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
			createData.owner = owner;
			createData.userLabel = userLabel + (i + 1);

			sncs[i] = createData;
			startChannel += 3;
		}

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		// A End
		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		// tps.value[0].tpName = aEnd[0];
		tps.value[0].tpName = new NameAndStringValue_T[3];
		tps.value[0].tpName[0] = aEnd[0][0];
		tps.value[0].tpName[1] = aEnd[0][1];
		tps.value[0].tpName[2] = aEnd[0][2];

		tps.value[0].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		// tps.value[0].transmissionParams[1].transmissionParams[2] = new
		// NameAndStringValue_T(
		// "AllocatedNumber", String.valueOf(noOfVC4));
		// tps.value[0].transmissionParams[1].transmissionParams[3] = new
		// NameAndStringValue_T(
		// "DegradeThreshold", String.valueOf(noOfVC4));

		// Layer 96
		tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		tps.value[0].transmissionParams[2].layer = 96;
		tps.value[0].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		// tps.value[0].transmissionParams[2].transmissionParams[0] = new
		// NameAndStringValue_T(
		// "TunnelEndPoint", "/rack=1/shelf=2/slot=4/sub_slot=3/port=1");
		tps.value[0].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		// tps.value[1].tpName = zEnd[0];
		tps.value[1].tpName = new NameAndStringValue_T[3];
		tps.value[1].tpName[0] = zEnd[0][0];
		tps.value[1].tpName[1] = zEnd[0][1];
		tps.value[1].tpName[2] = zEnd[0][2];

		tps.value[1].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Disabled");
		// tps.value[1].transmissionParams[1].transmissionParams[2] = new
		// NameAndStringValue_T(
		// "AllocatedNumber", String.valueOf(noOfVC4));
		// tps.value[1].transmissionParams[1].transmissionParams[3] = new
		// NameAndStringValue_T(
		// "DegradeThreshold", String.valueOf(noOfVC4));

		// Layer 96
		tps.value[1].transmissionParams[2] = new LayeredParameters_T();
		tps.value[1].transmissionParams[2].layer = 96;
		tps.value[1].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		// tps.value[1].transmissionParams[2].transmissionParams[0] = new
		// NameAndStringValue_T(
		// "TunnelEndPoint", "/rack=1/shelf=2/slot=4/sub_slot=3/port=1");
		tps.value[1].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=1/sub_slot=12/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_BEST_EFFORT,
				LevelofEffort_T.LE_BEST_EFFORT, LevelofEffort_T.LE_BEST_EFFORT,
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	public void createMultiNodeEthOverSDH10GE()
			throws ProcessingFailureException {
		String sncID = "NISA-10GbEoverSDHlink-1";
		String userLabel = "NISA-10GbEoverSDHlink-1";
		String owner = new String("");

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 18;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// 10 GbE Service
		// Ethernet port; ClientType=10GbE_SONET_SDH
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");

		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=6/port=1");
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=9/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/sts192c_vc4_64c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// 10 GbE Service
		// Ethernet port; ClientType=10GbE_SONET_SDH
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");

		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=1/port=1");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=12/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/sts192c_vc4_64c=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.layerRate = layerRate;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		// The clientType parameter can be used to configure a termination point
		// to process a particular type of client signal. When this parameter is
		// set by an NMS (using the setTPData operation or through the
		// tpsToModify parameter in a createAndActivateSNC operation) it may
		// change the format (i.e. the layers modeled) of the termination point,
		// these changes will be notified to the NMS via an attribute value
		// change notification against the termination point transmissionParams
		// attribute.

		// TPs to Modify
		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[2];
		tpsToModify.value[0] = new TPData_T();
		tpsToModify.value[1] = new TPData_T();

		// A End
		tpsToModify.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[0].tpName = aEnd[0]; // ethPort;

		tpsToModify.value[0].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Frame Mode
		tpsToModify.value[0].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[0].transmissionParams[0].layer = 98;

		tpsToModify.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tpsToModify.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");
		tpsToModify.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");

		// Z End
		tpsToModify.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[1].tpName = zEnd[0]; // ethPort;

		tpsToModify.value[1].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Frame Mode
		tpsToModify.value[1].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[1].transmissionParams[0].layer = 98;

		tpsToModify.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tpsToModify.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");
		tpsToModify.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	/**
	 * Ethernet over OTN. While creating tunnel mapping type/frame mode needs to
	 * be specified as part of tpsToModify in createAndActivateSNC. Mapping type
	 * is applicable for SONET/SDH over OTN tunnel and frame mode for Ethernet
	 * over OTN tunnel.
	 * 
	 * 
	 * @throws ProcessingFailureException
	 */
	public void createMultiNodeEthOverOTN10Gbps()
			throws ProcessingFailureException {
		String sncID = "NISA-MN-SNC-EthernetoverOTN-1";
		String userLabel = "NISA-MN-SNC-EthernetoverOTN-1";
		String owner = new String("");

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 105; // 10G

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB1-ASON-CN-01");

		// 10G CBR Over OTN; STM64 over ODU2
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/odu2=1;tp=1");

		// 2.5G CBR Over OTN; STM16 over ODU1
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/odu1=1;tp=1");

		// Ethernet Over OTN; 10G over ODU2
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/odu2=1;tp=1");

		// Ethernet Over OTN; 1G over ODU0
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/PROP_odu0=1;tp=1");

		// OTN Service
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/odu1=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB2-ASON-CN-01");

		// 10G CBR Over OTN; STM64 over ODU2
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/odu2=1;tp=1");

		// 2.5G CBR Over OTN; STM16 over ODU1
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/odu1=1;tp=1");

		// Ethernet Over OTN; 10G over ODU2
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/odu2=1;tp=1");

		// Ethernet Over OTN; 1G over ODU0
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/PROP_odu0=1;tp=1");

		// OTN Service
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/odu1=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		// Required for OTN services
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");

		// additionalInfo.put("SNC_PREEMPTING", "No");
		// additionalInfo.put("SNC_PREEMPTABILITY", "No");
		// additionalInfo.put("SNC_UNPROTECTED_LINES", "Yes");
		// additionalInfo.put("SNC_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_LINEAR_APS_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_LINEAR_VLSR_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_MATCH_TIME_SLOTS", "Yes");
		// additionalInfo.put("SNC_MAX_ADMIN_WEIGHT", "11300");
		// additionalInfo.put("SNC_PERMANENT", "No");
		// additionalInfo.put("SNC_RETAIN_HOME_PATH", "Disabled");
		// additionalInfo.put("SNC_HOME_PATH_PREEMPTABILITY", "Disabled");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.layerRate = layerRate;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		// TPs to Modify
		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[2];
		tpsToModify.value[0] = new TPData_T();
		tpsToModify.value[1] = new TPData_T();

		// A End
		tpsToModify.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[0].tpName = aEnd[0]; // ethPort;

		tpsToModify.value[0].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Frame Mode
		tpsToModify.value[0].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[0].transmissionParams[0].layer = 98;

		tpsToModify.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tpsToModify.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_ORDERED_SETS");
		tpsToModify.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Z End
		tpsToModify.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[1].tpName = zEnd[0]; // ethPort;

		tpsToModify.value[1].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Frame Mode
		tpsToModify.value[1].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[1].transmissionParams[0].layer = 98;

		tpsToModify.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tpsToModify.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_ORDERED_SETS");
		tpsToModify.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	/**
	 * Transparent SDH: Transparent SDH is mapping STM16/64 as client signal
	 * into ODU1/ODU2 over OTN link.
	 * 
	 * This is SDH over OTN. i.e. STM frames are mapped to ODU. While creating
	 * tunnel mapping type/frame mode needs to be specified as part of
	 * tpsToModify in createAndActivateSNC. Mapping type is applicable for
	 * SONET/SDH over OTN tunnel and frame mode for Ethernet over OTN tunnel.
	 * 
	 * 
	 * @throws ProcessingFailureException
	 */
	public void createMultiNodeSDHOverOTN() throws ProcessingFailureException {
		String sncID = "NISA-MN-SDHoverOTN-1";
		String userLabel = "NISA-MN-SDHoverOTN-1";
		String owner = new String("");

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 104;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB1-ASON-CN-01");

		// 2.5G CBR Over OTN; STM16 over ODU1
		// STM16 port; Client Type = OC48_STM16_CBR
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/odu1=1;tp=1");

		// 10G CBR Over OTN; STM64 over ODU2
		// STM64 port; Client Type = OC192_STM64_CBR
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/odu2=1;tp=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB2-ASON-CN-01");

		// 2.5G CBR Over OTN; STM16 over ODU1
		// STM16 port; Client Type = OC48_STM16_CBR
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/odu1=1;tp=1");

		// 10G CBR Over OTN; STM64 over ODU2
		// STM64 port; Client Type = OC192_STM64_CBR
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/odu2=1;tp=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		// Required for OTN services
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");

		// additionalInfo.put("SNC_PREEMPTING", "No");
		// additionalInfo.put("SNC_PREEMPTABILITY", "No");
		// additionalInfo.put("SNC_UNPROTECTED_LINES", "Yes");
		// additionalInfo.put("SNC_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_LINEAR_APS_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_LINEAR_VLSR_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_MATCH_TIME_SLOTS", "Yes");
		// additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");
		// additionalInfo.put("SNC_MAX_ADMIN_WEIGHT", "11300");
		// additionalInfo.put("SNC_PERMANENT", "No");
		// additionalInfo.put("SNC_RETAIN_HOME_PATH", "Disabled");
		// additionalInfo.put("SNC_HOME_PATH_PREEMPTABILITY", "Disabled");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.layerRate = layerRate;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		// TPs to Modify
		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[2];
		tpsToModify.value[0] = new TPData_T();
		tpsToModify.value[1] = new TPData_T();

		// A End
		tpsToModify.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[0].tpName = aEnd[0];

		tpsToModify.value[0].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Mapping Type
		// Mapping type is applicable for SONET/SDH over OTN tunnel
		tpsToModify.value[0].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[0].transmissionParams[0].layer = 98;

		tpsToModify.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[1];
		tpsToModify.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"Mapping", "ASYNC_CBR");

		// Z End
		tpsToModify.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[1].tpName = zEnd[0];

		tpsToModify.value[1].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Mapping Type
		tpsToModify.value[1].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[1].transmissionParams[0].layer = 98;

		tpsToModify.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[1];
		tpsToModify.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"Mapping", "ASYNC_CBR");

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createSNCPSDHService() throws ProcessingFailureException {
		String callID = "NISA-SDHService-1";
		String sncID = "NISA-SDHService-1";
		String userLabel = "NISA-SDHService-1";
		String owner = new String("");

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 18;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=15/sub_slot=46/port=1");
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=15/sub_slot=10/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=10/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts192c_vc4_64c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=15/sub_slot=10/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=10/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts192c_vc4_64c=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID + " W");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_SNCP_PEER", sncID + " P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel + " W";

		// Protected SNC
		NameAndStringValue_T[][] neTpInclusions2 = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions2 = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions2 = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo2 = new Hashtable<String, String>();
		additionalInfo2.put("SNC_NAME", sncID + " P");
		additionalInfo2.put("SNC_PRIORITY", "0");
		additionalInfo2.put("SNC_SNIC_ENABLED", "No");
		additionalInfo2.put("CallName", callID);
		additionalInfo2.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo2.put("SNC_SNCP_PEER", sncID + " W");

		SNCCreateData_T createData2 = new SNCCreateData_T();

		createData2.aEnd = aEnd;
		createData2.zEnd = zEnd;
		createData2.additionalCreationInfo = getNameAndStringValues(additionalInfo2);
		createData2.neTpInclusions = neTpInclusions2;
		createData2.neTpSncExclusions = neTpSncExclusions2;
		createData2.ccInclusions = ccInclusions2;
		createData2.forceUniqueness = true;
		createData2.fullRoute = false;
		createData2.layerRate = layerRate;
		createData2.networkRouted = NetworkRouted_T.NR_NO;
		createData2.rerouteAllowed = Reroute_T.RR_NO;
		createData2.direction = ConnectionDirection_T.CD_BI;
		createData2.sncType = SNCType_T.ST_SIMPLE;
		createData2.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData2.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData2.owner = owner;
		createData2.userLabel = userLabel + " P";

		SNCCreateData_T sncs[] = new SNCCreateData_T[2];
		sncs[0] = createData;
		sncs[1] = createData2;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		// ccd.additionalCreationInfo[0] = new NameAndStringValue_T(
		// "LM_DIVERSITY", "notRequired");
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_NONE // coroutingLevelofEffort
				, LevelofEffort_T.LE_NONE // nodeDiversityLevelofEffort
				, LevelofEffort_T.LE_NONE // linkDiversityLevelofEffort
				, new NameAndStringValue_T[0] // nodeSRGName
				, new NameAndStringValue_T[0] // linkSRGName
		);

		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tpsToModify);
	}

	public void createSNCPSDHWithNEtpInc() throws ProcessingFailureException {
		String callID = "NISA-MN-SNCP-SDHService-2";
		String sncID = "NISA-MN-SNCP-SDHService-2";
		String userLabel = "NISA-MN-SNCP-SDHService-2";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c; 104 = OTU1
		short layerRate = 17;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=10/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=10/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 4: HKG/CH-SNG/PD 64S004(TGNIA)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute4TGNIA()[0];
		neTpInclusions[2] = this.getRoute4TGNIA()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID + " W");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_SNCP_PEER", sncID + " P");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel + " W";

		// Created protected SNC
		// NameAndStringValue_T[][] neTpInclusions2 = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions2 = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions2 = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 3 (OTU2): HKG/CH-SNG/PD 64S003-OTU2(SJC)
		NameAndStringValue_T[][] neTpInclusions2 = new NameAndStringValue_T[4][3];

		neTpInclusions2[0][0] = aEnd[0][0];
		neTpInclusions2[0][1] = aEnd[0][1];
		neTpInclusions2[0][2] = aEnd[0][2];

		neTpInclusions2[1] = this.getRoute2C2C()[0];
		neTpInclusions2[2] = this.getRoute2C2C()[1];

		neTpInclusions2[3][0] = zEnd[0][0];
		neTpInclusions2[3][1] = zEnd[0][1];
		neTpInclusions2[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo2 = new Hashtable<String, String>();
		additionalInfo2.put("SNC_NAME", sncID + " P");
		additionalInfo2.put("SNC_PRIORITY", "0");
		additionalInfo2.put("SNC_SNIC_ENABLED", "No");
		additionalInfo2.put("CallName", callID);
		additionalInfo2.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo2.put("SNC_SNCP_PEER", sncID + " W");

		SNCCreateData_T createData2 = new SNCCreateData_T();

		createData2.aEnd = aEnd;
		createData2.zEnd = zEnd;
		createData2.additionalCreationInfo = getNameAndStringValues(additionalInfo2);
		createData2.neTpInclusions = neTpInclusions2;
		createData2.neTpSncExclusions = neTpSncExclusions2;
		createData2.ccInclusions = ccInclusions2;
		createData2.forceUniqueness = true;
		createData2.fullRoute = true;
		createData2.layerRate = layerRate;
		createData2.networkRouted = NetworkRouted_T.NR_NO;
		createData2.rerouteAllowed = Reroute_T.RR_NO;
		createData2.direction = ConnectionDirection_T.CD_BI;
		createData2.sncType = SNCType_T.ST_SIMPLE;
		createData2.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData2.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData2.owner = owner;
		createData2.userLabel = userLabel + " P";

		SNCCreateData_T sncs[] = new SNCCreateData_T[2];
		sncs[0] = createData;
		sncs[1] = createData2;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);// null;
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = new String("");
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_NONE // coroutingLevelofEffort
				, LevelofEffort_T.LE_NONE // nodeDiversityLevelofEffort
				, LevelofEffort_T.LE_NONE // linkDiversityLevelofEffort
				, new NameAndStringValue_T[0] // nodeSRGName
				, new NameAndStringValue_T[0] // linkSRGName
		);

		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tpsToModify);
	}

	/*
	 * Working path over SDH link and protection path over OTN link
	 */
	public void createSNCPSDHWithNEtpInc2() throws ProcessingFailureException {
		String callID = "NISA-MN-SNCP-SDHService-3";
		String sncID = "NISA-MN-SNCP-SDHService-3";
		String userLabel = "NISA-MN-SNCP-SDHService-3";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c; 104 = OTU1
		short layerRate = 17;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=10/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=10/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 4 (Transparent SDH): HKG/CH-SNG/PD 64S004(TGNIA)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute4TGNIA()[0];
		neTpInclusions[2] = this.getRoute4TGNIA()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID + " W");
		// additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_SNCP_PEER", sncID + " P");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel + " W";

		// Created protected SNC
		// NameAndStringValue_T[][] neTpInclusions2 = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions2 = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions2 = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 3 (OTU2): HKG/CH-SNG/PD 64S003-OTU2(SJC)
		NameAndStringValue_T[][] neTpInclusions2 = new NameAndStringValue_T[4][3];

		neTpInclusions2[0][0] = aEnd[0][0];
		neTpInclusions2[0][1] = aEnd[0][1];
		neTpInclusions2[0][2] = aEnd[0][2];

		neTpInclusions2[1] = this.getRoute6Extra()[0];
		neTpInclusions2[2] = this.getRoute6Extra()[1];

		neTpInclusions2[3][0] = zEnd[0][0];
		neTpInclusions2[3][1] = zEnd[0][1];
		neTpInclusions2[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo2 = new Hashtable<String, String>();
		additionalInfo2.put("SNC_NAME", sncID + " P");
		// additionalInfo2.put("SNC_PRIORITY", "0");
		additionalInfo2.put("SNC_SNIC_ENABLED", "No");
		additionalInfo2.put("CallName", callID);
		additionalInfo2.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo2.put("SNC_SNCP_PEER", sncID + " W");

		SNCCreateData_T createData2 = new SNCCreateData_T();

		createData2.aEnd = aEnd;
		createData2.zEnd = zEnd;
		createData2.additionalCreationInfo = getNameAndStringValues(additionalInfo2);
		createData2.neTpInclusions = neTpInclusions2;
		createData2.neTpSncExclusions = neTpSncExclusions2;
		createData2.ccInclusions = ccInclusions2;
		createData2.forceUniqueness = true;
		createData2.fullRoute = true;
		createData2.layerRate = layerRate;
		createData2.networkRouted = NetworkRouted_T.NR_NO;
		createData2.rerouteAllowed = Reroute_T.RR_NO;
		createData2.direction = ConnectionDirection_T.CD_BI;
		createData2.sncType = SNCType_T.ST_SIMPLE;
		createData2.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData2.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData2.owner = owner;
		createData2.userLabel = userLabel + " P";

		SNCCreateData_T sncs[] = new SNCCreateData_T[2];
		sncs[0] = createData;
		sncs[1] = createData2;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);// null;
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_NONE // coroutingLevelofEffort
				, LevelofEffort_T.LE_NONE // nodeDiversityLevelofEffort
				, LevelofEffort_T.LE_NONE // linkDiversityLevelofEffort
				, new NameAndStringValue_T[0] // nodeSRGName
				, new NameAndStringValue_T[0] // linkSRGName
		);

		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tpsToModify);
	}

	public void createEthOverSDH10GEWithProtection()
			throws ProcessingFailureException {
		String callID = "NISA-10GE-1";
		String sncID = "NISA-10GE-1";
		String userLabel = "NISA-10GE-1";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 18;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=9/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/sts192c_vc4_64c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=12/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/sts192c_vc4_64c=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID + " W");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_SNCP_PEER", sncID + " P");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel + " W";

		// Create protected SNC
		NameAndStringValue_T[][] neTpInclusions2 = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions2 = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions2 = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo2 = new Hashtable<String, String>();
		additionalInfo2.put("SNC_NAME", sncID + " P");
		additionalInfo2.put("SNC_PRIORITY", "0");
		additionalInfo2.put("SNC_SNIC_ENABLED", "No");
		additionalInfo2.put("CallName", callID);
		additionalInfo2.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo2.put("SNC_SNCP_PEER", sncID + " W");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData2 = new SNCCreateData_T();

		createData2.aEnd = aEnd;
		createData2.zEnd = zEnd;
		createData2.additionalCreationInfo = getNameAndStringValues(additionalInfo2);
		createData2.neTpInclusions = neTpInclusions2;
		createData2.neTpSncExclusions = neTpSncExclusions2;
		createData2.ccInclusions = ccInclusions2;
		createData2.forceUniqueness = true;
		createData2.fullRoute = false;
		createData2.layerRate = layerRate;
		createData2.networkRouted = NetworkRouted_T.NR_NO;
		createData2.rerouteAllowed = Reroute_T.RR_NO;
		createData2.direction = ConnectionDirection_T.CD_BI;
		createData2.sncType = SNCType_T.ST_SIMPLE;
		createData2.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData2.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData2.owner = owner;
		createData2.userLabel = userLabel + " P";

		SNCCreateData_T sncs[] = new SNCCreateData_T[2];
		sncs[0] = createData;
		sncs[1] = createData2;

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		// A End
		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[0].tpName = aEnd[0];

		tps.value[0].transmissionParams = new LayeredParameters_T[1];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[1].tpName = zEnd[0];

		tps.value[1].transmissionParams = new LayeredParameters_T[1];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_NONE,
				LevelofEffort_T.LE_NONE, LevelofEffort_T.LE_NONE,
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	public void createEthOverSDH10GEsubrateWithProtection()
			throws ProcessingFailureException {
		String callID = "NISA-10GEsubrate-2";
		String sncID = "NISA-10GEsubrate-2";
		String userLabel = "NISA-10GEsubrate-2";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// 2 GbE Service - VCG port
		aEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=1/port_group=12/port=1-A-1-12");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// 2 GbE Service - VCG port
		zEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=1/port_group=12/port=1-A-1-12");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID + " W");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_SNCP_PEER", sncID + "P");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel + " W";

		// Created protected SNC
		NameAndStringValue_T[][] neTpInclusions2 = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions2 = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions2 = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo2 = new Hashtable<String, String>();
		additionalInfo2.put("SNC_NAME", sncID + " P");
		additionalInfo2.put("SNC_PRIORITY", "0");
		additionalInfo2.put("SNC_SNIC_ENABLED", "No");
		additionalInfo2.put("CallName", callID);
		additionalInfo2.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo2.put("SNC_SNCP_PEER", sncID + "W");
		// additionalInfo.put("SNC_DTL_SET_NAME", "W+P");

		SNCCreateData_T createData2 = new SNCCreateData_T();

		createData2.aEnd = aEnd;
		createData2.zEnd = zEnd;
		createData2.additionalCreationInfo = getNameAndStringValues(additionalInfo2);
		createData2.neTpInclusions = neTpInclusions2;
		createData2.neTpSncExclusions = neTpSncExclusions2;
		createData2.ccInclusions = ccInclusions2;
		createData2.forceUniqueness = true;
		createData2.fullRoute = true;
		createData2.layerRate = layerRate;
		createData2.networkRouted = NetworkRouted_T.NR_NO;
		createData2.rerouteAllowed = Reroute_T.RR_NO;
		createData2.direction = ConnectionDirection_T.CD_BI;
		createData2.sncType = SNCType_T.ST_SIMPLE;
		createData2.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData2.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData2.owner = owner;
		createData2.userLabel = userLabel + " P";

		SNCCreateData_T sncs[] = new SNCCreateData_T[2];
		sncs[0] = createData;
		sncs[1] = createData2;

		TPDataList_THolder tps = new TPDataList_THolder();
		tps.value = new TPData_T[2];

		// A End
		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[0].tpName = aEnd[0];

		tps.value[0].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[3];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "14");

		// Layer 96
		tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		tps.value[0].transmissionParams[2].layer = 96;
		tps.value[0].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[0].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=4/sub_slot=5/port=1");

		// Z End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tps.value[1].tpName = zEnd[0];

		tps.value[1].transmissionParams = new LayeredParameters_T[3];

		// Layer 98
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");
		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[3];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T(
				"VCAT", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T(
				"DynamicAllocationEnabled", "Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[2] = new NameAndStringValue_T(
				"AllocatedNumber", "14");

		// Layer 96
		tps.value[1].transmissionParams[2] = new LayeredParameters_T();
		tps.value[1].transmissionParams[2].layer = 96;
		tps.value[1].transmissionParams[2].transmissionParams = new NameAndStringValue_T[1];
		tps.value[1].transmissionParams[2].transmissionParams[0] = new NameAndStringValue_T(
				"TunnelEndPoint", "/rack=1/shelf=2/slot=4/sub_slot=5/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ccd.callName[1] = new NameAndStringValue_T("Call", callID);

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = "";
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.LE_NONE,
				LevelofEffort_T.LE_NONE, LevelofEffort_T.LE_NONE,
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.establishCall(ccd, sncs, tps);
	}

	public void createGTP() throws ProcessingFailureException {
		String userLabel = "TestGTP001";
		boolean forceUniqueness = false;
		String owner = ""; // Not used

		String neName = "HKG-CH-ASON-CN-01";
		String port = "/rack=1/shelf=2/slot=4/sub_slot=8/port=1";

		NameAndStringValue_T[][] listOfTPs = new NameAndStringValue_T[3][4];
		listOfTPs[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		listOfTPs[0][1] = new NameAndStringValue_T("ManagedElement", neName);
		listOfTPs[0][2] = new NameAndStringValue_T("PTP", port);
		listOfTPs[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=10");

		listOfTPs[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		listOfTPs[1][1] = new NameAndStringValue_T("ManagedElement", neName);
		listOfTPs[1][2] = new NameAndStringValue_T("PTP", port);
		listOfTPs[1][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=13");

		listOfTPs[2][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		listOfTPs[2][1] = new NameAndStringValue_T("ManagedElement", neName);
		listOfTPs[2][2] = new NameAndStringValue_T("PTP", port);
		listOfTPs[2][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=16");

		// In cases where the CTPs are contiguous and of the same layerRate,
		// this parameter is used to indicate the first CTP in the group.
		// This parameter is used in lieu of the listOfTPs parameter.
		NameAndStringValue_T[] initialCTPname = new NameAndStringValue_T[0];

		// This parameter is used in conjunction with the initialCTPname
		// parameter.
		// It indicates the number of contiguous CTPs that follow the initial
		// CTP.
		int numberOfCTPs = 0;

		// Only EFFORT_SAME is supported by NBI
		GTPEffort_T gtpEffort = GTPEffort_T.EFFORT_SAME;

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		// additionalInfo.put("GTP_NAME", userLabel);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		GTP_THolder theGTP = cmd.createGTP(userLabel, forceUniqueness, owner,
				listOfTPs, initialCTPname, numberOfCTPs, gtpEffort,
				getNameAndStringValues(additionalInfo));

		if (LOG.isInfoEnabled()) {
			LOG.info("GTP Name: "
					+ convertNameAndStringValueToString(theGTP.value.name));
			LOG.info("GTP nativeEMSName: " + theGTP.value.nativeEMSName);
		}
	}

	public void createGTP1() throws ProcessingFailureException {
		String userLabel = "TestGTP001";
		boolean forceUniqueness = false;
		String owner = ""; // Not used

		String neName = "SNG-PPD-ASON-CN-01";
		// String neName = "HKG-CH-ASON-CN-01";

		String port = "/rack=1/shelf=2/slot=1/sub_slot=12/port=1";

		int noOfCTPs = 14;
		int startChannel = 1;

		NameAndStringValue_T[][] listOfTPs = new NameAndStringValue_T[noOfCTPs][4];

		for (int i = 0; i < noOfCTPs; i++) {
			listOfTPs[i][0] = new NameAndStringValue_T("EMS", this.realEMSName);
			listOfTPs[i][1] = new NameAndStringValue_T("ManagedElement", neName);
			listOfTPs[i][2] = new NameAndStringValue_T("PTP", port);
			listOfTPs[i][3] = new NameAndStringValue_T("CTP", "/sts3c_au4="
					+ startChannel);

			startChannel = startChannel + 3;
		}

		// In cases where the CTPs are contiguous and of the same layerRate,
		// this parameter is used to indicate the first CTP in the group.
		// This parameter is used in lieu of the listOfTPs parameter.
		NameAndStringValue_T[] initialCTPname = new NameAndStringValue_T[0];

		// This parameter is used in conjunction with the initialCTPname
		// parameter.
		// It indicates the number of contiguous CTPs that follow the initial
		// CTP.
		int numberOfCTPs = 0;

		// Only EFFORT_SAME is supported by NBI
		GTPEffort_T gtpEffort = GTPEffort_T.EFFORT_SAME;

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		// additionalInfo.put("GTP_NAME", userLabel);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		GTP_THolder theGTP = cmd.createGTP(userLabel, forceUniqueness, owner,
				listOfTPs, initialCTPname, numberOfCTPs, gtpEffort,
				getNameAndStringValues(additionalInfo));

		if (LOG.isInfoEnabled()) {
			LOG.info("GTP Name: "
					+ convertNameAndStringValueToString(theGTP.value.name));
			LOG.info("GTP nativeEMSName: " + theGTP.value.nativeEMSName);
		}
	}

	public void deleteGTP() throws ProcessingFailureException {
		NameAndStringValue_T[] gtpName = new NameAndStringValue_T[3];

		gtpName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		gtpName[1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		gtpName[2] = new NameAndStringValue_T("GTP", "GTP_1434613321764");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.deleteGTP(gtpName);
	}

	public void createMultiNodeSDHServiceWithGTP()
			throws ProcessingFailureException {
		String sncID = "NISA-SDHServiceWithGTP-1+0-1";
		String userLabel = "NISA-SDHServiceWithGTP-1+0-1";
		String owner = new String("");

		short layerRate = 1;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][3];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		// aEnd[0][2] = new NameAndStringValue_T("GTP", "GTP_1430195806169");
		aEnd[0][2] = new NameAndStringValue_T("GTP", "GTP_1434614160167");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][3];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		// zEnd[0][2] = new NameAndStringValue_T("GTP", "GTP_1430196244883");
		zEnd[0][2] = new NameAndStringValue_T("GTP", "GTP_1434614092772");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		// additionalInfo.put("SNC_DTL_SET_NAME","M-C-W");
		// additionalInfo.put("SNC_PRIORITY", "0");
		// additionalInfo.put("SNC_SNIC_ENABLED", "No");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createMultiNodeSDHWithNEtpInc()
			throws ProcessingFailureException {
		String sncID = "NISA-MN-SDHService-TPInc-1+0-1";
		String userLabel = "NISA-MN-SDHService-TPInc-1+0-1";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// VC4
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=13");

		// VC4_4c
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// VC4
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=13");

		// VC4_4c
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=1");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		// additionalInfo.put("SNC_PERMANENT", "Yes");

		// NE TP Inclusions
		// Route 4: HKG/CH-SNG/PD 64S004(TGNIA)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=8/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=2/port=1");

		neTpInclusions[2][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[2][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[2][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=2/port=1");

		neTpInclusions[3][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[3][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=8/port=1");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO; // yes for 1+r protection
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	/**
	 * Transparent SDH: Transparent SDH is mapping STM16/64 as client signal
	 * into ODU1/ODU2 over OTN link.
	 * 
	 * This is SDH over OTN. i.e. STM frames are mapped to ODU. While creating
	 * tunnel mapping type/frame mode needs to be specified as part of
	 * tpsToModify in createAndActivateSNC. Mapping type is applicable for
	 * SONET/SDH over OTN tunnel and frame mode for Ethernet over OTN tunnel.
	 * 
	 * 
	 * @throws ProcessingFailureException
	 */
	public void createMultiNodeSDHOverOTNWithNEtpInc()
			throws ProcessingFailureException {
		String sncID = "NISA-MN-SDHOverOTN-TPInc-1+0-1";
		String userLabel = "NISA-MN-SDHOverOTN-TPInc-1+0-1";
		String owner = new String("");

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 104;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB1-ASON-CN-01");

		// 2.5G CBR Over OTN; STM16 over ODU1
		// STM16 port; Client Type = OC48_STM16_CBR
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/odu1=1;tp=1");

		// 10G CBR Over OTN; STM64 over ODU2
		// STM64 port; Client Type = OC192_STM64_CBR
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/odu2=1;tp=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB2-ASON-CN-01");

		// 2.5G CBR Over OTN; STM16 over ODU1
		// STM16 port; Client Type = OC48_STM16_CBR
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/odu1=1;tp=1");

		// 10G CBR Over OTN; STM64 over ODU2
		// STM64 port; Client Type = OC192_STM64_CBR
		// zEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/odu2=1;tp=1");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 3: HKG/CH-SNG/PD 64S003-OTU2(SJC)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute3SJC()[0];
		neTpInclusions[2] = this.getRoute3SJC()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		// Required for OTN services
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");

		// additionalInfo.put("SNC_PREEMPTING", "No");
		// additionalInfo.put("SNC_PREEMPTABILITY", "No");
		// additionalInfo.put("SNC_UNPROTECTED_LINES", "Yes");
		// additionalInfo.put("SNC_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_LINEAR_APS_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_LINEAR_VLSR_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_MATCH_TIME_SLOTS", "Yes");
		// additionalInfo.put("SNC_MAX_ADMIN_WEIGHT", "11300");
		// additionalInfo.put("SNC_PERMANENT", "No");
		// additionalInfo.put("SNC_RETAIN_HOME_PATH", "Disabled");
		// additionalInfo.put("SNC_HOME_PATH_PREEMPTABILITY", "Disabled");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.layerRate = layerRate;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		// TPs to Modify
		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[2];
		tpsToModify.value[0] = new TPData_T();
		tpsToModify.value[1] = new TPData_T();

		// A End
		tpsToModify.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[0].tpName = aEnd[0];

		tpsToModify.value[0].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Mapping Type
		// Mapping type is applicable for SONET/SDH over OTN tunnel
		tpsToModify.value[0].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[0].transmissionParams[0].layer = 98;

		tpsToModify.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[1];
		tpsToModify.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"Mapping", "ASYNC_CBR");

		// Z End
		tpsToModify.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[1].tpName = zEnd[0];

		tpsToModify.value[1].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Mapping Type
		tpsToModify.value[1].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[1].transmissionParams[0].layer = 98;

		tpsToModify.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[1];
		tpsToModify.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"Mapping", "ASYNC_CBR");

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createMultiNodeEthOverSDH10GEWithNEtpInc()
			throws ProcessingFailureException {
		String sncID = "NISA-MN-10GbEoverSDHlink-2";
		String userLabel = "NISA-MN-10GbEoverSDHlink-2";
		String owner = new String("");

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 18;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// 10 GbE Service
		// Ethernet port; ClientType=10GbE_SONET_SDH
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=12/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/sts192c_vc4_64c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// 10 GbE Service
		// Ethernet port; ClientType=10GbE_SONET_SDH
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=12/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/sts192c_vc4_64c=1");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		// NE TP Inclusions
		// Route 4: HKG/CH-SNG/PD 64S004(TGNIA)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute4TGNIA()[0];
		neTpInclusions[2] = this.getRoute4TGNIA()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.layerRate = layerRate;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		// The clientType parameter can be used to configure a termination point
		// to process a particular type of client signal. When this parameter is
		// set by an NMS (using the setTPData operation or through the
		// tpsToModify parameter in a createAndActivateSNC operation) it may
		// change the format (i.e. the layers modeled) of the termination point,
		// these changes will be notified to the NMS via an attribute value
		// change notification against the termination point transmissionParams
		// attribute.

		// TPs to Modify
		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[2];
		tpsToModify.value[0] = new TPData_T();
		tpsToModify.value[1] = new TPData_T();

		// A End
		tpsToModify.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[0].tpName = aEnd[0]; // ethPort;

		tpsToModify.value[0].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Frame Mode
		tpsToModify.value[0].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[0].transmissionParams[0].layer = 98;

		tpsToModify.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tpsToModify.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");
		tpsToModify.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");

		// Z End
		tpsToModify.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[1].tpName = zEnd[0]; // ethPort;

		tpsToModify.value[1].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Frame Mode
		tpsToModify.value[1].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[1].transmissionParams[0].layer = 98;

		tpsToModify.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tpsToModify.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");
		tpsToModify.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createMultiNodeEthOverOTN10GbpsWithNEtpInc()
			throws ProcessingFailureException {
		String sncID = "NISA-MN-ETHOverOTN-TPInc-1+0-3";
		String userLabel = "NISA-MN-ETHOverOTN-TPInc-1+0-3";
		String owner = "";

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 105;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=5/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/odu2=1;tp=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=5/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/odu2=1;tp=1");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		// Required for OTN services
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");

		// Route 3 (OTU2): HKG/CH-SNG/PD 64S003-OTU2(SJC)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute3SJC()[0];
		neTpInclusions[2] = this.getRoute3SJC()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO; // yes for 1+r protection
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		// TPs to Modify
		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[2];
		tpsToModify.value[0] = new TPData_T();
		tpsToModify.value[1] = new TPData_T();

		// A End
		tpsToModify.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[0].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[0].tpName = aEnd[0]; // ethPort;

		tpsToModify.value[0].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Frame Mode
		tpsToModify.value[0].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[0].transmissionParams[0].layer = 98;

		tpsToModify.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tpsToModify.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");
		tpsToModify.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_ORDERED_SETS");

		// Z End
		tpsToModify.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpsToModify.value[1].tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpsToModify.value[1].tpName = zEnd[0]; // ethPort;

		tpsToModify.value[1].transmissionParams = new LayeredParameters_T[1];

		// Layer 98 - Set Frame Mode
		tpsToModify.value[1].transmissionParams[0] = new LayeredParameters_T();
		tpsToModify.value[1].transmissionParams[0].layer = 98;

		tpsToModify.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];
		tpsToModify.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"GFPFrameCheckSequence", "Disabled");
		tpsToModify.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_ORDERED_SETS");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	// ODU1 circuit on OTU1 port
	public void createMultiNodeODU1onOTU1() throws ProcessingFailureException {
		String sncID = "NISA-MN-ODU1-1+0-1";
		String userLabel = "NISA-MN-ODU1-1+0-1";
		String owner = "";

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 104;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=26/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/frequency=228.85/odu1=1;tp=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=26/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/frequency=228.85/odu1=1;tp=1");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];

		// NE TP Inclusions
		// Route 3 (OTU2): HKG/CH-SNG/PD 64S003-OTU2(SJC)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute3SJC()[0];
		neTpInclusions[2] = this.getRoute3SJC()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		// Required for OTN services
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");

		// additionalInfo.put("SNC_PREEMPTING", "No");
		// additionalInfo.put("SNC_PREEMPTABILITY", "No");
		// additionalInfo.put("SNC_UNPROTECTED_LINES", "Yes");
		// additionalInfo.put("SNC_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_LINEAR_APS_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_LINEAR_VLSR_PROTECTED_LINES", "No");
		// additionalInfo.put("SNC_MATCH_TIME_SLOTS", "Yes");

		// additionalInfo.put("SNC_MAX_ADMIN_WEIGHT", "11300");
		// additionalInfo.put("SNC_PERMANENT", "No");
		// additionalInfo.put("SNC_RETAIN_HOME_PATH", "Disabled");
		// additionalInfo.put("SNC_HOME_PATH_PREEMPTABILITY", "Disabled");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO; // yes for 1+r protection
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	// ODU1 circuit on OTU2 port
	public void createMultiNodeODU1onOTU2() throws ProcessingFailureException {
		String sncID = "NISA-MN-ODU1-1+0-2";
		String userLabel = "NISA-MN-ODU1-1+0-2";
		String owner = "";

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 104;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=5/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/frequency=228.85/odu2=1;tp=1;pt=20/odu1=3;tp=3");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=5/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/frequency=228.85/odu2=1;tp=1;pt=20/odu1=3;tp=3");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];
		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];

		// NE TP Inclusions
		// Route 3 (OTU2): HKG/CH-SNG/PD 64S003-OTU2(SJC)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute3SJC()[0];
		neTpInclusions[2] = this.getRoute3SJC()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		// Required for OTN services
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO; // yes for 1+r protection
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	// ODU2 circuit on OTU2 port
	public void createMultiNodeODU2onOTU2() throws ProcessingFailureException {
		String sncID = "NISA-MN-ODU2-1+0-1";
		String userLabel = "NISA-MN-ODU2-1+0-1";
		String owner = "";

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 105;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=5/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/frequency=228.85/odu2=1;tp=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=5/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/frequency=228.85/odu2=1;tp=1");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];
		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];

		// NE TP Inclusions
		// Route 3 (OTU2): HKG/CH-SNG/PD 64S003-OTU2(SJC)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = aEnd[0][0];
		neTpInclusions[0][1] = aEnd[0][1];
		neTpInclusions[0][2] = aEnd[0][2];

		neTpInclusions[1] = this.getRoute3SJC()[0];
		neTpInclusions[2] = this.getRoute3SJC()[1];

		neTpInclusions[3][0] = zEnd[0][0];
		neTpInclusions[3][1] = zEnd[0][1];
		neTpInclusions[3][2] = zEnd[0][2];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		// Required for OTN services
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = getNameAndStringValues(additionalInfo);
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NO;
		createData.rerouteAllowed = Reroute_T.RR_NO; // yes for 1+r protection
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MINOR_IMPACT;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void configureEquipmentTSLM12() throws ProcessingFailureException,
			SAXException, UnsupportedEncodingException, FileNotFoundException {

		EquipmentConfigurationData_T equipmentConfig = new EquipmentConfigurationData_T();
		NameAndStringValue_T[] equipmentConfigNames = new NameAndStringValue_T[3];

		equipmentConfigNames[0] = new NameAndStringValue_T("EMS",
				this.realEMSName);

		equipmentConfigNames[1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		equipmentConfigNames[2] = new NameAndStringValue_T("EquipmentHolder",
				"/rack=1/shelf=2/slot=4");

		equipmentConfig.name = equipmentConfigNames;
		equipmentConfig.equipmentType = "TSLM-12";

		NameAndStringValue_T[] equipmentConfigParams = new NameAndStringValue_T[1];
		equipmentConfigParams[0] = new NameAndStringValue_T(
				"AI_PORT_GROUP_TYPE_7", "1x10GbE_SONET_SDH");

		equipmentConfig.eqtConfigParameters = equipmentConfigParams;

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("output.xml"),
				format);
		Corba2XMLHandler handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName,
				xmlWriter);
		handler.handlerBuilderStart();
		cmd.configureEquipment(equipmentConfig);
		handler.handlerBuilderEnd();
	}

	public void configureEquipmentTSLM48() throws ProcessingFailureException,
			SAXException, UnsupportedEncodingException, FileNotFoundException {

		EquipmentConfigurationData_T equipmentConfig = new EquipmentConfigurationData_T();
		NameAndStringValue_T[] equipmentConfigNames = new NameAndStringValue_T[3];

		equipmentConfigNames[0] = new NameAndStringValue_T("EMS",
				this.realEMSName);

		equipmentConfigNames[1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		equipmentConfigNames[2] = new NameAndStringValue_T("EquipmentHolder",
				"/rack=1/shelf=2/slot=15");

		equipmentConfig.name = equipmentConfigNames;
		equipmentConfig.equipmentType = "TSLM-48";

		NameAndStringValue_T[] equipmentConfigParams = new NameAndStringValue_T[1];
		equipmentConfigParams[0] = new NameAndStringValue_T(
				"AI_PORT_GROUP_TYPE_7", "4x2500M_OTN");

		equipmentConfig.eqtConfigParameters = equipmentConfigParams;

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("output.xml"),
				format);
		Corba2XMLHandler handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName,
				xmlWriter);
		handler.handlerBuilderStart();
		cmd.configureEquipment(equipmentConfig);
		handler.handlerBuilderEnd();
	}

	/**
	 * Change Port Client type to from 10GbE_SONET_SDH to 10GbE_SONET_SDH_VCAT
	 * 
	 * @throws ProcessingFailureException
	 * @throws SAXException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public void setTPData() throws ProcessingFailureException, SAXException,
			UnsupportedEncodingException, FileNotFoundException {

		NameAndStringValue_T[][] tp = new NameAndStringValue_T[1][3];

		tp[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		tp[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		tp[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=10/port=1");

		TPData_T tpData = new TPData_T();

		tpData.egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpData.ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpData.tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpData.tpName = tp[0];

		tpData.transmissionParams = new LayeredParameters_T[1];

		// Layer 50
		tpData.transmissionParams[0] = new LayeredParameters_T();
		tpData.transmissionParams[0].layer = 50;
		tpData.transmissionParams[0].transmissionParams = new NameAndStringValue_T[1];
		tpData.transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ClientType", "10GbE_SONET_SDH_VCAT");

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("output.xml"),
				format);
		Corba2XMLHandler handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName,
				xmlWriter);
		handler.handlerBuilderStart();
		cmd.setTPData(tpData);
		handler.handlerBuilderEnd();
	}

	/**
	 * Change Port Client type to from 10GbE_SONET_SDH_VCAT to 10GbE_SONET_SDH
	 * 
	 * @throws ProcessingFailureException
	 * @throws SAXException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	public void setTPData2() throws ProcessingFailureException, SAXException,
			UnsupportedEncodingException, FileNotFoundException {

		NameAndStringValue_T[][] tp = new NameAndStringValue_T[1][3];

		tp[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		tp[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		tp[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=10/port=1");

		TPData_T tpData = new TPData_T();

		tpData.egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpData.ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tpData.tpMappingMode = TerminationMode_T.TM_TERMINATED_AND_AVAILABLE_FOR_MAPPING;
		tpData.tpName = tp[0];

		tpData.transmissionParams = new LayeredParameters_T[1];

		// Layer 50
		tpData.transmissionParams[0] = new LayeredParameters_T();
		tpData.transmissionParams[0].layer = 50;
		tpData.transmissionParams[0].transmissionParams = new NameAndStringValue_T[1];
		tpData.transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T(
				"ClientType", "10GbE_SONET_SDH");

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("output.xml"),
				format);
		Corba2XMLHandler handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName,
				xmlWriter);
		handler.handlerBuilderStart();
		cmd.setTPData(tpData);
		handler.handlerBuilderEnd();
	}

	public void deactivateAndDeleteSNC() throws ProcessingFailureException {
		String sncID = "NISA-MN-ODU1-1+0-1";
		deactivateAndDeleteSNC(sncID);
	}

	public void deactivateAndDeleteSNC(String sncID)
			throws ProcessingFailureException {
		NameAndStringValue_T[] sncName = new NameAndStringValue_T[3];
		sncName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork", "MLSN_1");
		sncName[2] = new NameAndStringValue_T("SubnetworkConnection", sncID);

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MAJOR_IMPACT;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.deactivateAndDeleteSNC(sncName, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void releaseCall() throws ProcessingFailureException {
		String callID = "NISA-10GEsubrate-2";
		releaseCall(callID);
	}

	public void releaseCall(String callID) throws ProcessingFailureException {
		NameAndStringValue_T[] callName = new NameAndStringValue_T[2];
		callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		callName[1] = new NameAndStringValue_T("Call", callID);

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.releaseCall(callName, tpsToModify);
	}

	public void getSNC() throws ProcessingFailureException, SAXException,
			UnsupportedEncodingException, FileNotFoundException {

		String sncID = "NISA-MN-SNC-10GbEoverSDHlink-1";
		// String sncID = "NISA-MN-SDHService-TPInc-1+0-3";
		// String sncID = "HKG_CH_SNG_PD 1GE004";
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("output.xml"),
				format);
		Corba2XMLHandler handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName,
				xmlWriter);
		handler.handlerBuilderStart();
		cmd.getSNC(sncID);
		handler.handlerBuilderEnd();
	}

	public NameAndStringValue_T[] getNameAndStringValues(
			Hashtable<String, String> additionalInfo) {
		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		return additionalCreationInfo;
	}

	public String convertNameAndStringValueToString(
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

	public NameAndStringValue_T[][] getRoute1APCN2() {
		// Route 1 (OTU2) via DWDM: HKG/CH-SNG/PD 64S001-OTU2(APCN2)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=9/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=9/port=1");

		return neTpInclusions;
	}

	public NameAndStringValue_T[][] getRoute2C2C() {
		// Route 2 (SDH Transparent) via DWDM: HKG/CH-SNG/PD 64S002(C2C)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=8/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=8/port=1");

		return neTpInclusions;
	}

	public NameAndStringValue_T[][] getRoute3SJC() {
		// Route 3 (OTU2): HKG/CH-SNG/PD 64S003-OTU2(SJC)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=1/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=1/port=1");

		return neTpInclusions;
	}

	public NameAndStringValue_T[][] getRoute4TGNIA() {
		// Route 4 (SDH Transparent): HKG/CH-SNG/PD 64S004(TGNIA)
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=2/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=2/port=1");

		return neTpInclusions;
	}

	public NameAndStringValue_T[][] getRoute5OOB() {
		// Route 5 (OOB): HKG/CH-SNG/PD 64S005-OOB
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=2/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=2/port=1");

		return neTpInclusions;
	}

	public NameAndStringValue_T[][] getRoute6Extra() {
		// Route 5 (OOB): HKG/CH-SNG/PD 64S005-OOB
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=4/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=4/port=1");

		return neTpInclusions;
	}
}
