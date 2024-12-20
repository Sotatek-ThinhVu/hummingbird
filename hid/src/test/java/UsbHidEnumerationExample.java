/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2020 Gary Rowe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

import com.sparrowwallet.APDUCommand;
import com.sparrowwallet.APDUResponse;
import com.sparrowwallet.HidLedgerDevice;
import com.sparrowwallet.LedgerDevice;
import org.hid4java.*;

import java.nio.charset.StandardCharsets;

/**
 * Demonstrate the USB HID interface using a Satoshi Labs Trezor
 *
 * @since 0.0.1
 */
public class UsbHidEnumerationExample extends BaseExample {

  private static final int CLA = 0xE0;
  static final int RESULT_OK = 0x9000;

  public static void main(String[] args) throws HidException {

    UsbHidEnumerationExample example = new UsbHidEnumerationExample();
    example.executeExample();
  }

  private void executeExample() throws HidException {

    HidServicesSpecification hidSpec = new HidServicesSpecification();
    hidSpec.setAutoStart(false);

    // Scan for devices
    HidServices hidServices = HidManager.getHidServices(hidSpec);
    hidServices.start();

    for (HidDevice hidDevice : hidServices.getAttachedHidDevices()) {
      System.out.println(hidDevice);
      if (LedgerDevice.isLedgerDevice(hidDevice.getVendorId(), hidDevice.getProductId())) {
        handleLedgerDevice(hidDevice);
      }
    }

//    hidServices.stop();

    waitAndShutdown(hidServices);
  }

  private static void handleLedgerDevice(HidDevice hidDevice) {
    hidDevice.open();

    System.out.println(hidDevice.isClosed());
    HidLedgerDevice device = new HidLedgerDevice(hidDevice);
    APDUCommand command = new APDUCommand(0xB0, (byte) 0x3C, 0x00, 0x00, true);
    APDUResponse response = device.exchange(command);

    System.out.println(response.getSW1() == RESULT_OK ? "OK" : "ERROR");
    System.out.println(new String(response.getData(), StandardCharsets.UTF_8));

      // TODO
    device.close();
  }

}
