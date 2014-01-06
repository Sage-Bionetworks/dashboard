dashboard development environment
=========

### Redis

####  Install Redis and start the Redis server

    $ wget http://download.redis.io/releases/redis-2.8.3.tar.gz
    $ tar xzf redis-2.8.3.tar.gz
    $ cd redis-2.8.3
    $ make
    $ src/redis-server
    $ <ctrl-z>
    $ bg

#### Test the Redis server

    $ src/redis-cli
    redis 127.0.0.1:6379> PING
    PONG
    redis 127.0.0.1:6379> QUIT

#### Shut down Redis

    $ cd <redis-home>
    $ src/redis-cli
    redis 127.0.0.1:6379> SHUTDOWN
    redis 127.0.0.1:6379> QUIT

### Gradle

#### Download and install the latest Gradle

#### Set up the Gradle properties file

    $ touch ~/.gradle/gradle.properties

Add the following lines:

    accessKey=<Your AWS dev account access key>
    secretKey=<Your AWS dev account secrete Key>

    synapseUsr=<Your Synapse admin user account>
    synapsePwd=<Your Synapse admin account password>

(Alternatively, these parameters can be passed in as command-line arguments and will overwrite the Gradle properties.)

### Build and run the project

    $ cd <project-home>
    $ gradle --info clean build
    ...
    BUILD SUCCESSFUL
    Total time: 1 mins 25.893 secs
    $
    $ gradle run -PfilePath=/local/path/to/access/log/files
    $
