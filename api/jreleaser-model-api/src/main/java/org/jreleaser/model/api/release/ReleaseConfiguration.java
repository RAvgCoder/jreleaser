package org.jreleaser.model.api.release;

interface ReleaseConfiguration {
    String getTagName();

    String getPreviousTagName();

    String getReleaseName();

    String getBranch();

    String getBranchPush();
}
