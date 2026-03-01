package server.model.components;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XMLHandler<T> {
    private final String filePath;
    private final Document doc;
    private final String tagName;
    private final Class<T> clazz;

    /**
     * The constructor for our XMLHandler.
     * @param clazz The class type
     * @param filePath The XML path
     */
    public XMLHandler(Class<T> clazz, String filePath) throws ParserConfigurationException, IOException, SAXException {
        this.filePath = filePath;
        this.clazz = clazz;
        this.tagName = clazz.getSimpleName().toLowerCase();

        File inputFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        this.doc = docBuilder.parse(inputFile);

        // Ensure the root exists
        if (this.doc.getDocumentElement() != null) {
            this.doc.getDocumentElement().normalize();
        }
    }

    /**
     * Constructor with a tagName to avoid redundancy on providing it on our parameters
     * @param filePath The XML path
     * @param tagName The target tagName
     */
    public XMLHandler(Class<T> clazz, String filePath, String tagName) throws ParserConfigurationException, IOException, SAXException  {
        this.filePath = filePath;
        this.tagName = tagName;
        this.clazz = clazz;
        File inputFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        dbFactory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        this.doc = docBuilder.parse(inputFile);
        this.doc.getDocumentElement().normalize();
    }

    // Global methods

    /**
     * To get the current size / length of our XML(Data)
     * @return int of length
     */
    public int getCurrentLength() {
        NodeList allElements = doc.getElementsByTagName("*");
        return allElements.getLength();
    }

    /**
     * To get the current size of a certain tagName on our XML(Data)
     * @param tagName The tagname of we are looking for
     * @return int of lenght
     */
    public int getCurrentLength(String tagName) {
        NodeList allElements = doc.getElementsByTagName(tagName);
        return allElements.getLength();
    }

    /**
     * The function to save it to our XML
     */
    public void saveToXML() {
        try {
            cleanEmptyTextNodes(doc);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(this.filePath));

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To clean the spaces on our XML
     * @param node The DOC and entertained as Node
     */
    private void cleanEmptyTextNodes(Node node) {
        NodeList childNodes = node.getChildNodes();
        for (int n = childNodes.getLength() - 1; n >= 0; n--) {
            Node child = childNodes.item(n);
            short nodeType = child.getNodeType();

            if (nodeType == Node.ELEMENT_NODE) {
                cleanEmptyTextNodes(child);
            } else if (nodeType == Node.TEXT_NODE) {
                String trimmedContent = child.getTextContent().trim();
                if (trimmedContent.isEmpty()) {
                    node.removeChild(child);
                }
            }
        }
    }

    // CRUD

    private static boolean looksLikeJavaArrayToString(String val) {
        if (val == null) return false;
        // Examples: "[I@58243dbb", "[Ljava.lang.String;@5f7652d7"
        return val.startsWith("[") && val.contains("@");
    }

    private static String[] parseStringArray(String val) {
        if (val == null) return new String[0];
        String trimmed = val.trim();
        if (trimmed.isEmpty()) return new String[0];
        if (looksLikeJavaArrayToString(trimmed)) return new String[0];

        String[] parts = trimmed.contains(",") ? trimmed.split(",") : new String[]{trimmed};
        int count = 0;
        for (String p : parts) {
            if (p != null && !p.trim().isEmpty()) count++;
        }
        String[] result = new String[count];
        int idx = 0;
        for (String p : parts) {
            if (p == null) continue;
            String t = p.trim();
            if (t.isEmpty()) continue;
            result[idx++] = t;
        }
        return result;
    }

    private static int[] parseIntArray(String val) {
        if (val == null) return new int[0];
        String trimmed = val.trim();
        if (trimmed.isEmpty()) return new int[0];
        if (looksLikeJavaArrayToString(trimmed)) return new int[0];

        String[] parts = trimmed.contains(",") ? trimmed.split(",") : new String[]{trimmed};
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                result[i] = Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException e) {
                result[i] = 0;
            }
        }
        return result;
    }

    private String valueToString(Object value) {
        if (value == null) return "";
        
        if (value instanceof String[]) {
            String[] arr = (String[]) value;
            return String.join(",", arr);
        }
        if (value instanceof int[]) {
            int[] arr = (int[]) value;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(arr[i]);
            }
            return sb.toString();
        }
        
        if (value instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            return format.format((Date) value);
        }
        
        return value.toString();
    }

    /** Create
     * To store a data on our XML.
     * @param tagName The tagName(Root) of our storing object
     * @param o The object to store
     */
    public void Store(String tagName, Object o) {
        try {
            Element rootElement = doc.createElement(tagName);
            doc.getDocumentElement().appendChild(rootElement);

            Class<?> clazz = o.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(o);
                String valueStr = valueToString(value);

                if (fieldName.startsWith("attr_")) {
                    if (!valueStr.isEmpty()) {
                        rootElement.setAttribute(fieldName.substring(5), valueStr);
                    }
                }
                else {
                    if (!valueStr.isEmpty()) {
                        Element childElement = doc.createElement(fieldName);
                        childElement.appendChild(doc.createTextNode(valueStr));
                        rootElement.appendChild(childElement);
                    }
                }
            }
            saveToXML();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public void Store(Object o) {
        Store(tagName, o);
    }

    /** Read
     * Generic read method to search by field name and value
     * @param fieldName The field name to search by ("name", "id", "username")
     * @param value The value to match (can be String, int, or any Object)
     * @return Object or null
     */
    public T Read(String fieldName, Object value) {
        if (value == null) return null;

        NodeList list = doc.getElementsByTagName(tagName);
        String searchValue = value.toString();

        for (int i = 0; i < list.getLength(); i++) {
            Element el = (Element) list.item(i);

            String attrValue = el.getAttribute(fieldName);
            if (!attrValue.isEmpty() && attrValue.equals(searchValue)) {
                return map(el);
            }

            String elementValue = getElementText(el, fieldName);
            if (!elementValue.isEmpty() && elementValue.equalsIgnoreCase(searchValue)) {
                return map(el);
            }
        }
        return null;
    }

    /** Read All
     * Retrieve all entries from the XML
     * @return Array of all objects
     */
    public T[] ReadAll(String tagName) {
        NodeList list = doc.getElementsByTagName(tagName);
        @SuppressWarnings("unchecked")
        T[] result = (T[]) java.lang.reflect.Array.newInstance(clazz, list.getLength());

        for (int i = 0; i < list.getLength(); i++) {
            Element el = (Element) list.item(i);
            result[i] = map(el);
        }
        return result;
    }
    public T[] ReadAll(){
        return ReadAll(tagName);
    }
    /** Read
     * To map
     * Diba sa users merong id, name, address, contact
     * eto scrap nya yung element na binigay sa parameter tapos
     * imamap nya o ilalagay nya sa tamang data example attr_id=11, attr_type=0. Gets?
     * @param el The element to map (e.g Boi <user><name>Josh</name><address>SLU</address></user>)
     * @return kase eto yung nahanap nung return dun sa baba Users{attr_id=11, attr_type=0, name='Gereme', address='Mary Heights', contact=[]}
     */
    private T map(Element el) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            Object[] values = new Object[fields.length];
            Class<?>[] types = new Class<?>[fields.length];

            for (int i = 0; i < fields.length; i++) {
                types[i] = fields[i].getType();
                String name = fields[i].getName();
                String raw = name.startsWith("attr_") ? el.getAttribute(name.substring(5)) : getElementText(el, name);
                Object converted = convert(raw, types[i]);
                values[i] = converted;
            }

            if (clazz.isRecord()) return clazz.getConstructor(types).newInstance(values);

            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> ctor : constructors) {
                if (ctor.getParameterCount() == fields.length) {
                    ctor.setAccessible(true);
                    return clazz.cast(ctor.newInstance(values));
                }
            }
            throw new NoSuchMethodException("No constructor found with " + fields.length + " parameters.");

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    /** Read
     * To get the child of a Tag, example diba <TAG>JOSH</TAG> edi JOSH
     * @param element The whole Element (e.g Boi <user><name>Josh</name><address>SLU</address></user>)
     * @param tag Yung tag na hinahanap naten
     * @return String
     */
    private String getElementText(Element element, String tag) {
        NodeList node = element.getElementsByTagName(tag);
        return (node.getLength() > 0) ? node.item(0).getTextContent() : "";
    }

    /** Read
     * To convert Strings scraped to a valid data type
     * @param val Value nung string na nakuha sa map
     * @param type Kung anong data type.
     * @return Object kase nga tinatama ngani naten yung datatype
     */
    private Object convert(String val, Class<?> type) {
        if (val == null || val.isEmpty()) {
            if (type == String[].class) return new String[0];
            if (type == int[].class) return new int[0];
            if (type == int.class || type == Integer.class) return 0;
            if (type == Date.class) return new Date();
            return val;
        }

        // Kung gusto nyo magdagdag pa ng datatype pwede naman
        if (type == String[].class) return parseStringArray(val);
        if (type == int[].class) return parseIntArray(val);
        if (type == int.class || type == Integer.class) return Integer.parseInt(val);
        if (type == Date.class) {
            try {
                SimpleDateFormat[] formats = {
                    new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                    new SimpleDateFormat("yyyy-MM-dd"),
                    new SimpleDateFormat("MMM dd, yyyy")
                };
                for (SimpleDateFormat format : formats) {
                    try {
                        return format.parse(val);
                    } catch (Exception ignored) {
                    }
                }
                return new Date();
            } catch (Exception e) {
                return new Date();
            }
        }

        return val;
    }

    /** Update
     * Generic update method to update by any field name
     * @param searchField The field name to search by (e.g., "id", "username", "name")
     * @param searchValue The value to match
     * @param obj The updated object
     * @return boolean
     */
    public boolean Update(String searchField, Object searchValue, T obj) {
        if (searchValue == null) return false;

        try {
            NodeList list = doc.getElementsByTagName(tagName);
            String searchStr = searchValue.toString();

            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                boolean match = false;

                String attrValue = element.getAttribute(searchField);
                if (!attrValue.isEmpty() && attrValue.equals(searchStr)) {
                    match = true;
                }

                if (!match) {
                    String elementValue = getElementText(element, searchField);
                    if (!elementValue.isEmpty() && elementValue.equalsIgnoreCase(searchStr)) {
                        match = true;
                    }
                }

                if (match) {
                    Field[] fields = clazz.getDeclaredFields();

                    for (Field field : fields) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        Object value = field.get(obj);
                        String valueStr = valueToString(value);

                        if (fieldName.startsWith("attr_")) {
                            String attrName = fieldName.substring(5);
                            if (valueStr.isEmpty()) {
                                element.removeAttribute(attrName);
                            } else {
                                element.setAttribute(attrName, valueStr);
                            }
                        } else {
                            NodeList children = element.getElementsByTagName(fieldName);
                            if (children.getLength() > 0) {
                                children.item(0).setTextContent(valueStr);
                            } else {
                                Element newChild = doc.createElement(fieldName);
                                newChild.setTextContent(valueStr);
                                element.appendChild(newChild);
                            }
                        }
                    }

                    saveToXML();
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Delete
     * Generic delete method to delete by field name
     * @param searchField The field name to search by ("id", "username")
     * @param searchValue The value to match
     * @return boolean
     */
    public boolean Delete(String searchField, Object searchValue) {
        if (searchValue == null) return false;

        NodeList list = doc.getElementsByTagName(tagName);
        String searchStr = searchValue.toString();
        boolean deleted = false;

        for (int i = list.getLength() - 1; i >= 0; i--) {
            Node node = list.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                boolean match = false;

                String attrValue = element.getAttribute(searchField);
                if (!attrValue.isEmpty() && attrValue.equals(searchStr)) {
                    match = true;
                }

                if (!match) {
                    String elementValue = getElementText(element, searchField);
                    if (!elementValue.isEmpty() && elementValue.equalsIgnoreCase(searchStr)) {
                        match = true;
                    }
                }

                if (match) {
                    node.getParentNode().removeChild(node);
                    deleted = true;
                }
            }
        }

        if (deleted) {
            saveToXML();
        }
        return deleted;
    }

}