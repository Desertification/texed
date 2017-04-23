package texed;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Simple GUI for a text editor.
 */
public class Texed extends JFrame implements DocumentListener {
    private JTextArea textArea;
    private JLabel statusLabel;
    private JButton undoButton;
    private JButton redoButton;
    private HtmlValidationWorker htmlValidationWorker;
    private UndoAction undoAction;


    private boolean ignoreChange = false;
    private String textCopy;

    /**
     * Constructs a new GUI: A TextArea on a ScrollPane
     */
    public Texed() {
        super();

        //Initialize window
        setTitle("Texed: simple html text editor");
        setBounds(0, 0, 600, 400);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setLayout(new BorderLayout());

        //ToolBar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.PAGE_START);
        //new button
        JButton newButton = new JButton();
        newButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("new.png")));
        newButton.setToolTipText("New");
        //todo action listener
        toolBar.add(newButton);
        //open button
        JButton openButton = new JButton();
        openButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("open.png")));
        openButton.setToolTipText("Open");
        toolBar.add(openButton);
        //save button
        JButton saveButton = new JButton();
        saveButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("save.png")));
        saveButton.setToolTipText("Save");
        //todo action listener
        toolBar.add(saveButton);
        toolBar.addSeparator();
        //undo button
        undoButton = new JButton();
        undoButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("undo.png")));
        undoButton.setDisabledIcon(new ImageIcon(ClassLoader.getSystemResource("undo_disabled.png")));
        undoButton.setToolTipText("Undo");
        undoButton.setEnabled(false);
        //todo action listener
        toolBar.add(undoButton);
        //redo button
        redoButton = new JButton();
        redoButton.setIcon(new ImageIcon(ClassLoader.getSystemResource("redo.png")));
        redoButton.setDisabledIcon(new ImageIcon(ClassLoader.getSystemResource("redo_disabled.png")));
        redoButton.setToolTipText("Redo");
        redoButton.setEnabled(false);
        //todo action listener
        toolBar.add(redoButton);

        //TextArea
        textArea = new JTextArea();
        textArea.setLineWrap(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        // create the status bar panel and shove it down the bottom of the frame
        //http://stackoverflow.com/a/3035893
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // listeners
        textCopy = textArea.getText();
        openButton.addActionListener(new OpenFileAction(textArea, this));
        textArea.getDocument().addDocumentListener(this); //Registration of the callback
        htmlValidationWorker = new HtmlValidationWorker(textArea, statusLabel);

        undoAction = new UndoAction(undoButton, textArea);
        undoButton.addActionListener(undoAction);
    }



    /**
     * Callback when changing an element
     */
    public void changedUpdate(DocumentEvent ev) {
        undoAction.addUndo();
        htmlValidationWorker.documentChanged();
    }

    /**
     * Callback when deleting an element
     */
    public void removeUpdate(DocumentEvent ev) {
        undoAction.addUndo();
        htmlValidationWorker.documentChanged();
    }

    /**
     * Callback when inserting an element
     */
    public void insertUpdate(DocumentEvent ev) {
        undoAction.addUndo();
        htmlValidationWorker.documentChanged();

        //        //Check if the change is only a single character, otherwise return so it does not go in an infinite loop
        //        if (ev.getLength() != 1)
        //            return;
        //
        //        // In the callback you cannot change UI elements, you need to start a new Runnable
        //        SwingUtilities.invokeLater(new Task("foo"));
    }


    /**
     * Runnable: change UI elements as a result of a callback
     * Start a new Task by invoking it through SwingUtilities.invokeLater
     */
    private class Task implements Runnable {
        private String text;

        /**
         * Pass parameters in the Runnable constructor to pass data from the callback
         *
         * @param text which will be appended with every character
         */
        Task(String text) {
            this.text = text;
        }

        /**
         * The entry point of the runnable
         */
        public void run() {
            textArea.append(text);
        }
    }

    /**
     * Entry point of the application: starts a GUI
     */
    public static void main(String[] args) {
        new Texed();

    }

}
