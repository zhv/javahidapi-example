package example.javahid.server;

import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.apache.xmlrpc.XmlRpcException;

public class JavaHIDServerMain {
	public static void main(String[] args) throws ParseException, IOException, XmlRpcException {
		new JavaHIDServer().run(args);
	}
}
