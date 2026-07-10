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

    public boolean updateEnemyName(String oldName, String newName) {
        int index = findEnemyIndexByName(oldName);

        if (index == -1) {
            lastMessage = "Enemy not found. Name was not updated.";
            return false;
        }

        if (!Enemy.isValidText(newName)) {
            lastMessage = "Name was not updated. Enter 1-30 characters and do not use |.";
            return false;
        }

        int duplicateIndex = findEnemyIndexByName(newName);

        if (duplicateIndex != -1 && duplicateIndex != index) {
            lastMessage = "Name was not updated because that name already exists.";
            return false;
        }

        enemies.get(index).setName(newName);
        lastMessage = "Enemy name updated to " + enemies.get(index).getName() + ".";
        return true;
    }

    public boolean updateEnemyGameArea(String name, String gameArea) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Area was not updated.";
            return false;
        }

        if (enemies.get(index).setGameArea(gameArea)) {
            lastMessage = "Area updated for " + enemies.get(index).getName() + ".";
            return true;
        }

        lastMessage = "Area was not updated. Enter 1-30 characters and do not use |.";
        return false;
    }

    public boolean updateEnemyHealth(String name, int health) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Health was not updated.";
            return false;
        }

        if (enemies.get(index).setHealth(health)) {
            lastMessage = "Health updated for " + enemies.get(index).getName() + ".";
            return true;
        }

        lastMessage = "Health was not updated. Enter a whole number from "
                + Enemy.MIN_HEALTH + " to " + Enemy.MAX_HEALTH + ".";
        return false;
    }

    public boolean updateEnemyDamage(String name, int damage) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Damage was not updated.";
            return false;
        }

        if (enemies.get(index).setDamage(damage)) {
            lastMessage = "Damage updated for " + enemies.get(index).getName() + ".";
            return true;
        }

        lastMessage = "Damage was not updated. Enter a whole number from "
                + Enemy.MIN_DAMAGE + " to " + Enemy.MAX_DAMAGE + ".";
        return false;
    }

    public boolean updateEnemySpeed(String name, double speed) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Speed was not updated.";
            return false;
        }

        if (enemies.get(index).setSpeed(speed)) {
            lastMessage = "Speed updated for " + enemies.get(index).getName() + ".";
            return true;
        }

        lastMessage = "Speed was not updated. Enter a decimal number from "
                + Enemy.MIN_SPEED + " to " + Enemy.MAX_SPEED + ".";
        return false;
    }

    public boolean updateEnemyWeakness(String name, String weakness) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Weakness was not updated.";
            return false;
        }

        if (enemies.get(index).setWeakness(weakness)) {
            lastMessage = "Weakness updated for " + enemies.get(index).getName() + ".";
            return true;
        }

        lastMessage = "Weakness was not updated. Enter 1-30 characters and do not use |.";
        return false;
    }

    public boolean updateEnemyRewardPoints(String name, int rewardPoints) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Reward points were not updated.";
            return false;
        }

        if (enemies.get(index).setRewardPoints(rewardPoints)) {
            lastMessage = "Reward points updated for " + enemies.get(index).getName() + ".";
            return true;
        }

        lastMessage = "Reward points were not updated. Enter a whole number from "
                + Enemy.MIN_REWARD_POINTS + " to " + Enemy.MAX_REWARD_POINTS + ".";
        return false;
    }

    public boolean updateEnemyDefeated(String name, boolean defeated) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            lastMessage = "Enemy not found. Defeated status was not updated.";
            return false;
        }

        enemies.get(index).setDefeated(defeated);
        lastMessage = "Defeated status updated for " + enemies.get(index).getName() + ".";
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

    public String buildEnemyUpdateReport(String name) {
        int index = findEnemyIndexByName(name);

        if (index == -1) {
            return "Enemy not found.";
        }

        Enemy enemy = enemies.get(index);
        return "\nCurrent enemy values:\n"
                + "1. Name: " + enemy.getName() + "\n"
                + "2. Game area: " + enemy.getGameArea() + "\n"
                + "3. Health: " + enemy.getHealth() + "\n"
                + "4. Damage: " + enemy.getDamage() + "\n"
                + "5. Speed: " + enemy.getSpeed() + "\n"
                + "6. Weakness: " + enemy.getWeakness() + "\n"
                + "7. Reward points: " + enemy.getRewardPoints() + "\n"
                + "8. Defeated: " + enemy.isDefeated();
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