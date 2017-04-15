package texed;

import java.text.MessageFormat;

/**
 * Created by thoma on 15-Apr-17.
 */
public class HtmlTag {
    private final String name;
    private final String content;
    private final boolean selfClosing;
    private final HtmlAttribute[] attributes;

    public HtmlTag(String name, String content, boolean selfClosing, HtmlAttribute... attributes) {
        this.name = name;
        this.content = content;
        this.selfClosing = selfClosing;
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public boolean isSelfClosing() {
        return selfClosing;
    }

    public HtmlAttribute[] getAttributes() {
        return attributes;
    }

    private String attributesToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (HtmlAttribute attribute : attributes) {
            stringBuilder.append(" ");
            stringBuilder.append(attribute);
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        if (selfClosing) {
            String attributes = attributesToString();
            return MessageFormat.format("<{0}{1} />", name, attributes);
        } else {
            return MessageFormat.format("<{0}{1}>{2}<{0}/>", name, attributes, content);
        }
    }
}
