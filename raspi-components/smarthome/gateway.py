import json
import paho.mqtt.client as mqtt
import requests


MQTT_BROKER = '192.167.1.23'

TOPIC_ENVIRONMENT_READING_LOG = '/ie/sheehan/smart-home/envreading/log'


mqtt_client = mqtt.Client()


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(client, userdata, flags, rc):
    print 'Client connected with status code ', rc

    if client is not None:
        print 'Client: ', client

    if userdata is not None:
        print 'User Data: ', userdata

    if flags is not None:
        print 'Flags: ', flags


def on_message(client, userdata, message):
    print 'Client: ', client
    print 'Topic: ', message.topic
    print 'Payload:', message.payload

    if userdata is not None:
        print 'User Data: ', userdata

    if message.topic == TOPIC_ENVIRONMENT_READING_LOG:
        payload = json.loads(message.payload)
        request = requests.post('http://192.167.1.31:8080/environment/add', json=payload)
        print request.status_code
# =============================================================================


def main():
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_BROKER, 1883, 60)
    mqtt_client.subscribe('#')

    mqtt_client.loop_forever()


if __name__ == '__main__':
    main()
