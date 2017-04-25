import json
import threading
import time

import paho.mqtt.client as mqtt

from components.stock import WeighingScale
from datetime import datetime

# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[STOCK]'

MQTT_BROKER = '192.167.1.101'
MQTT_PORT = 1883

TOPIC_STOCK_SCALE_LOG = '/ie/sheehan/smart-home/stock/scale/log'

TOPIC_STOCK_SCALE_REQUEST = '/ie/sheehan/smart-home/stock/scale/request'
TOPIC_STOCK_SCALE_RESPONSE = '/ie/sheehan/smart-home/stock/scale/response'

TOPIC_STOCK_SCALE_CALIBRATE = '/ie/sheehan/smart-home/stock/scale/calibrate'
# =============================================================================

# ==== DEFINING VARIABLES =====================================================
scale = WeighingScale()
client = mqtt.Client()
# =============================================================================


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(c, userdata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)

    if client is not None:
        print 'Client: ', client

    if userdata is not None:
        print 'User Data: ', userdata

    if flags is not None:
        print 'Flags: ', flags


def on_message(c, userdata, message):
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
        print '{}: Pushing weight now!'.format(SCRIPT_LABEL)
        payload = json.dumps({'product': product, 'weight': weight, 'capacity': capacity, 'timestamp': timestamp})
        client.publish(TOPIC_STOCK_SCALE_LOG, payload)

    threading.Timer(5, stock_reading_log).start()


def initial_wait():
    # delay = 60 - datetime.now().second
    delay = 5
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

    client.connect(MQTT_BROKER, MQTT_PORT, 60)
    client.subscribe(TOPIC_STOCK_SCALE_REQUEST)
    client.subscribe(TOPIC_STOCK_SCALE_CALIBRATE)

    client.loop_forever()

if __name__ == '__main__':
    main()
# =============================================================================
