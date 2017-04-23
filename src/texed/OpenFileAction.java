package texed;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Created by thoma on 22-Apr-17.
 */
public class OpenFileAction implements ActionListener {
    private JTextComponent jTextComponent;
    private JFrame frame;

    public OpenFileAction(JTextComponent jTextComponent, JFrame frame) {
        this.jTextComponent = jTextComponent;
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new HtmlFileFilter());
        int returnVal = fileChooser.showDialog(frame, "Select");
        if (returnVal == JFileChooser.APPROVE_OPTION) { //todo warn for unsaved changes
            File file = fileChooser.getSelectedFile();
            try {
                String allLines = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                SwingUtilities.invokeLater(() -> jTextComponent.setText(allLines));
                String filePath = file.getPath();
                SwingUtilities.invokeLater(() -> frame.setTitle("Texed - " + filePath));
            } catch (IOException ex) {
                ex.printStackTrace();
                //todo file open error dialog
            }
        }
    }
}
