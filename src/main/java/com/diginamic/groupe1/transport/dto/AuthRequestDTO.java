package com.diginamic.groupe1.transport.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {

    private String corpEmail;
    private String password;

}
