package ex.corba.huawei;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.huawei.u2000.HW_mstpService.HW_EthServiceCreateData_T;
import com.huawei.u2000.HW_mstpService.HW_EthServiceTP_T;
import com.huawei.u2000.HW_mstpService.HW_EthServiceType_T;
import com.huawei.u2000.emsSession.EmsSession_I;
import com.huawei.u2000.globaldefs.ConnectionDirection_T;
import com.huawei.u2000.globaldefs.NameAndStringValue_T;
import com.huawei.u2000.globaldefs.ProcessingFailureException;
import com.huawei.u2000.multiLayerSubnetwork.EMSFreedomLevel_T;
import com.huawei.u2000.subnetworkConnection.CrossConnect_T;
import com.huawei.u2000.subnetworkConnection.GradesOfImpact_T;
import com.huawei.u2000.subnetworkConnection.NetworkRouted_T;
import com.huawei.u2000.subnetworkConnection.ProtectionEffort_T;
import com.huawei.u2000.subnetworkConnection.Reroute_T;
import com.huawei.u2000.subnetworkConnection.SNCCreateData_T;
import com.huawei.u2000.subnetworkConnection.SNCType_T;
import com.huawei.u2000.subnetworkConnection.StaticProtectionLevel_T;
import com.huawei.u2000.subnetworkConnection.TPDataList_THolder;
import com.huawei.u2000.subnetworkConnection.TPData_T;
import com.huawei.u2000.terminationPoint.Directionality_T;
import com.huawei.u2000.transmissionParameters.LayeredParameters_T;

import ex.corba.huawei.transform.sax.Corba2XMLHandler;

public class HuaweiActivationClient extends HuaweiConnection {
	public static final Logger LOG = LoggerFactory.getLogger(HuaweiActivationClient.class);

	protected static EmsSession_I emsSession;
	protected static StringHolder errorReason;

