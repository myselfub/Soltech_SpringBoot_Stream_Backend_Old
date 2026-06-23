package kr.co.soltech.stream.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

public class CreateImgTest {
	@Test
	public void createImgTest() throws Exception {
		String name = "[USER_NAME]";
		int width = 100;
		int height = 46;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2d = image.createGraphics();
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2d.setColor(new Color(255, 255, 255, 0));
		graphics2d.fillRect(0, 0, width, height);

		// 바깥선
		graphics2d.setColor(Color.decode("#FF0000"));
		graphics2d.setStroke(new BasicStroke(2));
		int padding = 4;
		graphics2d.drawRect(padding, padding, width - (padding * 2), height - (padding * 2));

		// 안쪽선
		graphics2d.setColor(Color.decode("#FF0000"));
		graphics2d.setStroke(new BasicStroke(2));
		graphics2d.drawRect(padding * 2, padding * 2, width - (padding * 4), height - (padding * 4));

		graphics2d.setColor(Color.decode("#FF0000"));
		graphics2d.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		FontMetrics fontMetrics = graphics2d.getFontMetrics();
		int textWidth = fontMetrics.stringWidth(name);
		int textHeight = fontMetrics.getHeight();

		int textX = (width - textWidth) / 2;
		int textY = ((height - 1) / 2) + (textHeight / 3);

		graphics2d.drawString(name, textX, textY);
		graphics2d.dispose();

		File file = new File("sign.png");
		try {
			ImageIO.write(image, "PNG", file);
			System.out.println(encodeFileToBase64(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(encodeImageToBase64(image));
	}

	private String encodeFileToBase64(File file) throws IOException {
		byte[] fileBytes = new byte[(int) file.length()];
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			fileInputStream.read(fileBytes);
		}

		return Base64.getEncoder().encodeToString(fileBytes);
	}

	private String encodeImageToBase64(BufferedImage bufferedImage) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
		byte[] imageBytes = byteArrayOutputStream.toByteArray();
		return Base64.getEncoder().encodeToString(imageBytes);
	}
}
