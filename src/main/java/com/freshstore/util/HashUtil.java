package com.freshstore.util;

import org.mindrot.jbcrypt.BCrypt;

/** Password hashing via bcrypt. */
public final class HashUtil {
    private HashUtil() {}

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean verify(String password, String storedHash) {
        try {
            return BCrypt.checkpw(password, storedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
