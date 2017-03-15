import json
import paho.mqtt.client as mqtt

import httplib
import urllib


ENVIRONMENT_TOPIC = '/ie/sheehan/smart-home/envreading/log'
FORWARD_TEMPERATURE = '/ie/sheehan/smart-home/envreading/forward'


def on_connect(client, userdata, flags, rc):
    print 'Client connected with status code ', rc


def on_message(client, userdata, message):

    if message.topic == ENVIRONMENT_TOPIC:
        payload = json.loads(message.payload)
        headers = {'Content-type': 'application/x-www-form-urlencoded', 'Accept': 'text/plain'}
        conn = httplib.HTTPConnection('192.167.1.31:8080')
        params = urllib.urlencode(payload)
        conn.request('POST', '/environment/add', params, headers)
        response = conn.getresponse()
        conn.close()
        print 'HTTP Status Code:', response.status, response.reason

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
