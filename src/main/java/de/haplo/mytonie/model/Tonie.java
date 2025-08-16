package de.haplo.mytonie.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tonie {

	private String id;
	private String name;
	private byte[] image;
	private String audiofileId;
	
}
