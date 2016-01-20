package ex.corba.ciena;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omg.CORBA.StringHolder;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.ciena.oc.callSNC.CallAndTopLevelConnections_THolder;
import com.ciena.oc.callSNC.CallCreateData_T;
import com.ciena.oc.common.Common_IHolder;
import com.ciena.oc.emsMgr.EMSMgr_I;
import com.ciena.oc.emsMgr.EMSMgr_IHelper;
import com.ciena.oc.emsSession.EmsSession_I;
import com.ciena.oc.equipment.EquipmentInventoryMgr_I;
import com.ciena.oc.equipment.EquipmentInventoryMgr_IHelper;
import com.ciena.oc.equipment.EquipmentOrHolderIterator_IHolder;
import com.ciena.oc.equipment.EquipmentOrHolderList_THolder;
import com.ciena.oc.equipment.EquipmentOrHolder_T;
import com.ciena.oc.equipment.Equipment_T;
import com.ciena.oc.equipmentManagerCIENA.EquipmentConfigurationData_T;
import com.ciena.oc.equipmentManagerCIENA.EquipmentConfigurationData_THolder;
import com.ciena.oc.equipmentManagerCIENA.EquipmentMgrCIENA_I;
import com.ciena.oc.equipmentManagerCIENA.EquipmentMgrCIENA_IHelper;
import com.ciena.oc.flowDomain.ConnectivityRequirement_T;
import com.ciena.oc.flowDomain.FlowDomainMgr_I;
import com.ciena.oc.flowDomain.FlowDomainMgr_IHelper;
import com.ciena.oc.flowDomainFragment.FDFrCreateData_T;
import com.ciena.oc.flowDomainFragment.FDFrIterator_I;
import com.ciena.oc.flowDomainFragment.FDFrIterator_IHolder;
import com.ciena.oc.flowDomainFragment.FDFrList_THolder;
import com.ciena.oc.flowDomainFragment.FlowDomainFragment_THolder;
import com.ciena.oc.flowDomainFragment.MatrixFlowDomainFragmentList_THolder;
import com.ciena.oc.globaldefs.NameAndStringValue_T;
import com.ciena.oc.globaldefs.NamingAttributesIterator_IHolder;
import com.ciena.oc.globaldefs.NamingAttributesList_THolder;
import com.ciena.oc.globaldefs.ProcessingFailureException;
import com.ciena.oc.managedElement.ManagedElementIterator_IHolder;
import com.ciena.oc.managedElement.ManagedElementList_THolder;
import com.ciena.oc.managedElement.ManagedElement_T;
import com.ciena.oc.managedElementManager.ManagedElementMgr_I;
import com.ciena.oc.managedElementManager.ManagedElementMgr_IHelper;
import com.ciena.oc.multiLayerSubnetwork.EMSFreedomLevel_T;
import com.ciena.oc.multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import com.ciena.oc.multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import com.ciena.oc.protection.ProtectionGroupIterator_IHolder;
import com.ciena.oc.protection.ProtectionGroupList_THolder;
import com.ciena.oc.protection.ProtectionMgr_I;
import com.ciena.oc.protection.ProtectionMgr_IHelper;
import com.ciena.oc.subnetworkConnection.CrossConnect_T;
import com.ciena.oc.subnetworkConnection.GradesOfImpact_T;
import com.ciena.oc.subnetworkConnection.Route_THolder;
import com.ciena.oc.subnetworkConnection.SNCCreateData_T;
import com.ciena.oc.subnetworkConnection.SNCIterator_IHolder;
import com.ciena.oc.subnetworkConnection.SubnetworkConnectionList_THolder;
import com.ciena.oc.subnetworkConnection.SubnetworkConnection_T;
import com.ciena.oc.subnetworkConnection.SubnetworkConnection_THolder;
import com.ciena.oc.subnetworkConnection.TPDataList_THolder;
import com.ciena.oc.subnetworkConnection.TPData_T;
import com.ciena.oc.terminationPoint.GTPEffort_T;
import com.ciena.oc.terminationPoint.GTP_THolder;
import com.ciena.oc.terminationPoint.GTPiterator_IHolder;
import com.ciena.oc.terminationPoint.GTPlist_THolder;
import com.ciena.oc.terminationPoint.TerminationPointIterator_IHolder;
import com.ciena.oc.terminationPoint.TerminationPointList_THolder;
import com.ciena.oc.terminationPoint.TerminationPoint_T;
import com.ciena.oc.terminationPoint.TerminationPoint_THolder;
import com.ciena.oc.topologicalLink.TopologicalLinkIterator_IHolder;
import com.ciena.oc.topologicalLink.TopologicalLinkList_THolder;
import com.ciena.oc.topologicalLink.TopologicalLink_T;

import ex.corba.CorbaConstants;
import ex.corba.ciena.error.CorbaErrorDescriptions;
import ex.corba.ciena.error.CorbaErrorProcessor;
import ex.corba.ciena.transform.sax.Corba2XMLContainer;
import ex.corba.ciena.transform.sax.Corba2XMLHandler;
import ex.corba.ciena.transform.sax.Corba2XMLHelper;
import ex.corba.ciena.transform.sax.Corba2XMLStructure;

public class CorbaCommands {
	public static final String ME_MANAGER_NAME = "ManagedElement";
	public static final String EI_MANAGER_NAME = "EquipmentInventory";
	public static final String MLS_MANAGER_NAME = "MultiLayerSubnetwork";
	public static final String EMS_MANAGER_NAME = "EMS";
	public static final String PRT_MANAGER_NAME = "Protection";
	public static final String FD_MANAGER_NAME = "FlowDomain";
	public static final String EICIENA_MANAGER_NAME = "EquipmentCIENA";

	public static final int HOW_MANY = 100;

	public static final Logger LOG = LoggerFactory
			.getLogger(CorbaCommands.class);

	private EmsSession_I emsSession;
	private String emsName;
	private Corba2XMLHandler handler;
	private Corba2XMLHelper helper;

	private Common_IHolder managerInterface;
	private ManagedElementMgr_I meManager;
	private EquipmentInventoryMgr_I eiManager;
	private MultiLayerSubnetworkMgr_I mlsnManager;
	private EMSMgr_I emsManager;
	private ProtectionMgr_I protectionMgr;
	private FlowDomainMgr_I flowDomainManager;
	private EquipmentMgrCIENA_I equipmentCIENAManager;

