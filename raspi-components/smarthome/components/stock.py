import struct
import threading


class WeighingScale:
    def __init__(self):
        self.current_weight = 0
        self.product = 'Default'
        self.thread = None

    def get_weight(self, dev="/dev/usb/hiddev0"):
        try:
            with open(dev, 'rb') as f:
                # Read 4 unsigned integers from USB device
                fmt = "IIII"
                bytes_to_read = struct.calcsize(fmt)
                usb_binary_read = struct.unpack(fmt, f.read(bytes_to_read))

                self.current_weight = usb_binary_read[3]
                return self.current_weight
        except OSError as e:
            print 'Error: ', e
            self.current_weight = -1
            return self.current_weight
