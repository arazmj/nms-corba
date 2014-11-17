package ex.corba.huawei;

import java.util.Enumeration;
import java.util.Hashtable;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netcracker.huawei.t2000.v200r002c01.HW_mstpService.HW_EthServiceCreateData_T;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpService.HW_EthServiceTP_T;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpService.HW_EthServiceType_T;
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
import com.netcracker.huawei.t2000.v200r002c01.terminationPoint.Directionality_T;
import com.netcracker.huawei.t2000.v200r002c01.transmissionParameters.LayeredParameters_T;

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
			// main.createPDHServiceE3();
			// main.createE1WithVNE();
			// main.createE1withNEtpInc();
			// main.createE1withCCInc();
			// main.createE4orVC4();
			// main.createE4withProtection();
			// main.createServerTrail();
			// main.createVC4_16c();

			// SDH Path for Ethernet service
			// main.createVC12Path();
			// main.createVC3Path();

			// main.deactivateAndDeleteSNC();

			// main.createEthService();
			// main.addBindingPathVC12();
			// main.addBindingPathVC3();
			main.addBindingPathVC12for4M();
			// main.configureEthernetPort();
			// main.configureVCTRUNKPort();
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
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=9");
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

	public void createPDHServiceE3() throws ProcessingFailureException {
		String userLabel = "NISA-PDHService-E3-1";
		String owner = "";

		// 7 = LR_E3_34M
		short layerRate = 7;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// PDH E1 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=2/domain=sdh/port=3");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=2/domain=sdh/port=3");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

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

	public void createE1WithVNE() throws ProcessingFailureException {
		String userLabel = "NISA-E1-VNE-1";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=10");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End (VNE)
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=2");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=1-l=1-m=2");

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

	// Slot 8 has SLQ4A board: 4xSTM-4 Optical Interface Board
	public void createE1withNEtpInc() throws ProcessingFailureException {
		String userLabel = "NISA-PDHService-E1-2";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// PDH E1 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=5");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=5");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][4];

		// NE TP Inclusions
		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145729");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		neTpInclusions[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement",
				"3145728");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		neTpInclusions[1][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=1");

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

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	// Slot 8 has SLQ4A board: 4xSTM-4 Optical Interface Board
	public void createE1withCCInc() throws ProcessingFailureException {
		String userLabel = "NISA-PDHService-E1-3";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// PDH E1 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=6");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=6");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[2];
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		// Node 1 cross-connects
		NameAndStringValue_T[][] aEndCCList0 = new NameAndStringValue_T[1][4];
		aEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145729");
		aEndCCList0[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		aEndCCList0[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=2");

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=6");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T(
				"ProtectionRole", "Work");

		// Node 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=2");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=3/domain=sdh/port=6");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T(
				"ProtectionRole", "Work");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

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
		// fullRoute should be true for ccInclusions
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.UNPROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_WHATEVER;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	// E4 path between Ethernet card (N3EFS4) SDH ports
	public void createE4orVC4() throws ProcessingFailureException {
		String userLabel = "NISA-E4-1";
		String owner = "";

		// 8 = LR_E4_140M
		short layerRate = 8;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End (VNE)
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");

		// E4/VC4 path
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// E4/VC4 path
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=11/domain=sdh/port=11");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

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

	// Slot 8 has SLQ4A board: 4xSTM-4 Optical Interface Board
	// Slot 11 has N1SLT1 board: 12xSTM-1 Optical Interface Board
	public void createE4withProtection() throws ProcessingFailureException {
		String userLabel = "NISA-E4-Prot-1";
		String owner = "";

		// 8 = LR_E4_140M
		short layerRate = 8;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// E4/VC4 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// E4/VC4 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[4];

		// Node 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0 = new NameAndStringValue_T[1][4];
		aEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145729");
		aEndCCList0[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T(
				"ProtectionRole", "Work");

		// Node 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T(
				"ProtectionRole", "Work");

		// Protection Path: Node 1 cross-connects
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0p = new NameAndStringValue_T[1][4];
		aEndCCList0p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0p[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145729");
		aEndCCList0p[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEndCCList0p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList0p = new NameAndStringValue_T[1][4];
		zEndCCList0p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0p[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145729");
		zEndCCList0p[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEndCCList0p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4");

		ccInclusions[2].aEndNameList = aEndCCList0p;
		ccInclusions[2].zEndNameList = zEndCCList0p;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T(
				"ProtectionRole", "Protection");

		// Protection Path: Node 2 cross-connects
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1p = new NameAndStringValue_T[1][4];
		aEndCCList1p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1p[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145728");
		aEndCCList1p[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEndCCList1p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1p = new NameAndStringValue_T[1][4];
		zEndCCList1p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1p[0][1] = new NameAndStringValue_T("ManagedElement",
				"3145728");
		zEndCCList1p[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEndCCList1p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4");

		ccInclusions[3].aEndNameList = aEndCCList1p;
		ccInclusions[3].zEndNameList = zEndCCList1p;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T(
				"ProtectionRole", "Protection");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

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
		// fullRoute should be true for ccInclusions
		createData.fullRoute = true;
		createData.layerRate = layerRate;
		createData.networkRouted = NetworkRouted_T.NR_NA;
		createData.rerouteAllowed = Reroute_T.RR_NA;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.sncType = SNCType_T.ST_SIMPLE;
		createData.staticProtectionLevel = StaticProtectionLevel_T.FULLY_PROTECTED;
		createData.protectionEffort = ProtectionEffort_T.EFFORT_WHATEVER;
		createData.owner = owner;
		createData.userLabel = userLabel;

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	// Server trail (VC4)
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

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_HITLESS;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify);
	}

	// VC12 path between Ethernet card (EGS4) SDH port for Ethernet service
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

	// VC3 path between Ethernet card (EGS4) SDH ports for Ethernet service
	public void createVC3Path() throws ProcessingFailureException {
		String userLabel = "NISA-VC3-Path-1";
		String owner = "";

		// 7 = LR_E3_34M
		short layerRate = 7;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// VC12 Path
		aEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=3/tu3_vc3-k=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// VC12 Path
		zEnd[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=3/tu3_vc3-k=1");

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

	public void createEthService() throws ProcessingFailureException {
		HW_EthServiceCreateData_T createData = new HW_EthServiceCreateData_T();

		createData.serviceType = HW_EthServiceType_T.HW_EST_EPL;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.additionalInfo = new NameAndStringValue_T[0];

		createData.aEndPoint = new HW_EthServiceTP_T();

		NameAndStringValue_T[] ethernetPort = new NameAndStringValue_T[3];
		ethernetPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ethernetPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		ethernetPort[2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=eth/type=mac/port=2");

		createData.aEndPoint.name = ethernetPort;
		createData.aEndPoint.vlanID = 1;
		createData.aEndPoint.tunnel = 0;
		createData.aEndPoint.vc = 0;

		createData.aEndPoint.additionalInfo = new NameAndStringValue_T[0];

		createData.zEndPoint = new HW_EthServiceTP_T();

		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];

		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		vctrunkPort[2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=2");

		createData.zEndPoint.name = vctrunkPort;
		createData.zEndPoint.vlanID = 1;
		createData.zEndPoint.tunnel = 0;
		createData.zEndPoint.vc = 0;

		createData.zEndPoint.additionalInfo = new NameAndStringValue_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createEthService(createData);
	}

	public void addBindingPathVC12() throws ProcessingFailureException {
		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];
		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		vctrunkPort[2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=2");

		NameAndStringValue_T[][] pathList = new NameAndStringValue_T[1][4];

		// Bind VCTRUNK port to VC-12 SDH path which is created using method
		// createVC12Path
		pathList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		pathList[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		pathList[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		pathList[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=1");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.addBindingPath(vctrunkPort, Directionality_T.D_BIDIRECTIONAL,
				pathList);
	}

	public void addBindingPathVC3() throws ProcessingFailureException {
		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];
		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		vctrunkPort[2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=3");

		NameAndStringValue_T[][] pathList = new NameAndStringValue_T[1][4];

		// Bind VCTRUNK port to VC-3 SDH path which is created using method
		// createVC3Path
		pathList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		pathList[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		pathList[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		pathList[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=3/tu3_vc3-k=1");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.addBindingPath(vctrunkPort, Directionality_T.D_BIDIRECTIONAL,
				pathList);
	}

	// Bind 2 VC12 SDH Path for 4M Ethernet service
	public void addBindingPathVC12for4M() throws ProcessingFailureException {
		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];
		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		vctrunkPort[2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=4");

		NameAndStringValue_T[][] pathList = new NameAndStringValue_T[2][4];

		// Bind VCTRUNK port to 2x VC-12 SDH path which is created using method
		// createVC12Path
		pathList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		pathList[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		pathList[0][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		pathList[0][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=2");

		pathList[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		pathList[1][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		pathList[1][2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		pathList[1][3] = new NameAndStringValue_T("CTP",
				"/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=3");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.addBindingPath(vctrunkPort, Directionality_T.D_BIDIRECTIONAL,
				pathList);
	}

	public void configureEthernetPort() throws ProcessingFailureException {
		NameAndStringValue_T[] ethernetPort = new NameAndStringValue_T[3];
		ethernetPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ethernetPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		ethernetPort[2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=eth/type=mac/port=2");

		LayeredParameters_T[] parameters = new LayeredParameters_T[1];
		parameters[0] = new LayeredParameters_T();
		parameters[0].layer = 96;

		NameAndStringValue_T[] loopback = new NameAndStringValue_T[15];
		loopback[0] = new NameAndStringValue_T("Tag", "Tag Aware");
		loopback[1] = new NameAndStringValue_T("VlanID", "1");
		loopback[2] = new NameAndStringValue_T("VLanPriority", "0");
		loopback[3] = new NameAndStringValue_T("PortEnable", "Enable");
		loopback[4] = new NameAndStringValue_T("WorkingMode", "Auto");
		loopback[5] = new NameAndStringValue_T("MaxPacketLength", "1522");
		loopback[6] = new NameAndStringValue_T("MACLoopBack", "NoLoopBack");
		loopback[7] = new NameAndStringValue_T("PHYLoopBack", "NoLoopBack");
		loopback[8] = new NameAndStringValue_T(
				"NonAutoNegotiationFlowControlMode", "Disable");
		loopback[9] = new NameAndStringValue_T("BroadcastMsgSuppress",
				"Disable");
		loopback[10] = new NameAndStringValue_T(
				"BroadcastMsgSuppressThreshold", "0.3");
		loopback[11] = new NameAndStringValue_T(
				"AutoNegotiationFlowControlMode", "Disable");
		loopback[12] = new NameAndStringValue_T("PortType", "PE");
		loopback[13] = new NameAndStringValue_T("EntranceDetect", "Enable");
		loopback[14] = new NameAndStringValue_T("PortState", "OOS-AU");

		parameters[0].transmissionParams = loopback;

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.setMstpEndPoint(ethernetPort, parameters);
	}

	public void configureVCTRUNKPort() throws ProcessingFailureException {
		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];
		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		vctrunkPort[2] = new NameAndStringValue_T("PTP",
				"/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=2");

		LayeredParameters_T[] parameters = new LayeredParameters_T[1];
		parameters[0] = new LayeredParameters_T();
		parameters[0].layer = 96;

		NameAndStringValue_T[] loopback = new NameAndStringValue_T[7];
		loopback[0] = new NameAndStringValue_T("Tag", "Tag Aware");
		// loopback[0] = new NameAndStringValue_T("VlanID","1");
		loopback[1] = new NameAndStringValue_T("CheckFieldLength", "No");
		// Alway gets Set to Big endian
		loopback[2] = new NameAndStringValue_T("FCSCalculateSeq", "Big endian");
		loopback[3] = new NameAndStringValue_T("PortType", "CAWARE");
		loopback[4] = new NameAndStringValue_T("EncapsulateFormat",
				"Stack Vlan"); // not getting set
		loopback[5] = new NameAndStringValue_T("LCASState", "Disable");
		loopback[6] = new NameAndStringValue_T("EntranceDetect", "Disable");

		parameters[0].transmissionParams = loopback;

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.setMstpEndPoint(vctrunkPort, parameters);
	}

	public void deactivateAndDeleteSNC() throws ProcessingFailureException {
		String sncID = "2014-11-11 13:20:55 - 61-sdh";

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

	public void delEthService() throws ProcessingFailureException {
		NameAndStringValue_T[] ethServiceName = new NameAndStringValue_T[3];
		ethServiceName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ethServiceName[1] = new NameAndStringValue_T("ManagedElement",
				"3145729");
		ethServiceName[2] = new NameAndStringValue_T("EthService", "1/4/0/1");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.deleteEthService(ethServiceName);
	}
}
