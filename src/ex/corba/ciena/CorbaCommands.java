package ex.corba.ciena;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.netcracker.ciena.oncenter.v11.common.Common_IHolder;
import com.netcracker.ciena.oncenter.v11.emsMgr.EMSMgr_I;
import com.netcracker.ciena.oncenter.v11.emsMgr.EMSMgr_IHelper;
import com.netcracker.ciena.oncenter.v11.emsSession.EmsSession_I;
import com.netcracker.ciena.oncenter.v11.equipment.EquipmentInventoryMgr_I;
import com.netcracker.ciena.oncenter.v11.equipment.EquipmentInventoryMgr_IHelper;
import com.netcracker.ciena.oncenter.v11.globaldefs.NameAndStringValue_T;
import com.netcracker.ciena.oncenter.v11.globaldefs.NamingAttributesIterator_IHolder;
import com.netcracker.ciena.oncenter.v11.globaldefs.NamingAttributesList_THolder;
import com.netcracker.ciena.oncenter.v11.globaldefs.ProcessingFailureException;
import com.netcracker.ciena.oncenter.v11.managedElementManager.ManagedElementMgr_I;
import com.netcracker.ciena.oncenter.v11.managedElementManager.ManagedElementMgr_IHelper;
import com.netcracker.ciena.oncenter.v11.multiLayerSubnetwork.EMSFreedomLevel_T;
import com.netcracker.ciena.oncenter.v11.multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import com.netcracker.ciena.oncenter.v11.multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import com.netcracker.ciena.oncenter.v11.subnetworkConnection.GradesOfImpact_T;
import com.netcracker.ciena.oncenter.v11.subnetworkConnection.SNCCreateData_T;
import com.netcracker.ciena.oncenter.v11.subnetworkConnection.SubnetworkConnection_THolder;
import com.netcracker.ciena.oncenter.v11.subnetworkConnection.TPDataList_THolder;

public class CorbaCommands {
	public static final String ME_MANAGER_NAME = "ManagedElement";
	public static final String EI_MANAGER_NAME = "EquipmentInventory";
	public static final String MLS_MANAGER_NAME = "MultiLayerSubnetwork";
	public static final String EMS_MANAGER_NAME = "EMS";

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

	// Cache list
	private List<String> neNames;

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
		} else
			return false;

		return true;
	}

	public List<String> getAllManagedElementNames()
			throws ProcessingFailureException, SAXException {
		System.out.println("getAllManagedElementNames...");

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return null;
		}

		NamingAttributesList_THolder meNameList = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder meNameItr = new NamingAttributesIterator_IHolder();

		this.meManager.getAllManagedElementNames(HOW_MANY, meNameList,
				meNameItr);

		neNames = new ArrayList<String>();

		for (int i = 0; i < meNameList.value.length; i++)
			for (int j = 0; j < meNameList.value[i].length; j++)
				if (meNameList.value[i][j].name.equals("ManagedElement")) {
					neNames.add(meNameList.value[i][j].value);
					LOG.info("NE: " + meNameList.value[i][j].value);
				}

		boolean exitwhile = false;

		if (meNameItr.value != null)
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = meNameItr.value.next_n(HOW_MANY, meNameList);
					for (int i = 0; i < meNameList.value.length; i++)
						for (int j = 0; j < meNameList.value[i].length; j++)
							if (meNameList.value[i][j].name
									.equals("ManagedElement")) {
								neNames.add(meNameList.value[i][j].value);
							}
				}

				exitwhile = true;
			} finally {
				if (!exitwhile)
					meNameItr.value.destroy();
			}

		return neNames;
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
}
