package zangyakui.pc2moblieSnapScreenSever.org;


/*
 * MsgHandler : handle MessageFrame;
 * 
 */
public class MsgStreamHandler extends Thread{
	
	private MsgFrame msgFrame;
	private MessageStream msgStream;
	private boolean bSwitch=true;
	
	public MsgStreamHandler(MessageStream mMsgStream){
		msgStream=mMsgStream;
	}

	@Override
	public synchronized void start() {
		super.start();
	}
	
	public void terminate(){
		bSwitch=false;
		onTerminated();
	}
	
	private void onTerminated() {
		msgStream=null;
		msgFrame=null;
	}

	@Override
	public void run() {
		super.run();
		
		while(bSwitch){
			if( (msgFrame=msgStream.read()) != null){
				System.out.println("msgFrame="+msgFrame);
				handleMessage(msgFrame);
			}
		}
		
		
	}
	
	public void handleMessage(MsgFrame msg){
//		must be override
	}
}
	
