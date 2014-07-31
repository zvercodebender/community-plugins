package com.xebialabs.xlrelease.plugin.svn;

import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SvnClientTest {
    SvnKitFacade svnKitFacade = mock(SvnKitFacade.class);

    @Test
    public void should_get_latest_revision() throws SVNException {
        // Given
        SVNRepository repository = mock(SVNRepository.class);
        ISVNAuthenticationManager authenticationManager = mock(ISVNAuthenticationManager.class);

        given(svnKitFacade.create("svnRoot")).willReturn(repository);
        given(svnKitFacade.createDefaultAuthenticationManager("user", "password")).willReturn(authenticationManager);
        given(repository.getLatestRevision()).willReturn(1337L);
        SvnClient svnClient = new SvnClient("svnRoot", "user", "password", svnKitFacade);

        // When
        long latestRevision = svnClient.getLatestRevision();

        // Then
        assertThat(latestRevision).isEqualTo(1337L);
        verify(svnKitFacade).setup();
        verify(repository).setAuthenticationManager(authenticationManager);
    }
}
