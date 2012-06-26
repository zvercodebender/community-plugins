/*
 * @(#)ManifestDefinition.java     21 Jul 2011
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package ext.deployit.community.cli.plainarchive.dar;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.Manifest;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ManifestBuilder {
    private static final String GENERATED_MANIFEST_VERSION = "1.0";

    private final ReentrantLock lock = new ReentrantLock();

    private final ConcurrentMap<String, String> mainAttributes = new ConcurrentHashMap<String, String>();
    private final ConcurrentMap<String, Map<String, String>> entryAttributes = new ConcurrentHashMap<String, Map<String, String>>();

    public ManifestBuilder addMainAttribute(@Nonnull String name, @Nonnull String value) {
        mainAttributes.put(checkNotNull(name), checkNotNull(value));
        return this;
    }

    public ManifestBuilder addEntryAttributes(@Nonnull String entryName, @Nonnull Map<String, String> attributes) {
        entryAttributes.putIfAbsent(entryName, new ConcurrentHashMap<String, String>(checkNotNull(attributes).size()));
        entryAttributes.get(entryName).putAll(attributes);
        return this;
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return (mainAttributes.isEmpty() && entryAttributes.isEmpty());
        } finally {
            lock.unlock();
        }
    }

    public @Nonnull Manifest build() {
        Manifest manifest = new Manifest();
        Attributes manifestMainAttributes = manifest.getMainAttributes();
        manifestMainAttributes.putValue(Name.MANIFEST_VERSION.toString(),
                GENERATED_MANIFEST_VERSION);
        Map<String, Attributes> manifestEntries = manifest.getEntries();
        lock.lock();
        try {
            for (Entry<String, String> mainAttribute : mainAttributes.entrySet()) {
                manifestMainAttributes.putValue(mainAttribute.getKey(), 
                        mainAttribute.getValue());
            }
            for (Entry<String, Map<String, String>> attributesForEntry : entryAttributes.entrySet()) {
                manifestEntries.put(attributesForEntry.getKey(),
                        toAttributes(attributesForEntry.getValue()));
            }
        } finally {
            lock.unlock();
        }
        return manifest;
    }

    private static Attributes toAttributes(Map<String, String> values) {
        Attributes attributes = new Attributes(values.size());
        for (Entry<String, String> value : values.entrySet()) {
            attributes.putValue(value.getKey(), value.getValue());
        }
        return attributes;
    }

}