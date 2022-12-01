import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class TestAccounts {
    private Accounts accounts;

    private void init() throws SQLException {
        Accounts.clearAll(); // NUKES THE DATABASE EVERY TIME TESTS ARE RUN

        Accounts.addAccount("Sebastian", "password1");
        Accounts.addAccount("Cole", "password2");
        Accounts.addAccount("Sam", "password3");
    }

    @Test
    public void testUpdateScore() throws SQLException { // also tests displayLeadBoard()
        init();
        Accounts.updateScore("Sam", 1000);
        Accounts.updateScore("Cole", 500);
        String expected;
        expected = "Leaderboard\n\n" +
                "Username\tScore\t\n" +
                "Sam\t1000\t\n" +
                "Cole\t500\t\n" +
                "Sebastian\t0\t\n";
        assertEquals(expected, Accounts.displayLeaderBoard(10));
    }

    @Test
    public void testAddAccount() throws SQLException { // also tests validLogin()
        init();
        Accounts.addAccount("Matt", "password4");
        assertTrue(Accounts.validLogin("Matt", "password4"));
    }

    @Test
    public void testDeleteAccount() throws SQLException { // also tests addAcount() and validLogin()
        init();
        Accounts.addAccount("Matt", "password4");
        Accounts.deleteAccount("Matt", "password4");
        assertFalse(Accounts.validLogin("Matt", "password4"));
    }
}
