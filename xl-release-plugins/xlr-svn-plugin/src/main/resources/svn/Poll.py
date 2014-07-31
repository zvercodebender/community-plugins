from com.xebialabs.xlrelease.plugin.svn import SvnClient

if svnRepository is None:
    print "No repository provided."
    sys.exit(1)

repositoryUrl = svnRepository['url']
if repositoryUrl.endswith('/'):
    repositoryUrl = repositoryUrl[:len(repositoryUrl)-1]

if branch is None:
    url = repositoryUrl
else:
    url = repositoryUrl + '/' + branch

credentials = CredentialsFallback(svnRepository, username, password).getCredentials()

client = SvnClient(url, credentials['username'], credentials['password'])
triggerState = "%s" % client.getLatestRevision()
commitId = triggerState
