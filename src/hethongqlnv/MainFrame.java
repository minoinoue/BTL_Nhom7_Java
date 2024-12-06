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
                "STT", "ID", "Số báo danh", "Mã Trường", 
                "Ngành", "Hệ Đào Tạo", "Chương Trình Đào Tạo", "Điểm Chuẩn", "Trạng Thái", "Ghi Chú"
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
        String query = "SELECT id,nv.so_bao_danh, ma_truong, nganh, he_dao_tao, chuong_trinh_dao_tao, diem_chuan, trang_thai, ghi_chu " +
                       "FROM nguyen_vong nv " +
                       "JOIN thi_sinh ts ON nv.so_bao_danh = ts.so_bao_danh;";

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        danhSachNguyenVong.clear();
        model.setRowCount(0);

        while (resultSet.next()) {
            NguyenVong nv = new NguyenVong(
                    resultSet.getString("id"),
                    resultSet.getString("so_bao_danh"),
                    resultSet.getString("ma_truong"),
                    resultSet.getString("nganh"),
                    resultSet.getString("he_dao_tao"),
                    resultSet.getString("chuong_trinh_dao_tao"),
                    resultSet.getDouble("diem_chuan"),
                    resultSet.getString("trang_thai"),
                    resultSet.getString("ghi_chu")
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
        JTextField so_bao_danhField = new JTextField(20);
        JTextField ma_truongField = new JTextField(20);
        JTextField nganhField = new JTextField(20);
        JTextField he_dao_taoField = new JTextField(20);
        JTextField chuong_trinh_dao_taoField = new JTextField(20);
        JTextField diem_chuanField = new JTextField(20);
        JTextField trang_thaiField = new JTextField(20);
        JTextField ghi_chuField = new JTextField(20);

        if (nv != null) {
            so_bao_danhField.setText(nv.so_bao_danh);
            ma_truongField.setText(nv.ma_truong);
            nganhField.setText(nv.nganh);
            he_dao_taoField.setText(nv.he_dao_tao);
            chuong_trinh_dao_taoField.setText(nv.chuong_trinh_dao_tao);
            diem_chuanField.setText(String.valueOf(nv.diem_chuan));
            trang_thaiField.setText(nv.trang_thai);
            ghi_chuField.setText(nv.ghi_chu);
            so_bao_danhField.setEditable(false);
        }

        JPanel panel = new JPanel(new GridLayout(10, 2));
        panel.add(new JLabel("Số Báo Danh:"));
        panel.add(so_bao_danhField);
        panel.add(new JLabel("Mã Trường:"));
        panel.add(ma_truongField);
        panel.add(new JLabel("Ngành:"));
        panel.add(nganhField);
        panel.add(new JLabel("Hệ Đào Tạo:"));
        panel.add(he_dao_taoField);
        panel.add(new JLabel("Chương Trình Đào Tạo:"));
        panel.add(chuong_trinh_dao_taoField);
        panel.add(new JLabel("Điểm Chuẩn:"));
        panel.add(diem_chuanField);
        panel.add(new JLabel("Trạng Thái:"));
        panel.add(trang_thaiField);
        panel.add(new JLabel("Ghi Chú:"));
        panel.add(ghi_chuField);

        int option = JOptionPane.showConfirmDialog(this, panel, nv == null ? "Thêm Nguyện Vọng" : "Sửa Nguyện Vọng", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String so_bao_danh = so_bao_danhField.getText().trim();
            String ma_truong = ma_truongField.getText().trim();
            String nganh = nganhField.getText().trim();
            String he_dao_tao = he_dao_taoField.getText().trim();
            String chuong_trinh_dao_tao = chuong_trinh_dao_taoField.getText().trim();
            double diem_chuan = Double.parseDouble(diem_chuanField.getText().trim());
            String trang_thai = trang_thaiField.getText().trim();
            String ghi_chu = ghi_chuField.getText().trim();

            String id = "NV" + (danhSachNguyenVong.size() + 1);
            
            NguyenVong newNv = new NguyenVong(id,so_bao_danh,ma_truong, nganh, he_dao_tao, chuong_trinh_dao_tao, diem_chuan, trang_thai, ghi_chu);

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
        String searchTerm = JOptionPane.showInputDialog("Nhập số báo danh cần tìm:");
        if (searchTerm != null) {
            boolean found = false;
            for (NguyenVong nv : danhSachNguyenVong) {
                if (nv.so_bao_danh.equalsIgnoreCase(searchTerm)) {
                    JOptionPane.showMessageDialog(this, "Tìm thấy nguyện vọng:\n" + nv);
                    found = true;
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nguyện vọng với số báo danh: " + searchTerm);
            } else {
                JOptionPane.showMessageDialog(this, "Tìm thấy thành công số báo danh: " + searchTerm);
            }
        }
    }

    private void saveDataToDatabase(NguyenVong nv, boolean isNew) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query;
            if (isNew) {query = "INSERT INTO nguyen_vong (id,so_bao_danh, ma_truong, nganh, he_dao_tao, chuong_trinh_dao_tao, diem_chuan, trang_thai, ghi_chu) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?)";
            }else {
                query = "UPDATE nguyen_vong SET ma_truong=?,nganh=?, he_dao_tao=?, chuong_trinh_dao_tao=?, diem_chuan=?, trang_thai=?, ghi_chu=? WHERE id = ? AND so_bao_danh=?";
            }

            PreparedStatement statement = connection.prepareStatement(query);
            if (isNew) {
            statement.setString(1, nv.id);
            statement.setString(2, nv.so_bao_danh);
            statement.setString(3, nv.ma_truong);
            statement.setString(4, nv.nganh);
            statement.setString(5, nv.he_dao_tao);
            statement.setString(6, nv.chuong_trinh_dao_tao);
            statement.setDouble(7, nv.diem_chuan);
            statement.setString(8, nv.trang_thai);
            statement.setString(9, nv.ghi_chu);
            } else {
            statement.setString(1, nv.ma_truong);
            statement.setString(2, nv.nganh);
            statement.setString(3, nv.he_dao_tao);
            statement.setString(4, nv.chuong_trinh_dao_tao);
            statement.setDouble(5, nv.diem_chuan);
            statement.setString(6, nv.trang_thai);
            statement.setString(7, nv.ghi_chu);
            statement.setString(8, nv.id);
            statement.setString(9, nv.so_bao_danh);
        }

            statement.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage());
        }
    }

    private void deleteDataFromDatabase(NguyenVong nv) {
    try (Connection connection = DatabaseConnection.getConnection()) {
        String query = "DELETE FROM nguyen_vong WHERE so_bao_danh = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nv.so_bao_danh);
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
                        Object value = model.getValueAt(i, j);
                        writer.write((value != null ? value.toString() : "") + ",");
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
