from datetime import datetime, timedelta
import date_converter
import pytz
import random
import requests


def main():
    end = datetime(2016, 5, 31, 0, 0, 0, 0, pytz.utc)
    start = datetime(2016, 1, 1, 0, 0, 0, 0, pytz.utc)

    while start < end:
        start += timedelta(hours=1)

        temperature = random.uniform(3.5, 18.5)
        humidity = random.uniform(70, 95)
        timestamp = date_converter.date_to_timestamp(start)

        payload = {
            'temperature': temperature,
            'humidity': humidity,
            'timestamp': timestamp
        }

        request = requests.post('http://127.0.0.1:8080/environment/add', json=payload)
        print request.status_code


if __name__ == '__main__':
    main()
