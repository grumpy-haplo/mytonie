package de.haplo.mytonie.model;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import lombok.Data;

@Data
public class Model {

	private File modelFile;
	private File audiofilesDirectory;
	private File mytonieDirectory;

	private List<Tonie> tonies = new ArrayList<>();
	private List<Audiofile> audiofiles = new ArrayList<>();

	public static void save(Model model) {
		try {
			Gson gson = createGson();
			String json = gson.toJson(model);
			FileUtils.writeStringToFile(model.getModelFile(), json, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Model load(File file) {
		Gson gson = createGson();

		try (FileReader reader = new FileReader(file)) {
			Model model = gson.fromJson(reader, Model.class);
			model.setModelFile(file);
			model.setMytonieDirectory(model.getModelFile().getParentFile());
			model.setAudiofilesDirectory(new File(model.getMytonieDirectory(), "mytonie-audiofiles"));
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public File getAudiofilesDirectory() {
		if (!audiofilesDirectory.exists()) {
			audiofilesDirectory.mkdirs();
		}

		return audiofilesDirectory;
	}

	private static Gson createGson() {
		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(byte[].class, (JsonSerializer<byte[]>) (src, typeOfSrc,
				context) -> new JsonPrimitive(Base64.getEncoder().encodeToString(src)));
		builder.registerTypeAdapter(byte[].class,
				(JsonDeserializer<byte[]>) (json, typeOfT, context) -> Base64.getDecoder().decode(json.getAsString()));

		builder.registerTypeAdapter(File.class,
				(JsonSerializer<File>) (src, typeOfSrc, context) -> new JsonPrimitive(src.getAbsolutePath()));
		builder.registerTypeAdapter(File.class,
				(JsonDeserializer<File>) (json, typeOfT, context) -> new File(json.getAsString()));

		return builder.create();
	}

}
