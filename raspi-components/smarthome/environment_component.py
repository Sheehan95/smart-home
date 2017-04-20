import json
import time
import threading

import paho.mqtt.client as mqtt

from components import Heating
from datetime import datetime
from sensors import TemperatureSensor
# from sensors import FakeTemperatureSensor


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[ENV]'

MQTT_BROKER = '192.167.1.101'
MQTT_PORT = 1883

TOPIC_ENVIRONMENT_READING_LOG = '/ie/sheehan/smart-home/envreading/log'
TOPIC_ENVIRONMENT_READING_REQUESTS = '/ie/sheehan/smart-home/envreading/request'
TOPIC_ENVIRONMENT_READING_RESPONSE = '/ie/sheehan/smart-home/envreading/response'

TOPIC_ENVIRONMENT_HEATING_REQUEST = '/ie/sheehan/smart-home/envreading/heating/request'
TOPIC_ENVIRONMENT_HEATING_RESPONSE = '/ie/sheehan/smart-home/envreading/heating/response'
TOPIC_ENVIRONMENT_HEATING_ACTIVATE = '/ie/sheehan/smart-home/envreading/heating/activate'
# =============================================================================


# ==== DEFINING VARIABLES =====================================================
heating = Heating()
mqtt_client = mqtt.Client()
sensor = TemperatureSensor()
# sensor = FakeTemperatureSensor()
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
    print 'Payload: ', message.payload

    if userdata is not None:
        print 'User Data: ', userdata

    if message.topic == TOPIC_ENVIRONMENT_READING_REQUESTS:
        environment_reading_response(sensor.get_temp(), sensor.get_humidity())

    elif message.topic == TOPIC_ENVIRONMENT_HEATING_ACTIVATE:
        payload = json.loads(message.payload)

        if payload['on']:
            print '{}: turning on heating'.format(SCRIPT_LABEL)
            heating.turn_on()
        elif not payload['on']:
            print '{}: turning off heating'.format(SCRIPT_LABEL)
            heating.turn_off()

    elif message.topic == TOPIC_ENVIRONMENT_HEATING_REQUEST:
        payload = json.dumps({'on': heating.on, 'timestamp': heating.last_on, 'duration': heating.last_duration})
        client.publish(TOPIC_ENVIRONMENT_HEATING_RESPONSE, payload)
# =============================================================================


# ==== METHOD DECLARATION =====================================================
def environment_reading_response(temperature, humidity):
    timestamp = int(time.time())
    payload = json.dumps({'temperature': temperature, 'humidity': humidity, 'timestamp': timestamp})
    mqtt_client.publish(TOPIC_ENVIRONMENT_READING_RESPONSE, payload)


def environment_reading_log():
    temperature = sensor.get_temp()
    humidity = sensor.get_humidity()
    timestamp = int(time.time())

    payload = json.dumps({'temperature': temperature, 'humidity': humidity, 'timestamp': timestamp})
    mqtt_client.publish(TOPIC_ENVIRONMENT_READING_LOG, payload)

    threading.Timer(60, environment_reading_log).start()


def initial_wait():
    delay = 60 - datetime.now().second
    threading.Timer(delay, environment_reading_log).start()
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    initial_wait()

    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_BROKER, MQTT_PORT, 60)
    mqtt_client.subscribe(TOPIC_ENVIRONMENT_READING_REQUESTS)

    mqtt_client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
