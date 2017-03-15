import json
import paho.mqtt.client as mqtt
import requests


ENVIRONMENT_TOPIC = '/ie/sheehan/smart-home/envreading/log'
FORWARD_TEMPERATURE = '/ie/sheehan/smart-home/envreading/forward'


def on_connect(client, userdata, flags, rc):
    print 'Client connected with status code ', rc


def on_message(client, userdata, message):

    if message.topic == ENVIRONMENT_TOPIC:
        payload = json.loads(message.payload)
        request = requests.post('http://192.167.1.31:8080/environment/add', json=payload)
        print request.status_code

    print 'Client: ', client
    print 'Topic: ', message.topic
    print 'Payload:', message.payload


def main():
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect('127.0.0.1', 1883, 60)
    client.subscribe('#')
    client.loop_forever()


if __name__ == '__main__':
    main()
