package server.components;

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

    // CRUD bois

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
                String valueStr = (value != null) ? value.toString() : "";

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
     * To search our target user by id
     * @param id the target user
     * @return Object
     */
    public T ReadbyId(String id) {
        NodeList list = doc.getElementsByTagName(tagName);
        for (int i = 0; i < list.getLength(); i++) {
            Element el = (Element) list.item(i);
            if (el.getAttribute("id").equals(id)) return map(el);
        }
        return null;
    }

    /** Read
     * To search our target user by username
     * @param username the target user
     * @return Object
     */
    public T ReadbyUsername(String username){
        NodeList list = doc.getElementsByTagName(tagName);
        for (int i = 0; i < list.getLength(); i++) {
            Element el = (Element) list.item(i);
            if (getElementText(el, "username").equalsIgnoreCase(username)) return map(el);
        }
        return null;
    }

    /** Read
     * To map shet di ko alam paano sa words per eto sample
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
                values[i] = convert(raw, types[i]);
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
     * @return
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
            if (type == int.class || type == Integer.class) return 0;
            return val;
        }

        // Kung gusto nyo magdagdag pa ng datatype pwede naman
        if (type == String[].class) return new String[]{val};
        if (type == int.class || type == Integer.class) return Integer.parseInt(val);

        return val;
    }

    /**
     * To update a user,
     * @param idValue
     * @param obj
     */
    public void Update(String idValue, T obj) {
        try {
            NodeList list = doc.getElementsByTagName(tagName);
            boolean found = false;

            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);

                if (element.getAttribute("id").equals(idValue)) {
                    Field[] fields = clazz.getDeclaredFields();

                    for (Field field : fields) {
                        field.setAccessible(true);
                        String fieldName = field.getName();
                        Object value = field.get(obj);
                        String valueStr = (value != null) ? value.toString() : "";

                        if (fieldName.startsWith("attr_")) {
                            element.setAttribute(fieldName.substring(5), valueStr);
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
                    found = true;
                    break;
                }
            }
            if (found) {
                saveToXML();
                System.out.println("Saved bobo");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** Delete
     * To delete a data by specific ID
     * @param tagName The RootName of Target
     * @param id The target's id
     */
    public void Delete(String tagName, String id) {
        NodeList list = doc.getElementsByTagName(tagName);

        // Iterate backwards to avoid index shifting issues when deleting
        for (int i = list.getLength() - 1; i >= 0; i--) {
            Node node = list.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("id").equals(id)) {
                    node.getParentNode().removeChild(node);
                }
            }
        }
        saveToXML();
    }
    public void Delete(String id){
        Delete(tagName, id);
    }
}