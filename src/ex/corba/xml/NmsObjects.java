package ex.corba.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//Below annotation defines root element of XML file  
@XmlRootElement (name = "main")
public class NmsObjects {
	private List<ManagedElement> managedElements;

	public List<ManagedElement> getManagedElements() {
		return managedElements;
	}

	@XmlElement(name = "NETWORK_ELEMENTS")
	public void setManagedElements(List<ManagedElement> managedElements) {
		this.managedElements = managedElements;
	}
}
