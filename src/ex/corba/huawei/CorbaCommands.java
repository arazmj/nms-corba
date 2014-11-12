package ex.corba.huawei;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netcracker.huawei.t2000.v200r002c01.HW_mstpInventory.HW_MSTPBindingPathList_THolder;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpInventory.HW_MSTPEndPoint_THolder;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpInventory.HW_MSTPInventoryMgr_I;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpInventory.HW_MSTPInventoryMgr_IHelper;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpService.HW_EthServiceCreateData_T;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpService.HW_EthServiceList_THolder;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpService.HW_EthService_T;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpService.HW_MSTPServiceMgr_I;
import com.netcracker.huawei.t2000.v200r002c01.HW_mstpService.HW_MSTPServiceMgr_IHelper;
import com.netcracker.huawei.t2000.v200r002c01.common.Common_IHolder;
import com.netcracker.huawei.t2000.v200r002c01.emsMgr.EMSMgr_I;
import com.netcracker.huawei.t2000.v200r002c01.emsMgr.EMSMgr_IHelper;
import com.netcracker.huawei.t2000.v200r002c01.emsSession.EmsSession_I;
import com.netcracker.huawei.t2000.v200r002c01.equipment.EquipmentInventoryMgr_I;
import com.netcracker.huawei.t2000.v200r002c01.equipment.EquipmentInventoryMgr_IHelper;
import com.netcracker.huawei.t2000.v200r002c01.globaldefs.NameAndStringValue_T;
import com.netcracker.huawei.t2000.v200r002c01.globaldefs.ProcessingFailureException;
import com.netcracker.huawei.t2000.v200r002c01.managedElementManager.ManagedElementMgr_I;
import com.netcracker.huawei.t2000.v200r002c01.managedElementManager.ManagedElementMgr_IHelper;
import com.netcracker.huawei.t2000.v200r002c01.multiLayerSubnetwork.EMSFreedomLevel_T;
import com.netcracker.huawei.t2000.v200r002c01.multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import com.netcracker.huawei.t2000.v200r002c01.multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.GradesOfImpact_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.SNCCreateData_T;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.SubnetworkConnection_THolder;
import com.netcracker.huawei.t2000.v200r002c01.subnetworkConnection.TPDataList_THolder;
import com.netcracker.huawei.t2000.v200r002c01.terminationPoint.Directionality_T;
import com.netcracker.huawei.t2000.v200r002c01.transmissionParameters.LayeredParameters_T;

public class CorbaCommands {
	public static final String ME_MANAGER_NAME = "ManagedElement";
	public static final String EI_MANAGER_NAME = "EquipmentInventory";
	public static final String MLS_MANAGER_NAME = "MultiLayerSubnetwork";
	public static final String EMS_MANAGER_NAME = "EMS";
	public static final String MSTP_SERVICE_MANAGER_NAME = "CORBA_MSTP_SVC";
	public static final String MSTP_INVENTORY_MANAGER_NAME = "CORBA_MSTP_INV";

	public static final int HOW_MANY = 100;

	public static final Logger LOG = LoggerFactory
			.getLogger(CorbaCommands.class);

	private EmsSession_I emsSession;
	private String emsName;

	private Common_IHolder managerInterface;
	private ManagedElementMgr_I meManager;
	private EquipmentInventoryMgr_I eiManager;
	private MultiLayerSubnetworkMgr_I mlsnManager;
	private EMSMgr_I emsManager;
	private HW_MSTPServiceMgr_I mstpServiceManager;
	private HW_MSTPInventoryMgr_I mstpInvertoryManager;

	public CorbaCommands(EmsSession_I emsSession, String emsName) {
		this.emsSession = emsSession;
		this.emsName = emsName;
	}

