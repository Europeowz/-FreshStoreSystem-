package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * SHA-256 密码哈希工具
 * 使用随机盐值 + SHA-256 迭代哈希，存储格式：salt$hash
 */
public class HashUtil {

    private static final int ITERATIONS = 10000;
    private static final int SALT_BYTES = 16;

    /** 生成 salt$hash 格式的密码密文 */
    public static String hash(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_BYTES];
            random.nextBytes(salt);

            byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS);
            return Base64.getEncoder().encodeToString(salt)
                   + "$"
                   + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hash failed", e);
        }
    }

    /** 验证密码是否匹配 */
    public static boolean verify(String password, String storedHash) {
        try {
            String[] parts = storedHash.split("\\$");
            if (parts.length != 2) return false;
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            byte[] actualHash = pbkdf2(password.toCharArray(), salt, ITERATIONS);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations)
            throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        // Simple iterative hash: H(salt + password) repeated
        md.update(salt);
        byte[] hash = md.digest(new String(password).getBytes("UTF-8"));
        for (int i = 1; i < iterations; i++) {
            md.reset();
            md.update(hash);
            hash = md.digest();
        }
        return hash;
    }
}
