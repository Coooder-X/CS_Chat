package UI;

import javax.swing.*;

import java.awt.*;

public class client_win extends JFrame{
	 JTextArea ip, userName, input;
//	private JTextField chat;
	 TextArea chat;
	private JButton sent;
	public String oppID = null;
	
	public client_win(/* int h, int w */){
//		height = h;
//		width = w;
//		sent.addMouseListener(new sentText(this.getinput(), this.getchat()));
		this.setBounds(((Toolkit.getDefaultToolkit().getScreenSize().width)/3)-150, ((Toolkit.getDefaultToolkit().getScreenSize().height)/3)-150,480,420);
		ip = new JTextArea();
		ip.setFont(new Font("黑体",Font.BOLD,17));
		ip.setBorder(BorderFactory.createEtchedBorder());
		ip.setEditable(false);
		userName = new JTextArea();
		userName.setBorder(BorderFactory.createEtchedBorder());
		userName.setFont(new Font("黑体",Font.BOLD,17));
		userName.setEditable(false);
		chat = new TextArea("", 20, 43, TextArea.SCROLLBARS_VERTICAL_ONLY);
		
		chat.setFont(new Font("宋体",Font.BOLD,17));
		chat.setBackground(new Color(255,255,255));
		chat.setEditable(false);
		input = new JTextArea();
		input.setBorder(BorderFactory.createEtchedBorder());
		input.setFont(new Font("宋体",Font.BOLD,17));
		sent = new JButton();
		sent.setText("发送");
		sent.setFont(new Font("黑体",Font.BOLD,17));
		
		this.setTitle("客户端");
		this.setLayout(new BorderLayout());
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		
		p1.setLayout(new BorderLayout());
		p1.add(ip, BorderLayout.WEST);
		p1.add(userName, BorderLayout.EAST);
		p1.add(new JPanel(), BorderLayout.SOUTH);
		
//		p2.setLayout(new BorderLayout());
//		JScrollPane text2=new JScrollPane(chat);
//		p2.add(text2);
//		text2.setVisible(true);
		
		p2.add(chat, BorderLayout.CENTER);
		p2.add(new JPanel(), BorderLayout.WEST);
		p2.add(new JPanel(), BorderLayout.EAST);
		
		p3.setLayout(new BorderLayout());
		p3.add(new JPanel(), BorderLayout.NORTH);
		p3.add(input, BorderLayout.CENTER);
		p3.add(sent, BorderLayout.EAST);
		
		this.add(p1, BorderLayout.NORTH);
		this.add(p2, BorderLayout.CENTER);
		this.add(p3, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	public void setClient(String ip, String userName) {
		this.ip.setText(ip);
		this.userName.setText(userName);
	}
	public void setFrame() {
//		this.setTitle("客户端");
//		this.setLayout(new BorderLayout());
//		JPanel p1 = new JPanel();
//		JPanel p2 = new JPanel();
//		JPanel p3 = new JPanel();
//		
//		p1.setLayout(new BorderLayout());
//		p1.add(ip, BorderLayout.WEST);
//		p1.add(userName, BorderLayout.EAST);
//		p1.add(new JPanel(), BorderLayout.SOUTH);
//		
////		p2.setLayout(new BorderLayout());
////		JScrollPane text2=new JScrollPane(chat);
////		p2.add(text2);
////		text2.setVisible(true);
//		
//		p2.add(chat, BorderLayout.CENTER);
//		p2.add(new JPanel(), BorderLayout.WEST);
//		p2.add(new JPanel(), BorderLayout.EAST);
//		
//		p3.setLayout(new BorderLayout());
//		p3.add(new JPanel(), BorderLayout.NORTH);
//		p3.add(input, BorderLayout.CENTER);
//		p3.add(sent, BorderLayout.EAST);
//		
//		this.add(p1, BorderLayout.NORTH);
//		this.add(p2, BorderLayout.CENTER);
//		this.add(p3, BorderLayout.SOUTH);
//		
//		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void close() {
		System.out.println("客户端结束");
		System.exit(0);
		System.out.println("客户端结束");
	}
	
	public JTextArea getinput() {
		return input;
	}
	public TextArea getchat() {
		return chat;
	}
	public JButton getButton() {
		return sent;
	}
	
	public static void main(String[] args)
	{
		client_win cw = new client_win();
		cw.setClient("192.168.1.1", "Coooder");
		cw.setFrame();
	}
}

