/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.shared;

/**
 * @author pierre.thirouin@ext.mpsa.com (Pierre Thirouin)
 */
public class UriBuilder {

    private UriBuilder() {
    }

    /**
     * Constructs a URI. The final slash will always be removed.
     * <p>
     * No encoding or replacement will be done.
     * </p>
     * The paths can be null or empty, in this case they wont be added.
     *
     * @param paths the paths
     * @return the built path
     */
    public static String uri(final String... paths) {
        StringBuilder sb = new StringBuilder();
        boolean firstPath = true;
        for (String s : paths) {
            if (s == null || s.isEmpty()) {
                continue;
            }
            if (!s.startsWith("/") && !firstPath) {
                sb.append("/");
            }
            sb.append(stripLeadingSlash(s));
            firstPath = false;
        }
        return sb.toString();
    }

    /**
     * Removes the leading slash in the given path.
     *
     * @param path the path
     * @return the new path
     */
    public static String stripLeadingSlash(String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        } else {
            return path;
        }
    }
}