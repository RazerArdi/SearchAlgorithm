package pro.searchalgorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class Node {
    int x, y;
    boolean isStart, isGoal, isWall, isVisited, isShortestPath, isAStar; // Tambahkan isAStar

    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class PathFinderGUI extends JFrame implements ActionListener {
    private final int ROWS = 10;
    private final int COLS = 10;
    private final int CELL_SIZE = 30;

    private final Color COLOR_START = Color.RED;
    private final Color COLOR_GOAL = Color.GREEN;
    private final Color COLOR_WALL = Color.BLACK;
    private final Color COLOR_VISITED = new Color(135, 206, 250);
    private final Color COLOR_SHORTEST_PATH = Color.YELLOW;

    private final String[] ALGORITHMS = {"A*", "Dijkstra"};

    private JButton[][] gridButtons;
    private JButton startButton;
    private JButton goalButton;
    private JButton wallButton;
    private JButton visualizeButton;
    private JButton resetButton;
    private JComboBox<String> algorithmComboBox;
    private JLabel statusLabel;
    private JButton removeBlockButton;
    private JButton resetPathButton;

    private Node[][] grid;
    private int visitedNodeIndex = 0;
    private Node selectedNode = null;

    PathFinderGUI() {
        setTitle("Path Finder");
        int controlPanelHeight = 400;
        int controlPanelWidth = 1000;
        setSize(CELL_SIZE * COLS + controlPanelWidth, CELL_SIZE * ROWS + controlPanelHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(ROWS, COLS));
        gridButtons = new JButton[ROWS][COLS];
        grid = new Node[ROWS][COLS];

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = new Node(i, j);
                gridButtons[i][j] = new JButton();
                gridButtons[i][j].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                gridButtons[i][j].setBackground(Color.WHITE);
                gridButtons[i][j].addActionListener(this);
                gridPanel.add(gridButtons[i][j]);
            }
        }

        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        goalButton = new JButton("Goal");
        goalButton.addActionListener(this);
        wallButton = new JButton("Wall");
        wallButton.addActionListener(this);
        visualizeButton = new JButton("Visualize");
        visualizeButton.addActionListener(this);
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        removeBlockButton = new JButton("Remove Block");
        removeBlockButton.addActionListener(this);
        algorithmComboBox = new JComboBox<>(ALGORITHMS);
        resetPathButton = new JButton("Reset Path");
        resetPathButton.addActionListener(this);
        controlPanel.add(resetPathButton);
        statusLabel = new JLabel("Status: ");
        controlPanel.add(removeBlockButton);
        controlPanel.add(startButton);
        controlPanel.add(goalButton);
        controlPanel.add(wallButton);
        controlPanel.add(visualizeButton);
        controlPanel.add(resetPathButton);
        controlPanel.add(resetButton);
        controlPanel.add(new JLabel("Algorithm: "));
        controlPanel.add(algorithmComboBox);
        controlPanel.add(statusLabel);

        add(gridPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == startButton) {
            selectedNode = new Node(-1, -1);
            selectedNode.isStart = true;
            statusLabel.setText("Status: Select starting point.");
        } else if (source == goalButton) {
            selectedNode = new Node(-1, -1);
            selectedNode.isGoal = true;
            statusLabel.setText("Status: Select goal point.");
        } else if (source == wallButton) {
            selectedNode = new Node(-1, -1);
            selectedNode.isWall = true;
            statusLabel.setText("Status: Place walls.");
        } else if (source == removeBlockButton) {
            selectedNode = new Node(-1, -1);
            selectedNode.isWall = true;
            statusLabel.setText("Status: Remove wall.");
        } else if (source == resetButton) {
            resetGrid();
            selectedNode = null;
            statusLabel.setText("Status: ");
        } else if (source == visualizeButton) {
            String algorithm = (String) algorithmComboBox.getSelectedItem();
            visualizeAlgorithm(algorithm);
            selectedNode = null;
        } else if (source == resetPathButton) {
            resetPath();
            statusLabel.setText("Status: Path reset.");
            selectedNode = null;

        } else {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (source == gridButtons[i][j]) {
                        if (selectedNode != null) {
                            handleNodeSelection(i, j);
                        }
                        return;
                    }
                }
            }
        }
    }

    private void resetPath() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isVisited || grid[i][j].isShortestPath) {
                    grid[i][j].isVisited = false;
                    grid[i][j].isShortestPath = false;
                    updateButtonColor(grid[i][j]);
                }
            }
        }
    }


    private void handleNodeSelection(int x, int y) {
        Node node = grid[x][y];
        if (selectedNode.isStart) {
            // Reset previous start node
            clearStartNode();
            // Set new start node
            node.isStart = true;
            updateButtonColor(node);
            selectedNode = null;
            statusLabel.setText("Status: ");
        } else if (selectedNode.isGoal) {
            // Reset previous goal node
            clearGoalNode();
            // Set new goal node
            node.isGoal = true;
            updateButtonColor(node);
            selectedNode = null;
            statusLabel.setText("Status: ");
        } else if (selectedNode.isWall) {
            // Toggle wall node
            node.isWall = !node.isWall;
            updateButtonColor(node);
        }
    }

    private void clearStartNode() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isStart) {
                    grid[i][j].isStart = false;
                    updateButtonColor(grid[i][j]);
                    return;
                }
            }
        }
    }

    private void clearGoalNode() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isGoal) {
                    grid[i][j].isGoal = false;
                    updateButtonColor(grid[i][j]);
                    return;
                }
            }
        }
    }

    private void resetGrid() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j].isStart = false;
                grid[i][j].isGoal = false;
                grid[i][j].isWall = false;
                updateButtonColor(grid[i][j]);
            }
        }
    }

    private void updateButtonColor(Node node) {
        Color color;
        String symbol = "";
        if (node.isStart) {
            color = COLOR_START;
            symbol = "S";
        } else if (node.isGoal) {
            color = COLOR_GOAL;
            symbol = "G";
        } else if (node.isWall) {
            color = COLOR_WALL;
        } else if (node.isShortestPath) {
            color = COLOR_SHORTEST_PATH;
        } else if (node.isVisited) {
            color = COLOR_VISITED;
        } else {
            color = Color.WHITE;
        }
        gridButtons[node.x][node.y].setBackground(color);
        gridButtons[node.x][node.y].setText(symbol);
    }
    private void visualizeAlgorithm(String algorithm) {
        ArrayList<Node> visitedNodes;
        ArrayList<Node> shortestPath;
        switch (algorithm) {
            case "A*":
                visitedNodes = new ArrayList<>();
                shortestPath = aStarAlgorithm(visitedNodes);
                colorVisitedNodes(visitedNodes);
                break;
            case "Dijkstra":
                visitedNodes = new ArrayList<>();
                shortestPath = dijkstraAlgorithm(visitedNodes);
                colorVisitedNodes(visitedNodes);
                break;
            default:
                return;
        }
        colorVisitedNodes(visitedNodes);
        Timer timer = new Timer(100 * visitedNodes.size(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colorShortestPath(shortestPath);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private ArrayList<Node> aStarAlgorithm(ArrayList<Node> visitedNodes) {
        // Inisialisasi nilai awal
        int[][] distance = new int[ROWS][COLS];
        boolean[][] visited = new boolean[ROWS][COLS];
        int[][] parentX = new int[ROWS][COLS];
        int[][] parentY = new int[ROWS][COLS];

        // Mengatur jarak awal ke tak terhingga
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                distance[i][j] = Integer.MAX_VALUE;
            }
        }

        // Mencari node awal dan goal
        Node startNode = null;
        Node goalNode = null;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isStart) {
                    startNode = grid[i][j];
                    distance[i][j] = 0;
                } else if (grid[i][j].isGoal) {
                    goalNode = grid[i][j];
                }
            }
        }

        // Pengecekan apakah start dan goal ditemukan
        if (startNode == null || goalNode == null) {
            return new ArrayList<>(); // Mengembalikan array kosong jika start atau goal tidak ditemukan
        }

        // Inisialisasi array openList dan closedList
        ArrayList<Node> openList = new ArrayList<>();
        ArrayList<Node> closedList = new ArrayList<>();

        // Menambahkan node awal ke openList
        openList.add(startNode);

        // Melakukan iterasi hingga openList kosong
        while (!openList.isEmpty()) {
            // Mengambil node dengan jarak terpendek dari openList
            Node currentNode = openList.get(0);
            for (Node node : openList) {
                if (distance[node.x][node.y] < distance[currentNode.x][currentNode.y]) {
                    currentNode = node;
                }
            }

            // Memindahkan currentNode ke closedList
            openList.remove(currentNode);
            closedList.add(currentNode);

            // Jika currentNode adalah goalNode, maka path ditemukan
            if (currentNode == goalNode) {
                ArrayList<Node> shortestPath = new ArrayList<>();
                while (currentNode != startNode) {
                    shortestPath.add(currentNode);
                    int parentXVal = parentX[currentNode.x][currentNode.y];
                    int parentYVal = parentY[currentNode.x][currentNode.y];
                    currentNode = grid[parentXVal][parentYVal];
                }
                shortestPath.add(startNode);
                return shortestPath;
            }

            // Mendapatkan tetangga dari currentNode
            ArrayList<Node> neighbors = getNeighbors(currentNode);
            for (Node neighbor : neighbors) {
                if (closedList.contains(neighbor) || neighbor.isWall) {
                    continue;
                }

                int tentativeDistance = distance[currentNode.x][currentNode.y] + 1;
                if (tentativeDistance < distance[neighbor.x][neighbor.y]) {
                    distance[neighbor.x][neighbor.y] = tentativeDistance;
                    parentX[neighbor.x][neighbor.y] = currentNode.x;
                    parentY[neighbor.x][neighbor.y] = currentNode.y;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                        neighbor.isAStar = true; // Tandai node sebagai dikunjungi oleh A*
                        visitedNodes.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // Mengembalikan array kosong jika path tidak ditemukan
    }

    private ArrayList<Node> dijkstraAlgorithm(ArrayList<Node> visitedNodes) {
        // Inisialisasi nilai awal
        int[][] distance = new int[ROWS][COLS];
        boolean[][] visited = new boolean[ROWS][COLS];
        int[][] parentX = new int[ROWS][COLS];
        int[][] parentY = new int[ROWS][COLS];

        // Mengatur jarak awal ke tak terhingga
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                distance[i][j] = Integer.MAX_VALUE;
            }
        }

        // Mencari node awal dan goal
        Node startNode = null;
        Node goalNode = null;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isStart) {
                    startNode = grid[i][j];
                    distance[i][j] = 0;
                } else if (grid[i][j].isGoal) {
                    goalNode = grid[i][j];
                }
            }
        }

        // Pengecekan apakah start dan goal ditemukan
        if (startNode == null || goalNode == null) {
            return new ArrayList<>(); // Mengembalikan array kosong jika start atau goal tidak ditemukan
        }

        // Inisialisasi array openList dan closedList
        ArrayList<Node> openList = new ArrayList<>();
        ArrayList<Node> closedList = new ArrayList<>();

        // Menambahkan node awal ke openList
        openList.add(startNode);

        // Melakukan iterasi hingga openList kosong
        while (!openList.isEmpty()) {
            // Mengambil node dengan jarak terpendek dari openList
            Node currentNode = openList.get(0);
            for (Node node : openList) {
                if (distance[node.x][node.y] < distance[currentNode.x][currentNode.y]) {
                    currentNode = node;
                }
            }

            // Memindahkan currentNode ke closedList
            openList.remove(currentNode);
            closedList.add(currentNode);

            // Menambahkan currentNode ke visitedNodes untuk visualisasi
            visitedNodes.add(currentNode);

            // Jika currentNode adalah goalNode, maka path ditemukan
            if (currentNode == goalNode) {
                ArrayList<Node> shortestPath = new ArrayList<>();
                while (currentNode != startNode) {
                    shortestPath.add(currentNode);
                    int parentXVal = parentX[currentNode.x][currentNode.y];
                    int parentYVal = parentY[currentNode.x][currentNode.y];
                    currentNode = grid[parentXVal][parentYVal];
                }
                shortestPath.add(startNode);
                return shortestPath;
            }

            // Mendapatkan tetangga dari currentNode
            ArrayList<Node> neighbors = getNeighbors(currentNode);
            for (Node neighbor : neighbors) {
                if (closedList.contains(neighbor) || neighbor.isWall) {
                    continue;
                }

                int tentativeDistance = distance[currentNode.x][currentNode.y] + 1;
                if (tentativeDistance < distance[neighbor.x][neighbor.y]) {
                    distance[neighbor.x][neighbor.y] = tentativeDistance;
                    parentX[neighbor.x][neighbor.y] = currentNode.x;
                    parentY[neighbor.x][neighbor.y] = currentNode.y;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                        visitedNodes.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // Mengembalikan array kosong jika path tidak ditemukan
    }

    private ArrayList<Node> getNeighbors(Node node) {
        ArrayList<Node> neighbors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newX = node.x + dx[i];
            int newY = node.y + dy[i];

            if (newX >= 0 && newX < ROWS && newY >= 0 && newY < COLS) {
                neighbors.add(grid[newX][newY]);
            }
        }

        return neighbors;
    }

    private void colorVisitedNodes(ArrayList<Node> visitedNodes) {
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (visitedNodeIndex < visitedNodes.size()) {
                    Node node = visitedNodes.get(visitedNodeIndex);
                    if (node.isAStar) {
                        // Tandai node yang dikunjungi oleh A* dengan warna tertentu
                        node.isVisited = true;
                        node.isAStar = false; // Atur kembali ke false setelah diwarnai
                        updateButtonColor(node);
                    } else {
                        // Tandai node yang dikunjungi oleh Dijkstra
                        node.isVisited = true;
                        updateButtonColor(node);
                    }
                    visitedNodeIndex++;
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        visitedNodeIndex = 0; // Reset visitedNodeIndex sebelum memulai visualisasi
        timer.start();
    }

    private void colorShortestPath(ArrayList<Node> shortestPath) {
        for (Node node : shortestPath) {
            node.isShortestPath = true;
            updateButtonColor(node);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PathFinderGUI gui = new PathFinderGUI();
            gui.setVisible(true);
        });
    }
}