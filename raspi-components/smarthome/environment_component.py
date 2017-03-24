import json
import time

import paho.mqtt.client as mqtt

# from sensors import TemperatureSensor
from sensors import FakeTemperatureSensor

MQTT_BROKER = '192.167.1.23'

TOPIC_ENVIRONMENT_READING_REQUESTS = '/ie/sheehan/smart-home/envreading/request'
TOPIC_ENVIRONMENT_READING_RESPONSE = '/ie/sheehan/smart-home/envreading/response'


mqtt_client = mqtt.Client()
# sensor = TemperatureSensor()
sensor = FakeTemperatureSensor()


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
    print 'Payload: ', message.payload

    if userdata is not None:
        print 'User Data: ', userdata

    if message.topic == TOPIC_ENVIRONMENT_READING_REQUESTS:
        log_environment_reading(sensor.get_temp(), sensor.get_humidity())
# =============================================================================


def log_environment_reading(temperature, humidity):
    timestamp = int(time.time())
    payload = json.dumps({'temperature': temperature, 'humidity': humidity, 'timestamp': timestamp})
    mqtt_client.publish(TOPIC_ENVIRONMENT_READING_RESPONSE, payload)


def main():
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_BROKER, 1883, 60)
    mqtt_client.subscribe(TOPIC_ENVIRONMENT_READING_REQUESTS)

    mqtt_client.loop_forever()


if __name__ == '__main__':
    main()
