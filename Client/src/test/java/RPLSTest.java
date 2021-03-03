import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RPLSTest {
	static Client clientTest;

	@BeforeAll
	static void setup() {
		clientTest = new Client(null,null,null,null,null,null, null, "127.0.0.1", 9999);
		clientTest.gameInfo = new GameInfo();
	}

	@Test
	void testInit() {		
		assertEquals("Client", clientTest.getClass().getName(), "Failed to properly initialize an instance of Client");
	}

	@Test
	void testEmptyGameInfo() {
		assertNotNull(clientTest.gameInfo, "Failed to assert that gameInfo is null after initialize an instance of Client");
	}

	@Test
	void testInitGameInfo() {
		clientTest.gameInfo = new GameInfo();
		assertEquals("GameInfo", clientTest.gameInfo.getClass().getName(), "Failed to properly initialize an instance of GameInfo");
	}

	@Test
	void testInitGameInfo2() { 	// Testing Overloaded Constructor
		clientTest.gameInfo = new GameInfo(5);
		assertEquals("GameInfo", clientTest.gameInfo.getClass().getName(), "Failed to properly initialize an instance of GameInfo");
	}

	@Test
	void testGameInfoResetPts() {
		clientTest.gameInfo.reset();
		assertEquals(0, clientTest.gameInfo.p1Pts, "Failed to reset player one points in gameInfo");
		assertEquals(0, clientTest.gameInfo.p2Pts, "Failed to reset player two points in gameInfo");
	}

	@Test
	void testGameInfoResetChoices() {
		clientTest.gameInfo.reset();
		assertEquals("", clientTest.gameInfo.p1Choice, "Failed to reset player one choice in gameInfo");
		assertEquals("", clientTest.gameInfo.p2Choice, "Failed to reset player two choice in gameInfo");
	}

	
	@Test
	void testGameInfoResetStartGame() {
		clientTest.gameInfo.reset();
		assertEquals(-1, clientTest.gameInfo.startGame, "Failed to reset startGame variable in gameInfo");
	}

	@Test
	void testGameInfoResetwinner() {
		clientTest.gameInfo.reset();
		assertEquals(0, clientTest.gameInfo.winner, "Failed to reset winner in gameInfo");
	}
	
	@Test
	void testGameInfoResetPlayAgain() {
		clientTest.gameInfo.reset();
		assertEquals(1, clientTest.gameInfo.playAgain, "Failed to reset playAgain in gameInfo");
	}

	@Test
	void testGameInfoRematchPlayAgain() {
		clientTest.gameInfo.rematch();
		assertEquals(0, clientTest.gameInfo.playAgain, "Rematch returned the wrong playAgain value in gameInfo");
	}

	@Test
	void testGameInfoRematchStartGame() {
		clientTest.gameInfo.rematch();
		assertEquals(1, clientTest.gameInfo.startGame, "Rematch returned the wrong startGame value in gameInfo");
	}
}
