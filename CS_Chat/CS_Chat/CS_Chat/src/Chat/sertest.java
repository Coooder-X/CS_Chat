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
 * 多线程服务器,实现多客户端聊天
 */
public class sertest {
	int[][] pairs = new int[30][30];
    List<receiveCall> receiveList = new ArrayList<>();//存放已连接客户端类
    private final static int MESSAGE_SIZE = 1024;//每次允许接受数据的最大长度
    int num = 0;//客户端编号
    boolean broadcast = false;//true;//false;
    HashMap<String, String> userInfo = new HashMap<>();
    

    public static void main(String[] args) {
    	System.out.println("服务器启动");
        new sertest();
    }

    //服务端处理逻辑
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
            serverSocket = new ServerSocket(4000);//用来监听的套接字，指定端口号
            while (true) {
                Socket socket = serverSocket.accept();//监听客户端连接，阻塞线程
                receiveCall receiveThread = new receiveCall(socket, num);
                receiveThread.start();
                receiveList.add(receiveThread);
                for(int i = 0; i < receiveList.size(); ++i) {//receiveCall thread : receiveList
                	receiveCall thread = receiveList.get(i);
                	for(int j = 0; j <= num; ++j) {
                		if(j != thread.num) {
                			System.out.println(thread.num + " connect_in "+j);
                			new SendThread(thread.socket, "connect_in "+ j + "\n").start();	//	向所有人发送在线的所有id
                		}
                	}
                }	//	若有客户连如，则群发有新连接的命令，以更新客户端的列表
                System.out.println("连接上客户端：" + num);
                //有客户端新上线，服务器就通知其他客户端
                String notice="有新客户端上线，现在在线客户端有：客户端:";
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

    //接受消息的线程（同时也有记录对应客户端socket的作用）
    class receiveCall extends Thread {
        int num;
        Socket socket;//客户端对应的套接字
        boolean go_on = true;//标识是否还维持连接需要接收

        public receiveCall(Socket socket, int num) {
            this.socket = socket;
            this.num = num;
            try {
                //给连接上的客户端发送，分配的客户端编号的通知
            	for(int j = 0; j <= num; ++j) {
            		if(j != num) {
            			System.out.println(num + " connect_in "+j);
//            			new SendThread(this.socket, "connect_in "+ j + "\n").start();	//	向所有人发送在线的所有id
            			this.socket.getOutputStream().write(("connect_in "+ j + "\n").getBytes());
            		}
            	}
                socket.getOutputStream().write(("你的客户端编号是 " + num + "\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
         @Override
        public void run() {
            super.run();
            //接收客户端发送的消息
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                byte[] b;
                while (go_on) {
                    b = new byte[MESSAGE_SIZE];
                    inputStream.read(b);
                    
                    b = splitByte(b);//去掉数组无用部分
                    
                    //发送end的客户端断开连接
                    if (new String(b).equals("end")) {
                        go_on = false;
                        receiveList.remove(this);
                        //通知其他客户端
                        String message = "客户端" + num + "断开\n" +
                                "现在在线的有，客户端：";
                    } else {
                        try {
                        	String ck = new String(b);	//	服务器检查对方发来的是否是命令
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
                            	System.out.println("登录成功");
                            	socket.getOutputStream().write(("你的客户端编号是 " + num + "\n").getBytes());
                            }
                            int res = check(ck, num);///////////////////////////服务器检查对方发来的是否是命令
                            if(res == -2) {
                            	System.out.println("设置了广播");
                            }
                            else if(res != -1) {
                            	System.out.println("是请求对话命令，则找到接收方并发送建立窗口通知\n");
                            	for (receiveCall receiveThread : receiveList) {	//	若是请求对话命令，则找到接收方并发送建立窗口通知
                            		if(receiveThread.num == res) {
                            			opencommand(receiveThread, num);
                            			break;
                            		}
                                }
                            }
                            else {
                            	System.out.print("不是命令！！\n" );
	                            String[] data = new String(b).split(" ", 2);//解析消息头部，以第一个空格，将字符串分成两个部分
	                            int clientNum = Integer.parseInt(data[0]);//转换为数字，即客户端编号数字
	                            //将消息发送给指定客户端
	                            for (receiveCall receiveThread : receiveList) {
	                            	if(broadcast == true) {	//	处于广播模式
	                            		new SendThread(receiveThread.socket, "someone_want_to_chat_with " + receiveThread.num).start();
	                            		if(receiveThread.num != num) {	//	避免给自己发消息
	                                    	new SendThread(receiveThread.socket, "客户端"+num+"发消息："+data[1]).start();
	                            		}
	                            	}	//	私聊模式
	                            	else if (receiveThread.num == clientNum) {
                                    	new SendThread(receiveThread.socket, "客户端"+num+"发消息："+data[1]).start();
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
                try {//关闭资源
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

	/* 根据用户发来的信息，解析头部，若是登陆信息则判断是否与注册信息匹配 */
    public int checkuser(String s) {	
    	String[] command = new String(s).split(" ");
    	if(command.length < 3) {
    		return -1;
    	}
    	if(command[0].indexOf("user_log_in_") != -1) {
    		if(command[2].indexOf(userInfo.get(command[1])) != -1) {
    			System.out.println(command[2] + " 密码正确");
    			return 1;
    		}
    		else {
    			System.out.println("密码错误");
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
    	System.out.println("是否是命令：" + (command[0].indexOf(new String("i_want_to_chat_with")) != -1));
        System.out.println(command[0]);
        System.out.println(s);
        command[0] = command[0].trim();
        int to = toInt(command[1]);
    	if(command[0].indexOf(new String("i_want_to_chat_with")) != -1) {	//	若是有新好友登录的命令，则加入按钮以及监听
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
    	//	此时num表示请求的发起方id
		String tongzhi = new String("someone_want_to_chat_with " + id);
		new SendThread(receiveThread.socket, tongzhi).start();
    }

    //发送消息的线程
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
            	//创建输出流
            	PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
            	socketOut.println(str);
            	socketOut.flush();	//及时刷新输出缓冲区，立即发送数据
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //去除byte数组多余部分
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


