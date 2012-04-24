# Resource: deployit::server
#
# This resource manages the Deployit server. It installs, starts and stops it.
#
# Parameters:
#
# Actions:
#
# Sample usage:
#
# deployit::server { 'server':
#	destinationDir => '/opt',
#	serverArchive => '/deployit/binaries/deployit-3.6.4-server.zip',
#	version => '3.6.4',
#	ensure => present / absent / running / stopped,
# }
#
# TO DO:
# - allow installation of plugins with the server

define deployit::server(
	$serverArchive = "unspecified",
	$version = "unspecified",
	$destinationDir = "/opt",
	$plugins = [],
	$ensure = present) {
	
	if $ensure == present {
	  exec { "extract-deployit-server":
	    cwd => "${destinationDir}",
	    command => "/usr/bin/unzip -o ${serverArchive} -d ${destinationDir} && cd /opt && mv deployit-*-server deployit-server",
	    creates => "${destinationDir}/deployit-server",
	  }

	  file { "empty-deployit-repo":
	    path => "${destinationDir}/deployit-server/repository",
	    ensure => "directory",
	    require => Exec["extract-deployit-server"],
	  }

	  exec { "init-deployit-repo":
	    cwd => "${destinationDir}/deployit-server",
	    command => $version ? {
	      # Note: -force option was introduced in 3.6.0
	      /3\.[0-5].*/ => "${destinationDir}/deployit-server/bin/server.sh -setup -reinitialize",
	      default => "${destinationDir}/deployit-server/bin/server.sh -setup -reinitialize -force",
	    },
	    creates => "${destinationDir}/deployit-server/repository/repository",
	    require => File["empty-deployit-repo"],
	  }

	  file { "default-deployit-cfg":
	    path => "${destinationDir}/deployit-server/conf/deployit.conf",
		source   => "puppet:///modules/deployit/deployit.conf",
	    ensure => "present",
	    require => Exec["init-deployit-repo"],
	  }

	  file { "enable-debug-logging":
	    path => "${destinationDir}/deployit-server/conf/logback.xml",
		source   => "puppet:///modules/deployit/logback.xml",
	    ensure => "present",
	    require => Exec["init-deployit-repo"],
	  }

	  exec { "install-available-plugins":
		cwd => "${destinationDir}/deployit-server",
	    command => $version ? {
	      # Note: available plugins were introduced in 3.6.0
	      /3\.[0-5].*/ => "/bin/ls",
	      default => "/bin/cp available-plugins/* plugins",
	    },
	    require => Exec["extract-deployit-server"],
	  }

	} elsif $ensure == absent {
		file { "Remove deployit-server":
			path   => "${destinationDir}/deployit-server",
			ensure => absent
		}
	} elsif $ensure == running {

		# Start the server
		exec { "start-deployit":
			cwd => "${destinationDir}/deployit-server",
			command => "/usr/bin/nohup bin/server.sh &",
			creates => "${destinationDir}/deployit-server/nohup.out",
		}

		exec { "wait-for-start-deployit":
			cwd => "/opt/deployit-puppet-module",
			command => "/opt/deployit-puppet-module/wait-for-server-start.sh",
			require => Exec["start-deployit"],
		}

	} elsif $ensure == stopped {

		# Stop the server
		# TBD

	} else {
		notice("Ensure $ensure not supported")
	}

}
