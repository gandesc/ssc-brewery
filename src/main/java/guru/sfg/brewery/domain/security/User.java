package guru.sfg.brewery.domain.security;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String password;
    private String username;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name="user_authority",
        joinColumns = {@JoinColumn(name="USER_ID", referencedColumnName = "ID")},
        inverseJoinColumns = {@JoinColumn(name="AUTHORITY_ID", referencedColumnName = "ID")})
    private Set<Authority> authorities;

    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;


}
