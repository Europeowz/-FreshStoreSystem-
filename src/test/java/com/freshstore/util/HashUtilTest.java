package com.freshstore.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HashUtilTest {

    @Test
    void hash_returnsNonEmptyString() {
        assertThat(HashUtil.hash("password")).isNotEmpty();
    }

    @Test
    void verify_correctPassword_returnsTrue() {
        String hash = HashUtil.hash("mypassword");
        assertThat(HashUtil.verify("mypassword", hash)).isTrue();
    }

    @Test
    void verify_wrongPassword_returnsFalse() {
        String hash = HashUtil.hash("correct");
        assertThat(HashUtil.verify("wrong", hash)).isFalse();
    }

    @Test
    void verify_invalidHashFormat_returnsFalse() {
        assertThat(HashUtil.verify("pwd", "not-a-valid-hash")).isFalse();
    }

    @Test
    void verify_nullHash_returnsFalse() {
        assertThat(HashUtil.verify("pwd", null)).isFalse();
    }

    @Test
    void verify_emptyHash_returnsFalse() {
        assertThat(HashUtil.verify("pwd", "")).isFalse();
    }

    @Test
    void samePassword_differentCalls_producesDifferentHashes() {
        String h1 = HashUtil.hash("same");
        String h2 = HashUtil.hash("same");
        assertThat(h1).isNotEqualTo(h2);
    }

    @Test
    void hash_emptyPassword() {
        String hash = HashUtil.hash("");
        assertThat(hash).isNotEmpty();
        assertThat(HashUtil.verify("", hash)).isTrue();
    }
}
