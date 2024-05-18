package pro.searchalgorithm; // Package yang berisi kelas-kelas terkait algoritma pencarian.

import javax.swing.*; // Mengimpor kelas-kelas dari paket javax.swing untuk GUI.
import java.awt.*; // Mengimpor kelas-kelas dari paket java.awt untuk GUI.
import java.awt.event.ActionEvent; // Mengimpor kelas ActionEvent dari paket java.awt.event untuk menangani event.
import java.awt.event.ActionListener; // Mengimpor kelas ActionListener dari paket java.awt.event untuk menangani event.
import java.util.ArrayList; // Mengimpor kelas ArrayList dari paket java.util untuk pengelolaan list dinamis.
import java.util.Comparator; // Mengimpor kelas Comparator dari paket java.util untuk pembanding.
import java.util.PriorityQueue; // Mengimpor kelas PriorityQueue dari paket java.util untuk antrian prioritas.

// Kelas Node merepresentasikan simpul pada grid.
class Node {
    int x, y; // Koordinat x dan y dari simpul.
    boolean isStart, isGoal, isWall, isVisited, isShortestPath, isAStar; // Variabel boolean yang menunjukkan status simpul.
    // Tambahkan isAStar untuk menandai apakah simpul digunakan dalam algoritma A*.

    Node(int x, int y) { // Konstruktor Node untuk inisialisasi koordinat x dan y.
        this.x = x;
        this.y = y;
    }
}

// Kelas PathFinderGUI adalah kelas utama yang mengatur GUI dan algoritma pencarian.
class PathFinderGUI extends JFrame implements ActionListener {
    private final int ROWS = 10; // Jumlah baris dalam grid.
    private final int COLS = 10; // Jumlah kolom dalam grid.
    private final int CELL_SIZE = 30; // Ukuran setiap sel dalam grid.

    // Warna untuk status berbeda dalam grid.
    private final Color COLOR_START = Color.RED; // Warna untuk titik awal.
    private final Color COLOR_GOAL = Color.GREEN; // Warna untuk titik tujuan.
    private final Color COLOR_WALL = Color.BLACK; // Warna untuk dinding.
    private final Color COLOR_VISITED = new Color(135, 206, 250); // Warna untuk simpul yang telah dikunjungi.
    private final Color COLOR_SHORTEST_PATH = Color.YELLOW; // Warna untuk jalur terpendek.

    private final String[] ALGORITHMS = {"A*", "Dijkstra"}; // Array nama algoritma yang tersedia.

    private JButton[][] gridButtons; // Tombol-tombol yang merepresentasikan setiap sel dalam grid.
    private JButton startButton; // Tombol untuk menandai titik awal.
    private JButton goalButton; // Tombol untuk menandai titik tujuan.
    private JButton wallButton; // Tombol untuk menempatkan dinding.
    private JButton visualizeButton; // Tombol untuk memvisualisasikan algoritma pencarian.
    private JButton resetButton; // Tombol untuk mereset grid.
    private JComboBox<String> algorithmComboBox; // ComboBox untuk memilih algoritma.
    private JLabel statusLabel; // Label untuk menampilkan status.
    private JButton removeBlockButton; // Tombol untuk menghapus blok (dinding).
    private JButton resetPathButton; // Tombol untuk mereset jalur.

    private Node[][] grid; // Array dua dimensi yang merepresentasikan grid.
    private int visitedNodeIndex = 0; // Indeks untuk menghitung simpul yang telah dikunjungi.
    private Node selectedNode = null; // Simpul yang dipilih saat ini.

