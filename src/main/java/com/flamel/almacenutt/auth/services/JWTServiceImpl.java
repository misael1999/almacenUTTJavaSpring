package com.flamel.almacenutt.auth.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flamel.almacenutt.auth.SimpleGrantedAuthorityMixin;
import com.flamel.almacenutt.auth.filter.JWTAuthenticationFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Component
public class JWTServiceImpl implements JWTService {

    public static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public static final long EXPIRATION_DATE = 3600000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    @Override
    public String create(Authentication authResult, Long idUsuario) throws IOException {
        User user = ((User) authResult.getPrincipal());
        Claims claims = Jwts.claims();
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
        claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
        claims.put("id", idUsuario);
//        String secretKeyString = new String(SECRET_KEY.getEncoded(), StandardCharsets.UTF_16);
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .signWith(JWTServiceImpl.SECRET_KEY)
                .setIssuedAt(new Date()) //  fecha de creacion
                .setExpiration(new Date(System.currentTimeMillis() + JWTServiceImpl.EXPIRATION_DATE))
                .compact();

        return token;
    }

    @Override
    public boolean validate(String token) {

        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Claims getClaims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(JWTServiceImpl.SECRET_KEY)
                .parseClaimsJws(resolve(token))
                .getBody();
        return claims;
    }

    @Override
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
        Object roles = getClaims(token).get("authorities");
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper()
                .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
                .readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));

        return authorities;
    }

    @Override
    public String resolve(String token) {
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.replace(TOKEN_PREFIX, "");
        }
        return null;
    }
}
