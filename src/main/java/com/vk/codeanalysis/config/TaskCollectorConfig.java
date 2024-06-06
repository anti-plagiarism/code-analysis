package com.vk.codeanalysis.config;

import com.vk.codeanalysis.public_interface.tokenizer.Language;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterCpp;
import org.treesitter.TreeSitterJava;
import org.treesitter.TreeSitterPython;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Configuration
public class TaskCollectorConfig {
    @Bean
    public Map<Language, TSLanguage> treeSitter() {
        return Map.of(
                Language.JAVA, new TreeSitterJava(),
                Language.CPP, new TreeSitterCpp(),
                Language.PY, new TreeSitterPython()
        );
    }

    @Bean
    public Set<Language> languages() {
        return EnumSet.allOf(Language.class);
    }
}
