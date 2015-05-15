package zangyakui.pc2moblieSnapScreen.org;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import zangyakui.pc2moblieSnapScreenSever.org.MessageStream;
import zangyakui.pc2moblieSnapScreenSever.org.MsgFrame;
import zangyakui.pc2moblieSnapScreenSever.org.MsgFrameBuilder;
import zangyakui.pc2moblieSnapScreenSever.org.MsgStreamHandler;


/*
 * Create an instance of TcpSocketClient for Client application.
 *
 * usage:
 * 1. new TcpSocketCilent(String Address ,int Port);
 * 	  parameters may be null but default Address,port will be 
 *    used to initialize Client socket; 
 *    
 * 2. connectToServer(int retryTimes);
 * 
 * 3. doTransactions();
 */
public class TcpSocketClient {
	
	
	private final int BUFFER_SIZE=8*1024; 
	
	private Socket mSocket;
	private String mHost;
	private OutputStream oStream;
	private DataInputStream iStream;
	private MessageStream mMsgStream;
	private int mPort;
	private MsgStreamHandler mMsgHandler;
	
	public TcpSocketClient(){
		
		this(TcpSocketConstant.SERVER_ADDRESS,TcpSocketConstant.PORT);
		
	}
	public TcpSocketClient(String host,int port){
		
		mHost=host;
		mPort= port;

	}
	
	public boolean connectToServer(int retryTimes){
		
		while(retryTimes>0){
			retryTimes--;
			try {
				mSocket= new Socket(mHost,mPort);
			} catch (IOException e) {
//				e.printStackTrace();
				mSocket=null;
				System.out.println("fail to connect to server, try again. "+retryTimes+"left.");
			}
			if(mSocket!=null){
				System.out.println("connect successfully");
				return true;
			}
		}
		return false;
	}
	
	public void  doTransactions(){
		System.out.println("Client ===> doTransactions");
		
		//client send greeting data first.
		sayHello();
		
		
		try {
			
			iStream=new DataInputStream(mSocket.getInputStream());
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.out.println("Client cannot get InputStream of socket ");
			return;
		}
		
		if(iStream!=null){
			int off=0;
			int len=0;
			int bufferSize=BUFFER_SIZE;
			mMsgStream=new MessageStream();
			
			byte[] buffer=new byte[bufferSize];
			
			try {
				
				while((len=iStream.read(buffer, off, bufferSize))!=-1){
					
					mMsgStream.write(buffer,off,len);
					
					off = off + len<bufferSize?len:0;
					bufferSize = bufferSize-len;
					
					if(bufferSize==0){
						
						bufferSize=BUFFER_SIZE;
						off=0;
					}
					handleMsg();
				}
				
			} catch (IOException e) {
				
				System.out.println("Client cannot read InputStream,it may have gone. ");
				e.printStackTrace();
				
			}	
			
			try {
				
				oStream.close();
				iStream.close();
				mSocket.close();
				mMsgStream.close();
				mMsgHandler=null;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
	}
	
	/*
	 * say Hello to server means that it's time to start working.
	 */
	private void sayHello() {
		System.out.println("Client sayHello first ");
		if(mSocket!=null && mSocket.isConnected() && oStream==null){
			try {
				
				oStream=mSocket.getOutputStream();
				MsgFrame msg =MsgFrameBuilder.getInstance().build();
				oStream.write(msg.getFrameBytes());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
	}
	private void handleMsg() {
		if(mMsgHandler==null){
			mMsgHandler=new MsgStreamHandler(mMsgStream){

				@Override
				public void handleMessage(MsgFrame msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					
					System.out.println("ServerThread===> handleMsg");
					if(mSocket!=null && mSocket.isClosed() && oStream==null){
						try {
							oStream=mSocket.getOutputStream();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
					}
					switch(msg.getCtlType()){
					
						case MsgFrameBuilder.CTL_DATA:
							
							System.out.println("msgFrame ctl type : CTL_DATA");
							String msgString =new String (msg.getData());
							System.out.println(msgString);
//							terminate();
							break;
							
						case MsgFrameBuilder.CTL_SHOT:
							System.out.println("msgFrame ctl type : CTL_SHOT");
							break;
						case MsgFrameBuilder.CTL_SHUTDOWN_INSTREAM:
							break;
						case MsgFrameBuilder.CTL_SHUTDOWN_OUTSTREAM:
							break;
						case MsgFrameBuilder.CTL_STOPSERVER:
							break;
							
						default:
							break;
					}
				
				}
			};
			mMsgHandler.start();
		}
	}

	
}
