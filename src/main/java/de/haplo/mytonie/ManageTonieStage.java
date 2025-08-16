package de.haplo.mytonie;
import java.io.ByteArrayInputStream;
import java.io.File;

import org.apache.commons.io.FileUtils;

import de.haplo.mytonie.listener.AddListener;
import de.haplo.mytonie.model.Audiofile;
import de.haplo.mytonie.model.Model;
import de.haplo.mytonie.model.Tonie;
import de.haplo.mytonie.util.DialogUtil;
import de.haplo.mytonie.util.HashUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ManageTonieStage extends MyStage {

	private static final Image NO_IMAGE_FOUND = new Image(ManageTonieStage.class.getResourceAsStream("/no-image-found.jpg"));

	private Model model;
	
	private ListView<Tonie> listView;
	
	private ObservableList<Tonie> listviewModel;
	
    public ManageTonieStage(Model pModel) {
    	this.model = pModel;

        Button addButton = new Button("Add Entry");
        addButton.setOnAction(e -> addEntryButtonAction());

        Button removeButton = new Button("Remove Entry");
        removeButton.setOnAction(e -> removeEntryButtonAction());
        
        listviewModel = FXCollections.observableList(model.getTonies());
        listView = new ListView<>(listviewModel);
        listView.setCellFactory(param -> new ListCell<Tonie>() {
            private final HBox cellBox = new HBox(8);
            private final ImageView imageView = new ImageView();
            private final Text tonieName = new Text();
            private final Text tonieId = new Text();
            private final Text audiofileInfos = new Text();
            {
                imageView.setFitWidth(30);
                imageView.setFitHeight(30);
                cellBox.getChildren().addAll(imageView, tonieId, tonieName, audiofileInfos);
            }
            @Override
            protected void updateItem(Tonie item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                	imageView.setImage(readImage(item.getImage()));
                    tonieId.setText("["+item.getId()+"]");
                    tonieName.setText(item.getName());

                    String text = model.getAudiofiles().stream()
                    		.filter(a -> a.getId().equals(item.getAudiofileId()))
                    		.findFirst()
                    		.map(a -> "["+a.getName()+"; "+a.getId()+"]")
                    		.orElse("[no audio file]");
                    
                    audiofileInfos.setText(text);
                    
                    setGraphic(cellBox);
                }
            }
            
			private Image readImage(byte[] bytes) {
                if (bytes != null) {
                	return new Image(new ByteArrayInputStream(bytes));
                } else {
                	return NO_IMAGE_FOUND;
                }
			}
        });

        HBox topLine = new HBox(10, addButton, removeButton);
        topLine.setPadding(new Insets(10));
        VBox.setMargin(listView, new Insets(10, 10, 10, 10));
        VBox root = new VBox(topLine, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        init(root, "Manage Tonies", 600, 800);
    }

	private void removeEntryButtonAction() {
		Tonie tonie = listView.getSelectionModel().getSelectedItem();
		if (tonie == null) {
			// no tonie selected, then return
			return;
		}

		if (DialogUtil.confirmation("Remove Tonie", "Are you sure you want to remove this Tonie?", "This can't be reversed!")) {
			listviewModel.remove(tonie);

			if (DialogUtil.confirmation("Remove audiofile", "Do you also want to remove the associated audiofile?", "This can't be reversed!")) {
				File audiofile = new File(model.getAudiofilesDirectory(), tonie.getAudiofileId());
				FileUtils.deleteQuietly(audiofile);
				model.getAudiofiles().removeIf(a -> a.getId().equals(tonie.getAudiofileId()));
			}
			
			Model.save(model);
		}
    }

    
    private void addEntryButtonAction() {
    	AddListener listener = new AddListener(model) {
			@Override
			public void addTonie(String id, String name, String image, String audiofile) {
				// audiodatei kopieren
				String audioId = Audiofile.newHash();
				File file = new File(audiofile);
				copyAudioToDestination(file, audioId);

				//hash berechnen
				String hash = HashUtil.buildHash(file);
				
				//Audiofile und tonie zum model hinzufügen.
				model.getAudiofiles().add(Audiofile.builder().id(audioId).name(name).hash(hash).build());
				listviewModel.add(Tonie.builder().id(id).name(name).audiofileId(audioId).image(toByteArray(image)).build());
				listviewModel.sort((e1, e2) -> e1.getName().compareTo(e2.getName()));
				
				//speichern
				Model.save(model);
			}

			@Override
			public void addAudiofile(String name, String path) {
				//is not used here
			}

		};
    	
    	new AddTonieStage(listener).showAndCenter();
    }
    
}