import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Used to test the functionality of the database class
 * @author mddutton
 */
public class TestDatabase {

    /**
     * Used to initialize the Test table
     * @throws SQLException // thrown if failed to write to database
     */
    private void init() throws SQLException {
        Database.initialize("Test");
        Database.clearAll();

        Database.addAccount("Sebastian", "password1");
        Database.addAccount("Cole", "password2");
        Database.addAccount("Sam", "password3");

    }

    /**
     * Used to test the Database update method (also tests getTopPlayers)
     * @throws SQLException thrown if failed to write to database
     */
    @Test
    public void testUpdate() throws SQLException {
        init();
        Database.setTable("Test");
        String[] data = new String[]{"Sam", "mypassword1", "1000"};
        Database.update(data);
        String[] data2 = new String[]{"Cole", "mypassword2", "500"};
        Database.update(data2);

        String[] expected = new String[]{"Sam", "1000", "Cole", "500", "Sebastian", "0"};
        String[] actual = Database.getTopPlayers(10);
        // System.out.println(Arrays.toString(actual));
        assertArrayEquals(expected, actual);
    }

    /**
     * Tests the valid login function
     * @throws SQLException thrown if failed to write to the database
     * @throws FileNotFoundException thrown if key file not found
     */
    @Test
    public void testValidLogin() throws SQLException, FileNotFoundException {
        init();
        assertTrue(Database.validLogin("Sebastian", "password1"));
    }

    /**
     * Test the addAccount function (also tests validLogin)
     * @throws SQLException thrown if failed to write to database
     * @throws FileNotFoundException thrown if key file not found
     */
    @Test
    public void testAddAccount() throws SQLException, FileNotFoundException {
        init();
        Database.setTable("Test");
        Database.addAccount("Matt", "password4");
        assertTrue(Database.validLogin("Matt", "password4"));
    }

    /**
     * Test deleteAccount(), also tests addAccount and validLogin
     * @throws SQLException thrown if failed to write to database
     * @throws FileNotFoundException thrown if key file not found
     */
    @Test
    public void testDeleteAccount() throws SQLException, FileNotFoundException {
        init();
        Database.setTable("Test");
        Database.addAccount("Matt", "password4");
        Database.deleteAccount("Matt", "password4");
        assertFalse(Database.validLogin("Matt", "password4"));
    }

    /**
     * tests getInfo()
     * @throws SQLException thrown if failed to write to database
     */
    @Test
    public void testGetInfo() throws SQLException {
        init();
        Database.setTable("Test");
        String[] expected = new String[]{"Sebastian", "password1", "0"};
        assertArrayEquals(expected, Database.getInfo("Sebastian"));
    }

    /**
     * tests getKeyInfo
     */
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

    /**
     * Tests various methods related to creating leaderboard tables, most notably: createTournament(), deleteTournament(),
     * addToTournament(), and removeFromTournament()
     * @throws SQLException thrown if failed to write to database
     */
    @Test
    public void testCreateAddToTournament() throws SQLException {
        init();
        Database.setTable("mastertournament");
        Database.createTournament("testtournament", "100");

        Database.addToTournament("Sebastian", "testtournament");
        assertFalse(Database.addToTournament("Sebastian", "testtournament")); // cannot add same account twice
        assertFalse(Database.removeFromTournament("yourmom", "testtournament")); // cannot remove account that's not there

        Database.setTable("testtournament");
//        System.out.println(Arrays.toString(Database.getInfo("Sebastian")));

        // GOOD EXAMPLE OF HOW RANK WILL NEED TO BE UPDATED, SETTING RANK TO ROW ONLY WORKS FOR INITIAL VALUES
        Database.addToTournament("Matt", "testtournament");
        Database.addToTournament("Sam", "testtournament");
        Database.addToTournament("Cole", "testtournament");
//        System.out.println(Arrays.toString(Database.getUserData("testtournament")));
        Database.removeFromTournament("Sebastian", "testtournament");

        Database.setTable("testtournament");
        Database.update(new String[]{"Sam", "1", "4"});
//        System.out.println(Arrays.toString(Database.getInfo("Matt")));

        Database.deleteTournament("testtournament");
        assertTrue(Database.createTournament("testtournament", "100")); // tournament deleted and then recreated successfully
        Database.deleteTournament("testtournament");

        assertFalse(Database.deleteTournament("testtournament"));
        assertFalse(Database.deleteAccount("hey", "hi"));
    }
}
