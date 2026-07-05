public class Enemy {
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
        this.name = name;
        this.gameArea = gameArea;
        this.health = health;
        this.damage = damage;
        this.speed = speed;
        this.weakness = weakness;
        this.rewardPoints = rewardPoints;
        this.defeated = defeated;
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
        return name.trim().length() > 0
                && gameArea.trim().length() > 0
                && health > 0
                && damage >= 0
                && speed >= 0
                && weakness.trim().length() > 0
                && rewardPoints >= 0;
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
        this.name = name;
        return true;
    }

    public boolean setGameArea(String gameArea) {
        this.gameArea = gameArea;
        return true;
    }

    public boolean setHealth(int health) {
        this.health = health;
        return true;
    }

    public boolean setDamage(int damage) {
        this.damage = damage;
        return true;
    }

    public boolean setSpeed(double speed) {
        this.speed = speed;
        return true;
    }

    public boolean setWeakness(String weakness) {
        this.weakness = weakness;
        return true;
    }

    public boolean setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
        return true;
    }

    public boolean setDefeated(boolean defeated) {
        this.defeated = defeated;
        return true;
    }
}