# Deployit Puppet Module #

This Puppet module is used to interact with Deployit from Puppet. It supports:

* installing the Deployit server and CLI
* starting / stopping the Deployit server
* running a CLI script using the Deployit CLI
* registering a provisioned host and adding it to an environment

## Sample usage ##

	class { 'deployit':
		serverHome => '/opt/deployit-server',
		cliHome => '/home/vagrant/deployit-cli',
	}
	
	deployit::server { 'install-server':
		serverArchive => "/download-cache/deployit-${DEPLOYIT_VERSION}-server.zip",
	  	ensure => running,
	}
	
	deployit::cli { 'install-cli':
	  	destinationDir => '/home/vagrant',
	  	cliArchive => "/download-cache/deployit-${DEPLOYIT_VERSION}-cli.zip",
	  	ensure => present,
	}
	
	deployit::exec { 'run-my-cli-script':
		username => 'admin',
		password => 'admin',
		host => 'localhost',
		port => '4516',
		source => '/tmp/cli.py',
	}
	
	deployit::ci { 'apache2':
	  ciId => 'Infrastructure/localhost/apache2',
	  ciType => 'www.ApacheHttpdServer',
	  ciValues => { startCommand => '/etc/init.d/apache2 start', stopCommand => '/etc/init.d/apache2 stop', restartCommand => '/usr/sbin/apache2ctl restart',
	                defaultDocumentRoot => '/var/www', configurationFragmentDirectory => '/etc/apache2/conf.d' },
	  ciTags => [ 'www' ],
	  ciEnvironments => [ 'Environments/env1' ],
	  ensure => present,
	}
	
	deployit::permission { 'deploy#initial to Environment/env1':
	  permissions => [ 'deploy#initial', 'read' ],
	  principals => [ 'john' ],
	  cis => [ 'Environments/env1' ],
	  ensure => present,
	}
