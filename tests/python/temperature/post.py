import httplib
import urllib
import random
import time


def main():
    headers = {'Content-type': 'application/x-www-form-urlencoded', 'Accept': 'text/plain'}

    for _ in range(100):
        temperature = random.uniform(3.5, 18.5)
        humidity = random.uniform(70, 95)
        parameters = urllib.urlencode({'temperature': temperature, 'humidity': humidity})

        connection = httplib.HTTPConnection('127.0.0.1:8080')
        connection.request('POST', '/environment/add', parameters, headers)
        connection.close()
        time.sleep(1)


if __name__ == '__main__':
    main()
