import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;

public class JGRitor  extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;
    private JFileChooser fileChooser;
    private ArrayList<Boolean> IsNew;
    private ArrayList<Boolean> HasChanges;
    private ArrayList<JLabel> tabTitleLabels = new ArrayList<>();
    private ArrayList<UndoManager> undoManagers = new ArrayList<>();


    public JGRitor() {

        // Setup frame
        setTitle("JGRiotr");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        IsNew = new ArrayList<>();
        HasChanges = new ArrayList<>();

        // Initialize components
        tabbedPane = new JTabbedPane();
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        add(tabbedPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem newItem = new JMenuItem("<html>New    <span style='color:gray;'>Ctrl+N</span></html>");
        newItem.addActionListener(this);
        newItem.setIcon( new ImageIcon(new ImageIcon(getClass().getResource("/resources/new.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        fileMenu.add(newItem);

        JMenuItem openItem = new JMenuItem("<html>Open  <span style='color:gray;'>Ctrl+O</span></html>");
        openItem.addActionListener(this);
        openItem.setIcon( new ImageIcon(new ImageIcon(getClass().getResource("/resources/open.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("<html>Save  <span style='color:gray;'>Ctrl+S</span></html>");
        saveItem.addActionListener(this);
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("<html>Save As <span style='color:gray;'>Ctrl+Alt+S</span></html>");
        saveAsItem.addActionListener(this);
        fileMenu.add(saveAsItem);

        fileMenu.add(new JToolBar.Separator());

        JMenuItem exitItem = new JMenuItem("<html>Exit  <span style='color:gray;'>Ctrl+W</span></html>");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        JMenuItem undoItem = new JMenuItem("<html>Undo    <span style='color:gray;'>Ctrl+Z</span></html>");
        undoItem.addActionListener(this);
        undoItem.setIcon( new ImageIcon(new ImageIcon(getClass().getResource("/resources/undo.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        editMenu.add(undoItem);

        JMenuItem redoItem = new JMenuItem("<html>Redo    <span style='color:gray;'>Ctrl+Y</span></html>");
        redoItem.addActionListener(this);
        redoItem.setIcon( new ImageIcon(new ImageIcon(getClass().getResource("/resources/redo.png")).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        editMenu.add(redoItem);

        editMenu.add(new JToolBar.Separator());

        JMenuItem cutItem = new JMenuItem("<html>Cut    <span style='color:gray;'>Ctrl+X</span></html>");
        cutItem.addActionListener(this);
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem("<html>Copy  <span style='color:gray;'>Ctrl+C</span></html>");
        copyItem.addActionListener(this);
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("<html>Paste    <span style='color:gray;'>Ctrl+V</span></html>");
        pasteItem.addActionListener(this);
        editMenu.add(pasteItem);



        InputMap inputMap = tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = tabbedPane.getActionMap();

        KeyStroke saveKeyStroke = KeyStroke.getKeyStroke("control S");
        inputMap.put(saveKeyStroke, "save");
        actionMap.put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control alt S"), "saveas");
        actionMap.put("saveas", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFileAs();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control N"), "new");
        actionMap.put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createNewTab();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control O"), "open");
        actionMap.put("open", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control W"), "exit");
        actionMap.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control Z"), "undo");
        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("control Y"), "redo");
        actionMap.put("redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "<html>New    <span style='color:gray;'>Ctrl+N</span></html>":
                createNewTab();
                break;
            case "<html>Open  <span style='color:gray;'>Ctrl+O</span></html>":
                openFile();
                break;
            case "<html>Save  <span style='color:gray;'>Ctrl+S</span></html>":
                saveFile();
                break;
            case "<html>Save As <span style='color:gray;'>Ctrl+Alt+S</span></html>":
                saveFileAs();
                break;
            case "<html>Exit  <span style='color:gray;'>Ctrl+W</span></html>":
                System.exit(0);
                break;
            case "<html>Cut    <span style='color:gray;'>Ctrl+X</span></html>":
                getCurrentTextArea().cut();
                break;
            case "<html>Copy    <span style='color:gray;'>Ctrl+C</span></html>":
                getCurrentTextArea().copy();
                break;
            case "<html>Paste    <span style='color:gray;'>Ctrl+V</span></html>":
                getCurrentTextArea().paste();
                break;
            case "<html>Undo    <span style='color:gray;'>Ctrl+Z</span></html>":
                undo();
                break;
            case "<html>Redo    <span style='color:gray;'>Ctrl+Y</span></html>":
                redo();
                break;
        }
    }

    private void createNewTab() {
        JTextArea textArea = new JTextArea();
        addTab("New Tab", textArea);

        int index = IsNew.size() - 1;
        IsNew.set(index, true);
        addDocumentListener(textArea, index);

        // Add an UndoManager for each new tab
        UndoManager undoManager = new UndoManager();
        undoManagers.add(undoManager);
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
    }

    private void undo() {
        int index = tabbedPane.getSelectedIndex();
        if (index == -1 || index >= undoManagers.size()) return;

        UndoManager undoManager = undoManagers.get(index);
        if (undoManager.canUndo()) {
            try {
                undoManager.undo();
            } catch (CannotUndoException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void redo() {
        int index = tabbedPane.getSelectedIndex();
        if (index == -1 || index >= undoManagers.size()) return;

        UndoManager undoManager = undoManagers.get(index);
        if (undoManager.canRedo()) {
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void openFile() {
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                JTextArea textArea = new JTextArea();
                textArea.read(reader, null);
                addTab(file.getName(), textArea);

                int index = IsNew.size() - 1;
                addDocumentListener(textArea, index);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be opened.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void addTab(String title, JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setOpaque(false);

        JLabel tabTitle = new JLabel(title);
        tabTitleLabels.add(tabTitle); // Store the tab title label

        JButton closeButton = new JButton("x");

        closeButton.setPreferredSize(new Dimension(15, 15));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);

        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setForeground(Color.RED);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setForeground(Color.BLACK);
            }
        });

        closeButton.addActionListener(e -> {
            int index = tabbedPane.indexOfTabComponent(tabPanel);
            if (index != -1) {
                tabbedPane.remove(index);
                IsNew.remove(index);
                HasChanges.remove(index);
                tabTitleLabels.remove(index); // Remove the tab title label
            }
        });

        IsNew.add(false);
        HasChanges.add(false);

        tabPanel.add(tabTitle);
        tabPanel.add(closeButton);

        tabPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        tabbedPane.addTab(title, scrollPane);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabPanel);
        tabbedPane.setSelectedComponent(scrollPane);


    }


    private JTextArea getCurrentTextArea() {
        JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        return (JTextArea) scrollPane.getViewport().getView();
    }

    private void addDocumentListener(JTextArea textArea, int index) {
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                HasChanges.set(index, true);
                updateTabTitle(index);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                HasChanges.set(index, true);
                updateTabTitle(index);
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                HasChanges.set(index, true);
                updateTabTitle(index);
            }
        });
    }

    private void updateTabTitle(int index) {
        SwingUtilities.invokeLater(() -> {
            JLabel tabTitle = tabTitleLabels.get(index);
            String title = tabTitle.getText();
            if (HasChanges.get(index) && !title.endsWith("*")) {
                tabTitle.setText(title + "*");
            } else if (!HasChanges.get(index) && title.endsWith("*")) {
                tabTitle.setText(title.substring(0, title.length() - 1));
            }
        });
    } private void renameTabTitle(int index,String newTitle) {
        SwingUtilities.invokeLater(() -> {
            JLabel tabTitle = tabTitleLabels.get(index);
            tabTitle.setText(newTitle);
        });
    }

    private void saveFile() {
        int index = tabbedPane.getSelectedIndex();
        if (index == -1) return; // No tab selected

        if (IsNew.get(index)) {
            saveFileAs();
        } else {
            String title = tabbedPane.getTitleAt(index).replace("*", "");
            if (!title.endsWith(".txt")) {
                title += ".txt";
            }
            File file = new File(title);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                JTextArea textArea = getCurrentTextArea();
                textArea.write(writer);
                HasChanges.set(index, false);
                updateTabTitle(index);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be saved.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Save As functionality
    private void saveFileAs() {
        int index = tabbedPane.getSelectedIndex();
        if (index == -1) return; // No tab selected

        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".txt")) {
                file = new File(filePath + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                JTextArea textArea = getCurrentTextArea();
                textArea.write(writer);
                tabbedPane.setTitleAt(index, file.getName());
                renameTabTitle(index,file.getName());
                IsNew.set(index, false);
                HasChanges.set(index, false);
                updateTabTitle(index);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be saved.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
