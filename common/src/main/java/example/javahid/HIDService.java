package example.javahid;

public interface HIDService {
	Object[] getDeviceInfo();
	String read(HIDDeviceReference deviceReference);
	Integer write(HIDDeviceReference deviceReference, String data);
}
