import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class EnemyFileRepository {
    private String delimiter;
    private String lastMessage;

    public EnemyFileRepository(String delimiter) {
        this.delimiter = delimiter;
        this.lastMessage = "Repository ready.";
    }

    // Reading and Loading the file
    public ArrayList<Enemy> loadEnemies(String filePath) {
        ArrayList<Enemy> loadedEnemies = new ArrayList<Enemy>();
        int badLines = 0;

        try (Scanner fileReader = new Scanner(new File(filePath))) {
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine().trim();

                if (line.length() > 0) {
                    try {
                        Enemy enemy = parseLine(line);

                        if (enemy.isValid()) {
                            loadedEnemies.add(enemy);
                        } else {
                            badLines++;
                        }
                    } catch (IllegalArgumentException error) {
                        badLines++;
                    }
                }
            }

            lastMessage = "Loaded " + loadedEnemies.size() + " enemies. Skipped " + badLines + " bad lines.";
        } catch (FileNotFoundException error) {
            lastMessage = "File was not found. No enemies were loaded.";
        }

        return loadedEnemies;
    }

    public boolean saveEnemies(String filePath, ArrayList<Enemy> enemies) {
        boolean saved = false;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Enemy enemy : enemies) {
                writer.println(formatEnemy(enemy));
            }

            lastMessage = "Saved " + enemies.size() + " enemies to " + filePath + ".";
            saved = true;
        } catch (IOException error) {
            lastMessage = "Could not save the file. Check the file path and try again.";
        }

        return saved;
    }

    public Enemy parseLine(String line) {
        String[] parts = line.split(Pattern.quote(delimiter), -1);

        if (parts.length != 8) {
            throw new IllegalArgumentException("Each line must have 8 fields.");
        }

        String name = parts[0].trim();
        String gameArea = parts[1].trim();
        int health = Integer.parseInt(parts[2].trim());
        int damage = Integer.parseInt(parts[3].trim());
        double speed = Double.parseDouble(parts[4].trim());
        String weakness = parts[5].trim();
        int rewardPoints = Integer.parseInt(parts[6].trim());
        boolean defeated = parseBoolean(parts[7].trim());

        return new Enemy(name, gameArea, health, damage, speed, weakness, rewardPoints, defeated);
    }

    public String formatEnemy(Enemy enemy) {
        return enemy.toFileLine(delimiter);
    }

    public boolean parseBoolean(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new IllegalArgumentException("Boolean must be true or false.");
        }
    }

    public String getLastMessage() {
        return lastMessage;
    }
}