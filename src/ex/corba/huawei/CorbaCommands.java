package ex.corba.huawei;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.huawei.u2000.HW_mstpInventory.HW_MSTPBindingPathList_THolder;
import com.huawei.u2000.HW_mstpInventory.HW_MSTPEndPointIterator_I;
import com.huawei.u2000.HW_mstpInventory.HW_MSTPEndPointIterator_IHolder;
import com.huawei.u2000.HW_mstpInventory.HW_MSTPEndPointList_THolder;
import com.huawei.u2000.HW_mstpInventory.HW_MSTPEndPointType_T;
import com.huawei.u2000.HW_mstpInventory.HW_MSTPEndPoint_T;
import com.huawei.u2000.HW_mstpInventory.HW_MSTPEndPoint_THolder;
import com.huawei.u2000.HW_mstpInventory.HW_MSTPInventoryMgr_I;
import com.huawei.u2000.HW_mstpInventory.HW_MSTPInventoryMgr_IHelper;
import com.huawei.u2000.HW_mstpService.HW_EthServiceCreateData_T;
import com.huawei.u2000.HW_mstpService.HW_EthServiceIterator_I;
import com.huawei.u2000.HW_mstpService.HW_EthServiceIterator_IHolder;
import com.huawei.u2000.HW_mstpService.HW_EthServiceList_THolder;
import com.huawei.u2000.HW_mstpService.HW_EthServiceType_T;
import com.huawei.u2000.HW_mstpService.HW_EthService_T;
import com.huawei.u2000.HW_mstpService.HW_MSTPServiceMgr_I;
import com.huawei.u2000.HW_mstpService.HW_MSTPServiceMgr_IHelper;
import com.huawei.u2000.common.Common_IHolder;
import com.huawei.u2000.emsMgr.EMSMgr_I;
import com.huawei.u2000.emsMgr.EMSMgr_IHelper;
import com.huawei.u2000.emsSession.EmsSession_I;
import com.huawei.u2000.equipment.EquipmentInventoryMgr_I;
import com.huawei.u2000.equipment.EquipmentInventoryMgr_IHelper;
import com.huawei.u2000.equipment.EquipmentOrHolderIterator_IHolder;
import com.huawei.u2000.equipment.EquipmentOrHolderList_THolder;
import com.huawei.u2000.globaldefs.NameAndStringValue_T;
import com.huawei.u2000.globaldefs.NamingAttributesIterator_IHolder;
import com.huawei.u2000.globaldefs.NamingAttributesList_THolder;
import com.huawei.u2000.globaldefs.ProcessingFailureException;
import com.huawei.u2000.managedElement.ManagedElementIterator_IHolder;
import com.huawei.u2000.managedElement.ManagedElementList_THolder;
import com.huawei.u2000.managedElement.ManagedElement_T;
import com.huawei.u2000.managedElementManager.ManagedElementMgr_I;
import com.huawei.u2000.managedElementManager.ManagedElementMgr_IHelper;
import com.huawei.u2000.multiLayerSubnetwork.EMSFreedomLevel_T;
import com.huawei.u2000.multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import com.huawei.u2000.multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;
import com.huawei.u2000.protection.ProtectionGroupIterator_IHolder;
import com.huawei.u2000.protection.ProtectionGroupList_THolder;
import com.huawei.u2000.protection.ProtectionMgr_I;
import com.huawei.u2000.protection.ProtectionMgr_IHelper;
import com.huawei.u2000.subnetworkConnection.CrossConnect_T;
import com.huawei.u2000.subnetworkConnection.GradesOfImpact_T;
import com.huawei.u2000.subnetworkConnection.Route_THolder;
import com.huawei.u2000.subnetworkConnection.SNCCreateData_T;
import com.huawei.u2000.subnetworkConnection.SNCIterator_IHolder;
import com.huawei.u2000.subnetworkConnection.SubnetworkConnectionList_THolder;
import com.huawei.u2000.subnetworkConnection.SubnetworkConnection_T;
import com.huawei.u2000.subnetworkConnection.SubnetworkConnection_THolder;
import com.huawei.u2000.subnetworkConnection.TPDataList_THolder;
import com.huawei.u2000.terminationPoint.Directionality_T;
import com.huawei.u2000.terminationPoint.TerminationPointIterator_IHolder;
import com.huawei.u2000.terminationPoint.TerminationPointList_THolder;
import com.huawei.u2000.terminationPoint.TerminationPoint_T;
import com.huawei.u2000.terminationPoint.TerminationPoint_THolder;
import com.huawei.u2000.topologicalLink.TopologicalLinkIterator_IHolder;
import com.huawei.u2000.topologicalLink.TopologicalLinkList_THolder;
import com.huawei.u2000.topologicalLink.TopologicalLink_T;
import com.huawei.u2000.transmissionParameters.LayeredParameters_T;

