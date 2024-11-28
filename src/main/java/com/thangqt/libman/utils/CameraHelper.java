package com.thangqt.libman.utils;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.nio.ByteBuffer;

public class CameraHelper {
  static {
    OpenCV.loadLocally();
  }

  private VideoCapture capture;
  private Mat frame;
  private int width;
  private int height;

  public CameraHelper(int width, int height) {
    this.width = width;
    this.height = height;
    this.capture = new VideoCapture(0); // Use default camera
    this.frame = new Mat();

    // Open the camera and set the desired frame width and height
    if (capture.isOpened()) {
      capture.set(Videoio.CAP_PROP_FRAME_WIDTH, width);
      capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, height);
    }
  }

  public boolean isCameraOpen() {
    return capture.isOpened();
  }

  public Image captureFrame() {
    if (isCameraOpen() && capture.read(frame) && !frame.empty()) {
      return matToJavaFXImage(frame);
    }
    return null;
  }

  private Image matToJavaFXImage(Mat mat) {
    org.opencv.imgproc.Imgproc.cvtColor(mat, mat, org.opencv.imgproc.Imgproc.COLOR_BGR2RGB);

    int bufferSize = mat.width() * mat.height() * (int) mat.elemSize();
    byte[] b = new byte[bufferSize];
    mat.get(0, 0, b);

    WritableImage writableImage = new WritableImage(mat.width(), mat.height());
    PixelWriter pixelWriter = writableImage.getPixelWriter();

    ByteBuffer buffer = ByteBuffer.wrap(b);
    for (int y = 0; y < mat.height(); y++) {
      for (int x = 0; x < mat.width(); x++) {
        int r = Byte.toUnsignedInt(buffer.get());
        int g = Byte.toUnsignedInt(buffer.get());
        int bValue = Byte.toUnsignedInt(buffer.get());
        int pixel = (r << 16) | (g << 8) | bValue;
        pixelWriter.setArgb(x, y, 0xFF000000 | pixel);
      }
    }

    return writableImage;
  }

  public void releaseCamera() {
    if (isCameraOpen()) {
      capture.release();
    }
  }
}
