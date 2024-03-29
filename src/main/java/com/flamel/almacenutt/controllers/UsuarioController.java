package com.flamel.almacenutt.controllers;


import com.flamel.almacenutt.models.entity.Area;
import com.flamel.almacenutt.models.entity.Privilegio;
import com.flamel.almacenutt.models.entity.PrivilegioUsuario;
import com.flamel.almacenutt.models.entity.Usuario;
import com.flamel.almacenutt.models.service.UsuarioService;
import com.flamel.almacenutt.util.ChangePassword;
import com.flamel.almacenutt.util.CustomErrorType;
import com.flamel.almacenutt.util.CustomResponseType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/v1")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @RequestMapping(value = "/usuarios", method = RequestMethod.GET)
    public ResponseEntity<?> getUsuarios() {
        Map<String, Object> response = new HashMap<>();
        response.put("usuarios", usuarioService.findAllUsuarios());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/usuarios", method = RequestMethod.POST)
    public ResponseEntity<?> createUsuario(@RequestBody() Usuario usuario, Authentication authentication) {

        Usuario usuarioAuth = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegioS = false;
        for (PrivilegioUsuario privilegioUsuario : usuarioAuth.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("agregar usuarios")) {
                privilegioS = true;
                break;
            }
        }
        if (!privilegioS) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }

        if (usuario.getNombreUsuario().isEmpty() || usuario.getPassword().isEmpty()
                || usuario.getRole().isEmpty() || usuario.getNombre().isEmpty()
                || usuario.getApellidoMaterno().isEmpty() || usuario.getApellidoPaterno().isEmpty()) {
            return new ResponseEntity<>(new CustomErrorType("Ingresa todos los campos",
                    "No has ingresado todos los campos").getResponse(), HttpStatus.CONFLICT);
        }


        Usuario usuarioAux = usuarioService.findByNombreUsuario(usuario.getNombreUsuario());
        if (usuarioAux != null) {
            return new ResponseEntity<>(new CustomErrorType("El usuario con el nombre " + usuario.getNombreUsuario() + "ya existe",
                    "El usuario ya existe").getResponse(), HttpStatus.CONFLICT);
        }

        if (usuario.getRole().equals("ROLE_ADMIN")) {
            PrivilegioUsuario privilegioUsuario = null;
            List<Privilegio> privilegios = usuarioService.getPrivilegios();
            for (Privilegio privilegio : privilegios) {
                privilegioUsuario = new PrivilegioUsuario();
                privilegioUsuario.setPrivilegio(privilegio);
                usuario.addPrivilegio(privilegioUsuario);
            }
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        try {
            usuarioService.saveUsuario(usuario);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al guardar el usuario, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(new CustomResponseType("Usuario creado correctamente", "", "", "Usuario creado").getResponse(), HttpStatus.CREATED);

    }

    // ACTUALIZAR USUARIO
    @RequestMapping(value = "/usuarios", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateUsuario(@RequestBody() Usuario usuario, Authentication authentication) {

        Usuario usuario1 = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario1.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("actualizar usuarios")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }
        try {
            usuarioService.saveUsuario(usuario);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al actualizar el usuario, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }

        if (!usuario.getStatus()) {
            return new ResponseEntity<>(new CustomResponseType("Se ha eliminado el usuario " + usuario.getNombreUsuario(),
                    "", "",
                    "Usuario eliminado").getResponse(), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponseType("Se actualizo el usuario " + usuario.getNombreUsuario(),
                "", "",
                "Se actualizo el usuario").getResponse(), HttpStatus.OK);

    }

    // OBTENER USUARIO POR NOMBRE_USUARIO
    @RequestMapping(value = "/usuarios/{nombreUsuario}", method = RequestMethod.GET)
    public ResponseEntity<?> getUsuarioByNombreUsuario(@PathVariable("nombreUsuario") String nombreUsuario) {
        return new ResponseEntity<>(new CustomResponseType("Usuario", "usuario",
                usuarioService.findByNombreUsuario(nombreUsuario), "").getResponse(),
                HttpStatus.OK);
    }


    @RequestMapping(value = "/usuarios/password", method = RequestMethod.PATCH)
    public ResponseEntity<?> cambiarPassword(@RequestBody ChangePassword changePassword, Authentication authentication) {

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("cambiar de contrasena")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }


        if (usuario == null) {
            return new ResponseEntity<>(new CustomErrorType("No existe el usuario", "El usuario no existe").getResponse(), HttpStatus.CONFLICT);
        }

        if (!passwordEncoder.matches(changePassword.getPasswordAnterior(), usuario.getPassword())) {
            return new ResponseEntity<>(new CustomErrorType("La contraseña anterior no es correcta", "La contraseña anterior no es correcta").getResponse(), HttpStatus.CONFLICT);
        }

        usuario.setPassword(passwordEncoder.encode(changePassword.getPasswordNueva()));
        try {
            usuarioService.saveUsuario(usuario);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al cambiar la contraseña, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(new CustomResponseType("Se ha cambiado la contraseña", "", "", "Se cambio la contraseña").getResponse(), HttpStatus.OK);

    }

    // AREAS
    @RequestMapping(value = "/areas", method = RequestMethod.GET)
    public ResponseEntity<?> getAreas() {
        return new ResponseEntity<>(new CustomResponseType("Lista de areas", "areas", usuarioService.findAllAreas(), "Areas encontradas").getResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/areas", method = RequestMethod.POST)
    public ResponseEntity<?> crearArea(@RequestBody() Area area, Authentication authentication) {

        Area areaAux = usuarioService.getAreaNombre(area.getNombre());
        if (areaAux != null && areaAux.getStatus()) {
            return new ResponseEntity<>(new CustomErrorType("El area con el nombre " + area.getNombre() + " ya existe",
                    "El area ya existe").getResponse(), HttpStatus.CONFLICT);
        } else if (areaAux != null && !areaAux.getStatus()) {
            Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
            areaAux.setResponsable(area.getResponsable());
            areaAux.setIdUsuario(usuario.getIdUsuario());
            areaAux.setStatus(true);
            usuarioService.saveArea(areaAux);
            return new ResponseEntity<>(new CustomResponseType("Area creada correctamente", "", "", "Area creada").getResponse(), HttpStatus.CREATED);
        }

        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        area.setIdUsuario(usuario.getIdUsuario());
        usuarioService.saveArea(area);
        return new ResponseEntity<>(new CustomResponseType("Area creada correctamente", "", "", "Area creada").getResponse(), HttpStatus.CREATED);
    }

    // ACTUALIZAR AREA
    @RequestMapping(value = "/areas", method = RequestMethod.PATCH)
    public ResponseEntity<?> updateAreas(@RequestBody() Area area, Authentication authentication) {


        Usuario usuario = usuarioService.findByNombreUsuario(authentication.getName());
        Boolean privilegio = false;
        for (PrivilegioUsuario privilegioUsuario : usuario.getPrivilegios()) {
            if (privilegioUsuario.getPrivilegio().getNombre().equals("actualizar areas")) {
                privilegio = true;
                break;
            }
        }
        if (!privilegio) {
            return new ResponseEntity<>(new CustomErrorType("No tienes permisos para esta accion", "No has ingresado ni un producto").getResponse(), HttpStatus.CONFLICT);
        }

        area.setIdUsuario(usuario.getIdUsuario());
        try {
            usuarioService.saveArea(area);
        } catch (DataAccessException e) {
            return new ResponseEntity<>(new CustomErrorType("Ocurrio un error al actualizar el area, contacte con el administrador del sistema ",
                    "Error").getResponse(),
                    HttpStatus.CONFLICT);
        }

        if (!area.getStatus()) {
            return new ResponseEntity<>(new CustomResponseType("Se ha eliminado la area " + area.getNombre(),
                    "", "",
                    "Area eliminado").getResponse(), HttpStatus.OK);
        }

        return new ResponseEntity<>(new CustomResponseType("Se actualizo la area " + area.getNombre(),
                "", "",
                "Se actualizo la area").getResponse(), HttpStatus.OK);

    }

    // OBTENER PRIVILEGIOS DISPONIBLES
    @RequestMapping(value = "/privilegios", method = RequestMethod.GET)
    public ResponseEntity<?> getPrivilegios() {
        return new ResponseEntity<>(new CustomResponseType("Privilegios", "privilegios",
                usuarioService.getPrivilegios(), "").getResponse(),
                HttpStatus.OK);
    }


}


