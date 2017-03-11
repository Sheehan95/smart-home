import json
import paho.mqtt.client as mqtt

import httplib
import urllib


TEMPERATURE_TOPIC = '/ie/sheehan/smart-home/temperature/log'


def on_connect(client, userdata, flags, rc):
    print 'Client connected with status code ', rc


def on_message(client, userdata, message):
    payload = json.loads(message.payload)
    headers = {'Content-type': 'application/x-www-form-urlencoded', 'Accept': 'text/plain'}

    if message.topic == TEMPERATURE_TOPIC:
        conn = httplib.HTTPConnection('192.167.1.31:8080')
        params = urllib.urlencode(payload)
        conn.request('POST', '/temperature/add', params, headers)
        response = conn.getresponse()
        conn.close()
        print 'HTTP Status Code:', response.status, response.reason

    print 'Client: ', client
    print 'Topic: ', message.topic
    print 'Payload:'

    for key in payload:
        print '\t', key, ':\t', payload[key]

    print '\n'


def main():
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect('127.0.0.1', 1883, 60)
    client.subscribe('#')
    client.loop_forever()


if __name__ == '__main__':
    main()
