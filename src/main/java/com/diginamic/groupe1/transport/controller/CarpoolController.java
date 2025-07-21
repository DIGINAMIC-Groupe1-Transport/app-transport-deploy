package com.diginamic.groupe1.transport.controller;

import com.diginamic.groupe1.transport.dto.carpool.CarpoolDTO;
import com.diginamic.groupe1.transport.dto.carpool.CarpoolDetailsDTO;
import com.diginamic.groupe1.transport.dto.carpool.CarpoolOrganizeUpsertDTO;
import com.diginamic.groupe1.transport.dto.carpool.CarpoolSearchResponseListDTO;
import com.diginamic.groupe1.transport.security.CustomUserDetails;
import com.diginamic.groupe1.transport.service.CarpoolService;
import com.diginamic.groupe1.transport.utils.ResponseApi;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/user/carpools")
@AllArgsConstructor
public class CarpoolController {

    private CarpoolService carpoolService;

    @GetMapping("/search")
    public ResponseEntity<ResponseApi<Page<CarpoolSearchResponseListDTO>>> getCarpools(
            @RequestParam Double startX,
            @RequestParam Double startY,
            @RequestParam Double endX,
            @RequestParam Double endY,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate departureDate,
            @RequestParam(required = false) Double startWeight,
            @RequestParam(required = false) Double endWeight,
            @PageableDefault(sort = {"creationTime"}, value = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ResponseApi.success(carpoolService.findAllCarpools(
                        startX,
                        startY,
                        endX,
                        endY,
                        departureDate,
                        startWeight,
                        endWeight,
                        pageable
                ),
                "Covoiturages récupérés"));
    }

    @GetMapping("/search/{carpoolId}")
    public ResponseEntity<ResponseApi<CarpoolDetailsDTO>> getCarpoolDetails(
            @PathVariable Long carpoolId
    ) {
        return ResponseEntity.ok(ResponseApi.success(carpoolService.findCarpoolDetails(carpoolId), "" +
                "Covoiturages organisés récupérés"));
    }

    @GetMapping("/organize")
    public ResponseEntity<ResponseApi<Page<CarpoolDTO>>> getOrganizedCarpools(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(sort = {"creationTime"}, value = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ResponseApi.success(carpoolService.findAllOrganizedCarpools(userDetails.getUserInfo(), pageable), "" +
                "Covoiturages organisés récupérés"));
    }

    @GetMapping("/participate")
    public ResponseEntity<ResponseApi<Page<CarpoolDTO>>> getParticipatedCarpools(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(sort = {"creationTime"}, value = 20) Pageable pageable

    ) {
        return ResponseEntity.ok(ResponseApi.success(carpoolService.findAllParticipatedCarpools(userDetails, pageable),
                "Covoiturages participés récupérés"));
    }

    @PostMapping("/organize")
    public ResponseEntity<ResponseApi<CarpoolDTO>> createOrganizeCarpool
            (@AuthenticationPrincipal CustomUserDetails userDetails,
             @Valid @RequestBody CarpoolOrganizeUpsertDTO carpoolOrganizeUpsertDTO
            ) {

        return ResponseEntity.ok(ResponseApi.success(carpoolService.createOrganizeCarpool(userDetails.getUserInfo(), carpoolOrganizeUpsertDTO),
                "Coivoiturage organisé avec succés"));
    }

    @PutMapping("/participate/{carpoolId}")
    public ResponseEntity<ResponseApi<CarpoolDTO>> createParticipateCarpool(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long carpoolId,
            @PageableDefault(sort = {"creationTime"}, value = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(ResponseApi.success(carpoolService.createParticipateCarpool(userDetails.getUserInfo(), carpoolId),
                "Participation au covoiturage faite avec succés"));
    }

    @DeleteMapping("/organize/{carpoolId}")
    public ResponseEntity<ResponseApi<Void>> deleteOrganizeCarpool(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long carpoolId
    ) {
        carpoolService.deleteOrganizeCarpool(userDetails.getUserInfo(), carpoolId);
        return ResponseEntity.ok(ResponseApi.success(null, "Covoiturage supprimé avec succés"));
    }

    @DeleteMapping("/participate/{carpoolId}")
    public ResponseEntity<ResponseApi<Void>> deleteParticipateCarpool(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long carpoolId
    ) {
        carpoolService.deleteParticipateCarpool(userDetails.getUserInfo(), carpoolId);
        return ResponseEntity.ok(ResponseApi.success(null, "Participation au covoiturage supprimé avec succés"));
    }
}
