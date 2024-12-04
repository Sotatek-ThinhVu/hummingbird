package com.sparrowwallet.hummingbird;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class QRCodeUtils {

    private static final int MAX_FRAGMENT_LENGTH = 100;

    // Generate QR code from a string
    private static BufferedImage generateQRCodeImage(String data) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 170, 170);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    // Split string into smaller parts and create QR code for each part
    private static List<BufferedImage> generateQRCodeImages(UR data, int fragmentLength) throws Exception {
        List<BufferedImage> images = new ArrayList<>();
        UREncoder encoder = new UREncoder(data, MAX_FRAGMENT_LENGTH, fragmentLength, 0);
        while(!encoder.isComplete()) {
            String fragment = encoder.nextPart();
            images.add(generateQRCodeImage(fragment));
        }
        return images;
    }

    // Create animated GIF from QR code images
    private static void createGifFromImages(List<BufferedImage> images, String outputFilePath, int delay) throws Exception {
        AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
        gifEncoder.start(new FileOutputStream(outputFilePath)); // Initialize output file
        gifEncoder.setDelay(delay); // Set transition time between frames (in ms)
        gifEncoder.setRepeat(0); // Set to repeat forever (0 is forever, -1 is no repeat)

        for (BufferedImage image : images) {
            gifEncoder.addFrame(image); // Add each frame
        }
        gifEncoder.finish(); // Finish and save GIF file
    }

    public static void generateQRCode(QRCodeEntity entity){
        try {
            List<BufferedImage> images = generateQRCodeImages(entity.getData(), entity.getFragmentLength());
            createGifFromImages(images, entity.getOutputFilePath(), entity.getInterval());
        } catch (Exception e) {
            System.out.println("Error generating QR code: " + e.getMessage());
        }
    }
}
