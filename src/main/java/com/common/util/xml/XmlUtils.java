package com.common.util.xml;

import com.common.util.DateUtil;
import com.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author chengwei
 * @date 2018/9/5 11:07
 */
public class XmlUtils {
    private static final Logger log = LoggerFactory.getLogger(XmlUtils.class);
    private static final String XMLNS_XSI = "xmlns:xsi";
    private static final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation";
    private static final String LOGIC_YES = "yes";
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * Creates a new document instance.ß
     *
     * @return a new document instance
     * @throws IOException problem creating a new document
     */
    public static Document newDocument() throws IOException {
        Document doc = null;

        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage(), e);
        }

        return doc;
    }

    /**
     * Parses the content of the given XML file as an XML document.
     *
     * @param file the XML file instance
     * @return the document instance representing the entire XML document
     * @throws IOException problem parsing the XML file
     */
    public static Document getDocument(File file) throws IOException {
        InputStream in = getInputStream(file);
        return getDocument(new InputSource(in), null);
    }

    /**
     * Parses the content of the given stream as an XML document.
     *
     * @param xml the XML file input stream
     * @return the document instance representing the entire XML document
     * @throws IOException problem parsing the XML input stream
     */
    public static Document getDocument(InputSource xml, InputStream xsd) throws IOException {
        Document doc = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            if (xsd != null) {
                dbf.setNamespaceAware(true);
            }

            DocumentBuilder builder = dbf.newDocumentBuilder();
            doc = builder.parse(xml);

            if (xsd != null) {
                validateXml(doc, xsd);
            }
        } catch (ParserConfigurationException e) {
            throw new IOException("XML_PARSE_ERROR", e);
        } catch (SAXException e) {
            throw new IOException("XML_PARSE_ERROR", e);
        } catch (IOException e) {
            throw new IOException("XML_READ_ERROR", e);
        } finally {
            closeStream(xml.getByteStream());
        }

        return doc;
    }

    /**
     * Creates a root element as well as a new document with specific tag name.
     *
     * @param tagName the name of the root element
     * @return a new element instance
     * @throws IOException problem generating a new document
     */
    public static Element createRootElement(String tagName) throws IOException {
        Document doc = newDocument();
        Element root = doc.createElement(tagName);
        doc.appendChild(root);
        return root;
    }

    /**
     * Gets the root element from input stream.
     *
     * @param xml the XML file input stream
     * @return the root element of parsed document
     * @throws IOException problem parsing the XML file input stream
     */
    public static Element getRootElementFromStream(InputStream xml) throws IOException {
        return getDocument(new InputSource(xml), null).getDocumentElement();
    }

    /**
     * Gets the root element from input stream.
     *
     * @param xml the XML file input stream
     * @return the root element of parsed document
     * @throws IOException problem parsing the XML file input stream
     */
    public static Element getRootElementFromStream(InputStream xml, InputStream xsd) throws IOException {
        return getDocument(new InputSource(xml), xsd).getDocumentElement();
    }

    /**
     * Gets the root element from given XML file.
     *
     * @param xml the name of the XML file
     * @return the root element of parsed document
     * @throws IOException problem parsing the XML file
     */
    public static Element getRootElementFromFile(File xml) throws IOException {
        return getDocument(xml).getDocumentElement();
    }

    /**
     * Gets the root element from the given XML payload.
     *
     * @param payload the XML payload representing the XML file.
     * @return the root element of parsed document
     * @throws IOException problem parsing the XML payload
     */
    public static Element getRootElementFromString(String payload) throws IOException {
        if (payload == null || payload.length() < 1) {
            throw new IOException("XML_PAYLOAD_EMPTY");
        }

        StringReader sr = new StringReader(escapeXml(payload));
        InputSource source = new InputSource(sr);
        return getDocument(source, null).getDocumentElement();
    }

    /**
     * Gets the descendant elements list from the parent element.
     *
     * @param parent  the parent element in the element tree
     * @param tagName the specified tag name
     * @return the NOT NULL descendant elements list
     */
    public static List<Element> getElements(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        List<Element> elements = new ArrayList<Element>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                elements.add((Element) node);
            }
        }

        return elements;
    }

    /**
     * Gets the immediately descendant element from the parent element.
     *
     * @param parent  the parent element in the element tree
     * @param tagName the specified tag name.
     * @return immediately descendant element of parent element, NULL otherwise.
     */
    public static Element getElement(Element parent, String tagName) {
        List<Element> children = getElements(parent, tagName);

        if (children.isEmpty()) {
            return null;
        } else {
            return children.get(0);
        }
    }

    /**
     * Gets the immediately child elements list from the parent element.
     *
     * @param parent  the parent element in the element tree
     * @param tagName the specified tag name
     * @return the NOT NULL immediately child elements list
     */
    public static List<Element> getChildElements(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        List<Element> elements = new ArrayList<Element>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element && node.getParentNode() == parent) {
                elements.add((Element) node);
            }
        }

        return elements;
    }

    /**
     * Gets the immediately child elements list from the parent element.
     *
     * @param parent the parent element in the element tree
     * @return the NOT NULL immediately child elements list
     */
    public static List<Element> getChildElements(Element parent) {
        NodeList nodes = parent.getChildNodes();
        List<Element> elements = new ArrayList<Element>();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element && node.getParentNode() == parent) {
                elements.add((Element) node);
            }
        }

        return elements;
    }

    /**
     * Gets the immediately child element from the parent element.
     *
     * @param parent  the parent element in the element tree
     * @param tagName the specified tag name
     * @return immediately child element of parent element, NULL otherwise
     */
    public static Element getChildElement(Element parent, String tagName) {
        List<Element> children = getChildElements(parent, tagName);

        if (children.isEmpty()) {
            return null;
        } else {
            return children.get(0);
        }
    }

    /**
     * Gets the value of the child element by tag name under the given parent
     * element. If there is more than one child element, return the value of the
     * first one.
     *
     * @param parent  the parent element
     * @param tagName the tag name of the child element
     * @return value of the first child element, NULL if tag not exists
     */
    public static String getElementValue(Element parent, String tagName) {
        Element element = getElement(parent, tagName);
        if (element != null) {
            return element.getTextContent();
        } else {
            return null;
        }
    }

    /**
     * Gets the value of the immediately child element by tag name under the given parent
     * element. If there is more than one child element, return the value of the
     * first one.
     *
     * @param parent  the parent element
     * @param tagName the tag name of the child element
     * @return value of the first child element, NULL if tag not exists
     */
    public static String getChildElementValue(Element parent, String tagName) {
        Element element = getChildElement(parent, tagName);
        if (element != null) {
            return element.getTextContent();
        } else {
            return null;
        }
    }

    /**
     * Gets the text value of current element.
     *
     * @param element the current element
     * @return text value of the element, NULL if element not exists
     */
    public static String getElementValue(Element element) {
        if (element != null) {
            NodeList nodes = element.getChildNodes();
            if (nodes != null && nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node instanceof Text) {
                        return ((Text) node).getData();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Gets the specific attribute's value form current element.
     *
     * @param current  current element
     * @param attrName attribute name in current element
     * @return attribute value if not null
     */
    public static String getAttributeValue(Element current, String attrName) {
        if (current.hasAttribute(attrName)) {
            return current.getAttribute(attrName);
        } else {
            return null;
        }
    }

    /**
     * Appends the child element to the parent element.
     *
     * @param parent  the parent element
     * @param tagName the child element name
     * @return the child element added to the parent element
     */
    public static Element appendElement(Element parent, String tagName) {
        Element child = parent.getOwnerDocument().createElement(tagName);
        parent.appendChild(child);
        return child;
    }

    /**
     * Appends the child element as well as value to the parent element.
     *
     * @param parent  the parent element
     * @param tagName the child element name
     * @param value   the child element value
     * @return the child element added to the parent element
     */
    public static Element appendElement(Element parent, String tagName, String value) {
        Element child = appendElement(parent, tagName);
        child.setTextContent(value);
        return child;
    }

    /**
     * Appends another element as a child element.
     *
     * @param parent the parent element
     * @param child  the child element to append
     */
    public static void appendElement(Element parent, Element child) {
        Node tmp = parent.getOwnerDocument().importNode(child, true);
        parent.appendChild(tmp);
    }

    /**
     * Appends the CDATA element to the parent element.
     *
     * @param parent  the parent element
     * @param tagName the CDATA element name
     * @param value   the CDATA element value
     * @return the CDATA element added to the parent element
     */
    public static Element appendCDATAElement(Element parent, String tagName, String value) {
        Element child = appendElement(parent, tagName);
        if (value == null) { // avoid "null" word in the XML payload
            value = "";
        }

        Node cdata = child.getOwnerDocument().createCDATASection(value);
        child.appendChild(cdata);
        return child;
    }

    /**
     * Converts the Node/Element instance to XML payload.
     *
     * @param node the node/element instance to convert
     * @return the XML payload representing the node/element
     * @throws IOException problem converting XML to string
     */
    public static String childNodeToString(Node node) throws IOException {
        String payload = null;

        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();

            Properties props = tf.getOutputProperties();
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, LOGIC_YES);
            props.setProperty(OutputKeys.ENCODING, DEFAULT_CHARSET);
            tf.setOutputProperties(props);

            StringWriter writer = new StringWriter();
            tf.transform(new DOMSource(node), new StreamResult(writer));
            payload = escapeXml(writer.toString());
        } catch (TransformerException e) {
            throw new IOException("XML_TRANSFORM_ERROR", e);
        }

        return payload;
    }

    /**
     * Converts the Node/Document/Element instance to XML payload.
     *
     * @param node the node/document/element instance to convert
     * @return the XML payload representing the node/document/element
     * @throws IOException problem converting XML to string
     */
    public static String nodeToString(Node node) throws IOException {
        String payload = null;

        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();

            Properties props = tf.getOutputProperties();
            props.setProperty(OutputKeys.ENCODING, DEFAULT_CHARSET);
            props.setProperty(OutputKeys.INDENT, LOGIC_YES);
            tf.setOutputProperties(props);

            StringWriter writer = new StringWriter();
            tf.transform(new DOMSource(node), new StreamResult(writer));
            payload = escapeXml(writer.toString());
        } catch (TransformerException e) {
            throw new IOException("XML_TRANSFORM_ERROR", e);
        }

        return payload;
    }

    public static String escapeXml(String payload) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < payload.length(); i++) {
            char c = payload.charAt(i);
            if ((c == 0x9) || (c == 0xA) || (c == 0xD) || ((c >= 0x20) && (c <= 0xD7FF))
                    || ((c >= 0xE000) && (c <= 0xFFFD))){
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Converts the an XML file to XML payload.
     *
     * @param file the XML file instance
     * @return the XML payload representing the XML file
     * @throws IOException problem transforming XML to string
     */
    public static String xmlToString(File file) throws IOException {
        Element root = getRootElementFromFile(file);
        return nodeToString(root);
    }

    /**
     * Converts the an XML file input stream to XML payload.
     *
     * @param in the XML file input stream
     * @return the payload represents the XML file
     * @throws IOException problem transforming XML to string
     */
    public static String xmlToString(InputStream in) throws IOException {
        Element root = getRootElementFromStream(in);
        return nodeToString(root);
    }

    /**
     * Saves the node/document/element as XML file.
     *
     * @param doc  the XML node/document/element to save
     * @param file the XML file to save
     * @throws IOException problem persisting XML file
     */
    public static void saveToXml(Node doc, File file) throws IOException {
        saveToXml(doc, file, DEFAULT_CHARSET);
    }

    /**
     * Saves the node/document/element as XML file.
     *
     * @param doc     the XML node/document/element to save
     * @param file    the XML file to save
     * @param charset the string encoding charset
     * @throws IOException problem persisting XML file
     */
    public static void saveToXml(Node doc, File file, String charset) throws IOException {
        Writer writer = null;

        try {
            Transformer tf = TransformerFactory.newInstance().newTransformer();

            Properties props = tf.getOutputProperties();
            props.setProperty(OutputKeys.METHOD, XMLConstants.XML_NS_PREFIX);
            props.setProperty(OutputKeys.INDENT, LOGIC_YES);
            tf.setOutputProperties(props);

            DOMSource dom = new DOMSource(doc);
            writer = new OutputStreamWriter(getOutputStream(file), charset);
            Result result = new StreamResult(writer);
            tf.transform(dom, result);
        } catch (Exception e) {
            throw new IOException("XML_WRITE_FILE_ERROR", e);
        } finally {
            closeStream(writer);
        }
    }

    /**
     * Validates the XML file via the given XML schema file.
     *
     * @param xml the XML file to validate
     * @param xsd the XML schema file
     * @throws IOException error occurs if validation fail
     */
    public static void validateXml(InputStream xml, InputStream xsd) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(xml);
            validateXml(doc, xsd);
        } catch (SAXException e) {
            throw new IOException("XML_VALIDATE_ERROR", e);
        } catch (Exception e) {
            throw new IOException("XML_READ_ERROR", e);
        } finally {
            closeStream(xml);
            closeStream(xsd);
        }
    }

    /**
     * Validates the XML file via the given XML schema file.
     *
     * @param root the root element of XML
     * @param xsd  the XML schema file
     * @throws IOException error occurs if validation fail
     */
    public static void validateXml(Node root, InputStream xsd) throws IOException {
        try {
            Source source = new StreamSource(xsd);
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(source);

            Validator validator = schema.newValidator();
            validator.validate(new DOMSource(root));
        } catch (SAXException e) {
            if (log.isErrorEnabled()) {
                log.error("检验֤XML出错\n" + nodeToString(root));
            }
            throw new IOException("XML_VALIDATE_ERROR", e);
        } catch (Exception e) {
            throw new IOException("XML_READ_ERROR", e);
        } finally {
            closeStream(xsd);
        }
    }

    /**
     * Transforms the XML content to XHTML/HTML format string with the XSL.
     *
     * @param payload  the XML payload to convert
     * @param xsltFile the XML stylesheet file
     * @return the transformed XHTML/HTML format string
     * @throws IOException problem converting XML to HTML
     */
    public static String xmlToHtml(String payload, File xsltFile) throws IOException {
        String result = null;

        try {
            Source template = new StreamSource(xsltFile);
            Transformer transformer = TransformerFactory.newInstance().newTransformer(template);

            Properties props = transformer.getOutputProperties();
            props.setProperty(OutputKeys.OMIT_XML_DECLARATION, LOGIC_YES);
            transformer.setOutputProperties(props);

            StreamSource source = new StreamSource(new StringReader(payload));
            StreamResult sr = new StreamResult(new StringWriter());
            transformer.transform(source, sr);

            result = sr.getWriter().toString();
        } catch (TransformerException e) {
            throw new IOException("XML_TRANSFORM_ERROR", e);
        }

        return result;
    }

    /**
     * Sets the namespace to specific element.
     *
     * @param element        the element to set
     * @param namespace      the namespace to set
     * @param schemaLocation the XML schema file location URI
     */
    public static void setNamespace(Element element, String namespace, String schemaLocation) {
        element.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE, namespace);
        element.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLNS_XSI, XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        element.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XSI_SCHEMA_LOCATION, schemaLocation);
    }

    /**
     * Encode the XML payload to legality character.
     *
     * @param payload the XML payload to encode
     * @return the encoded XML payload
     * @throws IOException problem encoding the XML payload
     */
    public static String encodeXml(String payload) throws IOException {
        Element root = createRootElement(XMLConstants.XML_NS_PREFIX);
        root.setTextContent(payload);
        return childNodeToString(root.getFirstChild());
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    private static InputStream getInputStream(File file) throws IOException {
        InputStream in = null;

        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IOException("XML_FILE_NOT_FOUND", e);
        }

        return in;
    }

    private static OutputStream getOutputStream(File file) throws IOException {
        OutputStream in = null;

        try {
            in = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new IOException("XML_FILE_NOT_FOUND", e);
        }

        return in;
    }


    public static String XmlFromObject(Object obj) throws Exception {
        return XmlFromObject("xmlData", obj);
    }

    public static String XmlFromObject(String rootName, Object obj) throws Exception {
        return XmlFromObject(rootName, obj, true);
    }

    public static String XmlFromObject(String rootName, Object obj, boolean appendVersion) throws Exception{
        String rtn = "";
        if (appendVersion) {
            rtn = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
        }
        rtn += "<" + rootName + ">" + getXmlString(obj) + "</" + rootName + ">";
        return rtn;
    }


    public static String getXmlString(Object obj) throws Exception{
        if (obj == null) {
            return "";
        }

        if (obj instanceof Number){
            return numberToString((Number)obj);
        }
        if (obj instanceof Boolean) {
            return obj.toString();
        }

        if (obj instanceof String){
            //特殊字符串转义
            return obj.toString().replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("'", "&apos;").replace("\"", "&quot;") ;
        }


        if (obj instanceof Map) {
            return mapToString((Map)obj);
        }

        //class
        if(obj instanceof Class) {
            return ((Class) obj).getSimpleName();
        }
        //enum
        if(obj instanceof Enum){
            return obj.toString();
        }

        // 日期
        if (obj instanceof Date) {
            return DateUtil.toDateString((Date)obj);
            //return DateUtil.FMT_DATETIME.format((Date)obj);
        }

        //数组
        if (obj instanceof Collection || obj.getClass().isArray()) {
            return getXmlArray(obj);
        }

        return reflectObject(obj);
    }

    @SuppressWarnings("unchecked")
    private static String mapToString(Map map) {
        try {
            Iterator keys = map.keySet().iterator();

            StringBuffer sb = new StringBuffer("");

            while (keys.hasNext()) {
                Object o = keys.next();
                sb.append("<" + o.toString() + ">");
                sb.append(getXmlString(map.get(o)));
                sb.append("</" + o.toString() + "> \n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private static String getXmlArray(Object obj) throws Exception{
        if(obj == null){
            return "";
        }

        StringBuffer sb = new StringBuffer("");
        if(obj instanceof Collection){
            Iterator iterator = ((Collection)obj).iterator();
            while(iterator.hasNext()){
                Object rowObj = iterator.next();
                sb.append(getXmlString(rowObj));
            }
        }
        if(obj.getClass().isArray()){
            int arrayLength = Array.getLength(obj);
            for(int i = 0; i < arrayLength; i ++){
                Object rowObj = Array.get(obj, i);
                sb.append(getXmlString(rowObj));
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static String reflectObject(Object bean) {
        Class c = bean.getClass();
        Method[] methods = c.getMethods();

        String ss = bean.getClass().getSimpleName();
        if (Character.isUpperCase(ss.charAt(0)) ) {
            if (ss.length() == 1) {
                ss = ss.toLowerCase();
            } else if (Character.isUpperCase(ss.charAt(1))) {
                ss = ss.substring(0, 2).toLowerCase() + ss.substring(2);
            } else {
                ss = ss.substring(0, 1).toLowerCase() + ss.substring(1);
            }
        }

        StringBuffer sb = new StringBuffer("<" + ss + "> \n");
        for (int i = 0; i < methods.length; i += 1) {
            try {
                Method method = methods[i];
                String name = method.getName();
                String key = "";
                if (name.startsWith("get")) {
                    key = name.substring(3);
                } else if (name.startsWith("is")) {
                    key = name.substring(2);
                }
                if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
                    if (key.length() == 1) {
                        key = key.toLowerCase();
                    } else if (!Character.isUpperCase(key.charAt(1))) {
                        key = key.substring(0, 1).toLowerCase() + key.substring(1);
                    }

                    Object elementObj = method.invoke(bean, null);
                    if(elementObj instanceof Class)
                        continue;

                    String str = getXmlString(elementObj);
                    if (!StringUtils.isEmpty(str) && str.length() > 0) {
                        sb.append("<" + key + ">");
                        sb.append(str);
                        sb.append("</" + key + "> \n");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("</" + ss + "> \n");
        return sb.toString();
    }

    private static String numberToString(Number number) throws Exception{
        if (number == null) {
            return "";
        }

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }
}
