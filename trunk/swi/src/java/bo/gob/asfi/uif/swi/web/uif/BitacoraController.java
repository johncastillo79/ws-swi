/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

import bo.gob.asfi.uif.swi.dao.Dao;
import bo.gob.asfi.uif.swi.model.Bitacora;
import bo.gob.asfi.uif.swi.model.UserService;
import bo.gob.asfi.uif.swi.model.Usuario;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author John
 */
@Controller
public class BitacoraController {
static int ARRAY_SIZE = 16384;
    static int BUFFER_SIZE = 1024;
    @Autowired
    Dao dao;

    @RequestMapping(value = "/busquedaporusuario")
    public String busquedaPorUsuario() {
        return "bitacora/busquedaporusuario";
    }

    @RequestMapping(value = "/busqueda1")
    public @ResponseBody
    Map<String, ? extends Object> busqueda1(String usuario, String servicio, Date fechai, Date fechaf) {
        if (usuario != null && servicio != null) {
            try {
                Map<String, Object> body = new HashMap<String, Object>();
                //SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");  
                //formatter.format(fechai)
                //List<Bitacora> lst = dao.getBitacoraBusqueda1(usuario, servicio,  formatter.parse(fechai.toString()), formatter.parse(fechaf.toString()));
                List<Bitacora> lst = dao.getBitacoraBusqueda1(usuario, servicio, fechai, fechaf);

                body.put("data", lst);
                return body;
            } catch (Exception e) {
                e.printStackTrace();
                //java.util.logging.Logger.getLogger(IndividualController.class.getName()).log(Level.SEVERE, null, e);

                return null;
            }
        }
        return null;
    }
    /*    @RequestMapping(value = "/listar_usuarios")
     public @ResponseBody
     Map<String, ? extends Object> listarUsuarios() {
     Map<String, Object> body = new HashMap<String, Object>();
     List<Usuario> lst = dao.find(Usuario.class);
     for(Usuario u:lst){
     u.setServicios(null);
     }
     body.put("data", lst);
     return body;
     }*/

    @RequestMapping(value = "/listar_bitacoras")
    public @ResponseBody
    Map<String, ? extends Object> listarBitacoras() {
        Map<String, Object> body = new HashMap<String, Object>();
        List<Bitacora> lst = dao.find(Bitacora.class);
        body.put("data", lst);
        return body;
    }

    @RequestMapping(value = "/listar_usuarios")
    public @ResponseBody
    Map<String, ? extends Object> listarUsuarios() {
        Map<String, Object> body = new HashMap<String, Object>();
        List<Usuario> lst = dao.find(Usuario.class);
        for (Usuario u : lst) {
            u.setServicios(null);
        }
        body.put("data", lst);
        return body;
    }

//    @RequestMapping(value = "/listar_servicios")
//    public @ResponseBody
//    List<UserService> listar_servicios() {
//        try {
//            Map params = new HashMap();
//            List<UserService> servicios = dao.find(UserService.class);
//            return servicios;
//        } catch (Exception e) {
//            e.printStackTrace();
//            java.util.logging.Logger.getLogger(IndividualController.class.getName()).log(Level.SEVERE, null, e);
//
//            return null;
//        }
//    }

    @RequestMapping(value = "/listar_serviciosporusuario")
    public @ResponseBody

    List<UserService> listar_serviciosporusuario(@RequestParam Integer id) {
        try {
            Map params = new HashMap();
            List<UserService> servicios = dao.getUserServices(id);
            return servicios;
        } catch (Exception e) {
            e.printStackTrace();
            java.util.logging.Logger.getLogger(AsignacionserviciosController.class.getName()).log(Level.SEVERE, null, e);

            return null;
        }

    }

    @RequestMapping(value = "/exportdatosentrada")
    public @ResponseBody

    List<UserService> exportDatosEntrada(@RequestParam String xml) {
        try {
            PrintWriter out = new PrintWriter("c:\\datos\\datosentrada.xml");
            out.println(xml);
            out.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            //java.util.logging.Logger.getLogger(AsignacionserviciosController.class.getName()).log(Level.SEVERE, null, e);

            return null;
        }

    }

    @RequestMapping(value = "/exportdatosalida")
    public @ResponseBody

    List<UserService> exportDatosSalida(@RequestParam String xml) {
        try {
            PrintWriter out = new PrintWriter("c:\\datos\\datossalida.xml");
            out.println(xml);
            out.close();

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            //java.util.logging.Logger.getLogger(AsignacionserviciosController.class.getName()).log(Level.SEVERE, null, e);

            return null;
        }

    }
     @RequestMapping(value = "/downloadStream")
    public @ResponseBody
    void downloadStream(String xmlStr, String filename, HttpServletResponse resp) {
        
         try {
/*
            resp.setContentType("application/x-download");
            resp.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");

            PrintWriter out = resp.getWriter();
            out.println(xmlStr);
            */
        /*    resp.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.write(okResponse);
        out.flush();
        out.close();
        response.flushBuffer();
*/
            resp.setContentType("application/octet-strem");
        resp.setHeader("Content-Disposition", "attachment;filename='" + filename + "'");

       
        //int contentLength = connection.getContentLength();
        PrintWriter out = resp.getWriter();

        out.write(xmlStr);
        out.close();

        } catch (IOException e) {
  e.printStackTrace();
        }
    }
}
