package org.jreleaser.model.api.release;

interface ServiceInformation {
    String getServiceName();

    String getCanonicalRepoName();

    String getReverseRepoHost();

    String getHost();

    String getName();
}