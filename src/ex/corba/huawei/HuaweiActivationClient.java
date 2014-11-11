package ex.corba.huawei;

import java.util.Enumeration;
import java.util.Hashtable;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netcracker.huawei.t2000.v200r002c01.emsSession.EmsSession_I;
import com.netcracker.huawei.t2000.v200r002c01.globaldefs.ConnectionDirection_T;
import com.netcracker.huawei.t2000.v200r002c01.globaldefs.NameAndStringValue_T;
import com.netcracker.huawei.t2000.v200r002c01.globaldefs.ProcessingFailureException;
import com.netcracker.huawei.t2000.v200r002c01.multiLayerSubnetwork.EMSFreedomLevel_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.CrossConnect_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.GradesOfImpact_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.NetworkRouted_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.ProtectionEffort_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.Reroute_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.SNCCreateData_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.SNCType_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.StaticProtectionLevel_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.TPDataList_THolder;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.TPData_T;

public class HuaweiActivationClient extends HuaweiConnection {
	public static final Logger LOG = LoggerFactory
			.getLogger(HuaweiActivationClient.class);

	protected static EmsSession_I emsSession;
	protected static StringHolder errorReason;

	public static void main(String[] args) {
		HuaweiActivationClient main = new HuaweiActivationClient();

		try {
			emsSession = main.openEmsSession(args);

			// main.createPDHServiceE1();
			// main.createE4Path();
			// main.createVC12Path();
			// main.createServerTrail();
			main.createVC4_16c();
			// main.deactivateAndDeleteSNC();
		} catch (ProcessingFailureException pfe) {
			LOG.error("errorReason:" + pfe.errorReason);
			pfe.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			main.closeEmsSession(emsSession);
		}
	}

	public void createPDHServiceE1() throws ProcessingFailureException {
		String userLabel = "NISA-PDHService-E1-1";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// PDH E1 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=9");
		// aEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/sts3c_au4-j=2/vt2_tu12-k=1-l=2-m=2");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=9");
		// zEnd[0][3] = new NameAndStringValue_T("CTP",
		// "/sts3c_au4-j=4/vt2_tu12-k=1-l=2-m=2");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

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
		createData.forceUniqueness = false;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_WHATEVER;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	// E4 path between Ethernet card (N3EFS4) SDH ports
	public void createE4Path() throws ProcessingFailureException {
		String userLabel = "NISA-E4-Path-1";
		String owner = "";

		// 8 = LR_E4_140M
		short layerRate = 8;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// PDH E1 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=4/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=4/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

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
		createData.forceUniqueness = false;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_WHATEVER;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	// VC12 path between Ethernet card (EGS4) SDH ports
	public void createVC12Path() throws ProcessingFailureException {
		String userLabel = "NISA-VC12-Path-1";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// VC12 Path
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// VC12 Path
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=2/vt2_tu12-k=3-l=1-m=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

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
		createData.forceUniqueness = false;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_WHATEVER;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	public void createServerTrail() throws ProcessingFailureException {
		String userLabel = "NISA-ServerTrail-1";
		String owner = "";

		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// Server Trail
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// Server Trail
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

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
		createData.forceUniqueness = false;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_WHATEVER;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	// Slot 6 has N1SLQ16 card
	public void createVC4_16c() throws ProcessingFailureException {
		String userLabel = "NISA-VC4_16c-1";
		String owner = "";

		// 17 = LR_STS48c_and_VC4_16c
		short layerRate = 17;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// VC4_16c circuit
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=6/domain=sdh/port=2");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// VC4_16c circuit
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=6/domain=sdh/port=2");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

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
		createData.forceUniqueness = false;
		createData.fullRoute = false;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_WHATEVER;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;// GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;// EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	public void deactivateAndDeleteSNC() throws ProcessingFailureException {
		String sncID = "2014-11-11 13:43:46 - 62-sdh";

		NameAndStringValue_T[] sncName = new NameAndStringValue_T[3];
		sncName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork", "1");
		sncName[2] = new NameAndStringValue_T("SubnetworkConnection", sncID);

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MAJOR_IMPACT;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.deactivateAndDeleteSNC(sncName, tolerableImpact, emsFreedomLevel,
				tpsToModify);
	}
}
