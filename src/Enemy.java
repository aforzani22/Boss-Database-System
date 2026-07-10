public class Enemy {
    public static final int MAX_TEXT_LENGTH = 30;
    public static final int MIN_HEALTH = 1;
    public static final int MAX_HEALTH = 1000;
    public static final int MIN_DAMAGE = 0;
    public static final int MAX_DAMAGE = 200;
    public static final double MIN_SPEED = 0.0;
    public static final double MAX_SPEED = 50.0;
    public static final int MIN_REWARD_POINTS = 0;
    public static final int MAX_REWARD_POINTS = 99999;

    private String name;
    private String gameArea;
    private int health;
    private int damage;
    private double speed;
    private String weakness;
    private int rewardPoints;
    private boolean defeated;

    public Enemy(String name, String gameArea, int health, int damage, double speed,
                 String weakness, int rewardPoints, boolean defeated) {
        this.name = "";
        this.gameArea = "";
        this.health = 0;
        this.damage = -1;
        this.speed = -1;
        this.weakness = "";
        this.rewardPoints = -1;
        this.defeated = defeated;

        setName(name);
        setGameArea(gameArea);
        setHealth(health);
        setDamage(damage);
        setSpeed(speed);
        setWeakness(weakness);
        setRewardPoints(rewardPoints);
    }

    public double calculateDifficultyScore() {
        return health + (damage * 10) + (speed * 5);
    }

    // Calculating the Difficulty
    public String getDifficultyRating() {
        double score = calculateDifficultyScore();

        if (score <= 300) {
            return "Easy";
        } else if (score <= 600) {
            return "Medium";
        } else if (score <= 850) {
            return "Hard";
        } else {
            return "Very Hard";
        }
    }

    public boolean isValid() {
        return isValidText(name)
                && isValidText(gameArea)
                && isValidHealth(health)
                && isValidDamage(damage)
                && isValidSpeed(speed)
                && isValidText(weakness)
                && isValidRewardPoints(rewardPoints);
    }

    public static boolean isValidText(String text) {
        return text != null
                && text.trim().length() > 0
                && text.trim().length() <= MAX_TEXT_LENGTH
                && !text.contains("|");
    }

    public static boolean isValidHealth(int value) {
        return value >= MIN_HEALTH && value <= MAX_HEALTH;
    }

    public static boolean isValidDamage(int value) {
        return value >= MIN_DAMAGE && value <= MAX_DAMAGE;
    }

    public static boolean isValidSpeed(double value) {
        return value >= MIN_SPEED && value <= MAX_SPEED;
    }

    public static boolean isValidRewardPoints(int value) {
        return value >= MIN_REWARD_POINTS && value <= MAX_REWARD_POINTS;
    }

    // Reading the files
    public String toFileLine(String delimiter) {
        return name + delimiter + gameArea + delimiter + health + delimiter + damage + delimiter
                + speed + delimiter + weakness + delimiter + rewardPoints + delimiter + defeated;
    }

    public String toString() {
        return "Name: " + name
                + " | Area: " + gameArea
                + " | Health: " + health
                + " | Damage: " + damage
                + " | Speed: " + speed
                + " | Weakness: " + weakness
                + " | Reward Points: " + rewardPoints
                + " | Defeated: " + defeated
                + " | Difficulty: " + calculateDifficultyScore()
                + " (" + getDifficultyRating() + ")";
    }

    public String getName() {
        return name;
    }

    public String getGameArea() {
        return gameArea;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public double getSpeed() {
        return speed;
    }

    public String getWeakness() {
        return weakness;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public boolean setName(String name) {
        if (isValidText(name)) {
            this.name = name.trim();
            return true;
        }

        return false;
    }

    public boolean setGameArea(String gameArea) {
        if (isValidText(gameArea)) {
            this.gameArea = gameArea.trim();
            return true;
        }

        return false;
    }

    public boolean setHealth(int health) {
        if (isValidHealth(health)) {
            this.health = health;
            return true;
        }

        return false;
    }

    public boolean setDamage(int damage) {
        if (isValidDamage(damage)) {
            this.damage = damage;
            return true;
        }

        return false;
    }

    public boolean setSpeed(double speed) {
        if (isValidSpeed(speed)) {
            this.speed = speed;
            return true;
        }

        return false;
    }

    public boolean setWeakness(String weakness) {
        if (isValidText(weakness)) {
            this.weakness = weakness.trim();
            return true;
        }

        return false;
    }

    public boolean setRewardPoints(int rewardPoints) {
        if (isValidRewardPoints(rewardPoints)) {
            this.rewardPoints = rewardPoints;
            return true;
        }

        return false;
    }

    public boolean setDefeated(boolean defeated) {
        this.defeated = defeated;
        return true;
    }
}