/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2024 The JReleaser authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jreleaser.model.internal.release;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jreleaser.model.internal.common.AbstractModelObject;
import org.jreleaser.model.internal.common.Domain;
import org.jreleaser.model.internal.common.EnabledAware;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public final class GithubReleaser extends BaseReleaser<org.jreleaser.model.api.release.GithubReleaser, GithubReleaser> {
    private static final long serialVersionUID = -7946819245345601709L;
    private final ReleaseNotes releaseNotes = new ReleaseNotes();
    @JsonIgnore
    private final ImmutableGitView immutable;
    private Boolean draft;
    private String discussionCategoryName;


    public GithubReleaser() {
        super(org.jreleaser.model.api.release.GithubReleaser.TYPE, true);
        setHost("github.com");
        setApiEndpoint("https://api.github.com");
        setRepoUrl("https://{{repoHost}}/{{repoOwner}}/{{repoName}}");
        setRepoCloneUrl("https://{{repoHost}}/{{repoOwner}}/{{repoName}}.git");
        setCommitUrl("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/commits");
        setSrcUrl("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/blob/{{repoBranch}}");
        setDownloadUrl("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/releases/download/{{tagName}}/{{artifactFile}}");
        setReleaseNotesUrl("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/releases/tag/{{tagName}}");
        setLatestReleaseUrl("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/releases/latest");
        setIssueTrackerUrl("https://{{repoHost}}/{{repoOwner}}/{{repoName}}/issues");

        immutable = new ImmutableGitView(this);
    }

    @Override
    public ImmutableGitView asImmutable() {
        return immutable;
    }

    @Override
    public void merge(GithubReleaser source) {
        super.merge(source);
        this.draft = merge(this.draft, source.draft);
        this.discussionCategoryName = merge(this.discussionCategoryName, source.discussionCategoryName);
        setReleaseNotes(source.releaseNotes);
    }

    public boolean isDraft() {
        return null != draft && draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public boolean isDraftSet() {
        return null != draft;
    }

    public String getDiscussionCategoryName() {
        return discussionCategoryName;
    }

    public void setDiscussionCategoryName(String discussionCategoryName) {
        this.discussionCategoryName = discussionCategoryName;
    }

    public ReleaseNotes getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(ReleaseNotes releaseNotes) {
        this.releaseNotes.merge(releaseNotes);
    }

    @Override
    public String getReverseRepoHost() {
        return "com.github";
    }

    @Override
    public Map<String, Object> asMap(boolean full) {
        Map<String, Object> map = super.asMap(full);
        map.put("draft", isDraft());
        map.put("discussionCategoryName", discussionCategoryName);
        map.put("releaseNotes", releaseNotes.asMap(full));
        return map;
    }
}
