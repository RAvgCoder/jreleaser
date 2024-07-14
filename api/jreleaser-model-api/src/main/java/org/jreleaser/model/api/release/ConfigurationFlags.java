package org.jreleaser.model.api.release;

interface ConfigurationFlags {
    boolean isSkipTag();

    boolean isSkipRelease();

    boolean isOverwrite();

    boolean isSign();

    boolean isArtifacts();

    boolean isFiles();

    boolean isChecksums();

    boolean isCatalogs();

    boolean isSignatures();
}