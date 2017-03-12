import json
import paho.mqtt.client as mqtt
import random
import time

TOPIC = '/ie/sheehan/smart-home/temperature/log'
REQUEST_TEMPERATURE = '/ie/sheehan/smart-home/temperature/request'


client = mqtt.Client()


def on_connect(c, userdata, flags, rc):
    print 'Client connected with status code ', rc


def on_message(c, userdata, message):
    print 'Client: ', c
    print 'Topic: ', message.topic
    print 'Payload: ', message.payload
    log_temperature(666, 666)


def log_temperature(temperature, humidity):
    payload = json.dumps({'temperature': temperature, 'humidity': humidity})
    client.publish("/ie/sheehan/smart-home/temperature/getit", payload)


def main():
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect('192.167.1.23', 1883, 60)

    client.subscribe(REQUEST_TEMPERATURE)
    client.loop_forever()

#    while True:
#        temperature = random.uniform(3.5, 18.5)
#        humidity = random.uniform(70, 95)
#        payload = json.dumps({'temperature': temperature, 'humidity': humidity})
#        client.publish(TOPIC, payload)
#        time.sleep(5)


if __name__ == '__main__':
    main()
