import json
import paho.mqtt.client as mqtt
import random
import time

TOPIC = '/ie/sheehan/smart-home/temperature/log'


def on_connect(client, userdata, flags, rc):
    print 'Client connected with status code ', rc


def on_message(client, userdata, message):
    print 'Client: ', client
    print 'Topic: ', message.topic
    print 'Payload: ', message.payload


def main():
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect('192.167.1.23', 1883, 60)

    while True:
        temperature = random.uniform(3.5, 18.5)
        humidity = random.uniform(70, 95)
        payload = json.dumps({'temperature': temperature, 'humidity': humidity})
        client.publish(TOPIC, payload)
        time.sleep(5)


if __name__ == '__main__':
    main()
