import paho.mqtt.client as mqtt
import signal
import socket
import sys
import subprocess
import threading
import time

from motioncam import PiMotionCamera


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[SEC]'

MQTT_BROKER = '192.167.1.16'
MQTT_PORT = 1883

TOPIC_CAMERA_FEED_ON = 'ie/sheehan/smarthome/security/camera/on'
TOPIC_CAMERA_FEED_OFF = 'ie/sheehan/smarthome/security/camera/off'

TOPIC_CAMERA_MOTION = 'ie/sheehan/smarthome/security/camera/motion'
# =============================================================================

# ==== DEFINING GLOBAL VARIABLES ==============================================
camera = PiMotionCamera()
client = mqtt.Client()
# =============================================================================


# ==== DEFINING CALLBACKS =====================================================
def on_connect(c, udata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)


def on_message(c, udata, message):
    global camera

    # stop the motion tracking service, start the streaming service
    if message.topic == TOPIC_CAMERA_FEED_ON:
        if camera.running:
            camera.stop()
            time.sleep(.5)

        if not is_stream_running():
            start_stream()

    # stop the streaming service, start the motion tracking service
    elif message.topic == TOPIC_CAMERA_FEED_OFF:
        if is_stream_running():
            stop_stream()
            time.sleep(.5)

        if not camera.running:
            threading.Thread(target=start_camera).start()


def on_motion():
    global client
    client.publish(TOPIC_CAMERA_MOTION)
# =============================================================================


# ==== DECLARING METHODS ======================================================
def signal_handler(signal, frame):
    global camera
    global client

    camera.stop()
    client.loop_stop()
    sys.exit(0)


def start_camera():
    global camera
    camera.start()


def start_stream():
    print '{}: starting network stream'.format(SCRIPT_LABEL)
    command = 'sudo service motion start'
    result = subprocess.call(command.split())

    if result == 0:
        print '{}: success!'.format(SCRIPT_LABEL)
    else:
        print '{}: failed to start network stream...'.format(SCRIPT_LABEL)


def stop_stream():
    print '{}: stopping network stream'.format(SCRIPT_LABEL)
    command = 'sudo service motion stop'
    result = subprocess.call(command.split())

    if result == 0:
        print '{}: success!'.format(SCRIPT_LABEL)
    else:
        print '{}: failed to stop network stream...'.format(SCRIPT_LABEL)


def is_stream_running():
    command = 'ps -A'
    output = subprocess.check_output(command.split())

    if 'motion' in output:
        return True
    else:
        return False
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    global camera
    global client

    signal.signal(signal.SIGINT, signal_handler)

    if is_stream_running():
        stop_stream()
        time.sleep(.5)

    camera.on_motion = on_motion

    threading.Thread(target=start_camera).start()

    client.on_connect = on_connect
    client.on_message = on_message

    try:
        client.connect(MQTT_BROKER, MQTT_PORT, 60)
    except socket.error:
        print '{}: MQTT failed to connect - is the broker online?'.format(SCRIPT_LABEL)
        sys.exit(0)

    client.subscribe('#')
    client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
