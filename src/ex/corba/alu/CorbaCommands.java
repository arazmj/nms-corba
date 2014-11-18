package ex.corba.alu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import managedElement.ManagedElementIterator_IHolder;
import managedElement.ManagedElementList_THolder;
import managedElement.ManagedElement_T;
import managedElementManager.ManagedElementMgr_I;
import managedElementManager.ManagedElementMgr_IHelper;
import multiLayerSubnetwork.EMSFreedomLevel_T;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_I;
import multiLayerSubnetwork.MultiLayerSubnetworkMgr_IHelper;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import subnetworkConnection.CrossConnect_T;
import subnetworkConnection.GradesOfImpact_T;
import subnetworkConnection.ProtectionEffort_T;
import subnetworkConnection.Route_THolder;
import subnetworkConnection.SNCCreateData_T;
import subnetworkConnection.SNCIterator_IHolder;
import subnetworkConnection.SNCModifyData_T;
import subnetworkConnection.SubnetworkConnectionList_THolder;
import subnetworkConnection.SubnetworkConnection_THolder;
import subnetworkConnection.TPDataList_THolder;
import terminationPoint.TerminationPointIterator_IHolder;
import terminationPoint.TerminationPointList_THolder;
import terminationPoint.TerminationPoint_T;
import terminationPoint.TerminationPoint_THolder;
import topologicalLink.TopologicalLinkIterator_IHolder;
import topologicalLink.TopologicalLinkList_THolder;
import topologicalLink.TopologicalLink_T;

import common.Common_IHolder;

