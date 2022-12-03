import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestAccounts {

    private void init() throws SQLException {
        Accounts.initialize("Test");
        Accounts.clearAll();

        Accounts.addAccount("Sebastian", "password1");
        Accounts.addAccount("Cole", "password2");
        Accounts.addAccount("Sam", "password3");

    }

    @Test
    public void testUpdate() throws SQLException { // also tests displayLeadBoard() and validLogin()
        init();
        Accounts.setTable("Test");
        String[] data = new String[]{"Sam", "1", "1000"}; // note when testing that the password (index 1) must be numerical because of the Integer.Parseint() in update()
        Accounts.update(data);
        String[] data2 = new String[]{"Cole", "2", "500"};
        Accounts.update(data2);

        String[] expected = new String[]{"Sam", "1000", "Cole", "500", "Sebastian", "0"};
        String[] actual = Accounts.getTopPlayers(10);
        System.out.println(Arrays.toString(actual));
        assertArrayEquals(expected, actual);
        //assertTrue(Accounts.validLogin("Cole", "password10"));
    }

    @Test
    public void testValidLogin() throws SQLException {
        init();
        assertTrue(Accounts.validLogin("Sebastian", "password1"));
    }

    @Test
    public void testAddAccount() throws SQLException { // also tests validLogin()
        init();
        Accounts.setTable("Test");
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

    @Test
    public void testGetInfo() throws SQLException {
        init();
        Accounts.setTable("Test");
        String[] expected = new String[]{"Sebastian","password1","0"};
        assertArrayEquals(expected, Accounts.getInfo("Sebastian"));
    }
}
