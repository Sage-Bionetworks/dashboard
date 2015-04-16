dashboard development environment
=========

[![Build Status](https://travis-ci.org/Sage-Bionetworks/dashboard.svg?branch=master)](https://travis-ci.org/Sage-Bionetworks/dashboard)

### Set up the required configuration

    $ touch ~/.dashboard/dashboard.config
    $ chmod 600 ~/.dashboard/dashboard.config

Add the following lines:

    access.record.bucket=<S3 bucket for the access records>

    aws.access.key=<Your AWS dev account access key>
    aws.secret.key=<Your AWS dev account secrete Key>

    synapse.user=<Your Synapse admin user account>
    synapse.password=<Your Synapse admin account password>

    google.client.id=<Your Google account info>
    google.client.secret=<Your Google account info>

(Alternatively, these parameters can be passed in as command-line arguments which will overwrite the above entries in the Gradle properties file.)

### Development

1. Download and install [VirtualBox](https://www.virtualbox.org/).
2. Download and install [Vagrant](http://www.vagrantup.com/).
3. At the project root, run `vagrant up`.
4. Once the box is up, ssh to it `vagrant ssh`.
5. Go to the shared folder `cd /vagrant`.
6. Run `./gradlew clean build`.
7. Outside the guest, at the host's project folder, run `./gradlew eclipse` to generate the files for importing the project into Eclipse.
8. The package has a command-line interface that can be used to read local copies of access records and populates the Redis cache.
```bash
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
```
