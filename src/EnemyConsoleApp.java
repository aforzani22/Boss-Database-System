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

    public String buildUpdateMenuText() {
        return "\n----- UPDATE ENEMY MENU -----\n"
                + "1. Change name\n"
                + "2. Change game area\n"
                + "3. Change health\n"
                + "4. Change damage\n"
                + "5. Change speed\n"
                + "6. Change weakness\n"
                + "7. Change reward points\n"
                + "8. Change defeated status\n"
                + "0. Finish updating this enemy";
    }

    public int readMenuChoice() {
        return readInt("Enter choice as a whole number from 0 to 7: ");
    }

    public Enemy readEnemyFromInput() {
        String name = readEnemyText("Name: ");
        String gameArea = readEnemyText("Game area (text, example: Wily's Castle): ");
        int health = readRangedInt("Health (whole number 1-1000): ", Enemy.MIN_HEALTH, Enemy.MAX_HEALTH);
        int damage = readRangedInt("Damage (whole number 0-200): ", Enemy.MIN_DAMAGE, Enemy.MAX_DAMAGE);
        double speed = readRangedDouble("Speed (decimal 0.0-50.0): ", Enemy.MIN_SPEED, Enemy.MAX_SPEED);
        String weakness = readEnemyText("Weakness (text, example: Fire): ");
        int rewardPoints = readRangedInt("Reward points (whole number 0-99999): ", Enemy.MIN_REWARD_POINTS, Enemy.MAX_REWARD_POINTS);
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
        filePath = readNonEmptyString("Enter text file path to load from (example: enemies.txt): ");
        int loaded = manager.loadEnemies(filePath);
        return "Load complete. Added " + loaded + " enemies. " + manager.getLastMessage();
    }

    public String handleSaveEnemies() {
        filePath = readNonEmptyString("Enter text file path to save to (example: enemies.txt): ");
        boolean success = manager.saveEnemies(filePath);
        return buildStatusMessage(success, "save enemies");
    }

    public String handleUpdateEnemy() {
        String currentName = readNonEmptyString("Enter the name of the enemy to update: ");

        if (!manager.hasEnemy(currentName)) {
            return "Enemy not found.Please check the spelling and try again.";
        }

        boolean updating = true;
        boolean changedSomething = false;

        while (updating) {
            System.out.println(manager.buildEnemyUpdateReport(currentName));
            System.out.println(buildUpdateMenuText());
            int choice = readInt("Choose the field to update, or enter 0 to finish: ");
            boolean success = false;

            switch (choice) {
                case 1:
                    String newName = readEnemyText("New name (text, 1-30 characters, no | symbol): ");
                    success = manager.updateEnemyName(currentName, newName);

                    if (success) {
                        currentName = newName;
                    }
                    break;
                case 2:
                    String gameArea = readEnemyText("New game area (text): ");
                    success = manager.updateEnemyGameArea(currentName, gameArea);
                    break;
                case 3:
                    int health = readRangedInt("New health (whole number 1-1000): ", Enemy.MIN_HEALTH, Enemy.MAX_HEALTH);
                    success = manager.updateEnemyHealth(currentName, health);
                    break;
                case 4:
                    int damage = readRangedInt("New damage (whole number 0-200): ", Enemy.MIN_DAMAGE, Enemy.MAX_DAMAGE);
                    success = manager.updateEnemyDamage(currentName, damage);
                    break;
                case 5:
                    double speed = readRangedDouble("New speed (decimal 0.0-50.0): ", Enemy.MIN_SPEED, Enemy.MAX_SPEED);
                    success = manager.updateEnemySpeed(currentName, speed);
                    break;
                case 6:
                    String weakness = readEnemyText("New weakness (text): ");
                    success = manager.updateEnemyWeakness(currentName, weakness);
                    break;
                case 7:
                    int rewardPoints = readRangedInt("New reward points (whole number 0-99999): ", Enemy.MIN_REWARD_POINTS, Enemy.MAX_REWARD_POINTS);
                    success = manager.updateEnemyRewardPoints(currentName, rewardPoints);
                    break;
                case 8:
                    boolean defeated = readBoolean("New defeated status. Enter true or false: ");
                    success = manager.updateEnemyDefeated(currentName, defeated);
                    break;
                case 0:
                    updating = false;
                    break;
                default:
                    System.out.println("Invalid update choice. Please choose 0 through 8.");
                    break;
            }

            if (choice >= 1 && choice <= 8) {
                System.out.println(manager.getLastMessage());

                if (success) {
                    changedSomething = true;
                }
            }
        }

        if (changedSomething) {
            return "Update finished. " + manager.getLastMessage();
        }

        return "Update finished. No changes were made.";
    }

    public String handleRemoveEnemy() {
        String name = readNonEmptyString("Enter the name of the enemy to remove: ");

        if (!manager.hasEnemy(name)) {
            return "Enemy not found. Please check the spelling and try again.";
        }

        boolean confirmed = readYesOrNo("Are you sure you want to remove this enemy? Enter yes or no: ");

        if (!confirmed) {
            return "Remove cancelled. No changes were made.";
        }

        boolean success = manager.removeEnemyByName(name);
        return buildStatusMessage(success, "remove enemy");
    }

    public String handleCalculateDifficulty() {
        String name = readNonEmptyString("Enter enemy name to calculate difficulty: ");
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

    public String readEnemyText(String prompt) {
        String text = "";
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            text = input.nextLine().trim();

            if (Enemy.isValidText(text)) {
                valid = true;
            } else {
                System.out.println("Enter text. Try again.");
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
                System.out.println("Please enter a whole number, not words or decimals.");
            }
        }

        return number;
    }

    public int readRangedInt(String prompt, int minimum, int maximum) {
        int number = readInt(prompt);

        while (number < minimum || number > maximum) {
            System.out.println("Please enter a whole number from " + minimum + " to " + maximum + ".");
            number = readInt(prompt);
        }

        return number;
    }

    public int readPositiveInt(String prompt) {
        return readRangedInt(prompt, 1, Integer.MAX_VALUE);
    }

    public int readZeroOrHigherInt(String prompt) {
        return readRangedInt(prompt, 0, Integer.MAX_VALUE);
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
                System.out.println("Please enter a decimal number, like 2.5 or 10.");
            }
        }

        return number;
    }

    public double readRangedDouble(String prompt, double minimum, double maximum) {
        double number = readDouble(prompt);

        while (number < minimum || number > maximum) {
            System.out.println("Please enter a decimal number from " + minimum + " to " + maximum + ".");
            number = readDouble(prompt);
        }

        return number;
    }

    public double readZeroOrHigherDouble(String prompt) {
        return readRangedDouble(prompt, 0, Double.MAX_VALUE);
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
                System.out.println("Please enter true or false only.");
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