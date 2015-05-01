package ex.corba.ciena;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.ciena.oc.common.Common_IHolder;
import com.ciena.oc.emsMgr.EMSMgr_I;
import com.ciena.oc.emsMgr.EMSMgr_IHelper;
import com.ciena.oc.emsSession.EmsSession_I;
import com.ciena.oc.equipment.EquipmentInventoryMgr_I;
import com.ciena.oc.equipment.EquipmentInventoryMgr_IHelper;
import com.ciena.oc.equipment.EquipmentOrHolderIterator_IHolder;
import com.ciena.oc.equipment.EquipmentOrHolderList_THolder;
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
import com.ciena.oc.subnetworkConnection.GradesOfImpact_T;
import com.ciena.oc.subnetworkConnection.SNCCreateData_T;
import com.ciena.oc.subnetworkConnection.SNCIterator_IHolder;
import com.ciena.oc.subnetworkConnection.SubnetworkConnectionList_THolder;
import com.ciena.oc.subnetworkConnection.SubnetworkConnection_THolder;
import com.ciena.oc.subnetworkConnection.TPDataList_THolder;
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

	// Cache list
	private List<String> neNames;
	private List<String> sncNames;

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
				eiManager.getAllEquipment(ne, HOW_MANY, equipOrHolderList,
						equipOrHolderItr);

				LOG.info("getAllEquipment: got "
						+ equipOrHolderList.value.length
						+ " equipments for ME " + ne[1].value);

				for (int i = 0; i < equipOrHolderList.value.length; i++) {
					helper.printEquipmentOrHolder(equipOrHolderList.value[i]);
				}

				exitWhile = false;

				if (equipOrHolderItr.value != null) {
					try {
						boolean hasMoreData = true;
						while (hasMoreData) {
							hasMoreData = equipOrHolderItr.value.next_n(
									HOW_MANY, equipOrHolderList);

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

				if (LOG.isInfoEnabled()) {
					LOG.info("getAllEquipment: finished getEquipment for ME "
							+ ne[1].value + " Order number # " + meCounter);
				}
			} catch (ProcessingFailureException ex) {
				handleProcessingFailureException(ex, "getAllEquipment. ME: "
						+ neName);
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
					helper.printTerminationPoint(terminationPointList.value[i]);
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

		TopologicalLinkList_THolder topologicalLinkList = new TopologicalLinkList_THolder();
		TopologicalLinkIterator_IHolder topologicalLinkIterator = new TopologicalLinkIterator_IHolder();

		try {
			mlsnManager.getAllTopologicalLinks(mlsn, HOW_MANY,
					topologicalLinkList, topologicalLinkIterator);

			for (int i = 0; i < topologicalLinkList.value.length; i++) {
				handler.printStructure(getTopologicalLinkParams(topologicalLinkList.value[i]));
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
					hasMoreData = sncIterator.value.next_n(HOW_MANY, sncList);
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
			LOG.info("deactivateAndDeleteSNC() complete.");
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

	public void handleProcessingFailureException(
			ProcessingFailureException pfe, String param)
			throws ProcessingFailureException {
		CorbaErrorProcessor err = new CorbaErrorProcessor(pfe);

		if (err.getPriority() == CorbaErrorDescriptions.PRIORITY.MAJOR) {
			LOG.error("Alcatel OMS 1350>> " + param + ", " + err.printError()
					+ " It is a major error. Stop interaction with server", pfe);

			throw pfe;
		} else {
			LOG.error("Alcatel OMS 1350>> " + param + err.printError()
					+ " It is a minor error. Continue interaction with server",
					pfe);
		}
	}
}
