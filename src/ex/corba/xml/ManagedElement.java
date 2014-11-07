package ex.corba.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "NETWORK_ELEMENTS")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "neID", "neName", "userLabel", "owner", "location",
		"productName", "communicationState", "emsSyncState", "supportedRate",
		"gateway" })
public class ManagedElement {
	@XmlElement(name = "NE_ID")
	private String neID;

	@XmlElement(name = "NE_NAME")
	private String neName;

	@XmlElement(name = "USER_LABEL")
	private String userLabel;

	@XmlElement(name = "OWNER")
	private String owner;

	@XmlElement(name = "LOCATION")
	private String location;

	@XmlElement(name = "PRODUCT_NAME")
	private String productName;

	@XmlElement(name = "COMMUNICATION_STATE")
	private String communicationState;

	@XmlElement(name = "EMS_INSYNC_STATE")
	private boolean emsSyncState;

	@XmlElement(name = "SUPPORTED_RATES")
	private short[] supportedRate;

	@XmlElement(name = "GATEWAYS")
	private String gateway;

	public String getNeID() {
		return neID;
	}

	public void setNeID(String neID) {
		this.neID = neID;
	}

	public String getNeName() {
		return neName;
	}

	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getUserLabel() {
		return userLabel;
	}

	public void setUserLabel(String userLabel) {
		this.userLabel = userLabel;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCommunicationState() {
		return communicationState;
	}

	public void setCommunicationState(String communicationState) {
		this.communicationState = communicationState;
	}

	public boolean getEmsSyncState() {
		return emsSyncState;
	}

	public void setEmsSyncState(boolean emsSyncState) {
		this.emsSyncState = emsSyncState;
	}

	public short[] getSupportedRate() {
		return supportedRate;
	}

	public void setSupportedRate(short[] supportedRate) {
		this.supportedRate = supportedRate;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
}
