import java.util.Objects;
import java.util.Scanner;

public class TestGame {
    public static void main(String[] args) { //TODO: update to work with client with similar schema to a live game


        Scanner scnr = new Scanner(System.in);
        for (int i = 0; i < 2; i++) {
            System.out.println("\nPlayer" + (i + 1) + "'s turn");
            boolean status = true;
            while (status) {
                // System.out.println("\nYour letters are: " + game.getLetters());
                System.out.print("\nEnter your guess: ");
                String input = scnr.next();
                if (Objects.equals(input, "quit")) {
                    status = false;
                } else {
                    if (i == 0) {
                        //game.guess(p1, input);
                    } else {
                        //game.guess(p2, input);
                    }
                }
            }
        }
    }
}