import ex.corba.CorbaConstants;
import ex.corba.huawei.error.CorbaErrorDescriptions;
import ex.corba.huawei.error.CorbaErrorProcessor;
import ex.corba.huawei.transform.sax.Corba2XMLContainer;
import ex.corba.huawei.transform.sax.Corba2XMLHandler;
import ex.corba.huawei.transform.sax.Corba2XMLHelper;
import ex.corba.huawei.transform.sax.Corba2XMLStructure;

public class CorbaCommands {
	public static final String ME_MANAGER_NAME = "ManagedElement";
	public static final String EI_MANAGER_NAME = "EquipmentInventory";
	public static final String MLS_MANAGER_NAME = "MultiLayerSubnetwork";
	public static final String EMS_MANAGER_NAME = "EMS";
	public static final String PRT_MANAGER_NAME = "Protection";
	public static final String MSTP_SERVICE_MANAGER_NAME = "CORBA_MSTP_SVC";
	public static final String MSTP_INVENTORY_MANAGER_NAME = "CORBA_MSTP_INV";

	public static final int HOW_MANY = 100;

	public static final Logger LOG = LoggerFactory.getLogger(CorbaCommands.class);

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
	private HW_MSTPServiceMgr_I mstpServiceManager;
	private HW_MSTPInventoryMgr_I mstpInvertoryManager;

	// Cache list
	private List<String> neNames;
	private List<String> sncNames;
	private List<String> subnetworkNames;
	private List<HW_MSTPEndPoint_T> mstpEndPointList;

	private Set<Short> terminationPointRates;

	public CorbaCommands(EmsSession_I emsSession, String emsName) {
		this.emsSession = emsSession;
		this.emsName = emsName;
	}

	public CorbaCommands(EmsSession_I emsSession, String emsName, ContentHandler contentHandler) {
		this.emsSession = emsSession;
		this.emsName = emsName;
		this.handler = new Corba2XMLHandler(contentHandler);
		this.helper = new Corba2XMLHelper(handler);
	}

	public boolean setManagerByName(final String managerName) throws ProcessingFailureException {

		this.managerInterface = new Common_IHolder();
		this.emsSession.getManager(managerName, this.managerInterface);

		if (managerName.equals(ME_MANAGER_NAME)) {
			if (this.meManager == null) {
				this.meManager = ManagedElementMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(EI_MANAGER_NAME)) {
			if (this.eiManager == null) {
				this.eiManager = EquipmentInventoryMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(MLS_MANAGER_NAME)) {
			if (this.mlsnManager == null) {
				this.mlsnManager = MultiLayerSubnetworkMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(EMS_MANAGER_NAME)) {
			if (this.emsManager == null) {
				this.emsManager = EMSMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(PRT_MANAGER_NAME)) {
			if (this.protectionMgr == null) {
				this.protectionMgr = ProtectionMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(MSTP_SERVICE_MANAGER_NAME)) {
			if (this.mstpServiceManager == null) {
				this.mstpServiceManager = HW_MSTPServiceMgr_IHelper.narrow(managerInterface.value);
			}
		} else if (managerName.equals(MSTP_INVENTORY_MANAGER_NAME)) {
			if (this.mstpInvertoryManager == null) {
				this.mstpInvertoryManager = HW_MSTPInventoryMgr_IHelper.narrow(managerInterface.value);
			}
		} else
			return false;

		return true;
	}

	public List<String> getAllManagedElementNames() throws ProcessingFailureException, SAXException {
		System.out.println("getAllManagedElementNames...");

		if (!setManagerByName(ME_MANAGER_NAME)) {
			return null;
		}

		NamingAttributesList_THolder meNameList = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder meNameItr = new NamingAttributesIterator_IHolder();

		this.meManager.getAllManagedElementNames(HOW_MANY, meNameList, meNameItr);

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
							if (meNameList.value[i][j].name.equals("ManagedElement"))
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
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllManagedElements() start.");
		}

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
						LOG.debug("getAllManagedElements: got " + mes.length + " MEs ");

					for (ManagedElement_T me : mes) {
						handler.printStructure(helper.getManagedElementParams(me));
						neNames.add(handler.getValueByName(me.name, "ManagedElement"));
					}
				}

				exitWhile = true;
			} finally {
				if (!exitWhile)
					meItr.value.destroy();
			}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllManagedElements() complete.");
		}
	}

	public void getAllEquipment() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEquipment() start.");
		}