import emsMgr.EMSMgr_I;
import emsMgr.EMSMgr_IHelper;
import emsSession.EmsSession_I;
import equipment.EquipmentInventoryMgr_I;
import equipment.EquipmentInventoryMgr_IHelper;
import equipment.EquipmentOrHolderIterator_IHolder;
import equipment.EquipmentOrHolderList_THolder;
import ex.corba.CorbaConstants;
import ex.corba.alu.error.CorbaErrorDescriptions;
import ex.corba.alu.error.CorbaErrorProcessor;
import ex.corba.alu.transform.jaxb.Corba2Object;
import ex.corba.alu.transform.sax.Corba2XMLContainer;
import ex.corba.alu.transform.sax.Corba2XMLHandler;
import ex.corba.alu.transform.sax.Corba2XMLHelper;
import ex.corba.alu.transform.sax.Corba2XMLStructure;
import ex.corba.xml.EquipmentHolder;
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
	private static final String EMS_MANAGER_NAME = "EMS";

	public static final int HOW_MANY = 100;

	public static final Logger LOG = LoggerFactory
			.getLogger(CorbaCommands.class);

	private EmsSession_I emsSession;
	private String emsName;
	private Corba2XMLHandler handler;
	private Corba2XMLHelper helper;
	// private JaxbOutputHandler jaxbOutputHandler;

	private Common_IHolder managerInterface;
	private ManagedElementMgr_I meManager;
	private EquipmentInventoryMgr_I eiManager;
	private MultiLayerSubnetworkMgr_I mlsnManager;
	private EMSMgr_I emsManager;

	// Cache list
	private List<String> neNames;
	private List<String> sncNames;
	private List<String> subnetworkNames;

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
		// this.jaxbOutputHandler = new JaxbOutputHandler(contentHandler);
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

		List<ManagedElement> managedElements = new ArrayList<ManagedElement>();

		for (ManagedElement_T me : mes) {
			handler.printStructure(helper.getManagedElementParams(me));
			managedElements.add(Corba2Object.getManagedElement(me));
			neNames.add(handler.getValueByName(me.name, "ManagedElement"));
			// jaxbOutputHandler.printManagedElementContentHandler(Corba2Object
			// .getManagedElement(me));
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

	public void getAllEquipment() throws ProcessingFailureException,
			SAXException {
		LOG.info("getAllEquipment() start.");

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

		// List<EquipmentHolder> equipmentHolders = new
		// ArrayList<EquipmentHolder>();

		for (String neName : neNames) {
			try {
				ne[1].value = neName;
				eiManager.getAllEquipment(ne, HOW_MANY, equipOrHolderList,
						equipOrHolderItr);

				System.out.println("getAllEquipment: got "
						+ equipOrHolderList.value.length
						+ " equipments for ME " + ne[1].value);

				for (int i = 0; i < equipOrHolderList.value.length; i++) {
					helper.listEquipmentOrHolderList(equipOrHolderList.value[i]);

					// if (equipOrHolderList.value[i].discriminator().value() ==
					// 1)
					// {
					// equipmentHolders.add(Corba2Object
					// .getEquipmentHolder(equipOrHolderList.value[i]
					// .holder()));
					// }
				}

				exitWhile = false;

				if (equipOrHolderItr.value != null)
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = equipOrHolderItr.value.next_n(
									HOW_MANY, equipOrHolderList);

							for (int i = 0; i < equipOrHolderList.value.length; i++) {
								helper.listEquipmentOrHolderList(equipOrHolderList.value[i]);
							}
						}

						exitWhile = true;
					} finally {
						if (!exitWhile)
							equipOrHolderItr.value.destroy();
					}

				meCounter++;

				System.out
						.println("getAllEquipment: finished getEquipment for ME "
								+ ne[1].value + " Order number # " + meCounter);
			} catch (ProcessingFailureException e) {
				CorbaErrorProcessor err = new CorbaErrorProcessor(e);
				if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
					LOG.error(
							"Alcatel OMS 1350>> getAllEquipment. ME: "
									+ neName
									+ ", "
									+ err.printError()
									+ " It is a major error. Stop interaction with server",
							e);

					throw e;
				} else {
					LOG.warn(
							"Alcatel OMS 1350>> getAllEquipment. "
									+ err.printError()
									+ " It is a minor error. Continue interaction with server",
							e);
				}
			}
		}

		// printEquipmentHolderList(equipmentHolders);
	}

	public void printEquipmentHolderList(List<EquipmentHolder> equipmentHolder)
			throws Exception {
		JaxbOutputHandler out = new JaxbOutputHandler("holders.xml");
		NmsObjects nmsObjects = new NmsObjects();
		nmsObjects.setEquipmentHolders(equipmentHolder);
		out.print(nmsObjects);
	}

	public void getAllPTPs() throws ProcessingFailureException, SAXException {
		try {
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
						helper.listTerminationPointList(terminationPointList.value[i]);
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
											"getAllPTPs: got {} PTP for ME {}.",
											terminationPointList.value.length,
											neNameArray[1].value);
								}

								for (int i = 0; i < terminationPointList.value.length; i++) {
									helper.listTerminationPointList(terminationPointList.value[i]);
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
				} catch (ProcessingFailureException e) {
					CorbaErrorProcessor err = new CorbaErrorProcessor(e);
					if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
						LOG.error(
								"Alcatel OMS 1350>> getAllPTPs. ME: "
										+ neName
										+ ", "
										+ err.printError()
										+ " It is a major error. Stop interaction with server",
								e);

						throw e;
					} else {
						LOG.error(
								"Alcatel OMS 1350>> getAllPTPs. "
										+ err.printError()
										+ " It is a minor error. Continue interaction with server",
								e);
					}
				}
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("getAllPTPs() complete.");
			}
		} catch (ProcessingFailureException prf) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Alcatel OMS 1350>> getAllPTPs:"
						+ CorbaErrorProcessor.printError(prf));
			}

			throw prf;
		}
	}

	public void getAllTopologicalLinks() throws ProcessingFailureException,
			SAXException {
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("getAllTopologicalLinks() start.");
			}

			if (subnetworkNames == null) {
				subnetworkNames = getAllTopLevelSubnetworkNames();
			}

			if (!setManagerByName(MLS_MANAGER_NAME))
				return;

			NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[2];

			nameAndStringValueArray[0] = new NameAndStringValue_T(
					CorbaConstants.EMS_STR, emsName);
			nameAndStringValueArray[1] = new NameAndStringValue_T(
					CorbaConstants.MULTILAYER_SUBNETWORK_STR, "1");

			TopologicalLinkList_THolder topologicalLinkList = new TopologicalLinkList_THolder();
			TopologicalLinkIterator_IHolder topologicalLinkIterator = new TopologicalLinkIterator_IHolder();

			for (String subnetwork : subnetworkNames) {
				try {
					nameAndStringValueArray[1].value = subnetwork;
					mlsnManager.getAllTopologicalLinks(nameAndStringValueArray,
							HOW_MANY, topologicalLinkList,
							topologicalLinkIterator);

					for (int i = 0; i < topologicalLinkList.value.length; i++) {
						handler.printStructure(getTopologicalLinkParams(topologicalLinkList.value[i]));
					}

					boolean exitWhile = false;
					if (topologicalLinkIterator.value != null) {
						try {
							boolean hasMoreData = true;
							while (hasMoreData) {
								hasMoreData = topologicalLinkIterator.value
										.next_n(HOW_MANY, topologicalLinkList);
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
					CorbaErrorProcessor err = new CorbaErrorProcessor(ex);
					if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
						LOG.error(
								"Alcatel OMS 1350>> getAllTopologicalLinks. MLS: "
										+ nameAndStringValueArray[1].value
										+ ", "
										+ err.printError()
										+ " It is a major error. Stop interaction with server",
								ex);

						throw ex;
					} else {
						LOG.error(
								"Alcatel OMS 1350>> getAllTopologicalLinks. MLS: "
										+ nameAndStringValueArray[1].value
										+ ", "
										+ err.printError()
										+ " It is a minor error. Continue interaction with server",
								ex);
					}
				}
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("getAllTopologicalLinks() complete.");
			}
		} catch (ProcessingFailureException prf) {
			LOG.error("Alcatel OMS 1350>> getAllTopologicalLinks:"
					+ CorbaErrorProcessor.printError(prf));

			throw prf;
		}
	}

	private Corba2XMLContainer getTopologicalLinkParams(
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
							CorbaConstants.A_TRANSMISSIONPARAMS_STR,
							handler.convertLayeredParametersToString(aEndTP.transmissionParams));
		} else {
			container
					.setFieldValue(CorbaConstants.A_TRANSMISSIONPARAMS_STR, "");
		}

		TerminationPoint_T zEndTP = getTerminationPoint(topologicalLink.zEndTP,
				topologicalLink.rate);
		if (zEndTP != null) {
			container
					.setFieldValue(
							CorbaConstants.Z_TRANSMISSIONPARAMS_STR,
							handler.convertLayeredParametersToString(zEndTP.transmissionParams));
		} else {
			container
					.setFieldValue(CorbaConstants.Z_TRANSMISSIONPARAMS_STR, "");
		}

		return container;
	}

	private TerminationPoint_T getTerminationPoint(
			NameAndStringValue_T[] endPointName, short rate)
			throws ProcessingFailureException {
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("getTerminationPoint() start for {}.",
						handler.convertNameAndStringValueToString(endPointName));
			}

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
				CorbaErrorProcessor err = new CorbaErrorProcessor(ex);
				if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
					LOG.error(
							"Alcatel OMS 1350>> getTerminationPoint. TP: "
									+ handler
											.convertNameAndStringValueToString(endPointName)
									+ ", "
									+ err.printError()
									+ " It is a major error. Stop interaction with server",
							ex);

					throw ex;
				} else {
					LOG.error(
							"Alcatel OMS 1350>> getTerminationPoint. TP: "
									+ handler
											.convertNameAndStringValueToString(endPointName)
									+ ", "
									+ err.printError()
									+ " It is a minor error. Continue interaction with server",
							ex);

				}
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("getTerminationPoint() complete");
			}

			return tpHolder.value;
		} catch (ProcessingFailureException prf) {
			LOG.error("Alcatel OMS 1350>> getTerminationPoint:"
					+ CorbaErrorProcessor.printError(prf));
			throw prf;
		}
	}

	public List<String> getAllTopLevelSubnetworkNames()
			throws ProcessingFailureException {
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("getAllTopLevelSubnetworkNames() start.");
			}

			if (!setManagerByName(EMS_MANAGER_NAME)) {
				return null;
			}

			NamingAttributesList_THolder namingAttributesList = new NamingAttributesList_THolder();
			NamingAttributesIterator_IHolder namingAttributesIterator = new NamingAttributesIterator_IHolder();

			emsManager.getAllTopLevelSubnetworkNames(HOW_MANY,
					namingAttributesList, namingAttributesIterator);

			List<String> arrayList = new ArrayList<String>();

			for (int i = 0; i < namingAttributesList.value.length; i++) {
				for (int j = 0; j < namingAttributesList.value[i].length; j++) {
					if (namingAttributesList.value[i][j].name
							.equals(CorbaConstants.MULTILAYER_SUBNETWORK_STR)) {
						arrayList.add(namingAttributesList.value[i][j].value);
					}
				}
			}

			boolean exitwhile = false;
			if (namingAttributesIterator.value != null) {
				try {
					boolean hasMoreData = true;
					while (hasMoreData) {
						hasMoreData = namingAttributesIterator.value.next_n(
								HOW_MANY, namingAttributesList);
						for (int i = 0; i < namingAttributesList.value.length; i++) {
							for (int j = 0; j < namingAttributesList.value[i].length; j++) {
								if (namingAttributesList.value[i][j].name
										.equals(CorbaConstants.MULTILAYER_SUBNETWORK_STR)) {
									arrayList
											.add(namingAttributesList.value[i][j].value);
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
				LOG.info(
						"getAllTopLevelSubnetworkNames() got {} top level subnetworks.",
						arrayList.size());
				LOG.info("getAllTopLevelSubnetworkNames() complete.");
			}

			return arrayList;
		} catch (ProcessingFailureException prf) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Alcatel OMS 1350>> getAllTopLevelSubnetworkNames:"
						+ CorbaErrorProcessor.printError(prf));
			}

			throw prf;
		}
	}

	public List<String> getAllSubnetworkConnections()
			throws ProcessingFailureException, SAXException {
		return getAllSubnetworkConnections("SDH");
	}

	public List<String> getAllSubnetworkConnections(final String mlsn)
			throws ProcessingFailureException, SAXException {
		try {
			if (LOG.isInfoEnabled()) {
				LOG.info("getAllSubnetworkConnections() start.");
			}

			if (!setManagerByName(MLS_MANAGER_NAME))
				return null;

			NameAndStringValue_T[] nameAndStringValueArray = new NameAndStringValue_T[2];

			nameAndStringValueArray[0] = new NameAndStringValue_T(
					CorbaConstants.EMS_STR, emsName);
			nameAndStringValueArray[1] = new NameAndStringValue_T(
					CorbaConstants.MULTILAYER_SUBNETWORK_STR, mlsn);

			SubnetworkConnectionList_THolder sncList = new SubnetworkConnectionList_THolder();
			SNCIterator_IHolder sncIterator = new SNCIterator_IHolder();
			short[] rateList = new short[0];

			mlsnManager.getAllSubnetworkConnections(nameAndStringValueArray,
					rateList, HOW_MANY, sncList, sncIterator);
			sncNames = new ArrayList<String>();

			for (int i = 0; i < sncList.value.length; i++) {
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
						hasMoreData = sncIterator.value.next_n(HOW_MANY,
								sncList);
						for (int i = 0; i < sncList.value.length; i++) {
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
				LOG.info("getAllSubnetworkConnections() complete.");
			}

			return sncNames;
		} catch (ProcessingFailureException prf) {
			LOG.error("Alcatel OMS 1350>> getAllSubnetworkConnections:"
					+ CorbaErrorProcessor.printError(prf));
			throw prf;
		}
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

		sncNameArray[0] = new NameAndStringValue_T(CorbaConstants.EMS_STR,
				emsName);
		sncNameArray[1] = new NameAndStringValue_T(
				CorbaConstants.MULTILAYER_SUBNETWORK_STR, "SDH");
		sncNameArray[2] = new NameAndStringValue_T();
		sncNameArray[2].name = CorbaConstants.SUBNETWORK_CONNECTION_STR;

		Route_THolder routeHolder = new Route_THolder();
		Iterator<String> iter = sncNames.iterator();

		while (iter.hasNext()) {
			sncNameArray[2].value = iter.next();

			try {
				mlsnManager.getRoute(sncNameArray, true, routeHolder);

				for (CrossConnect_T crossConnect : routeHolder.value) {
					handler.printStructure(helper.getRouteParams(crossConnect,
							sncNameArray[2].value));
				}
			} catch (ProcessingFailureException ex) {
				CorbaErrorProcessor err = new CorbaErrorProcessor(ex);
				if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
					LOG.error(
							"Alcatel OMS 1350>> getRoute: SNC = "
									+ sncNameArray[2].value
									+ ";  "
									+ err.printError()
									+ " It is a major error. Stop interaction with server",
							ex);
					throw ex;
				} else {
					LOG.warn(
							"Alcatel OMS 1350>> getRoute: SNC = "
									+ sncNameArray[2].value
									+ ";  "
									+ err.printError()
									+ " It is a minor error. Continue interaction with server",
							ex);
				}
			}
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("getRoute() complete.");
		}
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

	public StringHolder modifySNC(NameAndStringValue_T[] sncName,
			SNCModifyData_T modifyData, GradesOfImpact_T tolerableImpact,
			ProtectionEffort_T tolerableImpactEffort,
			EMSFreedomLevel_T emsFreedomLevel, TPDataList_THolder tpsToModify)
			throws ProcessingFailureException {

		if (LOG.isInfoEnabled()) {
			LOG.info("modifySNC() start.");
		}

		if (!setManagerByName(MLS_MANAGER_NAME))
			return null;

		SubnetworkConnection_THolder theSNC = new SubnetworkConnection_THolder();
		StringHolder errorReason = new StringHolder();

		this.mlsnManager.modifySNC(sncName, "", modifyData, tolerableImpact,
				tolerableImpactEffort, emsFreedomLevel, tpsToModify, theSNC,
				errorReason);

		if (LOG.isInfoEnabled()) {
			LOG.info("modifySNC() complete.");
		}

		return errorReason;
	}
}
