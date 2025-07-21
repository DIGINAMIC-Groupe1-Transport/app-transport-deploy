package com.diginamic.groupe1.transport.controller;

import com.diginamic.groupe1.transport.dto.AuthRequestDTO;
import com.diginamic.groupe1.transport.security.AuthService;
import com.diginamic.groupe1.transport.utils.ResponseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseApi<?>> login(@RequestBody AuthRequestDTO authRequestDto) {
        return ResponseEntity.ok(ResponseApi.success(authService.verify(authRequestDto), "ok"));

    }

}
