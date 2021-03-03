import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ServerTest {
	static Server testServer;
	@BeforeAll
	static void setup() {
		testServer = new Server(null, 9999);
	}

	@Test
	void testInit() {
		assertEquals("Server", testServer.getClass().getName());
	}

	@Test
	void testConvertChoiceRock() {
		assertEquals(1,testServer.convertChoice("Rock"), "Failed to convert Rock to correct id");
	}

	@Test
	void testConvertChoicePaper() {
		assertEquals(2,testServer.convertChoice("Paper"), "Failed to convert Paper to correct id");
	}

	@Test
	void testConvertChoiceScissors() {
		assertEquals(3,testServer.convertChoice("Scissors"), "Failed to convert Scissors to correct id");
	}

	@Test
	void testConvertChoiceLizard() {
		assertEquals(4,testServer.convertChoice("Lizard"), "Failed to convert Lizard to correct id");
	}

	@Test
	void testConvertChoiceSpock() {
		assertEquals(5,testServer.convertChoice("Spock"), "Failed to convert Spock to correct id");
	}

	@ParameterizedTest
	@ValueSource(strings={"Rock", "Paper", "Scissors", "Lizard", "Spock"})
	void testGameLogicDraw(String s) {
		GameInfo g1 = new GameInfo();
		GameInfo g2 = new GameInfo();
		g1.p1Choice = s;
		g2.p1Choice = s;
		testServer.gameLogic(g1, g2);
		assertEquals(0, g1.winner, "Failed to correctly evaluate result");
		assertEquals(0, g2.winner, "Failed to correctly evaluate result");
	}

	@Test
	void testGameLogicC1Wins() {
		GameInfo g1 = new GameInfo();
		GameInfo g2 = new GameInfo();
		g1.p1Choice = "Rock";
		g2.p1Choice = "Scissors";
		testServer.gameLogic(g1, g2);
		assertEquals(1, g1.winner, "Failed to correctly evaluate result");
		assertEquals(2, g2.winner, "Failed to correctly evaluate result");
	}

	@Test
	void testGameLogicC2Wins() {
		GameInfo g1 = new GameInfo();
		GameInfo g2 = new GameInfo();
		g1.p1Choice = "Paper";
		g2.p1Choice = "Scissors";
		testServer.gameLogic(g1, g2);
		assertEquals(2, g1.winner, "Failed to correctly evaluate result");
		assertEquals(1, g2.winner, "Failed to correctly evaluate result");
	}

}
