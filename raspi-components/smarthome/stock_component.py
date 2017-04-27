import json
import paho.mqtt.client as mqtt
import threading
import time

from components.stock import WeighingScale
from constants.mqtt import *
from datetime import datetime


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[STOCK]'

TOPIC_STOCK_SCALE_LOG = '/ie/sheehan/smart-home/stock/scale/log'

TOPIC_STOCK_SCALE_REQUEST = '/ie/sheehan/smart-home/stock/scale/request'
TOPIC_STOCK_SCALE_RESPONSE = '/ie/sheehan/smart-home/stock/scale/response'
TOPIC_STOCK_SCALE_CALIBRATE = '/ie/sheehan/smart-home/stock/scale/calibrate'
# =============================================================================

# ==== DEFINING VARIABLES =====================================================
scale = WeighingScale()
client = mqtt.Client(userdata=SCRIPT_LABEL)
# =============================================================================


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(c, userdata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)


def on_message(c, userdata, message):
    print '{}: received message with topic {} from {}'.format(SCRIPT_LABEL, message.topic, userdata)

    if message.topic == TOPIC_STOCK_SCALE_REQUEST:
        stock_reading_response()

    elif message.topic == TOPIC_STOCK_SCALE_CALIBRATE:
        payload = json.loads(message.payload)
        scale.calibrate(payload['product'])
# =============================================================================


# ==== METHOD DECLARATION =====================================================
def stock_reading_response():
    weight = scale.current_weight
    capacity = scale.capacity
    timestamp = int(time.time())
    product = scale.product

    payload = json.dumps({'product': product, 'weight': weight, 'capacity': capacity, 'timestamp': timestamp})
    client.publish(TOPIC_STOCK_SCALE_RESPONSE, payload)


def stock_reading_log():
    weight = scale.current_weight
    capacity = scale.capacity
    timestamp = int(time.time())
    product = scale.product

    if weight >= 0:
        print '{}: pushing weight now!'.format(SCRIPT_LABEL)
        payload = json.dumps({'product': product, 'weight': weight, 'capacity': capacity, 'timestamp': timestamp})
        client.publish(TOPIC_STOCK_SCALE_LOG, payload)

    threading.Timer(60, stock_reading_log).start()


def initial_wait():
    delay = 60 - datetime.now().second
    threading.Timer(delay, stock_reading_log).start()


def on_lift():
    # start thread, wait for weight to not be 0, push new value
    print 'Lifted!'


def on_down():
    print 'DOWN!'
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    initial_wait()

    scale.on_lift = on_lift
    scale.on_down = on_down

    client.on_connect = on_connect
    client.on_message = on_message

    client.connect(MQTT_BROKER, MQTT_PORT)
    client.subscribe(TOPIC_STOCK_SCALE_REQUEST)
    client.subscribe(TOPIC_STOCK_SCALE_CALIBRATE)

    client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
