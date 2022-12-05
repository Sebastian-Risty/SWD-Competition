import java.io.*;
import java.util.Scanner;

/**
 * Encryptor contains is used to shift
 */
public class Encryptor {
    /**
     * in order alphabet, every character is uppercase
     */
    public final static char[] alphabet = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    /**
     * The encryption shift value. This differs from the key shift value because the key's updates
     */
    private int position;
    /**
     * the name of the encryption file
     */
    private final String fileName;

    /**
     * Sets the filename
     *
     * @param fileName the desired name of the encryption file
     */
    public Encryptor(String fileName) {
        this.fileName = fileName;
    }

    /**
     * shiftLetter takes in a letter and returns the appropriately shifted version of that letter. This function can
     * be used for both encryption and decryption. If decrypt is true, it will decrypt. If false, it will encrypt.
     * If the letter character is non-alphabetic, it will simply return the same character
     *
     * @param letter     the letter to be shifted
     * @param shiftValue the value the letter will be shifted by
     * @param decrypt    true if decryption, false if encryption. Used to determine the appropriate method of shifting
     * @return character of the shifted letter
     */
    private static char shiftLetter(char letter, int shiftValue, boolean decrypt) {
        if (!Character.isLetter(letter)) // if a character in the message isn't a letter, leave it as is
            return letter;
        int index = 0;
        for (char currLetter : alphabet) {
            if (currLetter == letter) {
                break;
            }
            index++;
        }
        // if you are encrypting, you need to take the index the letter is at and shift it up by the shift value
        // if you are decrypting, the letter has already been shifted, so you subtract by the shift value
        if (!decrypt && index + shiftValue < alphabet.length)
            return alphabet[index + shiftValue];
        else if (!decrypt && index + shiftValue >= alphabet.length) // if the shift is out of range, wrap it to the beginning by subtracting length
            return alphabet[index + shiftValue - alphabet.length];
        else if (decrypt && index - shiftValue >= 0)
            return alphabet[index - shiftValue];
        else
            return alphabet[index - shiftValue + alphabet.length];


    }

    /**
     * Shifts the given message based on the provided keyInfo. If decrypt is true, it will shift in the appropriate way
     * for decryption. If false, it will shift for encryption. The message is shifted by shifting each individual letter
     *
     * @param message The message to be shifted
     * @param keyInfo String array containing the shift index and key values from the key file
     * @param decrypt true if decryption, false if encryption
     * @return String of the shifted message
     */
    public static String shiftMessage(String message, String[] keyInfo, boolean decrypt) {
        int currPos = Integer.parseInt(keyInfo[0]);
        String[] keyVals = keyInfo[1].split(",");

        int[] shiftVals = new int[keyVals.length];
        for (int i = 0; i < keyVals.length; i++)
            shiftVals[i] = Integer.parseInt(keyVals[i]);

        // message = message.toUpperCase();
        StringBuilder output = new StringBuilder();
        int shiftIndex = currPos % shiftVals.length; // modulo operator handles cases where position is greater than shiftVals.length, without this there would be an out-of-bounds error
        for (int i = 0; i < message.length(); i++) {
            output.append(shiftLetter(message.charAt(i), shiftVals[shiftIndex], decrypt));
            shiftIndex++;
            if (shiftIndex >= shiftVals.length) {
                shiftIndex = 0;
            }
        }
        return output.toString();
    }

    /**
     * returns the information from a given fileName. If the fileName is not found, the output array will contain an error message
     *
     * @param fileName the name of the file to be read from
     * @return String array containing the information from fileName
     */
    public static String[] getFileInfo(String fileName) {
        String[] output = new String[2];
        try {
            File keyFile = new File(fileName);
            Scanner scnr = new Scanner(keyFile);
            int index = 0;
            while (scnr.hasNext()) {
                output[index] = scnr.nextLine();
                index++;
            }
        } catch (FileNotFoundException | NumberFormatException ex) {
            output[0] = "Error: file \"" + fileName + "\" not found";
            output[1] = "Error: file \"" + fileName + "\" not found";
        }
        return output;
    }

    /**
     * Replaces the key file's old shift index with a new shift index
     *
     * @param keyFileName the name of the key file
     * @param newPos      the position to replace the old position in the key file
     * @return String displaying whether the file was successfully updated
     */
    public static String updateKeyFile(String keyFileName, int newPos) {
        try {
            File updatedKey = new File(keyFileName);
            BufferedReader reader = new BufferedReader(new FileReader(updatedKey));

            reader.readLine(); // reads the old position
            String shiftVals = reader.readLine();

            FileWriter writer = new FileWriter(keyFileName);
            writer.write(newPos + "\n" + shiftVals);
            writer.close();
            reader.close();
            return "Successfully updated key file";
        } catch (IOException ex) {
            return "Could not update key: could not find key file";
        }
    }

    /**
     * Creates a file where the name is gotten from the instance variable fileName
     *
     * @return String message displaying whether the file was created successfully
     */
    public String createFile() {
        try {
            File encryptFile = new File(fileName);
            if (encryptFile.createNewFile()) {
                return "Encryptor file \"" + encryptFile.getName() + "\" created successfully";
            } else {
                return "Filename \"" + fileName + "\" already exists";
            }
        } catch (IOException e) {
            return "Error creating file";
        }
    }

    /**
     * Uses a FileWriter object to write to encryptor file. The first line is the instance variable position, the second
     * line is the inputted encrypted message.
     *
     * @param message the encrypted message to be written to the file
     * @return String displaying whether the file was successfully written to
     */
    public String writeToEncryptorFile(String message) {
        try {
            FileWriter keyWriter = new FileWriter(fileName);
            keyWriter.write(position + "\n" + message);
            keyWriter.close();
            return "Successfully wrote to file \"" + fileName + "\"";
        } catch (IOException ex) {
            return "An error occurred";
        }
    }

    /**
     * sets the position
     *
     * @param position the desired position
     */
    public void setPosition(int position) {
        this.position = position;
    }
}
