package com.mister.lacurvaleague.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mister.lacurvaleague.servicios.WhatsAppService;

@Controller
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    public WhatsAppController(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    @GetMapping("/resumen-jornada")
    public String redirigirAWhatsApp(@RequestParam(defaultValue = "8") int jornada) {
        String urlWhatsApp = whatsAppService.generarLinkWhatsAppResumen(jornada);
        return "redirect:" + urlWhatsApp;
    }
}
