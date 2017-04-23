import threading
import time

import paho.mqtt.client as mqtt

from components.stock import WeighingScale
from datetime import datetime

# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[STOCK]'

MQTT_BROKER = '192.167.1.101'
MQTT_PORT = 1883

TOPIC_STOCK_SCALE_REQUEST = '/ie/sheehan/smart-home/stock/scale/request'
TOPIC_STOCK_SCALE_RESPONSE = '/ie/sheehan/smart-home/stock/scale/response'

TOPIC_STOCK_PRODUCT_SET = '/ie/sheehan/smart-home/stock/product/set'
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

        client.publish(TOPIC_STOCK_SCALE_RESPONSE)

# =============================================================================


# ==== METHOD DECLARATION =====================================================
def stock_reading_log():
    weight = scale.get_weight()
    timestamp = int(time.time())

    threading.Timer(60, stock_reading_log).start()

def initial_wait():
    delay = 60 - datetime.now().second
    threading.Timer(delay, stock_reading_log).start()
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    initial_wait()

    client.on_connect = on_connect
    client.on_message = on_message

    client.connect(MQTT_BROKER, MQTT_PORT, 60)
    client.subscribe(TOPIC_STOCK_SCALE_REQUEST)

    # client.loop_forever()

    while True:
        try:
            print 'Grams: ', scale.get_weight()
        except IOError as e:
            print 'IO Error: ', e

if __name__ == '__main__':
    main()
# =============================================================================
