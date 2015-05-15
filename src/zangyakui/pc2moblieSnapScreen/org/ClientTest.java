package zangyakui.pc2moblieSnapScreen.org;

public class ClientTest {

	public static void main(String[] args) throws Exception{
		TcpSocketClient client = new TcpSocketClient();
		
		if(client.connectToServer(10)){
			client.doTransactions();
		}
	}
}
