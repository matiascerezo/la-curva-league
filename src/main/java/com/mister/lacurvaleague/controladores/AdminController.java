package com.mister.lacurvaleague.controladores;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mister.lacurvaleague.modelos.Equipo;
import com.mister.lacurvaleague.modelos.Mister;
import com.mister.lacurvaleague.modelos.dto.dtoAdmin.AdminEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClausulazosDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LloroIndividualDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.LlorometroDTO;
import com.mister.lacurvaleague.servicios.MantenimientoDatosService;
import com.mister.lacurvaleague.servicios.MisterService;

@Controller
@RequestMapping("/mantenimiento/admin")
public class AdminController {

    final MantenimientoDatosService mantenimientoDatosService;
    final MisterService misterService;

    AdminController(MantenimientoDatosService mantenimientoDatosService, MisterService misterService) {
        this.mantenimientoDatosService = mantenimientoDatosService;
        this.misterService = misterService;
    }

    // Esta es la URL para entrar a ver la web: lacurvaleague.es/mantenimiento/admin/panel
    @GetMapping("/panel")
    public String mostrarPanelAdmin(Model model) {
        return "admin/admin";
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
            
            if(mensaje.contains("Error")) return mensaje;
            redirectAttributes.addFlashAttribute("Todo bien.", mensaje);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error crítico: " + e.getMessage());
        }

        return "redirect:/inicio";
    }

    @GetMapping("/equipos")
    public String gestionarEquipos(Model model) {
        List<AdminEquipoDTO> adminEquipos = mantenimientoDatosService.obtenerTodosLosEquiposOrdenados();

        model.addAttribute("adminEquipos", adminEquipos);
        return "admin/admin-equipos";
    }

    @GetMapping("/jornadas")
    public String mostrarPanelJornada(@RequestParam(name = "jornada", required = false, defaultValue = "1") Integer jornada, Model model) {
        
        model.addAttribute("jornadaActual", jornada);
        return "admin/admin-jornada";
    }

    @PostMapping("/equipos/update")
    public void actualizarEquipo(Equipo equipo) {
        mantenimientoDatosService.actualizarEquipo(equipo);
    }

    @PostMapping("/misters/update")
    public void actualizarMister(Mister m) {
        mantenimientoDatosService.actualizarMister(m);
    }

    @GetMapping("/listaEquipos")
    @ResponseBody
    public List<Mister> getListaEquiposSeleccionables() {
        return misterService.getTodosMistersOrdenados();
    }
    
    @GetMapping("/listaJornadas")
    @ResponseBody
    public Set<Integer> getListaJornadas() {
        return misterService.getListaJornadas();
    }

    @GetMapping("/clausulazos")
    public String mostrarFormularioClausulazos() {
        return "admin/admin-clausulazos";
    }

    @GetMapping("/lloros")
    public String mostrarFormularioLloros() {
        return "admin/admin-lloros";
    }

    @PostMapping("/guardarClausulazo")
    @ResponseBody
    public ResponseEntity<String> guardarClausulazoFormulario(@RequestBody List<ClausulazosDTO> clausulazosDTOs) {
        try {
            // Pasamos null en InputStream para reutilizar tu método adapter
            String resultado = mantenimientoDatosService.cargarClausulazoInd(clausulazosDTOs);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar: " + e.getMessage());
        }
    }

    @PostMapping("/guardarLloro")
    @ResponseBody
    public ResponseEntity<String> guardarLloro(@RequestBody LloroIndividualDTO lloroDTO) {
        try {
            String resultado = mantenimientoDatosService.cargarLloroInd(lloroDTO);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al guardar: " + e.getMessage());
        }
    }
}
