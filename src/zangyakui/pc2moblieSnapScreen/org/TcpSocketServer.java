package zangyakui.pc2moblieSnapScreen.org;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class TcpSocketServer {
	public static final String SERVER_ADDRESS ="192.168.15.128";
	public static final int PORT = 9123;
	
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
	
		mServerSocket= new ServerSocket(PORT);
	}
	
	class SocketThread extends Thread{
		Socket mSocket;
		DataOutputStream oStream;
		InputStream iStream;
		
		public SocketThread(Socket socket){
			mSocket=socket;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			System.out.println("SocketThread is running");
			try {
				oStream=new DataOutputStream(mSocket.getOutputStream());
				iStream=mSocket.getInputStream();
				
				File f=new File("e:/tosent.png");
				DataInputStream dis=new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
				
				int bufferSize=8192;
				byte[] buffer=new byte[bufferSize];
				int len=0;
				if(f!=null){
					while((len=dis.read(buffer, 0, bufferSize))!=-1){
						oStream.write(buffer,0,len);
//						oStream.flush();
					}	
				}
				
//				int len=0;
//				int index=0;
//				StringBuffer sb=new StringBuffer();
//				byte[] b=new byte[1024]; 
//				while((len=iStream.read(b))!=-1){
//					
//					System.out.println("byteLenght = "+len);
//					String tmp= new String(b,0,len);
//					if((index=tmp.indexOf("eof"))!=-1){
//						sb.append(tmp,0,index);
//						break;
//					}
//					sb.append(tmp,0,len);
//				};
//				
//				oStream.write(sb.toString().getBytes());
				oStream.write("eof".getBytes());
				oStream.flush();
				dis.close();
				oStream.close();
				iStream.close();
				mSocket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
		
		
	}
}
