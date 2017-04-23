package texed;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.text.MessageFormat;

/**
 * Created by thoma on 23-Apr-17.
 */
public class HtmlValidationWorker {
    private boolean checkPending;
    private JTextComponent jTextComponent;
    private JLabel statusLabel;
    private Thread worker;

    public HtmlValidationWorker(JTextComponent jTextComponent, JLabel statusLabel) {
        this.jTextComponent = jTextComponent;
        this.statusLabel = statusLabel;
        worker = new Thread(this::check);
        worker.start();
    }

    private void check() {
        do {
            checkPending = false;
            HtmlValidator htmlValidator = new HtmlValidator(jTextComponent.getText());
            HtmlValidator.HtmlValidationResult htmlValidationResult = htmlValidator.validate();
            if (htmlValidationResult.isValid()) {
                SwingUtilities.invokeLater(() -> statusLabel.setText("OK"));
            } else {
                String errorMessage = htmlValidationResult.getErrorMessage();
                int line = htmlValidationResult.getLine();
                int col = htmlValidationResult.getCol();
                SwingUtilities.invokeLater(() -> statusLabel.setText(MessageFormat.format("{0} at {1}:{2}", errorMessage, line, col)));
            }
        } while (checkPending);
    }

    public void documentChanged() {
        checkPending = true;
        if (!worker.isAlive()) {
            worker = new Thread(this::check);
            worker.start();
        }
    }
}
