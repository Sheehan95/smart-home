import json
import paho.mqtt.client as mqtt
import requests
import time

from constants.mqtt import *
from constants.webservice import *


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[GATE]'

TOPIC_SMARTHOME_ROOT = '/ie/sheehan/smart-home/#'

TOPIC_ENVIRONMENT_READING_LOG = '/ie/sheehan/smart-home/envreading/log'
TOPIC_STOCK_SCALE_LOG = '/ie/sheehan/smart-home/stock/scale/log'
TOPIC_SECURITY_CAMERA_MOTION = '/ie/sheehan/smart-home/security/camera/motion'
# =============================================================================


# ==== DEFINING VARIABLES =====================================================
client = mqtt.Client()
# =============================================================================


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(c, userdata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)


def on_message(c, userdata, message):
    print '{}: received message with topic {}'.format(SCRIPT_LABEL, message.topic)

    if message.topic == TOPIC_ENVIRONMENT_READING_LOG:
        print '{}: forwarding temperature log to web server'.format(SCRIPT_LABEL)
        payload = json.loads(message.payload)

        try:
            target = 'http://{}:8080/{}/{}'.format(DOMAIN, ENDPOINT_ENVIRONMENT, 'add')
            request = requests.post(target, json=payload)
            print '{}: HTTP request status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect to web server'.format(SCRIPT_LABEL)

    elif message.topic == TOPIC_STOCK_SCALE_LOG:
        print '{}: forwarding stock log to web server'.format(SCRIPT_LABEL)
        payload = json.loads(message.payload)

        try:
            target = 'http://{}:8080/{}/{}'.format(DOMAIN, ENDPOINT_STOCK, 'add')
            request = requests.post(target, json=payload)
            print '{}: HTTP request status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect to web server'.format(SCRIPT_LABEL)

    elif message.topic == TOPIC_SECURITY_CAMERA_MOTION:
        print '{}: forwarding motion notice to web server'.format(SCRIPT_LABEL)
        payload = {'image': message.payload, 'timestamp': int(time.time()), 'viewed': False}

        try:
            target = 'http://{}:8080/{}/{}'.format(DOMAIN, ENDPOINT_SECURITY, 'intrusion/add')
            request = requests.post(target, json=payload)
            print '{}: HTTP status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect to web server'.format(SCRIPT_LABEL)

    print '\n'
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect(MQTT_BROKER, MQTT_PORT)
    client.subscribe(TOPIC_SMARTHOME_ROOT)

    client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
