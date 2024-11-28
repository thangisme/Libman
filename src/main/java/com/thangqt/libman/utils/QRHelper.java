package com.thangqt.libman.utils;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class QRHelper {

  // Generate QR Code and return as JavaFX Image
  public Image generateQRCode(String text, int width, int height) {
    try {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

      BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
      WritableImage writableImage = new WritableImage(width, height);
      PixelWriter pixelWriter = writableImage.getPixelWriter();

      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          pixelWriter.setArgb(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
        }
      }
      return writableImage;
    } catch (WriterException e) {
      e.printStackTrace();
      return null;
    }
  }

  // Decode QR Code from JavaFX Image
  public String decodeQRCode(Image fxImage) {
    // Convert JavaFX Image to a format suitable for ZXing
    BufferedImage bufferedImage = fromFXImage(fxImage);
    LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

    try {
      Result result = new MultiFormatReader().decode(bitmap);
      return result.getText();
    } catch (NotFoundException e) {
      return null;
    }
  }

  // Helper method to convert JavaFX Image to BufferedImage
  private BufferedImage fromFXImage(Image image) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    javafx.scene.image.PixelReader pixelReader = image.getPixelReader();
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int argb = pixelReader.getArgb(x, y);
        bufferedImage.setRGB(x, y, argb & 0xFFFFFF); // Applying mask for RGB values
      }
    }

    return bufferedImage;
  }
}