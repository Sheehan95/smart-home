import datetime


class Alarm:
    def __init__(self):
        self.armed = False
        self.last_armed = None

    def arm_alarm(self):
        if not self.armed:
            print '[SEC]: actually arming it now boiiiii'
            self.armed = True
            self.last_armed = datetime.datetime.now()

    def disarm_alarm(self):
        if self.armed:
            self.armed = False
