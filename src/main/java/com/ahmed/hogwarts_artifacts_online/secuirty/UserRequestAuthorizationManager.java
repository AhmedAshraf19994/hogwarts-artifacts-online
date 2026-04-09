package com.ahmed.hogwarts_artifacts_online.secuirty;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class UserRequestAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final UriTemplate USER_URI_TEMPLATE = new UriTemplate("/users/{userId}");

    @Override
    public @Nullable AuthorizationResult authorize(Supplier<? extends @Nullable Authentication> authsupplier, RequestAuthorizationContext context) {
        // extract the user id from  the request
        assert context != null;
        Map<String, String> values = USER_URI_TEMPLATE.match(context.getRequest().getRequestURI());
        String userId = values.get("userId");

        // extract the user id from the auth object which is jwt object
        Authentication authentication = authsupplier.get();
        String userIdFromJwt = ((Jwt)authentication.getPrincipal()).getClaim("userId").toString();

        //check if he has role admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        //check if he has role user
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

        //check if he userId match
        boolean matchedId =userIdFromJwt != null && userIdFromJwt.equals(userId);

        return new AuthorizationDecision(isAdmin || isUser && matchedId);
    }

}

