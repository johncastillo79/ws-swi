/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

import bo.gob.asfi.uif.swi.dao.Dao;
import bo.gob.asfi.uif.swi.model.Bitacora;
import bo.gob.asfi.uif.swi.model.Servidor;
import bo.gob.asfi.uif.swi.model.UserService;
import com.google.gson.Gson;
import com.icg.entityclassutils.DynamicEntityMap;
import com.predic8.membrane.client.core.util.FormParamsExtractor;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wstool.creator.RequestCreator;
import com.predic8.wstool.creator.RequestTemplateCreator;
import com.predic8.wstool.creator.SOARequestCreator;
import groovy.xml.MarkupBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author John Castillo V. john.gnu@gmail.com
 */
@Controller
@Scope("session")
public class WebServiceRequestController {

    @Autowired
    Dao dao;
    private Map<String, Definitions> serverDefinitions = new HashMap<String, Definitions>();
    private String xml;

    private Definitions getDefinitios(String id) {
        Definitions defs = serverDefinitions.get(id);
        if (defs == null) {
            Servidor s = dao.get(Servidor.class, new Long(id));
            WSDLParser parser = new WSDLParser();
            defs = parser.parse(s.getWsdlurl());
            serverDefinitions.put(id, defs);
        }
        return defs;
    }

    /**
     * WebService System Request Invoca un servicio habilitado en el sistema Se
     * registra en Bitacora
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/webservicesystem", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> webServiceSystemRequest(HttpServletRequest request) {
        Map<String, Object> body = new HashMap<String, Object>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Bitacora bit = new Bitacora();
        bit.setUsuario(auth.getName());
        bit.setFecha(new Date());
        try {
            Map<String, Object> params = DynamicEntityMap.requestMapToEntityMap(request.getParameterMap());
            body.put("id", params.get("_swi_userservice_id_"));
            UserService us = dao.get(UserService.class, new Integer(params.get("_swi_userservice_id_").toString()));
            String[] routerparts = us.getRouter().split(":");
            String serverid = routerparts[0];
            bit.setServicio(us.getNombre());
            //reate body
            StringWriter writer = new StringWriter();
            //SOAPRequestCreator constructor: SOARequestCreator(Definitions, Creator, MarkupBuilder)
            SOARequestCreator creator = new SOARequestCreator(getDefinitios(serverid), new RequestTemplateCreator(), new MarkupBuilder(writer));
            creator.createRequest(routerparts[2], routerparts[3], routerparts[4]);

            FormParamsExtractor sp = new FormParamsExtractor();
            //System.out.println(sp.extract(writer.toString()));

            Map<String, String> map = sp.extract(writer.toString());

            for (Entry<String, Object> entry : params.entrySet()) {
                map.put("xpath:/" + routerparts[3] + "/" + entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }

            StringWriter writer2 = new StringWriter();
            SOARequestCreator creator2 = new SOARequestCreator(getDefinitios(serverid), new RequestCreator(), new MarkupBuilder(writer2));
            creator2.setFormParams(map);
            creator2.createRequest(routerparts[2], routerparts[3], routerparts[4]);

            //System.out.println(writer2.toString());
            bit.setRequest(writer2.toString());
            String url = us.getUrl(); //EndPoint url
            System.out.println("Operation EndPoint: " + url);
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapResponse = soapConnection.call(getSoapMessageFromString(writer2.toString()), url);


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            //System.out.println(strMsg);

            String response = strMsg;//printSOAPResponse(soapResponse);
            bit.setResponse(response);
            //System.out.println(response);
            //stringToDom(response);
            //getStringFromDocument(this.stringToDom2(response));
            response = prettyFormat(response, 2);
            //System.out.println(response);
            //response = response.replaceAll("<", "&lt;");
            //response = response.replaceAll(">", "&gt;");

            ArbolNodo an = new SOAPProcessor2().parseXML2014(response, us.getResponseXpath());

            body.put("result", new Gson().toJson(an));
            body.put("gridcfg", us.getGridCols());
            body.put("success", Boolean.TRUE);
            soapConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
            body.put("success", Boolean.FALSE);
        }
        dao.persist(bit);
        return body;
    }

    public void stringToDom(String xmlSource) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException {
        // Parse the given input
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlSource)));

        // Write the parsed document to an xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(new File("d:/tempo/my-file.xml"));
        transformer.transform(source, result);
    }

    public Document stringToDom2(String xmlSource) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xmlSource)));
    }

    public static String prettyFormat(String input, int indent) {
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); // simple exception handling, please review it
        }
    }

    //method to convert Document to String
    public String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            System.out.println("xml : \n" + writer.toString());
            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private SOAPMessage getSoapMessageFromString(String xml) throws SOAPException, IOException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
        return message;
    }

    @Deprecated
    private String printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        StringWriter writer2 = new StringWriter();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(writer2);
        transformer.transform(sourceContent, result);
        return writer2.toString();
    }

    /**
     * WebService World Request Invoca un servicio generico anonimamente
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/webserviceworld", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> /*String*/ webServiceWorldRequest(Model model, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            Map<String, Object> params = DynamicEntityMap.requestMapToEntityMap(request.getParameterMap());

            //UserService us = dao.get(UserService.class, new Integer(params.get("_swi_userservice_id_").toString()));
            String[] routerparts = params.get("__router_swi_var").toString().split(":");//us.getRouter().split(":");
            String serverid = routerparts[0];
            //reate body
            StringWriter writer = new StringWriter();
            //SOAPRequestCreator constructor: SOARequestCreator(Definitions, Creator, MarkupBuilder)
            SOARequestCreator creator = new SOARequestCreator(getDefinitios(serverid), new RequestTemplateCreator(), new MarkupBuilder(writer));
            creator.createRequest(routerparts[2], routerparts[3], routerparts[4]);

            FormParamsExtractor sp = new FormParamsExtractor();
            //System.out.println(sp.extract(writer.toString()));

            Map<String, String> map = sp.extract(writer.toString());

            for (Entry<String, Object> entry : params.entrySet()) {
                map.put("xpath:/" + routerparts[3] + "/" + entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }

            StringWriter writer2 = new StringWriter();
            SOARequestCreator creator2 = new SOARequestCreator(getDefinitios(serverid), new RequestCreator(), new MarkupBuilder(writer2));
            creator2.setFormParams(map);
            creator2.createRequest(routerparts[2], routerparts[3], routerparts[4]);

            //System.out.println(writer2.toString());

            String url = params.get("__endpoint_swi_var").toString(); //EndPoint url
            System.out.println("Operation EndPoint: " + url);
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapResponse = soapConnection.call(getSoapMessageFromString(writer2.toString()), url);

            //String response = prettyFormat(printSOAPResponse(soapResponse), 2);
            //this.xml = response;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            //String response = prettyFormat(printSOAPResponse(soapResponse), 2);
            String response = prettyFormat(strMsg, 2);
            this.xml = response;

            response = response.replaceAll("<", "&lt;");
            response = response.replaceAll(">", "&gt;");

            body.put("result", response);
            model.addAttribute("result", response);
            //body.put("id", params.get("_swi_userservice_id_"));
            body.put("success", Boolean.TRUE);
            soapConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
            body.put("success", Boolean.FALSE);
        }
        return body;
        //return "xml";
    }

