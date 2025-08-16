package de.haplo.mytonie.listener;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import de.haplo.mytonie.model.Model;

public abstract class AddListener {

	private Model model;
	
	public AddListener(Model model) {
		this.model = model;
	}
	
	public abstract void addTonie(String id, String name, String image, String audiofile);
	
	public abstract void addAudiofile(String name, String path);
	
	protected byte[] toByteArray(String image) {
		if (StringUtils.isBlank(image)) {
			return null;
		}
		
		try {
			return IOUtils.toByteArray(new File(image).toURI());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void copyAudioToDestination(File audiofile, String audioId) {
		try {
			FileUtils.copyFile(audiofile, new File(model.getAudiofilesDirectory(), audioId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
