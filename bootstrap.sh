#!/usr/bin/env bash

echo 'deb http://mirrors.kernel.org/debian wheezy-backports main' >> /etc/apt/sources.list
yes | apt-get update
yes | apt-get -t wheezy-backports upgrade

yes | apt-get -t wheezy-backports install redis-server
yes | apt-get -t wheezy-backports install openjdk-7-jdk

