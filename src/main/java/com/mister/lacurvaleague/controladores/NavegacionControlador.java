package com.mister.lacurvaleague.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionEquipoDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.TarjetasDTO;
import com.mister.lacurvaleague.repository.EquipoRepository;
import com.mister.lacurvaleague.repository.MisterRepository;
import com.mister.lacurvaleague.servicios.LlorometroService;
import com.mister.lacurvaleague.servicios.MisterService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@ControllerAdvice
@PropertySource("classpath:messages.properties")
public class NavegacionControlador {

    @Autowired
    LlorometroService llorometroService;
    
    @Autowired
    MisterControlador misterControlador;
    @Autowired
    InicioController inicioController;

    @Autowired
    MisterService misterService;

    @Autowired
    MisterRepository misterRepository;

    @Autowired
    EquipoRepository equipoRepository;

    @Autowired
    private Environment env;

    @GetMapping("/top/{top}")
    public String verTop(@PathVariable String top, RedirectAttributes redirectAttrs) {
        
        //Según lo que venga, tira por un camino u otro.

        /* switch (top) {
            case "pichichis":
                model.addAttribute("goleadores", misterControlador.obtenerGoleadores());
                break;
            case "llorometro":
                //model.addAttribute("goleadores", mc.obtenerGoleadores());
                break;    
            default:
                break;
        } */
        redirectAttrs.addFlashAttribute("warning", "¡Ups! La sección de " + top + " está en mantenimiento. Estará lista pronto.");
        return "redirect:/inicio";
    }

    @GetMapping("/club/{nombreMisterURL}")
    public String verEquipo(@PathVariable String nombreMisterURL, Model model) {
        
        String nombreMister = misterService.getNombreEquipoByURL(nombreMisterURL);
        List<ClasificacionEquipoDTO> cEquipoDTOs = misterService.obtenerPuntosEquipoXJornadaByURL(nombreMisterURL);
        model.addAttribute("nombreEquipo", nombreMister);
        model.addAttribute("puntosEquipoJornadas", cEquipoDTOs);
        model.addAttribute("puntosEquipoJornadasGrafica", cEquipoDTOs.reversed());
        model.addAttribute("colorEquipo", env.getProperty("color."+nombreMisterURL));
        return "equipos";
    }

    @ModelAttribute("tarjetasTotales")
    public List<TarjetasDTO> getTarjetasTotales(){
        return misterRepository.getTarjetasTotales();
    }

    @ModelAttribute("listaEquipos")
    public List<ClasificacionGeneralDTO> getListaEquipos() {
        return misterControlador.getClasificacionGeneral();
    }

    @ModelAttribute("jornadaActual")
    public String getJornadaActual() {
        String jornada = misterControlador.getJornadaActual();
        return jornada != null ? jornada : "0";
    }

    @ModelAttribute("seccionActual")
    public String getPantallaActual(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/top/goleadores")
    public String getGolesYGoleadoresXEquipo(Model model) {
        model.addAttribute("listaGoleadores", misterService.getGolesYGoleadoresXEquipo());
        return "goleadores";
    }

    @GetMapping("/top/tarjetas")
    public String mostrarTarjetas() {
        return "tarjetas";
    }

    @GetMapping("/top/llorometro")
    public String mostrarLloros(Model model) { 
        model.addAttribute("listaJornadasLloros", llorometroService.getAllLlorosParaVista());
        return "llorometro";
    }
}
