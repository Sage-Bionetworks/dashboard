#!/usr/bin/env bash

export DEBIAN_FRONTEND=noninteractive

# All the commands will run as root by vagrant provisioning unless the user is explicitly set
# Add '/usr/local/bin' to root's $PATH for this session as programs installed there may be needed
export PATH=${PATH}:/usr/local/bin

# Some packages need to be installed locally for the user 'vagrant'
export TARGET_USER=vagrant

# Create ~/.bash_profile if it does not exist
su - ${TARGET_USER} -c "cd ~"
su - ${TARGET_USER} -c "bash /${TARGET_USER}/vagrant-scripts/bash-profile.sh"

# Add wheezy-backports to the sources list
cp --force /${TARGET_USER}/vagrant-scripts/sources.list /etc/apt/sources.list

# Debian maintenance
apt-get --quiet --yes autoremove
apt-get --quiet --yes autoclean
apt-get --quiet --yes update
apt-get --quiet --yes upgrade
apt-get --quiet --yes --target-release wheezy-backports update
apt-get --quiet --yes --target-release wheezy-backports upgrade
apt-get --quiet --yes autoremove
apt-get --quiet --yes autoclean

# NFS Client
apt-get --quiet --yes install nfs-common

# Java
apt-get --quiet --yes --target-release wheezy-backports install openjdk-7-jdk

# PostgreSQL
apt-get --quiet --yes install postgresql
apt-get --quiet --yes install postgresql-client
echo "listen_addresses = '*'" >> /etc/postgresql/9.1/main/postgresql.conf
echo "host all all 10.0.0.0/16 trust" >> /etc/postgresql/9.1/main/pg_hba.conf
su - postgres -c "psql -f /${TARGET_USER}/vagrant-scripts/dw-bootstrap.sql"
su - ${TARGET_USER}
service postgresql restart

# Redis
apt-get --quiet --yes --target-release wheezy-backports install redis-server
# Comment out binding to specific IPs
sed 's/^[[:space:]]*bind[[:space:]]/# &/g' /etc/redis/redis.conf | tee /etc/redis/redis.conf
service redis-server restart
