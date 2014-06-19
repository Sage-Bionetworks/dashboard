#!/usr/bin/env bash

export DEBIAN_FRONTEND=noninteractive

apt-get autoclean
apt-get autoremove

echo 'deb http://mirrors.kernel.org/debian wheezy-backports main' >> /etc/apt/sources.list
apt-get --quiet --yes update
apt-get --quiet --yes --target-release wheezy-backports upgrade

apt-get --quiet --yes --target-release wheezy-backports install redis-server
apt-get --quiet --yes --target-release wheezy-backports install openjdk-7-jdk

