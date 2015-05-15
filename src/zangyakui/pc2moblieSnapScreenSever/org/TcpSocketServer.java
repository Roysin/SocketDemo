package zangyakui.pc2moblieSnapScreenSever.org;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/* 
 * server 负责发送
 */
public class TcpSocketServer {
	
	
	private final int BUFFER_SIZE =8*1024;
	
	ServerSocket mServerSocket;
	private boolean bServerSwitch;
	
	public void  turnOn() throws IOException{
		bServerSwitch=true;
		while(bServerSwitch){
			System.out.println("Server:>>>>>>>>>>Waitting for Clients");
			Socket socket = mServerSocket.accept();
			new SocketThread(socket).start();
		}
	}
	
	public void trueOff(){
		
		bServerSwitch=false;
	}

	public TcpSocketServer () throws IOException{
	
		mServerSocket= new ServerSocket(TcpSocketConstant.PORT);
	}
	

	
	
	class SocketThread extends Thread{
		Socket mSocket;
		OutputStream oStream;
		InputStream iStream;
		MessageStream mMsgStream;
		MsgStreamHandler mMsgHandler;
		private boolean stopSending=false;
		
		public SocketThread(Socket socket){
			mSocket=socket;
		}
		
		public void handleTransaction(){
			
			System.out.println("ServerThread===> handleTransaction()");
			
			try {
				iStream=new BufferedInputStream(mSocket.getInputStream());
			} catch (IOException e1) {
				
				e1.printStackTrace();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				
				try {
					
					oStream.close();
					iStream.close();
					mSocket.close();
					mMsgStream.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			}
		}
		
		@Override
		public void run() {
			super.run();
			System.out.println("SocketThread is running");
			try {
				oStream=new DataOutputStream(mSocket.getOutputStream());
				iStream=mSocket.getInputStream();
				handleTransaction();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}

		private void handleMsg() {
			// TODO Auto-generated method stub
			if(mMsgHandler==null){
				mMsgHandler=new MsgStreamHandler(mMsgStream){

					@Override
					public void handleMessage(MsgFrame msg) {
						// TODO Auto-generated method stub
//						super.handleMessage(msg);
						
						System.out.println("ServerThread===> handleMsg msg="+msg);
						
						switch(msg.getCtlType()){
						
						case MsgFrameBuilder.CTL_DATA:
							while(!stopSending){
								if(mSocket!=null && !mSocket.isClosed() && oStream!=null){
									int ctl =MsgFrameBuilder.CTL_DATA;
									byte[] data= "Hello Baby here is Data".getBytes();
									MsgFrame msgFrame=MsgFrameBuilder.getInstance().build(ctl, data);
									try {
										oStream.write(msgFrame.getFrameBytes());
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							break;
						case MsgFrameBuilder.CTL_SHOT:
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
}
