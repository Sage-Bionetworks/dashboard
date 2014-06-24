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
echo "listen_addresses = '*'" >> /etc/postgresql/9.1/main/postgresql.conf
echo "host all all 10.0.0.0/16 trust" >> /etc/postgresql/9.1/main/pg_hba.conf
service postgresql restart

# Redis
apt-get --quiet --yes --target-release wheezy-backports install redis-server
# Comment out binding to specific IPs
sed 's/^[[:space:]]*bind[[:space:]]/# &/g' /etc/redis/redis.conf | tee /etc/redis/redis.conf
service redis-server restart

