package org.example;

import java.util.Map;

public interface TemplateGenerator {
    String generate(String template, Map<String, String> input);
}
