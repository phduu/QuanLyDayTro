package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class QuanLyDayTro extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField tfMaPhong, tfTenKhach, tfSoDienThoai, tfGiaThue, tfTimKiem;

    private Connection conn;

    public QuanLyDayTro() {
        // Kết nối cơ sở dữ liệu
        connectToDatabase();

        // Thiết lập giao diện
        initComponents();

        // Tải dữ liệu lên bảng
        loadDataToTable();
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/daytro";
            String user = "root"; // Thay bằng tài khoản MySQL của bạn
            String password = ""; // Thay bằng mật khẩu MySQL của bạn

            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void initComponents() {
        setTitle("Quản Lý Dãy Trọ");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Bảng dữ liệu
        model = new DefaultTableModel(new String[]{"ID", "Mã Phòng", "Tên Khách", "Số Điện Thoại", "Giá Thuê"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        inputPanel.add(new JLabel("Mã Phòng:"));
        tfMaPhong = new JTextField();
        inputPanel.add(tfMaPhong);

        inputPanel.add(new JLabel("Tên Khách:"));
        tfTenKhach = new JTextField();
        inputPanel.add(tfTenKhach);

        inputPanel.add(new JLabel("Số Điện Thoại:"));
        tfSoDienThoai = new JTextField();
        inputPanel.add(tfSoDienThoai);

        inputPanel.add(new JLabel("Giá Thuê:"));
        tfGiaThue = new JTextField();
        inputPanel.add(tfGiaThue);

        inputPanel.add(new JLabel("Tìm Kiếm:"));
        tfTimKiem = new JTextField();
        inputPanel.add(tfTimKiem);

        // Panel chức năng
        JPanel controlPanel = new JPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnSearch = new JButton("Tìm Kiếm");

        controlPanel.add(btnAdd);
        controlPanel.add(btnUpdate);
        controlPanel.add(btnDelete);
        controlPanel.add(btnSearch);

        // Xử lý sự kiện
        btnAdd.addActionListener(e -> addPhongTro());
        btnUpdate.addActionListener(e -> updatePhongTro());
        btnDelete.addActionListener(e -> deletePhongTro());
        btnSearch.addActionListener(e -> searchPhongTro());
    }

    private void loadDataToTable() {
        try {
            model.setRowCount(0); // Xóa dữ liệu cũ
            String query = "SELECT * FROM phong_tro";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("ma_phong"),
                        rs.getString("ten_khach"),
                        rs.getString("so_dien_thoai"),
                        rs.getDouble("gia_thue")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addPhongTro() {
        try {
            String query = "INSERT INTO phong_tro (ma_phong, ten_khach, so_dien_thoai, gia_thue) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, tfMaPhong.getText());
            pstmt.setString(2, tfTenKhach.getText());
            pstmt.setString(3, tfSoDienThoai.getText());
            pstmt.setDouble(4, Double.parseDouble(tfGiaThue.getText()));

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadDataToTable();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePhongTro() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để sửa!");
                return;
            }

            int id = (int) model.getValueAt(selectedRow, 0);

            String query = "UPDATE phong_tro SET ma_phong = ?, ten_khach = ?, so_dien_thoai = ?, gia_thue = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, tfMaPhong.getText());
            pstmt.setString(2, tfTenKhach.getText());
            pstmt.setString(3, tfSoDienThoai.getText());
            pstmt.setDouble(4, Double.parseDouble(tfGiaThue.getText()));
            pstmt.setInt(5, id);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadDataToTable();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePhongTro() {
        try {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng để xóa!");
                return;
            }

            int id = (int) model.getValueAt(selectedRow, 0);

            String query = "DELETE FROM phong_tro WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loadDataToTable();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPhongTro() {
        try {
            model.setRowCount(0); // Xóa dữ liệu cũ
            String keyword = tfTimKiem.getText();

            String query = "SELECT * FROM phong_tro WHERE ma_phong LIKE ? OR ten_khach LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("ma_phong"),
                        rs.getString("ten_khach"),
                        rs.getString("so_dien_thoai"),
                        rs.getDouble("gia_thue")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuanLyDayTro().setVisible(true));
    }
}