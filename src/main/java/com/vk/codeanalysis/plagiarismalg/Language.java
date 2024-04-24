package com.vk.codeanalysis.plagiarismalg;

import java.util.HashMap;
import java.util.Map;

public enum Language {
    JAVA, CPP, PYTHON;

    private static final Map<String, Language> STRING_TO_ENUM = new HashMap<>();

    static {
        for (Language lang : Language.values()) {
            STRING_TO_ENUM.put(lang.toString().toLowerCase(), lang);
        }
    }

    public static Language fromString(String value) {
        return STRING_TO_ENUM.get(value.toLowerCase());
    }

    public static String toString(Language lang) {
        return lang.toString().toLowerCase();
    }
}
