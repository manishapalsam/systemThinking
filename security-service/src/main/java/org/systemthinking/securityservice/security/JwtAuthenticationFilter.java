package org.systemthinking.securityservice.security;


import jakarta.servlet.FilterChain;
import  jakarta.servlet.ServletException;
import  jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private  JwtTokenUtil jwtTokenUtil;

    private static final String TOKEN_HEADER = "Authorization";

    private  static  final String TOKEN_PREFIX = "Bearer";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            String jwt = getJwtFromRequest(request);

            if(StringUtils.hasText(jwt) && jwtTokenUtil.validateToken(jwt,)){}
        }
    }



    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(TOKEN_HEADER);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)){
        return bearerToken.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
