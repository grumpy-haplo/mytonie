package de.haplo.mytonie.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public final class VersionUtil {

	public static String get() {
		try {
			return IOUtils.resourceToString("/version.properties", StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "unknown";
	}
	
}
