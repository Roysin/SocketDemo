package zangyakui.pc2moblieSnapScreenSever.org;

public class ServerTest {

	public static void main(String[] args) throws Exception{
		
		TcpSocketServer server=new TcpSocketServer();
		server.turnOn();
	}
}
