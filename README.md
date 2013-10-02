dashboard
=========

##  Install Redis and start the Redis server ##

    $ wget http://download.redis.io/releases/redis-2.6.16.tar.gz
    $ tar xzf redis-2.6.16.tar.gz
    $ cd redis-2.6.16
    $ make
    $ src/redis-server
    $ <ctrl-z>
    $ bg

## Test the Redis server ##

    $ src/redis-cli
    redis 127.0.0.1:6379> PING
    PONG
    redis 127.0.0.1:6379> QUIT

## Build the project ##

    $ cd <project-folder>
    $ gradle --info clean build
    ...
    BUILD SUCCESSFUL
    Total time: 12.903 secs
    $
