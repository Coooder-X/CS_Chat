package UI;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class userLogin extends JFrame{
	String userName;
	String pwd;
	public JTextArea nametxt;
	public JPasswordField pwdtxt = new JPasswordField();
	public JButton login = new JButton("登录");
	JButton regin = new JButton("注册");
	
	public userLogin() {
		this.setTitle("登录");
		this.setBounds(((Toolkit.getDefaultToolkit().getScreenSize().width)/3)-150, ((Toolkit.getDefaultToolkit().getScreenSize().height)/3)-150,300,300);
		this.setLayout(new BorderLayout());
		
		login.setFont(new Font("宋体",Font.BOLD,20));
		regin.setFont(new Font("宋体",Font.BOLD,20));
		nametxt = new JTextArea("");
//		pwdtxt = new JTextArea("");
		nametxt.setFont(new Font("宋体",Font.BOLD,20));
//		nametxt.setBorder(new Border());
		nametxt.setBorder(BorderFactory.createEtchedBorder());
		pwdtxt.setFont(new Font("宋体",Font.BOLD,20));
		
		this.setLayout(new GridLayout(3, 1, 0, 9));
		
		JPanel head = new JPanel();
//		this.add(head, BorderLayout.NORTH);
//		head.setBounds(0, 0, 200, 200);
		head.setLayout(new BorderLayout());
//		head.add(new JTextArea(), BorderLayout.CENTER);
		
		this.add(new JLabel());
//		this.add(new JLabel());
		JPanel p1 = new JPanel();
		p1.setLayout(new BorderLayout());
//		p1.setBounds(0, 0, 100, 100);
		JPanel p11 = new JPanel();
		JPanel p12 = new JPanel();
		
		p11.setLayout(new BorderLayout());
		p12.setLayout(new BorderLayout());
		p11.add(nametxt, BorderLayout.CENTER);
		p12.add(pwdtxt, BorderLayout.CENTER);
		JLabel l1 = new JLabel("  用户：");
		JLabel l2 = new JLabel("  密码：");
		l1.setFont(new Font("黑体",Font.BOLD,20));
		l2.setFont(new Font("黑体",Font.BOLD,20));
		p11.add(l1, BorderLayout.WEST);
		p12.add(l2, BorderLayout.WEST);
		p11.add(new JLabel("       "), BorderLayout.EAST);
		p12.add(new JLabel("       "), BorderLayout.EAST);
		p1.add(p11, BorderLayout.NORTH);
		p1.add(p12, BorderLayout.SOUTH);
		this.add(p1);
		
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(3, 3, 0, 0));
		
		p2.add(new JLabel(" "));
		p2.add(new JLabel(" "));
		p2.add(new JLabel(" "));
		p2.add(login);
		p2.add(new JLabel(" "));
		p2.add(regin);
		p2.add(new JLabel(" "));
		p2.add(new JLabel(" "));
		p2.add(new JLabel(" "));
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		p3.add(p2, BorderLayout.CENTER);
		p3.add(new JLabel("        "), BorderLayout.WEST);
		p3.add(new JLabel("        "), BorderLayout.EAST);
		this.add(p3);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void close() {
		this.dispose();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		userLogin login = new userLogin();
	}

}
