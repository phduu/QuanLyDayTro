import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;

public class QuanLyDayTro extends JFrame {
    private JTabbedPane tabbedPane;
    private DefaultTableModel roomTableModel;
    private JTable roomTable;
    private JTextField txtRoomId, txtRoomName, txtArea, txtRent;
    private Connection connection;

    public QuanLyDayTro() {
        // Thiết lập giao diện
        setTitle("Quản Lý Dãy Trọ");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel chính
        tabbedPane = new JTabbedPane();

        // Thêm các tab
        tabbedPane.addTab("Phòng Trọ", createRoomPanel());
        tabbedPane.addTab("Khách Thuê", createCustomerPanel());  // Tạo panel khách thuê (chưa có trong mã gốc)
        tabbedPane.addTab("Tài Chính", createFinancePanel());   // Tạo panel tài chính (chưa có trong mã gốc)
        tabbedPane.addTab("Hợp Đồng", createContractPanel());   // Tạo panel hợp đồng (chưa có trong mã gốc)

        add(tabbedPane);
        setVisible(true);

        // Kết nối cơ sở dữ liệu
        connectDatabase();

        // Hiển thị dữ liệu
        loadRoomData();
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Bảng hiển thị dữ liệu
        roomTableModel = new DefaultTableModel(new String[]{"ID", "Tên phòng", "Diện tích", "Giá thuê"}, 0);
        roomTable = new JTable(roomTableModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin phòng"));

        txtRoomId = new JTextField();
        txtRoomName = new JTextField();
        txtArea = new JTextField();
        txtRent = new JTextField();

        inputPanel.add(new JLabel("ID Phòng:"));
        inputPanel.add(txtRoomId);
        inputPanel.add(new JLabel("Tên phòng:"));
        inputPanel.add(txtRoomName);
        inputPanel.add(new JLabel("Diện tích:"));
        inputPanel.add(txtArea);

        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        btnAdd.addActionListener(e -> addRoom());
        btnUpdate.addActionListener(e -> updateRoom());
        btnDelete.addActionListener(e -> deleteRoom());

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomerPanel() {
        // Panel này sẽ được tạo sau khi bạn quyết định cách xử lý dữ liệu khách thuê
        return new JPanel();
    }

    private JPanel createFinancePanel() {
        // Panel này sẽ được tạo sau khi bạn quyết định cách xử lý dữ liệu tài chính
        return new JPanel();
    }

    private JPanel createContractPanel() {
        // Panel này sẽ được tạo sau khi bạn quyết định cách xử lý dữ liệu hợp đồng
        return new JPanel();
    }

    private void connectDatabase() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=QL_DayTro;encrypt=true;trustServerCertificate=true";
        String userName = "sa";
        String password = "123456789";

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, userName, password);
            System.out.println("Kết nối cơ sở dữ liệu thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoomData() {
        try {
            roomTableModel.setRowCount(0); // Xóa dữ liệu cũ trong bảng
            String sql = "SELECT * FROM phong_tro";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                roomTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("ten_phong"),
                        rs.getDouble("dien_tich"),
                        rs.getBigDecimal("gia_thue")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addRoom() {
        String roomId = txtRoomId.getText();
        String roomName = txtRoomName.getText();
        String area = txtArea.getText();
        String rent = txtRent.getText();

        try {
            String sql = "INSERT INTO phong_tro (id, ten_phong, dien_tich, gia_thue) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(roomId));
            ps.setString(2, roomName);
            ps.setDouble(3, Double.parseDouble(area));
            ps.setBigDecimal(4, new BigDecimal(rent));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
            loadRoomData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateRoom() {
        String roomId = txtRoomId.getText();
        String roomName = txtRoomName.getText();
        String area = txtArea.getText();
        String rent = txtRent.getText();

        try {
            String sql = "UPDATE phong_tro SET ten_phong = ?, dien_tich = ?, gia_thue = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, roomName);
            ps.setDouble(2, Double.parseDouble(area));
            ps.setBigDecimal(3, new BigDecimal(rent));
            ps.setInt(4, Integer.parseInt(roomId));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật phòng thành công!");
            loadRoomData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteRoom() {
        String roomId = txtRoomId.getText();

        try {
            String sql = "DELETE FROM phong_tro WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(roomId));

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa phòng thành công!");
            loadRoomData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa phòng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyDayTro app = new QuanLyDayTro();
            app.setVisible(true);
        });
    }
}
