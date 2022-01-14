package us.soupland.kitpvp.utilities.chat;

import java.util.HashMap;
import java.util.Map;

public class MessageBuilder {
    private Map<String, String> variableMap;

    public MessageBuilder() {
        variableMap = new HashMap<>();
    }

    public MessageBuilder setVariable(String variable, String value) {
        if (variable != null && !variable.isEmpty()) {
            variableMap.put(variable, value);
        }
        return this;
    }

    public String format(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        for (Map.Entry<String, String> entry : variableMap.entrySet()) {
            String s = entry.getKey();
            String s2 = entry.getValue();
            message = message.replace(s, s2);
        }

        return ColorText.translate(message);
    }
}