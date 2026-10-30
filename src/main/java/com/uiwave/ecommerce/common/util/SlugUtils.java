package com.uiwave.ecommerce.common.util;

import java.text.Normalizer;
import java.util.Locale;

/** Utilidades para generar slugs amigables a partir de nombres. */
public final class SlugUtils {

    private SlugUtils() {
    }

    public static String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
