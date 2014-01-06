/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

import bo.gob.asfi.uif.swi.dao.Dao;
import bo.gob.asfi.uif.swi.model.EntitySearch;
import bo.gob.asfi.uif.swi.model.FormField;
import bo.gob.asfi.uif.swi.model.Parametro;
import bo.gob.asfi.uif.swi.model.RpiConfig;
import bo.gob.asfi.uif.swi.model.RpiField;
import bo.gob.asfi.uif.swi.model.RpiResultado;
import bo.gob.asfi.uif.swi.model.UserService;
import bo.gob.asfi.uif.swi.model.Usuario;
import bo.gob.asfi.uif.swi.security.CustomUserDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.heyma.core.extjs.components.ExtJSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author John
 */
@Controller
@RequestMapping(value = "/rpiview")
public class RpiViewController {

    @Autowired
    Dao dao;
    private Collection<FormField> rpifields;

    @RequestMapping(value = "/rpiview")
    public String rpiView() {
        return "servicios/rpiview";
    }

    @RequestMapping(value = "/formrpiitems")
    public @ResponseBody
    Collection<FormField> formRpiItems() {
        List<UserService> lst = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
            if (ud.getRole().equals("admin_uif")) {
                return allFormRpiItems();//lst = dao.findAllServices();//findAll(UserService.class);
            } else {
                lst = dao.getUserServices(ud.getId());
            }
        } else {
            return allFormRpiItems();//lst = dao.findAllServices();//findAll(UserService.class);
        }

        return userFormRpiFields(lst);
    }

    private Collection<FormField> userFormRpiFields(List<UserService> lst) {

        Collection<Parametro> parametros = new ArrayList<Parametro>();
        for (UserService us : lst) {
            if (us.getRpiEnable()) {
                parametros.addAll(us.getParametros());
            }
        }
        Collection<FormField> userRpifields = rpiFormFiendsFromParams(parametros);

        Collection<FormField> ls = new ArrayList<FormField>();
        Collection<FormField> ls1 = allFormRpiItems();
        for (FormField ff : ls1) {
            for (FormField uf : userRpifields) {
                if (ff.getName().equals(uf.getName())) {
                    ls.add(ff);
                }
            }
        }
        return ls;
    }

    private Collection<FormField> allFormRpiItems() {
        RpiConfig rc = dao.get(RpiConfig.class, 1);
        if (rc != null) {
            Type listType = new TypeToken<ArrayList<FormField>>() {
            }.getType();
            return new Gson().fromJson(rc.getJsonfields(), listType);
        } else {
            return new ArrayList<FormField>();
        }
    }

    private Collection<FormField> rpiFormFiendsFromParams(Collection<Parametro> parametros) {
        //Set<FormField> list = new HashSet<FormField>();
        Map<String, FormField> lst = new HashMap<String, FormField>();
        for (Parametro pm : parametros) {
            if (pm.getRpifield() == null) {
                FormField ff = new FormField();
                ff.setFieldLabel(pm.getEtiqueta());
                ff.setXtype(ExtJSUtils.attributetypeTOExtJSType(pm.getTipo()));
                ff.setValue(pm.getValordefecto());
                ff.setAllowBlank(!pm.getRequerido());
                ff.setName(pm.getServicio().getId() + ":" + pm.getNombre());
                lst.put(ff.getName(), ff);
            } else {
                RpiField rf = dao.get(RpiField.class, pm.getRpifield());
                FormField ff = new FormField();
                ff.setFieldLabel(rf.getEtiqueta());
                ff.setXtype(rf.getTipo());
                ff.setValue(rf.getValordefecto());
                ff.setAllowBlank(!rf.getRequerido());
                ff.setName("rpifield-" + rf.getId());
                lst.put(ff.getName(), ff);
            }
        }
        return lst.values();
    }

    @RequestMapping(value = "/listaserviciosusuario", method = RequestMethod.GET)
    public @ResponseBody
    List<UserService> listaServiciosUsuario() {

        List<UserService> lst = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
            if (ud.getRole().equals("admin_uif")) {
                lst = dao.findAllServices();//findAll(UserService.class);
            } else {
                lst = dao.getUserServices(ud.getId());
            }
        } else {
            lst = dao.findAllServices();//findAll(UserService.class);
        }

        List<UserService> lst2 = new ArrayList<UserService>();
        for (UserService us : lst) {
            if (us.getRpiEnable()) {
                us.setParametros(listarParametros(us.getId()));
                lst2.add(us);
            }
        }

        return lst2;
    }

    private Collection<Parametro> listarParametros(@RequestParam Integer servicio_id) {
        Collection<Parametro> lst = dao.get(UserService.class, servicio_id).getParametros();
        for (Parametro pm : lst) {
            pm.setServicio(null);
        }
        return lst;
    }

    @RequestMapping(value = "/guardarcomorpi", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> guardarComoRpi(RpiResultado rpi) {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            rpi.setUsuario(auth.getName());
            rpi.setFecha(new Date());
            dao.persist(rpi);
            body.put("success", true);
        } catch (Exception e) {
            body.put("success", false);
        }
        return body;
    }
    
    @RequestMapping(value = "/guardarrpi", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> guardarRpi(RpiResultado rpi) {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            RpiResultado erpi = dao.get(RpiResultado.class, rpi.getId());
            erpi.setUsuario(auth.getName());
            erpi.setFechaupdate(new Date());
            erpi.setEntrada(rpi.getEntrada());
            erpi.setSalida(rpi.getSalida());
            dao.update(rpi);
            body.put("success", true);
        } catch (Exception e) {
            body.put("success", false);
        }
        return body;
    }

    @RequestMapping(value = "/listarpis", method = RequestMethod.POST)
    public @ResponseBody
    List<RpiResultado> listaRpiS(EntitySearch es) {
        return dao.getRpisGuardados(es.getUsuario(), es.getFecha1(), es.getFecha2());
    }

    @RequestMapping(value = "/pri/{id}", method = RequestMethod.GET)
    public @ResponseBody
    RpiResultado getRpi(@PathVariable Integer id) {
        return dao.get(RpiResultado.class, id);
    }

    @RequestMapping(value = "/listarusuarios", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, ? extends Object> listarUsuarios() {
        Map<String, Object> body = new HashMap<String, Object>();
        //List<UserService> lst = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();
            if (ud.getPlus()) {
                List<Usuario> lst = dao.findAll(Usuario.class);
                List<Usuario> lst2 = new ArrayList<Usuario>();
                for (Usuario u : lst) {
                    if (u.getRol().equals("usuario_uif")) {
                        u.setServicios(null);
                        lst2.add(u);
                    }
                }
                body.put("data", lst2);
            } else {
                List<Usuario> lst = new ArrayList<Usuario>();
                Usuario u = new Usuario();
                u.setUsuario(ud.getUsername());
                u.setId(ud.getId());
                lst.add(u);
                body.put("data", lst);
            }
        } else {
            List<Usuario> lst = dao.findAll(Usuario.class);
            List<Usuario> lst2 = new ArrayList<Usuario>();
            for (Usuario u : lst) {
                if (u.getRol().equals("usuario_uif")) {
                    u.setServicios(null);
                    lst2.add(u);
                }
            }
            body.put("data", lst2);
        }
        return body;
    }
}
