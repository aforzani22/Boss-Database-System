import java.util.*;

public class EnemyConsoleApp {
    private EnemyManager manager;
    private Scanner input;
    private String filePath;

    public EnemyConsoleApp() {
        EnemyFileRepository repository = new EnemyFileRepository("|");
        this.manager = new EnemyManager(repository);
        this.input = new Scanner(System.in);
        this.filePath = "enemies.txt";
    }

    public static void main(String[] args) {
        EnemyConsoleApp app = new EnemyConsoleApp();
        app.run();
    }

    public int run() {
        boolean running = true;

        // The Options for the menu
        while (running) {
            System.out.println(buildMenuText());
            int choice = readMenuChoice();
            String message;

            switch (choice) {
                case 1:
                    message = handleAddEnemyManually();
                    break;
                case 2:
                    message = handleLoadEnemies();
                    break;
                case 3:
                    message = handleSaveEnemies();
                    break;
                case 4:
                    message = manager.buildRosterReport();
                    break;
                case 5:
                    message = handleUpdateEnemy();
                    break;
                case 6:
                    message = handleRemoveEnemy();
                    break;
                case 7:
                    message = handleCalculateDifficulty();
                    break;
                case 0:
                    message = "Goodbye! Program closed by user choice.";
                    running = false;
                    break;
                default:
                    message = "Invalid menu choice. Please choose 0 through 7.";
                    break;
            }

            System.out.println(message);
        }

        return 0;
    }

    // The Menu
    public String buildMenuText() {
        return "\n---------- MENU ----------\n"
                + "1. Add enemy manually\n"
                + "2. Load enemies from text file\n"
                + "3. Save enemies to text file\n"
                + "4. Display all enemies\n"
                + "5. Update enemy by name\n"
                + "6. Remove enemy by name\n"
                + "7. Calculate difficulty\n"
                + "0. Exit";
    }

    public int readMenuChoice() {
        return readInt("Enter choice: ");
    }

    public Enemy readEnemyFromInput() {
        String name = readNonEmptyString("Name: ");
        String gameArea = readNonEmptyString("Game area: ");
        int health = readPositiveInt("Health: ");
        int damage = readZeroOrHigherInt("Damage: ");
        double speed = readZeroOrHigherDouble("Speed: ");
        String weakness = readNonEmptyString("Weakness: ");
        int rewardPoints = readZeroOrHigherInt("Reward points: ");
        boolean defeated = readBoolean("Defeated? Enter true or false: ");

        return new Enemy(name, gameArea, health, damage, speed, weakness, rewardPoints, defeated);
    }

    public String handleAddEnemyManually() {
        System.out.println("\nEnter enemy information.");
        Enemy enemy = readEnemyFromInput();
        boolean success = manager.addEnemy(enemy);
        return buildStatusMessage(success, "add enemy");
    }

    public String handleLoadEnemies() {
        filePath = readNonEmptyString("Enter text file path to load from: ");
        int loaded = manager.loadEnemies(filePath);
        return "Load complete. Added " + loaded + " enemies. " + manager.getLastMessage();
    }

    public String handleSaveEnemies() {
        filePath = readNonEmptyString("Enter text file path to save to: ");
        boolean success = manager.saveEnemies(filePath);
        return buildStatusMessage(success, "save enemies");
    }

    public String handleUpdateEnemy() {
        String name = readNonEmptyString("Enter the name of the enemy to update: ");

        if (!manager.hasEnemy(name)) {
            return "Enemy not found. Nothing was updated.";
        }

        System.out.println("Enter the new information for this enemy.");
        Enemy updatedEnemy = readEnemyFromInput();
        boolean success = manager.updateEnemy(name, updatedEnemy);
        return buildStatusMessage(success, "update enemy");
    }

    public String handleRemoveEnemy() {
        String name = readNonEmptyString("Enter the name of the enemy to remove: ");

        if (!manager.hasEnemy(name)) {
            return "Enemy not found. Nothing was removed.";
        }

        boolean confirmed = readYesOrNo("Are you sure you want to remove this enemy? Enter yes or no: ");

        if (!confirmed) {
            return "Remove cancelled. No changes were made.";
        }

        boolean success = manager.removeEnemyByName(name);
        return buildStatusMessage(success, "remove enemy");
    }

    public String handleCalculateDifficulty() {
        String name = readNonEmptyString("Enter enemy name: ");
        double score = manager.calculateDifficulty(name);

        if (score < 0) {
            return manager.getLastMessage();
        }

        return "Difficulty Score: " + score + "\nRating: " + manager.getDifficultyRating(name);
    }

    public String buildStatusMessage(boolean success, String action) {
        if (success) {
            return "Success: " + action + ". " + manager.getLastMessage();
        } else {
            return "Failed to " + action + ". " + manager.getLastMessage();
        }
    }

    public String readNonEmptyString(String prompt) {
        String text = "";
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            text = input.nextLine().trim();

            if (text.length() > 0) {
                valid = true;
            } else {
                System.out.println("Input cannot be empty. Try again.");
            }
        }

        return text;
    }

    public int readInt(String prompt) {
        boolean valid = false;
        int number = 0;

        while (!valid) {
            System.out.print(prompt);
            String text = input.nextLine().trim();

            try {
                number = Integer.parseInt(text);
                valid = true;
            } catch (NumberFormatException error) {
                System.out.println("Please enter a whole number.");
            }
        }

        return number;
    }

    public int readPositiveInt(String prompt) {
        int number = readInt(prompt);

        while (number <= 0) {
            System.out.println("Please enter a number greater than 0.");
            number = readInt(prompt);
        }

        return number;
    }

    public int readZeroOrHigherInt(String prompt) {
        int number = readInt(prompt);

        while (number < 0) {
            System.out.println("Please enter 0 or higher.");
            number = readInt(prompt);
        }

        return number;
    }

    public double readDouble(String prompt) {
        boolean valid = false;
        double number = 0;

        while (!valid) {
            System.out.print(prompt);
            String text = input.nextLine().trim();

            try {
                number = Double.parseDouble(text);
                valid = true;
            } catch (NumberFormatException error) {
                System.out.println("Please enter a valid decimal number.");
            }
        }

        return number;
    }

    public double readZeroOrHigherDouble(String prompt) {
        double number = readDouble(prompt);

        while (number < 0) {
            System.out.println("Please enter 0 or higher.");
            number = readDouble(prompt);
        }

        return number;
    }

    public boolean readBoolean(String prompt) {
        boolean answer = false;
        boolean valid = false;

        while (!valid) {
            String text = readNonEmptyString(prompt);

            if (text.equalsIgnoreCase("true")) {
                answer = true;
                valid = true;
            } else if (text.equalsIgnoreCase("false")) {
                answer = false;
                valid = true;
            } else {
                System.out.println("Please enter true or false.");
            }
        }

        return answer;
    }

    public boolean readYesOrNo(String prompt) {
        boolean answer = false;
        boolean valid = false;

        while (!valid) {
            String text = readNonEmptyString(prompt);

            if (text.equalsIgnoreCase("yes") || text.equalsIgnoreCase("y")) {
                answer = true;
                valid = true;
            } else if (text.equalsIgnoreCase("no") || text.equalsIgnoreCase("n")) {
                answer = false;
                valid = true;
            } else {
                System.out.println("Please enter yes or no.");
            }
        }

        return answer;
    }
}