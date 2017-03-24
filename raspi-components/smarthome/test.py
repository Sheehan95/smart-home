from datetime import datetime
import threading
import time


def run_every_minute():
    print '=======>', datetime.now()
    threading.Timer(60, run_every_minute).start()


delay = 60 - datetime.now().second
print delay
print datetime.now()
threading.Timer(delay, run_every_minute).start()

while True:
    print 'Still running here'
    print datetime.now()
    time.sleep(2)
