package UI;

import java.io.*;
import java.net.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

import Chat.client;

import java.awt.*;

public class connect_in extends JFrame{
	private client client1, client2;
	JButton broadcast = new JButton("���ù㲥");
	JButton nobroadcast = new JButton("ȡ���㲥");
	public List<JButton> btList = new ArrayList<>();
	JPanel panel = new JPanel();
	public connect_in() {
		this.setTitle("�б����");
		this.setBounds(((Toolkit.getDefaultToolkit().getScreenSize().width)/3)-150, ((Toolkit.getDefaultToolkit().getScreenSize().height)/3)-150,300,600);
		this.setLayout(new BorderLayout());
		
		JPanel p1 = new JPanel();
//		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		
		p1.setLayout(new BorderLayout());
		JTextArea username = new JTextArea("�����б�");
		username.setEditable(false);
		username.setFont(new Font("����",Font.BOLD,25));
		p1.add(username, BorderLayout.CENTER);
		this.add(p1, BorderLayout.NORTH);
		
		panel.setLayout(new GridLayout(10, 1, 30, 13));
		this.add(panel, BorderLayout.CENTER);
		p3.setLayout(new BorderLayout());
//		JPanel p4 = new JPanel();
//		p4.setLayout(new GridLayout(1, 2, 20, 13));
//		JButton broadcast = new JButton("���ù㲥");
		broadcast.setFont(new Font("����",Font.BOLD,20));
//		JButton nobroadcast = new JButton("ȡ���㲥");
		nobroadcast.setFont(new Font("����",Font.BOLD,20));
//		p4.add(broadcast, BorderLayout.WEST);
//		p4.add(nobroadcast, BorderLayout.EAST);
//		p3.add(p4, BorderLayout.CENTER);
		p3.add(broadcast, BorderLayout.WEST);
		p3.add(nobroadcast, BorderLayout.EAST);
		this.add(p3, BorderLayout.SOUTH);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		this.setVisible(true);
	}
	
	public void showPanel() {
		panel.removeAll();
		for(JButton bt : btList) {
			panel.add(bt);
		}
		panel.repaint();
//		panel.add(Account.setAccount());
		panel.revalidate();
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		connect_in ci = new connect_in();
		for(int i = 0; i < 4; ++i) {
			JButton jb = new JButton(new Integer(i).toString());
			ci.btList.add(jb);
		}
		ci.showPanel();
		int k;
		Scanner cin = new Scanner(System.in);
		k = cin.nextInt();
		if(k == 3) {
			ci.btList.remove(2);
			ci.showPanel();
		}
	}
	public JButton getbc() {
		return broadcast;
	}
	public JButton getnbc() {
		return nobroadcast;
	}
}
