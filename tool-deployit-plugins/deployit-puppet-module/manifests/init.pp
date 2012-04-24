# Class: deployit
#
# This module integrates puppet with Deployit.
#
class deployit (
	$serverHome = "/opt/deployit-server",
	$cliHome = "/home/vagrant/deployit-cli",
	$host = "localhost",
	$port = "4516",
	$username = "admin",
	$password = "admin"
	) {

	# Install scripts
	file { "/opt/deployit-puppet-module/wait-for-server-start.sh":
		ensure   => file,
	    owner    => "root",
	    mode     => "0755",
		source   => "puppet:///modules/deployit/wait-for-server-start.sh",
		require  => [File["/opt/deployit-puppet-module"]]
	}

	file { "/opt/deployit-puppet-module/create-ci.py":
		ensure   => file,
	    owner    => "root",
	    mode     => "0755",
		source   => "puppet:///modules/deployit/create-ci.py",
		require  => [File["/opt/deployit-puppet-module"]]
	}

	file { "/opt/deployit-puppet-module/set-envs.py":
		ensure   => file,
	    owner    => "root",
	    mode     => "0755",
		source   => "puppet:///modules/deployit/set-envs.py",
		require  => [File["/opt/deployit-puppet-module"]]
	}

	file { "/opt/deployit-puppet-module/set-tags.py":
		ensure   => file,
	    owner    => "root",
	    mode     => "0755",
		source   => "puppet:///modules/deployit/set-tags.py",
		require  => [File["/opt/deployit-puppet-module"]]
	}

	file { "/opt/deployit-puppet-module/delete-ci.py":
		ensure   => file,
	    owner    => "root",
	    mode     => "0755",
		source   => "puppet:///modules/deployit/delete-ci.py",
		require  => [File["/opt/deployit-puppet-module"]]
	}

	file { "/opt/deployit-puppet-module/grant-permission.py":
		ensure   => file,
	    owner    => "root",
	    mode     => "0755",
		source   => "puppet:///modules/deployit/grant-permission.py",
		require  => [File["/opt/deployit-puppet-module"]]
	}

	file { "/opt/deployit-puppet-module/revoke-permission.py":
		ensure   => file,
	    owner    => "root",
	    mode     => "0755",
		source   => "puppet:///modules/deployit/revoke-permission.py",
		require  => [File["/opt/deployit-puppet-module"]]
	}

	file { "/opt/deployit-puppet-module/import-package.py":
		ensure   => file,
	    owner    => "root",
	    mode     => "0755",
		source   => "puppet:///modules/deployit/import-package.py",
		require  => [File["/opt/deployit-puppet-module"]]
	}

	file { "/opt/deployit-puppet-module":
		ensure => directory
	}

}