		if (neNames == null) {
			neNames = getAllManagedElementNames();
		}

		if (!setManagerByName(EI_MANAGER_NAME))
			return;

		NameAndStringValue_T[] ne = new NameAndStringValue_T[2];

		ne[0] = new NameAndStringValue_T("EMS", emsName);
		ne[1] = new NameAndStringValue_T();
		ne[1].name = "ManagedElement";

		EquipmentOrHolderList_THolder equipOrHolderList = new EquipmentOrHolderList_THolder();
		EquipmentOrHolderIterator_IHolder equipOrHolderItr = new EquipmentOrHolderIterator_IHolder();

		int meCounter = 0;
		boolean exitWhile = false;

		for (String neName : neNames) {
			try {
				ne[1].value = neName;
				eiManager.getAllEquipment(ne, HOW_MANY, equipOrHolderList, equipOrHolderItr);

				System.out.println(
						"getAllEquipment: got " + equipOrHolderList.value.length + " equipments for ME " + ne[1].value);

				for (int i = 0; i < equipOrHolderList.value.length; i++) {
					helper.printEquipmentOrHolder(equipOrHolderList.value[i]);
				}

				exitWhile = false;

				if (equipOrHolderItr.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = equipOrHolderItr.value.next_n(HOW_MANY, equipOrHolderList);

							for (int i = 0; i < equipOrHolderList.value.length; i++) {
								helper.printEquipmentOrHolder(equipOrHolderList.value[i]);
							}
						}

						exitWhile = true;
					} finally {
						if (!exitWhile)
							equipOrHolderItr.value.destroy();
					}
				}

				meCounter++;

