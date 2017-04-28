import base64
import json
import signal
import subprocess
import sys
import threading
import time

import cv2
import paho.mqtt.client as mqtt

from components.security import Alarm, PiMotionCamera
from constants.mqtt import *


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[SEC]'

TOPIC_SECURITY_CAMERA_FEED = '/ie/sheehan/smart-home/security/camera/feed'
TOPIC_SECURITY_CAMERA_MOTION = '/ie/sheehan/smart-home/security/camera/motion'

TOPIC_SECURITY_ALARM_REQUEST = '/ie/sheehan/smart-home/security/alarm/request'
TOPIC_SECURITY_ALARM_RESPONSE = '/ie/sheehan/smart-home/security/alarm/response'
TOPIC_SECURITY_ALARM_ARM = '/ie/sheehan/smart-home/security/alarm/arm'
# =============================================================================


# ==== DEFINING GLOBAL VARIABLES ==============================================
alarm = Alarm()
camera = PiMotionCamera()
client = mqtt.Client()
# =============================================================================


# ==== DEFINING CALLBACKS =====================================================
def on_connect(c, userdata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)


def on_message(c, userdata, message):
    global camera

    print '{}: received message with topic {}'.format(SCRIPT_LABEL, message.topic)

    if message.topic == TOPIC_SECURITY_CAMERA_FEED:
        payload = json.loads(message.payload)

        if payload['stream']:
            close_camera()
            time.sleep(.5)
            open_stream()
        elif not payload['stream']:
            close_stream()
            time.sleep(.5)
            open_camera()

    elif message.topic == TOPIC_SECURITY_ALARM_ARM:
        payload = json.loads(message.payload)

        if payload['arm']:
            print '{}: arming the alarm...'.format(SCRIPT_LABEL)
            alarm.arm_alarm()
        elif not payload['arm']:
            print '{}: disarming the alarm...'.format(SCRIPT_LABEL)
            alarm.disarm_alarm()

    elif message.topic == TOPIC_SECURITY_ALARM_REQUEST:
        if alarm.last_armed is not None:
            payload = json.dumps({'armed': alarm.armed, 'timestamp': alarm.last_armed.strftime('%s')})
        else:
            payload = json.dumps({'armed': alarm.armed, 'timestamp': 0})

        client.publish(TOPIC_SECURITY_ALARM_RESPONSE, payload)


def on_motion(frame):
    global alarm
    global client
    global camera

    print '{}: camera has detected motion'.format(SCRIPT_LABEL)

    if alarm.armed:
        print '{}: pushing image to web server'.format(SCRIPT_LABEL)

        cv2.imwrite('/media/usb/test.png', frame.array)

        with open('/media/usb/test.png', 'rb') as image_file:
            encoded_str = base64.b64encode(image_file.read())

        client.publish(TOPIC_SECURITY_CAMERA_MOTION, payload=encoded_str)
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


def open_stream():
    print '{}: starting camera stream on port 8081'.format(SCRIPT_LABEL)
    if not is_stream_running():
        command = 'sudo service motion start'
        result = subprocess.call(command.split())

        if result == 0:
            print '{}: stream started successfully'.format(SCRIPT_LABEL)
        else:
            print '{}: failed to start stream'.format(SCRIPT_LABEL)


def close_stream():
    print '{}: stopping camera stream'.format(SCRIPT_LABEL)
    if is_stream_running():
        command = 'sudo service motion stop'
        result = subprocess.call(command.split())

        if result == 0:
            print '{}: stream stopped successfully'.format(SCRIPT_LABEL)
        else:
            print '{}: failed to stop stream'.format(SCRIPT_LABEL)
    else:
        print '{}: stream already stopped'.format(SCRIPT_LABEL)


def open_camera():
    global camera

    if not camera.running:
        threading.Thread(target=start_camera).start()


def close_camera():
    global camera

    if camera.running:
        camera.stop()


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

    camera.on_motion = on_motion

    close_stream()
    time.sleep(1.5)
    open_camera()

    client.on_connect = on_connect
    client.on_message = on_message

    client.connect(MQTT_BROKER, MQTT_PORT)
    client.subscribe(TOPIC_SECURITY_CAMERA_FEED)
    client.subscribe(TOPIC_SECURITY_ALARM_ARM)
    client.subscribe(TOPIC_SECURITY_ALARM_REQUEST)

    client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
