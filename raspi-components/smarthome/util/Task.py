from datetime import datetime
import threading


# defines a list of task types used to identify tasks
class TaskType:
    def __init__(self):
        pass

    UNDEFINED = 'undefined'
    ARM_ALARM = 'arm_alarm'
    DISARM_ALARM = 'disarm_alarm'
    TURN_ON_HEATING = 'turn_on_heating'
    TURN_OFF_HEATING = 'turn_off_heating'


# raised when the supplied date is invalid for a task to run, i.e.: a date that has already occurred
class InvalidScheduleDateError(Exception):
    def __init__(self, message):
        self.message = message

    def __str__(self):
        return repr(self.message)


# raised when attempting to stop a schedule that hasn't been started yet
class TaskNotScheduledError(Exception):
    def __init__(self, message):
        self.message = message

    def __str__(self):
        return repr(self.message)


# raised when attempting to schedule a task that has already been scheduled
class TaskAlreadyScheduledError(Exception):
    def __init__(self, message):
        self.message = message

    def __str__(self):
        return repr(self.message)


# manages a list of tasks
class TaskScheduler:

    def __init__(self):
        self.scheduled = []

    # adds a task to the scheduler to run on a given date & time
    def add_task(self, action, date, task_type=TaskType.UNDEFINED):
        task = Task(action, date, task_type, lambda: self.remove_task(task))
        self.scheduled.append(task)
        task.start()

    # removes a task from the scheduler
    def remove_task(self, scheduled_task):
        if scheduled_task in self.scheduled:
            self.scheduled.remove(scheduled_task)
            scheduled_task.cancel()
        else:
            raise TaskNotScheduledError('This task has not been scheduled to run')


# a task that can be run at a given time
class Task:

    counter = 0

    def __init__(self, action, date, task_type=TaskType.UNDEFINED, callback=None):
        self.action = action
        self.task_id = Task.counter
        Task.counter += 1
        self.task_type = task_type
        self.date = date
        self.callback = callback
        self.timer = None

    # the action the task will carried out when the scheduled time arrives
    def run(self):
        self.action()

        if self.callback is not None:
            self.callback()

    # schedules the action for the given time
    def start(self):
        # if the date has already passed
        if not self.date > datetime.now():
            raise InvalidScheduleDateError('The given date has already passed')

        # calculate the time between now & the scheduled time
        run_at = (self.date - datetime.now()).total_seconds()
        self.timer = threading.Timer(run_at, self.run)
        self.timer.start()

    # cancels the action
    def cancel(self):
        if self.timer is None:
            raise TaskNotScheduledError('Task has not been scheduled')
        else:
            self.timer.cancel()

    def __str__(self):
        return '#{0}: {1} is set to run at {2}'.format(self.task_id, self.task_type, self.date)

    def __eq__(self, other):
        if self.task_type == other.task_type and self.date == other.date:
            return True
        else:
            return False

    def __ne__(self, other):
        return not self.__eq__(other)
