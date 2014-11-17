package ex.corba.alu;

import java.util.Enumeration;
import java.util.Hashtable;

import multiLayerSubnetwork.EMSFreedomLevel_T;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import subnetworkConnection.CrossConnect_T;
import subnetworkConnection.GradesOfImpact_T;
import subnetworkConnection.NetworkRouted_T;
import subnetworkConnection.ProtectionEffort_T;
import subnetworkConnection.Reroute_T;
import subnetworkConnection.SNCCreateData_T;
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

			main.createPDHServiceE1();
			// main.createPDHServiceDS3();
			// main.createPDHServiceWithConstraintsNE();
			// main.createPDHServiceWithConstraintsST();
			// main.createPDHServiceSNCP();

			// Path for Ethernet services
			// main.create2MPathForEVC();

			// main.createSDHServiceWithConstraintsPort();

			// main.createServerTrail();
			// main.createServerTrailWithConstraintsPort();
			// main.createServerTrailWithConstraintsTL();

			// main.deactivateAndDeleteSNC();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession(emsSession);
		}
	}

	public void createPDHServiceE1() throws ProcessingFailureException {
		String sncID = "NISA-SDHService-1";
		String userLabel = "NISA-SDHService-1";
		String owner = "";

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

	public void createPDHServiceDS3() throws ProcessingFailureException {
		String sncID = "NISA-PDHService-DS3-1";
		String userLabel = "NISA-PDHService-DS3-1";
		String owner = "";

		// DS3
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

		// SDH Service
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

	public void createPDHServiceWithConstraintsNE()
			throws ProcessingFailureException {
		String sncID = "NISA-SDHService-2";
		String userLabel = "NISA-SDHService-2";
		String owner = "";

		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b01p014c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b01p014c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "102/1");

		// SDH Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC3/r01s1b01p020c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC3/r01s1b01p020c1");

		// Route constraints
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[1][2];
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"103/1");

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

	public void createPDHServiceWithConstraintsST()
			throws ProcessingFailureException {
		String sncID = "NISA-SDHService-3";
		String userLabel = "NISA-SDHService-3";
		String owner = "";

		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b01p004c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b01p004c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");

		// SDH Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC1/r01s1b01p002c1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC1/r01s1b01p002c1");

		// Route constraints as Server trail End points
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

	public void createSDHServiceWithConstraintsPort()
			throws ProcessingFailureException {
		String sncID = "NISA-SDHService-VC4-1";
		String userLabel = "NISA-SDHService-VC4-1";
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

	public void createPDHServiceSNCP() throws ProcessingFailureException {
		String sncID = "NISA-SDHServiceSNCP-1";
		String userLabel = "NISA-SDHServiceSNCP-1";
		String owner = "";

		short layerRate = 80;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b01p008c1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b01p008c1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "102/1");

		// SDH Service
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

	public void createServerTrail() throws ProcessingFailureException {
		String sncID = "NISA-ServerTrail-1";
		String userLabel = "NISA-ServerTrail-1";
		String owner = "";

		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
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

	public void createServerTrailWithConstraintsPort()
			throws ProcessingFailureException {
		String sncID = "NISA-ServerTrail-2";
		String userLabel = "NISA-ServerTrail-2";
		String owner = "";

		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "101/1");

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b37p001");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b37p001 06");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");

		// SDH Service
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

		// SDH Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "NISA_CC2/r01s1b37p001");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "NISA_CC2/r01s1b37p001 03");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "103/1");

		// SDH Service
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

	public void deactivateAndDeleteSNC() throws ProcessingFailureException {
		String sncID = "PATH_83";

		NameAndStringValue_T[] sncName = new NameAndStringValue_T[4];
		sncName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork", "SDH");
		sncName[2] = new NameAndStringValue_T("SubnetworkConnection", sncID);
		sncName[3] = new NameAndStringValue_T("sncType", "path");

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MAJOR_IMPACT;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);

		cmd.deactivateAndDeleteSNC(sncName, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}
}
