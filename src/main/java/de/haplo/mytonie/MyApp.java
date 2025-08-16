package de.haplo.mytonie;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import de.haplo.mytonie.model.Model;
import de.haplo.mytonie.model.box.TonieContent;
import de.haplo.mytonie.model.box.Toniebox;
import de.haplo.mytonie.util.DialogUtil;
import de.haplo.mytonie.util.FxUtil;
import de.haplo.mytonie.util.HashUtil;
import de.haplo.mytonie.util.VersionUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MyApp extends Application {

	private File modelJson;
	private Model model;
	
	private Toniebox toniebox;
	
	private Button openDatabaseButton;
	private Label modelLabel;

	private Button manageToniesButton;
	private Button manageAudiofilesButton;

	private Button manageTonieboxButton;

	@Override
    public void start(Stage stage) {
		VBox layout = new VBox(10);
		
		buildRowOne(stage, layout);
		buildRowTwo(stage, layout);
		buildRowThree(stage, layout);

        stage.setScene(new Scene(layout));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setTitle("MyTonie Library (" + VersionUtil.get() + ")");
        stage.setWidth(500);
        stage.setHeight(200);
        stage.show();
        FxUtil.center(stage);
    }

	
	private void buildRowThree(Stage primaryStage, VBox layout) {
        HBox row = new HBox();

        manageTonieboxButton = new Button("Manage Tonie-Box");
        manageTonieboxButton.setOnAction(e -> actionManageTonieboxButton(primaryStage, model));
        manageTonieboxButton.setDisable(true);
        HBox.setMargin(manageTonieboxButton, new Insets(10,10,10,10));

        row.getChildren().add(manageTonieboxButton);

        layout.getChildren().add(row);

	}


	private void buildRowTwo(Stage primaryStage, VBox layout) {
        HBox row = new HBox();

        manageAudiofilesButton = new Button("Manage Audiofiles");
        manageAudiofilesButton.setOnAction(e -> actionManageAudiofilesButton(model));
        manageAudiofilesButton.setDisable(true);
        HBox.setMargin(manageAudiofilesButton, new Insets(10,10,10,10));

        manageToniesButton = new Button("Manage Tonies");
        manageToniesButton.setOnAction(e -> actionManageToniesButton(model));
        manageToniesButton.setDisable(true);
        HBox.setMargin(manageToniesButton, new Insets(10,10,10,10));
        
        row.getChildren().add(manageAudiofilesButton);
        row.getChildren().add(manageToniesButton);

        layout.getChildren().add(row);
	}

	
	private void buildRowOne(Stage primaryStage, VBox layout) {
		HBox row = new HBox();
        openDatabaseButton = new Button("Open Database");
        openDatabaseButton.setOnAction(e -> actionOpenDatabaseButton(primaryStage));
        HBox.setMargin(openDatabaseButton, new Insets(10,10,10,10));

        modelLabel = new Label("");
        HBox.setMargin(modelLabel, new Insets(10,10,10,10));

        row.getChildren().add(openDatabaseButton);
        row.getChildren().add(modelLabel);

        layout.getChildren().add(row);
	}
	
    private void actionOpenDatabaseButton(Stage stage) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Database");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File selectedDirectory = directoryChooser.showDialog(stage);
        
        if (selectedDirectory == null) {
        	DialogUtil.error("No directory", "The directory does not exists.");
    		return;
        }
        
        modelJson = new File(selectedDirectory, "mytonie.json");
        modelLabel.setText("Model: " + modelJson.getAbsolutePath());
        if (modelJson.exists()) {
        	model = Model.load(modelJson);
        } else {
        	model = new Model();
        	model.setModelFile(modelJson);
        }
        manageToniesButton.setDisable(false);
        manageAudiofilesButton.setDisable(false);
        manageTonieboxButton.setDisable(false);
        openDatabaseButton.setDisable(true);
        
	}
        

	private void actionManageToniesButton(Model pModel) {
		new ManageTonieStage(pModel).showAndCenter();
	}

	private void actionManageAudiofilesButton(Model pModel) {
		new ManageAudiofileStage(pModel).showAndCenter();
	}

	private void actionManageTonieboxButton(Stage stage, Model pModel) {
		File selectedDirectory = DialogUtil.openDirectory(stage, "Open Toniebox root directory");
        
        if (selectedDirectory != null) {
        	toniebox = new Toniebox();
        	//haben wir ein unterordner namens CONTENT dann sind wir im root ordner
        	if (isTonieboxRootDirectory(selectedDirectory)) {
        		toniebox.setContentDirectory(new File(selectedDirectory, "CONTENT"));
        	} else if (isTonieboxContentDirectory(selectedDirectory)) {
        		toniebox.setContentDirectory(selectedDirectory);
        	}
		}
        
        if (toniebox.getContentDirectory() == null) {
        	DialogUtil.error("No Toniebox selected", "The directory is not the root directory or the CONTENT directory of a Toniebox.");
    		return;
        }
		
		BusyDialog.run(stage, "Toniebox is analysed and prepared ...", new Runnable() {
			@Override
			public void run() {
				analyseToniebox(toniebox);
				Platform.runLater(() -> new ManageTonieboxStage(pModel, toniebox).showAndCenter());
			}
		});
		
	}

	private boolean isTonieboxContentDirectory(File directory) {
		return "CONTENT".equals(directory.getName()) 
				&& new File(directory, "00000000").exists() 
				&& new File(directory, "00000001").exists();
	}


	private boolean isTonieboxRootDirectory(File directory) {
    	File contentDirectory = new File(directory, "CONTENT");
    	return contentDirectory.exists() && isTonieboxContentDirectory(contentDirectory);
	}

	private void analyseToniebox(Toniebox toniebox) {
		for (File file : toniebox.getContentDirectory().listFiles()) {
			if ("00000000".equals(file.getName()) || "00000001".equals(file.getName())) {
				continue;
			}
			try {
				prepareContentOnBox(file, toniebox);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	

	
	private void prepareContentOnBox(File directory, Toniebox box) throws IOException {
		File audioFile = Arrays.asList(directory.listFiles((f) -> f.getName().matches("[0-9A-F]{8}"))).stream().findFirst().orElse(null);
		if (audioFile == null) {
			return;
		}
		
		File hashFile = new File(directory, audioFile.getName() + ".hash");
		String hash;
		if (!hashFile.exists()) {
			hash = createHash(audioFile, hashFile);
		} else {
			hash = FileUtils.readFileToString(hashFile, StandardCharsets.UTF_8);
		}
		
		TonieContent content = new TonieContent();
		content.setHash(hash);
		content.setTonieId(directory.getName() + audioFile.getName());
		content.setAudioId(model.getAudiofiles().stream().filter(a -> hash.equals(a.getHash())).findFirst().map(a -> a.getId()).orElse(null));
		box.getContents().add(content);
	}


	private String createHash(File audioFile, File hashFile) throws IOException {
		String hash = HashUtil.buildHash(audioFile);
		FileUtils.writeStringToFile(hashFile, hash, StandardCharsets.UTF_8);
		return hash;
	}

	public static void main(String[] args) {
        launch(args);
    }
	
}
