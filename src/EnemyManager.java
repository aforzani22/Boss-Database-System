import java.util.*;

public class EnemyManager {
    private ArrayList<Enemy> enemies;
    private EnemyFileRepository repository;
    private String lastMessage;

    public EnemyManager(EnemyFileRepository repository) {
        this.repository = repository;
        this.enemies = new ArrayList<Enemy>();
        this.lastMessage = "Manager ready.";
    }

    public boolean addEnemy(Enemy enemy) {
        if (!enemy.isValid()) {
            lastMessage = "Enemy was not added because one or more fields were invalid.";
            return false;
        }

        if (findEnemyIndexByName(enemy.getName()) != -1) {
            lastMessage = "Enemy was not added because that name already exists.";
            return false;
        }

        enemies.add(enemy);
        lastMessage = "Enemy added: " + enemy.getName();
        return true;
    }

    public boolean removeEnemyByName(String name) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Nothing was removed.";
            return false;
        }

        String removedName = enemies.get(index).getName();
        enemies.remove(index);
        lastMessage = "Enemy removed: " + removedName;
        return true;
    }

    public boolean updateEnemy(String name, Enemy updatedEnemy) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Nothing was updated.";
            return false;
        }

        if (!updatedEnemy.isValid()) {
            lastMessage = "Enemy was not updated because one or more fields were invalid.";
            return false;
        }

        int duplicateIndex = findEnemyIndexByName(updatedEnemy.getName());

        if (duplicateIndex != -1 && duplicateIndex != index) {
            lastMessage = "Enemy was not updated because the new name already exists.";
            return false;
        }

        enemies.set(index, updatedEnemy);
        lastMessage = "Enemy updated: " + updatedEnemy.getName();
        return true;
    }

    public int findEnemyIndexByName(String name) {
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).getName().trim().equalsIgnoreCase(name.trim())) {
                return i;
            }
        }

        return -1;
    }

    public boolean hasEnemy(String name) {
        return findEnemyIndexByName(name) != -1;
    }

    public ArrayList<Enemy> getAllEnemies() {
        return new ArrayList<Enemy>(enemies);
    }

    public double calculateDifficulty(String name) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Difficulty was not calculated.";
            return -1;
        }

        lastMessage = "Difficulty calculated for " + enemies.get(index).getName() + ".";
        return enemies.get(index).calculateDifficultyScore();
    }

    public String getDifficultyRating(String name) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            return "Enemy not found";
        }

        return enemies.get(index).getDifficultyRating();
    }

    public String buildRosterReport() {
        if (enemies.size() == 0) {
            return "No enemies are currently stored.";
        }

        StringBuilder report = new StringBuilder();
        report.append("\n---------- ENEMY ROSTER ----------\n");

        for (int i = 0; i < enemies.size(); i++) {
            report.append(i + 1).append(". ").append(enemies.get(i).toString()).append("\n");
        }

        return report.toString();
    }

    public int loadEnemies(String filePath) {
        ArrayList<Enemy> loadedEnemies = repository.loadEnemies(filePath);
        int added = 0;
        int skippedDuplicates = 0;

        for (Enemy enemy : loadedEnemies) {
            if (addEnemy(enemy)) {
                added++;
            } else {
                skippedDuplicates++;
            }
        }

        lastMessage = repository.getLastMessage() + " Added " + added
                + " new enemies. Skipped " + skippedDuplicates + " duplicates.";
        return added;
    }

    public boolean saveEnemies(String filePath) {
        boolean saved = repository.saveEnemies(filePath, enemies);
        lastMessage = repository.getLastMessage();
        return saved;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}