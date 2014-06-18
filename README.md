dashboard development environment
=========

### Virtual development environment via Vagrant

1. Download and install [VirtualBox](https://www.virtualbox.org/).
2. Download and install [Vagrant](http://www.vagrantup.com/).
3. At the project root, run `vagrant up`.
4. Once the box is up, ssh to it `vagrant ssh`.
5. We now should be in the Vagrant box. Add the required configuration to `~/.gradle/gradle.properties`. This only needs to be done once.
6. Go to the shared folder `cd /vagrant`.
7. Run `gradlew --info clean build`.
8. Run `gradlew eclipse` to generate the files for importing the project into Eclipse.

If this works, we are done and the steps below can be skipped.

### The traditional way

Only follow the steps below if Vagrant is not working for you.

#### Install Redis and start the Redis server

    $ wget http://download.redis.io/releases/redis-2.8.6.tar.gz
    $ tar xzf redis-2.8.6.tar.gz
    $ cd redis-2.8.6
    $ make
    $ src/redis-server
    $ <ctrl-z>
    $ bg

#### Set up the required configuration

    $ touch ~/.gradle/gradle.properties

Add the following lines:

    accessKey=<Your AWS dev account access key>
    secretKey=<Your AWS dev account secrete Key>

    synapseUsr=<Your Synapse admin user account>
    synapsePwd=<Your Synapse admin account password>

(Alternatively, these parameters can be passed in as command-line arguments which wil overwrite the Gradle properties.)

### Build and run the project

    $ cd <project-home>
    $ gradlew --info clean build
    ...
    BUILD SUCCESSFUL
    Total time: 1 mins 25.893 secs

    $ gradlew run -PfilePath=</local/path/to/access/log/files>
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
