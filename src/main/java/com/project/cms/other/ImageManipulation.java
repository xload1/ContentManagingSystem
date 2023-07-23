package com.project.cms.other;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageManipulation {
    // Method to stretch an image to a fixed resolution
    public static byte[] stretchImage(byte[] imageData, int targetWidth, int targetHeight) throws IOException {
        // Convert the byte[] image data to a BufferedImage
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage originalImage = ImageIO.read(bais);
        bais.close();

        // Create a new BufferedImage with the desired resolution
        BufferedImage stretchedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());

        // Draw the original image onto the new image with the desired resolution
        Graphics2D graphics = stretchedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics.dispose();

        // Save the stretched image back to a byte[] format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(stretchedImage, "jpg", baos);
        baos.flush();
        byte[] stretchedImageData = baos.toByteArray();
        baos.close();

        return stretchedImageData;
    }
}