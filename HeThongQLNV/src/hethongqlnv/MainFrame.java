package hethongqlnv;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
class MainFrame extends JFrame {
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
        model.setColumnIdentifiers(new Object[]{"STT", "Mã Xét Tuyển", "Họ Tên", "Điểm Thi", "Trường", "Ngành", "Hệ Đào Tạo", "Chương Trình Đào Tạo", "Trạng Thái", "Ghi Chú"});

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

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

        addButton.addActionListener(e -> showAddEditDialog(null));
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                showAddEditDialog(danhSachNguyenVong.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Chọn một nguyện vọng để sửa!");
            }
        });
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa nguyện vọng này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    danhSachNguyenVong.remove(row);
                    model.removeRow(row);
                    updateSTT();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Chọn một nguyện vọng để xóa!");
            }
        });
        searchButton.addActionListener(e -> {
            String searchTerm = JOptionPane.showInputDialog("Nhập mã xét tuyển cần tìm:");
            if (searchTerm != null) {
                search(searchTerm);
            }
        });

        exportButton.addActionListener(e -> exportToCSV());
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
        diemThiField.setText(nv.diemThi);
        truongField.setText(nv.truong);
        nganhField.setText(nv.nganh);
        heDaoTaoField.setText(nv.heDaoTao);
        chuongTrinhDaoTaoField.setText(nv.chuongTrinhDaoTao);
        trangThaiField.setText(nv.trangThai);
        ghiChuField.setText(nv.ghiChu);
    }

    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(10, 2));
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

    int result = JOptionPane.showConfirmDialog(this, panel, "Thêm/Sửa nguyện vọng", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        try {
            double diemThi = Double.parseDouble(diemThiField.getText());
            if (maXetTuyenField.getText().isEmpty() || hoTenField.getText().isEmpty() || diemThiField.getText().isEmpty() || truongField.getText().isEmpty() || nganhField.getText().isEmpty() || heDaoTaoField.getText().isEmpty() || chuongTrinhDaoTaoField.getText().isEmpty() || trangThaiField.getText().isEmpty() || ghiChuField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            NguyenVong newNv = new NguyenVong(maXetTuyenField.getText(), hoTenField.getText(), String.valueOf(diemThi), truongField.getText(), nganhField.getText(), heDaoTaoField.getText(), chuongTrinhDaoTaoField.getText(), trangThaiField.getText(), ghiChuField.getText());

            if (nv == null) {
                danhSachNguyenVong.add(newNv);
                model.addRow(newNv.toArray());
                updateSTT();
            } else {
                int row = table.getSelectedRow();
                danhSachNguyenVong.set(row, newNv);
                for (int i = 0; i < model.getColumnCount(); i++) {
                    model.setValueAt(newNv.toArray()[i], row, i);
                }
                updateSTT();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Điểm thi phải là một số thực!");
        }
    }
}


    private void exportToCSV() {
        try {
            FileWriter writer = new FileWriter("nguyen_vong.csv");
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            for (int i = 0; i < model.getColumnCount(); i++) {
                bufferedWriter.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) bufferedWriter.write(",");
            }
            bufferedWriter.newLine();

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    bufferedWriter.write(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() - 1) bufferedWriter.write(",");
                }
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
            JOptionPane.showMessageDialog(this, "Xuất CSV thành công!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất CSV!");
        }
    }

    private void search(String searchTerm) {
        boolean found = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            String maXetTuyen = (String) model.getValueAt(i, 1);
            if (maXetTuyen.equals(searchTerm)) {
                table.setRowSelectionInterval(i, i);
                found = true;
                break;
            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mã xét tuyển!");
        }
    }

    private void updateSTT() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }
}
