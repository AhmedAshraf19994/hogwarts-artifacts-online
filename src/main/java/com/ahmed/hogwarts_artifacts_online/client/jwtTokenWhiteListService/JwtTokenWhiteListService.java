package com.ahmed.hogwarts_artifacts_online.client.jwtTokenWhiteListService;

import java.util.concurrent.TimeUnit;

public interface JwtTokenWhiteListService {

    void addToken (int userId, String token, long timeout, TimeUnit timeUnit);

    String getToken (int userId);

    void removeToken (int userId);

    boolean isTokenValid (int userId, String tokenFromUser);
}
