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
    static String client = "�ң�";
    static String server = "Server:";
    static Socket socket = null;
    connect_in connect;	//���Ӷ���
    client_win cw;	//	���촰�ڶ���
    String myid = null, myname = null;	//	�����ڷ������е�id�ź��û���
    userLogin log = null;	//	��¼����
    
    public client() throws IOException, ClassNotFoundException {
		/* �����������Ӷ��󣬼���㲥���ܵİ������� */
    	connect = new connect_in();
    	connect.getbc().addMouseListener(new broadcast());
    	connect.getnbc().addMouseListener(new nobroadcast());
		/* ���ñ��ͻ��˶˿ںţ������׽��ֶ��� */
    	int port = 4000;
    	socket = new Socket("192.168.2.113"/*192.168.0.31*/, port);
    	updateList();	//һ�����Ϸ���������ͨ���ú�����÷������������û�����Ϣ���Խ����Լ��ĺ����б�
        System.out.println("�ͻ�������");
        
		/* ������¼���棬Ϊ��¼��ť���밴����������ʵ��������������û��ĵ�¼��Ϣ */
        log = new userLogin();
		log.login.addMouseListener(new getUserInfo(log.nametxt, log.pwdtxt));
		/*
		 * ���´����Ϊ��¼ʱ���͵�¼��Ϣ���������������Ϣ����ƥ�䣬 
		 * �������ɹ���¼�����������������ķ������������������תwrongPage������ʾ
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
        
		/* ����¼�ɹ������ֳ��û��б���棬����ʼ�����촰�ڣ����밴������ */
    	connect.setVisible(true);
    	connect.setTitle("�û��б�");
    	connect.showPanel();
        cw = new client_win();
        cw.setTitle(null);
        cw.getButton().addMouseListener(new sentText(cw.getinput(), cw.getchat()));
		/* chat()����ִ������ʱ���շ���Ϣ�߼� */
		chat();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        connect.showPanel();
    }
    
	/* ����ĺ���ʵ�� */
    public void chat() throws IOException {
		while(connect.isVisible()){
			/* �û����������׼��������Ϣ
			 * cwΪ���촰�ڣ�ѭ���в��������촰���ж�ȡ��Ϣ */
			reciString(cw.getinput(), cw.getchat());
		}
		cw.close();
		cw = null;
		if(cw == null) {
			PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
			socketOut.println("_end__ ");
			socketOut.flush();
			socket.close();
			System.out.println("�ͻ��˽���");
			System.exit(0);
		}
    }
    
    public static void main(String[] args) throws Exception {
        client client = new client();
    }
    
	/*	updateList()
	 * ���û��ոյ�¼�ɹ���������������ʱ�����յ������������������û���Ϣ��
	 * �ÿͻ���ͨ������Ϣ���º����б� ��ȡ���ߺ���
	 */
    public void updateList() throws IOException {
    	BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	while(true) {
	    	inTemp = socketIn.readLine();
	    	String[] command = new String(inTemp).split(" ", 2);//�Ե�һ���ո񣬽��ַ����ֳ���������,����Ϊconnect_in id
	        if(command[0].indexOf(new String("connect_in")) != -1) {	//	�������º��ѵ�¼����Ϣ������밴ť�Լ�����
	    		JButton newbt = new JButton(command[1]);
	    		int find = 0;
	    		for(JButton bt : connect.btList) {
	    			if(bt.getText().indexOf(command[1]) != -1) { //	����������еĺ���
	    				find = 1;
	    				break;
	    			}
	    		}
	    		if(find == 0) {
	    			connect.btList.add(newbt);
	    			connect.showPanel();
	        		newbt.addMouseListener(new sendTarId(command[1]));		//	�б�ѡ����ӷ�����������ļ���,����Է�id
	    		}
	    		connect.showPanel();	//ˢ�º����б�
	        }
	        else if(command[0].indexOf(new String("��Ŀͻ��˱����")) != -1) {
	        	myid = command[1];
	        	return;
	        }
	        else {
	        	break;
	        }
    	}
    }

	/*
	 * ������Ϣ�ĺ���ʵ�֣��������촰�ڵķ��Ͱ�ť����ʱ�����ã� 
	 * �����촰�ڵķ���������ַ������ͣ���������Ϣ��������Ϣ��¼��
	 */
    public void sentString(JTextArea t, TextArea c) throws IOException {
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
        readline = t.getText();
        if(readline == null || readline.length()==0)	//���ⷢ�Ϳհ���Ϣ
        	return;
		String tmp = c.getText() + "\n" + client + readline;
		c.setText(tmp);
		t.setText("");
		System.out.println("��" + cw.oppID + "���ͣ�" + readline);
		socketOut.println(cw.oppID + " " + readline);	//	�����͵�Ŀ��ID�����ڴ˷��ͣ����촰�ڲ��ɼ�
        socketOut.flush();    //ˢ�������ʹserver�յ���Ϣ
	}

	/*
	 * ������Ϣ�ĺ���ʵ�֣����socket�������յ�����Ϣ��
	 * ���Ƿ�����������ָ������з����� ���������û���������Ϣ���������Ϣ��¼
	 */
    public void reciString(JTextArea t, TextArea c) throws IOException {
        BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
    	inTemp = socketIn.readLine();
    	String[] command = new String(inTemp).split(" ", 2);//�Ե�һ���ո񣬽��ַ����ֳ���������,����Ϊconnect_in id
        if(command[0].indexOf(new String("��Ŀͻ��˱����")) != -1) {	//	��ȡ�����������ID
        	myid = command[1];
        	return;
        }
        else if(command[0].indexOf(new String("connect_in")) != -1) {	//	�������º��ѵ�¼���������밴ť�Լ�����
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
        		newbt.addMouseListener(new sendTarId(command[1]));		//	�б�ѡ����ӷ�����������ļ���,����Է�id
    		}
    		connect.showPanel();
    		return ;
    	}
    	else {
    		if(command[0].indexOf(new String("someone_want_to_chat_with")) != -1) {//����������û������Ի�����
    			System.out.println("������Ի��ͽ�������");
    			cw.setClient("���ID�ǣ�" + myid, "�Է�ID�ǣ�"+command[1]);	//	��ȡ�Է�ID���������촰����Ϣ
    			cw.setFrame();
    			cw.setTitle(myname);
    			cw.oppID = command[1];
    			return;
    		}
    		else {
    			System.out.println("���յ��Է���" + cw.oppID + "������Ϣ��" + inTemp);
    		}
    	}
    	String tmp = c.getText();
    	tmp = tmp + "\n" + inTemp;
    	c.setText(tmp);	//	���������¼
    }

	/* ���б��е�������û�ʱ���ã�������������Լ���Ҫ���ӵ�ID */
    public void choseID(String id) throws IOException {
    	PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
        String command = "i_want_to_chat_with " + new Integer(id).toString();
        cw.oppID = id;
        socketOut.println(command);
        System.out.println("send tar id " + command);
        socketOut.flush();  
    }
    class sendTarId implements MouseListener{	//	����û�����������¼�����
    	String tarID;
    	public sendTarId(String id) throws IOException {	//	���������������ͨ�ŵĿͻ���ID
    		tarID = id;
    	}
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				choseID(tarID);
				cw.setClient("���ID�ǣ�" + myid, "�Է�ID�ǣ�"+tarID);
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
     public int checkin() throws IOException {		// �жϵ�½
    	 String res = new String("");
		BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		res = socketIn.readLine();
		if(res != null && res.indexOf("ok") != -1) {
			System.out.println("������ȷ");
			return 1;
		}
		else {
			return 0;
		}
     }

	/* ����û���½ʱ���û���Ϣ */
     class getUserInfo  implements MouseListener{	//	�ӱ��ͻ��˵ĵ�½�����ȡ�û���Ϣ
    	 JTextArea username;
    	 JPasswordField pwd;
    	 public getUserInfo(JTextArea n, JPasswordField p){
    		 username = n;
    		 pwd = p;
    		 System.out.println("user���湹�캯��" + username.getText() + " " + pwd.getText());
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
		System.out.println("����㲥");
		socketOut.println("want_broadcast");
		socketOut.flush();
     }

	/* �û����������������㲥 */
     class broadcast implements MouseListener{
		broadcast(){
			System.out.println("broadcast����");
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			PrintWriter socketOut;
			System.out.println(connect.getbc().getMouseListeners().toString());
			System.out.println("����㲥");
			try {
				socketOut = new PrintWriter(socket.getOutputStream());
				System.out.println("����㲥");
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
     /* �û������������ȡ������㲥 */
     class nobroadcast implements MouseListener{
		nobroadcast(){
			System.out.println("nobroadcast����");
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			PrintWriter socketOut;
			System.out.println("ȡ���㲥");
			try {
				socketOut = new PrintWriter(socket.getOutputStream());
				System.out.println("ȡ���㲥");
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