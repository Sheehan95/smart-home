import json
import threading
import time
from datetime import datetime

import paho.mqtt.client as mqtt

from components.environment import Heating
from components.sensors import TemperatureSensor
from constants.mqtt import *


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[ENV]'

TOPIC_ENVIRONMENT_READING_LOG = '/ie/sheehan/smart-home/envreading/log'
TOPIC_ENVIRONMENT_READING_REQUESTS = '/ie/sheehan/smart-home/envreading/request'
TOPIC_ENVIRONMENT_READING_RESPONSE = '/ie/sheehan/smart-home/envreading/response'

TOPIC_ENVIRONMENT_HEATING_REQUEST = '/ie/sheehan/smart-home/envreading/heating/request'
TOPIC_ENVIRONMENT_HEATING_RESPONSE = '/ie/sheehan/smart-home/envreading/heating/response'
TOPIC_ENVIRONMENT_HEATING_ACTIVATE = '/ie/sheehan/smart-home/envreading/heating/activate'
# =============================================================================


# ==== DEFINING VARIABLES =====================================================
heating = Heating()
sensor = TemperatureSensor()
client = mqtt.Client()
# =============================================================================


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(c, userdata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)


def on_message(c, userdata, message):
    print '{}: received message with topic {}'.format(SCRIPT_LABEL, message.topic)

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
        if heating.last_on is not None:
            payload = json.dumps(
                {'on': heating.on, 'timestamp': heating.last_on.strftime('%s'), 'duration': heating.last_duration})
        else:
            payload = json.dumps({'on': heating.on, 'timestamp': 0, 'duration': heating.last_duration})

        client.publish(TOPIC_ENVIRONMENT_HEATING_RESPONSE, payload)
# =============================================================================


# ==== METHOD DECLARATION =====================================================
def environment_reading_response(temperature, humidity):
    timestamp = int(time.time())
    payload = json.dumps({'temperature': temperature, 'humidity': humidity, 'timestamp': timestamp})
    client.publish(TOPIC_ENVIRONMENT_READING_RESPONSE, payload)


def environment_reading_log():
    temperature = sensor.get_temp()
    humidity = sensor.get_humidity()
    timestamp = int(time.time())

    payload = json.dumps({'temperature': temperature, 'humidity': humidity, 'timestamp': timestamp})
    client.publish(TOPIC_ENVIRONMENT_READING_LOG, payload)

    threading.Timer(60, environment_reading_log).start()


def initial_wait():
    delay = 60 - datetime.now().second
    threading.Timer(delay, environment_reading_log).start()
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    initial_wait()

    client.on_connect = on_connect
    client.on_message = on_message

    client.connect(MQTT_BROKER, MQTT_PORT)
    client.subscribe(TOPIC_ENVIRONMENT_READING_REQUESTS)
    client.subscribe(TOPIC_ENVIRONMENT_HEATING_REQUEST)
    client.subscribe(TOPIC_ENVIRONMENT_HEATING_ACTIVATE)

    client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
