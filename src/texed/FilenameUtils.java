package texed;

import java.io.File;

/**
 * Created by thoma on 16-Apr-17.
 */
public final class FilenameUtils {

    //http://stackoverflow.com/a/21974043
    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
