# Smart-Home
An IoT based smart home solution using Raspberry Pis.

## Installation & Setup

### MongoDB
The Spring Boot application uses a MongoDB database for the backend. The Mongo service must be installed and run before the application can be launched. To install MongoDB, follow these instructions:

* Visit [MongoDB](https://www.mongodb.com/download-center) and download the community server edition
* Once installed you must create a data folder for the database files to be stored. This can be placed in any directory and usually follows the structure /data/db
* Launch the MongoDB process by executing the following command in the MongoDB directory:
```sh
$ mongod --dbpath C:\path\to\data\db
```

### Spring Tool Suite
Spring Tool Suite isn't required to run the Spring Boot web application, but it does make building it significantly easier. Follow these steps to install STS and import the project:

* Visit [Spring Tool Suite](https://spring.io/tools) to download the latest version of STS
* When installation has finished, open STS and select File > Import > Maven > Existing Maven Projects
* When browsing for the Root Directory, select the smart-home/web-services folder and click Okay
* Tick the pom.xml file that has appeared in the Projects view and select Finish

The Spring Boot application is now ready to be launched. Ensure the MongoDB service is running before launching the application or it will crash.

## Usage & Testing

### Populating the Database
The MongoDB database is running, but by default will have no data in it. A Python script  was created to populate the database with dummy data values for testing purposes.

* Navigate to the smart-home/tests/python/temperature directory
* Import the "post.py" Python file into an IDE or execute it via the command line like so:
```sh
$ python post.py
```
* The Python script can be run any number of times to populate the database with further values. Each execution of the script adds 100 new documents to the database

### Testing the API
A number of Postman queries have been saved to test the REST API. They can be accessed by following the steps below:

* Install Postman as either a [Chrome plugin](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop?hl=en) or a [stand-alone application](https://www.getpostman.com/)
* Select the import option and browse for the file smart-home/tests/Postman API Tests.json
* The imported collection should be added to the "Collections" tab on the left of the screen. Open the imported "Smart-Home REST API Tests" collection to view and run the tests

## RESTful API Documentation

### Log Environment Reading
Used to log a new environment reading, a pairing of temperature and humidity values, to the database.

#### Endpoint
/temperature/log

#### Method
GET

#### URL Parameters
None

#### Data Parameters
A JSON environment reading object which looks like the following:
```javascript
{
  temperature: 16.15953354,
  humidity: 59.9562189
}
```

| Field       | Data Type | Description                               |
|:----------- |:--------- |:----------------------------------------- |
| temperature | Double    | Temperature in celsius                    |
| humidity    | Double    | Humidity value                            |

#### Successful Response
*HTTP Code:* 200 OK

*Content:* JSON return in the format
```javascript
{
  _id: "507f191e810c19729de860ea"
}
```

#### Failed Response
*HTTP Code:* 404 NOT FOUND

*Content:* JSON return in the format
```javascript
{
  error: "Unable to reach sensor for reading."
}
```

#### Sample Request
```javascript
$.ajax({
  url: "/temperature/log",
  type: "PUT"
  dataType: "json",
  data: {
    temperature: 15.8954656,
    humidity: 59.698495895
  }
});
```

---

### Get Environment Readings Between Two Dates
Returns all the logged temperature readings between two dates.

#### Endpoint
/temperature/get/range

#### Method
GET

#### URL Parameters
```
from=[integer]
```
example: 1489572649
```
to=[integer]
```
example: 1489572674

#### Data Parameters
None

#### Successful Response
*HTTP Code:* 200 OK

*Content:* JSON return in the format
```javascript
[
  {
    _id: "507f191e810c19729de860ea",
    temperature: 18.6549896851,
    humidity: 64.198958198,
    timestamp: 1489572674
  },
  {
    _id: "507f191e810c19729de860eb",
    temperature: 19.126565246,
    humidity: 64.986516521789,
    timestamp: 1489572956
  },
  ...
]
```

#### Failed Response
*HTTP Code:* 404 NOT FOUND

*Content:* JSON return in the format
```javascript
{
  error: "No temperature values found within that time-frame."
}
```

#### Sample Request
```javascript
$.ajax({
  url: "/temperature/get/range",
  type: "GET"
  dataType: "json",
  data: {
    from: 1489572649,
    to: 1489572956
  }
});
```

---
