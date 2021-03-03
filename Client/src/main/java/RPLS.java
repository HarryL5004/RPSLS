import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.WindowEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class RPLS extends Application {
	ListView<String> listView = new ListView<>();
	TextField ipBox = new TextField();
	TextField portBox = new TextField();	
	Button connectBtn = new Button("Connect");
	Button startBtn = new Button("PLAY");


	ImageView img1 = new ImageView();
	ImageView img2 = new ImageView();
	ImageView img3 = new ImageView();
	ImageView img4 = new ImageView();
	ImageView img5 = new ImageView();
	ImageView opnt = new ImageView();
	TextField ptsBox = new TextField();
	TextField optPtsBox = new TextField();
	Button againBtn = new Button("Play Again");
	Button quitBtn = new Button("Quit");
	HashMap<String, Scene> scenes = new HashMap<>();
	Client clientConn;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("RPLS Client");

		connectBtn = new Button("Connect");
		connectBtn.setOnAction(e-> {
			startSecondScene(primaryStage);
		});

		firstScene(primaryStage);		
		primaryStage.setScene(scenes.get("firstS"));
		primaryStage.show();

		quitBtn.setOnAction(e -> {
			Platform.exit();
			System.exit(0);
		});

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	// start the second scene
	public void startSecondScene(Stage primaryStage) {
		secondScene(primaryStage);
		primaryStage.setResizable(false);
		primaryStage.setScene(scenes.get("secondS"));

		String ip = ipBox.getText(); // get ip
		if (ip.equals("localhost")) {
			ip = "127.0.0.1";
		}
		int port = Integer.parseInt(portBox.getText()); // get port

		clientConn = new Client(data -> {
			Platform.runLater(()->{
				listView.getItems().add(data.toString());
				listView.scrollTo(listView.getItems().size()-1);
			});

		}, cnd -> { // for enabling/disabling start button
			Platform.runLater(()->{
				startBtn.setDisable((boolean)cnd);	
			});
		}, img -> { // for setting opponent's image of their choice
			Platform.runLater(()->{
				opnt.setImage(new Image(img.toString()));
		});
		}, p -> { // for setting the client's points
			Platform.runLater(()->{
				ptsBox.setText(p.toString());
		});
		}, optPts -> { // for setting the opponent's points
			Platform.runLater(()->{
				optPtsBox.setText(optPts.toString());
		});
		},replay -> { // for enabling/disabling again button and quit button
			Platform.runLater(()->{
				againBtn.setDisable((boolean)replay);
				quitBtn.setDisable((boolean)replay);
		});
		},() -> { //lambda passed to Client thread to reset game state
			img1.setDisable(false);
			img2.setDisable(false);
			img3.setDisable(false);
			img4.setDisable(false);
			img5.setDisable(false);
			againBtn.setDisable(true);
			quitBtn.setDisable(true);
		},ip, port);
		clientConn.start();			
	}
	
	// Disable choices after choosing and send client's choices
	public void handleGame() {
		listView.scrollTo(listView.getItems().size()-1);
		img1.setDisable(true);
		img2.setDisable(true);
		img3.setDisable(true);
		img4.setDisable(true);
		img5.setDisable(true);
		clientConn.send(clientConn.gameInfo);
	}

	// create first scene
	public void firstScene(Stage primaryStage) {
		primaryStage.setResizable(false);
		GridPane gPane = new GridPane();
		HBox hBox = new HBox();
		Text ipText = new Text("IP: ");		
		ipBox.setPromptText("localhost");
		Text portText = new Text("Port: ");
		

		hBox.getChildren().addAll(ipText,ipBox,portText,portBox, connectBtn);
		hBox.setSpacing(7);
		gPane.add(hBox,2,2);
		gPane.setHgap(15);
		gPane.setAlignment(Pos.CENTER);
		Scene scene = new Scene(gPane,500,100);
		scenes.put("firstS", scene);
	}

	// create second scene
	public void secondScene(Stage primaryStage) {
		primaryStage.setTitle("Tutorial");
		VBox vBox = new VBox();
		
		startBtn.setDisable(true);
		startBtn.setOnAction((e) -> {
			ThirdScene(primaryStage);
			primaryStage.setScene(scenes.get("thirdS"));
		});
		startBtn.setPrefSize(150, 40);
		BorderPane startPane = new BorderPane();
		startPane.setRight(startBtn);

		Text rules = new Text(RPLSrule); //RPSLS rules
		rules.setStyle("-fx-font: normal bold 14px 'Assistant' ");
		rules.autosize();
		
		ImageView imgRules = new ImageView(new Image("rules.png"));
		imgRules.setFitHeight(300);
		imgRules.setPreserveRatio(true);
		BorderPane bImg = new BorderPane();		
		bImg.setCenter(imgRules);
		bImg.setTop(rules);

		vBox.getChildren().addAll(bImg, listView, startPane);
		listView.setMaxHeight(100);
		Scene scene = new Scene(vBox,600,600);
		scenes.put("secondS", scene);
	}

	// create third scene
	public void ThirdScene(Stage primaryStage) {
		primaryStage.setTitle("Rock Paper Scissors Lizard Spock!");
		primaryStage.setResizable(true);
		BorderPane bPane = new BorderPane();
		VBox vBox = new VBox(); // right side vBox
		listView.setMaxHeight(600);
		listView.autosize();
		Text pts = new Text("Your Points:");		
		Text optPts = new Text("Opponent Points:");		
		ptsBox.setDisable(true);
		optPtsBox.setDisable(true);

		
		againBtn.setDisable(true);		
		againBtn.setOnAction(e -> {	// again button action	
			clientConn.replayReq();
			againBtn.setDisable(true);
			quitBtn.setDisable(true);
		});
		againBtn.setPrefSize(150, 40);
		quitBtn.setDisable(true);
		quitBtn.setPrefSize(150, 40);
		vBox.getChildren().addAll(pts, ptsBox, optPts, optPtsBox ,listView, againBtn, quitBtn);

		GridPane gPane = new GridPane(); // left grid pane		
		Text optText = new Text("Opponent");
		optText.setStyle("-fx-font: normal bold 14px 'Assistant' ");
		Text selfText = new Text("You");
		selfText.setStyle("-fx-font: normal bold 14px 'Assistant' ");
		HBox hBox = new HBox();	
		opnt.setImage(new Image("question.png"));
		opnt.setPreserveRatio(true);
		opnt.setFitHeight(150);			
		img1.setImage(new Image("lizard.jpg"));		
		img1.setPreserveRatio(true);
		img1.setFitHeight(150);
		img2.setImage(new Image("paper.png"));
		img2.setPreserveRatio(true);
		img2.setFitHeight(150);
		img3.setImage(new Image("rock.png"));
		img3.setPreserveRatio(true);
		img3.setFitHeight(150);
		img4.setImage(new Image("scissors.jpg"));
		img4.setPreserveRatio(true);
		img4.setFitHeight(150);
		img5.setImage(new Image("spock.jpg"));				
		img5.setPreserveRatio(true);
		img5.setFitHeight(150);
		img1.setOnMouseClicked(e -> {
			listView.getItems().add("You chose Lizard!");
			clientConn.gameInfo.p1Choice = "Lizard";
			handleGame();
		});
		img2.setOnMouseClicked(e -> {
			listView.getItems().add("You chose Paper!");
			clientConn.gameInfo.p1Choice = "Paper";
			handleGame();
		});
		img3.setOnMouseClicked(e -> {
			listView.getItems().add("You chose Rock!");
			clientConn.gameInfo.p1Choice = "Rock";
			handleGame();
		});
		img4.setOnMouseClicked(e -> {
			listView.getItems().add("You chose Scissors!");
			clientConn.gameInfo.p1Choice = "Scissors";
			handleGame();
		});
		img5.setOnMouseClicked(e -> {
			clientConn.gameInfo.p1Choice = "Spock";
			listView.getItems().add("You chose Spock!");
			handleGame();
		});
		
		hBox.getChildren().addAll(img1, img2, img3, img4, img5);
		BorderPane optBox = new BorderPane();
		optBox.setCenter(opnt);
		gPane.setVgap(30);
		gPane.add(optText, 3, 0);
		gPane.add(optBox, 3, 1);
		gPane.add(hBox,3,4);
		gPane.add(selfText, 3, 5);
		gPane.setAlignment(Pos.CENTER);

		bPane.setLeft(gPane);
		bPane.setRight(vBox);
		bPane.autosize();
		Scene scene = new Scene(bPane,1000,600);
		scenes.put("thirdS", scene);
	}

	String RPLSrule = "Welcome to the game of Rock, Paper, Scissors, Lizard, Spock.\n" +
	"The rules are as follows:\n" +
	"ROCK beats SCISSORS and LIZARD\n" +
	"PAPER beats ROCK and SPOCK\n" +
	"SCISSORS beats PAPER and LIZARD\n" +
	"LIZARD beats PAPER and SPOCK\n" +
	"SPOCK beats ROCK and SCISSORS\n" +
	"The first person to get to 3 points WINS.";
}
