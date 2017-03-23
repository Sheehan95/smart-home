import json
import random

import paho.mqtt.client as mqtt
import time

from Sensors import TemperatureSensor


TOPIC_ENVIRONMENT_READING_REQUESTS = '/ie/sheehan/smart-home/envreading/request'
TOPIC_ENVIRONMENT_READING_RESPONSE = '/ie/sheehan/smart-home/envreading/response'


client = mqtt.Client()
sensor = TemperatureSensor()


def on_connect(connected, userdata, flags, rc):
    print 'Client connected with status code ', rc


def on_message(connected, userdata, message):
    print 'Client: ', connected
    print 'Topic: ', message.topic
    print 'Payload: ', message.payload

    if message.topic is TOPIC_ENVIRONMENT_READING_REQUESTS:
        log_temperature(sensor.get_temp(), random.uniform(70, 95))


def log_temperature(temperature, humidity):
    timestamp = int(time.time() * 1000)

    payload = json.dumps({'temperature': temperature, 'humidity': humidity, 'timestamp': timestamp})
    client.publish(TOPIC_ENVIRONMENT_READING_RESPONSE, payload)


def main():
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect('192.167.1.23', 1883, 60)

    client.subscribe(TOPIC_ENVIRONMENT_READING_REQUESTS)
    client.loop_forever()


if __name__ == '__main__':
    main()
