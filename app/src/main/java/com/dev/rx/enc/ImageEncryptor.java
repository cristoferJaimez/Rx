package com.dev.rx.enc;

import javax.crypto.*;
import javax.crypto.spec.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;

public class ImageEncryptor {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;

    public static void encryptImage(String inputImagePath, String outputImagePath, SecretKey secretKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException {
        // Leer el archivo de entrada en un array de bytes
        byte[] inputBytes = Files.readAllBytes(Paths.get(inputImagePath));

        // Crear un cifrado AES en modo CBC con relleno PKCS5Padding
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Cifrar los datos de la imagen
        byte[] encryptedBytes = cipher.doFinal(inputBytes);

        // Escribir los datos cifrados en un archivo de salida
        FileOutputStream outputStream = new FileOutputStream(outputImagePath);
        outputStream.write(encryptedBytes);
        outputStream.close();
    }

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        // Crear una clave secreta aleatoria de 128 bits
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(KEY_SIZE);
        return keyGenerator.generateKey();
    }
}
