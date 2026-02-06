package br.com.bscash.efinanceira.infrastructure.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;

public class Criptografia {

    private static final Logger logger = LoggerFactory.getLogger(Criptografia.class);

    private Cipher embaralhar;
    private Cipher desembaralhar;

    public Criptografia() {
        this("9XD8L3aL4J");
    }

    public Criptografia(String passPhrase) {
        try {
            byte[] salt = { -87, -101, -57, 51, 85, 54, -28, 2 };
            int iterationCount = 27;

            java.security.spec.KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);

            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            embaralhar = Cipher.getInstance(key.getAlgorithm());
            desembaralhar = Cipher.getInstance(key.getAlgorithm());

            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            embaralhar.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            desembaralhar.init(Cipher.DECRYPT_MODE, key, paramSpec);
        } catch (GeneralSecurityException e) {
            logger.error("Erro de segurança ao inicializar Criptografia: {}", e.getMessage(), e);
            throw new IllegalStateException("Falha ao inicializar Criptografia. Verifique a configuração de segurança.", e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao inicializar Criptografia: {}", e.getMessage(), e);
            throw new IllegalStateException("Falha inesperada ao inicializar Criptografia.", e);
        }
    }

    public String encrypt(String str) {
        try {
            byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
            byte[] enc = embaralhar.doFinal(utf8);
            return (new String(new Base64().encode(enc)));
        } catch (GeneralSecurityException e) {
            logger.error("Erro de segurança ao criptografar string: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao criptografar string: {}", e.getMessage(), e);
        }
        return null;
    }

    public String decrypt(String str) {
        try {
            byte[] dec = (new Base64().decode(str.getBytes()));
            byte[] utf8 = desembaralhar.doFinal(dec);
            return new String(utf8, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            logger.error("Erro de segurança ao descriptografar string: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao descriptografar string: {}", e.getMessage(), e);
        }
        return null;
    }
}
