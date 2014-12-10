package example.javahid;

import java.io.Serializable;

public class HIDDeviceReference implements Serializable {
	private static final long serialVersionUID = -2208885320944139851L;

	private int vendorId;
	private int productId;
	private String serialNumber;

	public HIDDeviceReference(int vendorId, int deviceId, String serialNumber) {
		this.vendorId = vendorId;
		this.productId = deviceId;
		this.serialNumber = serialNumber;
	}

	public int getVendorId() {
		return vendorId;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = "null".equals(serialNumber) ? null : serialNumber;
	}

	@Override
	public String toString() {
		return "" + vendorId + ":" + productId + "#" + ("null".equals(serialNumber) ? "<null>" : serialNumber);
	}
}
