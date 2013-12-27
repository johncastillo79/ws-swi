/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.web.uif;

import org.springframework.util.StringUtils;

/**
 *
 * @author John
 */
public class StringProcesor {
    public static void main(String argv[]) {
        String a = "1#/Artist/Name/Mr Guy";
        String b = "Artist/Name";
        System.out.println(a.substring(a.indexOf(b) + b.length() + 1));
    }
}
