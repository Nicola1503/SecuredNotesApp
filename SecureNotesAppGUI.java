package NotesApp;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class SecureNotesAppGUI extends JFrame {
    private Account currentAccount;

    // Declare input and button panels at the class level
    private JTextField titleField;
    private JTextArea contentArea;
    private JPanel inputPanel;
    private JPanel buttonPanel;
    private JList<String> noteList;
    private DefaultListModel<String> noteListModel;

    public SecureNotesAppGUI() {
        showLoginScreen();
    }

    private void showLoginScreen() {
        setTitle("Login - Secure Notes App");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginPanel.add(new JLabel("Username:"));
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        loginPanel.add(passwordField);

        buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                currentAccount = AccountManager.login(username, password);
                JOptionPane.showMessageDialog(this, "Login successful!");
                showNotesScreen();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
            }
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                currentAccount = AccountManager.createAccount(username, password);
                JOptionPane.showMessageDialog(this, "Account created successfully!");
                showNotesScreen();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
            }
        });

        add(loginPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showNotesScreen() {
        getContentPane().removeAll();
        setTitle("Secure Notes App - " + currentAccount.getUsername());
        setSize(500, 500);
        setLayout(new BorderLayout());

        titleField = new JTextField();
        contentArea = new JTextArea(5, 20);
        JButton addButton = new JButton("Add Note");
        JButton removeButton = new JButton("Remove Note");
        JButton updateButton = new JButton("Update Note");

        inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Content:"));
        inputPanel.add(new JScrollPane(contentArea));

        noteListModel = new DefaultListModel<>();
        noteList = new JList<>(noteListModel);
        updateNoteList();  // Load notes from DB on opening the notes screen

        noteList.addListSelectionListener(e -> {
            int selectedIndex = noteList.getSelectedIndex();
            if (selectedIndex != -1) {
                Note selectedNote = currentAccount.getNotesFromDB().get(selectedIndex);
                titleField.setText(selectedNote.getTitle());
                contentArea.setText(selectedNote.getContent());
            }
        });

        addButton.addActionListener(e -> addNote());
        removeButton.addActionListener(e -> removeNote());
        updateButton.addActionListener(e -> updateNote());

        buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(noteList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void addNote() {
        String title = titleField.getText();
        String content = contentArea.getText();
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }
        Note newNote = new Note(-1, title, content);  // ID will be set in Account.addNote
        currentAccount.addNote(newNote);

        JOptionPane.showMessageDialog(this, "Note added successfully.");
        titleField.setText("");
        contentArea.setText("");
        updateNoteList();
    }

    private void removeNote() {
        int selectedIndex = noteList.getSelectedIndex();
        if (selectedIndex != -1) {
            Note selectedNote = currentAccount.getNotesFromDB().get(selectedIndex);
            currentAccount.removeNoteById(selectedNote.getId());

            JOptionPane.showMessageDialog(this, "Note removed successfully.");
            updateNoteList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a note to remove.");
        }
    }

    private void updateNote() {
        int selectedIndex = noteList.getSelectedIndex();
        if (selectedIndex != -1) {
            Note selectedNote = currentAccount.getNotesFromDB().get(selectedIndex);
            String newTitle = titleField.getText();
            String newContent = contentArea.getText();
            if (newTitle.isEmpty() || newContent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields");
                return;
            }

            selectedNote.setTitle(newTitle);
            selectedNote.setContent(newContent);
            currentAccount.updateNoteById(selectedNote);

            JOptionPane.showMessageDialog(this, "Note updated successfully.");
            updateNoteList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a note to update.");
        }
    }

    private void updateNoteList() {
        noteListModel.clear();
        List<Note> notes = currentAccount.getNotesFromDB();
        for (Note note : notes) {
            noteListModel.addElement("Title: " + note.getTitle());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SecureNotesAppGUI().setVisible(true));
    }
}
