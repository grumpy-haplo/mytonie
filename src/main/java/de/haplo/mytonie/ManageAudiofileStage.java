package de.haplo.mytonie;
import java.io.File;

import org.apache.commons.io.FileUtils;

import de.haplo.mytonie.listener.AddListener;
import de.haplo.mytonie.model.Audiofile;
import de.haplo.mytonie.model.Model;
import de.haplo.mytonie.util.DialogUtil;
import de.haplo.mytonie.util.HashUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ManageAudiofileStage extends MyStage {

	private Model model;
	
	private ListView<Audiofile> listView;
	
	private ObservableList<Audiofile> listviewModel;
	
    public ManageAudiofileStage(Model pModel) {
    	this.model = pModel;

        Button addButton = new Button("Add Entry");
        addButton.setOnAction(e -> addEntryButtonAction());

        Button removeButton = new Button("Remove Entry");
        removeButton.setOnAction(e -> removeEntryButtonAction());
        
        listviewModel = FXCollections.observableList(model.getAudiofiles());
        listView = new ListView<>(listviewModel);
        listView.setCellFactory(param -> new ListCell<Audiofile>() {
            private final HBox cellBox = new HBox(8);
            private final Text name = new Text();
            private final Text id = new Text();
            {
                cellBox.getChildren().addAll(id, name);
            }
            @Override
            protected void updateItem(Audiofile item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    id.setText(item.getId());
                    name.setText(item.getName());
                    setGraphic(cellBox);
                }
            }
        });

        HBox topLine = new HBox(10, addButton, removeButton);
        topLine.setPadding(new Insets(10));
        VBox.setMargin(listView, new Insets(10, 10, 10, 10));
        VBox root = new VBox(topLine, listView);
        VBox.setVgrow(listView, Priority.ALWAYS);

        
        init(root, "Manage Audiofiles", 600, 800);
    }

	private void removeEntryButtonAction() {
		Audiofile audiofile = listView.getSelectionModel().getSelectedItem();
		if (audiofile == null) {
			return;
		}

		if (DialogUtil.confirmation("Remove Confirmation", "Are you sure you want to remove this audiofile? If it is associated with a Tonie, it will be removed too.", "This can't be reversed!")) {
			File file = new File(model.getAudiofilesDirectory() , audiofile.getId());
			FileUtils.deleteQuietly(file);
			listviewModel.remove(audiofile);

			model.getTonies().removeIf(t -> t.getAudiofileId().equals(audiofile.getId()));
			
			Model.save(model);
		}
    }

    
    private void addEntryButtonAction() {
    	AddListener listener = new AddListener(model) {
			@Override
			public void addAudiofile(String name, String path) {
				// audiodatei kopieren
				String audioId = Audiofile.newHash();
				File audiofile = new File(path);
				copyAudioToDestination(audiofile, audioId);
				
				//hash berechnen
				String hash = HashUtil.buildHash(audiofile);

				//Audiofile und tonie zum model hinzufügen.
				listviewModel.add(Audiofile.builder().id(audioId).name(name).hash(hash).build());
				listviewModel.sort((e1, e2) -> e1.getName().compareTo(e2.getName()));

				//speichern
				Model.save(model);
			}

			@Override
			public void addTonie(String id, String name, String image, String audiofile) {
				// is not needed here
			}

		};
    	
    	new AddAudiofileStage(listener).showAndCenter();
    }
    
}