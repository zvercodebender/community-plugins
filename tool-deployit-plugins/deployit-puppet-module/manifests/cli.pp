# Resource: deployit::cli
#
# This resource manages the Deployit CLI
#
# Parameters:
#
# Actions:
#
# Sample Usage:
#
define deployit::cli(
	$cliArchive,
	$version = "unspecified",
	$destinationDir = "/home/vagrant",
	$ensure = present) {
	
	if $ensure == present {

		exec { "install-deployit-cli":
			cwd => "${destinationDir}",
			command => "/usr/bin/unzip -o ${cliArchive} -d ${destinationDir} && mv deployit-*-cli deployit-cli",
			creates => "${destinationDir}/deployit-cli",
		}

	} elsif $ensure == absent {

		file { "remove-deployit-cli":
			path   => "${destinationDir}/deployit-cli",
			ensure => absent
		}

	} else {
		notice("Ensure $ensure not supported")
	}
}
