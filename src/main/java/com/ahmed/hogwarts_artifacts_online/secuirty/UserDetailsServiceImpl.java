package com.ahmed.hogwarts_artifacts_online.secuirty;

import com.ahmed.hogwarts_artifacts_online.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username)//fetching the user from database
                .map(MyUserPrincipal::new)//convert it to userdetails object for spring secuirty
                .orElseThrow(() -> new UsernameNotFoundException("username does not exist")); //throwing error if user doesn't exist

    }
}
