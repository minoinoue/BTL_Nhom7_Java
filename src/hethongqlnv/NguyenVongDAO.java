package hethongqlnv;

import java.sql.*;
import java.util.ArrayList;

public class NguyenVongDAO {
    public ArrayList<NguyenVong> getAllNguyenVong() {
        ArrayList<NguyenVong> danhSach = new ArrayList<>();
        String query = "SELECT * FROM nguyen_vong";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                NguyenVong nv = new NguyenVong(
                        rs.getString("id"),
                        rs.getString("so_bao_danh"),           
                        rs.getString("ma_truong"),             
                        rs.getString("nganh"),                 
                        rs.getString("he_dao_tao"),            
                        rs.getString("chuong_trinh_dao_tao"), 
                        rs.getDouble("diem_chuan"),           
                        rs.getString("trang_thai"), 
                        rs.getString("ghi_chu")
                );
                danhSach.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return danhSach;
    }
}
