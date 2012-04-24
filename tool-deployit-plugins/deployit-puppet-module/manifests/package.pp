#
# Package.pp
#
# Usage:
#   deployit::package { 'petclinic-ear/1.0':
#		package => 'build/petclinic-1.0.dar',
#		ensure => present / absent
#   }
#
# TO DO:
# - support URL upload

define deployit::package(
	$package = "unknown",
	$ciId = "unknown",
	$ensure = present
) {

	include deployit

	if $ensure == present {

		deployit::exec { "import package ${package}":
			source => "/opt/deployit-puppet-module/import-package.py",
			params => inline_template("'<%= package %>'"),
		}
		
	} elsif $ensure == absent {

		deployit::exec { "delete package ${ciId}":
			source => "/opt/deployit-puppet-module/delete-ci.py",
			params => inline_template("'<%= ciId %>'"),
		}
		
	} else {
		notice("Ensure $ensure not supported")
	}

}
