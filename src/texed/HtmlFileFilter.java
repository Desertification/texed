package texed;


import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by thoma on 16-Apr-17.
 */
public class HtmlFileFilter extends FileFilter {
    private final String[] VALID_EXTENSIONS = {"html", "htm", "xhtml", "jhtml"};

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = FilenameUtils.getFileExtension(f);
        for (String valid : VALID_EXTENSIONS) {
            if (extension.toLowerCase().equals(valid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "HTML";
    }
}
