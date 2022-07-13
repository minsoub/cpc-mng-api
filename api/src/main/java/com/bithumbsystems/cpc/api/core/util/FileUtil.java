package com.bithumbsystems.cpc.api.core.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import org.springframework.core.io.ClassPathResource;

public class FileUtil {

  public static String readResourceFile(String path) throws IOException {
    ClassPathResource resources = new ClassPathResource(path);
    try (InputStream inputStream = resources.getInputStream()) {
      String string = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      System.out.println(string);
      return string;
    }
  }

  public static boolean isImage(File img) {
    try {
      return isImage(ImageIO.read(img));
    } catch (IOException e) {
      return false;
    }
  }

  public static boolean isImage(Image img) {
    return img != null && img.getWidth(null) > -1 && img.getHeight(null) > -1;
  }

  public static final boolean isImage(InputStream is) {
    boolean ret = false;

    try {
      BufferedImage bufferedImage = ImageIO.read(is);
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      if (width == 0 || height == 0) {
        ret = false;
      } else {
        ret = true;
      }
    } catch (IOException e) {
      ret = false;
    } catch (Exception e) {
      ret = false;
    }

    return ret;
  }

  public static final String getExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }
}
