package de.haplo.mytonie.model.box;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Toniebox {

	private File contentDirectory;
	private List<TonieContent> contents = new ArrayList<TonieContent>();
	
}
