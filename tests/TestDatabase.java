import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.SQLException;
//import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestDatabase {

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
        // System.out.println(Arrays.toString(actual));
        assertArrayEquals(expected, actual);
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
        Database.setTable("Test");
        Database.addAccount("Matt", "password4");
        Database.deleteAccount("Matt", "password4");
        assertFalse(Database.validLogin("Matt", "password4"));
    }

    @Test
    public void testGetInfo() throws SQLException {
        init();
        Database.setTable("Test");
        String[] expected = new String[]{"Sebastian", "password1", "0"};
        assertArrayEquals(expected, Database.getInfo("Sebastian"));
    }

    @Test
    public void testGetKeyInfo() {
        boolean passed = true;
        try {
            Database.getKeyInfo();
        } catch (FileNotFoundException ex) {
            passed = false;
        }
        assertTrue(passed);
    }

    @Test
    public void testCreateAddToTournament() throws SQLException {
        init();
        Database.setTable("mastertournament");
        Database.createTournament("testtournament", 100);

        Database.addToTournament("Sebastian", "testtournament");
        assertFalse(Database.addToTournament("Sebastian", "testtournament")); // cannot add same account twice
        assertFalse(Database.removeFromTournament("yourmom", "testtournament")); // cannot remove account that's not there

        Database.setTable("testtournament");
//        System.out.println(Arrays.toString(Database.getInfo("Sebastian")));

        // GOOD EXAMPLE OF HOW RANK WILL NEED TO BE UPDATED, SETTING RANK TO ROW ONLY WORKS FOR INITIAL VALUES
        Database.addToTournament("Matt", "testtournament");
        Database.addToTournament("Sam", "testtournament");
        Database.addToTournament("Cole", "testtournament");
        Database.removeFromTournament("Sebastian", "testtournament");
//        System.out.println(Arrays.toString(Database.getInfo("Matt")));

        Database.deleteTournament("testtournament");
        assertTrue(Database.createTournament("testtournament", 100)); // tournament deleted and then recreated successfully
        Database.deleteTournament("testtournament");

        assertFalse(Database.deleteTournament("testtournament"));
        assertFalse(Database.deleteAccount("hey", "hi"));


    }
}
