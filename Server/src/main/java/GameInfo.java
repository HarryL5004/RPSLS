import java.io.Serializable;

//GameInfo contains all information of a game
public class GameInfo implements Serializable {
    int p1Pts;
    int p2Pts;
    String p1Choice;
    String p2Choice;
    int startGame; // -1: no ; 0: started ; 1: yes
    int winner; // -1: no ; 0: draw ; 1: yes
    int numClients;
    int playAgain; // -1: no ; 0: haven't decided ; 1: yes

    GameInfo() {
        p1Pts = 0;
        p2Pts = 0;
        p1Choice = "";
        p2Choice = "";
        startGame = -1;
        winner = 0;
        playAgain = 0;
    }

    GameInfo(int clients) {
        p1Pts = 0;
        p2Pts = 0;
        p1Choice = "";
        p2Choice = "";
        startGame = -1;
        winner = 0;
        numClients = clients;
        playAgain = 0;
    }

    public void rematch() {
        p1Choice = "";
        p2Choice = "";
        startGame = 1;
        winner = 0;
        playAgain = 0;
    }

    public void reset() {
        p1Pts = 0;
        p2Pts = 0;
        p1Choice = "";
        p2Choice = "";
        startGame = -1;
        winner = 0;
        playAgain = 1;
        numClients = 1;
    }
}
