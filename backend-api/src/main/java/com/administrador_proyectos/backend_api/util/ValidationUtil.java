package com.administrador_proyectos.backend_api.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    // Patrón para validar email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Patrón para validar teléfono (formato internacional o local)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$");

    /**
     * Valida que el email tenga un formato correcto
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida que el teléfono tenga un formato correcto
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Valida que la contraseña sea segura
     * Requisitos:
     * - Mínimo 8 caracteres
     * - Al menos una letra mayúscula
     * - Al menos una letra minúscula
     * - Al menos un número
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpperCase = true;
            if (Character.isLowerCase(c))
                hasLowerCase = true;
            if (Character.isDigit(c))
                hasDigit = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit;
    }

    /**
     * Obtiene un mensaje descriptivo de los requisitos de contraseña
     */
    public static String getPasswordRequirements() {
        return "La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas y números";
    }
}
