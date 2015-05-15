package zangyakui.pc2moblieSnapScreenSever.org;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MsgFrame{
	private int mCtl;
	private int mLength;
	private byte[] mData;//mData maybe null
	
	public int getCtlType(){
		
		return mCtl;
	}
	
	private void setLength(int length) {
		// TODO Auto-generated method stub
		mLength=length;
	}
	
	
	public MsgFrame(){
		this(MsgFrameBuilder.CTL_DATA,null);
	}
	
	public MsgFrame(int ctl){
		this(ctl,null);
	}
	
	public MsgFrame(byte[] data){
		this(MsgFrameBuilder.CTL_DATA,data);
	}
	
	public  MsgFrame(int ctl,byte[] data) {
		this.mCtl=ctl;
		this.mData=data;
		
		
		if(mData!=null){
			setLength(mData.length);
		}else{
			setLength(0);
		}
		
	}
	
	/*
	 * create a byte[] combined the mCtl,mLength,mDate of this MsgFrame
	 */
	public byte[] getFrameBytes(){
		
		int totalLength=8+mLength;
//		ByteArrayOutputStream writer=new ByteArrayOutputStream(totalLength);
//		writer.write(mCtl);
//		writer.write(mLength);
//		if(mData!=null){
//			writer.write(mData,8,mLength);
//		}
//		byte[] frameBytes=writer.toByteArray();
//		System.out.println("buildFrameBytes length"+frameBytes.length);
//		try {
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		byte[] frameBytes=new byte[totalLength];
		for(int i =0;i<4;i++){
			frameBytes[i]=(byte) (mCtl>>(8*i)&0xff);
		}
		for(int i =4;i<8;i++){
			frameBytes[i]=(byte) (mLength>>(8*(i-4))&0xff);
		}
		for(int i =8;i<totalLength;i++){
			frameBytes[i]=mData[i-8];
		}
		return frameBytes;
	}
	
	public int getLength(){
		return mLength;
	}
	
	public byte[] getData(){
		return mData;
	}
}