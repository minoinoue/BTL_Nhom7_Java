package hethongqlnv;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame() {
        setTitle("Đăng nhập");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Tên đăng nhập:");
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        JTextField userTextField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Đăng nhập");

        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> {
            String username = userTextField.getText();
            String password = new String(passwordField.getPassword());

            if (username.equals("admin") && password.equals("12345")) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                new MainFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu sai!");
            }
        });
    }
}
