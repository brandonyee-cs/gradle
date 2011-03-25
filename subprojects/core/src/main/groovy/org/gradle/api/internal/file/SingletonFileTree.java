/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.api.internal.file;

import org.gradle.api.internal.file.collections.DirectoryFileTree;
import org.gradle.api.internal.file.collections.FileCollectionResolveContext;
import org.gradle.api.tasks.TaskDependency;

import java.io.File;

public class SingletonFileTree extends CompositeFileTree {
    private final File file;
    private final TaskDependency builtBy;

    public SingletonFileTree(File file, TaskDependency builtBy) {
        this.file = file;
        this.builtBy = builtBy;
    }

    @Override
    public String getDisplayName() {
        return String.format("file '%s'", file);
    }

    @Override
    public TaskDependency getBuildDependencies() {
        return builtBy;
    }

    protected void resolve(FileCollectionResolveContext context) {
        if (file.isDirectory()) {
            context.add(new DirectoryFileTree(file));
        } else if (file.isFile()) {
            context.add(new org.gradle.api.internal.file.collections.SingletonFileTree(file));
        }
    }
}
