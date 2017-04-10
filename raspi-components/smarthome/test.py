import signal
import sys
import threading

import paho.mqtt.client as mqtt

from motioncam import PiMotionCamera


# ==== DEFINING CONSTANTS =====================================================
TOPIC_CAMERA_FEED_ON = 'ie/sheehan/smarthome/security/camera/on'
TOPIC_CAMERA_FEED_OFF = 'ie/sheehan/smarthome/security/camera/off'
# =============================================================================

# ==== DEFINING GLOBAL VARIABLES ==============================================
camera = PiMotionCamera()
client = mqtt.Client()
# =============================================================================


# ==== MQTT CALLBACKS =========================================================
def on_connect(c, udata, flags, rc):
    print 'Connected with status code ', rc


def on_message(c, udata, message):
    global camera

    if message.topic == TOPIC_CAMERA_FEED_ON:
        if not camera.running:
            print '[APP]: Starting camera...'
            threading.Thread(target=start_camera).start()
    elif message.topic == TOPIC_CAMERA_FEED_OFF:
        if camera.running:
            print '[APP]: Stopping camera...'
            camera.stop()
# =============================================================================


def signal_handler(signal, frame):
    global camera
    global client

    camera.stop()
    client.loop_stop()
    sys.exit(0)


def start_camera():
    global camera
    camera.start()


def main():
    global camera
    global client

    signal.signal(signal.SIGINT, signal_handler)
    threading.Thread(target=start_camera).start()

    client.on_connect = on_connect
    client.on_message = on_message
    client.connect('192.167.1.23', 1883, 60)
    client.subscribe('#')

    client.loop_forever()

if __name__ == '__main__':
    main()
