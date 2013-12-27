/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

import bo.gob.asfi.uif.swi.dao.Dao;
import bo.gob.asfi.uif.swi.model.FieldSet;
import bo.gob.asfi.uif.swi.model.FormField;
import bo.gob.asfi.uif.swi.model.Parametro;
import bo.gob.asfi.uif.swi.model.RpiConfig;
import bo.gob.asfi.uif.swi.model.RpiField;
import bo.gob.asfi.uif.swi.model.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.heyma.core.extjs.components.Button;
import org.heyma.core.extjs.components.ExtJSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author John
 */
@Controller
@RequestMapping(value = "/rpi")
public class RpiController {
    
    @Autowired
    Dao dao;
    
    @RequestMapping(value = "/rpi")
    public String rpi() {
        return "servicios/rpi";
    }
    
    @RequestMapping(value = "/setserviceenabled", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> setServiceEnabled(@RequestParam Integer id) {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            UserService us = dao.get(UserService.class, id);
            us.setRpiEnable(!us.getRpiEnable());
            dao.update(us);
            
            setRpiformFieldsConfig();
            
            body.put("success", true);
        } catch (Exception e) {
        }
        body.put("success", false);
        return body;
    }
    
    private void setRpiformFieldsConfig() {
        RpiConfig rpi = dao.get(RpiConfig.class, 1);
        if (rpi == null) {
            rpi = new RpiConfig();
            rpi.setJsonfields(new Gson().toJson(this.rpiFormFieldsFromServices()));
            dao.persist(rpi);
        } else {
            rpi.setJsonfields(new Gson().toJson(this.rpiFormFieldsFromServices()));
            dao.update(rpi);
        }
        System.out.println(rpi.getJsonfields());
    }

    /**
     * rpiFormFieldsFromServices
     *
     * @return RPI Fields
     */
    private Collection<FormField> rpiFormFieldsFromServices() {
        List<UserService> ls = dao.findAllServices();
        Collection<Parametro> parametros = new ArrayList<Parametro>();
        for (UserService us : ls) {
            if (us.getRpiEnable()) {
                parametros.addAll(us.getParametros());
            }
        }
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
//        RpiConfig rc = dao.get(RpiConfig.class, 1);
//        if(rc == null) {
//            dao.persist(new RpiConfig());
//        } else {
//            dao.update(lst);
//        }
        return lst.values();
    }
    
    @RequestMapping(value = "/formserviceitems")
    public @ResponseBody
    List<FieldSet> formServiceItems() {
        List<UserService> lst = dao.findAllServices();//findAll(UserService.class);

        return requestFormFiends(lst);
    }
    
    @RequestMapping(value = "/guardarcampo", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> guardarCampo(RpiField field) {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            dao.persist(field);
            //System.out.println(srvfields);
            String[] srvParams = field.getServiceParamsIds().split(":");//srvfields.split(":");
            for (String sp : srvParams) {
                Parametro p = dao.get(Parametro.class, new Integer(sp));
                p.setRpifield(field.getId());
                dao.update(p);
            }
            setRpiformFieldsConfig();
            body.put("success", true);
        } catch (Exception e) {
        }
        body.put("success", false);
        return body;
    }
    
    @RequestMapping(value = "/eliminarcampo", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> eliminarCampo(@RequestParam Integer id) {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            RpiField rf = dao.get(RpiField.class, id);
            for (String sp : rf.getServiceParamsIds().split(":")) {
                Parametro p = dao.get(Parametro.class, new Integer(sp));
                p.setRpifield(null);
                dao.update(p);
            }
            dao.remove(rf);
            setRpiformFieldsConfig();
            body.put("success", true);
        } catch (Exception e) {
        }
        body.put("success", false);
        return body;
    }
    
    private List<FieldSet> requestFormFiends(List<UserService> services) {
        List<FieldSet> list = new ArrayList<FieldSet>();
        for (UserService us : services) {
            FieldSet fs = new FieldSet();
            fs.setTitle(us.getNombre());
            fs.setId(us.getId().toString());
            //fs.setCheckboxToggle(true);
            //fs.setCollapsed(!us.getRpiEnable());
            if (us.getRpiEnable()) {
                fs.getButtons().add(new Button("Deshabilitar", "", "delete", 100));
            } else {
                fs.getButtons().add(new Button("Habilitar", "", "create", 100));
            }
            for (Parametro pm : us.getParametros()) {
                FormField ff = new FormField();
                ff.setFieldLabel(pm.getEtiqueta());
                ff.setXtype(ExtJSUtils.attributetypeTOExtJSType(pm.getTipo()));
                ff.setValue(pm.getValordefecto());
                ff.setAllowBlank(!pm.getRequerido());
                fs.getItems().add(ff);
            }
            list.add(fs);
        }
        return list;
    }
    
    @RequestMapping(value = "/formrpiitems")
    public @ResponseBody
    List<FormField> formRpiItems() {
        List<RpiField> fields = dao.findAll(RpiField.class);
        
        return RpiController.rpiFormFiends(fields);
    }
    
    public static List<FormField> rpiFormFiends(List<RpiField> fields) {
        List<FormField> list = new ArrayList<FormField>();
        for (RpiField pm : fields) {
            FormField ff = new FormField();
            ff.setFieldLabel(pm.getEtiqueta());
            ff.setXtype(pm.getTipo());
            ff.setValue(pm.getValordefecto());
            ff.setAllowBlank(!pm.getRequerido());
            ff.setId(pm.getId() + ":rpifield");
            list.add(ff);
        }
        return list;
    }
    
    @RequestMapping(value = "/movefield", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, ? extends Object> moveField(@RequestParam String name, @RequestParam String dir) {
        Map<String, Object> body = new HashMap<String, Object>();
        try {
            RpiConfig rc = dao.get(RpiConfig.class, 1);
            Type listType = new TypeToken<ArrayList<FormField>>() {
            }.getType();
            List<FormField> ls = new Gson().fromJson(rc.getJsonfields(), listType);
            
            int index = this.indexOfField(ls, name);
            if (dir.equals("up")) {
                Collections.swap(ls, index - 1, index);
            }
            
            if (dir.equals("down")) {
                Collections.swap(ls, index, index + 1);
            }
            
            rc.setJsonfields(new Gson().toJson(ls));
            
            dao.update(rc);
            body.put("success", true);
        } catch (Exception e) {
        }
        body.put("success", false);
        return body;
    }
    
    private int indexOfField(List<FormField> ls, String naem) {
        int i = 0;
        for (FormField a : ls) {
            if (a.getName().equals(naem)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
