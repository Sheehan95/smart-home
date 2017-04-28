import datetime


class Heating:
    def __init__(self):
        self.on = False
        self.last_on = None
        self.last_duration = 0

    def turn_on(self):
        if not self.on:
            print '[ENV]: actually turning it on now boiiiii'
            self.on = True
            self.last_on = datetime.datetime.now()

    def turn_off(self):
        if self.on:
            self.on = False
            self.last_duration = (datetime.datetime.now() - self.last_on).total_seconds()
