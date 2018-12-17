package com.flamel.almacenutt.auth.filter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flamel.almacenutt.auth.services.JWTService;
import com.flamel.almacenutt.auth.services.JWTServiceImpl;
import com.flamel.almacenutt.models.entity.Usuario;
import com.flamel.almacenutt.models.service.UsuarioService;
import com.flamel.almacenutt.util.CustomErrorType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JWTService jwtService;
    private UsuarioService usuarioService;


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService, UsuarioService usuarioService) {
        this.authenticationManager = authenticationManager;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/v1/login", "POST"));
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);


        Usuario user = null;
        try {
            // leemos el json y lo convertimos en un objeto de java
            user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);

            username = user.getNombreUsuario();
            password = user.getPassword();


        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        username = username.trim();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        User user = ((User) authResult.getPrincipal());
        Usuario usuario = usuarioService.findByNombreUsuario(user.getUsername());
        String token = jwtService.create(authResult, usuario.getIdUsuario());
        usuario.setPassword("");
        response.addHeader(JWTServiceImpl.HEADER_STRING, JWTServiceImpl.TOKEN_PREFIX + token);
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message", String.format("Hola %s bienvenido!", user.getUsername()));
        body.put("usuario", usuario);

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {


        CustomErrorType errorType = new CustomErrorType("Username o password incorrecto", "Error en la autenticacion");

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorType.getResponse()));
        response.setStatus(401);
        response.setContentType("application/json");
    }


}
