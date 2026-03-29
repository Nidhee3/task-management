package com.tasksystemnidharshana.taskmanager.security;

import com.tasksystemnidharshana.taskmanager.entity.User;
import com.tasksystemnidharshana.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Set;
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    //loads user and helps in auth
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException("User not found with email: "+email));

        GrantedAuthority authority= new SimpleGrantedAuthority("ROLE_"+user.getRole());
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), Set.of(authority)
        );
    }
}
