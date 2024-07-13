package org.jreleaser.model.internal.release;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jreleaser.model.internal.common.AbstractModelObject;
import org.jreleaser.model.internal.common.Domain;
import org.jreleaser.model.internal.common.EnabledAware;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ReleaseNotes extends AbstractModelObject<ReleaseNotes> implements Domain, EnabledAware {
    private static final long serialVersionUID = -1029998017479730113L;
    @JsonIgnore
    private final ImmutableReleaseNotes immutable = new ImmutableReleaseNotes(this);
    private Boolean enabled;
    private String configurationFile;

    public org.jreleaser.model.api.release.GithubReleaser.ReleaseNotes asImmutable() {
        return immutable;
    }

    @Override
    public void merge(ReleaseNotes source) {
        this.enabled = merge(this.enabled, source.enabled);
        this.configurationFile = merge(this.configurationFile, source.configurationFile);
    }

    @Override
    public boolean isEnabled() {
        return null != enabled && enabled;
    }

    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabledSet() {
        return null != enabled;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("enabled", isEnabled());
        map.put("configurationFile", configurationFile);
        return map;
    }
}