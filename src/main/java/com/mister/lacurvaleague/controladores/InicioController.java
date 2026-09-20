package com.mister.lacurvaleague.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    final MisterControlador mc;

    InicioController(MisterControlador mc) {
        this.mc = mc;
    }

    @GetMapping("/")
    public String redirectARoot() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String paginaInicio() {
        return "inicio"; 
    }
}
