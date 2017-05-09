from datetime import datetime, timedelta
import json
import socket
import sys
import time
from util.task import Task, TaskScheduler, TaskType

import paho.mqtt.client as mqtt
import requests

from constants.mqtt import *
from constants.webservice import *


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[GATE]'

TOPIC_SMARTHOME_ROOT = '/ie/sheehan/smart-home/#'

TOPIC_TASK_SCHEDULE = '/ie/sheehan/smart-home/task/schedule'
TOPIC_TASK_CANCEL = '/ie/sheehan/smart-home/task/cancel'
TOPIC_TASK_GET = '/ie/sheehan/smart-home/task/get'
TOPIC_TASK_RESPONSE = '/ie/sheehan/smart-home/task/response'

TOPIC_ENVIRONMENT_READING_LOG = '/ie/sheehan/smart-home/envreading/log'
TOPIC_STOCK_SCALE_LOG = '/ie/sheehan/smart-home/stock/scale/log'
TOPIC_SECURITY_CAMERA_MOTION = '/ie/sheehan/smart-home/security/camera/motion'
# =============================================================================


# ==== DEFINING VARIABLES =====================================================
client = mqtt.Client()
scheduler = TaskScheduler()
# =============================================================================


# ==== DECLARING MQTT CALLBACK METHODS ========================================
def on_connect(c, userdata, flags, rc):
    print '{}: MQTT connected with status code {}'.format(SCRIPT_LABEL, rc)


def on_message(c, userdata, message):
    print '{}: received message with topic {}'.format(SCRIPT_LABEL, message.topic)

    if message.topic == TOPIC_ENVIRONMENT_READING_LOG:
        print '{}: forwarding temperature log to web server'.format(SCRIPT_LABEL)
        payload = json.loads(message.payload)

        try:
            target = 'http://{}:8080/{}/{}'.format(DOMAIN, ENDPOINT_ENVIRONMENT, 'add')
            request = requests.post(target, json=payload)
            print '{}: HTTP request status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect to web server'.format(SCRIPT_LABEL)

    elif message.topic == TOPIC_STOCK_SCALE_LOG:
        print '{}: forwarding stock log to web server'.format(SCRIPT_LABEL)
        payload = json.loads(message.payload)

        try:
            target = 'http://{}:8080/{}/{}'.format(DOMAIN, ENDPOINT_STOCK, 'add')
            request = requests.post(target, json=payload)
            print '{}: HTTP request status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect to web server'.format(SCRIPT_LABEL)

    elif message.topic == TOPIC_SECURITY_CAMERA_MOTION:
        print '{}: forwarding motion notice to web server'.format(SCRIPT_LABEL)
        payload = {'image': message.payload, 'timestamp': int(time.time()), 'viewed': False}

        try:
            target = 'http://{}:8080/{}/{}'.format(DOMAIN, ENDPOINT_SECURITY, 'intrusion/add')
            request = requests.post(target, json=payload)
            print '{}: HTTP status code {}'.format(SCRIPT_LABEL, request.status_code)
        except requests.ConnectionError:
            print '{}: failed to connect to web server'.format(SCRIPT_LABEL)

    elif message.topic == TOPIC_TASK_GET:
        task_list = list()

        for task in scheduler.scheduled:
            task_dict = {
                'id': task.task_id,
                'type': task.task_type,
                'timestamp': task.date.strftime('%s')
            }

            task_list.append(task_dict)

        payload = json.dumps(task_list)
        client.publish(TOPIC_TASK_RESPONSE, payload)

    elif message.topic == TOPIC_TASK_SCHEDULE:
        payload = json.loads(message.payload)
        task_type = payload['type']
        date = datetime.fromtimestamp(payload['timestamp'])

        if task_type == TaskType.ARM_ALARM:
            scheduler.add_task(arm_alarm, date, task_type)
        elif task_type == TaskType.TURN_ON_HEATING:
            scheduler.add_task(turn_on_heating, date, task_type)

    elif message.topic == TOPIC_TASK_CANCEL:
        payload = json.loads(message.payload)
        task_type = payload['type']
        date = datetime.fromtimestamp(payload['timestamp'])
        task = Task(None, date, task_type)

        scheduler.remove_task(task)

    print '\n'
# =============================================================================


# ==== DEFINE METHODS =========================================================
def arm_alarm():
    print 'Arming'


def turn_on_heating():
    print 'Turning on'
# =============================================================================


# ==== ENTRY POINT ============================================================
def main():
    client.on_connect = on_connect
    client.on_message = on_message

    try:
        client.connect(MQTT_BROKER, MQTT_PORT)
    except socket.error:
        print '{}: unable to connect to MQTT broker - is it on & available at {}?'.format(SCRIPT_LABEL, MQTT_BROKER)
        sys.exit(1)

    client.subscribe(TOPIC_SMARTHOME_ROOT)

    client.loop_forever()


if __name__ == '__main__':
    main()
# =============================================================================
