package UI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class wrongPage extends JFrame{
	
	public wrongPage() {
		this.setBounds(((Toolkit.getDefaultToolkit().getScreenSize().width)/3)-150, ((Toolkit.getDefaultToolkit().getScreenSize().height)/3)-150,300,170);
		this.setLayout(new BorderLayout());
		JLabel l = new JLabel("  密码错误！请重新输入。");
		l.setFont(new Font("宋体",Font.BOLD,20));
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(l, BorderLayout.CENTER);
		this.add(l,BorderLayout.CENTER);
		this.add(new JPanel(), BorderLayout.EAST);
		this.add(new JPanel(), BorderLayout.WEST);
		this.add(new JPanel(), BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		wrongPage w = new wrongPage(); 
	}

}
