/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

/**
 *
 * @author John
 */
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SOAPProcessor2 {

    public static void main(String argv[]) throws FileNotFoundException, SAXException, IOException, XPathExpressionException {

        try {

            String expression = "/Envelope/Body/ListAllArtistsResponse/ListAllArtistsResult";

            SOAPProcessor2 xp = new SOAPProcessor2();

            ArbolNodo root = xp.parseXML2014(new FileInputStream("D:/tempo/cachexml9.xml"), expression);

            Gson g = new Gson();
            System.out.println(g.toJson(root));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int nroElements(Node node) {
        int n = 0;
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node nn = node.getChildNodes().item(i);
            if (nn.getNodeType() == Node.ELEMENT_NODE) {
                n++;
            }
        }
        return n;
    }

    public int maxLevel(Node node) {
        int l = 0;
        NodeList children = node.getChildNodes();
        int len = children.getLength();
        for (int i = 0; i < len; i++) {
            l = maxLevel(children.item(i)) + 1;
        }

        return l;
    }

    private int getNro(NodeList nodeList, String attr) {
        int count = 0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                //System.out.println(":: " + n.getNodeName() + " : " + attr);
                if (n.getNodeName().equals(attr)) {
                    count++;
                }
            }
        }
        return count;
    }

    public void rec(Node node, ArbolNodo root, int level) {
        //System.out.println("Name: " + node.getNodeName());
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NodeList children = node.getChildNodes();
            System.out.println(node.getNodeName() + ", LNG: " + children.getLength());
            int len = children.getLength();
            for (int i = 0; i < len; i++) {
                Node n = children.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (n.getChildNodes().getLength() == 1) {
                        int na = getNro(children, n.getNodeName());
                        if (na == 1) {
                            //System.out.println(n.getNodeName() + ", " + n.);
                            root.addAttribute(n.getNodeName(), n.getTextContent());
                        } else {
                            ArbolNodo an = new ArbolNodo(n.getNodeName());
                            an.addAttribute(n.getNodeName(), n.getTextContent());
                            root.addChildren(an);
                        }
                    }
                }
            }
            for (int i = 0; i < len; i++) {
                Node n = children.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (n.getChildNodes().getLength() > 1) {
                        ArbolNodo an = new ArbolNodo(n.getNodeName());
                        root.addChildren(an);
                        rec(n, an, level++);
                    }
                }
            }
        }
    }

    public ArbolNodo parseXML2014(String xml, String expression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        return parseXML2014(builder.parse(new ByteArrayInputStream(xml.getBytes())), expression);
    }

    public ArbolNodo parseXML2014(FileInputStream file, String expression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        return this.parseXML2014(builder.parse(file), expression);
    }

    public ArbolNodo parseXML2014(Document document, String expression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        XPath xPath = XPathFactory.newInstance().newXPath();
        System.out.println("XPath expression: " + expression);
        //read a nodelist using xpath
        Node node = (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);
        if (node != null) {
            System.out.println(" - RootName" + node.getNodeName());
            //System.out.println(" - AttrLength: " + node.getAttributes().getLength());
            //System.out.println(" - ChildLength: " + node.getChildNodes().getLength());

            //System.out.println(" - MaxLevels: " + maxLevel(node));
            ArbolNodo tree = new ArbolNodo(node.getNodeName());

            rec(node, tree, 1);

            System.out.println(new Gson().toJson(tree));

            return tree;
        }
        return null;
    }
    static Document document;
    protected static PrintWriter out;

    public void imprimirNodos(Node node) {

        // Si ya no existen nodos por Imprimir salir.....
        if (node == null) {
            return;
        }

        /**
         * Investigar el Tipo de Nodo
         */
        int type = node.getNodeType();
        //System.err.println("type:" + type);
        /**
         * En base al Tipo de Nodo ejecutar
         */
        switch (type) {

            // Imprimir Documento 
            case Node.DOCUMENT_NODE: {
                NodeList children = node.getChildNodes();
                for (int iChild = 0; iChild < children.getLength(); iChild++) {
                    imprimirNodos(children.item(iChild));
                }
                out.flush();
                break;
            }

            // Imprimir elementos con atributos
            case Node.ELEMENT_NODE: {
                out.print('<');
                out.print(node.getNodeName());
                Attr attrs[] = sortAttributes(node.getAttributes());
                for (int i = 0; i < attrs.length; i++) {
                    Attr attr = attrs[i];
                    out.print(' ');
                    out.print(attr.getNodeName());
                    out.print("=\"");
                    out.print(attr.getNodeValue());
                    out.print('"');
                }
                out.print('>');
                NodeList children = node.getChildNodes();
                if (children != null) {
                    int len = children.getLength();
                    for (int i = 0; i < len; i++) {
                        imprimirNodos(children.item(i));
                    }
                }
                break;
            }

            // Imprimir Texto 
            case Node.TEXT_NODE: {
                out.print(node.getNodeValue());
                break;
            }

            // Imprimir Nodos con Instrucciones de Proceso 
            case Node.PROCESSING_INSTRUCTION_NODE: {
                out.print("<?");
                out.print(node.getNodeName());
                String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                    out.print(' ');
                    out.print(data);
                }
                out.println("?>");
                break;
            }

            // Imprimir Texto de Elementos CDATA
            case Node.CDATA_SECTION_NODE: {
                out.print(node.getNodeValue());
                break;
            }

        } // Termina Switch

        // En caso de ser nodo de Elemento cerrar Tag en Pantalla 
        if (type == Node.ELEMENT_NODE) {
            out.print("</");
            out.print(node.getNodeName());
            out.print('>');
        }

        // Enviar a Pantalla Buffer
        out.flush();

    } // Termina Impresion de Nodos 

    /**
     * Funcion utilizada para Ordenar Atributos de Elementos
     */
    protected Attr[] sortAttributes(NamedNodeMap attrs) {

        int len = (attrs != null) ? attrs.getLength() : 0;
        Attr array[] = new Attr[len];
        for (int i = 0; i < len; i++) {
            array[i] = (Attr) attrs.item(i);
        }
        for (int i = 0; i < len - 1; i++) {
            String name = array[i].getNodeName();
            int index = i;
            for (int j = i + 1; j < len; j++) {
                String curName = array[j].getNodeName();
                if (curName.compareTo(name) < 0) {
                    name = curName;
                    index = j;
                }
            }
            if (index != i) {
                Attr temp = array[i];
                array[i] = array[index];
                array[index] = temp;
            }
        }

        return (array);

    } // Terminar ordenar de Atributos 
}
