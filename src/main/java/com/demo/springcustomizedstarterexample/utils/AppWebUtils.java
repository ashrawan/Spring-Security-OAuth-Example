package com.demo.springcustomizedstarterexample.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class AppWebUtils {

    private static int cookieExpireSeconds;

    public AppWebUtils(int cookieExpireSeconds) {
        AppWebUtils.cookieExpireSeconds = cookieExpireSeconds;
    }

    // Fetches a cookie from the request
    public static Optional<Cookie> getCookie(HttpServletRequest request,
                                             String cookieKey) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieKey)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }


    // Utility for adding cookie
    public static void addCookie(HttpServletResponse response,
                                 String cookieKey,
                                 String cookieValue) {

        if (StringUtils.hasText(cookieKey) && StringUtils.hasText(cookieValue)) {
            Cookie cookie = new Cookie(cookieKey, cookieValue);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(cookieExpireSeconds);
            response.addCookie(cookie);
        }
    }

    // Utility for deleting cookie
    public static void deleteCookie(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String cookieKey) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieKey)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

}
