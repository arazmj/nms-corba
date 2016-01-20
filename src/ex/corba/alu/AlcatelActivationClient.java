package ex.corba.alu;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import multiLayerSubnetwork.EMSFreedomLevel_T;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import subnetworkConnection.CrossConnect_T;
import subnetworkConnection.GradesOfImpact_T;
import subnetworkConnection.NetworkRouted_T;
import subnetworkConnection.ProtectionEffort_T;
import subnetworkConnection.Reroute_T;
import subnetworkConnection.RouteDescriptor_T;
import subnetworkConnection.SNCCreateData_T;
import subnetworkConnection.SNCModifyData_T;
import subnetworkConnection.SNCType_T;
import subnetworkConnection.StaticProtectionLevel_T;
import subnetworkConnection.TPDataList_THolder;
import subnetworkConnection.TPData_T;
import emsSession.EmsSession_I;
import globaldefs.ConnectionDirection_T;
import globaldefs.NameAndStringValue_T;
import globaldefs.ProcessingFailureException;

public class AlcatelActivationClient extends AlcatelConnection {

	public static final Logger LOG = LoggerFactory
			.getLogger(AlcatelActivationClient.class);

	protected static EmsSession_I emsSession;

	public static void main(String[] args) {
		AlcatelActivationClient main = new AlcatelActivationClient();

		try {
			emsSession = main.openEmsSession(args);

			// main.createE1();
			// main.createDS3();
			// main.createE1WithConstraintsNE();
			// main.createE1WithConstraintsST();
			// main.createE1WithNEtpInc();
			main.deactivateAndDeleteSNC("PATH_790", "path");
			main.createE1WithNEtpInc2();
			// main.createDS3WithNEtpInc2();
			// main.createE1WithSNCP();
			// main.createE1WithSNCPWithNEtpInc();

			// main.createAU4WithNEtpInc();

			// main.createE1WithVNE();
			// main.createDS3WithVNE();

			// Contiguous Concatenated Trail
			// main.createVC4_4c();
			// main.createVC4_4cWithNEtpInc();

			/*
			 * Channelized Circuits
			 */
			// main.createAu4ChannelizedWithE1();
			// main.createAu4ChannelizedWithDS3();

			// Path for Ethernet services
			// main.create2MPathForEVC();
			// main.create4MPathForEVC();
			// main.create10MPathForEVC();

			// main.modifySNCUpgrade();
			// main.modifySNCDowngrade();

			/*
			 * Server Trail
			 */
			// main.createServerTrailNodeToNode();
			// main.modifySNC("TRAIL_5536");
			// main.createServerTrailWithNEtpInc();
			// main.createServerTrailWithConstraintsTL();
			// main.createServerTrailNodeToVNE();

			// main.deactivateAndDeleteSNC("PATH_703", "path");
			// main.deactivateAndDeleteSNC("TRAIL_5310", "trail");
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

	public void createE1() throws ProcessingFailureException {
		String sncID = "NISA-E1-Path-1";
		String userLabel = "NISA-E1-Path-1";
		String owner = "";

		// 80 = LR_DSR_2M
		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b01p010c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b01p010c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "102/1");

		// SDH Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC3/r01s1b01p012c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC3/r01s1b01p012c1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

	public void createDS3() throws ProcessingFailureException {
		String sncID = "NISA-DS3-Path-1";
		String userLabel = "NISA-DS3-Path-1";
		String owner = "";

		// DS3
		// 84 = LR_DSR_45M
		short layerRate = 84;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b13p001c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b13p001c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");

		// SDH Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b13p001c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b13p001c1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo45Mb");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

	public void createE1WithConstraintsNE() throws ProcessingFailureException {
		String sncID = "NISA-E1-Path-ConstraintsNE-1";
		String userLabel = "NISA-E1-Path-ConstraintsNE-1";
		String owner = "";

		// 80 = LR_DSR_2M
		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b01p014c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b01p014c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b01p020c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b01p020c1");

		// Route constraints
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[1][2];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"102/1");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
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

	public void createE1WithConstraintsST() throws ProcessingFailureException {
		String sncID = "NISA-E1-Path-ConstraintsST-1";
		String userLabel = "NISA-E1-Path-ConstraintsST-1";
		String owner = "";

		// 80 = LR_DSR_2M
		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b01p004c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b01p004c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b01p002c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b01p002c1");

		// Route constraints as Server trail End points
		// Refer <TL_ID>CONNECT_23</TL_ID> for Server trail end points
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"101/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b37p001");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b25p001");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");
		additionalInfo.put("INCLU2_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
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

	public void createE1WithNEtpInc() throws ProcessingFailureException {
		String sncID = "NISA-E1-Path-2";
		String userLabel = "NISA-E1-Path-2";
		String owner = "";

		// 80 = LR_DSR_2M
		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b01p004c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b01p004c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b01p002c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b01p002c1");

		// Route constraints as Server trail End points
		// Refer <TL_ID>CONNECT_23</TL_ID> for Server trail end points
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][4];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"101/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b37p001");
		neTpInclusions[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC2/r01s1b37p001 01/1/1.1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"101/1");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b01p004c1");
		neTpInclusions[1][3] = new NameAndStringValue_T("CTP",
				"NISA_CC2/r01s1b01p004c1");

		neTpInclusions[2][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[2][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[2][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b25p001");
		neTpInclusions[2][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b25p001 01/1/1.1");

		neTpInclusions[3][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[3][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b01p002c1");
		neTpInclusions[3][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b01p002c1");

		// NameAndStringValue_T[][] neTpInclusions= new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");
		additionalInfo.put("INCLU2_SECTION", "main");
		additionalInfo.put("INCLU3_SECTION", "main");
		additionalInfo.put("INCLU4_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
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

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createE1WithNEtpInc2() throws ProcessingFailureException {
		String sncID = "NISA-E1-Path-6";
		String userLabel = "NISA-E1-Path-6";
		String owner = "";

		// 80 = LR_DSR_2M
		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End - Node
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b01p019c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b01p019c1");

		// Z-End - VNE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/105");

		// Route constraints as Server trail End points
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[1][4];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b31p001");
		neTpInclusions[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b31p001 02/1/1.2");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");
		// additionalInfo.put("INCLU2_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);

			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = false;
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

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createDS3WithNEtpInc2() throws ProcessingFailureException {
		String sncID = "NISA-DS3-Path-2";
		String userLabel = "NISA-DS3-Path-2";
		String owner = "";

		// 84 = LR_DSR_45M
		short layerRate = 84;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End - Node
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b13p003c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b13p003c1");

		// Z-End - VNE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/105");

		// Route constraints as Server trail End points
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[1][4];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b31p001");
		neTpInclusions[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b31p001 01/3");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo45Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");
		// additionalInfo.put("INCLU2_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);

			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = false;
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

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	// Slot 29 on NISA_CC1 and NISA_CC2 has 8 port Ethernet card. Each Ethernet
	// port (local port) has corresponding SDH port (remote port). i.e. it has 8
	// SDH ports
	public void create2MPathForEVC() throws ProcessingFailureException {
		String sncID = "NISA-EVC-2M-2";
		String userLabel = "NISA-EVC-2M-2";
		String owner = "";

		// Service Type = Ethernet
		short layerRate = 96;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// Ethernet service SDH Path
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b29p05-gMAU");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC2/r01s1b29p05-gMAU");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");

		// Ethernet service SDH Path
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b29p01-gMAU");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b29p01-gMAU");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "eth10or100MbRateAdapt");
		additionalInfo.put("transportRate", "loTu12");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

	// This create one Path and 2 Trails (VC12)
	public void create4MPathForEVC() throws ProcessingFailureException {
		String sncID = "NISA-EVC-4M-1";
		String userLabel = "NISA-EVC-4M-1";
		String owner = "";

		// Service Type = Ethernet
		short layerRate = 96;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// Ethernet service SDH Path
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b29p05-gMAU");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC2/r01s1b29p05-gMAU");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");

		// Ethernet service SDH Path
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b29p07-gMAU");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b29p07-gMAU");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "eth10or100MbRateAdapt");
		additionalInfo.put("transportRate", "tu12virtN");
		additionalInfo.put("ConcatenationLevel", "2");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

	// This create one Path and 5 Trails (VC12)
	public void create10MPathForEVC() throws ProcessingFailureException {
		String sncID = "NISA-EVC-10M-1";
		String userLabel = "NISA-EVC-10M-1";
		String owner = "";

		// Service Type = Ethernet
		short layerRate = 96;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];

		// A-End - VNE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b29p07-gMAU");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b29p07-gMAU");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "eth10or100MbRateAdapt");
		additionalInfo.put("transportRate", "tu12virtN");
		additionalInfo.put("ConcatenationLevel", "5");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

	// Upgrade Ethernet service bandwidth
	public void modifySNCUpgrade() throws ProcessingFailureException {
		String sncID = "PATH_173";
		String userLabel = "NISA-EVC-10M-1";
		String owner = "";

		// Service Type = Ethernet
		short layerRate = 96;

		NameAndStringValue_T[] sncName = new NameAndStringValue_T[3];
		sncName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork", "SDH");
		sncName[2] = new NameAndStringValue_T("SubnetworkConnection", sncID);

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];

		// A-End - VNE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b29p07-gMAU");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b29p07-gMAU");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		RouteDescriptor_T[] addedOrNewRoute = new RouteDescriptor_T[0];
		RouteDescriptor_T[] removedRoute = new RouteDescriptor_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		// Add 2 more trails. Initial bandwidth 10M i.e. 5 VC12 trails. Final
		// will be 5+2=7 trails i.e. 14M
		additionalInfo.put("numberOfTrail", "2");
		additionalInfo.put("serverTrailState", "activated");
		additionalInfo.put("protType", "None");

		NameAndStringValue_T[] additionalModifyInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalModifyInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCModifyData_T modifyData = new SNCModifyData_T();

		modifyData.aEnd = aEnd;
		modifyData.zEnd = zEnd;
		modifyData.modifyType = "increaseNewTrailsOnPath";
		modifyData.retainOldSNC = false;
		modifyData.addedOrNewRoute = addedOrNewRoute;
		modifyData.removedRoute = removedRoute;
		modifyData.additionalCreationInfo = additionalModifyInfo;
		modifyData.neTpInclusions = neTpInclusions;
		modifyData.neTpSncExclusions = neTpExclusions;
		modifyData.forceUniqueness = true;
		modifyData.fullRoute = false;
		modifyData.layerRate = layerRate;
		modifyData.networkRouted = NetworkRouted_T.NR_YES;
		modifyData.rerouteAllowed = Reroute_T.RR_NO;
		modifyData.direction = ConnectionDirection_T.CD_BI;
		modifyData.sncType = SNCType_T.ST_SIMPLE;
		modifyData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		modifyData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		modifyData.owner = owner;
		modifyData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.modifySNC(sncName, modifyData, tolerableImpact,
				ProtectionEffort_T.EFFORT_SAME, emsFreedomLevel, tpsToModify);
	}

	// Downgrade Ethernet service bandwidth
	public void modifySNCDowngrade() throws ProcessingFailureException {
		String sncID = "PATH_173";
		String userLabel = "NISA-EVC-10M-1";
		String owner = "";

		// Service Type = Ethernet
		short layerRate = 96;

		NameAndStringValue_T[] sncName = new NameAndStringValue_T[3];
		sncName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork", "SDH");
		sncName[2] = new NameAndStringValue_T("SubnetworkConnection", sncID);

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];

		// A-End - VNE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b29p07-gMAU");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b29p07-gMAU");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		RouteDescriptor_T[] addedOrNewRoute = new RouteDescriptor_T[0];
		RouteDescriptor_T[] removedRoute = new RouteDescriptor_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		// Decrease 2 trails. Initial bandwidth 14M i.e. 7 VC12 trails.
		// Final 7-2=5 trails i.e. 10M
		additionalInfo.put("numberOfTrail", "2");
		additionalInfo.put("enableDeactivate", "enabled");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCModifyData_T modifyData = new SNCModifyData_T();

		modifyData.aEnd = aEnd;
		modifyData.zEnd = zEnd;
		modifyData.modifyType = "decreaseIdleTrailsOnPath";
		modifyData.retainOldSNC = false;
		modifyData.addedOrNewRoute = addedOrNewRoute;
		modifyData.removedRoute = removedRoute;
		modifyData.additionalCreationInfo = additionalCreationInfo;
		modifyData.neTpInclusions = neTpInclusions;
		modifyData.neTpSncExclusions = neTpExclusions;
		modifyData.forceUniqueness = true;
		modifyData.fullRoute = false;
		modifyData.layerRate = layerRate;
		modifyData.networkRouted = NetworkRouted_T.NR_YES;
		modifyData.rerouteAllowed = Reroute_T.RR_NO;
		modifyData.direction = ConnectionDirection_T.CD_BI;
		modifyData.sncType = SNCType_T.ST_SIMPLE;
		modifyData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		modifyData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		modifyData.owner = owner;
		modifyData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.modifySNC(sncName, modifyData, tolerableImpact,
				ProtectionEffort_T.EFFORT_SAME, emsFreedomLevel, tpsToModify);
	}

	public void createE1WithVNE() throws ProcessingFailureException {
		String sncID = "NISA-E1-Path-VNE-1";
		String userLabel = "NISA-E1-Path-VNE-1";
		String owner = "";

		// 80 = LR_DSR_2M
		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];

		// A-End - ENE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End - ENE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/24");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

	public void createDS3WithVNE() throws ProcessingFailureException {
		String sncID = "NISA-DS3-Path-VNE-1";
		String userLabel = "NISA-DS3-Path-VNE-1";
		String owner = "";

		// 84 = LR_DSR_45M
		short layerRate = 84;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];

		// A-End - ENE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End - ENE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/24");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo45Mb");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

	public void createAU4WithNEtpInc() throws ProcessingFailureException {
		String sncID = "NISA-AU4-Path-neTpInclusions-1";
		String userLabel = "NISA-AU4-Path-neTpInclusions-1";
		String owner = "";

		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];

		// A-End - External Network (ENE)
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End - External Network (ENE)
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/24");

		// Route constraints as NE Ports
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[1][3];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b36p001");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "au4");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
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

	public void createE1WithSNCP() throws ProcessingFailureException {
		String sncID = "NISA-E1-SNCP-1";
		String userLabel = "NISA-E1-SNCP-1";
		String owner = "";

		// 80 = LR_DSR_2M
		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// E1
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b01p008c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b01p008c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "102/1");

		// E1
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC3/r01s1b01p018c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC3/r01s1b01p018c1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		// SNCP Service
		createData.staticProtectionLevel = StaticProtectionLevel_T.FULLY_PROTECTED;
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

	public void createE1WithSNCPWithNEtpInc() throws ProcessingFailureException {
		String sncID = "NISA-E1-SNCP-2";
		String userLabel = "NISA-E1-SNCP-2";
		String owner = "";

		// 80 = LR_DSR_2M
		short layerRate = 80;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/24");

		// Route constraints as NE Ports
		// Working Path
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[6][3];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"101/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b37p001");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b25p001");

		// Protection Path
		neTpInclusions[2][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[2][1] = new NameAndStringValue_T("ManagedElement",
				"101/1");
		neTpInclusions[2][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b25p001");

		neTpInclusions[3][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[3][1] = new NameAndStringValue_T("ManagedElement",
				"102/1");
		neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
				"NISA_CC3/r01s1b37p001");

		neTpInclusions[4][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[4][1] = new NameAndStringValue_T("ManagedElement",
				"102/1");
		neTpInclusions[4][2] = new NameAndStringValue_T("PTP",
				"NISA_CC3/r01s1b25p001");

		neTpInclusions[5][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[5][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[5][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b37p001");

		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");
		additionalInfo.put("INCLU2_SECTION", "main");
		additionalInfo.put("INCLU3_SECTION", "spare");
		additionalInfo.put("INCLU4_SECTION", "spare");
		additionalInfo.put("INCLU5_SECTION", "spare");
		additionalInfo.put("INCLU6_SECTION", "spare");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_YES;
		createData.rerouteAllowed = Reroute_T.RR_NO;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		// SNCP Service
		createData.staticProtectionLevel = StaticProtectionLevel_T.FULLY_PROTECTED;
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

	public void createVC4_4c() throws ProcessingFailureException {
		String sncID = "NISA-VC4_4c-VNE-1";
		String userLabel = "NISA-VC4_4c-VNE-1";
		String owner = "";

		// 16 = LR_STS12c_and_VC4_4c
		short layerRate = 16;

		// A-End - ENE
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End - ENE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/24");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "hoConc4");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createVC4_4cWithNEtpInc() throws ProcessingFailureException {
		String sncID = "NISA-VC4_4c-VNE-NETP-1";
		String userLabel = "NISA-VC4_4c-VNE-NETP-1";
		String owner = "";

		// 16 = LR_STS12c_and_VC4_4c
		short layerRate = 16;

		// A-End - NE
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "102/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC3/r01s1b01p004c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC3/r01s1b01p004c1 01");

		// Z-End - NE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b13p001c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b13p001c1 01");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];

		// Route constraints
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"102/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC3/r01s1b25p001");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b37p001");

		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "hoConc4");
		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");
		additionalInfo.put("INCLU2_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpExclusions;
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

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void createAu4ChannelizedWithE1() throws ProcessingFailureException {

		String sncID = "NISA-STM1-Channelized-VNE";
		String userLabel = "NISA-STM1-Channelized-VNE";
		String owner = "";

		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/164");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];

		// Route constraints as NE Ports
		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[8][4];
		// neTpInclusions[0][0] = new NameAndStringValue_T("EMS",
		// this.realEMSName);
		// neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
		// "101/1");
		// neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
		// "NISA_CC2/r01s1b37p001");
		// neTpInclusions[0][3] = new NameAndStringValue_T("CTP",
		// "NISA_CC2/r01s1b37p001 01/2/1.1");
		//
		// neTpInclusions[1][0] = new NameAndStringValue_T("EMS",
		// this.realEMSName);
		// neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
		// "101/1");
		// neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
		// "NISA_CC2/r01s1b27p001");
		// neTpInclusions[1][3] = new NameAndStringValue_T("CTP",
		// "NISA_CC2/r01s1b27p001 01/2/1.1");
		//
		// neTpInclusions[2][0] = new NameAndStringValue_T("EMS",
		// this.realEMSName);
		// neTpInclusions[2][1] = new NameAndStringValue_T("ManagedElement",
		// "103/1");
		// neTpInclusions[2][2] = new NameAndStringValue_T("PTP",
		// "NISA_CC1/r01s1b36p001");
		// neTpInclusions[2][3] = new NameAndStringValue_T("CTP",
		// "NISA_CC1/r01s1b36p001 01/2/1.1");
		//
		// neTpInclusions[3][0] = new NameAndStringValue_T("EMS",
		// this.realEMSName);
		// neTpInclusions[3][1] = new NameAndStringValue_T("ManagedElement",
		// "103/1");
		// neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
		// "NISA_CC1/r01s1b37p001");
		// neTpInclusions[3][3] = new NameAndStringValue_T("CTP",
		// "NISA_CC1/r01s1b37p001 01/2/1.1");
		//
		// neTpInclusions[4][0] = new NameAndStringValue_T("EMS",
		// this.realEMSName);
		// neTpInclusions[4][1] = new NameAndStringValue_T("ManagedElement",
		// "102/1");
		// neTpInclusions[4][2] = new NameAndStringValue_T("PTP",
		// "NISA_CC3/r01s1b25p001");
		// neTpInclusions[4][3] = new NameAndStringValue_T("CTP",
		// "NISA_CC3/r01s1b25p001 01/2/1.1");
		//
		// neTpInclusions[5][0] = new NameAndStringValue_T("EMS",
		// this.realEMSName);
		// neTpInclusions[5][1] = new NameAndStringValue_T("ManagedElement",
		// "101/1");
		// neTpInclusions[5][2] = new NameAndStringValue_T("PTP",
		// "NISA_CC2/r01s1b25p001");
		// neTpInclusions[5][3] = new NameAndStringValue_T("CTP",
		// "NISA_CC2/r01s1b25p001 01/2/1.1");
		//
		// neTpInclusions[6][0] = new NameAndStringValue_T("EMS",
		// this.realEMSName);
		// neTpInclusions[6][1] = new NameAndStringValue_T("ManagedElement",
		// "102/1");
		// neTpInclusions[6][2] = new NameAndStringValue_T("PTP",
		// "NISA_CC3/r01s1b37p001");
		// neTpInclusions[6][3] = new NameAndStringValue_T("CTP",
		// "NISA_CC3/r01s1b37p001 01/2/1.1");
		//
		// neTpInclusions[7][0] = new NameAndStringValue_T("EMS",
		// this.realEMSName);
		// neTpInclusions[7][1] = new NameAndStringValue_T("ManagedElement",
		// "101/1");
		// neTpInclusions[7][2] = new NameAndStringValue_T("PTP",
		// "NISA_CC2/r01s1b36p001");
		// neTpInclusions[7][3] = new NameAndStringValue_T("CTP",
		// "NISA_CC2/r01s1b36p001 01/2/1.1");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo2Mb");

		// Mandatory for neTpInclusions
		// additionalInfo.put("INCLU1_SECTION", "main");
		// additionalInfo.put("INCLU2_SECTION", "main");
		// additionalInfo.put("INCLU3_SECTION", "spare");
		// additionalInfo.put("INCLU4_SECTION", "spare");
		// additionalInfo.put("INCLU5_SECTION", "spare");
		// additionalInfo.put("INCLU6_SECTION", "spare");
		// additionalInfo.put("INCLU7_SECTION", "spare");
		// additionalInfo.put("INCLU8_SECTION", "spare");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.FULLY_PROTECTED;
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

	public void createAu4ChannelizedWithDS3() throws ProcessingFailureException {
		String sncID = "NISA-DS3-Channelized-VNE";
		String userLabel = "NISA-DS3-Channelized-VNE";
		String owner = "";

		short layerRate = 84;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][2];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/44");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][2];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/24");

		// NameAndStringValue_T[][] neTpInclusions = new
		// NameAndStringValue_T[0][0];

		// Route constraints as NE Ports
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[4][4];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b36p001");
		neTpInclusions[0][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b36p001 02/2");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"101/1");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b37p001");
		neTpInclusions[1][3] = new NameAndStringValue_T("CTP",
				"NISA_CC2/r01s1b37p001 06/2");

		neTpInclusions[2][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[2][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[2][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b25p001");
		neTpInclusions[2][3] = new NameAndStringValue_T("CTP",
				"NISA_CC1/r01s1b25p001 06/2");

		neTpInclusions[3][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[3][1] = new NameAndStringValue_T("ManagedElement",
				"101/1");
		neTpInclusions[3][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b27p001");
		neTpInclusions[3][3] = new NameAndStringValue_T("CTP",
				"NISA_CC2/r01s1b27p001 01/2");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "lo45Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");
		additionalInfo.put("INCLU2_SECTION", "main");
		additionalInfo.put("INCLU3_SECTION", "main");
		additionalInfo.put("INCLU4_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.FULLY_PROTECTED;
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

	public void createServerTrailNodeToNode() throws ProcessingFailureException {
		String sncID = "NISA-ServerTrail-1";
		String userLabel = "NISA-ServerTrail-1";
		String owner = "";

		short layerRate = 15;

		// A-End
		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b37p001");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b37p001 05");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");

		// SDH Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b25p001");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b25p001 05");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "ho140Mb");
		// additionalInfo.put("TRANSPORTRATE", "hoAu4");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
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

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void modifySNC(String sncID) {

		String userLabel = "NISA-ServerTrail-1";
		String owner = "";

		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End - VNE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b37p001");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b37p001 05");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b25p001");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b25p001 05");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		RouteDescriptor_T[] addedOrNewRoute = new RouteDescriptor_T[0];
		RouteDescriptor_T[] removedRoute = new RouteDescriptor_T[0];

		NameAndStringValue_T[] sncName = new NameAndStringValue_T[3];
		sncName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork", "SDH");
		sncName[2] = new NameAndStringValue_T("SubnetworkConnection", sncID);

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("payload", "tu12Tu12Tu3");

		NameAndStringValue_T[] additionalModifyInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalModifyInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCModifyData_T modifyData = new SNCModifyData_T();

		modifyData.aEnd = aEnd;
		modifyData.zEnd = zEnd;
		modifyData.modifyType = "modifyPayload";
		modifyData.retainOldSNC = false;
		modifyData.addedOrNewRoute = addedOrNewRoute;
		modifyData.removedRoute = removedRoute;
		modifyData.additionalCreationInfo = additionalModifyInfo;
		modifyData.neTpInclusions = neTpInclusions;
		modifyData.neTpSncExclusions = neTpExclusions;
		modifyData.forceUniqueness = true;
		modifyData.fullRoute = false;
		modifyData.layerRate = layerRate;
		modifyData.networkRouted = NetworkRouted_T.NR_NO;
		modifyData.rerouteAllowed = Reroute_T.RR_NO;
		modifyData.direction = ConnectionDirection_T.CD_BI;
		modifyData.sncType = SNCType_T.ST_SIMPLE;
		modifyData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		modifyData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		modifyData.owner = owner;
		modifyData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_RECONFIGURATION;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);

		try {
			cmd.modifySNC(sncName, modifyData, tolerableImpact,
					ProtectionEffort_T.EFFORT_SAME, emsFreedomLevel,
					tpsToModify);
		} catch (ProcessingFailureException e) {
			e.printStackTrace();
		}
	}

	public void createServerTrailWithNEtpInc()
			throws ProcessingFailureException {
		String sncID = "NISA-ServerTrail-2";
		String userLabel = "NISA-ServerTrail-2";
		String owner = "";

		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b37p001");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b37p001 06");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b25p001");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b25p001 06");

		// Route constraints as NE Ports
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][3];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"101/1");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"NISA_CC2/r01s1b37p001");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"NISA_CC1/r01s1b25p001");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "ho140Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");
		additionalInfo.put("INCLU2_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
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

	public void createServerTrailWithConstraintsTL()
			throws ProcessingFailureException {
		String sncID = "NISA-ServerTrail-3";
		String userLabel = "NISA-ServerTrail-3";
		String owner = "";

		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b37p001");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b37p001 03");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b25p001");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b25p001 04");

		// Route constraints as Topological Link
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[1][2];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("TopologicalLink",
				"CONNECT_23");

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "ho140Mb");

		// Mandatory for neTpInclusions
		additionalInfo.put("INCLU1_SECTION", "main");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
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

	// For Node to VNE server trail. VNE end point require VNE port and port
	// channel. However, Node to VNE Client trail does NOT need VNE port and
	// port channel.
	public void createServerTrailNodeToVNE() throws ProcessingFailureException {
		String sncID = "ExtCC1<->NisaCC1";
		String userLabel = "ExtCC1<->NisaCC1";
		String owner = "";

		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End - Node
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b31p001");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b31p001 01");

		// Z-End - VNE
		// For Node to VNE server trail. VNE end point require VNE port and port
		// channel. However, Node to VNE Client trail does NOT need VNE port and
		// port channel.
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "9000/105");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"Ext_NISA_CC1/EXT_NISA_CC1/S31/P1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"Ext_NISA_CC1/EXT_NISA_CC1/S31/P1 01");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];

		NameAndStringValue_T[][] neTpSncExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();
		additionalInfo.put("SNC_NAME", sncID);
		additionalInfo.put("SIGNALRATE", "ho140Mb");

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo
				.size()];
		Enumeration<String> keySet = additionalInfo.keys();

		for (int i = 0; keySet.hasMoreElements(); i++) {
			String name = (String) keySet.nextElement();
			String value = (String) additionalInfo.get(name);
			additionalCreationInfo[i] = new NameAndStringValue_T(name, value);
		}

		SNCCreateData_T createData = new SNCCreateData_T();

		createData.aEnd = aEnd;
		createData.zEnd = zEnd;
		createData.additionalCreationInfo = additionalCreationInfo;
		createData.neTpInclusions = neTpInclusions;
		createData.neTpSncExclusions = neTpSncExclusions;
		createData.ccInclusions = ccInclusions;
		createData.forceUniqueness = true;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_SAME;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}

	public void deactivateAndDeleteSNC() throws ProcessingFailureException {
		final String sncType = "path";
		List<String> sncIDsList = new ArrayList<String>();
		sncIDsList.add("PATH_702");
		// sncIDsList.add("PATH_28");

		for (String sncID : sncIDsList) {
			deactivateAndDeleteSNC(sncID, sncType);
		}

	}

	public void deactivateAndDeleteSNC(String[] sncIDs, String sncType)
			throws ProcessingFailureException {

		for (String sncID : sncIDs) {
			deactivateAndDeleteSNC(sncID, sncType);
		}

	}

	public void deactivateAndDeleteSNC(String sncID, String sncType)
			throws ProcessingFailureException {

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MAJOR_IMPACT;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);

		NameAndStringValue_T[] sncName = new NameAndStringValue_T[4];
		sncName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork", "SDH");
		sncName[2] = new NameAndStringValue_T("SubnetworkConnection", sncID);
		sncName[3] = new NameAndStringValue_T("sncType", sncType);

		cmd.deactivateAndDeleteSNC(sncName, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}
}
