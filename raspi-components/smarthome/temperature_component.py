import json
import paho.mqtt.client as mqtt

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
    payload = json.dumps({'temperature': 12, 'humidity': 100})
    client.publish(TOPIC, payload)


if __name__ == '__main__':
    main()
