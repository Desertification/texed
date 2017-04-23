package texed;

/**
 * Created by thoma on 23-Apr-17.
 */
public class HtmlValidator {
    private static final String[] NON_CLOSING = {"", "area", "base", "br", "col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"};

    private StringBuilder html; //text can be passed as reference and not copied internally when going over nested tags

    private int index = 0;

    private int line = 1;
    private int col = 0;

    private int errorLine = 0;
    private int errorCol = 0;

    private String tagName = "";

    public HtmlValidator(String html) {
        this.html = new StringBuilder(html);
    }

    public HtmlValidator(StringBuilder html) {
        this.html = html;
    }

    private void setErrorLocation() {
        errorLine = line;
        errorCol = col;
    }

    // track the location of the char
    private void updateLocation(char c) {
        // todo handle other non visible chars as '\r'
        switch (c) {
            case '\n':
                ++line;
                col = 1;
                break;
            case '\r':
                break;
            default:
                ++col;
                break;
        }
    }

    private boolean isNonClosing() {
        for (String s : NON_CLOSING) {
            if (tagName.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private void findOpenTag() throws UnexpectedClosingBracket, OpenBracketNotFound, StringNotClosedException, UnexpectedClosingTag, TagNameFormatException, ClosingBracketNotFound, UnexpectedOpeningBracket {
        findOpenBracket();
        findTagName();
        findClosingBracket();
    }

    private void findClosingBracket() throws UnexpectedOpeningBracket, ClosingBracketNotFound {
        setErrorLocation();
        for (int i = index; i < html.length(); i++) {
            char c = html.charAt(i);
            updateLocation(c);

            if (c == '>') {
                index = i + 1;
                return;
            } else if (c == '<') {
                setErrorLocation();
                throw new UnexpectedOpeningBracket();
            }
        }
        throw new ClosingBracketNotFound();
    }

    private void findTagName() throws UnexpectedClosingTag, TagNameFormatException, StringNotClosedException {
        StringBuilder name = new StringBuilder();
        setErrorLocation();
        for (int i = index; i < html.length(); i++) {
            char c = html.charAt(i);
            updateLocation(c);

            if (c == ' ' || c == '>') {
                if (name.length() > 0) {
                    tagName = name.toString();
                    index = i;
                    return; // trailing space or bracket means end of name
                } else {
                    setErrorLocation();
                    throw new TagNameFormatException(); // cant have a name with leading spaces or have a empty name
                }
                // todo handle comments
            } else if (c == '"') {
                index = i + 1;
                jumpBehindQuote();
            } else if (c == '/') {
                setErrorLocation();
                throw new UnexpectedClosingTag(); // this tag should not be closing
            } else if (c == '!' || c == '-') {
                // ignore comments or special
                tagName = "";
                index = i + 1;
                return;
            } else if (!Character.isAlphabetic(c)) {
                setErrorLocation();
                throw new TagNameFormatException(); // tags cant have non alphabetic chars in their name
            } else {
                name.append(c);
            }
        }
        throw new TagNameFormatException(); // file cant end on tag name
    }

    private void findClosingTag() throws UnexpectedClosingBracket, OpenBracketNotFound, StringNotClosedException, UnexpectedClosingTag, ClosingTagNotFound, TagNameFormatException, UnexpectedOpeningBracket, ClosingBracketNotFound {
        findOpenBracket();
        findSlash();
        String tagnameCopy = tagName;
        findTagName();
        if (!tagnameCopy.equals(tagName)){
            throw new UnexpectedClosingTag();
        }
        findClosingBracket();
    }

    private void findSlash() throws ClosingTagNotFound, UnexpectedClosingBracket, UnexpectedClosingTag, TagNameFormatException, OpenBracketNotFound, UnexpectedOpeningBracket, StringNotClosedException, ClosingBracketNotFound {
        setErrorLocation();
        for (int i = index; i < html.length(); i++) {
            char c = html.charAt(i);
            updateLocation(c);

            if (c == '/') {
                index = i + 1;
                return;
            } else { // can be nested opening tag
                index = index - 1;
                new HtmlValidator(html).validateInt(); //process nested tag
                findOpenBracket(); // find the open bracket again before continuing the search
            }
        } throw new ClosingTagNotFound();
    }

    private void findOpenBracket() throws UnexpectedClosingBracket, OpenBracketNotFound, StringNotClosedException {
        setErrorLocation();
        for (int i = index; i < html.length(); i++) {
            char c = html.charAt(i);
            updateLocation(c);
            if (c == '<') {
                index = i + 1;
                return;
            } else if (c == '"') {
                index = i + 1;
                jumpBehindQuote(); //skip over strings
            } else if (c == '>') {
                setErrorLocation();
                throw new UnexpectedClosingBracket();
            }
        }
        throw new OpenBracketNotFound();
    }

    private void validateInt() throws UnexpectedClosingBracket, UnexpectedClosingTag, UnexpectedOpeningBracket, ClosingBracketNotFound, TagNameFormatException, StringNotClosedException, OpenBracketNotFound, ClosingTagNotFound {
        for (int i = index; i < html.length(); i++) {
            findOpenTag();
            if (!isNonClosing()) {
                findClosingTag();
            }
        }
    }

    public HtmlValidationResult validate() {
        try {
            validateInt();
            return new HtmlValidationResult(true,"Valid", -1,-1); // everything ok
        } catch (HtmlException e) {
            return new HtmlValidationResult(false, e.getMessage(), errorLine, errorCol);
        }
    }

    // returns index where
    private void jumpBehindQuote() throws StringNotClosedException {
        for (int i = index; i < html.length(); i++) {
            char c = html.charAt(i);
            if (c == '"') {
                index = i + 1;
                return;
            }
        }
        throw new StringNotClosedException();
    }


    private class HtmlValidationResult {
        private boolean valid;
        private String message;
        private int line;
        private int col;

        public HtmlValidationResult(boolean valid, String message, int line, int col) {
            this.valid = valid;
            this.message = message;
            this.line = line;
            this.col = col;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return message;
        }

        public int getLine() {
            return line;
        }

        public int getCol() {
            return col;
        }
    }

    private class HtmlException extends Exception{
        public HtmlException(String message) {
            super(message);
        }
    }

    private class TagNameFormatException extends HtmlException {
        public TagNameFormatException() {
            super("TagNameFormatException");
        }
    }

    private class UnexpectedClosingTag extends HtmlException {
        public UnexpectedClosingTag() {
            super("UnexpectedClosingTag");
        }
    }

    private class ClosingBracketNotFound extends HtmlException {
        public ClosingBracketNotFound() {
            super("ClosingBracketNotFound");
        }
    }

    private class OpenBracketNotFound extends HtmlException {
        public OpenBracketNotFound() {
            super("OpenBracketNotFound");
        }
    }

    private class UnexpectedClosingBracket extends HtmlException {
        public UnexpectedClosingBracket() {
            super("UnexpectedClosingBracket");
        }
    }

    private class UnexpectedOpeningBracket extends HtmlException {
        public UnexpectedOpeningBracket() {
            super("UnexpectedOpeningBracket");
        }
    }

    private class StringNotClosedException extends HtmlException {
        public StringNotClosedException() {
            super("StringNotClosedException");
        }
    }

    private class ClosingTagNotFound extends HtmlException {
        public ClosingTagNotFound() {
            super("ClosingTagNotFound");
        }
    }
}
