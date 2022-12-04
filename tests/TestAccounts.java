import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TestAccounts {

    private static final String KEY_NAME = "key";

    private void init() throws SQLException {
        Database.initialize("Test");
        Database.clearAll();

        Database.addAccount("Sebastian", "password1");
        Database.addAccount("Cole", "password2");
        Database.addAccount("Sam", "password3");

    }

    @Test
    public void testUpdate() throws SQLException { // also tests displayLeadBoard() and validLogin()
        init();
        Database.setTable("Test");
        String[] data = new String[]{"Sam", "mypassword1", "1000"}; // note when testing that the password (index 1) must be numerical because of the Integer.Parseint() in update()
        Database.update(data);
        String[] data2 = new String[]{"Cole", "mypassword2", "500"};
        Database.update(data2);

        String[] expected = new String[]{"Sam", "1000", "Cole", "500", "Sebastian", "0"};
        String[] actual = Database.getTopPlayers(10);
        System.out.println(Arrays.toString(actual));
        assertArrayEquals(expected, actual);
        //assertTrue(Accounts.validLogin("Cole", "password10"));
    }

    @Test
    public void testValidLogin() throws SQLException, FileNotFoundException {
        init();
        assertTrue(Database.validLogin("Sebastian", "password1"));
    }

    @Test
    public void testAddAccount() throws SQLException, FileNotFoundException { // also tests validLogin()
        init();
        Database.setTable("Test");
        Database.addAccount("Matt", "password4");
        assertTrue(Database.validLogin("Matt", "password4"));
    }

    @Test
    public void testDeleteAccount() throws SQLException, FileNotFoundException { // also tests addAcount() and validLogin()
        init();
        Database.addAccount("Matt", "password4");
        Database.deleteAccount("Matt", "password4");
        assertFalse(Database.validLogin("Matt", "password4"));
    }

    @Test
    public void testGetInfo() throws SQLException, FileNotFoundException {
        init();
        Database.setTable("Test");

        String[] keyInfo = Encryptor.getFileInfo(KEY_NAME);
        if (Objects.equals(keyInfo[0], "Error: file \"" + KEY_NAME + "\" not found")) { // if the key is not found
            System.out.println("Error: key \"" + KEY_NAME + "\" not found");
            throw new FileNotFoundException();
        }

        String[] expected = new String[]{"Sebastian","password1","0"};
        assertArrayEquals(expected, Database.getInfo("Sebastian"));
    }
}
