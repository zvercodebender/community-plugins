package com.xebialabs.xlrelease.plugin.svn;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;

public class SvnClient {
    private String svnRoot;
    private String userName;
    private String password;
    private SvnKitFacade svnKitFacade;

    public SvnClient(String svnRoot, String userName, String password) {
        this(svnRoot, userName, password, new SvnKitFacade());
    }

    SvnClient(String svnRoot, String userName, String password, SvnKitFacade svnKitFacade) {
        this.svnRoot = svnRoot;
        this.userName = userName;
        this.password = password;
        this.svnKitFacade = svnKitFacade;
    }

    public long getLatestRevision() throws SVNException {
        svnKitFacade.setup();
        SVNRepository repository = svnKitFacade.create(svnRoot);
        ISVNAuthenticationManager authenticationManager = svnKitFacade.createDefaultAuthenticationManager(userName, password);
        repository.setAuthenticationManager(authenticationManager);
        return repository.getLatestRevision();
    }
}
