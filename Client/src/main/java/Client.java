import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;




public class Client extends Thread{
	Socket socketClient;	
	volatile ObjectOutputStream out;
    volatile ObjectInputStream in;
    String ip;
    int port;
	
    private Consumer<Serializable> callback;
    private Consumer<Serializable> callbtn;
    private Consumer<Serializable> optImg;
    private Consumer<Serializable> pts;
    private Consumer<Serializable> optPts;
    private Consumer<Serializable> againbtn;
    Game rematch;
    volatile GameInfo gameInfo;
	
    Client(Consumer<Serializable> call, Consumer<Serializable> callBtn, Consumer<Serializable> optImg,
                Consumer<Serializable> ptsBox, Consumer<Serializable> optPtsBox,
                Consumer<Serializable> againBtn, Game rematch,
                String ip, int port){	
        callback = call;
        this.callbtn = callBtn;
        this.optImg = optImg;
        this.pts = ptsBox;
        this.optPts = optPtsBox;
        this.againbtn = againBtn;        
        this.rematch = rematch;
        this.ip = ip;
        this.port = port;
	}
	
	public void run() {
		
        try {            
            socketClient= new Socket(ip,port);            
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }        
		catch(Exception e) {
            System.out.println("Error creating i/o streams: " + e);
            e.printStackTrace();
        }
		
		while(true) {
			try {
                gameInfo = (GameInfo) in.readObject();                
                if (gameInfo.startGame == -1) {
                    callback.accept("Number of clients on server: " + gameInfo.numClients);
                    callback.accept("Waiting for other clients to connect...");
                    callbtn.accept(true);
                } else if (gameInfo.startGame == 1) {
                    callback.accept("Game is ready to start!!!");
                    optImg.accept("question.png");
                    callbtn.accept(false);               
                    rematch.reset();
                }

                if (!gameInfo.p2Choice.equals("")) {
                    String opntImg = "";
                    switch (gameInfo.p2Choice) {
                        case "Lizard":
                            opntImg = "lizard.jpg";
                            break;
                        case "Paper":
                            opntImg = "paper.png";
                            break;
                        case "Rock":
                            opntImg = "rock.png";
                            break;
                        case "Scissors":
                            opntImg = "scissors.jpg";
                            break;
                        case "Spock":
                            opntImg = "spock.jpg";
                            break;
                    }
                    optImg.accept(opntImg);                
                    String result = "";
                    switch (gameInfo.winner) {
                        case 0:
                            result = "This round is a Draw!";
                            break;
                        case 1:
                            result = "You Won This Round!";
                            break;
                        case 2:
                            result = "You Lost This Round!";
                            break;
                    }
                    callback.accept(result);
                    pts.accept(Integer.toString(gameInfo.p1Pts));
                    optPts.accept(Integer.toString(gameInfo.p2Pts));
                    Thread.sleep(2000);
                    againbtn.accept(false);
                }
            }
			catch(Exception e) {
                System.out.println("Input stream error: " + e);                
                e.printStackTrace();
                break;
            }
		}
	
    }
	
	public void send(GameInfo gameInfo) {		
		try {
			out.writeObject(gameInfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void replayReq() {
        try {
            gameInfo.rematch();
            gameInfo.playAgain = 1;
			out.writeObject(gameInfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


}
