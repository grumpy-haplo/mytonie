package de.haplo.mytonie;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import de.haplo.mytonie.model.Audiofile;
import de.haplo.mytonie.model.Model;
import de.haplo.mytonie.model.Tonie;
import de.haplo.mytonie.model.actions.Action;
import de.haplo.mytonie.model.actions.ActionType;
import de.haplo.mytonie.model.box.TonieContent;
import de.haplo.mytonie.model.box.Toniebox;
import de.haplo.mytonie.util.TonieUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ManageTonieboxStage extends MyStage {

	private ListView<TonieContent> tonieContentListView;
	private ListView<Action> actionListView;
	private ComboBox<String> tonieCombo;
	private ComboBox<String> audiofileCombo;
	private Button okButton;
	
	
	private Model model;
	private Toniebox toniebox;
	
	private List<Action> actions = new ArrayList<>();
	private ObservableList<Action> actionListViewModel = FXCollections.observableList(actions);
	
	public ManageTonieboxStage(Model model, Toniebox toniebox) {
        this.model = model;
        this.toniebox = toniebox;
		
		initUi();
	}

	private void initUi() {
        getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
		setTitle("Manage Toniebox");
        
        VBox root = new VBox(10);

        // Oben: Remove-Button
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> addActionRemoveEntry());

        // Mittig: ListView (Bild + Text)
        tonieContentListView = initTonieboxList();

        // Rechts daneben: Zwei ComboBoxen und Add-Button
        VBox rightControls = initAddPanel();
        
        //obere beiden elemente
        HBox listAndControls = new HBox(10, tonieContentListView, rightControls);
        listAndControls.setAlignment(Pos.CENTER_LEFT);

        // Darunter: Text-Liste
        actionListView = new ListView<>(actionListViewModel);
        actionListView.setPrefHeight(80);

        // Unten: OK-Button
        okButton = new Button("Ok");
        okButton.setDisable(true);
        okButton.setOnAction(e -> actionOkButton());

        // Alles zum root hinzfügen
        root.getChildren().addAll(removeButton, listAndControls, actionListView, okButton);
        root.setPadding(new Insets(10));
        
        init(root, "Manage Toniebox", 600, 620);
	}

	private void actionOkButton() {
		BusyDialog.run(this, "Actions will be performed...", new Runnable() {
			@Override
			public void run() {
				performActions();
				Platform.runLater(() -> close());
			}


		});
	}

	
	private void performActions() {
		for (Action action : actions) {
			if (ActionType.ADD.equals(action.getType())) {
				try {
					performAddAction(action);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	//TODO testen!
	private void performAddAction(Action action) throws IOException {
		File dataDir = new File(toniebox.getContentDirectory(), TonieUtil.directoryPart(action.getTonieId()));
		File dstAudioFile = new File(dataDir, TonieUtil.filePart(action.getTonieId()));
		File dstHashFile = new File(dataDir, TonieUtil.filePart(action.getTonieId()) + ".hash");
		
		dstAudioFile.mkdirs();
		
		Audiofile mAudiofile = getAudiofile(action.getAudioId());
		File srcAudioFile = new File(model.getAudiofilesDirectory(), mAudiofile.getId());
		
		FileUtils.copyFile(srcAudioFile, dstAudioFile);
		FileUtils.writeStringToFile(dstHashFile, mAudiofile.getHash(), StandardCharsets.UTF_8);
	}

	
	private void addActionRemoveEntry() {
		TonieContent selectedTonieContent = tonieContentListView.getSelectionModel().getSelectedItem();
		if (selectedTonieContent == null) {
			return;
		}
		
		Action action = Action.builder().type(ActionType.REMOVE).tonieId(selectedTonieContent.getTonieId()).build();
		if (!actionListViewModel.contains(action)) {
			actionListViewModel.add(action);
			okButton.setDisable(false);
		}
		
	}

	private void addActionAddEntry() {
		String tonieItem = tonieCombo.getSelectionModel().getSelectedItem();
		String audioItem = audiofileCombo.getSelectionModel().getSelectedItem();
		
		if (tonieItem == null || audioItem == null) {
			return;
		}

		String tonieId = tonieItem.substring(tonieItem.indexOf("[") + 1, tonieItem.indexOf("]"));  
		String audioId = audioItem.substring(audioItem.indexOf("[") + 1, audioItem.indexOf("]"));  
		
		if (actionListViewModel.stream().filter(a -> ActionType.ADD.equals(a.getType()) && a.getTonieId().equals(tonieId)).findFirst().orElse(null) != null) {
			//es gibt schon eine add action für den tonie.
			return;
		}
		
		actionListViewModel.add(Action.builder().type(ActionType.ADD).tonieId(tonieId).audioId(audioId).build());
		okButton.setDisable(false);
	}

	
	private VBox initAddPanel() {
		VBox rightControls = new VBox(10);
        rightControls.setAlignment(Pos.TOP_CENTER);

        HBox row1 = new HBox(10);
        tonieCombo = new ComboBox<>();
        tonieCombo.getItems().addAll(model.getTonies().stream().map(t -> "["+t.getId()+"] " + t.getName()).toList());
        row1.getChildren().addAll(new Label("Tonie: "), tonieCombo);

        HBox row2 = new HBox(10);
        audiofileCombo = new ComboBox<>();
        audiofileCombo.getItems().addAll(model.getAudiofiles().stream().map(a -> a.getName()+" ["+a.getId()+"]").toList());
        row2.getChildren().addAll(new Label("Audio: "), audiofileCombo);

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> addActionAddEntry());

        rightControls.getChildren().addAll(row1, row2, addButton);
		return rightControls;
	}

	private ListView<TonieContent> initTonieboxList() {
		ListView<TonieContent> listView = new ListView<>();
        listView.getItems().addAll(toniebox.getContents());

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(TonieContent item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10);
                    box.setAlignment(Pos.CENTER_LEFT);

                    Tonie tonie = getTonie(item.getTonieId());
                    Audiofile audiofile = getAudiofile(item.getAudioId());

                    ImageView imageView = initImageView(tonie);
                    Label text = initLabel(item, tonie, audiofile);

                    box.getChildren().addAll(imageView, text);
                    setGraphic(box);
                }
            }

			private Label initLabel(TonieContent item, Tonie tonie, Audiofile audiofile) {
				StringBuilder builder = new StringBuilder();
				builder.append("[").append(item.getTonieId()).append("] ");
				
				if (tonie != null) {
					builder.append(tonie.getName()).append(" ");
				}
				//audio
				builder.append("[");
				if (audiofile != null) {
					builder.append(audiofile.getName()).append("; ");
				}
				builder.append(item.getAudioId());
				builder.append("]");
				
				return new Label(builder.toString());
			}

			private ImageView initImageView(Tonie tonie) {
				ImageView imageView = new ImageView();
				imageView.setFitWidth(30);
				imageView.setFitHeight(30);
                
				if (tonie != null && tonie.getImage() != null && tonie.getImage().length > 0) {
					imageView.setImage(new Image(new ByteArrayInputStream(tonie.getImage())));
				} else {
					imageView.setImage(new Image(getClass().getResourceAsStream("/no-image-found.jpg")));
				}
				return imageView;
			}
        });
		return listView;
	}
	
	private Tonie getTonie(String tonieId) {
		Optional<Tonie> oTonie = model.getTonies().stream().filter(t -> t.getId().equals(tonieId)).findFirst();
		if (oTonie.isPresent()) {
			return oTonie.get();
		}else {
			return null;
		}
	}

	private Audiofile getAudiofile(String id) {
		Optional<Audiofile> oAudiofile = model.getAudiofiles().stream().filter(a -> a.getId().equals(id)).findFirst();
		if (oAudiofile.isPresent()) {
			return oAudiofile.get();
		}else {
			return null;
		}
	}


}