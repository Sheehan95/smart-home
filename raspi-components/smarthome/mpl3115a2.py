# from ctypes import *

# sensor = CDLL("./sensor.so")
sensor = None


class MPL:
    def __init__(self):
        if sensor.bcm2835_init() == 0:
            print 'Failed'
            return

    def writeRegister(self, register, value):
        sensor.MPL3115A2_WRITE_REGISTER(register, value)

    def readRegister(self, register):
        return sensor.MPL3115A2_READ_REGISTER(register)

    def active(self):
        sensor.MPL3115A2_Active()

    def standby(self):
        sensor.MPL3115A2_Standby()

    def initAlt(self):
        sensor.MPL3115A2_Init_Alt()

    def initBar(self):
        sensor.MPL3115A2_Init_Bar()

    def readAlt(self):
        return sensor.MPL3115A2_Read_Alt()

    def readTemp(self):
        return sensor.MPL3115A2_Read_Temp()

    def setOSR(self, osr):
        sensor.MPL3115A2_SetOSR(osr)

    def setStepTime(self, step):
        sensor.MPL3115A2_SetStepTime(step)

    def getTemp(self):
        t = self.readTemp()
        t_m = (t >> 8) & 0xff
        t_l = t & 0xff

        if t_l > 99:
            t_l /= 1000.0
        else:
            t_l /= 100.0

        return t_l + t_m

    def getAlt(self):
        alt = self.readAlt()
        alt_m = alt >> 8
        alt_l = alt & 0xff

        if alt_l > 99:
            alt_l /= 1000.0
        else:
            alt_l /= 100.0

        return self.twosToInt(alt_m, 16) + alt_l

    def getBar(self):
        alt = self.readAlt()
        alt_l = alt & 0x03
        alt_m = alt >> 6

        return self.twosToInt(alt_m, 18)

    def twosToInt(self, val, len):
        if val & (1 << len - 1):
            val -= 1 << len

        return val
