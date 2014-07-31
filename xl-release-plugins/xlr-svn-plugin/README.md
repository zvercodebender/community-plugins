### SVN trigger plugin

In order to use SVN trigger plugin, you need to create a **SVN Repository**. SVN repositories could be managed from the configuration screen.

A SVN Repository has the following properties:

* **Title**: The title of the repository.
* **Url**: The address where the server is reachable.
* **Username**: The login user ID on the server.
* **Password**: The login user password on the server.

For memory, the svn trigger it's used to poll periodically a SVN repository and triggers a release if it detects a new commit. The specific fields of the plugin during its creation are:

* **Svn Repository**: The SVN repository to watch (mandatory)
* **Branch**: The branch of the repository to used (will be concatenated to the SVN repository URL)
* **Username**: The username used to connect to this repository (left blank if there is no authentication)
* **Password**: The password used to connect to this repository (left blank if there is no authentication)

This trigger exposes:

* **Commit Id**: Corresponding to the id of the new commit detected