    @RequestMapping(value = "/cachexml", method = RequestMethod.GET)
    public String cacheXml(Model model) {
        model.addAttribute("result", this.xml);//.substring(40, this.xml.length()));      
        return "xml";
    }

    @RequestMapping(value = "/webserviceworld1", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> /*String*/ webServiceWorldRequest1(Model model, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            Map<String, Object> params = DynamicEntityMap.requestMapToEntityMap(request.getParameterMap());

            //UserService us = dao.get(UserService.class, new Integer(params.get("_swi_userservice_id_").toString()));
            String[] routerparts = params.get("__router_swi_var").toString().split(":");//us.getRouter().split(":");
            String serverid = routerparts[0];
            //reate body
            StringWriter writer = new StringWriter();
            //SOAPRequestCreator constructor: SOARequestCreator(Definitions, Creator, MarkupBuilder)
            SOARequestCreator creator = new SOARequestCreator(getDefinitios(serverid), new RequestTemplateCreator(), new MarkupBuilder(writer));
            creator.createRequest(routerparts[2], routerparts[3], routerparts[4]);

            FormParamsExtractor sp = new FormParamsExtractor();
            //System.out.println(sp.extract(writer.toString()));

            Map<String, String> map = sp.extract(writer.toString());

            for (Entry<String, Object> entry : params.entrySet()) {
                map.put("xpath:/" + routerparts[3] + "/" + entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }

            StringWriter writer2 = new StringWriter();
            SOARequestCreator creator2 = new SOARequestCreator(getDefinitios(serverid), new RequestCreator(), new MarkupBuilder(writer2));
            creator2.setFormParams(map);
            creator2.createRequest(routerparts[2], routerparts[3], routerparts[4]);

            String url = params.get("__endpoint_swi_var").toString(); //EndPoint url
            System.out.println("Operation EndPoint: " + url);
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            SOAPMessage soapResponse = soapConnection.call(getSoapMessageFromString(writer2.toString()), url);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapResponse.writeTo(out);
            String strMsg = new String(out.toByteArray());
            //String response = prettyFormat(printSOAPResponse(soapResponse), 2);
            String response = prettyFormat(strMsg, 2);
            this.xml = response;

            ArbolNodo an = new SOAPProcessor2().parseXML2014(response, params.get("__xpath_swi_var").toString());

            body.put("result", new Gson().toJson(an));
            body.put("success", Boolean.TRUE);
            soapConnection.close();
        } catch (XPathExpressionException e) {
            body.put("success", Boolean.FALSE);
            body.put("message", "Error en XPATH");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Class:  " + e.getClass());
            body.put("success", Boolean.FALSE);
        }
        return body;
        //return "xml";
    }

    @RequestMapping(value = "/parsexml", method = RequestMethod.POST)
    public @ResponseBody
    String parseXML(@RequestParam String xml, @RequestParam String xpath) {
        try {
            SOAPProcessor2 xmlproc = new SOAPProcessor2();
            ArbolNodo an = xmlproc.parseXML2014(xml, xpath);
            return new Gson().toJson(an);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WebServiceRequestController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(WebServiceRequestController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WebServiceRequestController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WebServiceRequestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
