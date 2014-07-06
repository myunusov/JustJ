/*
 * Copyright (c) 2014 Maxim Yunusov
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.maxur.jj.orm;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>6/30/2014</pre>
 */
public final class ROMapper {

    private static final Map<Class, EntityMetaData> META_DATA_HASH_MAP = new HashMap<>();

    private ROMapper() {
        // Util class
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> map(
            final ResultSet resultSet,
            final Class<? extends T> outputClass
    ) throws ORMException {
        if (resultSet == null) {
            return emptyList();
        }
        final EntityMetaData<T> entityMetaData = getEntityMetaData(outputClass);
        final List<T> outputList = new ArrayList<>();
        try {
            final ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            while (resultSet.next()) {
                final T bean = outputClass.newInstance();
                for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
                    final String columnName = resultSetMetaData.getColumnName(i + 1);
                    final Field field = entityMetaData.fieldBy(columnName, i);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(bean, resultSet.getObject(i + 1));
                    }
                }
                outputList.add(bean);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
           throw new ORMException(e);
        }
        return outputList;
    }

    private static <T> EntityMetaData getEntityMetaData(Class<? extends T> entityClass) {
        //noinspection unchecked
        EntityMetaData<T> result = META_DATA_HASH_MAP.get(entityClass);
        if (result == null) {
            result = new EntityMetaData<>(entityClass);
            META_DATA_HASH_MAP.put(entityClass, result);
        }
        return result;
    }

    static class EntityMetaData<T> {

        private final Map<String, Field> name2Field = new HashMap<>();

        private final Map<Integer, Field> number2Field = new HashMap<>();

        EntityMetaData(final Class<? extends T> entityClass) {
            if (entityClass.isAnnotationPresent(Entity.class)) {
                for (Field field : entityClass.getDeclaredFields()) {
                    process(field);
                }
            }
        }

        private void process(final Field field) {
            if (field.isAnnotationPresent(Column.class)) {
                final Column column = field.getAnnotation(Column.class);
                if (!column.name().isEmpty()) {
                    name2Field.put(column.name(), field);
                } else if (column.index() != -1) {
                    number2Field.put(column.index(), field);
                }
            }
        }

        public Field fieldBy(final String name, final int index) {
            final Field field = name2Field.get(name);
            return field == null ? number2Field.get(index) : field;
        }
    }


}