	public boolean setManagerByName(final String managerName)
			throws ProcessingFailureException {

		this.managerInterface = new Common_IHolder();
		this.emsSession.getManager(managerName, this.managerInterface);

		if (managerName.equals(ME_MANAGER_NAME)) {
			if (this.meManager == null) {
				this.meManager = ManagedElementMgr_IHelper
						.narrow(managerInterface.value);
			}
		} else if (managerName.equals(EI_MANAGER_NAME)) {
			if (this.eiManager == null) {
				this.eiManager = EquipmentInventoryMgr_IHelper
						.narrow(managerInterface.value);
			}
		} else if (managerName.equals(MLS_MANAGER_NAME)) {
			if (this.mlsnManager == null) {
				this.mlsnManager = MultiLayerSubnetworkMgr_IHelper
						.narrow(managerInterface.value);
			}
		} else if (managerName.equals(EMS_MANAGER_NAME)) {
			if (this.emsManager == null) {
				this.emsManager = EMSMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(MSTP_SERVICE_MANAGER_NAME)) {
			if (this.mstpServiceManager == null) {
				this.mstpServiceManager = HW_MSTPServiceMgr_IHelper
						.narrow(managerInterface.value);
			}
		} else if (managerName.equals(MSTP_INVENTORY_MANAGER_NAME)) {
			if (this.mstpInvertoryManager == null) {
				this.mstpInvertoryManager = HW_MSTPInventoryMgr_IHelper
						.narrow(managerInterface.value);
			}
		} else
			return false;

		return true;
	}

	public StringHolder createAndActivateSNC(SNCCreateData_T createData,
			GradesOfImpact_T tolerableImpact,
			EMSFreedomLevel_T emsFreedomLevel, TPDataList_THolder tpsToModify)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("createAndActivateSNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		SubnetworkConnection_THolder theSNC = new SubnetworkConnection_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.createAndActivateSNC(createData, tolerableImpact,
				emsFreedomLevel, tpsToModify, theSNC, errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info("createAndActivateSNC() complete.");
		}

		return errorReason;
	}

	public StringHolder deactivateAndDeleteSNC(NameAndStringValue_T[] sncName,
			GradesOfImpact_T tolerableImpact,
			EMSFreedomLevel_T emsFreedomLevel, TPDataList_THolder tpsToModify)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("deactivateAndDeleteSNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		SubnetworkConnection_THolder theSNC = new SubnetworkConnection_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.deactivateAndDeleteSNC(sncName, tolerableImpact,
				emsFreedomLevel, tpsToModify, theSNC, errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info(" deactivateAndDeleteSNC() complete.");
		}

		return errorReason;
	}

	public HW_EthServiceList_THolder createEthService(
			HW_EthServiceCreateData_T createData)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("createEthService() start.");
		}

		if (!setManagerByName(MSTP_SERVICE_MANAGER_NAME))
			return null;

		HW_EthServiceList_THolder ethServiceList = new HW_EthServiceList_THolder();

		this.mstpServiceManager.createEthService(createData, ethServiceList);

		if (ethServiceList.value != null) {
			LOG.info("Created Ethernet Services: "
					+ ethServiceList.value.length);

			for (int i = 0; i < ethServiceList.value.length; i++) {
				HW_EthService_T service = ethServiceList.value[i];
				LOG.info("Name : " + service.name[2].value);
				LOG.info("NativeEMSName : " + service.nativeEMSName);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("createEthService() complete.");
		}

		return ethServiceList;
	}

	public void deleteEthService(NameAndStringValue_T[] name)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("createEthService() start.");
		}

		if (!setManagerByName(MSTP_SERVICE_MANAGER_NAME))
			return;

		this.mstpServiceManager.deleteEthService(name);

		if (LOG.isInfoEnabled()) {
			LOG.info("createEthService() complete.");
		}
	}

	public HW_MSTPBindingPathList_THolder addBindingPath(
			NameAndStringValue_T[] vctrunkPort, Directionality_T directionlity,
			NameAndStringValue_T[][] pathList)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("addBindingPath() start.");
		}

		if (!setManagerByName(MSTP_INVENTORY_MANAGER_NAME))
			return null;

		HW_MSTPBindingPathList_THolder pathListHolder = new HW_MSTPBindingPathList_THolder();

		this.mstpInvertoryManager.addBindingPath(vctrunkPort, directionlity,
				pathList, pathListHolder);

		return pathListHolder;
	}

	public HW_MSTPBindingPathList_THolder delBindingPath(
			NameAndStringValue_T[] vctrunkPort, Directionality_T directionlity,
			NameAndStringValue_T[][] pathList)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("addBindingPath() start.");
		}

		if (!setManagerByName(MSTP_INVENTORY_MANAGER_NAME))
			return null;

		HW_MSTPBindingPathList_THolder pathListHolder = new HW_MSTPBindingPathList_THolder();

		this.mstpInvertoryManager.delBindingPath(vctrunkPort, directionlity,
				pathList, pathListHolder);

		return pathListHolder;
	}

	public HW_MSTPEndPoint_THolder setMstpEndPoint(
			NameAndStringValue_T[] endPointName, LayeredParameters_T[] paraList)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("setMstpEndPoint() start.");
		}

		if (!setManagerByName(MSTP_INVENTORY_MANAGER_NAME))
			return null;

		HW_MSTPEndPoint_THolder endPoint = new HW_MSTPEndPoint_THolder();

		this.mstpInvertoryManager.setMstpEndPoint(endPointName, paraList,
				endPoint);

		if (LOG.isInfoEnabled()) {
			LOG.info("setMstpEndPoint() complete: out {}", endPoint.value);
		}

		return endPoint;
	}
}
