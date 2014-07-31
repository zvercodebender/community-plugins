package com.xebialabs.xlrelease.plugin.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnKitFacade {

    public void setup() {
        DAVRepositoryFactory.setup();
    }

    public SVNRepository create(String svnRoot) throws SVNException {
        return SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnRoot));
    }

    public ISVNAuthenticationManager createDefaultAuthenticationManager(String userName, String password) {
        return SVNWCUtil.createDefaultAuthenticationManager(userName, password);
    }
}
