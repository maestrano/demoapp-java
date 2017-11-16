package com.example;

import java.io.IOException;
import java.io.InputStream;

import com.github.lalyos.jfiglet.FigletFont;

public class BigStringUtils {

	private static final FigletFont font = initFont();

	public static String render(String s) {
		try {
			return font.convert(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static FigletFont initFont() {
		try {
			InputStream is = BigStringUtils.class.getResourceAsStream("/big.flf");
			return new FigletFont(is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
