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
			// main.createMultiNodeSDHService();
			// main.createMultiNodeSDHWithNEtpInc();
			// main.createMultiNodeEthernetOverOTNWithNEtpInc();
			// main.createMultiNodeSDHOverOTNWithNEtpInc();

			// main.createGTP();
			// main.createMultiNodeSDHServiceWithGTP();

			// main.deactivateAndDeleteSNC();
			// main.deleteGTP();
			main.getSNC();

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
		String sncID = "NISA-MN-SDHService-1+0-3";
		String userLabel = "NISA-MN-SDHService-1+0-1";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=7");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// SDH Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=7");

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

		// EstablishCallCommand establishCallCommand = new EstablishCallCommand(
		// ccd, sncs, tps);
	}

	public void createMultiNodeEthernetServiceWithVCG()
			throws ProcessingFailureException {
		String callID = "NISA-MN-EthService-1";
		String sncID = "NISA-MN-EthService-1";
		String userLabel = "NISA-MN-EthService-1";
		String owner = new String("");

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
				"/rack=1/shelf=2/slot=16/port_group=2/port=1-A-10-1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB2-ASON-CN-01");

		// GbE Service
		// VCG port
		zEnd[0][2] = new NameAndStringValue_T("FTP",
				"/rack=1/shelf=2/slot=16/port_group=2/port=1-A-10-1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");
		additionalInfo.put("CallName", callID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
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

		tps.value[0] = new TPData_T();
		tps.value[0].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[0].tpMappingMode = TerminationMode_T.from_int(1);
		tps.value[0].tpName = zEnd[0];// zEndTp;//ethPort;

		// Layer 98
		tps.value[0].transmissionParams = new LayeredParameters_T[2];
		tps.value[0].transmissionParams[0] = new LayeredParameters_T();
		tps.value[0].transmissionParams[0].layer = 98;
		tps.value[0].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];

		tps.value[0].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T();
		tps.value[0].transmissionParams[0].transmissionParams[0].name = new String(
				"GFPFrameCheckSequence");
		tps.value[0].transmissionParams[0].transmissionParams[0].value = new String(
				"Enabled");

		tps.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T();
		tps.value[0].transmissionParams[0].transmissionParams[1].name = new String(
				"ProtocolIdentifier");
		tps.value[0].transmissionParams[0].transmissionParams[1].value = new String(
				"GFP_FRAME_MAPPED");

		// Layer 99
		tps.value[0].transmissionParams[1] = new LayeredParameters_T();
		tps.value[0].transmissionParams[1].layer = 99;
		tps.value[0].transmissionParams[1].transmissionParams = new NameAndStringValue_T[2];
		tps.value[0].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T();
		tps.value[0].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T();
		// tps.value[0].transmissionParams[1].transmissionParams[2] = new
		// NameAndStringValue_T();
		tps.value[0].transmissionParams[1].transmissionParams[0].name = new String(
				"VCAT");
		tps.value[0].transmissionParams[1].transmissionParams[0].value = new String(
				"Enabled");
		tps.value[0].transmissionParams[1].transmissionParams[1].name = new String(
				"DynamicAllocationEnabled");
		tps.value[0].transmissionParams[1].transmissionParams[1].value = new String(
				"Enabled");
		// tps.value[0].transmissionParams[1].transmissionParams[2].name = new
		// String("AllocatedNumber");
		// tps.value[0].transmissionParams[1].transmissionParams[2].value = new
		// String("7");

		// A End
		tps.value[1] = new TPData_T();
		tps.value[1].egressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].ingressTrafficDescriptorName = new NameAndStringValue_T[0];
		tps.value[1].tpMappingMode = TerminationMode_T.from_int(1);
		tps.value[1].tpName = aEnd[0];// aEndTp;//ethPort;

		// Layer 98
		tps.value[1].transmissionParams = new LayeredParameters_T[2];
		tps.value[1].transmissionParams[0] = new LayeredParameters_T();
		tps.value[1].transmissionParams[0].layer = 98;
		tps.value[1].transmissionParams[0].transmissionParams = new NameAndStringValue_T[2];

		tps.value[1].transmissionParams[0].transmissionParams[0] = new NameAndStringValue_T();
		tps.value[1].transmissionParams[0].transmissionParams[0].name = new String(
				"GFPFrameCheckSequence");
		tps.value[1].transmissionParams[0].transmissionParams[0].value = new String(
				"Enabled");

		tps.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T();
		tps.value[1].transmissionParams[0].transmissionParams[1].name = new String(
				"ProtocolIdentifier");
		tps.value[1].transmissionParams[0].transmissionParams[1].value = new String(
				"GFP_FRAME_MAPPED");

		// Layer 99
		tps.value[1].transmissionParams[1] = new LayeredParameters_T();
		tps.value[1].transmissionParams[1].layer = 99;
		tps.value[1].transmissionParams[1].transmissionParams = new NameAndStringValue_T[2];
		tps.value[1].transmissionParams[1].transmissionParams[0] = new NameAndStringValue_T();
		tps.value[1].transmissionParams[1].transmissionParams[1] = new NameAndStringValue_T();
		// tps.value[0].transmissionParams[1].transmissionParams[2] = new
		// NameAndStringValue_T();
		tps.value[1].transmissionParams[1].transmissionParams[0].name = new String(
				"VCAT");
		tps.value[1].transmissionParams[1].transmissionParams[0].value = new String(
				"Enabled");
		tps.value[1].transmissionParams[1].transmissionParams[1].name = new String(
				"DynamicAllocationEnabled");
		tps.value[1].transmissionParams[1].transmissionParams[1].value = new String(
				"Enabled");

		// tps.value[0].transmissionParams[2] = new LayeredParameters_T();
		// tps.value[0].transmissionParams[2].layer = 1;
		// tps.value[0].transmissionParams[2].transmissionParams = new
		// NameAndStringValue_T[1];
		// tps.value[0].transmissionParams[2].transmissionParams[0] = new
		// NameAndStringValue_T();
		// tps.value[0].transmissionParams[2].transmissionParams[0].name = new
		// String("TunnelEndPoint");
		// tps.value[0].transmissionParams[2].transmissionParams[0].value = new
		// String("PTP=/rack=1/shelf=5/slot=17/sub_slot=2/port=1");

		CallCreateData_T ccd = new CallCreateData_T();

		ccd.callName = new NameAndStringValue_T[2];
		ccd.callName[0] = new NameAndStringValue_T();
		ccd.callName[0].name = new String("EMS");
		ccd.callName[0].value = this.realEMSName;

		ccd.callName[1] = new NameAndStringValue_T();
		ccd.callName[1].name = new String("Call");
		ccd.callName[1].value = callID;

		ccd.aEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);// null;
		ccd.zEnd = new CallEnd_T("", "", "", new NameAndStringValue_T[0]);

		ccd.forceUniqueness = false;
		ccd.userLabel = userLabel;
		ccd.additionalCreationInfo = new NameAndStringValue_T[0];
		ccd.networkAccessDomain = new String("");
		ccd.owner = owner;

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.from_int(0),
				LevelofEffort_T.from_int(0), LevelofEffort_T.from_int(0),
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		// EstablishCallCommand establishCallCommand = new EstablishCallCommand(
		// ccd, sncs, tps);
	}

	public void createMultiNodeEthernetoverSDHService()
			throws ProcessingFailureException {
		String sncID = "NISA-MN-SNC-EthService-WithoutVCG-1";
		String userLabel = "NISA-MN-SNC-EthService-WithoutVCG-1";
		String owner = new String("");

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 18;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB1-ASON-CN-01");

		// GbE Service
		// Ethernet port; ClientType=10GbE_SONET_SDH
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/sts192c_vc4_64c=1");
		// aEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/sts3c_au4=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB2-ASON-CN-01");

		// GbE Service
		// Ethernet port; ClientType=10GbE_SONET_SDH
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=11/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/encapsulation=1/sts192c_vc4_64c=1");
		// zEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/encapsulation=1/sts3c_au4=1");

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
				"GFPFrameCheckSequence", "Enabled");
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
				"GFPFrameCheckSequence", "Enabled");
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
	public void createMultiNodeEthernetoverOTNService()
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

		additionalInfo.put("SNC_PREEMPTING", "No");
		additionalInfo.put("SNC_PREEMPTABILITY", "No");
		additionalInfo.put("SNC_UNPROTECTED_LINES", "Yes");
		additionalInfo.put("SNC_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_LINEAR_APS_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_LINEAR_VLSR_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_MATCH_TIME_SLOTS", "Yes");
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");
		additionalInfo.put("SNC_MAX_ADMIN_WEIGHT", "11300");
		additionalInfo.put("SNC_PERMANENT", "No");
		additionalInfo.put("SNC_RETAIN_HOME_PATH", "Disabled");
		additionalInfo.put("SNC_HOME_PATH_PREEMPTABILITY", "Disabled");

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
				"GFPFrameCheckSequence", "Enabled");
		tpsToModify.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");

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
				"GFPFrameCheckSequence", "Enabled");
		tpsToModify.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");

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
	public void createMultiNodeSDHoverOTNService()
			throws ProcessingFailureException {
		String sncID = "NISA-MN-SNC-SDHoverOTN-1";
		String userLabel = "NISA-MN-SNC-SDHoverOTN-1";
		String owner = new String("");

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 104;

		// A Side
		// SDH over OTN; STM16 over ODU1
		// STM16 port; Client Type = OC48_STM16_CBR
		String A_PTP = "/rack=1/shelf=2/slot=1/sub_slot=11/port=1";
		String A_CTP = "/encapsulation=1/odu1=1;tp=1";

		// for odu2=1 on STM64 interface - "/encapsulation=1/odu2=1;tp=1"
		// for odu1=1 on STM16 interface - "/encapsulation=1/odu1=1;tp=1"

		// Z Side
		// SDH over OTN; STM16 over ODU1
		// STM16 port; Client Type = OC48_STM16_CBR
		String Z_PTP = "/rack=1/shelf=2/slot=1/sub_slot=11/port=1"; // STM16
		String Z_CTP = "/encapsulation=1/odu1=1;tp=1";

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB1-ASON-CN-01");
		aEnd[0][2] = new NameAndStringValue_T("PTP", A_PTP);
		aEnd[0][3] = new NameAndStringValue_T("CTP", A_CTP);

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB2-ASON-CN-01");
		zEnd[0][2] = new NameAndStringValue_T("PTP", Z_PTP);
		zEnd[0][3] = new NameAndStringValue_T("CTP", Z_CTP);

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");

		additionalInfo.put("SNC_PREEMPTING", "No");
		additionalInfo.put("SNC_PREEMPTABILITY", "No");
		additionalInfo.put("SNC_UNPROTECTED_LINES", "Yes");
		additionalInfo.put("SNC_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_LINEAR_APS_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_LINEAR_VLSR_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_MATCH_TIME_SLOTS", "Yes");
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");
		additionalInfo.put("SNC_MAX_ADMIN_WEIGHT", "11300");
		additionalInfo.put("SNC_PERMANENT", "No");
		additionalInfo.put("SNC_RETAIN_HOME_PATH", "Disabled");
		additionalInfo.put("SNC_HOME_PATH_PREEMPTABILITY", "Disabled");

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
		String callID = "NISA-MN-SNCP-SDHService-1";
		String sncID = "NISA-MN-SNCP-SDHService-1";
		String userLabel = "NISA-MN-SNCP-SDHService-1";
		String owner = new String("");

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c; 104 = OTU1
		short layerRate = 17;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB1-ASON-CN-01");
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=1/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-LAB2-ASON-CN-01");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=1/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

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

		ccd.callDiversity = new Diversity_T(LevelofEffort_T.from_int(0),
				LevelofEffort_T.from_int(0), LevelofEffort_T.from_int(0),
				new NameAndStringValue_T[0], new NameAndStringValue_T[0]);
		ccd.callParameters = new CallParameterProfile_T("", "", "",
				new NameAndStringValue_T[0]);

		// EstablishCallCommand establishCallCommand = new EstablishCallCommand(
		// ccd, sncs, tpsToModify);
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

	public void deleteGTP() throws ProcessingFailureException {
		NameAndStringValue_T[] gtpName = new NameAndStringValue_T[3];

		gtpName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		gtpName[1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		gtpName[2] = new NameAndStringValue_T("GTP", "GTP_1430196244883");

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
		aEnd[0][2] = new NameAndStringValue_T("GTP", "GTP_1430195806169");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][3];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		zEnd[0][2] = new NameAndStringValue_T("GTP", "GTP_1430196244883");

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

	public void createMultiNodeSDHWithNEtpInc()
			throws ProcessingFailureException {
		String sncID = "NISA-MN-SDHService-TPInc-1+0-3";
		String userLabel = "NISA-MN-SDHService-TPInc-1+0-3";
		String owner = "";

		// 15 = vc4; 16 = vc4_4c; 17 = vc4_16c; 18 = vc4_64c
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=13");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");

		// SDH Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=8/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4=13");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_PRIORITY", "0");

		// NE TP Inclusions
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
				"/rack=1/shelf=2/slot=1/sub_slot=8/port=1");

		neTpInclusions[2][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[2][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[2][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=1/sub_slot=8/port=1");

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
		String sncID = "NISA-MN-SDHOverOTN-TPInc-1+0-3";
		String userLabel = "NISA-MN-SDHOverOTN-TPInc-1+0-3";
		String owner = new String("");

		// 104 = ODU1, 105 = ODU2, 11315 = PROP_ODU0
		short layerRate = 104;

		// A Side
		// SDH over OTN; STM16 over ODU1
		// STM16 port; Client Type = OC48_STM16_CBR
		String A_PTP = "/rack=1/shelf=2/slot=15/sub_slot=34/port=1";
		String A_CTP = "/encapsulation=1/odu1=1;tp=1";

		// for odu2=1 on STM64 interface - "/encapsulation=1/odu2=1;tp=1"
		// for odu1=1 on STM16 interface - "/encapsulation=1/odu1=1;tp=1"

		// Z Side
		// SDH over OTN; STM16 over ODU1
		// STM16 port; Client Type = OC48_STM16_CBR
		String Z_PTP = "/rack=1/shelf=2/slot=15/sub_slot=34/port=1"; // STM16
		String Z_CTP = "/encapsulation=1/odu1=1;tp=1";

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		aEnd[0][2] = new NameAndStringValue_T("PTP", A_PTP);
		aEnd[0][3] = new NameAndStringValue_T("CTP", A_CTP);

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		zEnd[0][2] = new NameAndStringValue_T("PTP", Z_PTP);
		zEnd[0][3] = new NameAndStringValue_T("CTP", Z_CTP);

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SNC_REGROOM_ALLOWED", "No");
		additionalInfo.put("SNC_PRIORITY", "0");
		additionalInfo.put("SNC_SNIC_ENABLED", "No");

		additionalInfo.put("SNC_PREEMPTING", "No");
		additionalInfo.put("SNC_PREEMPTABILITY", "No");
		additionalInfo.put("SNC_UNPROTECTED_LINES", "Yes");
		additionalInfo.put("SNC_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_LINEAR_APS_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_LINEAR_VLSR_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_MATCH_TIME_SLOTS", "Yes");
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");
		additionalInfo.put("SNC_MAX_ADMIN_WEIGHT", "11300");
		additionalInfo.put("SNC_PERMANENT", "No");
		additionalInfo.put("SNC_RETAIN_HOME_PATH", "Disabled");
		additionalInfo.put("SNC_HOME_PATH_PREEMPTABILITY", "Disabled");

		// NE TP Inclusions
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=34/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=1/port=1");

		neTpInclusions[2][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[2][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[2][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=1/port=1");

		neTpInclusions[3][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[3][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=15/sub_slot=34/port=1");

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

	public void createMultiNodeEthernetOverOTNWithNEtpInc()
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

		additionalInfo.put("SNC_PREEMPTING", "No");
		additionalInfo.put("SNC_PREEMPTABILITY", "No");
		additionalInfo.put("SNC_UNPROTECTED_LINES", "Yes");
		additionalInfo.put("SNC_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_LINEAR_APS_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_LINEAR_VLSR_PROTECTED_LINES", "No");
		additionalInfo.put("SNC_MATCH_TIME_SLOTS", "Yes");
		additionalInfo.put("SNC_COST_CRITERIA", "Admin Weight");
		additionalInfo.put("SNC_MAX_ADMIN_WEIGHT", "11300");
		additionalInfo.put("SNC_PERMANENT", "No");
		additionalInfo.put("SNC_RETAIN_HOME_PATH", "Disabled");
		additionalInfo.put("SNC_HOME_PATH_PREEMPTABILITY", "Disabled");

		// NE TP Inclusions
		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][3];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=5/port=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"SNG-PPD-ASON-CN-01");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=1/port=1");

		neTpInclusions[2][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[2][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[2][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=1/port=1");

		neTpInclusions[3][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[3][1] = new NameAndStringValue_T("ManagedElement",
				"HKG-CH-ASON-CN-01");
		neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=2/slot=4/sub_slot=5/port=1");

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
				"GFPFrameCheckSequence", "Enabled");
		tpsToModify.value[0].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");

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
				"GFPFrameCheckSequence", "Enabled");
		tpsToModify.value[1].transmissionParams[0].transmissionParams[1] = new NameAndStringValue_T(
				"ProtocolIdentifier", "GFP_FRAME_MAPPED");

		// TPDataList_THolder tpsToModify = new TPDataList_THolder();
		// tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void deactivateAndDeleteSNC() throws ProcessingFailureException {
		// String sncID = "NISA-MultiNodeSNC-SDHService-1+0-1";
		String sncID = "NISA-MN-SDHService-TPInc-1+0-3";
		// String sncID = "NISA-SDHServiceWithGTP-1+0-1";

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
		String callID = "NISA-MultiNodeSNC-EthService-1";

		NameAndStringValue_T[] callName = new NameAndStringValue_T[2];
		callName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		callName[1] = new NameAndStringValue_T("Call", callID);

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		// ReleaseCallCommand cmd = new ReleaseCallCommand(callName,
		// tpsToModify);
	}

	public void getSNC() throws ProcessingFailureException, SAXException,
			UnsupportedEncodingException, FileNotFoundException {

		String sncID = "NISA-MN-ETHOverOTN-TPInc-1+0-3";
		// String sncID = "NISA-MN-SDHService-TPInc-1+0-3";
		//String sncID = "HKG_CH_SNG_PD 1GE004";
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

}
