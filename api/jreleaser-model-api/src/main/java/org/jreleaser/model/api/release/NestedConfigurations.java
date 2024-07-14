package org.jreleaser.model.api.release;

interface NestedConfigurations {
    Releaser.Update getUpdate();

    Releaser.Prerelease getPrerelease();

    Changelog getChangelog();

    Releaser.Milestone getMilestone();

    Releaser.Issues getIssues();
}