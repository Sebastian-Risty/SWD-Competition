import org.junit.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class TestAccounts {

    private void init() throws SQLException {

        Accounts.initialize("test");
        Accounts.clearAll();
        Accounts.addAccount("Sebastian", "password1");
        Accounts.addAccount("Cole", "password2");
        Accounts.addAccount("Sam", "password3");

    }

    @Test
    public void testUpdate() throws SQLException { // also tests displayLeadBoard() and validLogin()
        init();
        String[] data = new String[]{"Sam", "password3", "1000"};
        Accounts.update(data);
        String[] data2 = new String[]{"Cole", "password10", "500"};
        Accounts.update(data2);

        String[] expected = new String[]{"Sam", "1000", "Cole", "500", "Sebastian", "0"};
        assertArrayEquals(expected, Accounts.getTopPlayers(10));
        assertTrue(Accounts.validLogin("Cole", "password10"));
    }

    @Test
    public void testValidLogin() throws SQLException {
        init();
        assertTrue(Accounts.validLogin("Sebastian", "password1"));
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
