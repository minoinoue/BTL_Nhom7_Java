package hethongqlnv;

public class NguyenVong {
    String maXetTuyen, hoTen, truong, nganh, heDaoTao, chuongTrinhDaoTao, trangThai, ghiChu;
    double diemThi;

    public NguyenVong(String maXetTuyen, String hoTen, double diemThi, String truong, String nganh,
                      String heDaoTao, String chuongTrinhDaoTao, String trangThai, String ghiChu) {
        this.maXetTuyen = maXetTuyen;
        this.hoTen = hoTen;
        this.diemThi = diemThi;
        this.truong = truong;
        this.nganh = nganh;
        this.heDaoTao = heDaoTao;
        this.chuongTrinhDaoTao = chuongTrinhDaoTao;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public Object[] toArray() {
        return new Object[]{"", maXetTuyen, hoTen, diemThi, truong, nganh, heDaoTao, chuongTrinhDaoTao, trangThai, ghiChu};
    }

    @Override
    public String toString() {
        return "NguyenVong{" +
                "maXetTuyen='" + maXetTuyen + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", diemThi=" + diemThi +
                ", truong='" + truong + '\'' +
                ", nganh='" + nganh + '\'' +
                ", heDaoTao='" + heDaoTao + '\'' +
                ", chuongTrinhDaoTao='" + chuongTrinhDaoTao + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
    }
}
