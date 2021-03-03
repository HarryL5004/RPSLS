import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.function.Consumer;

//Server class for spinning up a server to host the game
public class Server{
    int clientCnt = 1;
    int port;
    volatile ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;

    Server(Consumer<Serializable> call, int port){
        this.port = port;
        callback = call;
        server = new TheServer();
        server.start();
    }

    // gameLogic determines which client wins and update their points
    public int gameLogic(GameInfo p1Info, GameInfo p2Info) {
        int p1 = convertChoice(p1Info.p1Choice);
        int p2 = convertChoice(p2Info.p1Choice);

        if (p1 == p2) {
            p1Info.winner = 0;
            p2Info.winner = 0;
            return 0;
        }
        else if ((p1 == 1 && p2 == 3) || (p1 == 1 && p2 == 4) || // Rock > lizard; Rock > Scissors
                (p1 == 2 && p2 == 1) || (p1 == 2 && p2 == 5) || // Paper > rock; Paper > Spock
                (p1 == 3 && p2 == 2) || (p1 == 3 && p2 == 4) || // Scissors > lizard; Scissors > Paper
                (p1 == 4 && p2 == 5) || (p1 == 4 && p2 == 2) || // Lizard > spock; Lizard > Paper
                (p1 == 5 && p2 == 1) || (p1 == 5 && p2 == 3)) { // Spock > scissors; Spock > Rock
            p1Info.winner = 1;
            p2Info.winner = 2;
            p1Info.p1Pts++;
            return 1;
        }
        else {
            p1Info.winner = 2;
            p2Info.winner = 1;
            p2Info.p1Pts++;
            return 2;
        }
    }

    // convert choice in string to integer
    public int convertChoice(String choice) {
        switch (choice) {
            case "Rock":
                return 1;
            case "Paper":
                return 2;
            case "Scissors":
                return 3;
            case "Lizard":
                return 4;
            case "Spock":
                return 5;
        }
        return -1;
    }

    // startGame contains the logic that starts the game
    public void startGame(int round) {
        callback.accept("Starting game...");
        callback.accept("ROUND " + round + "!");
        System.out.println("Started Game");
        try {
            try {
                Thread.sleep(1000); // wait for 1 sec for better transitions
            } catch (Exception e) {
                System.out.println("Start game error: " + e);
            }
            for (int i = 0; i < clients.size(); ++i) {
                clients.get(i).playGame(); // tell clients to start the game
            }
            GameInfo p1Info;
            GameInfo p2Info;
            while (true) { // loop that waits for clients to submit their choices
                p1Info = clients.get(0).gameInfo;
                p2Info = clients.get(1).gameInfo;
                if (!p1Info.p1Choice.equals("") && !p2Info.p1Choice.equals(""))
                    break;
            }

            callback.accept("Client #1 played: " + p1Info.p1Choice);
            callback.accept("Client #2 played: " + p2Info.p1Choice);
            int winner = gameLogic(p1Info, p2Info); // compare choices
            p1Info.p2Pts = p2Info.p1Pts; // exchange info
            p2Info.p2Pts = p1Info.p1Pts; // exchange info
            p1Info.p2Choice = p2Info.p1Choice; // exchange info
            p2Info.p2Choice = p1Info.p1Choice; // exchange info
            p1Info.startGame = 0; // indicate the game is ongoing
            p2Info.startGame = 0;
            switch (winner) { // display winner for this round
                case 0:
                    callback.accept("This round is a draw!");
                    break;
                case 1:
                    callback.accept("Client #1 wins this round!");
                    break;
                case 2:
                    callback.accept("Client #2 wins this round!");
            }
            callback.accept("Client #1 Points: " + p1Info.p1Pts); // display clients' points
            callback.accept("Client #2 Points: " + p2Info.p1Pts);
            clients.get(0).send(p1Info); // send results
            clients.get(1).send(p2Info); // send results
            System.out.println("Reached end of round");
        } catch(Exception e) {
            System.out.println("Game is disrupted: " + e);
            e.printStackTrace();
        }
    }

    // rematchReq handles any rematch requests from clients
    public boolean rematchReq() {
        while (true) { // wait for client to decide if they want a rematch
            if (clients.size() < 2) // no rematch if one client left
                break;
            try {
                Thread.sleep(3000);
                if (clients.get(0).gameInfo.playAgain == 1 && clients.get(1).gameInfo.playAgain == 1) {
                    return true;
                }
            } catch(Exception e) {
                System.out.println("Thread sleep error: " + e);
                e.printStackTrace();
                break;
            }
        }
        return false;
    }

