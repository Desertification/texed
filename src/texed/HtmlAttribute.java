package texed;

import java.text.MessageFormat;

/**
 * Created by thoma on 15-Apr-17.
 */
public class HtmlAttribute {
    private final String name;
    private final String value;

    public HtmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}=\"{1}\"", name, value);
    }
}
