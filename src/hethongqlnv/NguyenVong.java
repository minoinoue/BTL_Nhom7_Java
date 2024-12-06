package hethongqlnv;

public class NguyenVong {
    String id,so_bao_danh, ma_truong, nganh, he_dao_tao, chuong_trinh_dao_tao, trang_thai, ghi_chu;
    double diem_chuan;

    // Constructor với thông tin thêm về tên thí sinh
    public NguyenVong(String id, String so_bao_danh, String ma_truong, String nganh,
                      String he_dao_tao, String chuong_trinh_dao_tao, double diem_chuan, String trang_thai, String ghi_chu) {
        this.id = id;
        this.so_bao_danh = so_bao_danh;
        this.ma_truong = ma_truong;
        this.nganh = nganh;
        this.he_dao_tao = he_dao_tao;
        this.chuong_trinh_dao_tao = chuong_trinh_dao_tao;
        this.diem_chuan = diem_chuan;
        this.trang_thai = trang_thai;
        this.ghi_chu = ghi_chu;
    }

    // Phương thức chuyển đối tượng thành mảng dữ liệu cho bảng
    public Object[] toArray() {
        return new Object[]{"",id,so_bao_danh, ma_truong, nganh, he_dao_tao, chuong_trinh_dao_tao, diem_chuan, trang_thai, ghi_chu};
    }

    @Override
    public String toString() {
        return "NguyenVong{" +
                "id='" + id + '\'' +
                "so_bao_danh='" + so_bao_danh + '\'' +
                ", ma_truong='" + ma_truong + '\'' +
                ", nganh='" + nganh + '\'' +
                ", he_dao_tao='" + he_dao_tao + '\'' +
                ", chuong_trinh_dao_tao='" + chuong_trinh_dao_tao + '\'' +
                ", diem_chuan=" + diem_chuan +
                ", trang_thai='" + trang_thai + '\'' +
                ", ghi_chu='" + ghi_chu + '\'' +
                '}';
    }
}
