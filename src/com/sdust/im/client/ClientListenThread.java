package com.sdust.im.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

import com.sdust.im.DataBase.UserDao;
import com.sdust.im.bean.TranObject;
import com.sdust.im.bean.TranObjectType;
import com.sdust.im.bean.User;

/**
 * 服务器对客户端的监听监听
 * 
 */
public class ClientListenThread implements Runnable {
	private ClientActivity client;
	private ObjectInputStream read;
	private boolean isRunning;

	public ClientListenThread(ObjectInputStream read, ClientActivity client) {
		this.read = read;
		this.client = client;
		isRunning = true;
	}

	@Override
	public void run() {
		SocketAddress s = client.getmClient().getRemoteSocketAddress();
		while (isRunning) {
			readMsg();
		}
	}

	private void readMsg() {
		SocketAddress s = client.getmClient().getRemoteSocketAddress();
		System.out.println(s.toString());
		
		try{   
			client.getmClient().sendUrgentData(0xFF);   
		}catch(Exception ex){   
			ex.printStackTrace();
		     System.out.println("duankai");  
		}
		
		try {
			TranObject tran = (TranObject) read.readObject();
			TranObjectType type = tran.getTranType();
			System.out.println(type.toString());
			switch (type) {
			case REGISTER_ACCOUNT:
				String account = (String) tran.getObject();
				System.out.println(account);
				client.checkAccount(account);
				break;
			case REGISTER:
				client.regist(tran);
				break;
			case LOGIN:
				client.login(tran);
				break;
			case SEARCH_FRIEND:
				client.searchFriend(tran);
				break;
			case FRIEND_REQUEST:
				client.friendRequset(tran);
				break;
			case MESSAGE:
				client.sendMessage(tran);
				break;
			case SEARCH_FRIEND_SIMILARITY:
				client.searchFriendBySimilarity(tran);
				break;
			default:
				break;
			}
		}catch(SocketException e) {
			client.close();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("EXCEPTION!!!");
		}
	}
	public void close() {
		isRunning = false;
	}

}
