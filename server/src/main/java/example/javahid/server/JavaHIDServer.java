package example.javahid.server;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

import example.javahid.HIDService;

public class JavaHIDServer {
	public void run(String[] args) throws ParseException, IOException, XmlRpcException {
		Options options = createOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption("help")) {
				printHelpScreen(options);
			} else {
				Number port = (Number) line.getParsedOptionValue("port");
				startServer(port);
			}
		} catch (ParseException e) {
			System.err.println("Parsing failed. Reason: " + e.getMessage());
			throw e;
		}
	}

	private void startServer(Number port) throws IOException, XmlRpcException {
		WebServer webServer = null;
		try {
			System.out.println("Attempting to start XML-RPC Server...");
			// ClassPathLibraryLoader.loadNativeHIDLibrary();
			webServer = new WebServer(port.intValue());
			XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
			PropertyHandlerMapping phm = new PropertyHandlerMapping();
			phm.setVoidMethodEnabled(true);
			phm.addHandler(HIDService.class.getName(), HIDServiceImpl.class);
			xmlRpcServer.setHandlerMapping(phm);
			webServer.start();
			System.out.println("Started successfully.");
			System.out.println("Accepting requests. (Halt program to stop.)");
		} catch (IOException e) {
			if (webServer != null) {
				webServer.shutdown();
			}
			throw e;
		} catch (XmlRpcException e) {
			if (webServer != null) {
				webServer.shutdown();
			}
			throw e;
		}
	}

	@SuppressWarnings("static-access")
	private Options createOptions() {
		OptionGroup og = new OptionGroup();
		og.setRequired(true);
		/* @formatter:off */
		og.addOption(OptionBuilder
				.withArgName("PORT")
				.withDescription("Specify port number")
				.withLongOpt("port")
				.hasArg()
				.withType(Number.class)
				.create("p"));
		og.addOption(OptionBuilder
				.withLongOpt("help")
				.withDescription("Show help screen")
				.create("h"));
		/* @formatter:on */
		Options options = new Options();
		options.addOptionGroup(og);
		return options;
	}

	private void printHelpScreen(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar JavaHIDServer.jar", options);
	}
}
