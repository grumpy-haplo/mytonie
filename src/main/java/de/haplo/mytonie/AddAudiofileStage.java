package de.haplo.mytonie;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import de.haplo.mytonie.listener.AddListener;
import de.haplo.mytonie.util.DialogUtil;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AddAudiofileStage extends MyStage {
	
	private File selectedAudioFile;
	private TextField nameField;

	private Label selectedAudioLabel;

	private Button okButton;

	private AddListener listener;

	public AddAudiofileStage(AddListener pListener) {
		listener = pListener;

		nameField = new TextField();
		nameField.setOnKeyReleased(e -> updateOkButton());

		Button selectAudioButton = new Button("Choose Audio");

		selectedAudioLabel = new Label("No audio selected");

		selectAudioButton.setOnAction(e -> {
			selectedAudioFile = DialogUtil.openFile(this, "Choose Audio", "Toniebox audio files", "*");
			if (selectedAudioFile != null) {
				selectedAudioLabel.setText("Audio: " + selectedAudioFile.getName());
			}
			updateOkButton();
		});

		okButton = new Button("Ok");
		okButton.setDisable(true);
		okButton.setOnAction(e -> {
			listener.addAudiofile(nameField.getText(), selectedAudioFile.getAbsolutePath());
			hide();
		});

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10));
		grid.setVgap(10);
		grid.setHgap(10);

		grid.add(new Label("Name:"), 0, 0);
		grid.add(nameField, 1, 0);

		grid.add(selectAudioButton, 0, 1);
		grid.add(selectedAudioLabel, 1, 1);

		grid.add(okButton, 0, 2);

		init(grid, "Add Audiofile", 400, 150);
	}


	private void updateOkButton() {
		boolean nameOk = StringUtils.isNotBlank(nameField.getText());
		boolean audioOk = selectedAudioFile != null && selectedAudioFile.exists();
		boolean buttonOk = nameOk && audioOk;
		okButton.setDisable(!buttonOk);
	}
}