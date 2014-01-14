/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author John
 */
public class ArbolNodo {

    private ArbolNodo parent;
    private String name;
    private Boolean leaf;
    private Map<String, String> attributes;
    private List<ArbolNodo> children;

    public ArbolNodo(String name) {
        this.name = name;
        this.leaf = true;
    }

    public void addAttribute(String key, String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, String>();            
        }
        this.attributes.put(key, value);
    }

    public ArbolNodo getParent() {
        return parent;
    }

    public void addChildren(ArbolNodo an) {
        if (this.children == null) {
            this.children = new ArrayList<ArbolNodo>();
            this.leaf = false;
        }
        this.children.add(an);
    }

    public void setParent(ArbolNodo parent) {
        this.parent = parent;
    }

    public Map<String, String> getValues() {
        return attributes;
    }

    public void setValues(Map<String, String> values) {
        this.attributes = values;
    }

    public List<ArbolNodo> getChildren() {
        return children;
    }

    public void setChildren(List<ArbolNodo> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getLeaf() {
        return this.children == null ? true : false;
    }
}
