package com.maozy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	public class Service implements Runnable {

		private BufferedReader in = null;
		private String msg = "";
		private Socket socket;

		public Service(Socket socket) {
			this.socket = socket;
			try {
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				msg = "用户：" + this.socket.getInetAddress() + "~加入了聊天室，当前在线人数："
						+ mList.size();
				this.sendmsg();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			while (true) {
				try {
					if ((msg = in.readLine()) != null) {
						if (msg.equals("bye")) {
							System.out.println("~~~~~~~");
							mList.remove(socket);
							in.close();
							msg = "用户：" + socket.getInetAddress()
									+ "退出,当前在线人数：" + mList.size();
							socket.close();
							this.sendmsg();
							break;
						} else {
							msg = socket.getInetAddress() + "说：" + msg;
							this.sendmsg();
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/**
		 * 为连接上服务端的每个客户端发送信息
		 */
		private void sendmsg() {
			System.out.println(msg);
			int num = mList.size();
			for (int index = 0; index < num; index++) {
				Socket mSocket = mList.get(index);
				PrintWriter pout = null;
				try {
					pout = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(mSocket.getOutputStream())));
					pout.println(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	// 定义相关参数、端口、存储Socket连接的集合，ServerSocket对象以及线程池
	private static final int PORT = 12345;
	private List<Socket> mList = new ArrayList<Socket>();
	private ExecutorService myExecutorService = null;
	private ServerSocket server = null;
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		new Server();

	}

	public Server() {
		try {
			server = new ServerSocket(PORT);
			// 创建线程池
			myExecutorService = Executors.newCachedThreadPool();
			System.out.println("服务端运行中~~~\n");

			Socket client = null;

			while (true) {
				client = server.accept();
				mList.add(client);
				myExecutorService.execute(new Service(client));

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
