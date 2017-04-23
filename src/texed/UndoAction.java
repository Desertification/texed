package texed;

import myLinkedList.StackImp;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by thoma on 23-Apr-17.
 */
public class UndoAction implements ActionListener {
    private StackImp<Runnable> undoStack;
    private AbstractButton undoButton;
    private JTextComponent jTextComponent;
    private String textCopy;
    private boolean ignored;

    public UndoAction(AbstractButton undoButton, JTextComponent jTextComponent) {
        this.undoButton = undoButton;
        this.jTextComponent = jTextComponent;
        undoStack = new StackImp<>();
        textCopy = jTextComponent.getText();
        ignored = false;
    }

    private void updateUndoButton() {
        SwingUtilities.invokeLater(() -> undoButton.setEnabled(!undoStack.isEmpty()));
    }

    public void addUndo() {
        System.out.println(undoStack.size());
        if (!ignored) {
            undoStack.push(new Task(this, textCopy));
            textCopy = jTextComponent.getText();
            updateUndoButton();
        }
        ignored = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(undoStack.size());
        Runnable runnable = undoStack.pop();
        updateUndoButton();
        SwingUtilities.invokeLater(runnable);
    }

    private class Task implements Runnable{

        private final UndoAction undoAction;
        private final String text;

        public Task(UndoAction undoAction, String text) {
            this.undoAction = undoAction;
            this.text = text;
        }

        @Override
        public void run() {
            undoAction.ignored = true;
            undoAction.jTextComponent.setText(text);
        }
    }
}
