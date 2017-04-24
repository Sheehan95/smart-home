import struct
import threading


class WeighingScale:
    def __init__(self):
        self.current_weight = 0
        self.last_weight = 0
        self.capacity = 0
        self.product = 'Default'
        self.thread = threading.Thread(target=self.update_weight).start()
        self.on_lift = None
        self.on_down = None
        self.on_change = None

    def update_weight(self):
        values = list()

        for _ in range(9):
            values.append(WeighingScale.read_scale())

        self.last_weight = self.current_weight
        self.current_weight = WeighingScale.mode(values)

        if self.last_weight > 0 and self.current_weight == 0:
            if self.on_lift is not None:
                self.on_lift()
        elif self.last_weight == 0 and self.current_weight > 0:
            if self.on_down is not None:
                self.on_down()
        elif self.last_weight < self.current_weight:
            if self.on_change is not None:
                self.on_change()

        self.thread = threading.Thread(target=self.update_weight).start()

    def calibrate(self, product):
        self.capacity = self.current_weight
        self.product = product

    @staticmethod
    def read_scale(dev="/dev/usb/hiddev0"):
        try:
            with open(dev, 'rb') as f:
                # Read 4 unsigned integers from USB device
                fmt = "IIII"
                bytes_to_read = struct.calcsize(fmt)
                usb_binary_read = struct.unpack(fmt, f.read(bytes_to_read))

                return usb_binary_read[3]
        except IOError:
            return -1
        except OSError:
            return -1

    @staticmethod
    def mode(numbers):
        return max(set(numbers), key=numbers.count)
