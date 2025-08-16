package de.haplo.mytonie;

import de.haplo.mytonie.util.FxUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class BusyDialog {

    private final Stage stage;

    public BusyDialog(Window owner, String message) {
        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY); // Optional: simples Dialogfenster TODO testen
        stage.setOnShown(e -> {
        	FxUtil.center(stage);
        });
        
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        Label label = new Label(message);

        VBox vbox = new VBox(16, label, progressBar);
        vbox.setPadding(new Insets(10));
        vbox.setMinWidth(300);

        stage.setScene(new Scene(vbox));
    }

    public void show() {
    	stage.showAndWait();
    }

    public void close() {
        stage.close();
    }
    
    public static void run(Window owner, String message, Runnable runnable) {
		BusyDialog busyDialog = new BusyDialog(owner, message);

		new Thread(() -> {
			Platform.runLater(() -> busyDialog.show());
			runnable.run();
		    Platform.runLater(() -> busyDialog.close());
		}).start();
    }

}