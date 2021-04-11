import java.util.Scanner;
import java.io.File;

public class AddEcfaRadar {
    public static void main(String[] args) {
        char option = menu();
        //System.out.println(option);
        switch (option) {
            case 'a':
                getFile();
            case 'b':
                chooseDifficulty();
            case 'c':
                System.exit(0);
        }
    }

    /**
     * Displays the main menu.
     * @return The option selected.
     */
    private static char menu() {
        Scanner kb = new Scanner(System.in);
        char option;

        do {
            System.out.println("Welcome to the ECFA Tech Radar " +
                               "Injection Tool!\n");
            System.out.println("Enter the letter of your chosen" +
                               "operation");
            System.out.println("\ta) Insert ECFA tech values");
            System.out.println("\tb) List references for tech values");
            System.out.println("\tc) Exit");
            option = kb.nextLine().toLowerCase().charAt(0);

            if (!validateMenuOption(option)) {
                System.out.println("Invalid choice, please try again.");
            }
        } while (!validateMenuOption(option));

        kb.close();
        return option;
    }

    /**
     * Checks whether an option for the main menu is valid.
     * @param option The character to be processed.
     * @return True if a, b, or c was typed, false otherwise.
     */
    private static boolean validateMenuOption(char option) {
        switch (option) {
            case 'a': case 'b': case 'c':
                return true;
            default:
                return false;
        }
    }

    private static void getFile() {

    }

    private static void chooseDifficulty() {
    }
}

