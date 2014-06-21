#!/usr/bin/env bash

export DEBIAN_FRONTEND=noninteractive

apt-get autoclean
apt-get autoremove

echo 'deb http://mirrors.kernel.org/debian wheezy-backports main' >> /etc/apt/sources.list
apt-get --quiet --yes update
apt-get --quiet --yes --target-release wheezy-backports upgrade

# Java
apt-get --quiet --yes --target-release wheezy-backports install openjdk-7-jdk

# PostgreSQL
apt-get --quiet --yes --target-release wheezy-backports install postgresql
apt-get --quiet --yes --target-release wheezy-backports install postgresql-client

# Redis
apt-get --quiet --yes --target-release wheezy-backports install redis-server
# Comment out binding to specific IP addresses so that the host can access the Redis server
sed 's/^[[:space:]]*bind[[:space:]]/# &/g' /etc/redis/redis.conf | tee /etc/redis/redis.conf
service redis-server restart

