package client.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * To store Credentials on %temp% (System temp directory) para sa Remember me
 */
public class CredentialManager {
    private static final String CREDENTIAL_FILE = "sys_cache.dat";

    /**
     * Save credentials to temp directory
     * @param username Username of the user
     * @param password Password of the user
     * @return Boolean
     */
    public static boolean saveCredentials(String username, String password) {
        try {
            String data = username + "|#|" + password;

            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path credFile = tempDir.resolve(CREDENTIAL_FILE);

            Files.writeString(credFile, data);

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                try {
                    Files.setAttribute(credFile, "dos:hidden", true);
                } catch (Exception ignored) {}
            }

            return true;
        } catch (Exception e) {
            System.err.println("Failed to save credentials: " + e.getMessage());
            return false;
        }
    }

    /**
     * Load credentials from %temp% (System temp directory) directory
     * @return String Array [username, password] or null if not found/invalid
     */
    public static String[] loadCredentials() {
        try {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path credFile = tempDir.resolve(CREDENTIAL_FILE);

            if (!Files.exists(credFile)) {
                return null;
            }

            String data = new String(Files.readAllBytes(credFile), StandardCharsets.UTF_8);

            if (data.contains("|#|")) {
                String[] parts = data.split("\\|#\\|");
                if (parts.length == 2) {
                    return parts; // [username, password]
                }
            }else{
                deleteCredentials();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Failed to load credentials: " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete stored credentials from %temp% (System temp directory)
     * @return Boolean
     */
    public static boolean deleteCredentials() {
        try {
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path credFile = tempDir.resolve(CREDENTIAL_FILE);

            if (Files.exists(credFile)) {
                Files.delete(credFile);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete credentials: " + e.getMessage());
            return false;
        }
    }
}
