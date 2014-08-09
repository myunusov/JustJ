/*
 * Copyright (c) 2014 Maxim Yunusov
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.maxur.jj.core.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

/**
 * @author Maxim Yunusov
 * @version 1.0 09.08.2014
 */
public abstract class MemberDescriptor<T> {

    private final Member member;

    private final Class<T> declaringClass;

    private final boolean isInheritable;


    public MemberDescriptor(final Member member) {
        this.member = member;
        //noinspection unchecked
        this.declaringClass = (Class<T>) member.getDeclaringClass();
        this.isInheritable = calcInheritable();
    }

    public boolean isInheritable() {
        return isInheritable;
    }

    private boolean calcInheritable() {
        int modifiers = member.getModifiers();
        return Modifier.isPublic(modifiers) ||
                Modifier.isProtected(modifiers) ||
                !Modifier.isPrivate(modifiers);
    }

    public Class getDeclaringClass() {
        return declaringClass;
    }

    public Package getPackage() {
        return declaringClass.getPackage();
    }

    public String getName() {
        return member.getName();
    }

    public boolean isPublic() {
        return Modifier.isPublic(member.getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(member.getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(member.getModifiers());
    }

    public boolean isDefault() {
        final int modifiers = member.getModifiers();
        return !Modifier.isPublic(modifiers) &&
                !Modifier.isProtected(modifiers) &&
                !Modifier.isPrivate(modifiers);
    }

    public Member getMember() {
        return member;
    }

    protected boolean sameName(final MemberDescriptor method) {
        return getName().equals(method.getName());
    }

    public abstract boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

    public abstract Class getType();

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemberDescriptor)) {
            return false;
        }
        final MemberDescriptor that = (MemberDescriptor) o;
        return member.equals(that.member);
    }

    @Override
    public final int hashCode() {
        return member.hashCode();
    }

}
