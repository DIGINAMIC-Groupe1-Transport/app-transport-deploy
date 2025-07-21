package com.diginamic.groupe1.transport.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoDTO {

    private String corpEmail;

    private String personalEmail;

    private String lastName;

    private String firstName;

    private String phoneNumber;
}
