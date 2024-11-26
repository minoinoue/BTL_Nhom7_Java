package hethongqlnv;
public class NguyenVong {
    String maXetTuyen;
    String hoTen;
    String diemThi;
    String truong;
    String nganh;
    String heDaoTao;
    String chuongTrinhDaoTao;
    String trangThai;
    String ghiChu;

    public NguyenVong(String maXetTuyen, String hoTen, String diemThi, String truong, String nganh, String heDaoTao, String chuongTrinhDaoTao, String trangThai, String ghiChu) {
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
}