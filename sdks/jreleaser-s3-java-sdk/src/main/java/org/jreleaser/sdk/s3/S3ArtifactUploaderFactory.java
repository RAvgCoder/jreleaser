/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2022 The JReleaser authors.
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
package org.jreleaser.sdk.s3;

import org.jreleaser.model.internal.JReleaserContext;
import org.jreleaser.model.internal.upload.S3Uploader;
import org.jreleaser.model.spi.upload.ArtifactUploaderFactory;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

/**
 * @author Andres Almiray
 * @since 0.8.0
 */
@ServiceProviderFor(ArtifactUploaderFactory.class)
public class S3ArtifactUploaderFactory implements ArtifactUploaderFactory<org.jreleaser.model.api.upload.S3Uploader,
    S3Uploader, S3ArtifactUploader> {
    @Override
    public String getName() {
        return org.jreleaser.model.api.upload.S3Uploader.TYPE;
    }

    @Override
    public S3ArtifactUploader getArtifactUploader(JReleaserContext context) {
        return new S3ArtifactUploader(context);
    }
}