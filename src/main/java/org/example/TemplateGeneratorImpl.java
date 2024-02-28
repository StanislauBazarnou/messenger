package org.example;

import java.util.Map;

public class TemplateGeneratorImpl implements TemplateGenerator {

    @Override
    public String generate(String template, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("#{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}
