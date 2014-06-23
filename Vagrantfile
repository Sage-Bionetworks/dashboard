# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.box = "chef/debian-7.4"
  config.vm.provision :shell, path: "bootstrap.sh"
  config.vm.network :forwarded_port, host: 6379, guest: 6379 # Redis
  config.vm.network :forwarded_port, host: 5432, guest: 5432 # PostgreSQL
  config.vm.synced_folder "~/.gradle", "/home/vagrant/.gradle"

end
