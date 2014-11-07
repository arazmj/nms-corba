package ex.corba.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "neID", "holderName", "userLabel", "nativeEMSName",
		"owner", "alarmReportingIndicator", "holderType",
		"expectedOrInstalledEquipment", "acceptableEquipmentTypeList",
		"holderState", "additionalInfo" })
public class EquipmentHolder {
	private String neID;
	private String holderName;
	private String userLabel;
	private String nativeEMSName;
	private String owner;
	private boolean alarmReportingIndicator;
	private String holderType;
	private String expectedOrInstalledEquipment;
	private String[] acceptableEquipmentTypeList;
	private String holderState;
	private String additionalInfo;

	public String getNeID() {
		return neID;
	}

	@XmlElement(name = "NE_ID")
	public void setNeID(String neID) {
		this.neID = neID;
	}

	public String getHolderName() {
		return holderName;
	}

	@XmlElement(name = "HOLDER")
	public void setHolderName(String holderName) {
		this.holderName = holderName;
	}

	public String getUserLabel() {
		return userLabel;
	}

	@XmlElement(name = "USER_LABEL")
	public void setUserLabel(String userLabel) {
		this.userLabel = userLabel;
	}

	public String getNativeEMSName() {
		return nativeEMSName;
	}

	@XmlElement(name = "USER_LABEL")
	public void setNativeEMSName(String nativeEMSName) {
		this.nativeEMSName = nativeEMSName;
	}

	public String getOwner() {
		return owner;
	}

	@XmlElement(name = "OWNER")
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isAlarmReportingIndicator() {
		return alarmReportingIndicator;
	}

	@XmlElement(name = "ALARM_REPORT_INDIC")
	public void setAlarmReportingIndicator(boolean alarmReportingIndicator) {
		this.alarmReportingIndicator = alarmReportingIndicator;
	}

	public String getHolderType() {
		return holderType;
	}

	@XmlElement(name = "HOLDER_TYPE")
	public void setHolderType(String holderType) {
		this.holderType = holderType;
	}

	public String getExpectedOrInstalledEquipment() {
		return expectedOrInstalledEquipment;
	}

	@XmlElement(name = "EXP_INST_EQUIPMENT")
	public void setExpectedOrInstalledEquipment(
			String expectedOrInstalledEquipment) {
		this.expectedOrInstalledEquipment = expectedOrInstalledEquipment;
	}

	public String[] getAcceptableEquipmentTypeList() {
		return acceptableEquipmentTypeList;
	}

	@XmlElement(name = "ACCEPT_EQUIPMENT")
	public void setAcceptableEquipmentTypeList(
			String[] acceptableEquipmentTypeList) {
		this.acceptableEquipmentTypeList = acceptableEquipmentTypeList;
	}

	public String getHolderState() {
		return holderState;
	}

	@XmlElement(name = "STATE")
	public void setHolderState(String holderState) {
		this.holderState = holderState;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	@XmlElement(name = "ADDITIONAL_INFO")
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

}
