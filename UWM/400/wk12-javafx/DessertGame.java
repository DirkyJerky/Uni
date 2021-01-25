import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class DessertGame extends Application {
	private int score = 0;
	
    @Override
    public void start(final Stage stage) {
        BorderPane bPane = new BorderPane();
        
        Label scoreLabel = new Label("Score: 0");
        BorderPane.setAlignment(scoreLabel, Pos.TOP_LEFT);
        bPane.setTop(scoreLabel);
        
        Button exitButton = new Button("Exit");
        BorderPane.setAlignment(exitButton, Pos.BOTTOM_RIGHT);
        exitButton.setOnAction((e) -> stage.close());
        bPane.setBottom(exitButton);
        
        Button[] buttons = new Button[8];
        buttons[0] = new Button("Dessert");
        
        for (int i = 1; i < buttons.length; i += 1) {
        	buttons[i] = new Button("Desert");
        }

        Pane centerPane = new Pane();
        centerPane.getChildren().addAll(buttons);
        bPane.setCenter(centerPane);
        
        Random random = new Random();
        randomizeButtonPositions(random, buttons);
        
        for (Button button : buttons) {
        	button.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					randomizeButtonPositions(random, buttons); 
					exitButton.requestFocus();
					
					if (((Button) event.getTarget()).getText() == "Dessert") {
						score += 1;
					} else {
						score -= 1;
					}
					scoreLabel.setText("Score: " + score);
				}
			});
        }
        
        Scene scene = new Scene(bPane,700,500);
        stage.setScene(scene);
        
        stage.setTitle("Dessert in the Desert JavaFX Game");
        stage.show();
        
        exitButton.requestFocus();
    }
    
    private void randomizeButtonPositions(Random random, Button[] buttons) {
    	for (Button button : buttons) {
    		button.setLayoutX(random.nextInt(600));
    		button.setLayoutY(random.nextInt(400));
    	}
    }
    
    public static void main(String[] args) {
        Application.launch();
    }
}
