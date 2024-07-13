package org.jreleaser.model.internal.release;

import org.jreleaser.model.Active;
import org.jreleaser.model.api.release.Releaser;

import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public final class ImmutableGitView implements org.jreleaser.model.api.release.GithubReleaser {
    private static final long serialVersionUID = -4561765054201402112L;
    private final GithubReleaser githubReleaser;

    ImmutableGitView(GithubReleaser githubReleaser) {
        this.githubReleaser = githubReleaser;
    }

    @Override
    public boolean isPrerelease() {
        return githubReleaser.isPrerelease();
    }

    @Override
    public boolean isDraft() {
        return githubReleaser.isDraft();
    }

    @Override
    public String getDiscussionCategoryName() {
        return githubReleaser.getDiscussionCategoryName();
    }

    @Override
    public org.jreleaser.model.api.release.GithubReleaser.ReleaseNotes getReleaseNotes() {
        return githubReleaser.getReleaseNotes().asImmutable();
    }

    @Override
    public String getServiceName() {
        return githubReleaser.getServiceName();
    }

    @Override
    public boolean isReleaseSupported() {
        return githubReleaser.isReleaseSupported();
    }

    @Override
    public String getCanonicalRepoName() {
        return githubReleaser.getCanonicalRepoName();
    }

    @Override
    public String getReverseRepoHost() {
        return githubReleaser.getReverseRepoHost();
    }

    @Override
    public boolean isMatch() {
        return githubReleaser.isMatch();
    }

    @Override
    public String getHost() {
        return githubReleaser.getHost();
    }

    @Override
    public String getName() {
        return githubReleaser.getName();
    }

    @Override
    public String getRepoUrl() {
        return githubReleaser.getRepoUrl();
    }

    @Override
    public String getRepoCloneUrl() {
        return githubReleaser.getRepoCloneUrl();
    }

    @Override
    public String getCommitUrl() {
        return githubReleaser.getCommitUrl();
    }

    @Override
    public String getSrcUrl() {
        return githubReleaser.getSrcUrl();
    }

    @Override
    public String getDownloadUrl() {
        return githubReleaser.getDownloadUrl();
    }

    @Override
    public String getReleaseNotesUrl() {
        return githubReleaser.getReleaseNotesUrl();
    }

    @Override
    public String getLatestReleaseUrl() {
        return githubReleaser.getLatestReleaseUrl();
    }

    @Override
    public String getIssueTrackerUrl() {
        return githubReleaser.getIssueTrackerUrl();
    }

    @Override
    public String getUsername() {
        return githubReleaser.getUsername();
    }

    @Override
    public String getToken() {
        return githubReleaser.getToken();
    }

    @Override
    public String getTagName() {
        return githubReleaser.getTagName();
    }

    @Override
    public String getPreviousTagName() {
        return githubReleaser.getPreviousTagName();
    }

    @Override
    public String getReleaseName() {
        return githubReleaser.getReleaseName();
    }

    @Override
    public String getBranch() {
        return githubReleaser.getBranch();
    }

    @Override
    public String getBranchPush() {
        return githubReleaser.getBranchPush();
    }

    @Override
    public Releaser.Prerelease getPrerelease() {
        return githubReleaser.getPrerelease().asImmutable();
    }

    @Override
    public boolean isSign() {
        return githubReleaser.isSign();
    }

    @Override
    public org.jreleaser.model.api.release.Changelog getChangelog() {
        return githubReleaser.getChangelog().asImmutable();
    }

    @Override
    public Releaser.Milestone getMilestone() {
        return githubReleaser.getMilestone().asImmutable();
    }

    @Override
    public Releaser.Issues getIssues() {
        return githubReleaser.getIssues().asImmutable();
    }

    @Override
    public boolean isSkipTag() {
        return githubReleaser.isSkipTag();
    }

    @Override
    public boolean isSkipRelease() {
        return githubReleaser.isSkipRelease();
    }

    @Override
    public boolean isOverwrite() {
        return githubReleaser.isOverwrite();
    }

    @Override
    public Releaser.Update getUpdate() {
        return githubReleaser.getUpdate().asImmutable();
    }

    @Override
    public String getApiEndpoint() {
        return githubReleaser.getApiEndpoint();
    }

    @Override
    public boolean isArtifacts() {
        return githubReleaser.isArtifacts();
    }

    @Override
    public boolean isFiles() {
        return githubReleaser.isFiles();
    }

    @Override
    public boolean isChecksums() {
        return githubReleaser.isChecksums();
    }

    @Override
    public boolean isCatalogs() {
        return githubReleaser.isCatalogs();
    }

    @Override
    public boolean isSignatures() {
        return githubReleaser.isSignatures();
    }

    @Override
    public Active getUploadAssets() {
        return githubReleaser.getUploadAssets();
    }

    @Override
    public org.jreleaser.model.api.common.CommitAuthor getCommitAuthor() {
        return githubReleaser.getCommitAuthor().asImmutable();
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        return unmodifiableMap(githubReleaser.asMap(full));
    }

    @Override
    public boolean isEnabled() {
        return githubReleaser.isEnabled();
    }

    @Override
    public String getOwner() {
        return githubReleaser.getOwner();
    }

    @Override
    public Integer getConnectTimeout() {
        return githubReleaser.getConnectTimeout();
    }

    @Override
    public Integer getReadTimeout() {
        return githubReleaser.getReadTimeout();
    }
}
