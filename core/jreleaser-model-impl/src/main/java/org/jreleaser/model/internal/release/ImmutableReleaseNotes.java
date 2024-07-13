package org.jreleaser.model.internal.release;

import org.jreleaser.model.api.release.GithubReleaser;

import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class ImmutableReleaseNotes implements GithubReleaser.ReleaseNotes {
    private static final long serialVersionUID = -301461478911447433L;
    private final ReleaseNotes releaseNotes;

    ImmutableReleaseNotes(ReleaseNotes releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    @Override
    public String getConfigurationFile() {
        return releaseNotes.getConfigurationFile();
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        return unmodifiableMap(releaseNotes.asMap(full));
    }

    @Override
    public boolean isEnabled() {
        return releaseNotes.isEnabled();
    }
}