    //TheServer listens for clients
    public class TheServer extends Thread{
        public void run() {

            try(ServerSocket mysocket = new ServerSocket(port)){
                callback.accept("Server is waiting for a client!");

                while(true) {
                    callback.accept("Waiting for "+ (3-clientCnt) + " more to start the game");
                    ClientThread c = new ClientThread(mysocket.accept(), clientCnt);
                    for (int i = 0; i < clients.size(); ++i) {
                        clients.get(i).updateClientCnt(clientCnt);
                    }
                    callback.accept("client has connected to server: " + "client #" + clientCnt);
                    clients.add(c);
                    c.start();
                    if (clientCnt == 2) {
                        callback.accept("Found 2 clients!");
                        while (true) { // main game loop
                            try {
                                clients.get(0).gameInfo.reset(); // reset clients' gameInfo
                                clients.get(1).gameInfo.reset();
                                int round = 1;
                                while (true) { // loop until a player gets 3 points
                                    clients.get(0).gameInfo.rematch(); // set clients' gameInfo to rematch
                                    clients.get(1).gameInfo.rematch();

                                    startGame(round++);
                                    if (clients.get(0).gameInfo.p1Pts == 3) { // end game if there's a client with 3 points
                                        callback.accept("Client #1 Wins!");
                                        break;
                                    }
                                    if (clients.get(1).gameInfo.p1Pts == 3) {
                                        callback.accept("Client #2 Wins!");
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Error in between rounds: " + e);
                                if (clientCnt < 2)
                                    System.out.println("Back to finding clients");
                                e.printStackTrace();
                                break;
                            }
                            if (!rematchReq()) { // Check if clients want a rematch
                                System.out.println("Back to finding clients");
                                break;
                            }
                        }
                    }

                    clientCnt++;
                }//end of while
            }//end of try
            catch(Exception e) {
                System.out.println("Server socket did not launch: " + e);
                e.printStackTrace();
            }
        }//end of run
    }

    // ClientThread handles connection between a client and server
    class ClientThread extends Thread{
        Socket connection;
        int innerClientCnt;
        volatile ObjectInputStream in;
        volatile ObjectOutputStream out;
        volatile GameInfo gameInfo;

        ClientThread(Socket s, int clientCnt){
            this.connection = s;
            innerClientCnt = clientCnt;
            gameInfo = new GameInfo(clientCnt);
        }

        public void run(){

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open: " + e);
                e.printStackTrace();
            }

            if (innerClientCnt < 2)
                notifyClient();

            while(true) {
                try {
                    gameInfo = (GameInfo) in.readObject();
                    if (gameInfo.playAgain == 1) {
                        callback.accept("Client #" + innerClientCnt + " wants to have a rematch!");
                    }
                    else if (gameInfo.playAgain == -1)
                        callback.accept("Client #"+innerClientCnt+" does not want to play again");
                }
                catch(Exception e) {
                    callback.accept("Client #"+innerClientCnt+" has left the server!");
                    clientCnt--;
                    clients.remove(this);
                    if (clients.size() > 0) {
                        clients.get(0).gameInfo.reset();
                        clients.get(0).notifyClient();
                    }
                    break;
                }
            }
        }//end of run

        public void send(GameInfo gameInfo) {
            try {
                //System.out.println(gameInfo.startGame +""+ gameInfo.numClients + "" + out);
                out.writeObject(gameInfo);
                out.reset();
            } catch (Exception e) {
                System.out.println("Error while sending info: " + e);
                System.out.println("Out: " + out);
                System.out.println("GameInfo: " + gameInfo);
                e.printStackTrace();
            }
        }

        public void updateClientCnt(int count) {
            gameInfo.numClients = count;
        }

        public void playGame() {
            gameInfo.startGame = 1;
            gameInfo.playAgain = 0;
            send(gameInfo);
        }

        public void notifyClient() {
            try {
                System.out.println("Notifying client");
                gameInfo.startGame = -1;
                send(gameInfo);
            } catch(Exception e) {
                System.out.println("Failed to notify client: " + e);
                e.printStackTrace();
            }
        }

    }//end of client thread
}






