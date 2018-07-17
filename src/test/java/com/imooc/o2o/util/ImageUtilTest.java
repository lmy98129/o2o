package com.imooc.o2o.util; 

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class ImageUtilTest {
	private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("%20", " ");
	// java无法识别Windows传来进程所在文件路径信息中的“%20”空格字符，所以在这里做一个转换
	public static void main(String[] args) throws IOException {
		Thumbnails.of("C:\\Users\\Blean\\a.jpg").size(200, 200)
				//加入水印 0.25f是透明度  outputQuality(0.8f)是压缩比
				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "watermark.jpg")), 0.25f)
				.outputQuality(0.8f).toFile("C:\\Users\\Blean\\newa.jpg");
	}
}
 