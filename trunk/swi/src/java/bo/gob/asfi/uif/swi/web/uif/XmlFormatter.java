/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

//import org.apache.xml.serialize.OutputFormat;
//import org.apache.xml.serialize.XMLSerializer;
//import org.w3c.dom.Document;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.io.Writer;

/**
 * Pretty-prints xml, supplied as a string.
 * <p/>
 * eg.
 * <code>
 * String formattedXml = new XmlFormatter().format("<tag><nested>hello</nested></tag>");
 * </code>
 */
public class XmlFormatter {

//    public XmlFormatter() {
//    }
//
//    public String format(String unformattedXml) {
//        try {
//            final Document document = parseXmlFile(unformattedXml);
//
//            OutputFormat format = new OutputFormat(document);
//            format.setLineWidth(65);
//            format.setIndenting(true);
//            format.setIndent(2);
//            Writer out = new StringWriter();
//            XMLSerializer serializer = new XMLSerializer(out, format);
//            serializer.serialize(document);
//
//            return out.toString();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Document parseXmlFile(String in) {
//        try {
//            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            InputSource is = new InputSource(new StringReader(in));
//            return db.parse(is);
//        } catch (ParserConfigurationException e) {
//            throw new RuntimeException(e);
//        } catch (SAXException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static void main(String[] args) {
//        String unformattedXml =
//                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:helloResponse xmlns:ns2=\"http://newpackage/\"><return>Hello dsfsff !</return></ns2:helloResponse></S:Body></S:Envelope>";
//
//        System.out.println(new XmlFormatter().format(unformattedXml));
//    }
}
