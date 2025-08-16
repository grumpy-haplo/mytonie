package de.haplo.mytonie.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.codec.digest.DigestUtils;

public final class HashUtil {

	private HashUtil() {
	}
	
	public static String buildHash(File audiofile) {
		try {
			return audiofile.length() + "_" + DigestUtils.sha256Hex(new FileInputStream(audiofile));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
}
