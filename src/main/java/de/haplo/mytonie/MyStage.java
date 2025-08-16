package de.haplo.mytonie;

import de.haplo.mytonie.util.FxUtil;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MyStage extends Stage {

	public void init(Parent root, String title, double width, double height) {
        setScene(new Scene(root, width, height));
        initModality(Modality.APPLICATION_MODAL);
        getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        setTitle(title);
        setWidth(width);
        setHeight(height);
	}
	
	public void showAndCenter() {
		show();
		FxUtil.center(this);
	}
	
}