				System.out.println("getAllEquipment: finished getEquipment for ME " + ne[1].value + " Order number # "
						+ meCounter);
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllEquipment. ME: " + neName);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEquipment() complete.");
		}
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

		neNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
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
				meManager.getAllPTPs(neNameArray, tpLayerRateList, connectionLayerRateList, HOW_MANY,
						terminationPointList, terminationPointIterator);

				if (LOG.isInfoEnabled()) {
					LOG.info("getAllPTPs: got {} PTP for ME {}.", terminationPointList.value.length,
							neNameArray[1].value);
				}

				for (int i = 0; i < terminationPointList.value.length; i++) {
					helper.printTerminationPoint(terminationPointList.value[i]);
				}

				exitWhile = false;

				if (terminationPointIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = terminationPointIterator.value.next_n(HOW_MANY, terminationPointList);
							if (LOG.isInfoEnabled()) {
								LOG.info("getAllPTPs: got {} PTP for ME {}.", terminationPointList.value.length,
										neNameArray[1].value);
							}

							for (int i = 0; i < terminationPointList.value.length; i++) {
								helper.printTerminationPoint(terminationPointList.value[i]);
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
						LOG.debug("getAllPTPs: finished getPTP for ME " + neNameArray[1].value + " Order number # "
								+ counter);
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllPTPs. ME: " + neName);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllPTPs() complete.");
		}
	}

	public void getAllTopologicalLinks() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopologicalLinks() start.");
		}

		if (subnetworkNames == null) {
			subnetworkNames = getAllTopLevelSubnetworkNames();
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] mlsn = new NameAndStringValue_T[2];

		mlsn[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		mlsn[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, "1");

		TopologicalLinkList_THolder topologicalLinkList = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder topologicalLinkIterator = new TopologicalLinkIterator_IHolder();

		for (String subnetwork : subnetworkNames) {
			try {
				mlsn[1].value = subnetwork;
				mlsnManager.getAllTopologicalLinks(mlsn, HOW_MANY, topologicalLinkList, topologicalLinkIterator);

				for (int i = 0; i < topologicalLinkList.value.length; i++) {
					handler.printStructure(getTopologicalLinkParams(topologicalLinkList.value[i]));
				}

				boolean exitWhile = false;
				if (topologicalLinkIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = topologicalLinkIterator.value.next_n(HOW_MANY, topologicalLinkList);
							for (int i = 0; i < topologicalLinkList.value.length; i++) {
								handler.printStructure(getTopologicalLinkParams(topologicalLinkList.value[i]));
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
				handleProcessingFailureException(ex, "getAllTopologicalLinks. MLS: " + mlsn[1].value);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopologicalLinks() complete.");
		}
	}

	private Corba2XMLContainer getTopologicalLinkParams(TopologicalLink_T topologicalLink)
			throws ProcessingFailureException {
		Corba2XMLContainer container = new Corba2XMLContainer(Corba2XMLStructure.TOPOL_LINKS);

		container.setFieldValue(CorbaConstants.USER_LABEL_STR, topologicalLink.userLabel);
		container.setFieldValue(CorbaConstants.TL_ID_STR,
				handler.getValueByName(topologicalLink.name, "TopologicalLink"));
		container.setFieldValue(CorbaConstants.NATIVE_EMS_NAME_STR, topologicalLink.nativeEMSName);
		container.setFieldValue(CorbaConstants.OWNER_STR, topologicalLink.owner);
		container.setFieldValue(CorbaConstants.DIRECTION_STR, String.valueOf(topologicalLink.direction.value()));
		container.setFieldValue(CorbaConstants.RATE_STR, String.valueOf(topologicalLink.rate));
		container.setFieldValue(CorbaConstants.A_END_NE_STR,
				handler.getValueByName(topologicalLink.aEndTP, CorbaConstants.MANAGED_ELEMENT_STR));
		container.setFieldValue(CorbaConstants.A_END_TP_STR,
				handler.getValueByName(topologicalLink.aEndTP, CorbaConstants.PTP_STR));
		container.setFieldValue(CorbaConstants.Z_END_NE_STR,
				handler.getValueByName(topologicalLink.zEndTP, CorbaConstants.MANAGED_ELEMENT_STR));
		container.setFieldValue(CorbaConstants.Z_END_TP_STR,
				handler.getValueByName(topologicalLink.zEndTP, CorbaConstants.PTP_STR));

		TerminationPoint_T aEndTP = getTerminationPoint(topologicalLink.aEndTP, topologicalLink.rate);

		if (aEndTP != null) {
			container.setFieldValue(CorbaConstants.A_TRANSMISSION_PARAMS_STR,
					handler.convertLayeredParametersToString(aEndTP.transmissionParams));
		} else {
			container.setFieldValue(CorbaConstants.A_TRANSMISSION_PARAMS_STR, "");
		}

		TerminationPoint_T zEndTP = getTerminationPoint(topologicalLink.zEndTP, topologicalLink.rate);
		if (zEndTP != null) {
			container.setFieldValue(CorbaConstants.Z_TRANSMISSION_PARAMS_STR,
					handler.convertLayeredParametersToString(zEndTP.transmissionParams));
		} else {
			container.setFieldValue(CorbaConstants.Z_TRANSMISSION_PARAMS_STR, "");
		}

		container.setFieldValue(CorbaConstants.ADDITIONAL_INFO_STR,
				handler.convertNameAndStringValueToString(topologicalLink.additionalInfo));

		return container;
	}

	private TerminationPoint_T getTerminationPoint(NameAndStringValue_T[] endPointName, short rate)
			throws ProcessingFailureException {

		if (!setManagerByName(ME_MANAGER_NAME))
			return null;

		if (terminationPointRates == null
				|| (terminationPointRates != null && !terminationPointRates.contains(Short.valueOf(rate)))) {
			return null;
		}

		TerminationPoint_THolder tpHolder = new TerminationPoint_THolder();

		try {
			meManager.getTP(endPointName, tpHolder);
		} catch (ProcessingFailureException ex) {
			handleProcessingFailureException(ex,
					"getTerminationPoint. TP: " + handler.convertNameAndStringValueToString(endPointName));
		}

		return tpHolder.value;
	}

	public List<String> getAllTopLevelSubnetworkNames() throws ProcessingFailureException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopLevelSubnetworkNames() start.");
		}

		if (!setManagerByName(EMS_MANAGER_NAME)) {
			return null;
		}

		NamingAttributesList_THolder namingAttributesList = new NamingAttributesList_THolder();
		NamingAttributesIterator_IHolder namingAttributesIterator = new NamingAttributesIterator_IHolder();

		emsManager.getAllTopLevelSubnetworkNames(HOW_MANY, namingAttributesList, namingAttributesIterator);

		List<String> arrayList = new ArrayList<String>();

		for (int i = 0; i < namingAttributesList.value.length; i++) {
			for (int j = 0; j < namingAttributesList.value[i].length; j++) {
				if (namingAttributesList.value[i][j].name.equals(CorbaConstants.MULTILAYER_SUBNETWORK_STR)) {
					arrayList.add(namingAttributesList.value[i][j].value);
				}
			}
		}

		boolean exitwhile = false;
		if (namingAttributesIterator.value != null) {
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = namingAttributesIterator.value.next_n(HOW_MANY, namingAttributesList);
					for (int i = 0; i < namingAttributesList.value.length; i++) {
						for (int j = 0; j < namingAttributesList.value[i].length; j++) {
							if (namingAttributesList.value[i][j].name
									.equals(CorbaConstants.MULTILAYER_SUBNETWORK_STR)) {
								arrayList.add(namingAttributesList.value[i][j].value);
							}
						}
					}
				}
				exitwhile = true;
			} finally {
				if (!exitwhile) {
					namingAttributesIterator.value.destroy();
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllTopLevelSubnetworkNames() got {} top level subnetworks.", arrayList.size());
			LOG.info("getAllTopLevelSubnetworkNames() complete.");
		}

		return arrayList;
	}

	public List<String> getAllSubnetworkConnections() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllSubnetworkConnections() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[2];

		nameAndStringValueArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		nameAndStringValueArray[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, "1");

		SubnetworkConnectionList_THolder sncList = new SubnetworkConnectionList_THolder();
		SNCIterator_IHolder sncIterator = new SNCIterator_IHolder();
		short[] rateList = new short[0];

		mlsnManager.getAllSubnetworkConnections(nameAndStringValueArray, rateList, HOW_MANY, sncList, sncIterator);
		sncNames = new ArrayList<String>();

		for (int i = 0; i < sncList.value.length; i++) {
			sncNames.add(handler.getValueByName(sncList.value[i].name, CorbaConstants.SUBNETWORK_CONNECTION_STR));
			handler.printStructure(helper.getSubnetworkConnectionParams(sncList.value[i]));
		}

		boolean exitWhile = false;
		if (sncIterator.value != null) {
			try {
				boolean hasMoreData = true;
				while (hasMoreData) {
					hasMoreData = sncIterator.value.next_n(HOW_MANY, sncList);
					for (int i = 0; i < sncList.value.length; i++) {
						sncNames.add(handler.getValueByName(sncList.value[i].name,
								CorbaConstants.SUBNETWORK_CONNECTION_STR));
						handler.printStructure(helper.getSubnetworkConnectionParams(sncList.value[i]));
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
			LOG.info("getAllSubnetworkConnections() complete.");
		}

		return sncNames;
	}

	public void getRoute() throws Exception {
		if (LOG.isInfoEnabled()) {
			LOG.info("getRoute() start.");
		}

		if (sncNames == null) {
			sncNames = getAllSubnetworkConnections();
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] sncNameArray = new NameAndStringValue_T[3];

		sncNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		sncNameArray[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, "1");
		sncNameArray[2] = new NameAndStringValue_T();
		sncNameArray[2].name = CorbaConstants.SUBNETWORK_CONNECTION_STR;

		Route_THolder routeHolder = new Route_THolder();
		Iterator<String> iter = sncNames.iterator();

		while (iter.hasNext()) {
			sncNameArray[2].value = iter.next();

			try {
				mlsnManager.getRoute(sncNameArray, true, routeHolder);

				for (CrossConnect_T crossConnect : routeHolder.value) {
					handler.printStructure(helper.getRouteParams(crossConnect, sncNameArray[2].value));
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getRoute. SNC: " + sncNameArray[2].value);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getRoute() complete.");
		}
	}

	public void getSNCsByUserLabel(String userLabel) throws ProcessingFailureException, SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getSNCsByUserLabel() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		SubnetworkConnectionList_THolder sncListHolder = new SubnetworkConnectionList_THolder();

		mlsnManager.getSNCsByUserLabel(userLabel, sncListHolder);

		String sncID = "";

		if (sncListHolder != null && sncListHolder.value != null && sncListHolder.value.length > 0) {
			for (SubnetworkConnection_T snc : sncListHolder.value) {
				sncID = handler.getValueByName(snc.name, CorbaConstants.SUBNETWORK_CONNECTION_STR);
				handler.printStructure(helper.getSubnetworkConnectionParams(snc));
			}
		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("No SNC Found for SNC Name:" + userLabel);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getSNCsByUserLabel() complete. sncID: " + sncID);
		}

	}

	public void getSNCsByUserLabelAndRoutes(String userLabel) throws ProcessingFailureException, SAXException {

		if (LOG.isInfoEnabled()) {
			LOG.info("getSNCsByUserLabelAndRoutes() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		SubnetworkConnectionList_THolder sncListHolder = new SubnetworkConnectionList_THolder();

		mlsnManager.getSNCsByUserLabel(userLabel, sncListHolder);

		String sncID = "";

		if (sncListHolder != null && sncListHolder.value != null) {
			for (SubnetworkConnection_T snc : sncListHolder.value) {
				sncID = handler.getValueByName(snc.name, CorbaConstants.SUBNETWORK_CONNECTION_STR);
				handler.printStructure(helper.getSubnetworkConnectionParams(snc));
			}
		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("No SNC Found for SNC Name:" + userLabel);
			}

			return;
		}

		NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[3];

		nameAndStringValueArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		nameAndStringValueArray[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, "1");
		nameAndStringValueArray[2] = new NameAndStringValue_T(CorbaConstants.SUBNETWORK_CONNECTION_STR, sncID);

		Route_THolder routeHolder = new Route_THolder();

		try {
			mlsnManager.getRoute(nameAndStringValueArray, true, routeHolder);

			for (CrossConnect_T crossConnect : routeHolder.value) {
				handler.printStructure(helper.getRouteParams(crossConnect, nameAndStringValueArray[2].value));
			}
		} catch (ProcessingFailureException ex) {
			handleProcessingFailureException(ex, "getRoute. SNC: " + nameAndStringValueArray[2].value);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getSNCsByUserLabelAndRoutes() complete.");
		}

	}

	// getSNC by SNC ID
	public void getSNCAndRoute(String sncID) throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getSNCAndRoute() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return;

		NameAndStringValue_T[] snc = new NameAndStringValue_T[3];

		snc[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		snc[1] = new NameAndStringValue_T(CorbaConstants.MULTILAYER_SUBNETWORK_STR, "1");
		snc[2] = new NameAndStringValue_T(CorbaConstants.SUBNETWORK_CONNECTION_STR, sncID);

		SubnetworkConnection_THolder sncHolder = new SubnetworkConnection_THolder();

		mlsnManager.getSNC(snc, sncHolder);

		if (sncHolder != null && sncHolder.value != null) {
			if (LOG.isInfoEnabled()) {
				LOG.info("SNC User Label:" + sncHolder.value.userLabel);
				LOG.info("SNC State:" + sncHolder.value.sncState);
			}

			handler.printStructure(helper.getSubnetworkConnectionParams(sncHolder.value));
		} else {
			if (LOG.isInfoEnabled()) {
				LOG.info("No SNC Found for SNC Name:" + sncID);
			}

			return;
		}

		Route_THolder routeHolder = new Route_THolder();

		try {
			mlsnManager.getRoute(snc, true, routeHolder);

			for (CrossConnect_T crossConnect : routeHolder.value) {
				handler.printStructure(helper.getRouteParams(crossConnect, snc[2].value));
			}
		} catch (ProcessingFailureException ex) {
			handleProcessingFailureException(ex, "getRoute. SNC: " + snc[2].value);
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getSNCAndRoute() complete.");
		}

	}

	public void getAllProtectionGroups() throws ProcessingFailureException, SAXException {

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
				protectionMgr.getAllProtectionGroups(nameAndStringValueArray, HOW_MANY, protectionGroupList,
						protectionGroupIterator);

				if (LOG.isDebugEnabled()) {
					LOG.debug("getAllProtectionGroups: got " + protectionGroupList.value.length
							+ " pieces of ProtectionGroup for ME " + nameAndStringValueArray[1].value);
				}

				helper.printProtectionGroup(protectionGroupList.value);

				exitwhile = false;
				if (protectionGroupIterator.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = protectionGroupIterator.value.next_n(HOW_MANY, protectionGroupList);

							if (LOG.isDebugEnabled()) {
								LOG.debug("getAllProtectionGroups: got next " + protectionGroupList.value.length
										+ " pieces of ProtectionGroup for ME " + nameAndStringValueArray[1].value);
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
								+ nameAndStringValueArray[1].value + " Order number # " + counter);
					}
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllProtectionGroups. ME: " + n);
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllProtectionGroups() complete");
		}
	}

	public void getAllMstpEndPoints() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllMstpEndPoints() start.");
		}

		if (!setManagerByName(MSTP_INVENTORY_MANAGER_NAME))
			return;

		if (neNames == null) {
			neNames = getAllManagedElementNames();
		}

		NameAndStringValue_T[] ne = new NameAndStringValue_T[2];
		ne[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		ne[1] = new NameAndStringValue_T();
		ne[1].name = CorbaConstants.MANAGED_ELEMENT_STR;

		HW_MSTPEndPointList_THolder listHolder = new HW_MSTPEndPointList_THolder();
		HW_MSTPEndPointIterator_IHolder iteratorHolder = new HW_MSTPEndPointIterator_IHolder();

		HW_MSTPEndPointIterator_I iterator = null;

		for (String neName : neNames) {
			ne[1].value = neName;
			mstpInvertoryManager.getAllMstpEndPoints(ne, new HW_MSTPEndPointType_T[0], HOW_MANY, listHolder,
					iteratorHolder);

			if (LOG.isInfoEnabled()) {
				LOG.info("Got " + (listHolder.value != null ? listHolder.value.length : "0")
						+ " MstpEndPoints for managedElement: " + neName);
			}

			mstpEndPointList = helper.printMstpEndPoints(listHolder.value);

			iterator = iteratorHolder.value;
			if (iterator != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Iterator returned for managedElement: " + neName);
				}

				boolean hasMoreData = true;
				boolean exitedWhile = false;
				try {
					while (hasMoreData) {
						hasMoreData = iterator.next_n(HOW_MANY, listHolder);
						mstpEndPointList = helper.printMstpEndPoints(listHolder.value);
					}

					exitedWhile = true;
				} finally {
					if (!exitedWhile) {
						iterator.destroy();
					}
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllMstpEndPoints() complete.");
		}
	}

	public void getAllEthService() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEthService() start.");
		}

		if (!setManagerByName(MSTP_SERVICE_MANAGER_NAME))
			return;

		if (neNames == null) {
			neNames = getAllManagedElementNames();
		}

		NameAndStringValue_T[] ne = new NameAndStringValue_T[2];
		ne[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR, emsName);
		ne[1] = new NameAndStringValue_T();
		ne[1].name = CorbaConstants.MANAGED_ELEMENT_STR;

		HW_EthServiceList_THolder listHolder = new HW_EthServiceList_THolder();
		HW_EthServiceIterator_IHolder iteratorHolder = new HW_EthServiceIterator_IHolder();

		HW_EthServiceIterator_I iterator = null;

		for (String neName : neNames) {
			ne[1].value = neName;
			mstpServiceManager.getAllEthService(ne, new HW_EthServiceType_T[0], HOW_MANY, listHolder, iteratorHolder);

			if (LOG.isInfoEnabled()) {
				LOG.info("Got " + (listHolder.value != null ? listHolder.value.length : "0")
						+ " EthServices for managedElement: " + neName);
			}

			helper.printEthServices(listHolder.value);

			iterator = iteratorHolder.value;
			if (iterator != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Iterator returned for managedElement: " + neName);
				}

				boolean hasMoreData = true;
				boolean exitedWhile = false;
				try {
					while (hasMoreData) {
						hasMoreData = iterator.next_n(HOW_MANY, listHolder);
						helper.printEthServices(listHolder.value);
					}

					exitedWhile = true;
				} finally {
					if (!exitedWhile) {
						iterator.destroy();
					}
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getAllEthService() complete.");
		}
	}

	public void getBindingPath() throws ProcessingFailureException, SAXException {
		if (LOG.isInfoEnabled()) {
			LOG.info("getBindingPath() start.");
		}

		if (!setManagerByName(MSTP_INVENTORY_MANAGER_NAME))
			return;

		if (mstpEndPointList == null) {
			this.getAllMstpEndPoints();
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("No of VCTRUNK ports: " + mstpEndPointList.size());
		}

		HW_MSTPBindingPathList_THolder mstpBindingPathListHolder = null;

		for (HW_MSTPEndPoint_T mstpEndPoint : mstpEndPointList) {
			mstpBindingPathListHolder = new HW_MSTPBindingPathList_THolder();

			try {
				mstpInvertoryManager.getBindingPath(mstpEndPoint.name, mstpBindingPathListHolder);

				if (LOG.isInfoEnabled()) {
					LOG.info("getBindingPath: got " + mstpBindingPathListHolder.value.length
							+ " pieces of BindingPath for MSTP End point name " + mstpEndPoint.name);
				}

				for (int i = 0; i < mstpBindingPathListHolder.value.length; i++) {
					helper.printBindingPath(mstpBindingPathListHolder.value[i], mstpEndPoint.name);
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getBindingPath. VCTRUNK Port: "
						+ handler.convertNameAndStringValueToString(mstpEndPoint.name));
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getBindingPath() complete.");
		}
	}

	public StringHolder createAndActivateSNC(SNCCreateData_T createData, GradesOfImpact_T tolerableImpact,
			EMSFreedomLevel_T emsFreedomLevel, TPDataList_THolder tpsToModify) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("createAndActivateSNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		SubnetworkConnection_THolder theSNC = new SubnetworkConnection_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.createAndActivateSNC(createData, tolerableImpact, emsFreedomLevel, tpsToModify, theSNC,
				errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info("createAndActivateSNC() complete.");
		}

		return errorReason;
	}

	public StringHolder deactivateAndDeleteSNC(NameAndStringValue_T[] sncName, GradesOfImpact_T tolerableImpact,
			EMSFreedomLevel_T emsFreedomLevel, TPDataList_THolder tpsToModify) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("deactivateAndDeleteSNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		SubnetworkConnection_THolder theSNC = new SubnetworkConnection_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.deactivateAndDeleteSNC(sncName, tolerableImpact, emsFreedomLevel, tpsToModify, theSNC,
				errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info(" deactivateAndDeleteSNC() complete.");
		}

		return errorReason;
	}

	public HW_EthServiceList_THolder createEthService(HW_EthServiceCreateData_T createData)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("createEthService() start.");
		}

		if (!setManagerByName(MSTP_SERVICE_MANAGER_NAME))
			return null;

		HW_EthServiceList_THolder ethServiceList = new HW_EthServiceList_THolder();

		this.mstpServiceManager.createEthService(createData, ethServiceList);

		if (ethServiceList.value != null) {
			LOG.info("Created Ethernet Services: " + ethServiceList.value.length);

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

	public void deleteEthService(NameAndStringValue_T[] name) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("deleteEthService() start.");
		}

		if (!setManagerByName(MSTP_SERVICE_MANAGER_NAME))
			return;

		this.mstpServiceManager.deleteEthService(name);

		if (LOG.isInfoEnabled()) {
			LOG.info("deleteEthService() complete.");
		}
	}

	public HW_MSTPBindingPathList_THolder addBindingPath(NameAndStringValue_T[] vctrunkPort,
			Directionality_T directionlity, NameAndStringValue_T[][] pathList) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("addBindingPath() start.");
		}

		if (!setManagerByName(MSTP_INVENTORY_MANAGER_NAME))
			return null;

		HW_MSTPBindingPathList_THolder pathListHolder = new HW_MSTPBindingPathList_THolder();

		this.mstpInvertoryManager.addBindingPath(vctrunkPort, directionlity, pathList, pathListHolder);

		return pathListHolder;
	}

	public HW_MSTPBindingPathList_THolder delBindingPath(NameAndStringValue_T[] vctrunkPort,
			Directionality_T directionlity, NameAndStringValue_T[][] pathList) throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("addBindingPath() start.");
		}

		if (!setManagerByName(MSTP_INVENTORY_MANAGER_NAME))
			return null;

		HW_MSTPBindingPathList_THolder pathListHolder = new HW_MSTPBindingPathList_THolder();

		this.mstpInvertoryManager.delBindingPath(vctrunkPort, directionlity, pathList, pathListHolder);

		return pathListHolder;
	}

	public HW_MSTPEndPoint_THolder setMstpEndPoint(NameAndStringValue_T[] endPointName, LayeredParameters_T[] paraList)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("setMstpEndPoint() start.");
		}

		if (!setManagerByName(MSTP_INVENTORY_MANAGER_NAME))
			return null;

		HW_MSTPEndPoint_THolder endPoint = new HW_MSTPEndPoint_THolder();

		this.mstpInvertoryManager.setMstpEndPoint(endPointName, paraList, endPoint);

		if (LOG.isInfoEnabled()) {
			LOG.info("setMstpEndPoint() complete: out {}", endPoint.value);
		}

		return endPoint;
	}

	public void handleProcessingFailureException(ProcessingFailureException pfe, String param)
			throws ProcessingFailureException {
		CorbaErrorProcessor err = new CorbaErrorProcessor(pfe);

		if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
			LOG.error("Huawei U2000>> " + param + ", " + err.printError()
					+ " It is a major error. Stop interaction with server", pfe);

			throw pfe;
		} else {
			LOG.error("Huawei U2000>> " + param + err.printError()
					+ " It is a minor error. Continue interaction with server", pfe);
		}
	}
}
