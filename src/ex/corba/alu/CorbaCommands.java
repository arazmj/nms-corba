package ex.corba.alu;

import java.util.ArrayList;
import java.util.List;

import managedElementManager.ManagedElementMgr_I;
import managedElementManager.ManagedElementMgr_IHelper;
import multiLayerSubnetwork.EMSFreedomLevel_T;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.StringHolder;

import subnetworkConnection.GradesOfImpact_T;
import subnetworkConnection.SNCCreateData_T;
import subnetworkConnection.SubnetworkConnection_THolder;
import subnetworkConnection.TPDataList_THolder;

import common.Common_IHolder;

import emsSession.EmsSession_I;
import equipment.EquipmentHolder_T;
import equipment.EquipmentInventoryMgr_I;
import equipment.EquipmentInventoryMgr_IHelper;
import equipment.EquipmentOrHolderIterator_IHolder;
import equipment.EquipmentOrHolderList_THolder;
import equipment.EquipmentOrHolder_T;
import equipment.Equipment_T;
import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesIterator_IHolder;
import globaldefs.NamingAttributesList_THolder;
import globaldefs.ProcessingFailureException;

public class CorbaCommands {
	public static final String ME_MANAGER_NAME = "ManagedElement";
	public static final String EI_MANAGER_NAME = "EquipmentInventory";
	private static final String MLS_MANAGER_NAME = "MultiLayerSubnetwork";

	private static final Log LOG = LogFactory.getLog(CorbaCommands.class);

	private EmsSession_I emsSession = null;

	private String emsName = null;

	private Common_IHolder managerInterface = null;
	private ManagedElementMgr_I meManager = null;
	private EquipmentInventoryMgr_I eiManager;
	private MultiLayerSubnetworkMgr_I mlsnManager;

	public CorbaCommands(EmsSession_I emsSession, String emsName) {
		this.emsSession = emsSession;
		this.emsName = emsName;

		System.out.println("CorbaCommands:emsSession: " + emsSession);
		System.out.println("CorbaCommands:emsName: " + emsName);
	}

	public boolean setManagerByName(final String managerName)
			throws ProcessingFailureException {

		this.managerInterface = new Common_IHolder();
		this.emsSession.getManager(managerName, this.managerInterface);

		if (managerName.equals(ME_MANAGER_NAME))
			this.meManager = ManagedElementMgr_IHelper
					.narrow(managerInterface.value);
		else if (managerName.equals(EI_MANAGER_NAME))
			this.eiManager = EquipmentInventoryMgr_IHelper
					.narrow(managerInterface.value);
		else if (managerName.equals(MLS_MANAGER_NAME))
			this.mlsnManager = MultiLayerSubnetworkMgr_IHelper
					.narrow(managerInterface.value);
		else
			return false;

		return true;
	}

	public List<String> getAllManagedElementNames() throws Exception {
		System.out.println("\ngetAllManagedElementNames...");

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return null;
		}

		NamingAttributesList_THolder meNameList = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder meNameItr = new NamingAttributesIterator_IHolder();

		int howMany = 100;

		this.meManager
				.getAllManagedElementNames(howMany, meNameList, meNameItr);

		List<String> neName = new ArrayList<String>();

		for (int i = 0; i < meNameList.value.length; i++)
			for (int j = 0; j < meNameList.value[i].length; j++)
				if (meNameList.value[i][j].name.equals("ManagedElement")) {
					neName.add(meNameList.value[i][j].value);
					System.out.println("\nNE: " + meNameList.value[i][j].value);
				}

		boolean exitwhile = false;

		if (meNameItr.value != null)
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = meNameItr.value.next_n(howMany, meNameList);
					for (int i = 0; i < meNameList.value.length; i++)
						for (int j = 0; j < meNameList.value[i].length; j++)
							if (meNameList.value[i][j].name
									.equals("ManagedElement"))
								neName.add(meNameList.value[i][j].value);
				}
				exitwhile = true;
			} finally {
				if (!exitwhile)
					meNameItr.value.destroy();
			}

		return neName;
	}

	public void getAllEquipment() throws Exception {
		System.out.println("getAllEquipment...");

		List<String> neNames = getAllManagedElementNames();

		if (!setManagerByName(EI_MANAGER_NAME))
			return;

		NameAndStringValue_T[] ne = new NameAndStringValue_T[2];

		ne[0] = new NameAndStringValue_T();
		ne[0].name = "EMS";
		ne[0].value = emsName;
		ne[1] = new NameAndStringValue_T();
		ne[1].name = "ManagedElement";

		EquipmentOrHolderList_THolder equipOrHolderList = new EquipmentOrHolderList_THolder();
		EquipmentOrHolderIterator_IHolder equipOrHolderItr = new EquipmentOrHolderIterator_IHolder();

		int howMany = 100;
		int meCounter = 0;
		boolean exitWhile = false;

		for (String neName : neNames) {
			ne[1].value = neName;
			eiManager.getAllEquipment(ne, howMany, equipOrHolderList,
					equipOrHolderItr);

			System.out.println("getAllEquipment: got "
					+ equipOrHolderList.value.length
					+ " pieces of equipment for ME " + ne[1].value);

			for (int i = 0; i < equipOrHolderList.value.length; i++) {
				listEquipmentOrHolderList(equipOrHolderList.value[i]);
			}

			exitWhile = false;

			if (equipOrHolderItr.value != null)
				try {
					boolean hasMoreData = true;
					while (hasMoreData) {
						hasMoreData = equipOrHolderItr.value.next_n(howMany,
								equipOrHolderList);

						for (int i = 0; i < equipOrHolderList.value.length; i++) {
							listEquipmentOrHolderList(equipOrHolderList.value[i]);
						}
					}

					exitWhile = true;
				} finally {
					if (!exitWhile)
						equipOrHolderItr.value.destroy();
				}

			meCounter++;

			System.out.println("getAllEquipment: finished getEquipment for ME "
					+ ne[1].value + " Order number # " + meCounter);
		}
	}

	public void listEquipmentOrHolderList(EquipmentOrHolder_T eoh)
			throws Exception {
		if (eoh.discriminator().value() == 1) {
			EquipmentHolder_T holder = eoh.holder();

			System.out.println("Holder details... ");
			System.out.println("name: " + holder.name);
			System.out.println("userLabel: " + holder.userLabel);
			System.out.println("nativeEMSName: " + holder.nativeEMSName);
		} else {
			Equipment_T equip = eoh.equip();

			System.out.println("Card details... ");
			System.out.println("name: " + equip.name);
			System.out.println("userLabel: " + equip.userLabel);
			System.out.println("nativeEMSName: " + equip.nativeEMSName);
			System.out.println("installedPartNumber: "
					+ equip.installedPartNumber);
		}
	}

	public void getSNC() throws Exception {
		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] sncName = new NameAndStringValue_T[3];
		sncName[0] = new NameAndStringValue_T("EMS", emsName);
		sncName[1] = new NameAndStringValue_T("MultiLayerSubnetwork",
				"Subnetwork-7");

		sncName[2] = new NameAndStringValue_T("SubnetworkConnection",
				"ETHERNET_TEST_2");

		SubnetworkConnection_THolder sncHolder = new SubnetworkConnection_THolder();
		this.mlsnManager.getSNC(sncName, sncHolder);

		System.out.println(sncHolder.value);
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

		if (errorReason != null) {
			if (LOG.isInfoEnabled()) {
				LOG.info("errorReason:" + errorReason.value);
			}
		}

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
