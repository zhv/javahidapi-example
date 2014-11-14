package example.javahid;

public interface HIDService {
	String[] getDeviceInfo();
	String read(HIDDeviceReference deviceReference);
	Integer write(HIDDeviceReference deviceReference, String data);
}
