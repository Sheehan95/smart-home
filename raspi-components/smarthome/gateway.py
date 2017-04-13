import json
import paho.mqtt.client as mqtt
import requests


# ==== DEFINING CONSTANTS =====================================================
MQTT_BROKER = '127.0.0.1'
MQTT_PORT = 1883

TOPIC_ENVIRONMENT_READING_LOG = '/ie/sheehan/smart-home/envreading/log'
# =============================================================================


# ==== DEFINING VARIABLES =====================================================
mqtt_client = mqtt.Client()
# =============================================================================


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

        try:
            request = requests.post('http://192.167.1.31:8080/environment/add', json=payload)
            print request.status_code
        except requests.ConnectionError:
            print 'Failed to connect to web service'
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_BROKER, MQTT_PORT, 60)
    mqtt_client.subscribe('/ie/sheehan/smart-home/#')

    mqtt_client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
