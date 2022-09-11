package com.jwt.crud.security.controller;

import com.jwt.crud.dto.Message;
import com.jwt.crud.security.dto.JwtDto;
import com.jwt.crud.security.dto.LoginUser;
import com.jwt.crud.security.dto.NewUser;
import com.jwt.crud.security.entity.Role;
import com.jwt.crud.security.entity.User;
import com.jwt.crud.security.enums.RoleName;
import com.jwt.crud.security.jwt.JwtProvider;
import com.jwt.crud.security.service.RoleService;
import com.jwt.crud.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/new")
    public ResponseEntity<?> new_user(@Valid @RequestBody NewUser newUser, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Message("campos mal puestos o email inválido"), HttpStatus.BAD_REQUEST);
        if(userService.existsByNameUser(newUser.getNameUser()))
            return new ResponseEntity(new Message("ese nombre ya existe"), HttpStatus.BAD_REQUEST);
        if(userService.existsByEmail(newUser.getEmail()))
            return new ResponseEntity(new Message("ese email ya existe"), HttpStatus.BAD_REQUEST);
        User user =
                new User(newUser.getName(), newUser.getNameUser(), newUser.getEmail(),
                        passwordEncoder.encode(newUser.getPassword()));
        Set<Role> role = new HashSet<>();
        role.add(roleService.getByRoleName(RoleName.ROLE_USER).get());
        if(newUser.getRole().contains("admin"))
            role.add(roleService.getByRoleName(RoleName.ROLE_ADMIN).get());
        user.setRole(role);
        userService.save(user);
        return new ResponseEntity(new Message("usuario guardado"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUser loginUser, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Message("campos mal puestos"), HttpStatus.BAD_REQUEST);
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getNameUser(), loginUser.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());
        return new ResponseEntity(jwtDto, HttpStatus.OK);
    }
}
