package zangyakui.pc2moblieSnapScreenSever.org;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MsgFrameBuilder {
	
	public static final int CTL_DATA = 0x0001; 
	public static final int CTL_SHUTDOWN_INSTREAM = 0x0002; 
	public static final int CTL_SHUTDOWN_OUTSTREAM = 0x0003; 
	public static final int CTL_SHOT = 0x0004; 
	public static final int CTL_STOPSERVER = 0x0005; 
//	public static final String  DELIMITER="n!@^ht";
	public  static MsgFrameBuilder mInstance;

	public  static MsgFrameBuilder getInstance(){
		if(mInstance==null)
			mInstance= new MsgFrameBuilder();
		return mInstance;
	}
	
	public  MsgFrame build(int ctl,byte[] data){
		 return new MsgFrame(ctl, data);
	}
	
	public MsgFrame build(){
		return new MsgFrame();
	}
	
	public MsgFrame build(int ctl){
		return new MsgFrame(ctl);
	}
	
	public MsgFrame build(byte[] data){
		return new MsgFrame(data);
	}
	
	public MsgFrame buildFromBytes(byte[] data){
		ByteArrayInputStream reader=new ByteArrayInputStream(data);
		
		int ctl=reader.read();//get Ctl command
		int length=reader.read();// get data length
		
		byte[] tmpData=new byte[length];
		reader.read(tmpData, 0, length);//get the actual data
				
//		for(int i=0;i<4;i++){
//			ctl += (int)data[i]<<(8*i);
//		}
//		for(int i=4;i<8;i++){
//			length += (int)data[i]<<(8*(i-4));
//		}
		
		return build(ctl, tmpData);
	}
}
