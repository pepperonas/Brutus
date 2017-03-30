/*
 * Copyright (c) 2017 Martin Pfeffer
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

package io.celox.brutus.model;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author Martin Pfeffer
 * @see <a href="https://celox.io">https://celox.io</a>
 */
public class Field extends AbsBase {


    public enum Type {
        // clear text
        TEXT(0), NUMBER(1), LOGIN(2),
        // hidden
        PASSWORD(3), OTP(4),
        // clear text
        URL(5), MAIL(6), PHONE(7), DATE(8),
        // hidden
        PIN(9), SECRET(10);

        private int i;

        Type(int i) {
            this.i = i;
        }
    }

    private String description;
    private String value;
    private Type type;

    public Field(@NonNull Context context, long created) {
        super(context, created);
    }

    public Field(@NonNull Context context, long created,
        String description, String value, Type type) {
        super(context, created);
        this.description = description;
        this.value = value;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
