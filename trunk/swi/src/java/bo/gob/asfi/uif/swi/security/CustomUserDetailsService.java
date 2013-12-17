/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bo.gob.asfi.uif.swi.security;

import bo.gob.asfi.uif.swi.dao.Dao;
import bo.gob.asfi.uif.swi.model.Usuario;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

/**
 *
 * @author John Caastillo Valencia
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    Dao dao;

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("user to load swi:" + username);
        Usuario user = null;
        if (username.equals("admin")) {
            ApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
            user = (Usuario) ctx.getBean("defaultuser");
        } else {
            user = dao.getUsuarioByUsername(username);
        }

        if (user == null) {
            System.out.println("user not found");
            throw new UsernameNotFoundException("user not found");
        }

        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        CustomUserDetails userDetail = new CustomUserDetails(
                username,
                user.getClave(),
                user.getActivo(),
                accountNonExpired,
                accountNonLocked,
                credentialsNonExpired,
                getRoles(user.getRol()));

        userDetail.setNombre(user.getNombres());
        userDetail.setApellido(user.getPaterno());
        userDetail.setRole(user.getRol());
        userDetail.setId(user.getId());
        return userDetail;
    }

    private Set<String> getRoles(String role) {
        Set<String> roles = new HashSet<String>();
        roles.add(role);
        return roles;
    }
}
