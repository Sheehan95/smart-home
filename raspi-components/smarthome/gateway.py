import json
import paho.mqtt.client as mqtt
import requests
import time


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[GATE]'

MQTT_BROKER = '127.0.0.1'
MQTT_PORT = 1883

DOMAIN = '192.168.0.30'
ENDPOINT_ENVIRONMENT = 'environment'
ENDPOINT_SECURITY = 'security'

TOPIC_SMARTHOME_ROOT = '/ie/sheehan/smart-home/#'

TOPIC_ENVIRONMENT_READING_LOG = '/ie/sheehan/smart-home/envreading/log'
TOPIC_STOCK_SCALE_LOG = '/ie/sheehan/smart-home/stock/scale/log'
TOPIC_SECURITY_CAMERA_MOTION = '/ie/sheehan/smart-home/security/camera/motion'
# =============================================================================


# ==== DEFINING VARIABLES =====================================================
mqtt_client = mqtt.Client()
# =============================================================================


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(client, userdata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)


def on_message(client, userdata, message):
    print '{}: received message with topic {}'.format(SCRIPT_LABEL, message.topic)

    if message.topic == TOPIC_ENVIRONMENT_READING_LOG:
        print '{}: forwarding temperature log to web server'.format(SCRIPT_LABEL)
        payload = json.loads(message.payload)

        try:
            request = requests.post('http://192.168.0.30:8080/environment/add', json=payload)
            print '{}: HTTP request status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect to web server'.format(SCRIPT_LABEL)

    elif message.topic == TOPIC_STOCK_SCALE_LOG:
        print '{}: forwarding stock log to web server'.format(SCRIPT_LABEL)
        payload = json.loads(message.payload)

        try:
            request = requests.post('http://192.168.0.30:8080/stock/add', json=payload)
            print '{}: HTTP request status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect to web server'.format(SCRIPT_LABEL)

    elif message.topic == TOPIC_SECURITY_CAMERA_MOTION:
        print '{}: forwarding motion notice to web server'.format(SCRIPT_LABEL)

        payload = {'image': message.payload, 'timestamp': int(time.time()), 'viewed': False}

        try:
            request = requests.post('http://192.168.0.30:8080/security/intrusion/add', json=payload)
            print '{}: POSTing image status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect'.format(SCRIPT_LABEL)
        except Exception:
            print '{}: unknown error occurred'.format(SCRIPT_LABEL)

    print '\n'
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_BROKER, MQTT_PORT, 60)
    mqtt_client.subscribe(TOPIC_SMARTHOME_ROOT)

    mqtt_client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
