package bo.gob.asfi.uif.swi.web.uif;

import bo.gob.asfi.uif.swi.dao.Dao;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author John Castillo Valencia
 */
@Controller
public class UIFController {

    @Autowired
    Dao dao;

    @RequestMapping(value = "/main")
    public String portal() {
        return "main";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, ? extends Object> getLogin(@RequestParam(value = "error", required = false) boolean error) {

        Map<String, Object> data = new HashMap<String, Object>();
        if (error == true) {
            data.put("success", Boolean.FALSE);
            data.put("reason", "Fallo el ingreso.. intente nuevamente");
        } else {
            data.put("success", Boolean.TRUE);
        }
        return data;
    }
}
