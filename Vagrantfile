VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  config.vm.box = "chef/debian-7.8"
  config.vm.provision :shell, path: "vagrant-scripts/bootstrap.sh"
  config.vm.network :forwarded_port, host: 6379, guest: 6379 # Redis
  config.vm.network :forwarded_port, host: 5432, guest: 5432 # PostgreSQL
  config.vm.network "private_network", ip: "192.168.55.131"
  config.vm.synced_folder "~/.gradle", "/home/vagrant/.gradle", nfs: true
  config.vm.synced_folder "~/.dashboard", "/home/vagrant/.dashboard", nfs: true

  config.vm.provider "virtualbox" do |v|
    v.customize ["modifyvm", :id, "--cpus", 2]
    v.customize ["modifyvm", :id, "--memory", 2048]
  end

end
