package Chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;

import Chat.client.sendTarId;

/**
 * Created by lzx on 2019/12/22.
 * ���̷߳�����,ʵ�ֶ�ͻ�������
 */
public class sertest {
	int[][] pairs = new int[30][30];
    List<receiveCall> receiveList = new ArrayList<>();//��������ӿͻ�����
    private final static int MESSAGE_SIZE = 1024;//ÿ������������ݵ���󳤶�
    int num = 0;//�ͻ��˱��
    boolean broadcast = false;//true;//false;
    HashMap<String, String> userInfo = new HashMap<>();
    

    public static void main(String[] args) {
    	System.out.println("����������");
        new sertest();
    }

    //����˴����߼�
    sertest() {
    	for(int i = 0; i < 30; ++i) {
    		for(int j = 0; j < 30; ++j)
    			pairs[i][j] = -1;
    	}
    	userInfo.put("coder", "111");
    	userInfo.put("moz", "11");
    	userInfo.put("neal", "22");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4000);//�����������׽��֣�ָ���˿ں�
            while (true) {
                Socket socket = serverSocket.accept();//�����ͻ������ӣ������߳�
                receiveCall receiveThread = new receiveCall(socket, num);
                receiveThread.start();
                receiveList.add(receiveThread);
                for(int i = 0; i < receiveList.size(); ++i) {//receiveCall thread : receiveList
                	receiveCall thread = receiveList.get(i);
                	for(int j = 0; j <= num; ++j) {
                		if(j != thread.num) {
                			System.out.println(thread.num + " connect_in "+j);
                			new SendThread(thread.socket, "connect_in "+ j + "\n").start();	//	�������˷������ߵ�����id
                		}
                	}
                }	//	���пͻ����磬��Ⱥ���������ӵ�����Ը��¿ͻ��˵��б�
                System.out.println("�����Ͽͻ��ˣ�" + num);
                //�пͻ��������ߣ���������֪ͨ�����ͻ���
                String notice="���¿ͻ������ߣ��������߿ͻ����У��ͻ���:";
                for (receiveCall thread : receiveList) {
                    notice = notice + "" + thread.num;
                }
                System.out.println(notice);
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //������Ϣ���̣߳�ͬʱҲ�м�¼��Ӧ�ͻ���socket�����ã�
    class receiveCall extends Thread {
        int num;
        Socket socket;//�ͻ��˶�Ӧ���׽���
        boolean go_on = true;//��ʶ�Ƿ�ά��������Ҫ����

        public receiveCall(Socket socket, int num) {
            this.socket = socket;
            this.num = num;
            try {
                //�������ϵĿͻ��˷��ͣ�����Ŀͻ��˱�ŵ�֪ͨ
            	for(int j = 0; j <= num; ++j) {
            		if(j != num) {
            			System.out.println(num + " connect_in "+j);
//            			new SendThread(this.socket, "connect_in "+ j + "\n").start();	//	�������˷������ߵ�����id
            			this.socket.getOutputStream().write(("connect_in "+ j + "\n").getBytes());
            		}
            	}
                socket.getOutputStream().write(("��Ŀͻ��˱���� " + num + "\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
         @Override
        public void run() {
            super.run();
            //���տͻ��˷��͵���Ϣ
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                byte[] b;
                while (go_on) {
                    b = new byte[MESSAGE_SIZE];
                    inputStream.read(b);
                    
                    b = splitByte(b);//ȥ���������ò���
                    
                    //����end�Ŀͻ��˶Ͽ�����
                    if (new String(b).equals("end")) {
                        go_on = false;
                        receiveList.remove(this);
                        //֪ͨ�����ͻ���
                        String message = "�ͻ���" + num + "�Ͽ�\n" +
                                "�������ߵ��У��ͻ��ˣ�";
                    } else {
                        try {
                        	String ck = new String(b);	//	���������Է��������Ƿ�������
                            if(ck.length() == 0 || ck == null)
                            	ck = "";
                            System.out.println("cccccccccchhhhhheecccckkkkkkk" + num + " " + ck);
                            if(ck.length() == 0 || ck == null)
                            	ck = "";
                            System.out.println("cccccccccchhhhhheecccckkkkkkk" + num + " " + ck);
                            int checkres = checkuser(ck);
                            if(checkres == 0) {
                            	PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
                            	socketOut.println("wrong");
                            	socketOut.flush();
                            }
                            else if(checkres == 1){
                            	PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
                            	socketOut.println("ok");
                            	socketOut.flush();
                            	System.out.println("��¼�ɹ�");
                            	socket.getOutputStream().write(("��Ŀͻ��˱���� " + num + "\n").getBytes());
                            }
                            int res = check(ck, num);///////////////////////////���������Է��������Ƿ�������
                            if(res == -2) {
                            	System.out.println("�����˹㲥");
                            }
                            else if(res != -1) {
                            	System.out.println("������Ի�������ҵ����շ������ͽ�������֪ͨ\n");
                            	for (receiveCall receiveThread : receiveList) {	//	��������Ի�������ҵ����շ������ͽ�������֪ͨ
                            		if(receiveThread.num == res) {
                            			opencommand(receiveThread, num);
                            			break;
                            		}
                                }
                            }
                            else {
                            	System.out.print("���������\n" );
	                            String[] data = new String(b).split(" ", 2);//������Ϣͷ�����Ե�һ���ո񣬽��ַ����ֳ���������
	                            int clientNum = Integer.parseInt(data[0]);//ת��Ϊ���֣����ͻ��˱������
	                            //����Ϣ���͸�ָ���ͻ���
	                            for (receiveCall receiveThread : receiveList) {
	                            	if(broadcast == true) {	//	���ڹ㲥ģʽ
	                            		new SendThread(receiveThread.socket, "someone_want_to_chat_with " + receiveThread.num).start();
	                            		if(receiveThread.num != num) {	//	������Լ�����Ϣ
	                                    	new SendThread(receiveThread.socket, "�ͻ���"+num+"����Ϣ��"+data[1]).start();
	                            		}
	                            	}	//	˽��ģʽ
	                            	else if (receiveThread.num == clientNum) {
                                    	new SendThread(receiveThread.socket, "�ͻ���"+num+"����Ϣ��"+data[1]).start();
	                                }
	                            }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {//�ر���Դ
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	/* �����û���������Ϣ������ͷ�������ǵ�½��Ϣ���ж��Ƿ���ע����Ϣƥ�� */
    public int checkuser(String s) {	
    	String[] command = new String(s).split(" ");
    	if(command.length < 3) {
    		return -1;
    	}
    	if(command[0].indexOf("user_log_in_") != -1) {
    		if(command[2].indexOf(userInfo.get(command[1])) != -1) {
    			System.out.println(command[2] + " ������ȷ");
    			return 1;
    		}
    		else {
    			System.out.println("�������");
    			return 0;
    		}
    	}
    	return -1;
    }
    
    public int check(/*receiveCall c,*/ String s, int from) {///////////////////////////////////////////////////////////
    	System.out.println(s);
    	if(s.indexOf("dont_want_broadcast") != -1) {
			System.out.println(s);
			broadcast = false;
			return -2;
		}
		else if(s.indexOf("want_broadcast") != -1) {
			System.out.println(s);
			broadcast = true;
			return -2;
		}
    	String[] command = new String(s).split(" ");
    	if(command.length < 1) {
    		return -1;
    	}
    	System.out.println("�Ƿ������" + (command[0].indexOf(new String("i_want_to_chat_with")) != -1));
        System.out.println(command[0]);
        System.out.println(s);
        command[0] = command[0].trim();
        int to = toInt(command[1]);
    	if(command[0].indexOf(new String("i_want_to_chat_with")) != -1) {	//	�������º��ѵ�¼���������밴ť�Լ�����
    		System.out.println("add pair");
    		if(pairs[from][to] == -1) {
    			pairs[from][to] = pairs[to][from] = 1;
    			return to;
    		}
    	}
    	return -1;
    }
    
    private int toInt(String s) {
    	int res = 0;
    	s = s.trim();
    	for(int i = 0; i < s.length(); ++i) {
    		System.out.print(res + " ");
    		res = res * 10 + (int)(s.charAt(i) - '0');
    	}
    	return res;
    }
    
    public void opencommand(receiveCall receiveThread, int id) throws IOException {
    	//	��ʱnum��ʾ����ķ���id
		String tongzhi = new String("someone_want_to_chat_with " + id);
		new SendThread(receiveThread.socket, tongzhi).start();
    }

    //������Ϣ���߳�
    class SendThread extends Thread {
        Socket socket;
        String str;
        public SendThread(Socket socket, String str) {
            this.socket = socket;
            this.str = str;
        }
        @Override
        public void run() {
            super.run();
            try {
            	//���������
            	PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
            	socketOut.println(str);
            	socketOut.flush();	//��ʱˢ�������������������������
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //ȥ��byte������ಿ��
   private   byte[] splitByte(byte b[]) {
        int i = 0;
        for(;i<b.length;i++) {
            if (b[i] == 0) {
                break;
            }
        }
        byte[] b2 = new byte[i];
        for (int j = 0; j <i ; j++) {
            b2[j] = b[j];
        }
        return b2;
    }

}
class Pair{
	public 	int[] pair = new int[2];
	public Pair(int a, int b){
		pair[0] = a;
		pair[1] = b;
	}
	public int getother(int x) {
		return pair[0] == x? pair[1] : pair[0];
	}
}


