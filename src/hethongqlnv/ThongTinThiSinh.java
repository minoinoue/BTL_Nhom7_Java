package hethongqlnv;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThongTinThiSinh extends JDialog {
    public ThongTinThiSinh(JFrame parent, String soBaoDanh) {
        super(parent, "Thông Tin Thí Sinh", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblHoTen = new JLabel("Họ Tên:");
        JLabel lblGioiTinh = new JLabel("Giới Tính:");
        JLabel lblNamSinh = new JLabel("Năm Sinh:");
        JLabel lblQueQuan = new JLabel("Quê Quán:");
        JLabel lblDiemThi = new JLabel("Điểm Thi:");

        JLabel hoTenValue = new JLabel();
        JLabel gioiTinhValue = new JLabel();
        JLabel namSinhValue = new JLabel();
        JLabel queQuanValue = new JLabel();
        JLabel diemThiValue = new JLabel();

        panel.add(lblHoTen);
        panel.add(hoTenValue);
        panel.add(lblGioiTinh);
        panel.add(gioiTinhValue);
        panel.add(lblNamSinh);
        panel.add(namSinhValue);
        panel.add(lblQueQuan);
        panel.add(queQuanValue);
        panel.add(lblDiemThi);
        panel.add(diemThiValue);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT ho_ten, gioi_tinh, nam_sinh, que_quan, diem_thi FROM thi_sinh WHERE so_bao_danh = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, soBaoDanh);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                hoTenValue.setText(rs.getString("ho_ten"));
                gioiTinhValue.setText(rs.getString("gioi_tinh"));
                namSinhValue.setText(String.valueOf(rs.getInt("nam_sinh")));
                queQuanValue.setText(rs.getString("que_quan"));
                diemThiValue.setText(String.valueOf(rs.getDouble("diem_thi")));
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin thí sinh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin thí sinh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        add(panel);
    }
}