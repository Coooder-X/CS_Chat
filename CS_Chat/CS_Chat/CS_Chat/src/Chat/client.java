package Chat;

import java.awt.Window.Type;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.*;
import java.text.Normalizer.Form;
import javax.swing.*;

import UI.client_win;
import UI.connect_in;
import UI.userLogin;
import UI.wrongPage;

import java.awt.*;

public class client extends JFrame implements Serializable{
	static String readline = null;
    static String inTemp = null;
    static String client = "我：";
    static String server = "Server:";
    static Socket socket = null;
    connect_in connect;	//连接对象
    client_win cw;	//	聊天窗口对象
    String myid = null, myname = null;	//	本用在服务器中的id号和用户名
    userLogin log = null;	//	登录界面
    
    public client() throws IOException, ClassNotFoundException {
		/* 创建聊天连接对象，加入广播功能的按键监听 */
    	connect = new connect_in();
    	connect.getbc().addMouseListener(new broadcast());
    	connect.getnbc().addMouseListener(new nobroadcast());
		/* 设置本客户端端口号，建立套接字对象 */
    	int port = 4000;
    	socket = new Socket("192.168.2.113"/*192.168.0.31*/, port);
    	updateList();	//一旦连上服务器，就通过该函数获得服务器中其他用户的信息，以建立自己的好友列表
        System.out.println("客户端启动");
        
		/* 创建登录界面，为登录按钮加入按键监听，以实现向服务器发送用户的登录信息 */
        log = new userLogin();
		log.login.addMouseListener(new getUserInfo(log.nametxt, log.pwdtxt));
		/*
		 * 以下代码段为登录时发送登录信息到服务器与个人信息进行匹配， 
		 * 并给出成功登录或密码错误重新输入的反馈，若密码错误则跳转wrongPage进行提示
		*/
		int f = 0, first = 0;
		wrongPage wp = null;
		while(true) {
			if(f == 1) {
				log.close();
				break;
			}
			f = checkin();
			if(f == 0) {
				if(first == 0) {
					first = 1;
					continue;
				}else if(first == 1 && wp == null)
					wp = new wrongPage();
				else if(wp.isVisible() == false)
					wp.setVisible(true);
			}
		}
        
		/* 若登录成功则显现出用户列表界面，并初始化聊天窗口，加入按键监听 */
    	connect.setVisible(true);
    	connect.setTitle("用户列表");
    	connect.showPanel();
        cw = new client_win();
        cw.setTitle(null);
        cw.getButton().addMouseListener(new sentText(cw.getinput(), cw.getchat()));
		/* chat()函数执行聊天时的收发消息逻辑 */
		chat();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        connect.showPanel();
    }
    
	/* 聊天的函数实现 */
    public void chat() throws IOException {
		while(connect.isVisible()){
			/* 用户界面存在则准备接受消息
			 * cw为聊天窗口，循环中不断在聊天窗口中读取信息 */
			reciString(cw.getinput(), cw.getchat());
		}
		cw.close();
		cw = null;
		if(cw == null) {
			PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
			socketOut.println("_end__ ");
			socketOut.flush();
			socket.close();
			System.out.println("客户端结束");
			System.exit(0);
		}
    }
    
    public static void main(String[] args) throws Exception {
        client client = new client();
    }
    
	/*	updateList()
	 * 在用户刚刚登录成功，连接上聊天室时，将收到服务器发来的在线用户信息，
	 * 该客户端通过该信息更新好友列表， 获取在线好友
	 */
    public void updateList() throws IOException {
    	BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	while(true) {
	    	inTemp = socketIn.readLine();
	    	String[] command = new String(inTemp).split(" ", 2);//以第一个空格，将字符串分成两个部分,命令为connect_in id
	        if(command[0].indexOf(new String("connect_in")) != -1) {	//	若是有新好友登录的消息，则加入按钮以及监听
	    		JButton newbt = new JButton(command[1]);
	    		int find = 0;
	    		for(JButton bt : connect.btList) {
	    			if(bt.getText().indexOf(command[1]) != -1) { //	避免加入已有的好友
	    				find = 1;
	    				break;
	    			}
	    		}
	    		if(find == 0) {
	    			connect.btList.add(newbt);
	    			connect.showPanel();
	        		newbt.addMouseListener(new sendTarId(command[1]));		//	列表选项添加发送连接请求的监听,传入对方id
	    		}
	    		connect.showPanel();	//刷新好友列表
	        }
	        else if(command[0].indexOf(new String("你的客户端编号是")) != -1) {
	        	myid = command[1];
	        	return;
	        }
	        else {
	        	break;
	        }
    	}
    }

	/*
	 * 发送消息的函数实现，将在聊天窗口的发送按钮按下时被调用， 
	 * 从聊天窗口的发送区获得字符串发送，并将该消息更新至消息记录中
	 */
    public void sentString(JTextArea t, TextArea c) throws IOException {
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
        readline = t.getText();
        if(readline == null || readline.length()==0)	//避免发送空白消息
        	return;
		String tmp = c.getText() + "\n" + client + readline;
		c.setText(tmp);
		t.setText("");
		System.out.println("向" + cw.oppID + "发送：" + readline);
		socketOut.println(cw.oppID + " " + readline);	//	将发送的目的ID隐藏在此发送，聊天窗口不可见
        socketOut.flush();    //刷新输出流使server收到消息
	}