    // Konstruktor untuk inisialisasi GUI.
    PathFinderGUI() {
        setTitle("Path Finder"); // Menetapkan judul frame.
        int controlPanelHeight = 400; // Tinggi panel kontrol.
        int controlPanelWidth = 1000; // Lebar panel kontrol.
        setSize(CELL_SIZE * COLS + controlPanelWidth, CELL_SIZE * ROWS + controlPanelHeight); // Menetapkan ukuran frame.
        setLocationRelativeTo(null); // Frame akan muncul di tengah layar.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Aksi tombol close akan menutup aplikasi.
        setLayout(new BorderLayout()); // Mengatur layout frame.

        JPanel gridPanel = new JPanel(); // Panel untuk grid.
        gridPanel.setLayout(new GridLayout(ROWS, COLS)); // Mengatur layout panel grid.
        gridButtons = new JButton[ROWS][COLS]; // Menginisialisasi array tombol grid.
        grid = new Node[ROWS][COLS]; // Menginisialisasi array simpul grid.

        // Membuat tombol-tombol untuk setiap sel dalam grid.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = new Node(i, j); // Inisialisasi simpul grid.
                gridButtons[i][j] = new JButton(); // Membuat tombol.
                gridButtons[i][j].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE)); // Mengatur ukuran tombol.
                gridButtons[i][j].setBackground(Color.WHITE); // Mengatur warna latar belakang tombol.
                gridButtons[i][j].addActionListener(this); // Menambahkan ActionListener ke tombol.
                gridPanel.add(gridButtons[i][j]); // Menambahkan tombol ke panel grid.
            }
        }

        JPanel controlPanel = new JPanel(); // Panel untuk kontrol.
        // Membuat tombol-tombol dan kontrol lainnya.
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

        add(gridPanel, BorderLayout.CENTER); // Menambahkan panel grid ke frame.
        add(controlPanel, BorderLayout.NORTH); // Menambahkan panel kontrol ke frame.
    }

    @Override
    public void actionPerformed(ActionEvent e) { // Metode yang menangani event.
        Object source = e.getSource(); // Mendapatkan sumber event.
        // Menangani event untuk
        if (source == startButton) { // Jika tombol "Start" ditekan.
            selectedNode = new Node(-1, -1); // Inisialisasi simpul yang dipilih.
            selectedNode.isStart = true; // Menandai simpul sebagai titik awal.
            statusLabel.setText("Status: Select starting point."); // Menetapkan status.
        } else if (source == goalButton) { // Jika tombol "Goal" ditekan.
            selectedNode = new Node(-1, -1); // Inisialisasi simpul yang dipilih.
            selectedNode.isGoal = true; // Menandai simpul sebagai titik tujuan.
            statusLabel.setText("Status: Select goal point."); // Menetapkan status.
        } else if (source == wallButton) { // Jika tombol "Wall" ditekan.
            selectedNode = new Node(-1, -1); // Inisialisasi simpul yang dipilih.
            selectedNode.isWall = true; // Menandai simpul sebagai dinding.
            statusLabel.setText("Status: Place walls."); // Menetapkan status.
        } else if (source == removeBlockButton) { // Jika tombol "Remove Block" ditekan.
            selectedNode = new Node(-1, -1); // Inisialisasi simpul yang dipilih.
            selectedNode.isWall = true; // Menandai simpul sebagai dinding.
            statusLabel.setText("Status: Remove wall."); // Menetapkan status.
        } else if (source == resetButton) { // Jika tombol "Reset" ditekan.
            resetGrid(); // Memanggil metode untuk mereset grid.
            selectedNode = null; // Mereset simpul yang dipilih.
            statusLabel.setText("Status: "); // Mereset status.
        } else if (source == visualizeButton) { // Jika tombol "Visualize" ditekan.
            String algorithm = (String) algorithmComboBox.getSelectedItem(); // Mendapatkan algoritma yang dipilih.
            visualizeAlgorithm(algorithm); // Memanggil metode untuk memvisualisasikan algoritma.
            selectedNode = null; // Mereset simpul yang dipilih.
        } else if (source == resetPathButton) { // Jika tombol "Reset Path" ditekan.
            resetPath(); // Memanggil metode untuk mereset jalur.
            statusLabel.setText("Status: Path reset."); // Menetapkan status.
            selectedNode = null; // Mereset simpul yang dipilih.
        } else { // Jika tombol sel dalam grid ditekan.
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (source == gridButtons[i][j]) { // Jika tombol sel tertentu ditekan.
                        if (selectedNode != null) { // Jika simpul telah dipilih sebelumnya.
                            handleNodeSelection(i, j); // Memanggil metode untuk menangani pemilihan simpul.
                        }
                        return; // Keluar dari loop.
                    }
                }
            }
        }
    }

    private void resetPath() { // Metode untuk mereset jalur.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isVisited || grid[i][j].isShortestPath) { // Jika simpul telah dikunjungi atau merupakan bagian dari jalur terpendek.
                    grid[i][j].isVisited = false; // Mereset status kunjungan.
                    grid[i][j].isShortestPath = false; // Mereset status jalur terpendek.
                    updateButtonColor(grid[i][j]); // Memperbarui warna tombol.
                }
            }
        }
    }

    private void handleNodeSelection(int x, int y) { // Metode untuk menangani pemilihan simpul.
        Node node = grid[x][y]; // Mendapatkan simpul dari koordinat.
        if (selectedNode.isStart) { // Jika simpul yang dipilih adalah titik awal.
            clearStartNode(); // Memanggil metode untuk menghapus titik awal sebelumnya.
            node.isStart = true; // Menandai simpul sebagai titik awal.
            updateButtonColor(node); // Memperbarui warna tombol.
            selectedNode = null; // Mereset simpul yang dipilih.
            statusLabel.setText("Status: "); // Mereset status.
        } else if (selectedNode.isGoal) { // Jika simpul yang dipilih adalah titik tujuan.
            clearGoalNode(); // Memanggil metode untuk menghapus titik tujuan sebelumnya.
            node.isGoal = true; // Menandai simpul sebagai titik tujuan.
            updateButtonColor(node); // Memperbarui warna tombol.
            selectedNode = null; // Mereset simpul yang dipilih.
            statusLabel.setText("Status: "); // Mereset status.
        } else if (selectedNode.isWall) { // Jika simpul yang dipilih adalah dinding.
            node.isWall = !node.isWall; // Toggle status dinding simpul.
            updateButtonColor(node); // Memperbarui warna tombol.
        }
    }

    private void clearStartNode() { // Metode untuk menghapus titik awal sebelumnya.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isStart) { // Jika simpul adalah titik awal.
                    grid[i][j].isStart = false; // Menghapus status titik awal.
                    updateButtonColor(grid[i][j]); // Memperbarui warna tombol.
                    return; // Keluar dari loop.
                }
            }
        }
    }

    private void clearGoalNode() { // Metode untuk menghapus titik tujuan sebelumnya.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isGoal) { // Jika simpul adalah titik tujuan.
                    grid[i][j].isGoal = false; // Menghapus status titik tujuan.
                    updateButtonColor(grid[i][j]); // Memperbarui warna tombol.
                    return; // Keluar dari loop.
                }
            }
        }
    }

    private void resetGrid() { // Metode untuk mereset grid.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j].isStart = false; // Menghapus status titik awal.
                grid[i][j].isGoal = false; // Menghapus status titik tujuan.
                grid[i][j].isWall = false; // Menghapus status dinding.
                updateButtonColor(grid[i][j]); // Memperbarui warna tombol.
            }
        }
    }

    private void updateButtonColor(Node node) { // Metode untuk memperbarui warna tombol berdasarkan status simpul.
        Color color;
        String symbol = "";
        if (node.isStart) { // Jika simpul adalah titik awal.
            color = COLOR_START; // Menetapkan warna untuk titik awal.
            symbol = "S"; // Menetapkan simbol untuk titik awal.
        } else if (node.isGoal) { // Jika simpul adalah titik tujuan.
            color = COLOR_GOAL; // Menetapkan warna untuk titik tujuan.
            symbol = "G"; // Menetapkan simbol untuk titik tujuan.
        } else if (node.isWall) { // Jika simpul adalah dinding.
            color = COLOR_WALL; // Menetapkan warna untuk dinding.
        } else if (node.isShortestPath) { // Jika simpul adalah bagian dari jalur terpendek.
            color = COLOR_SHORTEST_PATH; // Menetapkan warna untuk jalur terpendek.
        } else if (node.isVisited) { // Jika simpul telah dikunjungi.
            color = COLOR_VISITED; // Menetapkan warna untuk simpul yang telah dikunjungi.
        } else { // Jika simpul tidak memiliki status khusus.
            color = Color.WHITE; // Menetapkan warna default.
        }
        gridButtons[node.x][node.y].setBackground(color); // Mengatur warna tombol.
        gridButtons[node.x][node.y].setText(symbol); // Menetapkan simbol pada tombol.
    }

    private void visualizeAlgorithm(String algorithm) { // Metode untuk memvisualisasikan algoritma.
        ArrayList<Node> visitedNodes; // Menyimpan simpul-simpul yang telah dikunjungi.
        ArrayList<Node> shortestPath; // Menyimpan jalur terpendek.
        switch (algorithm) { // Memilih algoritma berdasarkan pilihan pengguna.
            case "A*": // Jika algoritma yang dipilih adalah A*.
                visitedNodes = new ArrayList<>(); // Inisialisasi ArrayList untuk menyimpan simpul yang dikunjungi.
                shortestPath = aStarAlgorithm(visitedNodes); // Memanggil metode A* dan menyimpan jalur terpendek.
                break;
            case "Dijkstra": // Jika algoritma yang dipilih adalah Dijkstra.
                visitedNodes = new ArrayList<>(); // Inisialisasi ArrayList untuk menyimpan simpul yang dikunjungi.
                shortestPath = dijkstraAlgorithm(visitedNodes); // Memanggil metode Dijkstra dan menyimpan jalur terpendek.
                break;
            default: // Jika algoritma yang dipilih tidak valid.
                return; // Keluar dari metode.
        }
        animateAlgorithm(visitedNodes, shortestPath); // Memanggil metode untuk memvisualisasikan algoritma.
    }

    private void animateAlgorithm(ArrayList<Node> visitedNodes, ArrayList<Node> shortestPath) { // Metode untuk membuat animasi dari algoritma.
        // Animasi simpul yang dikunjungi.
        Timer visitedTimer = new Timer(50, null); // Timer untuk mengatur interval animasi.
        visitedTimer.addActionListener(new ActionListener() {
            int index = 0; // Indeks untuk mengakses simpul yang dikunjungi.
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < visitedNodes.size()) { // Memeriksa apakah masih ada simpul yang belum dianimasikan.
                    Node node = visitedNodes.get(index); // Mendapatkan simpul yang akan dianimasikan.
                    node.isVisited = true; // Menandai simpul sebagai telah dikunjungi.
                    updateButtonColor(node); // Memperbarui warna tombol untuk menampilkan animasi.
                    index++; // Pindah ke simpul berikutnya.
                } else {
                    visitedTimer.stop(); // Menghentikan timer setelah semua simpul dikunjungi.
                    // Animasi jalur terpendek setelah semua simpul dikunjungi.
                    Timer pathTimer = new Timer(100, null); // Timer untuk mengatur interval animasi.
                    pathTimer.addActionListener(new ActionListener() {
                        int pathIndex = 0; // Indeks untuk mengakses simpul pada jalur terpendek.
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (pathIndex < shortestPath.size()) { // Memeriksa apakah masih ada simpul pada jalur terpendek yang belum dianimasikan.
                                Node node = shortestPath.get(pathIndex); // Mendapatkan simpul pada jalur terpendek yang akan dianimasikan.
                                node.isShortestPath = true; // Menandai simpul sebagai bagian dari jalur terpendek.
                                updateButtonColor(node); // Memperbarui warna tombol untuk menampilkan animasi.
                                pathIndex++; // Pindah ke simpul berikutnya pada jalur terpendek.
                            } else {
                                pathTimer.stop(); // Menghentikan timer setelah semua simpul pada jalur terpendek dianimasikan.
                            }
                        }
                    });
                    pathTimer.start(); // Memulai timer untuk animasi jalur terpendek.
                }
            }
        });
        visitedTimer.start(); // Memulai timer untuk animasi simpul yang dikunjungi.
    }

    private ArrayList<Node> aStarAlgorithm(ArrayList<Node> visitedNodes) { // Metode untuk menjalankan algoritma A*.
        // Inisialisasi nilai awal
        int[][] distance = new int[ROWS][COLS]; // Array untuk menyimpan jarak terpendek dari titik awal ke setiap simpul.
        boolean[][] visited = new boolean[ROWS][COLS]; // Array untuk menandai simpul yang sudah dikunjungi.
        int[][] parentX = new int[ROWS][COLS]; // Array untuk menyimpan koordinat X dari simpul yang merupakan parent.
        int[][] parentY = new int[ROWS][COLS]; // Array untuk menyimpan koordinat Y dari simpul yang merupakan parent.
        Node goalNode = null; // Node yang merupakan titik tujuan.

        // Mengatur jarak awal ke tak terhingga
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                distance[i][j] = Integer.MAX_VALUE; // Mengatur jarak awal ke tak terhingga.
            }
        }

        // Mencari node awal dan node tujuan
        Node startNode = null; // Node yang merupakan titik awal.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isStart) { // Jika simpul adalah titik awal.
                    startNode = grid[i][j]; // Mendapatkan node titik awal.
                    distance[i][j] = 0; // Mengatur jarak titik awal ke dirinya sendiri menjadi 0.
                } else if (grid[i][j].isGoal) { // Jika simpul adalah titik tujuan.
                    goalNode = grid[i][j]; // Mendapatkan node titik tujuan.
                }
            }
        }

        // Pengecekan apakah start dan goal ditemukan
        if (startNode == null || goalNode == null) {
            return new ArrayList<>(); // Mengembalikan array kosong jika titik awal atau titik tujuan tidak ditemukan.
        }

        // Inisialisasi array openList dan closedList
        Node finalGoalNode = goalNode;
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(a -> distance[a.x][a.y] + heuristic(a, finalGoalNode))); // Antrian prioritas untuk menyimpan simpul yang akan dieksplorasi.
        ArrayList<Node> closedList = new ArrayList<>(); // Daftar untuk menyimpan simpul yang sudah dieksplorasi.

        // Menambahkan node awal ke openList
        openList.add(startNode);

        // Melakukan iterasi hingga openList kosong
        while (!openList.isEmpty()) {
            // Mengambil node dengan jarak terpendek dari openList
            Node currentNode = openList.poll(); // Menghapus dan mengembalikan simpul dengan jarak terpendek dari openList.

            // Memindahkan currentNode ke closedList
            closedList.add(currentNode); // Menambahkan currentNode ke daftar yang sudah dieksplorasi.

            // Jika currentNode adalah goalNode, maka path ditemukan
            if (currentNode == goalNode) {
                ArrayList<Node> shortestPath = new ArrayList<>(); // Array untuk menyimpan jalur terpendek.
                while (currentNode != startNode) { // Melakukan iterasi mundur dari titik tujuan ke titik awal.
                    shortestPath.add(currentNode); // Menambahkan currentNode ke jalur terpendek.
                    int parentXVal = parentX[currentNode.x][currentNode.y]; // Mendapatkan koordinat X dari parent currentNode.
                    int parentYVal = parentY[currentNode.x][currentNode.y]; // Mendapatkan koordinat Y dari parent currentNode.
                    currentNode = grid[parentXVal][parentYVal]; // Mengupdate currentNode ke parent currentNode.
                }
                shortestPath.add(startNode); // Menambahkan titik awal ke jalur terpendek.
                return shortestPath; // Mengembalikan jalur terpendek.
            }

            // Mendapatkan tetangga dari currentNode
            ArrayList<Node> neighbors = getNeighbors(currentNode); // Mendapatkan tetangga dari simpul currentNode.
            for (Node neighbor : neighbors) { // Iterasi melalui tetangga currentNode.
                if (closedList.contains(neighbor) || neighbor.isWall) { // Jika tetangga sudah dieksplorasi atau merupakan dinding.
                    continue; // Melanjutkan ke tetangga berikutnya.
                }

                int tentativeDistance = distance[currentNode.x][currentNode.y] + 1; // Menghitung jarak sementara ke tetangga currentNode.
                if (tentativeDistance < distance[neighbor.x][neighbor.y]) { // Jika jarak sementara lebih pendek dari jarak saat ini ke tetangga.
                    distance[neighbor.x][neighbor.y] = tentativeDistance; // Mengupdate jarak ke tetangga.
                    parentX[neighbor.x][neighbor.y] = currentNode.x; // Mengupdate koordinat X parent dari tetangga.
                    parentY[neighbor.x][neighbor.y] = currentNode.y; // Mengupdate koordinat Y parent dari tetangga.

                    if (!openList.contains(neighbor)) { // Jika tetangga belum ada di openList.
                        openList.add(neighbor); // Menambahkan tetangga ke openList.
                        visitedNodes.add(neighbor); // Menambahkan tetangga ke daftar simpul yang dikunjungi.
                    }
                }
            }
        }

        return new  ArrayList<>(); // Mengembalikan array kosong jika jalur tidak ditemukan.
    }

    private ArrayList<Node> getNeighbors(Node node) { // Metode untuk mendapatkan tetangga dari suatu simpul.
        ArrayList<Node> neighbors = new ArrayList<>(); // Inisialisasi list neighbors
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Array yang berisi perubahan koordinat untuk keempat arah.
        for (int[] direction : directions) { // Iterasi melalui setiap arah.
            int newRow = node.x + direction[0]; // Koordinat X dari tetangga.
            int newCol = node.y + direction[1]; // Koordinat Y dari tetangga.
            if (isValidCell(newRow, newCol)) { // Memeriksa apakah tetangga berada dalam batas grid.
                neighbors.add(grid[newRow][newCol]); // Menambahkan tetangga ke daftar neighbors.
            }
        }
        return neighbors; // Mengembalikan daftar tetangga.
    }

    private boolean isValidCell(int row, int col) { // Metode untuk memeriksa apakah suatu sel valid dalam grid.
        return row >= 0 && row < ROWS && col >= 0 && col < COLS; // Menambahkan neighbor ke list
    }

    private int heuristic(Node a, Node b) { // Metode untuk menghitung nilai heuristik antara dua simpul.
        // Menggunakan jarak Manhattan sebagai heuristik
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // Menghitung nilai heuristik (Manhattan distance).
    }

    private ArrayList<Node> dijkstraAlgorithm(ArrayList<Node> visitedNodes) { // Metode untuk menjalankan algoritma Dijkstra.
        // Mirip dengan implementasi A*, tapi tanpa perhitungan heuristik
        // Inisialisasi nilai awal
        int[][] distance = new int[ROWS][COLS]; // Array untuk menyimpan jarak terpendek dari titik awal ke setiap simpul.
        boolean[][] visited = new boolean[ROWS][COLS]; // Array untuk menandai simpul yang sudah dikunjungi.
        int[][] parentX = new int[ROWS][COLS]; // Array untuk menyimpan koordinat X dari simpul yang merupakan parent.
        int[][] parentY = new int[ROWS][COLS]; // Array untuk menyimpan koordinat Y dari simpul yang merupakan parent.
        Node goalNode = null; // Node yang merupakan titik tujuan.

        // Mengatur jarak awal ke tak terhingga
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                distance[i][j] = Integer.MAX_VALUE; // Mengatur jarak awal ke tak terhingga.
            }
        }

        // Mencari node awal dan node tujuan
        Node startNode = null; // Node yang merupakan titik awal.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j].isStart) { // Jika simpul adalah titik awal.
                    startNode = grid[i][j]; // Mendapatkan node titik awal.
                    distance[i][j] = 0; // Mengatur jarak titik awal ke dirinya sendiri menjadi 0.
                } else if (grid[i][j].isGoal) { // Jika simpul adalah titik tujuan.
                    goalNode = grid[i][j]; // Mendapatkan node titik tujuan.
                }
            }
        }

        // Pengecekan apakah start dan goal ditemukan
        if (startNode == null || goalNode == null) {
            return new ArrayList<>(); // Mengembalikan array kosong jika titik awal atau titik tujuan tidak ditemukan.
        }

        // Inisialisasi array openList dan closedList
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(a -> distance[a.x][a.y])); // Antrian prioritas untuk menyimpan simpul yang akan dieksplorasi.
        ArrayList<Node> closedList = new ArrayList<>(); // Daftar untuk menyimpan simpul yang sudah dieksplorasi.

        // Menambahkan node awal ke openList
        openList.add(startNode);

        // Melakukan iterasi hingga openList kosong
        while (!openList.isEmpty()) {
            // Mengambil node dengan jarak terpendek dari openList
            Node currentNode = openList.poll(); // Menghapus dan mengembalikan simpul dengan jarak terpendek dari openList.

            // Memindahkan currentNode ke closedList
            closedList.add(currentNode); // Menambahkan currentNode ke daftar yang sudah dieksplorasi.

            // Jika currentNode adalah goalNode, maka path ditemukan
            if (currentNode == goalNode) {
                ArrayList<Node> shortestPath = new ArrayList<>(); // Array untuk menyimpan jalur terpendek.
                while (currentNode != startNode) { // Melakukan iterasi mundur dari titik tujuan ke titik awal.
                    shortestPath.add(currentNode); // Menambahkan currentNode ke jalur terpendek.
                    int parentXVal = parentX[currentNode.x][currentNode.y]; // Mendapatkan koordinat X dari parent currentNode.
                    int parentYVal = parentY[currentNode.x][currentNode.y]; // Mendapatkan koordinat Y dari parent currentNode.
                    currentNode = grid[parentXVal][parentYVal]; // Mengupdate currentNode ke parent currentNode.
                }
                shortestPath.add(startNode); // Menambahkan titik awal ke jalur terpendek.
                return shortestPath; // Mengembalikan jalur terpendek.
            }

            // Mendapatkan tetangga dari currentNode
            ArrayList<Node> neighbors = getNeighbors(currentNode); // Mendapatkan tetangga dari simpul currentNode.
            for (Node neighbor : neighbors) { // Iterasi melalui tetangga currentNode.
                if (closedList.contains(neighbor) || neighbor.isWall) { // Jika tetangga sudah dieksplorasi atau merupakan dinding.
                    continue; // Melanjutkan ke tetangga berikutnya.
                }

                int tentativeDistance = distance[currentNode.x][currentNode.y] + 1; // Menghitung jarak sementara ke tetangga currentNode.
                if (tentativeDistance < distance[neighbor.x][neighbor.y]) { // Jika jarak sementara lebih pendek dari jarak saat ini ke tetangga.
                    distance[neighbor.x][neighbor.y] = tentativeDistance; // Mengupdate jarak ke tetangga.
                    parentX[neighbor.x][neighbor.y] = currentNode.x; // Mengupdate koordinat X parent dari tetangga.
                    parentY[neighbor.x][neighbor.y] = currentNode.y; // Mengupdate koordinat Y parent dari tetangga.

                    if (!openList.contains(neighbor)) { // Jika tetangga belum ada di openList.
                        openList.add(neighbor); // Menambahkan tetangga ke openList.
                        visitedNodes.add(neighbor); // Menambahkan tetangga ke daftar simpul yang dikunjungi.
                    }
                }
            }
        }

        return new ArrayList<>(); // Mengembalikan array kosong jika jalur tidak ditemukan.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PathFinderGUI gui = new PathFinderGUI(); // Membuat instance PathFinderGUI
            gui.setVisible(true); // Menampilkan GUI
        });
    }
}




