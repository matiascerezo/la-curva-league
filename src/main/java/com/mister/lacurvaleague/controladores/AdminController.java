package com.mister.lacurvaleague.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mister.lacurvaleague.servicios.MantenimientoDatosService;

@Controller
@RequestMapping("/mantenimiento/admin")
public class AdminController {

    @Autowired
    MantenimientoDatosService mantenimientoDatosService;

    // Esta es la URL para entrar a ver la web: lacurvaleague.es/mantenimiento/admin/panel
    @GetMapping("/panel")
    public String mostrarPanelAdmin(Model model) {
        return "/admin/admin";
    }

    @PostMapping("/subirJson")
    public String subirJSON(@RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Archivo vacío");
            return "redirect:inicio";
        }

        try {
            String fileName = file.getOriginalFilename();
            String mensaje = mantenimientoDatosService.procesarFicheroJSON(file.getInputStream(), fileName);
            
            redirectAttributes.addFlashAttribute("Todo bien.", mensaje);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error crítico: " + e.getMessage());
        }

        return "redirect:/inicio";
    }
}
