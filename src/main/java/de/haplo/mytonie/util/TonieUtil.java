package de.haplo.mytonie.util;

public final class TonieUtil {

	private TonieUtil() {
	}
	
	public static String directoryPart(String tonieId) {
		return tonieId.substring(0, 8);
	}

	public static String filePart(String tonieId) {
		return tonieId.substring(8);
	}

	
}
