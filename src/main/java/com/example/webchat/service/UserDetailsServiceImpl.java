package com.example.webchat.service;

import com.example.webchat.exception.UserBlockedException;
import com.example.webchat.model.User;
import com.example.webchat.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    private final CacheManager cacheManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Searching user by username: " + username);
        Cache cache = cacheManager.getCache("loginAttempts");
        if (cache.get(username + "_blocked") != null) {
            throw new UserBlockedException("User blocked");
        }

        User user = userRepository.findByUsername(username);
        log.debug("User found: " + user.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

}
