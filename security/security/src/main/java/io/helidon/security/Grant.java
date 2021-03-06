/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.security;

import java.security.Principal;
import java.util.Collection;

import io.helidon.security.util.AbacSupport;

/**
 * A concept representing anything that can be granted to a subject.
 * This may be:
 * <ul>
 * <li>role</li>
 * <li>scope</li>
 * <li>permission</li>
 * <li>anything else grantable, including additional principals</li>
 * </ul>
 */
public class Grant implements AbacSupport, Principal {
    private final AbacSupport properties;
    private final String type;
    private final String name;

    /**
     * Create an instance for a type and a name.
     *
     * @param type type of the grant (currently in use are "role" and "scope")
     * @param name name of the grant (e.g. "admin", "calendar-read" etc.)
     */
    protected Grant(String type, String name) {
        this(builder()
                     .type(type)
                     .name(name));
    }

    /**
     * Create an instance for a type and a name and a set of custom attributes.
     *
     * @param type        type of the grant (currently in use are "role" and "scope")
     * @param name        name of the grant (e.g. "admin", "calendar-read" etc.)
     * @param abacSupport container of attributes to be available to attribute based access control (e.g. "nickname")
     */
    protected Grant(String type, String name, AbacSupport abacSupport) {
        this(builder()
                     .type(type)
                     .name(name)
                     .attributes(abacSupport));
    }

    /**
     * Create an instance based on a builder.
     *
     * @param builder builder instance
     */
    protected Grant(Builder<?> builder) {
        this.type = builder.type;
        this.name = builder.name;
        BasicAttributes properties = new BasicAttributes(builder.properties);
        properties.put("type", type);
        properties.put("name", name);
        this.properties = properties;
    }

    /**
     * Creates a fluent API builder to build new instances of this class.
     *
     * @return builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Object getAttributeRaw(String key) {
        return properties.getAttributeRaw(key);
    }

    @Override
    public Collection<String> getAttributeNames() {
        return properties.getAttributeNames();
    }

    /**
     * Type of this grant.
     * Known types:
     * <ul>
     * <li>"role" - represents a role grant, also a dedicated class is created for this type: {@link Role}</li>
     * <li>"scope" - represents a OAuth2 scope grant</li>
     * </ul>
     *
     * @return type of the grant
     */
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return type + ":" + name;
    }

    /**
     * A fluent API builder for {@link Grant}.
     *
     * @param <T> type of the builder, needed for builder inheritance
     */
    public static class Builder<T extends Builder> implements io.helidon.common.Builder<Grant> {
        private BasicAttributes properties = new BasicAttributes();
        private String type;
        private String name;
        private T instance;

        /**
         * Create a new instance.
         */
        @SuppressWarnings("unchecked")
        protected Builder() {
            this.instance = (T) this;
        }

        @Override
        public Grant build() {
            return new Grant(this);
        }

        /**
         * Configure type of this grant.
         *
         * @param type type name, known types are "role" and "scope"
         * @return updated builder instance
         */
        public T type(String type) {
            this.type = type;
            return instance;
        }

        /**
         * Name of this grant.
         *
         * @param name logical name of this grant (e.g. "admin", "calendar_read" etc.)
         * @return updated builder instance
         */
        public T name(String name) {
            this.name = name;
            return instance;
        }

        /**
         * Attributes of this grant.
         *
         * @param attribs Attributes to add to this grant, allowing us to extend the information known (such as "nickname",
         *                "cn" etc.)
         * @return updated builder instance
         */
        public T attributes(AbacSupport attribs) {
            this.properties = new BasicAttributes(attribs);
            return instance;
        }

        /**
         * Add and attribute to this grant.
         *
         * @param key   name of the attribute
         * @param value value of the attribute
         * @return updated builder instance
         */
        public T addAttribute(String key, Object value) {
            this.properties.put(key, value);
            return instance;
        }
    }
}
