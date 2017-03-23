from mpl3115a2 import MPL


class TemperatureSensor:

    def __init__(self):
        self.temperature = 0
        self.sensor = MPL()
        self.sensor.initAlt()
        self.sensor.active()

    def get_temp(self):
        self.temperature = self.sensor.getTemp()
        return self.temperature
