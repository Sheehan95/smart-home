import paho.mqtt.client as mqtt
import signal
import sys
import subprocess
import threading
import time

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
    print '[CAMERA]: starting network stream'
    command = 'sudo service motion start'
    result = subprocess.call(command.split())

    if result == 0:
        print '[CAMERA]: success!'
    else:
        print '[CAMERA]: failed to start network stream...'


def stop_stream():
    print '[CAMERA]: stopping network stream'
    command = 'sudo service motion stop'
    result = subprocess.call(command.split())

    if result == 0:
        print '[CAMERA]: success!'
    else:
        print '[CAMERA]: failed to stop network stream...'


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

    threading.Thread(target=start_camera).start()

    client.on_connect = on_connect
    client.on_message = on_message

    client.connect('192.167.1.23', 1883, 60)
    client.subscribe('#')

    client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
