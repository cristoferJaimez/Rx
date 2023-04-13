package com.dev.rx.enc;

import javax.crypto.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;

public class ImageDecryptor {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128;

    public static void decryptImage(String inputImagePath, String outputImagePath, SecretKey secretKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException {
        // Leer el archivo de entrada en un array de bytes
        byte[] inputBytes = Files.readAllBytes(Paths.get(inputImagePath));

        // Crear un cifrado AES en modo CBC con relleno PKCS5Padding
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Descifrar los datos de la imagen
        byte[] decryptedBytes = cipher.doFinal(inputBytes);

        // Escribir los datos descifrados en un archivo de salida
        FileOutputStream outputStream = new FileOutputStream(outputImagePath);
        outputStream.write(decryptedBytes);
        outputStream.close();
    }
}
