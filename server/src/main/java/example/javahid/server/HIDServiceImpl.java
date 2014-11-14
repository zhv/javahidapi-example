package example.javahid.server;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceInfo;
import com.codeminders.hidapi.HIDDeviceNotFoundException;
import com.codeminders.hidapi.HIDManager;

import example.javahid.HIDDeviceReference;
import example.javahid.HIDService;

public class HIDServiceImpl implements HIDService {
	@Override
	public String[] getDeviceInfo() {
		HIDManager manager = createManager();
		try {
			HIDDeviceInfo[] devices = manager.listDevices();
			String[] result = new String[devices.length];
			for (int i = 0; i < devices.length; i++) {
				result[i] = devices[i].toString();
			}
			return result;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public String read(HIDDeviceReference deviceReference) {
		HIDManager manager = createManager();
		HIDDevice device = openDevice(manager, deviceReference);
		try {
			byte[] buffer = new byte[1024];
			int readCount = device.read(buffer);
			return new String(buffer, 0, readCount);
		} catch (IOException e) {
			closeDeviceQuietly(device);
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Integer write(HIDDeviceReference deviceReference, String data) {
		HIDManager manager = createManager();
		HIDDevice device = openDevice(manager, deviceReference);
		try {
			int result = device.write(data.getBytes("UTF-8"));
			return Integer.valueOf(result);
		} catch (IOException e) {
			closeDeviceQuietly(device);
			throw new IllegalStateException(e);
		}
	}

	private HIDManager createManager() {
		HIDManager manager = null;
		try {
			manager = HIDManager.getInstance();
			return manager;
		} catch (Exception e) {
			if (manager != null) {
				manager.release();
			}
			throw new IllegalStateException(e);
		}
	}

	private HIDDevice openDevice(HIDManager manager, HIDDeviceReference deviceReference) {
		HIDDevice device = null;
		try {
			device = manager.openById(deviceReference.getVendorId(), deviceReference.getProductId(),
					deviceReference.getSerialNumber());
			return device;
		} catch (HIDDeviceNotFoundException e) {
			closeDeviceQuietly(device);
			throw new IllegalStateException(e);
		} catch (IOException e) {
			closeDeviceQuietly(device);
			throw new IllegalStateException(e);
		}
	}

	private void closeDeviceQuietly(HIDDevice device) {
		if (device != null) {
			try {
				device.close();
			} catch (IOException e) {
				Logger.getLogger(getClass()).debug("Unexpected error while closing resource", e);
			}
		}
	}
}
