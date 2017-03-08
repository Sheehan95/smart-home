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
