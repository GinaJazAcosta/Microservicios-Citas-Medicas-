package com.gina.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StringCustomUtils {

    private static final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void validarNoVacio(String texto, String mensaje){
        if(texto == null || texto.isBlank()){
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static void validarTamanio(String texto, Integer min, Integer max, String mensaje){
        validarNoVacio(texto, mensaje);
        if(texto.length() < min || texto.length() > max){
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static String quitarAcentos(String texto){
        return texto.toLowerCase()
                .replace("á", "a")
                .replace("é","e")
                .replace("í","i")
                .replace("ó","o")
                .replace("ú","u")
                .replace("ü","u");
    }

    public static void validarEmail(String correo) {
        if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new IllegalArgumentException("Por favor ingresar un correo valido");
    }

    public static void validarTelefono(String telefono) {
        if (!telefono.matches("^[0-9]{10}$"))
            throw new IllegalArgumentException("El teléfono debe contener solo números");
    }

    public static String localDateString(LocalDate fecha){
        return fecha == null ? null : fecha.format(formato);
    }
}

