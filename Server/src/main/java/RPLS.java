import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class RPLS extends Application {
	ListView listView = new ListView();
	Button startServer = new Button("Start Server");
	TextField portBox = new TextField();
	HashMap<String, Scene> scenes = new HashMap<>();
	Server serverConn;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("RPLS Server");

		startServer.setOnAction(e-> {
			secondScene(primaryStage);
			primaryStage.setResizable(true);
			primaryStage.setScene(scenes.get("secondS"));
			serverConn = new Server(data -> { // passing reference of listView to Server
				Platform.runLater(()->{
					listView.getItems().add(data);
					listView.scrollTo(listView.getItems().size()-1);
				});

			}, Integer.parseInt(portBox.getText()));
		});

		firstScene(primaryStage);
		primaryStage.setScene(scenes.get("firstS"));
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0); // close any remaining threads
			}
		});
	}

	// create first scene
	public void firstScene(Stage primaryStage) {
		primaryStage.setResizable(false);
		GridPane gPane = new GridPane();
		HBox hBox = new HBox();
		Text ipText = new Text("IP: ");
		TextField ipBox = new TextField();
		ipBox.setText("localhost");
		ipBox.setDisable(true);
		Text portText = new Text("Port: ");

		hBox.getChildren().addAll(ipText,ipBox,portText,portBox, startServer);
		hBox.setSpacing(7);
		gPane.add(hBox,2,2);
		gPane.setHgap(15);
		gPane.setAlignment(Pos.CENTER);
		Scene scene = new Scene(gPane,500,100);
		scenes.put("firstS", scene);
	}

	//create second scene
	public void secondScene(Stage primaryStage) {
		VBox vBox = new VBox();
		vBox.getChildren().add(listView);
		Scene scene = new Scene(vBox,600,600);
		scenes.put("secondS", scene);
	}

}
