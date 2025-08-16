package de.haplo.mytonie.model;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Audiofile {

	private String name;
	private String id;
	private String hash;

	public static String newHash() {
		return UUID.randomUUID().toString().substring(0, 6);
	}
	
}
