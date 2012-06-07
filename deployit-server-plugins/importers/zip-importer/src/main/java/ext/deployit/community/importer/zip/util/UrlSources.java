/*
 * @(#)UrlSources.java     20 Oct 2011
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
package ext.deployit.community.importer.zip.util;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.service.importer.source.UrlSource;

public class UrlSources {
    private static final Field LOCATION;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlSources.class);
    
    static {
        try {
            LOCATION = UrlSource.class.getDeclaredField("location");
        } catch (Exception exception) {
            throw new AssertionError(format("Unable to access field 'location' of '%s' due to: %s%n", 
                    UrlSource.class, exception));
        }
        LOCATION.setAccessible(true);
    }
    
    public static URL getLocation(UrlSource source) {
        // urgh!
        try {
            return (URL) LOCATION.get(source);
        } catch (Exception exception) {
            LOGGER.warn("Unable to get 'location' URL from '{}' due to: {}", source, exception);
            return null;
        }
    }
    
    public static URI getLocationAsUri(UrlSource source) {
        URL location = getLocation(source);
        try {
            return ((location != null) ? location.toURI() : null);
        } catch (URISyntaxException exception) {
            LOGGER.warn("Unable to convert URL '{}' to URI due to: {}", location, exception);
            return null;
        }
    }
}