	/*
	 * 接受消息的函数实现，获得socket读入流收到的消息，
	 * 若是服务器发出的指令则进行分析， 若是其他用户的聊天消息，则加入消息记录
	 */
    public void reciString(JTextArea t, TextArea c) throws IOException {
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
    	inTemp = socketIn.readLine();
    	String[] command = new String(inTemp).split(" ", 2);//以第一个空格，将字符串分成两个部分,命令为connect_in id
        if(command[0].indexOf(new String("你的客户端编号是")) != -1) {	//	获取服务器分配的ID
        	myid = command[1];
        	return;
        }
        else if(command[0].indexOf(new String("connect_in")) != -1) {	//	若是有新好友登录的命令，则加入按钮以及监听
    		JButton newbt = new JButton(command[1]);
    		int find = 0;
    		for(JButton bt : connect.btList) {
    			if(bt.getText().indexOf(command[1]) != -1) { 
    				find = 1;
    				break;
    			}
    		}
    		if(find == 0) {
    			connect.btList.add(newbt);
    			connect.showPanel();
        		newbt.addMouseListener(new sendTarId(command[1]));		//	列表选项添加发送连接请求的监听,传入对方id
    		}
    		connect.showPanel();
    		return ;
    	}
    	else {
    		if(command[0].indexOf(new String("someone_want_to_chat_with")) != -1) {//如果有其他用户发来对话请求
    			System.out.println("被请求对话和建立窗口");
    			cw.setClient("你的ID是：" + myid, "对方ID是："+command[1]);	//	获取对方ID，更新聊天窗口信息
    			cw.setFrame();
    			cw.setTitle(myname);
    			cw.oppID = command[1];
    			return;
    		}
    		else {
    			System.out.println("我收到对方发" + cw.oppID + "来的消息：" + inTemp);
    		}
    	}
    	String tmp = c.getText();
    	tmp = tmp + "\n" + inTemp;
    	c.setText(tmp);	//	更新聊天记录
    }

	/* 在列表中点击在线用户时调用，向服务器发送自己想要连接的ID */
    public void choseID(String id) throws IOException {
    	PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
        String command = "i_want_to_chat_with " + new Integer(id).toString();
        cw.oppID = id;
        socketOut.println(command);
        System.out.println("send tar id " + command);
        socketOut.flush();  
    }
    class sendTarId implements MouseListener{	//	点击用户发起聊天的事件监听
    	String tarID;
    	public sendTarId(String id) throws IOException {	//	向服务器发送你想通信的客户的ID
    		tarID = id;
    	}
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				choseID(tarID);
				cw.setClient("你的ID是：" + myid, "对方ID是："+tarID);
				cw.setFrame();
				cw.setTitle(myname);
				cw.getButton().addMouseListener(new sentText(cw.getinput(), cw.getchat()));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
    }
    
     public class sentText  implements MouseListener  {
    	String text = null;
    	JTextArea t;
    	TextArea c;
    	public sentText(JTextArea T, TextArea C) {
    		t = T;
    		c = C;
    	}
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				sentString(t, c);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
    }
     public int checkin() throws IOException {		// 判断登陆
    	 String res = new String("");
		BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		res = socketIn.readLine();
		if(res != null && res.indexOf("ok") != -1) {
			System.out.println("密码正确");
			return 1;
		}
		else {
			return 0;
		}
     }

	/* 获得用户登陆时的用户信息 */
     class getUserInfo  implements MouseListener{	//	从本客户端的登陆界面获取用户信息
    	 JTextArea username;
    	 JPasswordField pwd;
    	 public getUserInfo(JTextArea n, JPasswordField p){
    		 username = n;
    		 pwd = p;
    		 System.out.println("user界面构造函数" + username.getText() + " " + pwd.getText());
    	 }
		@Override
		public void mouseClicked(MouseEvent e) {
			PrintWriter socketOut;
			try {
				socketOut = new PrintWriter(socket.getOutputStream());
				myname = username.getText();
				System.out.println("user_log_in_ " + username.getText() + " " + pwd.getText());
				socketOut.println("user_log_in_ " + username.getText() + " " + pwd.getText());
				socketOut.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
     }
     public void bdtst() throws IOException {
    	 PrintWriter socketOut; socketOut = new PrintWriter(socket.getOutputStream());
		System.out.println("请求广播");
		socketOut.println("want_broadcast");
		socketOut.flush();
     }

	/* 用户向服务器发送请求广播 */
     class broadcast implements MouseListener{
		broadcast(){
			System.out.println("broadcast构造");
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			PrintWriter socketOut;
			System.out.println(connect.getbc().getMouseListeners().toString());
			System.out.println("请求广播");
			try {
				socketOut = new PrintWriter(socket.getOutputStream());
				System.out.println("请求广播");
				socketOut.println("want_broadcast");
				socketOut.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
     }
     /* 用户向服务器发送取消请求广播 */
     class nobroadcast implements MouseListener{
		nobroadcast(){
			System.out.println("nobroadcast构造");
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			PrintWriter socketOut;
			System.out.println("取消广播");
			try {
				socketOut = new PrintWriter(socket.getOutputStream());
				System.out.println("取消广播");
				socketOut.println("dont_want_broadcast");
				socketOut.flush();
			} 
			catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
     }
}