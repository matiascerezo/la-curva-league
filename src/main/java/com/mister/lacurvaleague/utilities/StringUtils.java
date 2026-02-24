package com.mister.lacurvaleague.utilities;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component("stringUtils")
public class StringUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String parsearRuta(String input) {
        if (input == null) return "";
        
        // 1. Quitar espacios al principio y final
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        
        // 2. Normalizar: separa la 'á' en 'a' + 'tilde'
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        
        // 3. Eliminar todo lo que no sea una letra normal (las tildes)
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        
        // 4. A minúsculas y quitar posibles guiones dobles
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-{2,}", "-");
    }
}