	// Cache list
	private List<String> neNames;
	private List<String> sncNames;
	private List<NameAndStringValue_T[]> tpNames;
	private List<NameAndStringValue_T[]> slotNames;

	private Set<Short> terminationPointRates;

	public CorbaCommands(EmsSession_I emsSession, String emsName) {
		this.emsSession = emsSession;
		this.emsName = emsName;
	}

	public CorbaCommands(EmsSession_I emsSession, String emsName,
			ContentHandler contentHandler) {
		this.emsSession = emsSession;
		this.emsName = emsName;
		this.handler = new Corba2XMLHandler(contentHandler);
		this.helper = new Corba2XMLHelper(handler);
	}

	public boolean setManagerByName(final String managerName)
			throws ProcessingFailureException {

		this.managerInterface = new Common_IHolder();
		this.emsSession.getManager(managerName, this.managerInterface);

		// LOG.info("Available managers: ");
		// ManagerNames_THolder names = new ManagerNames_THolder();
		// this.emsSession.getSupportedManagers(names);
		// String[] availableManagers = names.value;
		// for (int i = 0; i < availableManagers.length; i++) {
		// LOG.info(" " + i + ") " + availableManagers[i]);
		// }

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
		} else if (managerName.equals(PRT_MANAGER_NAME)) {
			if (this.protectionMgr == null) {
				this.protectionMgr = ProtectionMgr_IHelper
						.narrow(managerInterface.value);
			}
		} else if (managerName.equals(FD_MANAGER_NAME)) {
			if (this.flowDomainManager == null) {
				this.flowDomainManager = FlowDomainMgr_IHelper
						.narrow(managerInterface.value);
			}
		} else if (managerName.equals(EICIENA_MANAGER_NAME)) {
			if (this.equipmentCIENAManager == null) {
				this.equipmentCIENAManager = EquipmentMgrCIENA_IHelper
						.narrow(managerInterface.value);
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

		for (ManagedElement_T me : mes) {
			handler.printStructure(helper.getManagedElementParams(me));
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
						handler.printStructure(helper
								.getManagedElementParams(me));
						neNames.add(handler.getValueByName(me.name,
								"ManagedElement"));
					}
				}

				exitWhile = true;
			} finally {
				if (!exitWhile)
					meItr.value.destroy();
			}

