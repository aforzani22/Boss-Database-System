import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyConsoleAppTest {

    private InputStream originalSystemIn;

    @BeforeEach
    public void saveSystemIn() {
        originalSystemIn = System.in;
    }

    @AfterEach
    public void restoreSystemIn() {
        System.setIn(originalSystemIn);
    }

    private Enemy createValidEnemy() {
        return new Enemy(
                "Cut Man",
                "Robot Museum",
                100,
                20,
                5.5,
                "Thunder Beam",
                500,
                false
        );
    }

    private Enemy createSecondValidEnemy() {
        return new Enemy(
                "Guts Man",
                "Construction Site",
                300,
                35,
                3.0,
                "Hyper Bomb",
                900,
                false
        );
    }

    private EnemyManager createManager() {
        EnemyFileRepository repository = new EnemyFileRepository("|");
        return new EnemyManager(repository);
    }

    private EnemyConsoleApp createAppWithInput(String text) {
        InputStream input = new ByteArrayInputStream(text.getBytes());
        System.setIn(input);
        return new EnemyConsoleApp();
    }

    @Test
    public void testFileCanBeOpenedPositiveAndNegative() throws Exception {
        EnemyFileRepository repository = new EnemyFileRepository("|");

        // Positive test: a real file can be opened and loaded.
        File file = File.createTempFile("enemies", ".txt");

        PrintWriter writer = new PrintWriter(file);
        writer.println("Cut Man|Robot Museum|100|20|5.5|Thunder Beam|500|false");
        writer.close();

        ArrayList<Enemy> loadedEnemies = repository.loadEnemies(file.getAbsolutePath());

        assertEquals(1, loadedEnemies.size());
        assertEquals("Cut Man", loadedEnemies.get(0).getName());
        assertTrue(repository.getLastMessage().contains("Loaded 1 enemies"));

        // Negative test: a missing file should not crash the program.
        File missingFile = new File("this_file_does_not_exist_12345.txt");

        ArrayList<Enemy> missingEnemies = repository.loadEnemies(missingFile.getAbsolutePath());

        assertEquals(0, missingEnemies.size());
        assertTrue(repository.getLastMessage().contains("File was not found"));
    }

    @Test
    public void testHandleLoadEnemiesPositiveAndNegative() throws Exception {
        // Positive test: handleLoadEnemies should load from a valid file path.
        File file = File.createTempFile("enemies", ".txt");

        PrintWriter writer = new PrintWriter(file);
        writer.println("Cut Man|Robot Museum|100|20|5.5|Thunder Beam|500|false");
        writer.close();

        EnemyConsoleApp app = createAppWithInput(file.getAbsolutePath() + "\n");

        String message = app.handleLoadEnemies();

        assertTrue(message.contains("Load complete"));
        assertTrue(message.contains("Added 1"));

        // Negative test: handleLoadEnemies should handle a bad file path.
        EnemyConsoleApp badApp = createAppWithInput("fake_missing_file.txt\n");

        String badMessage = badApp.handleLoadEnemies();

        assertTrue(badMessage.contains("Load complete"));
        assertTrue(badMessage.contains("Added 0"));
        assertTrue(badMessage.contains("File was not found"));
    }

    @Test
    public void testAddEnemyPositiveAndNegative() {
        EnemyManager manager = createManager();

        // Positive test: valid enemy should be added.
        boolean added = manager.addEnemy(createValidEnemy());

        assertTrue(added);
        assertEquals(1, manager.getAllEnemies().size());
        assertTrue(manager.hasEnemy("Cut Man"));

        // Negative test: duplicate enemy should not be added.
        boolean duplicateAdded = manager.addEnemy(createValidEnemy());

        assertFalse(duplicateAdded);
        assertEquals(1, manager.getAllEnemies().size());
        assertTrue(manager.getLastMessage().contains("already exists"));

        // Negative test: invalid enemy should not be added.
        Enemy invalidEnemy = new Enemy(
                "",
                "Robot Museum",
                -100,
                20,
                5.5,
                "Thunder Beam",
                500,
                false
        );

        boolean invalidAdded = manager.addEnemy(invalidEnemy);

        assertFalse(invalidAdded);
        assertEquals(1, manager.getAllEnemies().size());
        assertTrue(manager.getLastMessage().contains("invalid"));
    }

    @Test
    public void testRemoveEnemyPositiveAndNegative() {
        EnemyManager manager = createManager();

        manager.addEnemy(createValidEnemy());

        // Positive test: existing enemy should be removed.
        boolean removed = manager.removeEnemyByName("Cut Man");

        assertTrue(removed);
        assertFalse(manager.hasEnemy("Cut Man"));
        assertEquals(0, manager.getAllEnemies().size());

        // Negative test: removing an enemy that does not exist should not crash.
        boolean removedMissing = manager.removeEnemyByName("Mega Man");

        assertFalse(removedMissing);
        assertTrue(manager.getLastMessage().contains("not found"));
    }

    @Test
    public void testUpdateEnemyPositiveAndNegative() {
        EnemyManager manager = createManager();

        manager.addEnemy(createValidEnemy());

        // Positive test: update health.
        boolean updatedHealth = manager.updateEnemyHealth("Cut Man", 250);

        assertTrue(updatedHealth);
        assertEquals(250, manager.getAllEnemies().get(0).getHealth());

        // Positive test: update name.
        boolean updatedName = manager.updateEnemyName("Cut Man", "Metal Man");

        assertTrue(updatedName);
        assertTrue(manager.hasEnemy("Metal Man"));
        assertFalse(manager.hasEnemy("Cut Man"));

        // Negative test: invalid health should not update.
        boolean badHealthUpdate = manager.updateEnemyHealth("Metal Man", -1);

        assertFalse(badHealthUpdate);
        assertEquals(250, manager.getAllEnemies().get(0).getHealth());
        assertTrue(manager.getLastMessage().contains("whole number"));

        // Negative test: updating an enemy that does not exist should fail safely.
        boolean missingUpdate = manager.updateEnemyDamage("Fake Boss", 50);

        assertFalse(missingUpdate);
        assertTrue(manager.getLastMessage().contains("not found"));
    }

    @Test
    public void testUpdateEnemyDuplicateNamePositiveAndNegative() {
        EnemyManager manager = createManager();

        manager.addEnemy(createValidEnemy());
        manager.addEnemy(createSecondValidEnemy());

        // Positive test: changing to a new unused name should work.
        boolean nameUpdated = manager.updateEnemyName("Cut Man", "Fire Man");

        assertTrue(nameUpdated);
        assertTrue(manager.hasEnemy("Fire Man"));

        // Negative test: changing to a name that already exists should fail.
        boolean duplicateNameUpdate = manager.updateEnemyName("Fire Man", "Guts Man");

        assertFalse(duplicateNameUpdate);
        assertTrue(manager.hasEnemy("Fire Man"));
        assertTrue(manager.hasEnemy("Guts Man"));
        assertTrue(manager.getLastMessage().contains("already exists"));
    }

    @Test
    public void testDisplayRosterPositiveAndNegative() {
        EnemyManager manager = createManager();

        // Negative test: displaying when no enemies exist should show an empty message.
        String emptyReport = manager.buildRosterReport();

        assertTrue(emptyReport.contains("No enemies"));

        // Positive test: displaying after adding an enemy should show enemy data.
        manager.addEnemy(createValidEnemy());

        String report = manager.buildRosterReport();

        assertTrue(report.contains("ENEMY ROSTER"));
        assertTrue(report.contains("Cut Man"));
        assertTrue(report.contains("Robot Museum"));
        assertTrue(report.contains("Thunder Beam"));
    }

    @Test
    public void testDifficultyCustomActionPositiveAndNegative() {
        EnemyManager manager = createManager();

        manager.addEnemy(createValidEnemy());

        // Positive test: custom action calculates difficulty.
        double score = manager.calculateDifficulty("Cut Man");

        assertEquals(327.5, score, 0.01);
        assertEquals("Medium", manager.getDifficultyRating("Cut Man"));
        assertTrue(manager.getLastMessage().contains("Difficulty calculated"));

        // Negative test: difficulty for missing enemy should fail safely.
        double missingScore = manager.calculateDifficulty("Fake Boss");

        assertEquals(-1, missingScore, 0.01);
        assertEquals("Enemy not found", manager.getDifficultyRating("Fake Boss"));
        assertTrue(manager.getLastMessage().contains("not found"));
    }

    @Test
    public void testRepositoryParsingPositiveAndNegative() {
        EnemyFileRepository repository = new EnemyFileRepository("|");

        // Positive test: correct line should parse into an Enemy object.
        Enemy enemy = repository.parseLine("Cut Man|Robot Museum|100|20|5.5|Thunder Beam|500|false");

        assertEquals("Cut Man", enemy.getName());
        assertEquals("Robot Museum", enemy.getGameArea());
        assertEquals(100, enemy.getHealth());
        assertEquals(20, enemy.getDamage());
        assertEquals(5.5, enemy.getSpeed(), 0.01);
        assertEquals("Thunder Beam", enemy.getWeakness());
        assertEquals(500, enemy.getRewardPoints());
        assertFalse(enemy.isDefeated());

        // Negative test: line with too few fields should throw an exception.
        assertThrows(IllegalArgumentException.class, () -> {
            repository.parseLine("Cut Man|Robot Museum|100");
        });

        // Negative test: boolean must be true or false.
        assertThrows(IllegalArgumentException.class, () -> {
            repository.parseLine("Cut Man|Robot Museum|100|20|5.5|Thunder Beam|500|maybe");
        });
    }

    @Test
    public void testBadLinesAreSkippedWhenLoadingFile() throws Exception {
        EnemyFileRepository repository = new EnemyFileRepository("|");

        File file = File.createTempFile("badEnemies", ".txt");

        PrintWriter writer = new PrintWriter(file);
        writer.println("Cut Man|Robot Museum|100|20|5.5|Thunder Beam|500|false");
        writer.println("Bad Line Without Enough Fields");
        writer.println("Broken Man|Robot Museum|wrong|20|5.5|Thunder Beam|500|false");
        writer.println("Pipe|Bad|Name|Robot Museum|100|20|5.5|Thunder Beam|500|false");
        writer.close();

        ArrayList<Enemy> enemies = repository.loadEnemies(file.getAbsolutePath());

        // Positive test: the good line loads.
        assertEquals(1, enemies.size());
        assertEquals("Cut Man", enemies.get(0).getName());

        // Negative test: bad lines are skipped instead of crashing the program.
        assertTrue(repository.getLastMessage().contains("Skipped"));
    }

    @Test
    public void testInputValidationPositiveAndNegative() {
        /*
         This simulates a user entering:
         - blank text first, then valid text
         - words instead of a number
         - a number outside the valid range
         - a valid number
         - an invalid boolean
         - a valid boolean
        */
        EnemyConsoleApp app = createAppWithInput(
                "\n" +
                        "Cut Man\n" +
                        "words\n" +
                        "1500\n" +
                        "500\n" +
                        "maybe\n" +
                        "true\n"
        );

        // Positive/negative text test:
        String text = app.readNonEmptyString("Enter name: ");
        assertEquals("Cut Man", text);

        // Positive/negative number test:
        int number = app.readRangedInt("Enter health: ", 1, 1000);
        assertEquals(500, number);

        // Positive/negative boolean test:
        boolean answer = app.readBoolean("Defeated? ");
        assertTrue(answer);
    }

    @Test
    public void testEnemyValidationPositiveAndNegative() {
        // Positive test: valid text and numbers should pass validation.
        assertTrue(Enemy.isValidText("Cut Man"));
        assertTrue(Enemy.isValidHealth(100));
        assertTrue(Enemy.isValidDamage(20));
        assertTrue(Enemy.isValidSpeed(5.5));
        assertTrue(Enemy.isValidRewardPoints(500));

        // Negative test: bad text and numbers should fail validation.
        assertFalse(Enemy.isValidText(""));
        assertFalse(Enemy.isValidText("Bad|Name"));
        assertFalse(Enemy.isValidHealth(0));
        assertFalse(Enemy.isValidHealth(1001));
        assertFalse(Enemy.isValidDamage(-1));
        assertFalse(Enemy.isValidDamage(201));
        assertFalse(Enemy.isValidSpeed(-1));
        assertFalse(Enemy.isValidSpeed(51));
        assertFalse(Enemy.isValidRewardPoints(-1));
        assertFalse(Enemy.isValidRewardPoints(100000));
    }
}