package ex.corba.alu;

import java.util.ArrayList;
import java.util.List;

import managedElement.ManagedElementIterator_IHolder;
import managedElement.ManagedElementList_THolder;
import managedElement.ManagedElement_T;
import managedElementManager.ManagedElementMgr_I;
import managedElementManager.ManagedElementMgr_IHelper;
import multiLayerSubnetwork.EMSFreedomLevel_T;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.StringHolder;
import org.xml.sax.ContentHandler;

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
import ex.corba.alu.transform.jaxb.Corba2Object;
import ex.corba.alu.transform.sax.Corba2XMLHandler;
import ex.corba.xml.JaxbOutputHandler;
import ex.corba.xml.ManagedElement;
import ex.corba.xml.NmsObjects;
import globaldefs.NameAndStringValue_T;
import globaldefs.NamingAttributesIterator_IHolder;
import globaldefs.NamingAttributesList_THolder;
import globaldefs.ProcessingFailureException;

public class CorbaCommands {
	public static final String ME_MANAGER_NAME = "ManagedElement";
	public static final String EI_MANAGER_NAME = "EquipmentInventory";
	public static final String MLS_MANAGER_NAME = "MultiLayerSubnetwork";

	public static final int HOW_MANY = 100;

	public static final Log LOG = LogFactory.getLog(CorbaCommands.class);

	private EmsSession_I emsSession;
	private String emsName;
	private Corba2XMLHandler handler;

	private Common_IHolder managerInterface;
	private ManagedElementMgr_I meManager;
	private EquipmentInventoryMgr_I eiManager;
	private MultiLayerSubnetworkMgr_I mlsnManager;

	private List<String> neNames;

	public CorbaCommands(EmsSession_I emsSession, String emsName) {
		this.emsSession = emsSession;
		this.emsName = emsName;
	}

	public CorbaCommands(EmsSession_I emsSession, String emsName,
			ContentHandler contentHandler) {
		this.emsSession = emsSession;
		this.emsName = emsName;
		this.handler = new Corba2XMLHandler(contentHandler);
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
					System.out.println("NE: " + meNameList.value[i][j].value);
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
									.equals("ManagedElement"))
								neNames.add(meNameList.value[i][j].value);
				}

				exitwhile = true;
			} finally {
				if (!exitwhile)
					meNameItr.value.destroy();
			}

		return neNames;
	}

	public void getAllManagedElements() throws Exception {
		LOG.info("getAllManagedElements() start.");

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return;
		}

		ManagedElementList_THolder meList = new ManagedElementList_THolder();
		ManagedElementIterator_IHolder meItr = new ManagedElementIterator_IHolder();

		this.meManager.getAllManagedElements(HOW_MANY, meList, meItr);
		neNames = new ArrayList<String>();

		ManagedElement_T[] mes = meList.value;
		if (LOG.isDebugEnabled())
			LOG.debug("getAllManagedElements: got " + mes.length + " MEs ");

		// Corba2XMLHelper helper = new Corba2XMLHelper(handler);
		List<ManagedElement> managedElements = new ArrayList<ManagedElement>();

		for (ManagedElement_T me : mes) {
			// handler.printStructure(helper.getManagedElementParams(me));
			managedElements.add(Corba2Object.getManagedElement(me));
			neNames.add(handler.getValueByName(me.name, "ManagedElement"));
		}

		boolean exitWhile = false;

		if (meItr.value != null)
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = meItr.value.next_n(HOW_MANY, meList);
					mes = meList.value;
					if (LOG.isDebugEnabled())
						LOG.debug("getAllManagedElements: got " + mes.length
								+ " MEs ");

					for (ManagedElement_T me : mes) {
						// handler.printStructure(helper.getManagedElementParams(me));
						managedElements.add(Corba2Object.getManagedElement(me));
						neNames.add(handler.getValueByName(me.name,
								"ManagedElement"));
					}
				}

				exitWhile = true;
			} finally {
				if (!exitWhile)
					meItr.value.destroy();
			}

		// Specific to JAXB XML output: Start
		JaxbOutputHandler out = new JaxbOutputHandler("managedelement.xml");
		NmsObjects nmsObjects = new NmsObjects();
		nmsObjects.setManagedElements(managedElements);
		out.print(nmsObjects);
		// Specific to JAXB XML output: End

		LOG.info("getAllManagedElements() complete.");
	}

	public void getAllEquipment() throws Exception {
		System.out.println("getAllEquipment...");

		if (neNames == null) {
			neNames = getAllManagedElementNames();
		}

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

		int meCounter = 0;
		boolean exitWhile = false;

		for (String neName : neNames) {
			ne[1].value = neName;
			eiManager.getAllEquipment(ne, HOW_MANY, equipOrHolderList,
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
						hasMoreData = equipOrHolderItr.value.next_n(HOW_MANY,
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
			LOG.error("errorReason:" + errorReason.value);
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

		if (errorReason != null) {
			LOG.error("errorReason:" + errorReason.value);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info(" deactivateAndDeleteSNC() complete.");
		}

		return errorReason;
	}
}