		LOG.info("getAllManagedElements() complete.");
	}

	public void getAllEquipment() throws ProcessingFailureException,
			SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEquipment() start.");
		}

		List<EquipmentOrHolder_T> equipmentList = getAllEquipmentList();

		if (equipmentList != null && equipmentList.size() > 0) {
			for (int i = 0; i < equipmentList.size(); i++) {
				helper.printEquipmentOrHolder(equipmentList.get(i));
			}
		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("getAllEquipment() got 0 Equipments");
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEquipment() complete.");
		}
	}

	public List<EquipmentOrHolder_T> getAllEquipmentList()
			throws ProcessingFailureException, SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEquipmentList() start.");
		}

		List<EquipmentOrHolder_T> equipmentList = new ArrayList<EquipmentOrHolder_T>();

		if (neNames == null) {
			neNames = getAllManagedElementNames();
		}

		if (!setManagerByName(EI_MANAGER_NAME))
			return null;

		NameAndStringValue_T[] ne = new NameAndStringValue_T[2];

		ne[0] = new NameAndStringValue_T("EMS", emsName);
		ne[1] = new NameAndStringValue_T();
		ne[1].name = "ManagedElement";

		if (this.slotNames == null) {
			this.slotNames = new ArrayList<NameAndStringValue_T[]>();
		}

		EquipmentOrHolderList_THolder equipOrHolderList = new EquipmentOrHolderList_THolder();
		EquipmentOrHolderIterator_IHolder equipOrHolderItr = new EquipmentOrHolderIterator_IHolder();

		int meCounter = 0;
		boolean exitWhile = false;

		for (String neName : neNames) {
			try {
				ne[1].value = neName;
				eiManager.getAllEquipment(ne, HOW_MANY, equipOrHolderList,
						equipOrHolderItr);

				LOG.info("getAllEquipmentList: got "
						+ equipOrHolderList.value.length
						+ " equipments for ME " + ne[1].value);

				for (int i = 0; i < equipOrHolderList.value.length; i++) {
					equipmentList.add(equipOrHolderList.value[i]);
					if (equipOrHolderList.value[i].discriminator().value() != 1) {
						Equipment_T equipment = equipOrHolderList.value[i]
								.equip();

						if (equipment != null
								&& ("TSLM-12"
										.equals(equipment.installedEquipmentObjectType) || "TSLM-48"
										.equals(equipment.installedEquipmentObjectType))) {

							NameAndStringValue_T[] slotNameAndStringValueArray = new NameAndStringValue_T[3];
							slotNameAndStringValueArray[0] = equipment.name[0];
							slotNameAndStringValueArray[1] = equipment.name[1];
							if ("EquipmentHolder"
									.equals(equipment.name[2].name)) {
								slotNameAndStringValueArray[2] = equipment.name[2];
							} else if (equipment.name.length > 3
									&& "EquipmentHolder"
											.equals(equipment.name[3].name)) {
								slotNameAndStringValueArray[2] = equipment.name[3];
							}
							slotNames.add(slotNameAndStringValueArray);
						}
					}
				}

				exitWhile = false;

				if (equipOrHolderItr.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = equipOrHolderItr.value.next_n(
									HOW_MANY, equipOrHolderList);

							for (int i = 0; i < equipOrHolderList.value.length; i++) {

								equipmentList.add(equipOrHolderList.value[i]);
								if (equipOrHolderList.value[i].discriminator()
										.value() != 1) {
									Equipment_T equipment = equipOrHolderList.value[i]
											.equip();
									if (equipment != null
											&& ("TSLM-12"
													.equals(equipment.installedEquipmentObjectType) || "TSLM-48"
													.equals(equipment.installedEquipmentObjectType))) {
										NameAndStringValue_T[] slotNameAndStringValueArray = new NameAndStringValue_T[3];
										slotNameAndStringValueArray[0] = equipment.name[0];
										slotNameAndStringValueArray[1] = equipment.name[1];
										if ("EquipmentHolder"
												.equals(equipment.name[2].name)) {
											slotNameAndStringValueArray[2] = equipment.name[2];
										} else if (equipment.name.length > 3
												&& "EquipmentHolder"
														.equals(equipment.name[3].name)) {
											slotNameAndStringValueArray[2] = equipment.name[3];
										}
										slotNames
												.add(slotNameAndStringValueArray);
									}
								}
							}
						}

						exitWhile = true;
					} finally {
						if (!exitWhile)
							equipOrHolderItr.value.destroy();
					}
				}

				meCounter++;

				if (LOG.isInfoEnabled()) {
					LOG.info("getAllEquipmentList: finished getEquipment for ME "
							+ ne[1].value + " Order number # " + meCounter);
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex,
						"getAllEquipmentList. ME: " + neName);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEquipmentList() complete.");
		}

		return equipmentList;
	}

	public void getAllPTPs() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllPTPs() start.");
		}

		if (neNames == null) {
			neNames = getAllManagedElementNames();
		}

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return;
		}

		NameAndStringValue_T[] neNameArray = new NameAndStringValue_T[2];

		neNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR,
				emsName);
		neNameArray[1] = new NameAndStringValue_T();
		neNameArray[1].name = CorbaConstants.MANAGED_ELEMENT_STR;

		TerminationPointList_THolder terminationPointList = new TerminationPointList_THolder();
		TerminationPointIterator_IHolder terminationPointIterator = new TerminationPointIterator_IHolder();
		short[] tpLayerRateList = new short[0];
		short[] connectionLayerRateList = new short[0];

		int counter = 0;
		boolean exitWhile = false;
		for (String neName : neNames) {
			try {
				neNameArray[1].value = neName;
				meManager.getAllPTPs(neNameArray, tpLayerRateList,
						connectionLayerRateList, HOW_MANY,
						terminationPointList, terminationPointIterator);

				if (LOG.isInfoEnabled()) {
					LOG.info("getAllPTPs: got {} PTP for ME {}.",
							terminationPointList.value.length,
							neNameArray[1].value);
				}

				for (int i = 0; i < terminationPointList.value.length; i++) {
					helper.printTerminationPoint(terminationPointList.value[i],
							CorbaConstants.PTPS_STR);
				}

				exitWhile = false;

				if (terminationPointIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = terminationPointIterator.value
									.next_n(HOW_MANY, terminationPointList);
							if (LOG.isInfoEnabled()) {
								LOG.info("getAllPTPs: got {} PTP for ME {}.",
										terminationPointList.value.length,
										neNameArray[1].value);
							}

							for (int i = 0; i < terminationPointList.value.length; i++) {
								helper.printTerminationPoint(
										terminationPointList.value[i],
										CorbaConstants.PTPS_STR);
							}
						}
						exitWhile = true;
					} finally {
						if (!exitWhile) {
							terminationPointIterator.value.destroy();
						}
					}

					counter++;

					if (LOG.isDebugEnabled()) {
						LOG.debug("getAllPTPs: finished getPTP for ME "
								+ neNameArray[1].value + " Order number # "
								+ counter);
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllPTPs. ME: "
						+ neName);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllPTPs() complete.");
		}
	}

	public void getAllGTPs() throws ProcessingFailureException, SAXException {
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("getAllGTPs() start");
			}

			List<String> neNames = getAllManagedElementNames();

			if (!setManagerByName(ME_MANAGER_NAME)) {
				return;
			}

			NameAndStringValue_T[] ne = new NameAndStringValue_T[2];

			ne[0] = new NameAndStringValue_T("EMS", emsName);
			ne[1] = new NameAndStringValue_T();
			ne[1].name = "ManagedElement";

			GTPlist_THolder th = new GTPlist_THolder();
			GTPiterator_IHolder ith = new GTPiterator_IHolder();
			short[] tpLayerRateList = new short[0];

			int counter = 0;
			boolean exitwhile = false;

			for (String n : neNames) {
				try {
					ne[1].value = n;
					this.meManager.getAllGTPs(ne, tpLayerRateList, HOW_MANY,
							th, ith);

					if (LOG.isInfoEnabled()) {
						LOG.info("getAllGTPs: got " + th.value.length
								+ " pieces of GTP for ME " + ne[1].value);
					}

					for (int i = 0; i < th.value.length; i++) {
						helper.printGTP(th.value[i]);
					}

					exitwhile = false;

					if (ith.value != null)
						try {
							boolean hasMoreData = true;
							while (hasMoreData) {
								hasMoreData = ith.value.next_n(HOW_MANY, th);

								if (LOG.isInfoEnabled()) {
									LOG.info("getAllGTPs: got next "
											+ th.value.length
											+ " pieces of GTP for ME "
											+ ne[1].value);
								}

								for (int i = 0; i < th.value.length; i++)
									helper.printGTP(th.value[i]);
							}
							exitwhile = true;
						} finally {
							if (!exitwhile)
								ith.value.destroy();
						}

					counter++;

					if (LOG.isInfoEnabled()) {
						LOG.info("getAllGTPs: finished getGTP for ME "
								+ ne[1].value + " Order number # " + counter);
					}
				} catch (ProcessingFailureException e) {
					handleProcessingFailureException(e, "getAllGTPs. ME");
				}
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("getAllGTPs() complete.");
			}
		} catch (ProcessingFailureException prf) {
			LOG.error("Ciena ON-Center>> getAllGTPs:"
					+ CorbaErrorProcessor.printError(prf));
			throw prf;
		}
	}

	public void getAllTopologicalLinks() throws ProcessingFailureException,
			SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopologicalLinks() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] mlsn = new NameAndStringValue_T[2];

		mlsn[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		mlsn[1] = new NameAndStringValue_T(
				CorbaConstants.MULTILAYER_SUBNETWORK_STR, "MLSN_1");

		if (this.tpNames == null)
			this.tpNames = new ArrayList<NameAndStringValue_T[]>();

		TopologicalLinkList_THolder topologicalLinkList = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder topologicalLinkIterator = new TopologicalLinkIterator_IHolder();

		try {
			mlsnManager.getAllTopologicalLinks(mlsn, HOW_MANY,
					topologicalLinkList, topologicalLinkIterator);

			for (int i = 0; i < topologicalLinkList.value.length; i++) {
				handler.printStructure(getTopologicalLinkParams(topologicalLinkList.value[i]));
				this.tpNames.add(topologicalLinkList.value[i].aEndTP);
				this.tpNames.add(topologicalLinkList.value[i].zEndTP);
			}

			boolean exitWhile = false;
			if (topologicalLinkIterator.value != null) {
				try {
					boolean hasMoreData = true;
					while (hasMoreData) {
						hasMoreData = topologicalLinkIterator.value.next_n(
								HOW_MANY, topologicalLinkList);
						for (int i = 0; i < topologicalLinkList.value.length; i++) {
							handler.printStructure(getTopologicalLinkParams(topologicalLinkList.value[i]));
							this.tpNames
									.add(topologicalLinkList.value[i].aEndTP);
							this.tpNames
									.add(topologicalLinkList.value[i].zEndTP);
						}
					}

					exitWhile = true;
				} finally {
					if (!exitWhile) {
						topologicalLinkIterator.value.destroy();
					}
				}
			}
		} catch (ProcessingFailureException ex) {
			handleProcessingFailureException(ex,
					"getAllTopologicalLinks. MLS: " + mlsn[1].value);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopologicalLinks() complete.");
		}
	}

	public Corba2XMLContainer getTopologicalLinkParams(
			TopologicalLink_T topologicalLink)
			throws ProcessingFailureException {
		Corba2XMLContainer container = new Corba2XMLContainer(
				Corba2XMLStructure.TOPOL_LINKS);

		container.setFieldValue(CorbaConstants.USER_LABEL_STR,
				topologicalLink.userLabel);
		container
				.setFieldValue(CorbaConstants.TL_ID_STR,
						handler.getValueByName(topologicalLink.name,
								"TopologicalLink"));
		container.setFieldValue(CorbaConstants.NATIVE_EMS_NAME_STR,
				topologicalLink.nativeEMSName);
		container
				.setFieldValue(CorbaConstants.OWNER_STR, topologicalLink.owner);
		container.setFieldValue(CorbaConstants.DIRECTION_STR,
				String.valueOf(topologicalLink.direction.value()));
		container.setFieldValue(CorbaConstants.RATE_STR,
				String.valueOf(topologicalLink.rate));
		container.setFieldValue(CorbaConstants.A_END_NE_STR, handler
				.getValueByName(topologicalLink.aEndTP,
						CorbaConstants.MANAGED_ELEMENT_STR));
		container
				.setFieldValue(CorbaConstants.A_END_TP_STR, handler
						.getValueByName(topologicalLink.aEndTP,
								CorbaConstants.PTP_STR));

		container.setFieldValue(CorbaConstants.Z_END_NE_STR, handler
				.getValueByName(topologicalLink.zEndTP,
						CorbaConstants.MANAGED_ELEMENT_STR));
		container
				.setFieldValue(CorbaConstants.Z_END_TP_STR, handler
						.getValueByName(topologicalLink.zEndTP,
								CorbaConstants.PTP_STR));

		TerminationPoint_T aEndTP = getTerminationPoint(topologicalLink.aEndTP,
				topologicalLink.rate);

		if (aEndTP != null) {
			container
					.setFieldValue(
							CorbaConstants.A_TRANSMISSION_PARAMS_STR,
							handler.convertLayeredParametersToString(aEndTP.transmissionParams));
		} else {
			container.setFieldValue(CorbaConstants.A_TRANSMISSION_PARAMS_STR,
					"");
		}

		TerminationPoint_T zEndTP = getTerminationPoint(topologicalLink.zEndTP,
				topologicalLink.rate);
		if (zEndTP != null) {
			container
					.setFieldValue(
							CorbaConstants.Z_TRANSMISSION_PARAMS_STR,
							handler.convertLayeredParametersToString(zEndTP.transmissionParams));
		} else {
			container.setFieldValue(CorbaConstants.Z_TRANSMISSION_PARAMS_STR,
					"");
		}

		return container;
	}

	public void getAllProtectionGroups() throws ProcessingFailureException,
			SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllProtectionGroups() start.");
		}

		if (neNames == null) {
			neNames = getAllManagedElementNames();
		}

		if (!setManagerByName(PRT_MANAGER_NAME))
			return;

		NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[2];

		nameAndStringValueArray[0] = new NameAndStringValue_T();
		nameAndStringValueArray[0].name = CorbaConstants.EMS_STR;
		nameAndStringValueArray[0].value = emsName;
		nameAndStringValueArray[1] = new NameAndStringValue_T();
		nameAndStringValueArray[1].name = CorbaConstants.MANAGED_ELEMENT_STR;

		ProtectionGroupList_THolder protectionGroupList = new ProtectionGroupList_THolder();
		ProtectionGroupIterator_IHolder protectionGroupIterator = new ProtectionGroupIterator_IHolder();

		int counter = 0;
		boolean exitwhile = false;
		for (String n : neNames) {
			try {
				nameAndStringValueArray[1].value = n;
				protectionMgr.getAllProtectionGroups(nameAndStringValueArray,
						HOW_MANY, protectionGroupList, protectionGroupIterator);

				if (LOG.isDebugEnabled()) {
					LOG.debug("getAllProtectionGroups: got "
							+ protectionGroupList.value.length
							+ " pieces of ProtectionGroup for ME "
							+ nameAndStringValueArray[1].value);
				}

				helper.printProtectionGroup(protectionGroupList.value);

				exitwhile = false;
				if (protectionGroupIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = protectionGroupIterator.value.next_n(
									HOW_MANY, protectionGroupList);

							if (LOG.isDebugEnabled()) {
								LOG.debug("getAllProtectionGroups: got next "
										+ protectionGroupList.value.length
										+ " pieces of ProtectionGroup for ME "
										+ nameAndStringValueArray[1].value);
							}

							helper.printProtectionGroup(protectionGroupList.value);
						}
						exitwhile = true;
					} finally {
						if (!exitwhile) {
							protectionGroupIterator.value.destroy();
						}
					}

					counter++;

					if (LOG.isDebugEnabled()) {
						LOG.debug("getAllProtectionGroups: finished getProtectionGroup for ME "
								+ nameAndStringValueArray[1].value
								+ " Order number # " + counter);
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex,
						"getAllProtectionGroups. ME: " + n);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllProtectionGroups() complete");
		}
	}

	private TerminationPoint_T getTerminationPoint(
			NameAndStringValue_T[] endPointName, short rate)
			throws ProcessingFailureException {

		if (!setManagerByName(ME_MANAGER_NAME))
			return null;

		if (terminationPointRates == null
				|| (terminationPointRates != null && !terminationPointRates
						.contains(Short.valueOf(rate)))) {
			return null;
		}

		TerminationPoint_THolder tpHolder = new TerminationPoint_THolder();

		try {
			meManager.getTP(endPointName, tpHolder);
		} catch (ProcessingFailureException ex) {
			handleProcessingFailureException(ex, "getTerminationPoint. TP: "
					+ handler.convertNameAndStringValueToString(endPointName));
		}

		return tpHolder.value;
	}

	public void getAllFDFrs() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllFDFrs() start.");
		}

		if (!setManagerByName(FD_MANAGER_NAME))
			return;

		NameAndStringValue_T[] fdname = new NameAndStringValue_T[2];
		fdname[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		fdname[1] = new NameAndStringValue_T("FlowDomain", "ETS_0");

		FDFrList_THolder listHolder = new FDFrList_THolder();
		FDFrIterator_IHolder iteratorHolder = new FDFrIterator_IHolder();

		FDFrIterator_I iterator = null;
		short[] rateList = new short[0];

		this.flowDomainManager.getAllFDFrs(fdname, HOW_MANY, rateList,
				listHolder, iteratorHolder);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Got "
					+ (listHolder.value != null ? listHolder.value.length : "0")
					+ " FDFrs");
		}

		helper.printFDFrs(listHolder.value);

		iterator = iteratorHolder.value;
		if (iterator != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Getting more data...");
			}

			boolean hasMoreData = true;
			boolean exitedWhile = false;

			try {
				while (hasMoreData) {
					hasMoreData = iterator.next_n(HOW_MANY, listHolder);
					if (LOG.isDebugEnabled()) {
						LOG.debug("Got "
								+ (listHolder.value != null ? listHolder.value.length
										: "0") + " FDFrs");
					}

					helper.printFDFrs(listHolder.value);
				}

				exitedWhile = true;
			} finally {
				if (!exitedWhile) {
					iterator.destroy();
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllFDFrs() end.");
		}
	}

	public List<String> getAllSubnetworkConnections()
			throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllSubnetworkConnections() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[2];

		nameAndStringValueArray[0] = new NameAndStringValue_T(
				CorbaConstants.EMS_STR, emsName);
		nameAndStringValueArray[1] = new NameAndStringValue_T(
				CorbaConstants.MULTILAYER_SUBNETWORK_STR, "MLSN_1");

		SubnetworkConnectionList_THolder sncList = new SubnetworkConnectionList_THolder();
		SNCIterator_IHolder sncIterator = new SNCIterator_IHolder();
		short[] rateList = new short[0];

		mlsnManager.getAllSubnetworkConnections(nameAndStringValueArray,
				rateList, HOW_MANY, sncList, sncIterator);
		sncNames = new ArrayList<String>();

		if (LOG.isInfoEnabled()) {
			LOG.info("--------------------getAllSubnetworkConnections() SNC Names List Start--------------------.");
		}

		for (int i = 0; i < sncList.value.length; i++) {
			if (LOG.isInfoEnabled()) {
				LOG.info(handler.getValueByName(sncList.value[i].name,
						CorbaConstants.SUBNETWORK_CONNECTION_STR));
			}
			sncNames.add(handler.getValueByName(sncList.value[i].name,
					CorbaConstants.SUBNETWORK_CONNECTION_STR));
			handler.printStructure(helper
					.getSubnetworkConnectionParams(sncList.value[i]));
		}

		boolean exitWhile = false;
		if (sncIterator.value != null) {
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = sncIterator.value.next_n(HOW_MANY, sncList);
					for (int i = 0; i < sncList.value.length; i++) {
						if (LOG.isInfoEnabled()) {
							LOG.info(handler.getValueByName(
									sncList.value[i].name,
									CorbaConstants.SUBNETWORK_CONNECTION_STR));
						}
						sncNames.add(handler.getValueByName(
								sncList.value[i].name,
								CorbaConstants.SUBNETWORK_CONNECTION_STR));
						handler.printStructure(helper
								.getSubnetworkConnectionParams(sncList.value[i]));
					}
				}
				exitWhile = true;
			} finally {
				if (!exitWhile) {
					sncIterator.value.destroy();
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("--------------------getAllSubnetworkConnections() SNC Names List End--------------------.");
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllSubnetworkConnections() complete.");
		}

		return sncNames;
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
			if (theSNC != null && theSNC.value != null) {
				SubnetworkConnection_T createdSNC = theSNC.value;
				LOG.info("createAndActivateSNC() SNC ID-User Label:"
						+ createdSNC.userLabel);
				LOG.info("createAndActivateSNC() SNC ID-Native EMS Name:"
						+ createdSNC.nativeEMSName);

				if (createdSNC.name != null && createdSNC.name.length > 0) {

					for (int counter = 0; counter < createdSNC.name.length; counter++) {
						LOG.info("createAndActivateSNC() SNC ID-Name:"
								+ createdSNC.name[counter].name);
						LOG.info("createAndActivateSNC() SNC ID-Value:"
								+ createdSNC.name[counter].value);
					}
				}
			}

			if (errorReason != null && errorReason.value != null
					&& !"".equals(errorReason.value)) {
				LOG.info("createAndActivateSNC() Error Reason:"
						+ errorReason.value);
			}
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
			LOG.info("deactivateAndDeleteSNC() complete.");
		}

		return errorReason;
	}

	public StringHolder releaseCall(NameAndStringValue_T[] sncName,
			TPDataList_THolder tpsToModify) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("releaseCall() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		StringHolder errorReason = new StringHolder();

		this.mlsnManager.releaseCall(sncName, tpsToModify, errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info("releaseCall() complete.");
		}

		return errorReason;
	}

	public GTP_THolder createGTP(String userLabel, boolean forceUniqueness,
			String owner, NameAndStringValue_T[][] listOfTPs,
			NameAndStringValue_T[] initialCTPname, int numberOfCTPs,
			GTPEffort_T gtpEffort, NameAndStringValue_T[] additionalCreationInfo)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("createGTP() start.");
		}

		if (!setManagerByName(ME_MANAGER_NAME))
			return null;

		GTP_THolder theGTP = new GTP_THolder();

		this.meManager.createGTP(userLabel, forceUniqueness, owner, listOfTPs,
				initialCTPname, numberOfCTPs, gtpEffort,
				additionalCreationInfo, theGTP);

		if (LOG.isInfoEnabled()) {
			LOG.info("createGTP() complete.");
		}

		return theGTP;
	}

	public void deleteGTP(NameAndStringValue_T[] gtpName)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("deleteGTP() start.");
		}

		setManagerByName(ME_MANAGER_NAME);

		this.meManager.deleteGTP(gtpName);

		if (LOG.isInfoEnabled()) {
			LOG.info("deleteGTP() complete.");
		}
	}

	public void getRoute() throws ProcessingFailureException, SAXException,
			NotFound, InvalidName, CannotProceed,
			org.omg.CORBA.ORBPackage.InvalidName {

		if (LOG.isInfoEnabled()) {
			LOG.info(" getRoute() start");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] sncNameAndStringValueArray = new NameAndStringValue_T[2];

		sncNameAndStringValueArray[0] = new NameAndStringValue_T(
				CorbaConstants.EMS_STR, emsName);
		sncNameAndStringValueArray[1] = new NameAndStringValue_T(
				CorbaConstants.MULTILAYER_SUBNETWORK_STR, "MLSN_1");

		SubnetworkConnectionList_THolder sncList = new SubnetworkConnectionList_THolder();
		SNCIterator_IHolder sncIterator = new SNCIterator_IHolder();
		short[] rateList = new short[0];

		mlsnManager.getAllSubnetworkConnections(sncNameAndStringValueArray,
				rateList, HOW_MANY, sncList, sncIterator);
		List<String> sncNames = new ArrayList<String>();

		for (int i = 0; i < sncList.value.length; i++) {
			sncNames.add(handler.getValueByName(sncList.value[i].name,
					CorbaConstants.SUBNETWORK_CONNECTION_STR));
		}

		boolean exitWhile = false;
		if (sncIterator.value != null) {
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = sncIterator.value.next_n(HOW_MANY, sncList);
					for (int i = 0; i < sncList.value.length; i++) {
						sncNames.add(handler.getValueByName(
								sncList.value[i].name,
								CorbaConstants.SUBNETWORK_CONNECTION_STR));
					}
				}
				exitWhile = true;
			} finally {
				if (!exitWhile) {
					sncIterator.value.destroy();
				}
			}
		}

		NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[3];

		nameAndStringValueArray[0] = new NameAndStringValue_T();
		nameAndStringValueArray[0].name = CorbaConstants.EMS_STR;
		nameAndStringValueArray[0].value = emsName;
		nameAndStringValueArray[1] = new NameAndStringValue_T();
		nameAndStringValueArray[1].name = CorbaConstants.MULTILAYER_SUBNETWORK_STR;
		nameAndStringValueArray[1].value = "MLSN_1";
		nameAndStringValueArray[2] = new NameAndStringValue_T();
		nameAndStringValueArray[2].name = CorbaConstants.SUBNETWORK_CONNECTION_STR;

		Route_THolder routeHolder = new Route_THolder();
		Iterator<String> iter = sncNames.iterator();
		try {
			while (iter.hasNext()) {
				nameAndStringValueArray[2].value = iter.next();
				try {
					mlsnManager.getRoute(nameAndStringValueArray, true,
							routeHolder);
					for (CrossConnect_T crossConnect : routeHolder.value) {
						handler.printStructure(getRouteParams(crossConnect,
								nameAndStringValueArray[2].value));
					}
				} catch (ProcessingFailureException ex) {
					CorbaErrorProcessor err = new CorbaErrorProcessor(ex);
					if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
						if (LOG.isErrorEnabled()) {
							LOG.error(
									"Ciena ON-Center>> getRoute: ME = "
											+ nameAndStringValueArray[2].value
											+ ";  "
											+ err.printError()
											+ " It is a major error. Stop interaction with server",
									ex);
						}
						throw ex;
					} else {
						if (LOG.isWarnEnabled()) {
							LOG.warn(
									"Ciena ON-Center>> getRoute: ME = "
											+ nameAndStringValueArray[2].value
											+ ";  "
											+ err.printError()
											+ " It is a minor error. Continue interaction with server",
									ex);
						}
					}
				}
			}
			if (LOG.isInfoEnabled()) {
				LOG.info(" getRoute() complete");
			}
		} catch (org.omg.CORBA.SystemException se) {
			if (se instanceof org.omg.CORBA.OBJECT_NOT_EXIST
					|| se instanceof org.omg.CORBA.TRANSIENT
					|| se instanceof org.omg.CORBA.COMM_FAILURE) {

				if (LOG.isErrorEnabled()) {
					LOG.error("Ciena ON-Center>> getRoute: CORBA Error >> "
							+ se.getMessage());
				}
			}
		} catch (ProcessingFailureException prf) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Ciena ON-Center>> getRoute:"
						+ CorbaErrorProcessor.printError(prf));
			}
			throw prf;
		} catch (Exception e1) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Ciena ON-Center>> getRoute: System Error >> "
						+ e1.getMessage());
			}
		}
	}

	private Corba2XMLContainer getRouteParams(CrossConnect_T crossConnect,
			String sncId) throws ProcessingFailureException {

		Corba2XMLContainer container = new Corba2XMLContainer(
				Corba2XMLStructure.ROUTES);

		container.setFieldValue(CorbaConstants.SNC_ID_STR, sncId);
		container.setFieldValue(CorbaConstants.ACTIVE_STR,
				String.valueOf(crossConnect.active));
		container.setFieldValue(CorbaConstants.DIRECTION_STR,
				String.valueOf(crossConnect.direction.value()));
		container.setFieldValue(CorbaConstants.CC_TYPE_STR,
				String.valueOf(crossConnect.ccType.value()));
		container.setFieldValue(CorbaConstants.A_END_NE_STR, handler
				.getValueByName(crossConnect.aEndNameList[0],
						CorbaConstants.MANAGED_ELEMENT_STR));
		container
				.setFieldValue(
						CorbaConstants.A_END_TP_STR,
						handler.convertNameAndStringValuesToStringExcludingEMS(crossConnect.aEndNameList));
		container.setFieldValue(CorbaConstants.Z_END_NE_STR, handler
				.getValueByName(crossConnect.zEndNameList[0],
						CorbaConstants.MANAGED_ELEMENT_STR));
		container
				.setFieldValue(
						CorbaConstants.Z_END_TP_STR,
						handler.convertNameAndStringValuesToStringExcludingEMS(crossConnect.zEndNameList));

		container.setFieldValue(CorbaConstants.SOURCE_TIME_STAMP_STR,
				handler.convertSystemTimeToString());

		return container;
	}

	public void getSNC(String sncName) throws ProcessingFailureException,
			SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getSNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[3];

		nameAndStringValueArray[0] = new NameAndStringValue_T(
				CorbaConstants.EMS_STR, emsName);
		nameAndStringValueArray[1] = new NameAndStringValue_T(
				CorbaConstants.MULTILAYER_SUBNETWORK_STR, "MLSN_1");
		nameAndStringValueArray[2] = new NameAndStringValue_T(
				CorbaConstants.SUBNETWORK_CONNECTION_STR, sncName);

		SubnetworkConnection_THolder sncHolder = new SubnetworkConnection_THolder();

		mlsnManager.getSNC(nameAndStringValueArray, sncHolder);

		if (sncHolder != null && sncHolder.value != null) {
			if (LOG.isInfoEnabled()) {
				LOG.info("SNC Name:" + sncHolder.value.userLabel);
				LOG.info("SNC State:" + sncHolder.value.sncState);
			}
			handler.printStructure(helper
					.getSubnetworkConnectionParams(sncHolder.value));
		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("No SNC Found for SNC Name:" + sncName);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getSNC() complete.");
		}

	}

	public StringHolder establishCall(CallCreateData_T callCreateData,
			SNCCreateData_T[] connectionCreateDataList,
			TPDataList_THolder tpsToModify) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("establishCall() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		CallAndTopLevelConnections_THolder callAndTopLevelConnections = new CallAndTopLevelConnections_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.establishCall(callCreateData,
				connectionCreateDataList, callAndTopLevelConnections,
				tpsToModify, errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info("establishCall() complete.");

			if (errorReason != null && errorReason.value != null
					&& !"".equals(errorReason.value)) {
				LOG.info("establishCall() Error Reason:" + errorReason.value);
			}
		}

		return errorReason;
	}

	public StringHolder createAndActivateFDFr(FDFrCreateData_T createData,
			ConnectivityRequirement_T connectivityRequirement,
			NamingAttributesList_THolder aEnd,
			NamingAttributesList_THolder zEnd,
			NamingAttributesList_THolder internalTPs,
			MatrixFlowDomainFragmentList_THolder mfdfrs,
			TPDataList_THolder tpsToModify) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("createAndActivateFDFr() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		FlowDomainFragment_THolder theFDFr = new FlowDomainFragment_THolder();
		StringHolder errorReason = new StringHolder();

		this.flowDomainManager.createAndActivateFDFr(createData,
				connectivityRequirement, aEnd, zEnd, internalTPs, mfdfrs,
				tpsToModify, theFDFr, errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info("createAndActivateFDFr() complete.");

			if (errorReason != null && errorReason.value != null
					&& !"".equals(errorReason.value)) {
				LOG.info("createAndActivateFDFr() Error Reason:"
						+ errorReason.value);
			}
		}

		return errorReason;

	}

	public void getContainedInUseTPs() throws ProcessingFailureException,
			SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getContainedInUseTPs() start.");
		}

		if (this.tpNames == null
				|| (this.tpNames != null && this.tpNames.size() <= 0)) {

			if (!setManagerByName(MLS_MANAGER_NAME))
				return;

			if (LOG.isInfoEnabled()) {
				LOG.info("getContainedInUseTPs() start.");
			}

			NameAndStringValue_T[] mlsn = new NameAndStringValue_T[2];

			mlsn[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
			mlsn[1] = new NameAndStringValue_T(
					CorbaConstants.MULTILAYER_SUBNETWORK_STR, "MLSN_1");

			this.tpNames = new ArrayList<NameAndStringValue_T[]>();

			TopologicalLinkList_THolder topologicalLinkList = new TopologicalLinkList_THolder();
			TopologicalLinkIterator_IHolder topologicalLinkIterator = new TopologicalLinkIterator_IHolder();

			try {
				mlsnManager.getAllTopologicalLinks(mlsn, HOW_MANY,
						topologicalLinkList, topologicalLinkIterator);

				for (int i = 0; i < topologicalLinkList.value.length; i++) {
					if (topologicalLinkList.value[i].aEndTP != null) {
						this.tpNames.add(topologicalLinkList.value[i].aEndTP);
					}

					if (topologicalLinkList.value[i].zEndTP != null) {
						this.tpNames.add(topologicalLinkList.value[i].zEndTP);
					}
				}

				boolean exitWhile = false;
				if (topologicalLinkIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = topologicalLinkIterator.value.next_n(
									HOW_MANY, topologicalLinkList);
							for (int i = 0; i < topologicalLinkList.value.length; i++) {
								if (topologicalLinkList.value[i].aEndTP != null) {
									this.tpNames
											.add(topologicalLinkList.value[i].aEndTP);
								}

								if (topologicalLinkList.value[i].zEndTP != null) {
									this.tpNames
											.add(topologicalLinkList.value[i].zEndTP);
								}
							}
						}

						exitWhile = true;
					} finally {
						if (!exitWhile) {
							topologicalLinkIterator.value.destroy();
						}
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex,
						"getContainedInUseTPs. MLS: " + mlsn[1].value);
			}
		}

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return;
		}

		LOG.info("TP Names List Size:" + this.tpNames != null
				&& this.tpNames.size() > 0 ? String.valueOf(this.tpNames.size())
				: "0");

		if (this.tpNames != null && this.tpNames.size() > 0) {

			short[] tpLayerRateList = new short[0];

			TerminationPointList_THolder terminationPointList = new TerminationPointList_THolder();
			TerminationPointIterator_IHolder terminationPointIterator = new TerminationPointIterator_IHolder();

			boolean exitWhile = false;
			int counter = 0;

			for (NameAndStringValue_T[] eachTPName : this.tpNames) {
				try {
					if (LOG.isInfoEnabled()) {
						LOG.info("getContainedInUseTPs: " + eachTPName);
					}

					meManager.getContainedInUseTPs(eachTPName, tpLayerRateList,
							HOW_MANY, terminationPointList,
							terminationPointIterator);

					for (int i = 0; i < terminationPointList.value.length; i++) {
						helper.printTerminationPoint(
								terminationPointList.value[i],
								CorbaConstants.IN_USE_TPS_STR);
					}

					exitWhile = false;

					if (terminationPointIterator.value != null) {
						try {
							boolean hasMoreData = true;
							while (hasMoreData) {
								hasMoreData = terminationPointIterator.value
										.next_n(HOW_MANY, terminationPointList);
								if (LOG.isInfoEnabled()) {
									LOG.info(
											"getContainedInUseTPs: got {} InUseTP for ME {}.",
											terminationPointList.value.length,
											eachTPName[1].value);
								}

								for (int i = 0; i < terminationPointList.value.length; i++) {
									helper.printTerminationPoint(
											terminationPointList.value[i],
											CorbaConstants.IN_USE_TPS_STR);
								}
							}
							exitWhile = true;
						} finally {
							if (!exitWhile) {
								terminationPointIterator.value.destroy();
							}
						}

						counter++;

						if (LOG.isDebugEnabled()) {
							LOG.debug("getContainedInUseTPs: finished getContainedInUseTP for Port "
									+ eachTPName[1].value
									+ " Order number # "
									+ counter);
						}
					}
				} catch (ProcessingFailureException ex) {
					handleProcessingFailureException(ex,
							"getContainedInUseTPs. ");
				}
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("getContainedInUseTPs() complete.");
			}
		}
	}

	public EquipmentConfigurationData_T configureEquipment(
			EquipmentConfigurationData_T equipmentConfiguredData)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("configureEquipment() start.");
		}

		if (!setManagerByName(EICIENA_MANAGER_NAME)) {
			return null;
		}

		EquipmentConfigurationData_THolder outputEquipmentConfigHolder = new EquipmentConfigurationData_THolder();

		this.equipmentCIENAManager.configureEquipment(equipmentConfiguredData,
				outputEquipmentConfigHolder);

		if (LOG.isInfoEnabled()) {
			LOG.info("configureEquipment() complete.");

		}

		EquipmentConfigurationData_T equipmentConfig = null;

		if (outputEquipmentConfigHolder != null) {
			equipmentConfig = outputEquipmentConfigHolder.value;
		}

		return equipmentConfig;
	}

	public void getEquipmentConfiguration() throws ProcessingFailureException,
			SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getEquipmentConfiguration() start.");
		}

		if (!setManagerByName(EICIENA_MANAGER_NAME)) {
			return;
		}

		if (this.slotNames == null
				|| (this.slotNames != null && this.slotNames.size() <= 0)) {

			List<EquipmentOrHolder_T> equipmentList = getAllEquipmentList();

			if (equipmentList != null && equipmentList.size() > 0) {
				for (int i = 0; i < equipmentList.size(); i++) {
					EquipmentOrHolder_T eachEquipmentOrHolder = equipmentList
							.get(i);

					if (eachEquipmentOrHolder != null
							&& eachEquipmentOrHolder.discriminator().value() != 1) {

						Equipment_T equipment = eachEquipmentOrHolder.equip();
						if (equipment != null
								&& ("TSLM-12"
										.equals(equipment.installedEquipmentObjectType) || "TSLM-48"
										.equals(equipment.installedEquipmentObjectType))) {

							NameAndStringValue_T[] slotNameAndStringValueArray = new NameAndStringValue_T[3];
							slotNameAndStringValueArray[0] = equipment.name[0];
							slotNameAndStringValueArray[1] = equipment.name[1];

							if ("EquipmentHolder"
									.equals(equipment.name[2].name)) {
								slotNameAndStringValueArray[2] = equipment.name[2];
							} else if (equipment.name.length > 3
									&& "EquipmentHolder"
											.equals(equipment.name[3].name)) {
								slotNameAndStringValueArray[2] = equipment.name[3];
							}

							slotNames.add(slotNameAndStringValueArray);
						}
					}
				}
			} else {
				if (LOG.isInfoEnabled()) {
					LOG.info("getAllEquipment() got 0 Equipments");
				}
			}
		}

		LOG.info("Slot Names List Size:" + this.slotNames != null
				&& this.slotNames.size() > 0 ? String.valueOf(this.slotNames
				.size()) : "0");

		if (this.slotNames != null && this.slotNames.size() > 0) {

			EquipmentConfigurationData_THolder equipmentConfigDataHolder = new EquipmentConfigurationData_THolder();
			String[] eqtParametersList = new String[0];

			for (NameAndStringValue_T[] eachSlotNameArray : this.slotNames) {

				try {
					this.equipmentCIENAManager.getEquipmentConfiguration(
							eachSlotNameArray, eqtParametersList,
							equipmentConfigDataHolder);

					if (equipmentConfigDataHolder != null
							&& equipmentConfigDataHolder.value != null) {

						handler.printStructure(helper
								.printEquipmentConfiguration(equipmentConfigDataHolder.value));
					} else {
						LOG.info("equipmentConfigDataHolder is empty");
					}

				} catch (ProcessingFailureException ex) {
					handleProcessingFailureException(ex,
							"getEquipmentConfiguration. ");
				}
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("getEquipmentConfiguration() complete.");
			}
		}
	}

	public TerminationPoint_T setTPData(TPData_T tpData)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("setTPData() start.");
		}

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return null;
		}

		TerminationPoint_THolder terminationPointHolder = new TerminationPoint_THolder();

		this.meManager.setTPData(tpData, terminationPointHolder);

		if (LOG.isInfoEnabled()) {
			LOG.info("setTPData() complete.");

		}

		TerminationPoint_T terminationPoint = null;

		if (terminationPointHolder != null) {
			terminationPoint = terminationPointHolder.value;
		}

		return terminationPoint;
	}

	public void handleProcessingFailureException(
			ProcessingFailureException pfe, String param)
			throws ProcessingFailureException {
		CorbaErrorProcessor err = new CorbaErrorProcessor(pfe);

		if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
			LOG.error("Ciena OC>> " + param + ", " + err.printError()
					+ " It is a major error. Stop interaction with server", pfe);

			throw pfe;
		} else {
			LOG.error("Ciena OC>> " + param + err.printError()
					+ " It is a minor error. Continue interaction with server",
					pfe);
		}
	}
}
