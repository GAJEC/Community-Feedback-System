package common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;

public final class XmlMessageCodec {
    private static final String ROOT = "message";
    private static final String PAYLOAD = "payload";
    private static final String ENCODING = "base64-java";

    private XmlMessageCodec() {
    }

    public static String encodeObject(Object object) {
        if (object == null) {
            return null;
        }

        try {
            byte[] bytes = serialize(object);
            String b64 = Base64.getEncoder().encodeToString(bytes);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement(ROOT);
            document.appendChild(root);

            Element payload = document.createElement(PAYLOAD);
            payload.setAttribute("encoding", ENCODING);
            payload.setTextContent(b64);
            root.appendChild(payload);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object decodeObject(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            return null;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));

            Element root = document.getDocumentElement();
            if (root == null || !ROOT.equals(root.getTagName())) {
                return null;
            }

            Element payload = (Element) root.getElementsByTagName(PAYLOAD).item(0);
            if (payload == null) {
                return null;
            }

            String encoding = payload.getAttribute("encoding");
            if (!ENCODING.equals(encoding)) {
                return null;
            }

            String b64 = payload.getTextContent();
            if (b64 == null || b64.trim().isEmpty()) {
                return null;
            }

            byte[] bytes = Base64.getDecoder().decode(b64.trim());
            return deserialize(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object decodeInboundObject(Object receivedRaw) {
        if (receivedRaw instanceof String xmlString) {
            Object decoded = decodeObject(xmlString);
            return decoded != null ? decoded : xmlString;
        }
        return receivedRaw;
    }

    private static byte[] serialize(Object object) throws Exception {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        java.io.ObjectOutputStream objectOutput = new java.io.ObjectOutputStream(byteOutput);
        objectOutput.writeObject(object);
        objectOutput.flush();
        return byteOutput.toByteArray();
    }

    private static Object deserialize(byte[] bytes) throws Exception {
        ByteArrayInputStream byteInput = new ByteArrayInputStream(bytes);
        java.io.ObjectInputStream objectInput = new java.io.ObjectInputStream(byteInput);
        return objectInput.readObject();
    }
}
