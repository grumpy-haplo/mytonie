package de.haplo.mytonie;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import de.haplo.mytonie.listener.AddListener;
import de.haplo.mytonie.util.DialogUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class AddTonieStage extends MyStage {
	
	private File selectedImageFile;
	private File selectedAudioFile;
	private TextField idField;
	private TextField nameField;

	private Label selectedAudioLabel;

	private Button okButton;

	private AddListener listener;

	public AddTonieStage(AddListener pListener) {
		listener = pListener;

		Button readFromTonieboxButton = new Button("Read from Toniebox");
		readFromTonieboxButton.setOnAction(e -> actionReadFromTonieboxButton());

		idField = new TextField();
		UnaryOperator<Change> hexFilter = change -> {
		    String newText = change.getControlNewText().toUpperCase();
		    if (newText.matches("[0-9A-F]*") && newText.length() <= 16) {
		        change.setText(change.getText().toUpperCase());
		        return change;
		    }
		    return null;
		};
		idField.setTextFormatter(new TextFormatter<>(hexFilter));
		idField.setOnKeyReleased(e -> updateOkButton());

		nameField = new TextField();
		nameField.setOnKeyReleased(e -> updateOkButton());

		Button selectImageButton = new Button("Choose Image");
		Button selectAudioButton = new Button("Choose Audio");

		ImageView imagePreview = new ImageView();
		imagePreview.setFitWidth(100);
		imagePreview.setFitHeight(100);
		imagePreview.setPreserveRatio(true);

		selectedAudioLabel = new Label("No Audio selected");

		selectImageButton.setOnAction(e -> {
			selectedImageFile = DialogUtil.openFile(this, "Choose Image", "Images", "*.png", "*.jpg", "*.jpeg", "*.gif");
			if (selectedImageFile != null) {
				Image img = new Image(selectedImageFile.toURI().toString());
				imagePreview.setImage(img);
				if (StringUtils.isBlank(nameField.getText())) {
					nameField.setText(FilenameUtils.getBaseName(selectedImageFile.getName()));
				}
			}
			updateOkButton();
		});

		selectAudioButton.setOnAction(e -> {
			selectedAudioFile = DialogUtil.openFile(this, "Choose Audio", getFullScreenExitHint(), "Tonie-Audio-Files", "*");
			if (selectedAudioFile != null) {
				selectedAudioLabel.setText("Audio: " + selectedAudioFile.getName());
			}
			updateOkButton();
		});

		okButton = new Button("Ok");
		okButton.setDisable(true);
		okButton.setOnAction(e -> {
			listener.addTonie(idField.getText(), nameField.getText(),
					Optional.ofNullable(selectedImageFile).map(File::getAbsolutePath).orElse(null),
					selectedAudioFile.getAbsolutePath());
			hide();
		});

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setVgap(10);
		grid.setHgap(10);

		grid.add(readFromTonieboxButton, 0, 0);

		grid.add(new Label("Id:"), 0, 1);
		grid.add(idField, 1, 1);

		grid.add(new Label("Name:"), 0, 2);
		grid.add(nameField, 1, 2);

		grid.add(selectImageButton, 0, 3);
		grid.add(imagePreview, 1, 3);

		grid.add(selectAudioButton, 0, 4);
		grid.add(selectedAudioLabel, 1, 4);

		grid.add(okButton, 0, 5, 2, 1);

		init(grid, "Add Tonie", 600, 350);
	}

	//TODO maybe remove this by a file chooser.
	private void actionReadFromTonieboxButton() {
		File selectedDirectory = DialogUtil.openDirectory(this, "Choose Toniebox audio directory in CONTENT");
		if (selectedDirectory == null) {
			return;
		}

		String dirname = selectedDirectory.getName();
		File[] files = selectedDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// Inside the audio directory there should only be 2 files, the audiofile and audiofile.hash. We only want the audiofile.
				return StringUtils.isBlank(FilenameUtils.getExtension(name));
			}
		});

		if (files.length != 1) {
			return;
		}

		selectedAudioFile = files[0];
		selectedAudioLabel.setText("Audio: " + selectedAudioFile.getName());

		idField.setText(dirname + selectedAudioFile.getName());

		updateOkButton();
	}

	private void updateOkButton() {
		boolean idOk = StringUtils.isNotBlank(idField.getText()) && idField.getText().matches("[0-9A-F]{16}");
		boolean nameOk = StringUtils.isNotBlank(nameField.getText());
		boolean audioOk = selectedAudioFile != null && selectedAudioFile.exists();
		boolean buttonOk = idOk && nameOk && audioOk;
		okButton.setDisable(!buttonOk);
	}
}