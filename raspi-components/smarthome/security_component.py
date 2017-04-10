import json
import paho.mqtt.client as mqtt
import subprocess
import requests


# ==== DEFINING CONSTANTS =====================================================
MQTT_BROKER = '192.167.1.23'
MQTT_PORT = 1883

MOTION_SERVICE = 'motion'

TOPIC_SECURITY_FEED_TRIGGER = '/ie/sheehan/smart-home/security/feed'
TOPIC_SECURITY_FEED_STATUS = '/ie/sheehan/smart-home/security/status'
# =============================================================================


# ==== DEFINING VARIABLES =====================================================
mqtt_client = mqtt.Client()
# =============================================================================


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(client, userdata, flags, rc):
    print 'Client connected with status code ', rc


def on_message(client, userdata, message):
    print 'Client: ', client
    print 'Topic: ', message.topic
    print 'Payload:', message.payload

    if userdata is not None:
        print 'User Data: ', userdata

    if message.topic == TOPIC_SECURITY_FEED_TRIGGER:
        payload = json.loads(message.payload)

        if payload['camera'] is True:
            print 'Turning camera stream on'
        else:
            print 'Turning camera stream off'

    if message.topic == TOPIC_SECURITY_FEED_STATUS:
        if get_motion_service_status():
            print 'Motion status is running'
# =============================================================================


# ==== DEFINING METHODS =======================================================
def get_motion_service_status():
    command = 'ps -A'
    output = subprocess.check_output(command.split(' '))

    if MOTION_SERVICE in output:
        return True
    else:
        return False
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_BROKER, MQTT_PORT, 60)
    mqtt_client.subscribe(TOPIC_SECURITY_FEED_STATUS, TOPIC_SECURITY_FEED_TRIGGER)

    mqtt_client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
