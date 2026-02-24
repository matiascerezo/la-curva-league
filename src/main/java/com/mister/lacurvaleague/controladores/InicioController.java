package com.mister.lacurvaleague.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @Autowired
    MisterControlador mc;

    @GetMapping("/")
    public String redirectARoot() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String paginaInicio() {
        return "inicio"; 
    }
}
