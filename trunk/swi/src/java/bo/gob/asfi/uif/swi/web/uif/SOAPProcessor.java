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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SOAPProcessor {

    public static void main(String argv[]) throws FileNotFoundException, SAXException, IOException, XPathExpressionException {

        try {

            //String expression = "Envelope/Body/ListOfContinentsByNameResponse/ListOfContinentsByNameResult/tContinent";
            //String expression = "Envelope/Body/sumarResponse";
            //String expression = "/Envelope/Body/GetWeatherResponse";
            //
            //String expression = "/ListAllArtistsResult/Artist";
            //String expression = "/Envelope/Body/ListOfContinentsByNameResponse/ListOfContinentsByNameResult/tContinent";
            //String expression = "/Envelope/Body/ListAllArtistsResponse/ListAllArtistsResult/Artist";
            String expression = "/Envelope/Body/sacalistaResponse/return";
            //String expression = "/Envelope/Body/ListOfCountryNamesByNameResponse/ListOfCountryNamesByNameResult/tCountryCodeAndName";

            SOAPProcessor xp = new SOAPProcessor();
            //List<Map<String, String>> lst = xp.parseXML(new FileInputStream("/root/Escritorio/swi/web/newXMLDocument.xml"), expression);
            //List<Map<String, String>> lst = xp.parseXML2(new FileInputStream("D:/tempo/Albunes.xml"), expression);
            //2013
            //List<Map<String, String>> lst = xp.parseXML2(new FileInputStream("D:/tempo/cachexml2.xml"), expression);
            //2014
            List<Map<String, String>> lst = xp.parseXML2014(new FileInputStream("D:/tempo/cachexml2.xml"), expression);

            Gson g = new Gson();
            System.out.println(g.toJson(lst));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //Document xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes()));

    public List<Map<String, String>> parseXML(Document document, String expression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        List<Map<String, String>> lst = new ArrayList<Map<String, String>>();

//        //DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
//        //DocumentBuilder builder = builderFactory.newDocumentBuilder();
        XPath xPath = XPathFactory.newInstance().newXPath();
//        System.out.println("XPath expression: " + expression);
        //read a nodelist using xpath
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
        System.out.println("Node Selected Length: " + nodeList.getLength());
        Set<String> labels = new HashSet<String>();
        List<String> data = new ArrayList<String>();
        //Integer i = 1;
        rec2(nodeList, "", labels, data, 0, 0);
        System.out.println("===================================  ");
        int levels = 0;
        for (String l : labels) {
            System.out.println(l);
            int x = Integer.parseInt(l.substring(0, l.indexOf(":#")));
            if (x > levels) {
                levels = x;
            }
        }
        System.out.println("=================== levels " + levels);
        List<Map<String, String>> lstdata = new ArrayList<Map<String, String>>();
        boolean sw = true;
        for (int i = levels; i >= 1; i--) {
            System.out.println("=================== reading level " + i);
            for (String lbl : labels) {
                if (lbl.split(":#")[0].equals(Integer.toString(i))) {
                    String lr = lbl.split(":#")[1];
                    System.out.println(lr);

                    if (i == levels) {
                        int indexdata = 0;
                        int rootindex = 0;
                        for (String d : data) {
                            if (d.equals("[item]")) {
                                rootindex++;
                            }
                            if (d.split(":#")[0].equals(Integer.toString(levels))) {
                                String dt = d.split(":#")[1];
                                if (dt.indexOf(lr) != -1) {
                                    String val = dt.substring(dt.indexOf(lr) + lr.length() + 1);
                                    //System.out.println(val);
                                    if (sw) {
                                        Map<String, String> item = new HashMap<String, String>();
                                        item.put(lr, val);
                                        item.put("_root_", Integer.toString(rootindex));
                                        lstdata.add(item);
                                        indexdata++;
                                    } else {
                                        lstdata.get(indexdata).put(lr, val);
                                        indexdata++;
                                    }
                                }
                            }
                        }
                        if (sw) {
                            sw = false;
                        }
                    } else {
                        //int indexdata = 0;
                        int rootindex = 0;
                        for (String d : data) {
                            if (d.equals("[item]")) {
                                rootindex++;
                            }
                            if (d.split(":#")[0].equals(Integer.toString(i))) {
                                String dt = d.split(":#")[1];
                                if (dt.indexOf(lr) != -1) {
                                    String val = dt.substring(dt.indexOf(lr) + lr.length() + 1);
                                    System.out.println(val);
                                    for (Map<String, String> m : lstdata) {
                                        if (m.get("_root_").equals(Integer.toString(rootindex))) {
                                            m.put(lr, val);
                                        }
                                    }
                                }
                            }
                        }
                    }
//                    for (String d : data) {
//                        System.out.println(d);
//                    }
                }
            }
        }
        System.out.println(new Gson().toJson(lstdata));
        return lstdata;
//        System.out.println("Node Selected Length: " + nodeList.getLength());
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node n = nodeList.item(i);
//            Map<String, String> map = new HashMap<String, String>();
//            for (int j = 0; j < n.getChildNodes().getLength(); j++) {
//                Node n2 = n.getChildNodes().item(j);
//                if (n2.getNodeType() == Node.ELEMENT_NODE) {
//                    map.put(n2.getNodeName(), n2.getTextContent());
//                }
//            }
//            lst.add(map);
//        }
//        return lst;
    }

    public List<Map<String, String>> parseXML(FileInputStream file, String expression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        //Reader reader = new InputStreamReader(file,"UTF-8");
        //InputSource is = new InputSource(reader);
        return parseXML(builder.parse(file), expression);
    }

    public List<Map<String, String>> parseXML(String xml, String expression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        return parseXML(builder.parse(new ByteArrayInputStream(xml.getBytes())), expression);
    }

    /**
     *
     * @param file
     * @param expression
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public List<Map<String, String>> parseXML2(FileInputStream file, String expression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        List<Map<String, String>> lst = new ArrayList<Map<String, String>>();
        //DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        //DocumentBuilder builder = builderFactory.newDocumentBuilder();
        XPath xPath = XPathFactory.newInstance().newXPath();
        System.out.println("XPath expression: " + expression);
        //read a nodelist using xpath
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(builder.parse(file), XPathConstants.NODESET);
        System.out.println("Node Selected Length: " + nodeList.getLength());
        Set<String> labels = new HashSet<String>();
        List<String> data = new ArrayList<String>();
        //Integer i = 1;
        rec2(nodeList, "", labels, data, 0, 0);
        System.out.println("===================================  ");
        int levels = 0;
        for (String l : labels) {
            System.out.println("lbl: " + l);
            int x = Integer.parseInt(l.substring(0, l.indexOf(":#")));
            if (x > levels) {
                levels = x;
            }
        }
        System.out.println("=================== levels " + levels);
        List<Map<String, String>> lstdata = new ArrayList<Map<String, String>>();
        boolean sw = true;
        for (int i = levels; i >= 1; i--) {
            System.out.println("=================== reading level " + i);
            for (String lbl : labels) {
                if (lbl.split(":#")[0].equals(Integer.toString(i))) {
                    String lr = lbl.split(":#")[1];
                    System.out.println(lr);

                    if (i == levels) {
                        int indexdata = 0;
                        int rootindex = 0;
                        for (String d : data) {
                            if (d.equals("[item]")) {
                                rootindex++;
                            }

                            if (d.split(":#")[0].equals(Integer.toString(levels))) {
                                String dt = d.split(":#")[1];
                                System.out.println("dt:  " + dt);
                                System.out.println("lr:  " + lr);
                                if (dt.indexOf(lr) != -1) {
                                    System.out.println(" kkkk ");
                                    String val = dt.substring(dt.indexOf(lr) + lr.length() + 1);
                                    System.out.println(val);
                                    if (sw) {
                                        Map<String, String> item = new HashMap<String, String>();
                                        item.put(lr, val);
                                        item.put("_root_", Integer.toString(rootindex));
                                        lstdata.add(item);
                                        indexdata++;
                                    } else {
                                        lstdata.get(indexdata).put(lr, val);
                                        indexdata++;
                                    }
                                }
                            }
                        }
                        if (sw) {
                            sw = false;
                        }
                    } else {
                        //int indexdata = 0;
                        int rootindex = 0;
                        for (String d : data) {
                            if (d.equals("[item]")) {
                                rootindex++;
                            }
                            if (d.split(":#")[0].equals(Integer.toString(i))) {
                                String dt = d.split(":#")[1];
                                if (dt.indexOf(lr) != -1) {
                                    String val = dt.substring(dt.indexOf(lr) + lr.length() + 1);
                                    System.out.println(val);
                                    for (Map<String, String> m : lstdata) {
                                        if (m.get("_root_").equals(Integer.toString(rootindex))) {
                                            m.put(lr, val);
                                        }
                                    }
                                }
                            }
                        }
                    }
//                    for (String d : data) {
//                        System.out.println(d);
//                    }
                }
            }
        }
        System.out.println(new Gson().toJson(lstdata));
//        return lstdata;   
        for (String d : data) {
            System.out.println(d);
        }
        return null;
//        for (String l : data) {                        
//            if (l.split("/").length == levels) {
//                System.out.println(l.split("/")[levels - 1]);
//            }
//        }




        //System.out.println(" ==== ");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Node n = nodeList.item(i);
//            System.out.println(n.getNodeType());
//            System.out.println(n.getChildNodes().getLength());
//
//            Map<String, String> map = new HashMap<String, String>();
//            for (int j = 0; j < n.getChildNodes().getLength(); j++) {
//                Node n2 = n.getChildNodes().item(j);
//                if (n2.getNodeType() == Node.ELEMENT_NODE) {
//                    map.put(n2.getNodeName(), n2.getTextContent());
//                }
//            }
//            lst.add(map);
//        }
//        return lst;
    }

    public void rec2(NodeList nodeList, String root, Set<String> labels, List<String> data, int level, int x) {
        //Set<String> labels = new HashSet<String>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == 1) {
                if (root.equals("")) {
                    System.out.println("--- " + i);
                    data.add("[item]");
                    level = 0;
                }

                if (n.getChildNodes().getLength() > 1) {

                    level++;
                    rec2(n.getChildNodes(), root + "/" + n.getNodeName(), labels, data, level--, i);
                } else {
                    //System.out.println(root + "  :  " + n.getNodeName() + ":" + n.getTextContent());  
                    //System.out.println(n.getParentNode().getNodeName() + " : " + i + "  -  " + level + " : " + root + "/" + n.getNodeName() + "/" + n.getTextContent());
                    System.out.println(root + " # " + n.getNodeName() + ":" + n.getTextContent());
                    labels.add(level + ":#/" + n.getParentNode().getNodeName() + "/" + n.getNodeName());
                    data.add(level + ":#" + root + "/" + n.getNodeName() + "/" + n.getTextContent());
                    //System.out.println(root + " :: " + n.getParentNode().getNodeName() + " : [" + n.getNodeName() + "," + n.getTextContent() + "]");                    
                }
            }
        }
        //return labels;
    }

    public void rec(NodeList nodeList, String root, Set<String> labels, List<String> data, int level) {
        //Set<String> labels = new HashSet<String>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == 1) {
                if (root.equals("")) {
                    System.out.println("--- " + i);
                    data.add("[item]");
                    level = 0;
                } 
                //System.out.println(n.getNodeType());
                //System.out.println(n.getChildNodes().getLength());
                //System.out.println(n.getNodeName() + ":" + n.getTextContent());
                //System.out.println(n.getTextContent());

                if (n.getChildNodes().getLength() > 1) {
                    //System.out.println("====== has items: " + n.getNodeName());
                    //rec(n.getChildNodes(), root + " > " + n.getNodeName());
                    level++;
                    rec(n.getChildNodes(), root + "/" + n.getNodeName(), labels, data, level--);
                } else {
                    //System.out.println(root + "  :  " + n.getNodeName() + ":" + n.getTextContent());  
                    System.out.println(level + " : " + root + "/" + n.getNodeName() + "/" + n.getTextContent());
                    labels.add(level + ":#/" + n.getParentNode().getNodeName() + "/" + n.getNodeName());
                    data.add(level + ":#" + root + "/" + n.getNodeName() + "/" + n.getTextContent());
                    //System.out.println(root + " :: " + n.getParentNode().getNodeName() + " : [" + n.getNodeName() + "," + n.getTextContent() + "]");                    
                }
            }
        }
        //return labels;
    }

    private int getNro(NodeList nodeList, String attr) {
        int count = 0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println(":: " + n.getNodeName() + " : " + attr);
                if (n.getNodeName().equals(attr)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 2014 Parser XML
     *
     * @param nodeList
     * @param map
     * @param recs
     * @param labels
     */
    public void rec2014(NodeList nodeList, Map<String, Object> map, List<Map<String, Object>> recs, Set<String> labels) {
        boolean entity = false;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (n.getChildNodes().getLength() == 1) {
                    entity = true;
                    labels.add(n.getNodeName());
                    System.out.println("  -  " + n.getNodeName());
                    System.out.println("  -  " + getNro(n.getParentNode().getChildNodes(), n.getNodeName()));

                    map.put(n.getNodeName(), n.getTextContent());
                }
            }
        }

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (n.getChildNodes().getLength() > 1) {
                    if (!entity) {
                        rec2014(n.getChildNodes(), new HashMap<String, Object>(), recs, labels);
                    } else {
                        System.out.println("  -  " + n.getNodeName());
                        int repeatAttr = getNro(n.getParentNode().getChildNodes(), n.getNodeName());
                        System.out.println("  -  " + repeatAttr);
                        if(repeatAttr == 1) { 
                           map.put(n.getNodeName(), new ArrayList<Map<String, Object>>());
                           rec2014(n.getChildNodes(), map, (List) map.get(n.getNodeName()), labels);
                        } 
                        if(repeatAttr > 1) { 
                           //isDuplis = true;
                           if(map.get(n.getNodeName()) == null) {
                               map.put(n.getNodeName(), new ArrayList<Map<String, Object>>());                               
                           }
                           rec2014(n.getChildNodes(), new HashMap<String, Object>(), (List) map.get(n.getNodeName()), labels);
                        } 
                    }
                }
            }
        }
        if (entity) {
            recs.add(map);
        }
    }

    public void rec20142(NodeList nodeList, Map<String, Object> map, List<Map<String, Object>> recs, Set<String> labels) {
        boolean entity = false;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (n.getChildNodes().getLength() == 1) {
                    entity = true;
                    labels.add(n.getNodeName());
                    map.put(n.getNodeName(), n.getTextContent());
                }
            }
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (n.getChildNodes().getLength() > 1) {
                    if (!entity) {
                        rec2014(n.getChildNodes(), new HashMap<String, Object>(), recs, labels);
                    } else {
                        map.put(n.getNodeName(), new ArrayList<Map<String, Object>>());
                        rec2014(n.getChildNodes(), map, (List) map.get(n.getNodeName()), labels);
                    }
                }
            }
        }
        if (entity) {
            recs.add(map);
        }
    }

    public List<Map<String, String>> parseXML2014(FileInputStream file, String expression) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
        //DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        //DocumentBuilder builder = builderFactory.newDocumentBuilder();
        XPath xPath = XPathFactory.newInstance().newXPath();
        System.out.println("XPath expression: " + expression);
        //read a nodelist using xpath
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(builder.parse(file), XPathConstants.NODESET);
        System.out.println("Node Selected Length: " + nodeList.getLength());
        Map<String, Object> mlst = new HashMap<String, Object>();
        Set<String> labels = new HashSet<String>();
        rec2014(nodeList, mlst, lst, labels);
        System.out.println(new Gson().toJson(lst));
        for (String s : labels) {
            System.out.println("lbl: " + s);
        }
        //proc2014(lst, labels);
        return null;
    }

    public void proc2014(List<Map<String, Object>> ls, Set<String> labels) {
        for (Map<String, Object> o : ls) {
            System.out.println("record --");
            for (Entry<String, Object> e : o.entrySet()) {
                if (e.getValue() instanceof String) {
                    System.out.println(e.getValue().getClass());
                }
            }
            for (Entry<String, Object> e : o.entrySet()) {
                if (e.getValue() instanceof ArrayList) {
                    proc2014((List) e.getValue(), labels);
                }
            }
        }
    }
}
