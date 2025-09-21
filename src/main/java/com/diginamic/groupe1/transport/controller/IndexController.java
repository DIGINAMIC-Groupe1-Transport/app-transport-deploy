package com.diginamic.groupe1.transport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "forward:/transport-angular/browser/index.html";    }

    @GetMapping(value = "/{path:^(?!api$|transport-angular$).*$}/**")
    public String redirect() {
        return "forward:/transport-angular/browser/index.html";
    }
}
