dashboard development environment
=========

[![Build Status](https://travis-ci.org/Sage-Bionetworks/dashboard.svg?branch=master)](https://travis-ci.org/Sage-Bionetworks/dashboard)

### Set up the required configuration

    $ touch ~/.gradle/gradle.properties

Add the following lines:

    access.record.bucket=<S3 bucket for the access records>

    aws.access.key=<Your AWS dev account access key>
    aws.secret.key=<Your AWS dev account secrete Key>

    synapse.user=<Your Synapse admin user account>
    synapse.password=<Your Synapse admin account password>

    dw.username=<Data warehouse user>
    dw.password=<Data warehouse password>

(Alternatively, these parameters can be passed in as command-line arguments which will overwrite the above entries in the Gradle properties file.)

### Virtual development environment via Vagrant

1. Download and install [VirtualBox](https://www.virtualbox.org/).
2. Download and install [Vagrant](http://www.vagrantup.com/).
3. At the project root, run `vagrant up`. Due to a Vagrant bug, after the Redis server has successfully restarted at the end, you may need to ctrl+c twice to exit the command.
4. Once the box is up, ssh to it `vagrant ssh`.
5. Go to the shared folder `cd /vagrant`.
6. Run `./gradlew --info clean build`.
7. Run `./gradlew eclipse` to generate the files for importing the project into Eclipse.

If this works, we are done and the steps below can be skipped.

### The non-virtual way

Only follow the steps below if Vagrant is not working for you.

#### Install Redis and start the Redis server

    $ wget http://download.redis.io/releases/redis-2.8.6.tar.gz
    $ tar xzf redis-2.8.6.tar.gz
    $ cd redis-2.8.6
    $ make
    $ src/redis-server
    $ <ctrl-z>
    $ bg

#### Install PostgreSQL and start the PostgreSQL server

#### Build and run the project

    $ cd <project-home>
    $ ./gradlew --info clean build
    ...
    BUILD SUCCESSFUL
    Total time: 1 mins 25.893 secs

The package has a command-line interface that can be used to read local copies of access records and populates the Redis cache.

    $ ./gradlew run -PfilePath=</local/path/to/access/log/files>
    :compileJava UP-TO-DATE
    :processResources UP-TO-DATE
    :classes UP-TO-DATE
    :run
    Total number of files: 956
    Loading file 1 of 956
    UpdateResult [filePath=/logs/access-records/22/2013-11-18/18-10-14.csv.gz, lineCount=12, status=SUCCEEDED]
    ...
    UpdateResult [filePath=/logs/access-records/22/2013-12-07/04-15-51.csv.gz, lineCount=1, status=SUCCEEDED]
    BUILD SUCCESSFUL
    Total time: 43 mins 7.178 secs
