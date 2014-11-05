package ex.corba.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

//Below annotation defines root element of XML file  
@XmlRootElement
// You can define order in which elements will be created in XML file
// Optional
@XmlType(propOrder = { "neID", "neName", "userLabel", "owner", "location",
		"productName", "communicationState", "emsSyncState", "supportedRate",
		"gateway" })
public class ManagedElement {
	private String neID;
	private String neName;
	private String userLabel;
	private String owner;
	private String location;
	private String productName;
	private String communicationState;
	private boolean emsSyncState;
	private short[] supportedRate;
	private String gateway;

	public String getNeID() {
		return neID;
	}

	@XmlElement(name = "NE_ID")
	public void setNeID(String neID) {
		this.neID = neID;
	}

	public String getNeName() {
		return neName;
	}

	@XmlElement(name = "NE_NAME")
	public void setNeName(String neName) {
		this.neName = neName;
	}

	public String getUserLabel() {
		return userLabel;
	}

	@XmlElement(name = "USER_LABEL")
	public void setUserLabel(String userLabel) {
		this.userLabel = userLabel;
	}

	public String getOwner() {
		return owner;
	}

	@XmlElement(name = "OWNER")
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLocation() {
		return location;
	}

	@XmlElement(name = "LOCATION")
	public void setLocation(String location) {
		this.location = location;
	}

	public String getProductName() {
		return productName;
	}

	@XmlElement(name = "PRODUCT_NAME")
	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCommunicationState() {
		return communicationState;
	}

	@XmlElement(name = "COMMUNICATION_STATE")
	public void setCommunicationState(String communicationState) {
		this.communicationState = communicationState;
	}

	public boolean getEmsSyncState() {
		return emsSyncState;
	}

	@XmlElement(name = "EMS_INSYNC_STATE")
	public void setEmsSyncState(boolean emsSyncState) {
		this.emsSyncState = emsSyncState;
	}

	public short[] getSupportedRate() {
		return supportedRate;
	}

	@XmlElement(name = "SUPPORTED_RATES")
	public void setSupportedRate(short[] supportedRate) {
		this.supportedRate = supportedRate;
	}

	public String getGateway() {
		return gateway;
	}

	@XmlElement(name = "GATEWAYS")
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
}
