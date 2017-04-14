import json
import paho.mqtt.client as mqtt
import requests


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[GATE]'

MQTT_BROKER = '127.0.0.1'
MQTT_PORT = 1883

TOPIC_SMARTHOME_ROOT = '/ie/sheehan/smart-home/#'

TOPIC_ENVIRONMENT_READING_LOG = '/ie/sheehan/smart-home/envreading/log'
TOPIC_SECURITY_CAMERA_MOTION = '/ie/sheehan/smart-home/security/camera/motion'
# =============================================================================


# ==== DEFINING VARIABLES =====================================================
mqtt_client = mqtt.Client()
# =============================================================================


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(client, userdata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)

    if client is not None:
        print 'Client: ', client

    if userdata is not None:
        print 'User Data: ', userdata

    if flags is not None:
        print 'Flags: ', flags


def on_message(client, userdata, message):
    print 'Client: ', client
    print 'Topic: ', message.topic
    # print 'Payload:', message.payload

    if userdata is not None:
        print 'User Data: ', userdata

    if message.topic == TOPIC_ENVIRONMENT_READING_LOG:
        payload = json.loads(message.payload)

        try:
            request = requests.post('http://192.167.1.31:8080/environment/add', json=payload)
            print '{}: HTTP request status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: Failed to connect to web service'.format(SCRIPT_LABEL)

    elif message.topic == TOPIC_SECURITY_CAMERA_MOTION:
        print '{}: Forwarding motion notice to web server'.format(SCRIPT_LABEL)
        payload = json.loads(message.payload)

        try:
            request = requests.post('http://192.167.1.31:8080/security/intrusion/log', json=payload)
            print '{}: POSTing image status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: Failed to connect'.format(SCRIPT_LABEL)
        except Exception:
            print '{}: Something else happened'.format(SCRIPT_LABEL)
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
