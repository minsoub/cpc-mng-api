package com.bithumbsystems.cpc.core.util;

import static com.bithumbsystems.cpc.api.core.util.AES256Util.decryptAES;
import static com.bithumbsystems.cpc.api.core.util.AES256Util.encryptAES;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AES256UtilTest {

  @DisplayName("AES-GCM Password-based Encryption/Decryption")
  @Test
  void aes_gcm_encrypt_decrypt_test() {
    // given
    String outputFormat = "%-30s:%s";
    String password = "This is password";
    String message = "name@example.com";

    // when
    String cipherText = encryptAES(password, message);

    System.out.println("\n------ AES-GCM Password-based Encryption ------");
    System.out.println(String.format(outputFormat, "Input (plain text)", message));
    System.out.println(String.format(outputFormat, "Encrypted (base64)", cipherText));

    String decryptedText = decryptAES(password, cipherText);

    System.out.println("\n------ AES-GCM Password-based Decryption ------");
    System.out.println(String.format(outputFormat, "Input (base64)", cipherText));
    System.out.println(String.format(outputFormat, "Decrypted (plain text)", decryptedText));

    // then
    assertEquals(message, decryptedText);
  }
}
