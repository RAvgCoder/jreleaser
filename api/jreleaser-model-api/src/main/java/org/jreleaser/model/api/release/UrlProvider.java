package org.jreleaser.model.api.release;

interface UrlProvider {
    String getRepoUrl();

    String getRepoCloneUrl();

    String getCommitUrl();

    String getSrcUrl();

    String getDownloadUrl();

    String getReleaseNotesUrl();

    String getLatestReleaseUrl();

    String getIssueTrackerUrl();

    String getApiEndpoint();
}