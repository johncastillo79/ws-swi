/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 *
 * @author John
 */
@Entity
@Table(name = "rpi_config")
public class RpiConfig {

    @Id    
    @Column(name = "id")
    private Integer id = 1;
    @Lob
    private String jsonfields;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getJsonfields() {
        return jsonfields;
    }

    public void setJsonfields(String jsonfields) {
        this.jsonfields = jsonfields;
    }
}
