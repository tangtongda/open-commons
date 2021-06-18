package com.tangtongda.open.commons.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.EnumMap;
import java.util.UUID;

/**
 * {@link QRCodeUtil} google qr code generate kit
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2020/12/11
 */
public class QRCodeUtil {

  private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

  private QRCodeUtil() {}

  private static final String PATH_SEPARATOR = "/";
  private static final String CHARSET = "utf-8";
  private static final String FORMAT_NAME = "png";
  private static final String IMAGE_FORMAT = ".png";
  /** QRCODE size */
  private static final int QRCODE_SIZE = 300;
  /** LOGO width */
  private static final int WIDTH = 64;
  /** LOGO height */
  private static final int HEIGHT = 64;

  /**
   * generate QR code with central logo
   *
   * @param content content
   * @param logoPath logo local path
   * @param destPath QR code destination path
   * @param logoCompress compress logo size
   * @throws IOException,WriterException exception
   * @return QR Code file path
   */
  public static String createQRCodeLocal(
      @NotNull String content, String logoPath, @NotNull String destPath, boolean logoCompress)
      throws IOException, WriterException {
    BufferedImage image = createBufferedImage(content, logoPath, logoCompress);
    mkdirs(destPath);
    // generate random file name
    String file = UUID.randomUUID() + IMAGE_FORMAT;
    ImageIO.write(image, FORMAT_NAME, new File(destPath + PATH_SEPARATOR + file));
    return destPath + file;
  }

  /**
   * generate QR code input stream
   *
   * @param content content
   * @param logoPath logo local path
   * @param logoCompress compress logo size
   * @throws IOException,WriterException exception
   */
  public static InputStream createQRCodeInputStream(
      String content, String logoPath, boolean logoCompress) throws IOException, WriterException {
    BufferedImage image = createBufferedImage(content, logoPath, logoCompress);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, FORMAT_NAME, os);
      return new ByteArrayInputStream(os.toByteArray());
    } catch (IOException e) {
      logger.error("image write error", e);
    }
    return null;
  }

  /**
   * generate QR code *
   *
   * @param content content
   * @param logoPath logo local path
   * @param logoCompress compress logo size
   * @throws WriterException,IOException exception
   * @return BufferedImage
   */
  private static BufferedImage createBufferedImage(
      String content, String logoPath, boolean logoCompress) throws WriterException, IOException {
    EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
    hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
    hints.put(EncodeHintType.MARGIN, 1);
    BitMatrix bitMatrix =
        new MultiFormatWriter()
            .encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
    int width = bitMatrix.getWidth();
    int height = bitMatrix.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
      }
    }
    if (StringUtils.isBlank(logoPath)) {
      return image;
    }
    // 插入图片
    insertLogo(image, logoPath, logoCompress);
    return image;
  }

  /**
   * insert logo
   *
   * @param source QR code BufferedImage
   * @param imgPath logo path
   * @param logoCompress compress logo size
   * @throws IOException exception
   */
  private static void insertLogo(BufferedImage source, String imgPath, boolean logoCompress)
      throws IOException {
    File file = new File(imgPath);
    if (!file.exists()) {
      logger.error("logo image is not exist,imgPath:{}", imgPath);
      return;
    }
    Image src = ImageIO.read(new File(imgPath));
    if (null == src) {
      logger.error("logo image cannot read,imgPath:{}", imgPath);
      return;
    }
    int width = src.getWidth(null);
    int height = src.getHeight(null);
    if (logoCompress) {
      if (width > WIDTH) {
        width = WIDTH;
      }
      if (height > HEIGHT) {
        height = HEIGHT;
      }
      Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      Graphics g = tag.getGraphics();
      g.drawImage(image, 0, 0, null); // 绘制缩小后的图
      g.dispose();
      src = image;
    }
    // use Graphics2D to draw logo
    Graphics2D graph = source.createGraphics();
    // anti-aliasing
    graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    int x = (QRCODE_SIZE - width) / 2;
    int y = (QRCODE_SIZE - height) / 2;
    graph.drawImage(src, x, y, width, height, null);
    Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
    graph.setStroke(new BasicStroke(3f));
    graph.draw(shape);
    graph.dispose();
  }

  /**
   * create folder if not exist
   *
   * @author lanyuan Email: mmm333zzz520@163.com
   * @param destPath destination path
   */
  private static void mkdirs(String destPath) {
    File file = new File(destPath);
    if (!file.exists() && !file.isDirectory()) {
      boolean mkdirs = file.mkdirs();
      logger.info("create dictionary result:{}", mkdirs);
    }
  }
}
