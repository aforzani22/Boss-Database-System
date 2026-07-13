import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class EnemyGUI extends JFrame {

    // Manages the enemy data
    private EnemyManager manager;

    // Colors
    private static final Color WINDOW_BACKGROUND = new Color(4, 8, 54);
    private static final Color PANEL_BACKGROUND = new Color(8, 16, 76);
    private static final Color FIELD_BACKGROUND = new Color(29, 43, 122);
    private static final Color LIST_BACKGROUND = new Color(24, 35, 115);
    private static final Color LIST_SELECTED = new Color(43, 61, 165);

    private static final Color TEXT_COLOR = new Color(210, 230, 255);
    private static final Color LIGHT_BLUE = new Color(132, 220, 255);
    private static final Color BUTTON_BLUE = new Color(47, 72, 180);
    private static final Color BUTTON_PURPLE = new Color(62, 53, 168);
    private static final Color RED = new Color(255, 0, 69);
    private static final Color GREEN = new Color(119, 255, 151);
    private static final Color WHITE = new Color(245, 245, 255);

    // Enemy roster
    private DefaultListModel<String> enemyListModel;
    private JList<String> enemyList;

    // Enemy information fields
    private JTextField nameField;
    private JTextField gameAreaField;
    private JTextField healthField;
    private JTextField damageField;
    private JTextField speedField;
    private JTextField weaknessField;
    private JTextField rewardPointsField;
    private JCheckBox defeatedCheckBox;

    // Difficulty information
    private JLabel difficultyLabel;
    private JLabel ratingLabel;
    private JLabel statusLabel;

    // Set up the manager and build the GUI
    public EnemyGUI() {
        EnemyFileRepository repository = new EnemyFileRepository("|");
        manager = new EnemyManager(repository);

        buildWindow();
        refreshEnemyList();
    }

    // Start the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName()
                );
            } catch (Exception error) {
                System.out.println("Could not change the appearance.");
            }

            EnemyGUI gui = new EnemyGUI();
            gui.setVisible(true);
        });
    }

    // Build the main window
    private void buildWindow() {
        setTitle("Boss Database System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 660);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(WINDOW_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(createRosterPanel(), BorderLayout.WEST);
        mainPanel.add(createDetailsPanel(), BorderLayout.CENTER);
        mainPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // Create the title at the top
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(WINDOW_BACKGROUND);

        JLabel titleLabel = new JLabel("Boss Database System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        titleLabel.setForeground(WHITE);

        panel.add(titleLabel);

        return panel;
    }

    // Create the enemy list
    private JPanel createRosterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PANEL_BACKGROUND);
        panel.setPreferredSize(new Dimension(290, 480));
        panel.setBorder(createSectionBorder("ENEMY ROSTER"));

        enemyListModel = new DefaultListModel<String>();
        enemyList = new JList<String>(enemyListModel);

        enemyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enemyList.setBackground(PANEL_BACKGROUND);
        enemyList.setForeground(TEXT_COLOR);
        enemyList.setSelectionBackground(LIST_SELECTED);
        enemyList.setSelectionForeground(WHITE);
        enemyList.setFont(new Font("Arial", Font.PLAIN, 18));
        enemyList.setFixedCellHeight(45);
        enemyList.setBorder(new EmptyBorder(8, 8, 8, 8));

        enemyList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                label.setFont(new Font("Arial", Font.PLAIN, 18));
                label.setForeground(TEXT_COLOR);
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(8, 15, 8, 15));

                if (isSelected) {
                    label.setBackground(LIST_SELECTED);
                    label.setForeground(WHITE);
                    label.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(LIGHT_BLUE, 1),
                            new EmptyBorder(7, 14, 7, 14)
                    ));
                } else {
                    label.setBackground(LIST_BACKGROUND);
                }

                return label;
            }
        });

        // Load an enemy
        enemyList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedEnemy();
            }
        });

        JScrollPane scrollPane = new JScrollPane(enemyList);
        scrollPane.setBorder(new LineBorder(LIGHT_BLUE, 1));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);

        // File buttons
        JButton loadButton = createButton(
                "Load File",
                BUTTON_BLUE,
                LIGHT_BLUE
        );

        JButton saveButton = createButton(
                "Save File",
                BUTTON_PURPLE,
                LIGHT_BLUE
        );

        loadButton.addActionListener(event -> loadEnemiesFromFile());
        saveButton.addActionListener(event -> saveEnemiesToFile());

        JPanel fileButtonPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        fileButtonPanel.setBackground(PANEL_BACKGROUND);
        fileButtonPanel.add(loadButton);
        fileButtonPanel.add(saveButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(fileButtonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Create the enemy information form
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(WINDOW_BACKGROUND);
        panel.setBorder(createSectionBorder("ENEMY DETAILS"));

        // Create the text fields
        nameField = createTextField();
        gameAreaField = createTextField();
        healthField = createTextField();
        damageField = createTextField();
        speedField = createTextField();
        weaknessField = createTextField();
        rewardPointsField = createTextField();

        // Create the defeated checkbox
        defeatedCheckBox = new JCheckBox("Enemy has been defeated");
        defeatedCheckBox.setBackground(WINDOW_BACKGROUND);
        defeatedCheckBox.setForeground(TEXT_COLOR);
        defeatedCheckBox.setFocusPainted(false);
        defeatedCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));

        // Arrange the form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WINDOW_BACKGROUND);

        addFormItem(formPanel, "Name", nameField, 0, 0);
        addFormItem(formPanel, "Game Area", gameAreaField, 1, 0);

        addFormItem(
                formPanel,
                "Health (1-1000)",
                healthField,
                0,
                1
        );

        addFormItem(
                formPanel,
                "Damage (0-200)",
                damageField,
                1,
                1
        );

        addFormItem(
                formPanel,
                "Speed (0.0-50.0)",
                speedField,
                0,
                2
        );

        addFormItem(
                formPanel,
                "Weakness",
                weaknessField,
                1,
                2
        );

        addFormItem(
                formPanel,
                "Reward Points (0-99999)",
                rewardPointsField,
                0,
                3
        );

        addFormItem(
                formPanel,
                "Defeated Status",
                defeatedCheckBox,
                1,
                3
        );

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(createDifficultyAndButtonPanel(), BorderLayout.CENTER);

        return panel;
    }

    // Create a styled text field
    private JTextField createTextField() {
        JTextField field = new JTextField();

        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(WHITE);
        field.setSelectionColor(LIST_SELECTED);
        field.setSelectedTextColor(WHITE);
        field.setFont(new Font("Arial", Font.PLAIN, 16));

        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LIGHT_BLUE, 1),
                new EmptyBorder(7, 9, 7, 9)
        ));

        return field;
    }

    // Add one label and input to the form
    private void addFormItem(
            JPanel panel,
            String labelText,
            JComponent component,
            int sectionColumn,
            int row
    ) {
        // Each form section uses two columns
        int startingColumn = sectionColumn * 2;

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = startingColumn;
        labelConstraints.gridy = row;
        labelConstraints.insets = new Insets(8, 8, 4, 8);
        labelConstraints.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(labelText);
        label.setForeground(TEXT_COLOR);
        label.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = startingColumn + 1;
        fieldConstraints.gridy = row;
        fieldConstraints.insets = new Insets(8, 0, 4, 15);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;

        component.setPreferredSize(new Dimension(170, 36));

        panel.add(component, fieldConstraints);
    }

    // Create the difficulty display and action buttons
    private JPanel createDifficultyAndButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 15));
        panel.setBackground(WINDOW_BACKGROUND);

        // Difficulty display
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(
                new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS)
        );

        difficultyPanel.setBackground(WINDOW_BACKGROUND);

        TitledBorder difficultyBorder = BorderFactory.createTitledBorder(
                new LineBorder(RED, 2),
                "DIFFICULTY"
        );

        difficultyBorder.setTitleColor(RED);
        difficultyBorder.setTitleFont(
                new Font("Arial", Font.BOLD, 14)
        );

        difficultyPanel.setBorder(BorderFactory.createCompoundBorder(
                difficultyBorder,
                new EmptyBorder(10, 15, 10, 15)
        ));

        difficultyLabel = new JLabel("Difficulty score: --");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 19));
        difficultyLabel.setForeground(GREEN);
        difficultyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ratingLabel = new JLabel("Rating: --");
        ratingLabel.setFont(new Font("Arial", Font.BOLD, 19));
        ratingLabel.setForeground(RED);
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        difficultyPanel.add(Box.createVerticalStrut(10));
        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(Box.createVerticalStrut(9));
        difficultyPanel.add(ratingLabel);
        difficultyPanel.add(Box.createVerticalStrut(10));

        // Main action buttons
        JButton addButton = createButton(
                "Add",
                BUTTON_BLUE,
                LIGHT_BLUE
        );

        JButton updateButton = createButton(
                "Update",
                BUTTON_PURPLE,
                LIGHT_BLUE
        );

        JButton deleteButton = createButton(
                "Delete",
                BUTTON_PURPLE,
                RED
        );

        JButton calculateButton = createButton(
                "Calculate",
                BUTTON_BLUE,
                WHITE
        );

        JButton clearButton = createButton(
                "Clear",
                new Color(38, 48, 108),
                LIGHT_BLUE
        );

        // Button Actions
        addButton.addActionListener(event -> addEnemy());
        updateButton.addActionListener(event -> updateEnemy());
        deleteButton.addActionListener(event -> deleteEnemy());
        calculateButton.addActionListener(
                event -> calculateDifficulty()
        );
        clearButton.addActionListener(event -> clearForm());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        buttonPanel.setBackground(WINDOW_BACKGROUND);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(clearButton);

        panel.add(difficultyPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Create a button
    private JButton createButton(
            String text,
            Color backgroundColor,
            Color borderColor
    ) {
        JButton button = new JButton(text);

        button.setBackground(backgroundColor);
        button.setForeground(WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, 2),
                new EmptyBorder(8, 12, 8, 12)
        ));

        return button;
    }

    // Create a border with a title
    private Border createSectionBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(LIGHT_BLUE, 1),
                title
        );

        border.setTitleColor(LIGHT_BLUE);
        border.setTitleFont(new Font("Arial", Font.BOLD, 16));

        return BorderFactory.createCompoundBorder(
                border,
                new EmptyBorder(5, 5, 5, 5)
        );
    }

    // Create the message bar at the bottom
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(new LineBorder(LIGHT_BLUE, 1));

        statusLabel = new JLabel("Ready.");
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setBorder(new EmptyBorder(7, 10, 7, 10));

        panel.add(statusLabel, BorderLayout.CENTER);

        return panel;
    }

    // Add a new enemy
    private void addEnemy() {
        try {
            Enemy enemy = readEnemyFromForm();

            boolean success = manager.addEnemy(enemy);

            if (success) {
                refreshEnemyList();
                selectEnemyInList(enemy.getName());
                setStatus(manager.getLastMessage());
            } else {
                showError(manager.getLastMessage());
            }
        } catch (IllegalArgumentException error) {
            showError(error.getMessage());
        }
    }

    // Update the selected enemy
    private void updateEnemy() {
        String selectedName = enemyList.getSelectedValue();

        if (selectedName == null) {
            showError(
                    "Select an enemy from the roster before updating."
            );
            return;
        }

        try {
            Enemy updatedEnemy = readEnemyFromForm();

            boolean success = manager.updateEnemy(
                    selectedName,
                    updatedEnemy
            );

            if (success) {
                refreshEnemyList();
                selectEnemyInList(updatedEnemy.getName());
                showDifficulty(updatedEnemy);
                setStatus(manager.getLastMessage());
            } else {
                showError(manager.getLastMessage());
            }
        } catch (IllegalArgumentException error) {
            showError(error.getMessage());
        }
    }

    // Delete the selected enemy
    private void deleteEnemy() {
        String selectedName = enemyList.getSelectedValue();

        if (selectedName == null) {
            showError(
                    "Select an enemy from the roster before deleting."
            );
            return;
        }

        // Ask before deleting
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove "
                        + selectedName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            setStatus("Delete cancelled.");
            return;
        }

        boolean success = manager.removeEnemyByName(selectedName);

        if (success) {
            refreshEnemyList();
            clearForm();
            setStatus(manager.getLastMessage());
        } else {
            showError(manager.getLastMessage());
        }
    }

    // Calculate difficulty
    private void calculateDifficulty() {
        try {
            Enemy enemy = readEnemyFromForm();

            showDifficulty(enemy);

            setStatus(
                    "Difficulty calculated for "
                            + enemy.getName() + "."
            );
        } catch (IllegalArgumentException error) {
            showError(error.getMessage());
        }
    }

    // Read all values from the form
    private Enemy readEnemyFromForm() {
        String name = nameField.getText().trim();
        String gameArea = gameAreaField.getText().trim();
        String weakness = weaknessField.getText().trim();

        if (!Enemy.isValidText(name)) {
            throw new IllegalArgumentException(
                    "Name must contain 1-30 characters "
                            + "and cannot contain |."
            );
        }

        if (!Enemy.isValidText(gameArea)) {
            throw new IllegalArgumentException(
                    "Game area must contain 1-30 characters "
                            + "and cannot contain |."
            );
        }

        if (!Enemy.isValidText(weakness)) {
            throw new IllegalArgumentException(
                    "Weakness must contain 1-30 characters "
                            + "and cannot contain |."
            );
        }

        // Read the number fields
        int health = readIntegerField(
                healthField,
                "Health",
                Enemy.MIN_HEALTH,
                Enemy.MAX_HEALTH
        );

        int damage = readIntegerField(
                damageField,
                "Damage",
                Enemy.MIN_DAMAGE,
                Enemy.MAX_DAMAGE
        );

        double speed = readDoubleField(
                speedField,
                "Speed",
                Enemy.MIN_SPEED,
                Enemy.MAX_SPEED
        );

        int rewardPoints = readIntegerField(
                rewardPointsField,
                "Reward points",
                Enemy.MIN_REWARD_POINTS,
                Enemy.MAX_REWARD_POINTS
        );

        boolean defeated = defeatedCheckBox.isSelected();

        return new Enemy(
                name,
                gameArea,
                health,
                damage,
                speed,
                weakness,
                rewardPoints,
                defeated
        );
    }


    private int readIntegerField(
            JTextField field,
            String fieldName,
            int minimum,
            int maximum
    ) {
        String text = field.getText().trim();

        try {
            int number = Integer.parseInt(text);

            if (number < minimum || number > maximum) {
                throw new IllegalArgumentException(
                        fieldName
                                + " must be a whole number from "
                                + minimum + " to " + maximum + "."
                );
            }

            return number;

        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be a whole number from "
                            + minimum + " to " + maximum + "."
            );
        }
    }

    private double readDoubleField(
            JTextField field,
            String fieldName,
            double minimum,
            double maximum
    ) {
        String text = field.getText().trim();

        try {
            double number = Double.parseDouble(text);

            if (number < minimum || number > maximum) {
                throw new IllegalArgumentException(
                        fieldName
                                + " must be a decimal number from "
                                + minimum + " to " + maximum + "."
                );
            }

            return number;

        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(
                    fieldName
                            + " must be a decimal number from "
                            + minimum + " to " + maximum + "."
            );
        }
    }

    // Put enemy data into the form
    private void loadSelectedEnemy() {
        String selectedName = enemyList.getSelectedValue();

        if (selectedName == null) {
            return;
        }

        Enemy enemy = findEnemy(selectedName);

        if (enemy == null) {
            showError("The selected enemy could not be found.");
            return;
        }

        nameField.setText(enemy.getName());
        gameAreaField.setText(enemy.getGameArea());
        healthField.setText(String.valueOf(enemy.getHealth()));
        damageField.setText(String.valueOf(enemy.getDamage()));
        speedField.setText(String.valueOf(enemy.getSpeed()));
        weaknessField.setText(enemy.getWeakness());

        rewardPointsField.setText(
                String.valueOf(enemy.getRewardPoints())
        );

        defeatedCheckBox.setSelected(enemy.isDefeated());

        showDifficulty(enemy);
        setStatus("Selected enemy: " + enemy.getName());
    }

    // Find an enemy
    private Enemy findEnemy(String name) {
        ArrayList<Enemy> enemies = manager.getAllEnemies();

        for (Enemy enemy : enemies) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                return enemy;
            }
        }

        return null;
    }

    // Enemy difficulty and rating
    private void showDifficulty(Enemy enemy) {
        String scoreText = formatDifficultyScore(
                enemy.calculateDifficultyScore()
        );

        difficultyLabel.setText(
                enemy.getName()
                        + " difficulty: "
                        + scoreText
        );

        ratingLabel.setText(
                "Rating: "
                        + enemy.getDifficultyRating().toUpperCase()
        );
    }

    // Remove .0 when the score is a whole number
    private String formatDifficultyScore(double score) {
        if (score == Math.floor(score)) {
            return String.valueOf((int) score);
        }

        return String.format("%.1f", score);
    }

    // Reload all enemy names
    private void refreshEnemyList() {
        enemyListModel.clear();

        ArrayList<Enemy> enemies = manager.getAllEnemies();

        for (Enemy enemy : enemies) {
            enemyListModel.addElement(enemy.getName());
        }
    }

    // Select an enemy in the roster
    private void selectEnemyInList(String name) {
        for (int i = 0; i < enemyListModel.size(); i++) {
            String currentName = enemyListModel.getElementAt(i);

            if (currentName.equalsIgnoreCase(name)) {
                enemyList.setSelectedIndex(i);
                enemyList.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    // Clear form fields
    private void clearForm() {
        enemyList.clearSelection();

        nameField.setText("");
        gameAreaField.setText("");
        healthField.setText("");
        damageField.setText("");
        speedField.setText("");
        weaknessField.setText("");
        rewardPointsField.setText("");
        defeatedCheckBox.setSelected(false);

        difficultyLabel.setText("Difficulty score: --");
        ratingLabel.setText("Rating: --");

        setStatus("Form cleared.");
    }

    // Load enemies
    private void loadEnemiesFromFile() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Load Enemy File");
        fileChooser.setSelectedFile(new File("bosses.txt"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            manager.loadEnemies(selectedFile.getAbsolutePath());
            refreshEnemyList();

            setStatus(manager.getLastMessage());
        }
    }

    // Save enemies to a file
    private void saveEnemiesToFile() {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Save Enemy File");
        fileChooser.setSelectedFile(new File("bosses.txt"));

        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            boolean success = manager.saveEnemies(
                    selectedFile.getAbsolutePath()
            );

            if (success) {
                setStatus(manager.getLastMessage());
            } else {
                showError(manager.getLastMessage());
            }
        }
    }

    // Show a normal status message
    private void setStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(TEXT_COLOR);
    }

    // Show an error message
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(RED);

        JOptionPane.showMessageDialog(
                this,
                message,
                "Boss Database System",
                JOptionPane.ERROR_MESSAGE
        );
    }
}