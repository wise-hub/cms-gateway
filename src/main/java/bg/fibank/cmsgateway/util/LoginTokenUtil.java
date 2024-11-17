package bg.fibank.cmsgateway.util;

/**
 * LoginTokenUtil is a utility class for generating and verifying login tokens.
 *
 * Logic:
 * - Generates a login token by combining an obfuscated prefix derived from the OAuth token and an HMAC hash.
 * - Obfuscation ensures the prefix is transformed for added complexity.
 * - The HMAC hash is generated using a predefined secret key for integrity and security.
 * - Verification checks if a given login token matches the one generated from the OAuth token.
 *
 * Key Details:
 * - Obfuscation applies character shifting based on predefined values.
 * - HMAC uses the "HmacSHA256" algorithm for secure hash generation.
 * - UUID-like input tokens are sanitized to ensure compatibility.
 * - Example input:
 *   - UUID: "a93a7d3f-049d-4cda-a33b-7d76dfc1a726"
 *   - Generated Login Token: "iaz1j608.hQARI7XdJVMfT0oIugsPx1Mwd41JV_w-ttigYH-66wg"
 */
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class LoginTokenUtil {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final byte[] BASE_LOCK_STRING = "8PjK7zWcHhjK7qcAe".getBytes();
    private static final int OBFUSCATION_LENGTH = 8;
    private static final int[] OBFUSCATION_SHIFTS = {83, 47, 75, 30, 61, 29, 58, 12};

    // Generates an HMAC hash of the input string using a predefined key and "HmacSHA256".
    private static String generateHash(String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(BASE_LOCK_STRING, "HmacSHA256"));
            return BASE64_ENCODER.encodeToString(hmac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Error generating HMAC hash", e);
        }
    }

    // Obfuscates the input string by shifting characters based on predefined values.
    private static String obfuscateString(String input) {
        StringBuilder obfuscated = new StringBuilder(OBFUSCATION_LENGTH);
        for (int i = 0; i < OBFUSCATION_LENGTH; i++) {
            char originalChar = input.charAt(i);
            int shift = OBFUSCATION_SHIFTS[i];
            char obfuscatedChar = shiftChar(originalChar, shift);
            obfuscated.append(obfuscatedChar);
        }
        return obfuscated.toString();
    }

    // Shifts a character by the given value, handling digits and letters differently.
    private static char shiftChar(char c, int shift) {
        if (Character.isDigit(c)) {
            return (char) ('0' + (c - '0' + shift) % 10);
        } else if (Character.isLetter(c)) {
            int base = Character.isUpperCase(c) ? 'A' : 'a';
            return (char) (base + (c - base + shift) % 26);
        } else {
            throw new IllegalArgumentException("Unsupported character: " + c);
        }
    }

    /**
     * Generates a login token.
     * - Obfuscates the sanitized OAuth token's prefix.
     * - Appends the HMAC hash of the OAuth token.
     * @param oauthToken The input OAuth token.
     * @return The generated login token.
     */
    public static String generateLoginToken(String oauthToken) {
        String sanitizedToken = oauthToken.replaceAll("[^a-zA-Z0-9]", "");

        StringBuilder prefixBuilder = new StringBuilder(sanitizedToken);
        while (prefixBuilder.length() < OBFUSCATION_LENGTH) {
            prefixBuilder.append("J");
        }

        String prefix = prefixBuilder.substring(Math.max(0, prefixBuilder.length() - OBFUSCATION_LENGTH));

        return obfuscateString(prefix) + "." + generateHash(oauthToken);
    }

    /**
     * Verifies if a given login token matches the one generated from the OAuth token.
     * @param softToken The provided login token.
     * @param oauthToken The original OAuth token.
     * @return True if the tokens match, false otherwise.
     */
    public static boolean verifyLoginToken(String softToken, String oauthToken) {
        if (softToken == null || oauthToken == null) {
            throw new IllegalArgumentException("SoftToken or OAuthToken cannot be null");
        }
        return softToken.equals(generateLoginToken(oauthToken));
    }

}
