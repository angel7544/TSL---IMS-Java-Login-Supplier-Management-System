package auth;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static final String USER_FILE = "users.dat";
    private Map<String, User> users = new HashMap<>();
    private User currentUser = null;
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";


    public AuthService() {
        loadUsers();

        // Create default admin if no users exist
        if (users.isEmpty()) {
            try {
                createUser(DEFAULT_USERNAME, DEFAULT_PASSWORD, "ADMIN");
                saveUsers();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
            currentUser = user;
            return true;
        }
        
        // Debug information
        System.out.println("Login failed for username: " + username);
        if (user == null) {
            System.out.println("User not found in database");
        } else {
            System.out.println("Password hash mismatch");
        }
        
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void createUser(String username, String password, String role) throws Exception {
        if (users.containsKey(username)) {
            throw new Exception("User already exists");
        }

        User newUser = new User(username, hashPassword(password), role);
        users.put(username, newUser);
        saveUsers();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback (not secure)
        }
    }

    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (Map<String, User>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
                users = new HashMap<>();
            }
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}