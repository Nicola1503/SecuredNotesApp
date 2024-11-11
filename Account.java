package NotesApp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private final String username;
    private final String passwordHash;

    public Account(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    // Add a new note
    public void addNote(Note note) {
        String insertNoteQuery = "INSERT INTO Notes (username, title, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertNoteQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, note.getTitle());
            stmt.setString(3, note.getContent());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all notes for this user
    public List<Note> getNotesFromDB() {
        List<Note> notes = new ArrayList<>();
        String selectNotesQuery = "SELECT id, title, content FROM Notes WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectNotesQuery)) {
            stmt.setString(1, this.username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                notes.add(new Note(id, title, content));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public void removeNoteById(int noteId) {
        String deleteNoteQuery = "DELETE FROM Notes WHERE id = ? AND username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteNoteQuery)) {
            stmt.setInt(1, noteId);
            stmt.setString(2, this.username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateNoteById(Note note) {
        String updateNoteQuery = "UPDATE Notes SET title = ?, content = ? WHERE id = ? AND username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateNoteQuery)) {
            stmt.setString(1, note.getTitle());
            stmt.setString(2, note.getContent());
            stmt.setInt(3, note.getId());
            stmt.setString(4, this.username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
