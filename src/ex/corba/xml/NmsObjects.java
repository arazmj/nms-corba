package ex.corba.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "main")
public class NmsObjects {
	private List<ManagedElement> managedElements;
	private List<EquipmentHolder> equipmentHolders;

	public List<ManagedElement> getManagedElements() {
		return managedElements;
	}

	@XmlElement(name = "NETWORK_ELEMENTS")
	public void setManagedElements(List<ManagedElement> managedElements) {
		this.managedElements = managedElements;
	}

	public List<EquipmentHolder> getEquipmentHolders() {
		return equipmentHolders;
	}

	@XmlElement(name = "HOLDERS")
	public void setEquipmentHolders(List<EquipmentHolder> equipmentHolders) {
		this.equipmentHolders = equipmentHolders;
	}

}
