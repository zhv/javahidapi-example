package example.javahid.client;

import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

import example.javahid.HIDDeviceReference;
import example.javahid.HIDService;

public class JavaHIDClient {
	public void run(String[] args) throws ParseException {
		Options options = createOptions();
		CommandLine line = parseArguments(options, args);
		if (line.hasOption("help")) {
			printHelpScreen(options);
		} else {
			URL serverUrl = (URL) line.getParsedOptionValue("url");
			HIDService service = createService(serverUrl);
			if (line.hasOption("device-info")) {
				for (String s : service.getDeviceInfo()) {
					System.out.println(s);
				}
			} else if (line.hasOption("read")) {
				int vendorId = ((Number) line.getParsedOptionValue("vendor-id")).intValue();
				int productId = ((Number) line.getParsedOptionValue("product-id")).intValue();
				String serialNumber = line.getOptionValue("serial");
				HIDDeviceReference ref = new HIDDeviceReference(vendorId, productId, serialNumber);
				String result = service.read(ref);
				System.out.println(result);
			} else {
				printHelpScreen(options);
			}
		}
	}

	@SuppressWarnings("static-access")
	private Options createOptions() {
		OptionGroup og = new OptionGroup();
		og.setRequired(true);
		/* @formatter:off */
		og.addOption(OptionBuilder
				.withLongOpt("help")
				.withDescription("Show help screen")
				.create("h"));
		og.addOption(OptionBuilder
				.withLongOpt("device-info")
				.withDescription("Get device info")
				.create("i"));
		og.addOption(OptionBuilder
				.withLongOpt("read")
				.withDescription("Read data from device")
				.create("r"));
		Options options = new Options();
		options.addOptionGroup(og);
		options.addOption(OptionBuilder
				.withArgName("URL")
				.withDescription("Specify server url")
				.withLongOpt("url")
				.hasArg()
				.withType(URL.class)
				.create());
		options.addOption(OptionBuilder
				.withArgName("VENDOR_ID")
				.withDescription("Specify device vendor id")
				.withLongOpt("vendor-id")
				.hasArg()
				.withType(Number.class)
				.create());
		options.addOption(OptionBuilder
				.withArgName("PRODUCT_ID")
				.withDescription("Specify device product id")
				.withLongOpt("product-id")
				.hasArg()
				.withType(Number.class)
				.create());
		options.addOption(OptionBuilder
				.withArgName("SERIAL_NUMBER")
				.withDescription("Specify device serial number")
				.withLongOpt("serial")
				.hasArg()
				.create());
		/* @formatter:on */
		return options;
	}

	private CommandLine parseArguments(Options options, String[] args) throws ParseException {
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse(options, args);
			if ((line.hasOption("device-info") && !line.hasOption("url"))
					|| (line.hasOption("read") && (!line.hasOption("url") || !line.hasOption("vendor-id")
							|| !line.hasOption("product-id") || !line.hasOption("serial")))) {
				System.err.println("Required arguments missed");
				printHelpScreen(options);
			}
			return line;
		} catch (ParseException e) {
			System.err.println("Arguments parsing failed. Reason: " + e.getMessage());
			throw e;
		}
	}

	private void printHelpScreen(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar JavaHIDClient.jar", options);
	}

	private HIDService createService(URL serverUrl) {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(serverUrl);
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		ClientFactory factory = new ClientFactory(client);
		HIDService service = (HIDService) factory.newInstance(HIDService.class);
		return service;
	}
}