	public static void main(String[] args) {
		HuaweiActivationClient main = new HuaweiActivationClient();

		try {
			emsSession = main.openEmsSession(args);

			// main.createE1();
			// main.createE3();
			// main.createE1WithVNE();
			// main.createE1withNEtpInc();
			// main.deactivateAndDeleteSNC();
			// main.createE1withCCInc();
			// main.createE3withCCInc();
			// main.createE4orVC4();
			// main.createE4orVC4withCCInc();
			// main.createE4withProtection();
			// main.createSTM1_1_plus_0();
			// main.createSTM1_1_plus_1();
			main.createSTM1_1_plus_1v2();
			main.getSNCsByUserLabelAndRoutes("NISA-STM1-1+1v2");

			// main.createServerTrail();

			// main.deactivateAndDeleteSNC();
			// main.createVC4_16c();
			// main.createVC4_16cWithCCInc();
			// main.createVC4_4cWithCCInc();

			/*
			 * SDH Path for Ethernet service
			 */
			// main.createVC12Path();
			// main.createVC3Path();
			// main.deactivateAndDeleteSNC();

			/*
			 * SDH Path on MSP 1+1
			 */
			// main.deactivateAndDeleteSNC("2016-04-26 13:14:48 - 1673-sdh");
			// main.createE1withCCIncOnMSP();
			// main.createVC3withCCIncOnMSP();
			// main.createSTM1withCCIncOnMSP();
			// main.createSncpE1withCCIncOnMSP();

			/*
			 * Ethernet service
			 */
			// main.createEthService();
			// main.delEthService();
			// main.addBindingPathVC12();
			// main.addBindingPathVC3();
			// main.addBindingPathVC12for4M();
			// main.configureEthernetPort();
			// main.configureVCTRUNKPort();

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

	public void createE1() throws ProcessingFailureException {
		String userLabel = "NISA-PDHService-E1-1";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// PDH E1 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=9");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=9");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createE3() throws ProcessingFailureException {
		String userLabel = "NISA-PDHService-E3-1";
		String owner = "";

		// 7 = LR_E3_34M
		short layerRate = 7;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// PDH E1 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=2/domain=sdh/port=3");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=2/domain=sdh/port=3");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
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
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=10");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End (VNE)
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=2");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=1-l=1-m=2");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
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
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=5");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=5");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// NE TP Inclusions
		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[2][4];

		neTpInclusions[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		neTpInclusions[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		neTpInclusions[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=1");

		neTpInclusions[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		neTpInclusions[1][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		neTpInclusions[1][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		neTpInclusions[1][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=1");

		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	// Slot 8 has SLQ4A board: 4xSTM-4 Optical Interface Board
	public void createE1withCCInc() throws ProcessingFailureException {
		String userLabel = "NISA-E1-3";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// PDH E1 Service
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=6");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// PDH E1 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=6");
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
		aEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		aEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=2");

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=6");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=2");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=6");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	// Slot 8 has SLQ4A board: 4xSTM-4 Optical Interface Board
	// Slot 2 has N2PQ3 board: 12xE3/T3 service processing board
	public void createE3withCCInc() throws ProcessingFailureException {
		String userLabel = "NISA-E3-3";
		String owner = "";

		// 7 = LR_E3_34M
		short layerRate = 7;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=2/domain=sdh/port=6");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=2/domain=sdh/port=6");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[2];
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		// NE 1 cross-connects
		NameAndStringValue_T[][] aEndCCList0 = new NameAndStringValue_T[1][4];
		aEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		aEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4/tu3_vc3-k=2");

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=2/domain=sdh/port=6");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4/tu3_vc3-k=2");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=2/domain=sdh/port=6");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
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
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// E4/VC4 path
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=11");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createE4orVC4withCCInc() throws ProcessingFailureException {
		String userLabel = "NISA-E4-2";
		String owner = "";

		// 8 = LR_E4_140M
		short layerRate = 8;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End (VNE)
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");

		// E4/VC4 path
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// E4/VC4 path
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=11");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[2];
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		// NE 1 cross-connects
		NameAndStringValue_T[][] aEndCCList0 = new NameAndStringValue_T[1][4];
		aEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		aEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4/tu3_vc3-k=2");

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=2/domain=sdh/port=6");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4/tu3_vc3-k=2");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=2/domain=sdh/port=6");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/tu3_vc3=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
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
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// E4/VC4 Service
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[4];

		// NE 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0 = new NameAndStringValue_T[1][4];
		aEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Protection Path: NE 1 cross-connects
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0p = new NameAndStringValue_T[1][4];
		aEndCCList0p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0p[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList0p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEndCCList0p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList0p = new NameAndStringValue_T[1][4];
		zEndCCList0p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0p[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList0p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEndCCList0p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4");

		ccInclusions[2].aEndNameList = aEndCCList0p;
		ccInclusions[2].zEndNameList = zEndCCList0p;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Protection Path: NE 2 cross-connects
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1p = new NameAndStringValue_T[1][4];
		aEndCCList1p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1p[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList1p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=7");
		aEndCCList1p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1p = new NameAndStringValue_T[1][4];
		zEndCCList1p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1p[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList1p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEndCCList1p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=4");

		ccInclusions[3].aEndNameList = aEndCCList1p;
		ccInclusions[3].zEndNameList = zEndCCList1p;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createSTM1_1_plus_0() throws ProcessingFailureException {
		String userLabel = "NISA-STM1-1+0";
		String owner = "";

		// 8 = LR_E4_140M
		short layerRate = 8;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End - VNE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=2");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		// aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145735");
		// aEnd[0][2] = new NameAndStringValue_T("PTP",
		// "/rack=1/shelf=1/slot=13/domain=sdh/port=2");
		// aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=5");

		// Z-End - VNE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145730");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=17/domain=sdh/port=2");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[4];
		// CrossConnect_T[] ccInclusions1 = new CrossConnect_T[0];

		// NE 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0 = zEnd;

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145730");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=18/domain=sdh/port=4");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=4");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 3 cross-connects
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList2 = new NameAndStringValue_T[1][4];
		aEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		aEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList2 = new NameAndStringValue_T[1][4];
		zEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[2].aEndNameList = aEndCCList2;
		ccInclusions[2].zEndNameList = zEndCCList2;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 4 cross-connects
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList3 = aEnd;

		NameAndStringValue_T[][] zEndCCList3 = new NameAndStringValue_T[1][4];
		zEndCCList3[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList3[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEndCCList3[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		zEndCCList3[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[3].aEndNameList = aEndCCList3;
		ccInclusions[3].zEndNameList = zEndCCList3;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createSTM1_1_plus_1() throws ProcessingFailureException {
		String userLabel = "NISA-STM1-1+1";
		String owner = "";

		// 8 = LR_E4_140M
		short layerRate = 8;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End - VNE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=2");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// Z-End - VNE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145730");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=17/domain=sdh/port=2");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[6];
		// CrossConnect_T[] ccInclusions1 = new CrossConnect_T[0];

		// NE 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0 = zEnd;

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145730");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=18/domain=sdh/port=4");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=4");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 3 cross-connects
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList2 = new NameAndStringValue_T[1][4];
		aEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		aEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList2 = new NameAndStringValue_T[1][4];
		zEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[2].aEndNameList = aEndCCList2;
		ccInclusions[2].zEndNameList = zEndCCList2;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 4 cross-connects
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList3 = aEnd;

		NameAndStringValue_T[][] zEndCCList3 = new NameAndStringValue_T[1][4];
		zEndCCList3[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList3[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEndCCList3[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		zEndCCList3[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[3].aEndNameList = aEndCCList3;
		ccInclusions[3].zEndNameList = zEndCCList3;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Protection Path: NE 1 cross-connects
		ccInclusions[4] = new CrossConnect_T();
		ccInclusions[4].active = false;
		ccInclusions[4].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[4].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0p = new NameAndStringValue_T[1][4];
		aEndCCList0p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0p[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList0p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=4");
		aEndCCList0p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList0p = new NameAndStringValue_T[1][4];
		zEndCCList0p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0p[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList0p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=28/domain=sdh/port=1");
		zEndCCList0p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[4].aEndNameList = aEndCCList0p;
		ccInclusions[4].zEndNameList = zEndCCList0p;
		ccInclusions[4].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[4].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Protection Path: NE 2 cross-connects
		ccInclusions[5] = new CrossConnect_T();
		ccInclusions[5].active = false;
		ccInclusions[5].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[5].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1p = new NameAndStringValue_T[1][4];
		aEndCCList1p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1p[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList1p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		aEndCCList1p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1p = new NameAndStringValue_T[1][4];
		zEndCCList1p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1p[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList1p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=1");
		zEndCCList1p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[5].aEndNameList = aEndCCList1p;
		ccInclusions[5].zEndNameList = zEndCCList1p;
		ccInclusions[5].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[5].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createSTM1_1_plus_1v2() throws ProcessingFailureException {
		String userLabel = "NISA-STM1-1+1v2";
		String owner = "";

		// 8 = LR_E4_140M
		short layerRate = 8;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End - VNE
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=2");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// Z-End - VNE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145730");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=17/domain=sdh/port=2");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[8];
		// CrossConnect_T[] ccInclusions1 = new CrossConnect_T[0];

		// NE 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0 = zEnd;

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145730");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=18/domain=sdh/port=4");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=4");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 3 cross-connects
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList2 = new NameAndStringValue_T[1][4];
		aEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		aEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList2 = new NameAndStringValue_T[1][4];
		zEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[2].aEndNameList = aEndCCList2;
		ccInclusions[2].zEndNameList = zEndCCList2;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 4 cross-connects
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList3 = aEnd;

		NameAndStringValue_T[][] zEndCCList3 = new NameAndStringValue_T[1][4];
		zEndCCList3[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList3[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEndCCList3[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		zEndCCList3[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[3].aEndNameList = aEndCCList3;
		ccInclusions[3].zEndNameList = zEndCCList3;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Protection Path: NE 1 cross-connects
		ccInclusions[4] = new CrossConnect_T();
		ccInclusions[4].active = false;
		ccInclusions[4].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[4].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0p = new NameAndStringValue_T[1][4];
		aEndCCList0p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0p[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList0p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=4");
		aEndCCList0p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList0p = new NameAndStringValue_T[1][4];
		zEndCCList0p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0p[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList0p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=28/domain=sdh/port=1");
		zEndCCList0p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[4].aEndNameList = aEndCCList0p;
		ccInclusions[4].zEndNameList = zEndCCList0p;
		ccInclusions[4].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[4].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Protection Path: NE 2 cross-connects
		ccInclusions[5] = new CrossConnect_T();
		ccInclusions[5].active = false;
		ccInclusions[5].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[5].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1p = new NameAndStringValue_T[1][4];
		aEndCCList1p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1p[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList1p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		aEndCCList1p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		NameAndStringValue_T[][] zEndCCList1p = new NameAndStringValue_T[1][4];
		zEndCCList1p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1p[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList1p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=1");
		zEndCCList1p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[5].aEndNameList = aEndCCList1p;
		ccInclusions[5].zEndNameList = zEndCCList1p;
		ccInclusions[5].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[5].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Protection Path: NE 3 cross-connects
		ccInclusions[6] = new CrossConnect_T();
		ccInclusions[6].active = false;
		ccInclusions[6].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[6].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList2p = zEnd;

		NameAndStringValue_T[][] zEndCCList2p = new NameAndStringValue_T[1][4];
		zEndCCList2p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList2p[0][1] = new NameAndStringValue_T("ManagedElement", "3145730");
		zEndCCList2p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=18/domain=sdh/port=4");
		zEndCCList2p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[6].aEndNameList = aEndCCList2p;
		ccInclusions[6].zEndNameList = zEndCCList2p;
		ccInclusions[6].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[6].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Protection Path: NE 4 cross-connects
		ccInclusions[7] = new CrossConnect_T();
		ccInclusions[7].active = false;
		ccInclusions[7].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[7].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList3p = aEnd;

		NameAndStringValue_T[][] zEndCCList3p = new NameAndStringValue_T[1][4];
		zEndCCList3p[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList3p[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEndCCList3p[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=1");
		zEndCCList3p[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1");

		ccInclusions[7].aEndNameList = aEndCCList3p;
		ccInclusions[7].zEndNameList = zEndCCList3p;
		ccInclusions[7].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[7].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	// Server trail (VC4)
	public void createServerTrail() throws ProcessingFailureException {
		String userLabel = "NISA-Server Trail-2";
		String owner = "";

		// 15 = LR_STS3c_and_AU4_VC4
		short layerRate = 15;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// Server Trail
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// Server Trail
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=3");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	// Slot 6 has N1SLQ16 card
	public void createVC4_16c() throws ProcessingFailureException {
		String userLabel = "NISA-VC4_16c-2";
		String owner = "";

		// 17 = LR_STS48c_and_VC4_16c
		short layerRate = 17;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// VC4_16c circuit
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// VC4_16c circuit
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	// Slot 6 has N1SLQ16 card
	public void createVC4_16cWithCCInc() throws ProcessingFailureException {
		String userLabel = "NISA-VC4_16c-1";
		String owner = "";

		// 17 = LR_STS48c_and_VC4_16c
		short layerRate = 17;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// VC4_16c circuit
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// VC4_16c circuit
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		// CC Inclusions
		// STM-64 MSB: PPD_5-1/S28P1 - HKC_H1-15/S11P1
		CrossConnect_T[] ccInclusions = new CrossConnect_T[2];

		// Node 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList = new NameAndStringValue_T[1][4];
		aEndCCList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=28/domain=sdh/port=1");
		aEndCCList[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		NameAndStringValue_T[][] zEndCCList = new NameAndStringValue_T[1][4];
		zEndCCList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEndCCList[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		ccInclusions[0].aEndNameList = aEndCCList;
		ccInclusions[0].zEndNameList = zEndCCList;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts48c_vc4_16c=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createVC4_4cWithCCInc() throws ProcessingFailureException {
		String userLabel = "NISA-VC4_4c-1";
		String owner = "";

		// 17 = LR_STS48c_and_VC4_16c
		short layerRate = 16;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// VC4_16c circuit
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=4");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");

		// VC4_16c circuit
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=2");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=1");

		// CC Inclusions
		// STM-64 MSB: PPD_5-1/S28P1 - HKC_H1-15/S11P1
		CrossConnect_T[] ccInclusions = new CrossConnect_T[2];

		// Node 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList = new NameAndStringValue_T[1][4];
		aEndCCList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		aEndCCList[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=4");
		aEndCCList[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=1");

		NameAndStringValue_T[][] zEndCCList = new NameAndStringValue_T[1][4];
		zEndCCList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");
		zEndCCList[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=28/domain=sdh/port=1");
		zEndCCList[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=17");

		ccInclusions[0].aEndNameList = aEndCCList;
		ccInclusions[0].zEndNameList = zEndCCList;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=11/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=17");

		NameAndStringValue_T[][] zEndCCList1 = new NameAndStringValue_T[1][4];
		zEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=7/domain=sdh/port=2");
		zEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts12c_vc4_4c=1");

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
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
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// VC12 Path
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=3-l=1-m=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
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

		// VC3 Path
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3/tu3_vc3-k=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145728");

		// VC3 Path
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3/tu3_vc3-k=1");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createE1withCCIncOnMSP() throws ProcessingFailureException {
		String userLabel = "NISA-E1-NV-MSP1+1-1";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=2");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=2-l=1-m=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[4];
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		// Node 1 cross-connect - 1
		NameAndStringValue_T[][] aEndCCList0 = aEnd;

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=2-l=1-m=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 2 cross-connect - 1
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=23/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=2-l=1-m=1");

		NameAndStringValue_T[][] zEndCCList1 = zEnd;

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 1 cross-connect - 2: Protected MSP path
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_UNI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList2 = aEnd;

		NameAndStringValue_T[][] zEndCCList2 = new NameAndStringValue_T[1][4];
		zEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=14/domain=sdh/port=1");
		zEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=2-l=1-m=1");

		ccInclusions[2].aEndNameList = aEndCCList2;
		ccInclusions[2].zEndNameList = zEndCCList2;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Node 2 cross-connect - 2: Protected MSP path
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_UNI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList5 = zEnd;

		NameAndStringValue_T[][] zEndCCList5 = new NameAndStringValue_T[1][4];
		zEndCCList5[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList5[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEndCCList5[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=24/domain=sdh/port=1");
		zEndCCList5[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=2-l=1-m=1");

		ccInclusions[3].aEndNameList = aEndCCList5;
		ccInclusions[3].zEndNameList = zEndCCList5;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createVC3withCCIncOnMSP() throws ProcessingFailureException {
		String userLabel = "NISA-E3-5";
		String owner = "";

		// 7 = LR_E3_34M
		short layerRate = 7;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End: Node
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/tu3_vc3-k=2");

		// Z-End: VNE
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/tu3_vc3-k=2");

		// CC Inclusions
		// CrossConnect_T[] ccInclusions = new CrossConnect_T[0];
		CrossConnect_T[] ccInclusions = new CrossConnect_T[4];

		// NE 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0 = new NameAndStringValue_T[1][4];
		aEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		aEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/tu3_vc3-k=2");

		NameAndStringValue_T[][] zEndCCList0 = aEnd;

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=23/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/tu3_vc3-k=2");

		NameAndStringValue_T[][] zEndCCList1 = zEnd;

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// NE 1 cross-connects - 2
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_UNI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList2 = new NameAndStringValue_T[1][4];
		aEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=14/domain=sdh/port=1");
		aEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/tu3_vc3-k=2");

		NameAndStringValue_T[][] zEndCCList2 = aEnd;

		ccInclusions[2].aEndNameList = aEndCCList2;
		ccInclusions[2].zEndNameList = zEndCCList2;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// NE 2 cross-connects - 2
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_UNI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList3 = new NameAndStringValue_T[1][4];
		aEndCCList3[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList3[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEndCCList3[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=24/domain=sdh/port=1");
		aEndCCList3[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/tu3_vc3-k=2");

		NameAndStringValue_T[][] zEndCCList3 = zEnd;

		ccInclusions[3].aEndNameList = aEndCCList3;
		ccInclusions[3].zEndNameList = zEndCCList3;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createSTM1withCCIncOnMSP() throws ProcessingFailureException {
		String userLabel = "NISA-E1-NV-MSP1+1-1";
		String owner = "";

		// 8 = LR_E1_2M
		short layerRate = 8;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=2");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=2-l=1-m=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[4];
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		// Node 1 cross-connect - 1
		NameAndStringValue_T[][] aEndCCList0 = aEnd;

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=2-l=1-m=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 2 cross-connect - 1
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=23/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=2-l=1-m=1");

		NameAndStringValue_T[][] zEndCCList1 = zEnd;

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 1 cross-connect - 2: Protected MSP path
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_UNI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList2 = aEnd;

		NameAndStringValue_T[][] zEndCCList2 = new NameAndStringValue_T[1][4];
		zEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=14/domain=sdh/port=1");
		zEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=2-l=1-m=1");

		ccInclusions[2].aEndNameList = aEndCCList2;
		ccInclusions[2].zEndNameList = zEndCCList2;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Node 2 cross-connect - 2: Protected MSP path
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_UNI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList5 = zEnd;

		NameAndStringValue_T[][] zEndCCList5 = new NameAndStringValue_T[1][4];
		zEndCCList5[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList5[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEndCCList5[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=24/domain=sdh/port=1");
		zEndCCList5[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=2-l=1-m=1");

		ccInclusions[3].aEndNameList = aEndCCList5;
		ccInclusions[3].zEndNameList = zEndCCList5;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createSncpE1withCCIncOnMSP() throws ProcessingFailureException {
		String userLabel = "NISA-E1-NV-MSP1_1-2";
		String owner = "";

		// 5 = LR_E1_2M
		short layerRate = 5;

		NameAndStringValue_T[][] aEnd = new NameAndStringValue_T[1][4];

		// A-End
		aEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		aEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=3/domain=sdh/port=1");
		aEnd[0][3] = new NameAndStringValue_T("CTP", "/vt2_tu12=1");

		// Z-End
		NameAndStringValue_T[][] zEnd = new NameAndStringValue_T[1][4];
		zEnd[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEnd[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEnd[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=1");
		zEnd[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=1-l=1-m=1");

		// CC Inclusions
		CrossConnect_T[] ccInclusions = new CrossConnect_T[6];

		// Node 1 cross-connects
		ccInclusions[0] = new CrossConnect_T();
		ccInclusions[0].active = false;
		ccInclusions[0].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[0].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList0 = aEnd;

		NameAndStringValue_T[][] zEndCCList0 = new NameAndStringValue_T[1][4];
		zEndCCList0[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList0[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList0[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=13/domain=sdh/port=1");
		zEndCCList0[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=1-l=1-m=1");

		ccInclusions[0].aEndNameList = aEndCCList0;
		ccInclusions[0].zEndNameList = zEndCCList0;
		ccInclusions[0].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[0].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 2 cross-connects
		ccInclusions[1] = new CrossConnect_T();
		ccInclusions[1].active = false;
		ccInclusions[1].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[1].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList1 = new NameAndStringValue_T[1][4];
		aEndCCList1[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList1[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEndCCList1[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=23/domain=sdh/port=1");
		aEndCCList1[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=1-l=1-m=1");

		NameAndStringValue_T[][] zEndCCList1 = zEnd;

		ccInclusions[1].aEndNameList = aEndCCList1;
		ccInclusions[1].zEndNameList = zEndCCList1;
		ccInclusions[1].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[1].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Work");

		// Node 1 cross-connect - 2
		ccInclusions[2] = new CrossConnect_T();
		ccInclusions[2].active = false;
		ccInclusions[2].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[2].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList2 = aEnd;

		NameAndStringValue_T[][] zEndCCList2 = new NameAndStringValue_T[1][4];
		zEndCCList2[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList2[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList2[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=8/domain=sdh/port=4");
		zEndCCList2[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=1-l=1-m=1");

		ccInclusions[2].aEndNameList = aEndCCList2;
		ccInclusions[2].zEndNameList = zEndCCList2;
		ccInclusions[2].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[2].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Node 1 cross-connect - 3
		ccInclusions[3] = new CrossConnect_T();
		ccInclusions[3].active = false;
		ccInclusions[3].direction = ConnectionDirection_T.CD_UNI;
		ccInclusions[3].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList3 = aEnd;

		NameAndStringValue_T[][] zEndCCList3 = new NameAndStringValue_T[1][4];
		zEndCCList3[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList3[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		zEndCCList3[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=14/domain=sdh/port=1");
		zEndCCList3[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=1-l=1-m=1");

		ccInclusions[3].aEndNameList = aEndCCList3;
		ccInclusions[3].zEndNameList = zEndCCList3;
		ccInclusions[3].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[3].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Node 2 cross-connect - 2
		ccInclusions[4] = new CrossConnect_T();
		ccInclusions[4].active = false;
		ccInclusions[4].direction = ConnectionDirection_T.CD_BI;
		ccInclusions[4].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList4 = new NameAndStringValue_T[1][4];
		aEndCCList4[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		aEndCCList4[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		aEndCCList4[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=18/domain=sdh/port=4");
		aEndCCList4[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=1-l=1-m=1");

		NameAndStringValue_T[][] zEndCCList4 = zEnd;

		ccInclusions[4].aEndNameList = aEndCCList4;
		ccInclusions[4].zEndNameList = zEndCCList4;
		ccInclusions[4].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[4].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		// Node 2 cross-connect - 3
		ccInclusions[5] = new CrossConnect_T();
		ccInclusions[5].active = false;
		ccInclusions[5].direction = ConnectionDirection_T.CD_UNI;
		ccInclusions[5].ccType = SNCType_T.ST_SIMPLE;

		NameAndStringValue_T[][] aEndCCList5 = zEnd;

		NameAndStringValue_T[][] zEndCCList5 = new NameAndStringValue_T[1][4];
		zEndCCList5[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		zEndCCList5[0][1] = new NameAndStringValue_T("ManagedElement", "3145732");
		zEndCCList5[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=24/domain=sdh/port=1");
		zEndCCList5[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=2/vt2_tu12-k=1-l=1-m=1");

		ccInclusions[5].aEndNameList = aEndCCList5;
		ccInclusions[5].zEndNameList = zEndCCList5;
		ccInclusions[5].additionalInfo = new NameAndStringValue_T[1];
		ccInclusions[5].additionalInfo[0] = new NameAndStringValue_T("ProtectionRole", "Protection");

		NameAndStringValue_T[][] neTpInclusions = new NameAndStringValue_T[0][0];
		NameAndStringValue_T[][] neTpExclusions = new NameAndStringValue_T[0][0];

		Hashtable<String, String> additionalInfo = new Hashtable<String, String>();

		NameAndStringValue_T[] additionalCreationInfo = new NameAndStringValue_T[additionalInfo.size()];
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
		errorReason = cmd.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void createEthService() throws ProcessingFailureException {
		HW_EthServiceCreateData_T createData = new HW_EthServiceCreateData_T();
		createData.serviceType = HW_EthServiceType_T.HW_EST_EPL;
		createData.direction = ConnectionDirection_T.CD_BI;
		createData.additionalInfo = new NameAndStringValue_T[0];
		createData.aEndPoint = new HW_EthServiceTP_T();

		NameAndStringValue_T[] ethernetPort = new NameAndStringValue_T[3];
		ethernetPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ethernetPort[1] = new NameAndStringValue_T("ManagedElement", "3145728");
		ethernetPort[2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=eth/type=mac/port=2");

		createData.aEndPoint.name = ethernetPort;
		// createData.aEndPoint.vlanID = 1;
		// createData.aEndPoint.tunnel = 0;
		// createData.aEndPoint.vc = 0;

		createData.aEndPoint.additionalInfo = new NameAndStringValue_T[0];
		createData.zEndPoint = new HW_EthServiceTP_T();

		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];
		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145728");
		vctrunkPort[2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=3");

		createData.zEndPoint.name = vctrunkPort;
		// createData.zEndPoint.vlanID = 1;
		// createData.zEndPoint.tunnel = 0;
		// createData.zEndPoint.vc = 0;
		createData.zEndPoint.additionalInfo = new NameAndStringValue_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.createEthService(createData);
	}

	public void addBindingPathVC12() throws ProcessingFailureException {
		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];
		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		vctrunkPort[2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=2");

		NameAndStringValue_T[][] pathList = new NameAndStringValue_T[1][4];

		// Bind VCTRUNK port to VC-12 SDH path which is created using method
		// createVC12Path
		pathList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		pathList[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		pathList[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		pathList[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=1");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.addBindingPath(vctrunkPort, Directionality_T.D_BIDIRECTIONAL, pathList);
	}

	public void addBindingPathVC3() throws ProcessingFailureException {
		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];
		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		vctrunkPort[2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=3");

		NameAndStringValue_T[][] pathList = new NameAndStringValue_T[1][4];

		// Bind VCTRUNK port to VC-3 SDH path which is created using method
		// createVC3Path
		pathList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		pathList[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		pathList[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		pathList[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=3/tu3_vc3-k=1");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.addBindingPath(vctrunkPort, Directionality_T.D_BIDIRECTIONAL, pathList);
	}

	// Bind 2 VC12 SDH Path for 4M Ethernet service
	public void addBindingPathVC12for4M() throws ProcessingFailureException {
		NameAndStringValue_T[] vctrunkPort = new NameAndStringValue_T[3];
		vctrunkPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		vctrunkPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		vctrunkPort[2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=4");

		NameAndStringValue_T[][] pathList = new NameAndStringValue_T[2][4];

		// Bind VCTRUNK port to 2x VC-12 SDH path which is created using method
		// createVC12Path
		pathList[0][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		pathList[0][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		pathList[0][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		pathList[0][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=2");

		pathList[1][0] = new NameAndStringValue_T("EMS", this.realEMSName);
		pathList[1][1] = new NameAndStringValue_T("ManagedElement", "3145729");
		pathList[1][2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=sdh/port=1");
		pathList[1][3] = new NameAndStringValue_T("CTP", "/sts3c_au4-j=1/vt2_tu12-k=3-l=1-m=3");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.addBindingPath(vctrunkPort, Directionality_T.D_BIDIRECTIONAL, pathList);
	}

	public void configureEthernetPort() throws ProcessingFailureException {
		NameAndStringValue_T[] ethernetPort = new NameAndStringValue_T[3];
		ethernetPort[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ethernetPort[1] = new NameAndStringValue_T("ManagedElement", "3145729");
		ethernetPort[2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=eth/type=mac/port=2");

		LayeredParameters_T[] parameters = new LayeredParameters_T[1];
		parameters[0] = new LayeredParameters_T();
		parameters[0].layer = 96;

		NameAndStringValue_T[] loopback = new NameAndStringValue_T[15];
		loopback[0] = new NameAndStringValue_T("Tag", "Tag Aware");
		loopback[1] = new NameAndStringValue_T("VlanID", "1");
		loopback[2] = new NameAndStringValue_T("VLanPriority", "0");
		loopback[3] = new NameAndStringValue_T("PortEnable", "Enable");
		// loopback[4] = new NameAndStringValue_T("WorkingMode", "Auto");
		loopback[4] = new NameAndStringValue_T("WorkingMode", "100MFullDuplex");
		loopback[5] = new NameAndStringValue_T("MaxPacketLength", "1522");
		loopback[6] = new NameAndStringValue_T("MACLoopBack", "NoLoopBack");
		loopback[7] = new NameAndStringValue_T("PHYLoopBack", "NoLoopBack");
		loopback[8] = new NameAndStringValue_T("NonAutoNegotiationFlowControlMode", "Disable");
		// loopback[8] = new NameAndStringValue_T(
		// "NonAutoNegotiationFlowControlMode", "Symmetric");
		loopback[9] = new NameAndStringValue_T("BroadcastMsgSuppress", "Disable");
		loopback[10] = new NameAndStringValue_T("BroadcastMsgSuppressThreshold", "0.3");
		// loopback[11] = new NameAndStringValue_T(
		// "AutoNegotiationFlowControlMode", "Disable");
		loopback[11] = new NameAndStringValue_T("AutoNegotiationFlowControlMode", "AutoNeg_Non-Symmetric");
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
		vctrunkPort[2] = new NameAndStringValue_T("PTP", "/rack=1/shelf=1/slot=5/domain=eth/type=mp/port=2");

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
		loopback[4] = new NameAndStringValue_T("EncapsulateFormat", "Stack Vlan"); // not
																					// getting
																					// set
		loopback[5] = new NameAndStringValue_T("LCASState", "Disable");
		loopback[6] = new NameAndStringValue_T("EntranceDetect", "Enable");

		parameters[0].transmissionParams = loopback;

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.setMstpEndPoint(vctrunkPort, parameters);
	}

	public void deactivateAndDeleteSNC() throws ProcessingFailureException {
		String sncID = "2015-07-31 05:01:33 - 1085-sdh";
		deactivateAndDeleteSNC(sncID);
	}

	public void deactivateAndDeleteSNC(String sncID) throws ProcessingFailureException {
		NameAndStringValue_T[] sncName = new NameAndStringValue_T[3];
		sncName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork", "1");
		sncName[2] = new NameAndStringValue_T("SubnetworkConnection", sncID);

		GradesOfImpact_T tolerableImpact = GradesOfImpact_T.GOI_MAJOR_IMPACT;
		EMSFreedomLevel_T emsFreedomLevel = EMSFreedomLevel_T.EMSFL_CC_AT_SNC_LAYER;

		TPDataList_THolder tpsToModify = new TPDataList_THolder();
		tpsToModify.value = new TPData_T[0];

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.deactivateAndDeleteSNC(sncName, tolerableImpact, emsFreedomLevel, tpsToModify);
	}

	public void delEthService() throws ProcessingFailureException {
		NameAndStringValue_T[] ethServiceName = new NameAndStringValue_T[3];
		ethServiceName[0] = new NameAndStringValue_T("EMS", this.realEMSName);
		ethServiceName[1] = new NameAndStringValue_T("ManagedElement", "3145728");
		ethServiceName[2] = new NameAndStringValue_T("EthService", "1/5/0/4");

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName);
		cmd.deleteEthService(ethServiceName);
	}

	public XMLWriter getXMLWriter() throws UnsupportedEncodingException, FileNotFoundException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		Calendar calendar = Calendar.getInstance();
		Date currentDate = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		XMLWriter xmlWriter = new XMLWriter(new FileOutputStream("huawei-part-" + sdf.format(currentDate) + ".xml"),
				format);

		return xmlWriter;
	}

	public void getSNCsByUserLabelAndRoutes(final String userLabel)
			throws ProcessingFailureException, SAXException, UnsupportedEncodingException, FileNotFoundException {

		XMLWriter xmlWriter = getXMLWriter();

		Corba2XMLHandler handler = new Corba2XMLHandler(xmlWriter);

		CorbaCommands cmd = new CorbaCommands(emsSession, this.realEMSName, xmlWriter);
		handler.handlerBuilderStart();
		cmd.getSNCsByUserLabelAndRoutes(userLabel);
		handler.handlerBuilderEnd();
	}
}
