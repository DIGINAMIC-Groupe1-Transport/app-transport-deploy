package com.diginamic.groupe1.transport.controller;

import com.diginamic.groupe1.transport.dto.UserInfoDTO;
import com.diginamic.groupe1.transport.service.UserInfoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserInfoController {

    private UserInfoService userInfoService;

    @GetMapping("/{corpEmail}")
    public ResponseEntity<UserInfoDTO> getUserByCorpEmail(
            @PathVariable String corpEmail
    ) {
        return ResponseEntity.ok(userInfoService.findUserInfoByCorpEmail(corpEmail));
    }

    @GetMapping
    public ResponseEntity<Page<UserInfoDTO>> getAllUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @PageableDefault(sort = { "firstName",
                    "lastName" }, value = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(userInfoService.findAllUsersInfo(firstName, lastName, pageable));
    }
}
