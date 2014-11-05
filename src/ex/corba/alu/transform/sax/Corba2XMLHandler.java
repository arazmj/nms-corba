package ex.corba.alu.transform.sax;

import globaldefs.NameAndStringValue_T;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import transmissionParameters.LayeredParameters_T;

/**
 * User: ROAG1205 Date: 16.05.2011 Time: 22:55:59
 */
public class Corba2XMLHandler {
	public static final String XML_ROOT_TAG = "main";
	public static final String XML_DEFAULT_URI = "";

	private ContentHandler handler;

	public Corba2XMLHandler(ContentHandler handler) {
		this.handler = handler;
	}

	// Start DefaultHandler
	public void handlerBuilderStart() throws SAXException {
		handler.startDocument();
		Attributes emptyAttr = new AttributesImpl();
		handler.startElement(XML_DEFAULT_URI, XML_ROOT_TAG, XML_ROOT_TAG,
				emptyAttr);
	}

	// Stop DefaultHandler
	public void handlerBuilderEnd() throws SAXException {
		handler.endElement(XML_DEFAULT_URI, XML_ROOT_TAG, XML_ROOT_TAG);
		handler.endDocument();
	}

	public void printStructure(Corba2XMLContainer container)
			throws SAXException {
		printStructure(container, true);
	}

	/**
	 * Print container as XML
	 * 
	 * @param container
	 *            - data
	 * @param closeStructure
	 *            - if true then close tag automatically will be added, if false
	 *            then structure will be unclosed
	 * @throws SAXException
	 */
	public void printStructure(Corba2XMLContainer container,
			boolean closeStructure) throws SAXException {
		startElement(container.getStructureName());
		
		for (String field : container.getFields()) {
			printElement(field, container.getValue(field));
		}

		if (closeStructure) {
			endElement(container.getStructureName());
		}
	}

	public void printElement(String elementName, String value)
			throws SAXException {
		startElement(elementName);
		handler.characters(value.toCharArray(), 0, value.length());
		endElement(elementName);
	}

	public void startElement(String elementName) throws SAXException {
		Attributes emptyAttr = new AttributesImpl();

		handler.startElement(XML_DEFAULT_URI, elementName, elementName,
				emptyAttr);
	}

	public void endElement(String elementName) throws SAXException {
		handler.endElement(XML_DEFAULT_URI, elementName, elementName);
	}

	/**
	 * Parses NameAndStringValue_T structure into String
	 * 
	 * @param nameAndValue
	 *            - array of NameAndStringValue_T
	 * @return String
	 */
	public String convertNameAndStringValueToString(
			NameAndStringValue_T[] nameAndValue) {
		if (nameAndValue == null) {
			return "";
		}

		StringBuffer nameAndValuesList = new StringBuffer();

		for (NameAndStringValue_T nv : nameAndValue) {
			nameAndValuesList.append(nv.name).append('=').append(nv.value)
					.append(';');
		}

		return nameAndValuesList.toString();
	}

	public String parseStringArray(String[] array) {
		if (array == null) {
			return "";
		}

		StringBuffer valuesList = new StringBuffer();

		for (String value : array) {
			valuesList.append(value).append(';');
		}

		return valuesList.toString();
	}

	/**
	 * Method print list from type NameAndStringValue_T []
	 * 
	 * @param nasv
	 *            - array of {@link NameAndStringValue_T}
	 * @param name
	 *            - parameter name
	 * @return String - parameter value
	 */
	public String getValueByName(NameAndStringValue_T[] nasv, String name) {
		for (int i = 0; i < nasv.length; i++) {
			if (nasv[i].name.equals(name)) {
				return nasv[i].value;
			}
		}
		return "";
	}

	/**
	 * Method print XML from type NameAndStringValue_T []
	 * 
	 * @param nasv
	 *            - array of {@link NameAndStringValue_T}
	 * @param name
	 *            - parameter name
	 * @param field
	 *            - name of field in result xml
	 * @throws SAXException
	 */
	public void printNameAndStringValueName(NameAndStringValue_T[] nasv,
			String name, String field) throws SAXException {
		for (int i = 0; i < nasv.length; i++) {
			if (name.equals(nasv[i].name)) {
				printElement(field, nasv[i].value);
			}
		}
	}

	/**
	 * Prints array of short to String
	 * 
	 * @param shorts
	 *            - array of short
	 * @param delimiter
	 *            - delimeter that should separate shorts in string
	 * @return String
	 */
	public String convertShortArrayToString(short[] shorts, String delimiter) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < shorts.length; i++) {
			stringBuffer.append(String.valueOf(shorts[i]));
			if (i != shorts.length - 1) {
				stringBuffer.append(delimiter);
			}
		}
		return stringBuffer.toString();
	}

	public String convertIntArrayToString(int[] ints, String delimiter) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < ints.length; i++) {
			stringBuffer.append(ints[i]);
			if (i != ints.length - 1) {
				stringBuffer.append(delimiter);
			}
		}
		return stringBuffer.toString();
	}

	public String convertLayeredParameterToString(
			LayeredParameters_T transmissionParam) {

		if (transmissionParam == null) {
			return "";
		}
		StringBuffer stBuf = new StringBuffer();
		stBuf.append("{transmissionParams=")
				.append(convertNameAndStringValueToString(transmissionParam.transmissionParams))
				.append('}');
		stBuf.append("{layer=").append(transmissionParam.layer).append('}');
		return stBuf.toString();
	}

	public String convertLayeredParametersToString(
			LayeredParameters_T[] layeredParamsArray) {

		if (layeredParamsArray == null) {
			return "";
		}

		if (layeredParamsArray.length == 0) {
			return "";
		}

		StringBuffer stBuf = new StringBuffer();
		for (LayeredParameters_T lp : layeredParamsArray) {
			stBuf.append("[ ");
			stBuf.append(convertLayeredParameterToString(lp));
			stBuf.append(" ]");
		}

		return stBuf.toString();
	}

	/**
	 * Parses NameAndStringValue_T structure into String
	 * 
	 * @param nameAndValuesArray
	 *            - array of NameAndStringValue_T
	 * @return String
	 */
	public String convertNameAndStringValuesToString(
			NameAndStringValue_T[][] nameAndValuesArray) {
		if (nameAndValuesArray == null) {
			return "";
		}

		StringBuffer nameAndValuesList = new StringBuffer();

		for (NameAndStringValue_T[] nv : nameAndValuesArray) {
			nameAndValuesList.append('[')
					.append(convertNameAndStringValueToString(nv)).append(']');
		}

		return nameAndValuesList.toString();
	}
}
