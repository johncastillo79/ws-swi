/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 *
 * @author John
 */
@Component(value="multipartResolver")
public class UploadControl extends CommonsMultipartResolver {

    public UploadControl() {
        this.setMaxUploadSize(1000000);
    }
    
}
