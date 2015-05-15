package zangyakui.pc2moblieSnapScreenSever.org;


import org.apache.commons.lang3.ArrayUtils;



/*
 * This class is aim to help socket communication easier.
 * usage:
 * 1. call write() to write data (byte[]) into MessageStream;
 * 2. call read(MsgFrame msgFrame), return true if a MsgFrame founded,and 
 * parameter msgFrame will be valued by the founded MsgFrame. otherwise false 
 * will be returned and msgFrame=null;
 * Notice:
 * 1.socket server and client agrees on a custom protocol, which formatted
 * as below:
 * |int CTL||int length||byte[] data|
 * CTL:   	control command;
 * length:  the length of byteArray data;
 * data:    the actual useful data.	
 */

public class MessageStream {
	
	private final int CAPACITY=10*1024;
	
	private byte[] mBuffer;
	private int mPosition;
	
	
	public MessageStream(){
		mBuffer=null;
		mPosition=0;
	}
	
	
	
	
	public int  getStreamLength(){
		
		if(mBuffer!=null){
			return mBuffer.length;
		}
		return  0;
	}
	
	public boolean write(byte[] b,int off, int len){
	
		boolean bufferAvaliable = ensureCapacity(b.length);
		if(bufferAvaliable){
			
			mBuffer=ArrayUtils.addAll(mBuffer,ArrayUtils.subarray(b, off, len+off));
			
			return true;
		}
		return false;
	}
	
	public boolean write(byte[] b,int off){
		
		return write(b,off,b.length-off);
	}
	
	public boolean write(byte[] b){
		
		return write(b,0,b.length);
	}
	
	
	public MsgFrame read(){
		
		System.out.println("MessageSream.read()===> Reading Message");
		
		if(mBuffer!=null){
			//at least contains ctl and length.
			if(mBuffer.length<(4+4)){
				System.out.println("MessageSream.read()===>mBuffer length <8 ,real length is "+ mBuffer.length);
				return null;
			}	
			int ctl=readInt();
			int length=readInt();
			
			System.out.println("DATA INFO: ctl = "+ctl+" length= "+length);
			byte [] data=readData(length);
			
			if(data!=null && data.length==length){
				
				MsgFrame msgFrame=MsgFrameBuilder.getInstance().build(ctl, data);
				System.out.println("msgFrame = "+msgFrame);
				clearReadMsgFrame();
				return msgFrame;
				
			}else{
				restoreMessage();
				return null;
			}
			
		}else{
			System.out.println("MessageSream.read()===>mBuffer is null ");
		}
		return null;
	}
	
	
	private void clearReadMsgFrame() {
		mBuffer=ArrayUtils.subarray(mBuffer, mPosition, mBuffer.length);
		mPosition=0;
	}

	private void restoreMessage() {
		//it seems not to happen.
	}

	private byte[] readData(int len){
		
		if(mBuffer.length-mPosition<len){
			return null;
		}
		
		byte[] tmpData=ArrayUtils.subarray(mBuffer, mPosition, mPosition+len);
		
		if(tmpData!=null && tmpData.length!=len){
			return null;
		}
		mPosition+=len;
		return tmpData;
	}
	
	/*
	 * if the sum of byte number in mBuffer less than 4, then return oxffffffff; 
	 */
	private int readInt(){
		
		if(mBuffer.length-mPosition<4)
			return 0xffffffff;
		
		
		int value=0;
		for(int i=0;i<4;i++){
			value = value+(readByte()<<(8*i));
		}
		
		return value;
		
	}
	
	private byte readByte(){

		if(mPosition==mBuffer.length)
			throw new IndexOutOfBoundsException();
		
		return mBuffer[mPosition++];
	}

	public  boolean ensureCapacity(int len){
		
		if(getCapacity()-mPosition>len) return true;
		
		return false;
	}
	
	

	private int getCapacity() {

		return CAPACITY;
	}




	public void close() {
		// TODO Auto-generated method stub
		
	}
}
