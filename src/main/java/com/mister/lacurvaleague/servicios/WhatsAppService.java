package com.mister.lacurvaleague.servicios;

import org.springframework.stereotype.Service;
import com.mister.lacurvaleague.modelos.dto.dtoFronts.ClasificacionGeneralDTO;
import com.mister.lacurvaleague.utilities.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class WhatsAppService {

    private final MisterService misterService; // Tu servicio actual

    public WhatsAppService(MisterService misterService) {
        this.misterService = misterService;
    }

    public String generarLinkWhatsAppResumen(int jornada) {
        List<ClasificacionGeneralDTO> equipos = misterService.getClasificacionGeneralXJornada(jornada);
        String observacionesJornada = misterService.getObservacionesJornada(jornada);

        StringBuilder sb = new StringBuilder();
        sb.append("🏆 *LA CURVA LEAGUE* - Resumen Jornada ").append(jornada).append("\n\n");
        sb.append("📊 *CLASIFICACIÓN*\n");

        int pos = 1;
        for (ClasificacionGeneralDTO equipo : equipos) {
            String medalla = pos == 1 ? "🥇 " : (pos == 2 ? "🥈 " : (pos == 3 ? "🥉 " : pos + ". "));
            String literalPuntos = equipo.getDifPuntos() == 1 ? " punto" : " puntos";
            sb.append(medalla)
              .append("*").append(equipo.getNombreEquipo()).append("* - ")
              .append(equipo.getPuntosTotales()).append(" pts - ")
              .append(" Puntos recortados: " + equipo.getDifPuntos() + literalPuntos + "\n");
            pos++;
        }

        if(StringUtils.niNuloNiVacio(observacionesJornada)){
            sb.append("\n*" +observacionesJornada + "*\n");
        }

        sb.append("\n🔗 *Ver detalles completos:* https://www.lacurvaleague.es/inicio");

        // Codificar el texto para que sea un enlace URL válido
        String textoCodificado = URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8);
        
        return "https://api.whatsapp.com/send?text=" + textoCodificado;
    }
}