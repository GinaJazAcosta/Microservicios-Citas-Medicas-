package com.gina.common.utils;

import java.math.BigDecimal;

public class ValoresUnicosUtils {

    public static <N extends Number> void validarNumeroRequerido(N numero){
        if(numero==null)
            throw new IllegalArgumentException("El valor númerico es requerido");
    }

    public static void validarEnteroPositivo(Integer entero, String mensaje){
        validarNumeroRequerido(entero);
        if (entero<0)
            throw new IllegalArgumentException(mensaje);
    }

    public static void validarLongPositivo(Long entero, String mensaje){
        validarNumeroRequerido(entero);
        if (entero<0)
            throw new IllegalArgumentException(mensaje);
    }

    public static void validarBigDecimalPositivo(BigDecimal numero, String mensaje){
        validarNumeroRequerido(numero);
        if (numero.compareTo(BigDecimal.ZERO)<0)
            throw new IllegalArgumentException(mensaje);
    }

    public static void validarEdad(Integer entero){
        validarNumeroRequerido(entero);
        if (entero < 18 || entero > 100)
            throw new IllegalArgumentException("La edad debe ser entre 18 y 100");
    }

    public static void validarRangoShort(Short numero, short min, short max, String mensaje){
        validarNumeroRequerido(numero);

        if (numero < min || numero > max)
            throw new IllegalArgumentException(mensaje);
    }

    public static void validarRangoDouble(Double numero, double min, double max, String mensaje){
        validarNumeroRequerido(numero);

        if (numero < min || numero > max)
            throw new IllegalArgumentException(mensaje);
    }

}
