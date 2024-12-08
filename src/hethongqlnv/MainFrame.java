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
    private String currentUsername;

    public MainFrame(String username) {
        this.currentUsername = username;
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
        JPanel settingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPopupMenu settingMenu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        JMenuItem showAdminItem = new JMenuItem("Xem thông tin Admin");
        
        JButton addButton = new JButton("Thêm nguyện vọng");
        JButton editButton = new JButton("Sửa nguyện vọng");
        JButton deleteButton = new JButton("Xóa nguyện vọng");
        JButton searchButton = new JButton("Tìm kiếm nguyện vọng");
        JButton exportButton = new JButton("Xuất CSV");
        JButton settingButton = new JButton("Setting");
        JButton statisticsButton = new JButton("Thống kê");
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(searchButton);
        panel.add(exportButton);
        panel.add(statisticsButton); // Thêm nút vào panel
        settingPanel.add(settingButton);
        settingMenu.add(showAdminItem);
        settingMenu.add(logoutItem);
        JLabel titleLabel = new JLabel("Danh sách thông tin đăng ký nguyện vọng của thí sinh: ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        
        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(titleLabel, BorderLayout.CENTER);
        northPanel.add(settingPanel, BorderLayout.EAST);

        add(northPanel, BorderLayout.NORTH);
        addPopupMenu();
        // Action listeners
        addButton.addActionListener(e -> showAddEditDialog(null));
        editButton.addActionListener(e -> editNguyenVong());
        deleteButton.addActionListener(e -> deleteNguyenVong());
        searchButton.addActionListener(e -> searchNguyenVong());
        exportButton.addActionListener(e -> exportToCSV());
        statisticsButton.addActionListener(e -> showStatistics());
        settingButton.addActionListener(e -> {
    // Hiển thị menu setting
        settingMenu.show(settingButton, 0, settingButton.getHeight());
        });
        showAdminItem.addActionListener(e -> {
                    showAdminInfo();
        });
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Đóng cửa sổ hiện tại
                new LoginFrame().setVisible(true); // Hiển thị lại màn hình đăng nhập
            }
        });
        
        loadDataFromDatabase();
    }

    private MainFrame() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private void loadDataFromDatabase() {
    try (Connection connection = DatabaseConnection.getConnection()) {
        // Truy vấn kết hợp hai bảng
        String query = """
                SELECT nv.id, nv.so_bao_danh, nv.ma_truong, nv.nganh, 
                       nv.he_dao_tao, nv.chuong_trinh_dao_tao, nv.diem_chuan, 
                       ts.diem_thi, nv.trang_thai, nv.ghi_chu 
                FROM nguyen_vong nv
                JOIN thi_sinh ts ON nv.so_bao_danh = ts.so_bao_danh;
                """;

        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        danhSachNguyenVong.clear();
        model.setRowCount(0);

        // Chuẩn bị câu lệnh UPDATE để cập nhật trạng thái
        String updateQuery = """
                UPDATE nguyen_vong
                SET trang_thai = ?
                WHERE id = ?;
                """;
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String soBaoDanh = resultSet.getString("so_bao_danh");
            String maTruong = resultSet.getString("ma_truong");
            String nganh = resultSet.getString("nganh");
            String heDaoTao = resultSet.getString("he_dao_tao");
            String chuongTrinhDaoTao = resultSet.getString("chuong_trinh_dao_tao");
            double diemChuan = resultSet.getDouble("diem_chuan");
            double diemThi = resultSet.getDouble("diem_thi");
            String ghiChu = resultSet.getString("ghi_chu");

            // So sánh điểm thi với điểm chuẩn
            String trangThai = diemThi >= diemChuan ? "Đỗ" : "Trượt";

            // Cập nhật trạng thái vào cơ sở dữ liệu
            updateStatement.setString(1, trangThai);
            updateStatement.setString(2, id);
            updateStatement.executeUpdate();

            // Tạo đối tượng NguyenVong và thêm vào danh sách
            NguyenVong nv = new NguyenVong(
                    id, soBaoDanh, maTruong, nganh, heDaoTao, chuongTrinhDaoTao,
                    diemChuan, trangThai, ghiChu
            );
            danhSachNguyenVong.add(nv);

            // Thêm dữ liệu vào bảng hiển thị
            model.addRow(nv.toArray());
        }

        // Cập nhật số thứ tự
        updateSTT();

        // Đóng statement cập nhật
        updateStatement.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi khi tải và cập nhật dữ liệu từ cơ sở dữ liệu: " + ex.getMessage());
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
            trang_thaiField.setEditable(false);
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
                query = "UPDATE nguyen_vong " +
                    "SET ma_truong=?, nganh=?, he_dao_tao=?, chuong_trinh_dao_tao=?, diem_chuan=?, trang_thai = ?, ghi_chu=? " +
                    "WHERE id=?";
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
    private void addPopupMenu() {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenuItem viewInfoItem = new JMenuItem("Xem Thông Tin");

    viewInfoItem.addActionListener(e -> {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String soBaoDanh = (String) model.getValueAt(row, 2); // Cột "Số báo danh"
            new ThongTinThiSinh(this, soBaoDanh).setVisible(true);
        }
    });

    popupMenu.add(viewInfoItem);

    table.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                int row = table.rowAtPoint(e.getPoint());
                table.setRowSelectionInterval(row, row); // Chọn hàng khi nhấp chuột phải
                popupMenu.show(table, e.getX(), e.getY());
            }
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                int row = table.rowAtPoint(e.getPoint());
                table.setRowSelectionInterval(row, row);
                popupMenu.show(table, e.getX(), e.getY());
            }
        }
    });
    } 
    private Admin getAdminInfo() {
    Admin admin = null;
    try (Connection connection = DatabaseConnection.getConnection()) {
        String query = "SELECT username, email, password FROM admin WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, currentUsername); // Sử dụng currentUsername
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            admin = new Admin(username, email, password);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin admin: " + ex.getMessage());
    }
    return admin;
}

    private void showAdminInfo() {
    Admin admin = getAdminInfo();
    if (admin != null) {
        // Tạo một hộp thoại hiển thị thông tin admin
        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Username:"));
        panel.add(new JTextField(admin.getUsername()));
        panel.add(new JLabel("Email:"));
        panel.add(new JTextField(admin.getEmail()));
        panel.add(new JLabel("Password:"));
        panel.add(new JTextField(admin.getPassword())); // Mật khẩu không cần hiển thị

        // Không cho phép chỉnh sửa mật khẩu
        ((JTextField) panel.getComponent(5)).setEditable(false);

        JOptionPane.showMessageDialog(this, panel, "Thông tin Admin", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin admin.");
    }
}
    private void showStatistics() {
    // Tạo danh sách các thí sinh đã đỗ và trượt
    DefaultListModel<String> passedModel = new DefaultListModel<>();
    DefaultListModel<String> failedModel = new DefaultListModel<>();
    
    
    // Phân loại thí sinh đã đỗ và trượt
    for (NguyenVong nv : danhSachNguyenVong) {
        if (nv.trang_thai.equals("Đỗ")) {
            passedModel.addElement(nv.so_bao_danh);
        } else {
            failedModel.addElement(nv.so_bao_danh);
        }
    }

    // Tạo các danh sách hiển thị cho thí sinh đỗ và trượt
    JList<String> passedList = new JList<>(passedModel);
    JList<String> failedList = new JList<>(failedModel);

    // Tạo các hộp cuộn để chứa danh sách
    JScrollPane passedScrollPane = new JScrollPane(passedList);
    JScrollPane failedScrollPane = new JScrollPane(failedList);

    // Tạo bảng chọn giữa "Thí sinh đỗ" và "Thí sinh trượt"
    String[] options = {"Thí sinh đỗ", "Thí sinh trượt"};
    JComboBox<String> selectionComboBox = new JComboBox<>(options);
    selectionComboBox.addActionListener(e -> {
        String selectedOption = (String) selectionComboBox.getSelectedItem();
        if ("Thí sinh đỗ".equals(selectedOption)) {
            JOptionPane.showMessageDialog(this, passedScrollPane, "Thí sinh đỗ", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, failedScrollPane, "Thí sinh trượt", JOptionPane.INFORMATION_MESSAGE);
        }
    });

    // Hiển thị hộp thoại với lựa chọn
    JPanel panel = new JPanel();
    panel.add(selectionComboBox);
    JOptionPane.showMessageDialog(this, panel, "Chọn danh sách thí sinh", JOptionPane.PLAIN_MESSAGE);
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
