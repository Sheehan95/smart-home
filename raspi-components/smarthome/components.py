import cv2
import datetime
import imutils
import time

from picamera import PiCamera
from picamera.array import PiRGBArray


# ==== DEFINING CONSTANTS =====================================================
SCRIPT_LABEL = '[SEC]'

CAMERA_FPS = 22
CAMERA_RESOLUTION = [640, 480]
CAMERA_PREP_TIME = 2.5

DELTA_THRESHOLD = 5
MINIMUM_MOTION_AREA = 5000
MINIMUM_MOTION_DETECTED = 8
MINIMUM_TIME_BETWEEN_MOTION = 5

SHOW_VIDEO = False
# =============================================================================


class PiMotionCamera:
    def __init__(self):
        self.camera = None
        self.raw_capture = None
        self.average_frame = None
        self.motion_counter = 0
        self.running = False
        self.on_motion = None
        self.last_motion = datetime.datetime.now()

    @staticmethod
    def configure_camera():
        camera = PiCamera()
        camera.resolution = tuple(CAMERA_RESOLUTION)
        camera.framerate = CAMERA_FPS
        return camera

    def start(self):
        self.camera = self.configure_camera()
        self.raw_capture = PiRGBArray(self.camera, size=tuple(CAMERA_RESOLUTION))

        time.sleep(CAMERA_PREP_TIME)
        print '{}: Starting motion detection'.format(SCRIPT_LABEL)

        self.running = True

        motion_counter = 0

        for f in self.camera.capture_continuous(self.raw_capture, format="bgr", use_video_port=True):
            if not self.running:
                self.camera.close()
                self.raw_capture.truncate(0)
                break

            frame = f.array
            timestamp = datetime.datetime.now()
            text = 'Unoccupied'

            frame = imutils.resize(frame, width=500)  # change the size of the frame
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)  # convert the frame to grayscale
            gray = cv2.GaussianBlur(gray, (21, 21), 0)  # apply a blur to the frame

            # initializes the 'average' frame if it hasn't already been
            if self.average_frame is None:
                self.average_frame = gray.copy().astype('float')
                self.raw_capture.truncate(0)  # clearing the buffer
                continue

            cv2.accumulateWeighted(gray, self.average_frame, 0.5)  # updating average frame with current frame elements

            frame_delta = cv2.absdiff(gray, cv2.convertScaleAbs(self.average_frame))  # difference between two frames
            threshold = cv2.threshold(frame_delta, DELTA_THRESHOLD, 255, cv2.THRESH_BINARY)[1]
            threshold = cv2.dilate(threshold, None, iterations=2)
            (image, contours, _) = cv2.findContours(threshold.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

            for contour in contours:
                # if contour is insignificant, skip this loop iteration
                if cv2.contourArea(contour) < MINIMUM_MOTION_AREA:
                    continue

                (x, y, w, h) = cv2.boundingRect(contour)
                cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)  # get rectangle for motion
                text = 'Occupied'

            # drawing the time and room status to the screen
            ts = timestamp.strftime('%A %d %B %Y %I:%M:%S%p')
            cv2.putText(frame, 'Room Status: {}'.format(text), (10, 20), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 2)
            cv2.putText(frame, ts, (10, frame.shape[0] - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.35, (0, 0, 255), 1)

            if text == 'Occupied':
                if (timestamp - self.last_motion).total_seconds() > MINIMUM_TIME_BETWEEN_MOTION:
                    motion_counter += 1

                    if motion_counter >= MINIMUM_MOTION_DETECTED:
                        self.last_motion = timestamp

                        if self.on_motion is not None:
                            self.on_motion(f)
            else:
                motion_counter = 0

            if SHOW_VIDEO:
                cv2.imshow('Security Feed', frame)
                key = cv2.waitKey(1) & 0xFF

                if key == ord('q'):
                    break

            self.raw_capture.truncate(0)

    def stop(self):
        self.running = False


class Alarm:
    def __init__(self):
        self.armed = False
        self.last_armed = None

    def arm_alarm(self):
        if not self.armed:
            print '[SEC]: actually arming it now boiiiii'
            self.armed = True
            self.last_armed = datetime.datetime.now()

    def disarm_alarm(self):
        if self.armed:
            self.armed = False


class Heating:
    def __init__(self):
        self.on = False
        self.last_on = None
        self.last_duration = None

    def turn_on(self):
        if not self.on:
            print '[ENV]: actually turning it on now boiiiii'
            self.on = True
            self.last_on = datetime.datetime.now()

    def turn_off(self):
        if self.on:
            self.on = False
            self.last_duration = (self.last_on - datetime.datetime.now()).total_seconds()
