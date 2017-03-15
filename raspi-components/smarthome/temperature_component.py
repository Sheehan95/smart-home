import json
import random

import paho.mqtt.client as mqtt


TOPIC = '/ie/sheehan/smart-home/envreading/log'
TOPIC_ENVIRONMENT_READING_REQUESTS = '/ie/sheehan/smart-home/envreading/request'
TOPIC_ENVIRONMENT_READING_RESPONSE = '/ie/sheehan/smart-home/envreading/response'


client = mqtt.Client()


def on_connect(connected, userdata, flags, rc):
    print 'Client connected with status code ', rc


def on_message(connected, userdata, message):
    print 'Client: ', connected
    print 'Topic: ', message.topic
    print 'Payload: ', message.payload

    temperature = random.uniform(3.5, 18.5)
    humidity = random.uniform(70, 95)
    log_temperature(temperature, humidity)


def log_temperature(temperature, humidity):
    payload = json.dumps({'temperature': temperature, 'humidity': humidity})
    client.publish(TOPIC_ENVIRONMENT_READING_RESPONSE, payload)


def main():
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect('192.167.1.23', 1883, 60)

    client.subscribe(TOPIC_ENVIRONMENT_READING_REQUESTS)
    client.loop_forever()

#    while True:
#        temperature = random.uniform(3.5, 18.5)
#        humidity = random.uniform(70, 95)
#        payload = json.dumps({'temperature': temperature, 'humidity': humidity})
#        client.publish(TOPIC, payload)
#        time.sleep(5)


if __name__ == '__main__':
    main()
