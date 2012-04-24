#
# Ci.pp
#
# Usage:
#   deployit::ci { 'tomcat':
#		id => 'Infrastructure/mydirectory/newhost/tomcat',
#		type => 'tomcat.Server',
#		values => { home => /opt/tomcat, startCommand => start.sh, stopCommand => stop.sh, tags => [ tag1, tag2 ] }
#		environments => [ 'a', 'b', 'c'], # Add CI to environments a, b and c
#		ensure => present / absent / discovered, # Trigger discovery, store discovered resources.
#   }

define deployit::ci(
	$ciId,
	$ciType = "undefined",
	$ciValues = {},
	$ciEnvironments = [],
	$ciTags = [],
	$ensure = present
) {
	if $ensure == present {

		deployit::exec { "create CI ${ciId} of type ${ciType}":
			source => "/opt/deployit-puppet-module/create-ci.py",
			params => inline_template("'<%= ciId %>' '<%= ciType %>' <% ciValues.each do |key, val| -%><%= key %>='<%= val %>' <% end -%>"),
		}

		deployit::exec { "set tags on CI ${ciId}":
			source => "/opt/deployit-puppet-module/set-tags.py",
			params => inline_template("'<%= ciId %>' <% ciTags.each do |val| -%>'<%= val %>' <% end %>"),
		}

		deployit::exec { "set environments on CI ${ciId}":
			source => "/opt/deployit-puppet-module/set-envs.py",
			params => inline_template("'<%= ciId %>' <% ciEnvironments.each do |val| -%>'<%= val %>' <% end %>"),
		}

		Deployit::Exec["create CI ${ciId} of type ${ciType}"] 
			-> Deployit::Exec ["set tags on CI ${ciId}"] 
			-> Deployit::Exec["set environments on CI ${ciId}"]
		
	} elsif $ensure == absent {

		deployit::exec { "delete CI ${ciId}":
			source => "/opt/deployit-puppet-module/delete-ci.py",
			params => inline_template("'<%= ciId %>'"),
		}
		
	} else {
		notice("Ensure $ensure not supported")
	}

}
