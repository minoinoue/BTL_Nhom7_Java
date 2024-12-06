package hethongqlnv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<NguyenVong> danhSachNguyenVong;

    public MainFrame() {
        setTitle("Quản lý đăng ký nguyện vọng xét tuyển");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        danhSachNguyenVong = new ArrayList<>();
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{
                "STT", "Mã Xét Tuyển", "Họ Tên", "Điểm Thi", "Trường", 
                "Ngành", "Hệ Đào Tạo", "Chương Trình Đào Tạo", "Trạng Thái", "Ghi Chú"
        });

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Thêm nguyện vọng");
        JButton editButton = new JButton("Sửa nguyện vọng");
        JButton deleteButton = new JButton("Xóa nguyện vọng");
        JButton searchButton = new JButton("Tìm kiếm nguyện vọng");
        JButton exportButton = new JButton("Xuất CSV");

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(searchButton);
        panel.add(exportButton);

        JLabel titleLabel = new JLabel("Danh sách thông tin đăng ký nguyện vọng của thí sinh");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        // Action listeners
        addButton.addActionListener(e -> showAddEditDialog(null));
        editButton.addActionListener(e -> editNguyenVong());
        deleteButton.addActionListener(e -> deleteNguyenVong());
        searchButton.addActionListener(e -> searchNguyenVong());
        exportButton.addActionListener(e -> exportToCSV());

        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM nguyen_vong";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            danhSachNguyenVong.clear();
            model.setRowCount(0);

            while (resultSet.next()) {
                NguyenVong nv = new NguyenVong(
                        resultSet.getString("MaXetTuyen"),
                        resultSet.getString("HoTen"),
                        resultSet.getDouble("DiemThi"),
                        resultSet.getString("Truong"),
                        resultSet.getString("Nganh"),
                        resultSet.getString("HeDaoTao"),
                        resultSet.getString("ChuongTrinhDaoTao"),
                        resultSet.getString("TrangThai"),
                        resultSet.getString("GhiChu")
                );
                danhSachNguyenVong.add(nv);
                model.addRow(nv.toArray());
            }

            updateSTT();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu từ cơ sở dữ liệu: " + ex.getMessage());
        }
    }

    private void showAddEditDialog(NguyenVong nv) {
        JTextField maXetTuyenField = new JTextField(20);
        JTextField hoTenField = new JTextField(20);
        JTextField diemThiField = new JTextField(20);
        JTextField truongField = new JTextField(20);
        JTextField nganhField = new JTextField(20);
        JTextField heDaoTaoField = new JTextField(20);
        JTextField chuongTrinhDaoTaoField = new JTextField(20);
        JTextField trangThaiField = new JTextField(20);
        JTextField ghiChuField = new JTextField(20);

        if (nv != null) {
            maXetTuyenField.setText(nv.maXetTuyen);
            hoTenField.setText(nv.hoTen);
            diemThiField.setText(String.valueOf(nv.diemThi));
            truongField.setText(nv.truong);
            nganhField.setText(nv.nganh);
            heDaoTaoField.setText(nv.heDaoTao);
            chuongTrinhDaoTaoField.setText(nv.chuongTrinhDaoTao);
            trangThaiField.setText(nv.trangThai);
            ghiChuField.setText(nv.ghiChu);
            maXetTuyenField.setEditable(false);
        }

        JPanel panel = new JPanel(new GridLayout(10, 2));
        panel.add(new JLabel("Mã Xét Tuyển:"));
        panel.add(maXetTuyenField);
        panel.add(new JLabel("Họ Tên:"));
        panel.add(hoTenField);
        panel.add(new JLabel("Điểm Thi:"));
        panel.add(diemThiField);
        panel.add(new JLabel("Trường:"));
        panel.add(truongField);
        panel.add(new JLabel("Ngành:"));
        panel.add(nganhField);
        panel.add(new JLabel("Hệ Đào Tạo:"));
        panel.add(heDaoTaoField);
        panel.add(new JLabel("Chương Trình Đào Tạo:"));
        panel.add(chuongTrinhDaoTaoField);
        panel.add(new JLabel("Trạng Thái:"));
        panel.add(trangThaiField);
        panel.add(new JLabel("Ghi Chú:"));
        panel.add(ghiChuField);

        int option = JOptionPane.showConfirmDialog(this, panel, nv == null ? "Thêm Nguyện Vọng" : "Sửa Nguyện Vọng", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String maXetTuyen = maXetTuyenField.getText().trim();
            String hoTen = hoTenField.getText().trim();
            double diemThi = Double.parseDouble(diemThiField.getText().trim());
            String truong = truongField.getText().trim();
            String nganh = nganhField.getText().trim();
            String heDaoTao = heDaoTaoField.getText().trim();
            String chuongTrinhDaoTao = chuongTrinhDaoTaoField.getText().trim();
            String trangThai = trangThaiField.getText().trim();
            String ghiChu = ghiChuField.getText().trim();

            NguyenVong newNv = new NguyenVong(maXetTuyen, hoTen, diemThi, truong, nganh, heDaoTao, chuongTrinhDaoTao, trangThai, ghiChu);

            if (nv == null) {
                danhSachNguyenVong.add(newNv);
                model.addRow(newNv.toArray());
                saveDataToDatabase(newNv, true);
                JOptionPane.showMessageDialog(this, "Thêm nguyện vọng thành công!");
            } else {
                int row = table.getSelectedRow();
                danhSachNguyenVong.set(row, newNv);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    model.setValueAt(newNv.toArray()[i], row, i);
                }
                JOptionPane.showMessageDialog(this, "Sửa nguyện vọng thành công!");
                saveDataToDatabase(newNv, false);
            }
            updateSTT();
        }
    }

    private void editNguyenVong() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            showAddEditDialog(danhSachNguyenVong.get(row));
        } else {
            JOptionPane.showMessageDialog(this, "Chọn một nguyện vọng để sửa!");
        }
    }

    private void deleteNguyenVong() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nguyện vọng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteDataFromDatabase(danhSachNguyenVong.get(row));
                danhSachNguyenVong.remove(row);
                model.removeRow(row);
                updateSTT();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Chọn một nguyện vọng để xóa!");
        }
    }

    private void searchNguyenVong() {
        String searchTerm = JOptionPane.showInputDialog("Nhập mã xét tuyển cần tìm:");
        if (searchTerm != null) {
            boolean found = false;
            for (NguyenVong nv : danhSachNguyenVong) {
                if (nv.maXetTuyen.equalsIgnoreCase(searchTerm)) {
                    JOptionPane.showMessageDialog(this, "Tìm thấy nguyện vọng:\n" + nv);
                    found = true;
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nguyện vọng với mã xét tuyển: " + searchTerm);
            } else {
                JOptionPane.showMessageDialog(this, "Tìm thấy thành công mã xét tuyển: " + searchTerm);
            }
        }
    }

    private void saveDataToDatabase(NguyenVong nv, boolean isNew) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query;
            if (isNew) {
                query = "INSERT INTO nguyen_vong VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                query = "UPDATE nguyen_vong SET HoTen=?, DiemThi=?, Truong=?, Nganh=?, HeDaoTao=?, ChuongTrinhDaoTao=?, TrangThai=?, GhiChu=? WHERE MaXetTuyen=?";
            }

            PreparedStatement statement = connection.prepareStatement(query);
            if (isNew) {
                statement.setString(1, nv.maXetTuyen);
                statement.setString(2, nv.hoTen);
                statement.setDouble(3, nv.diemThi);
                statement.setString(4, nv.truong);
                statement.setString(5, nv.nganh);
                statement.setString(6, nv.heDaoTao);
                statement.setString(7, nv.chuongTrinhDaoTao);
                statement.setString(8, nv.trangThai);
                statement.setString(9, nv.ghiChu);
            } else {
                statement.setString(9, nv.maXetTuyen);
                statement.setString(1, nv.hoTen);
                statement.setDouble(2, nv.diemThi);
                statement.setString(3, nv.truong);
                statement.setString(4, nv.nganh);
                statement.setString(5, nv.heDaoTao);
                statement.setString(6, nv.chuongTrinhDaoTao);
                statement.setString(7, nv.trangThai);
                statement.setString(8, nv.ghiChu);
            }

            statement.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage());
        }
    }

    private void deleteDataFromDatabase(NguyenVong nv) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM nguyen_vong WHERE MaXetTuyen = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nv.maXetTuyen);
            statement.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa dữ liệu: " + ex.getMessage());
        }
    }

    private void updateSTT() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Luu tai");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.write(model.getColumnName(i) + ",");
                }
                writer.write("\n");

                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        writer.write(model.getValueAt(i, j) + ",");
                    }
                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(this, "Xuất file CSV thành công!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file CSV: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
