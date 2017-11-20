/**
 * Copyright (c) 2011-2017, yuan hang 袁航 (1275513803@qq.com).
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
package com.httpinterface.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by yuan on 2017/11/18.
 */
@SuppressWarnings("unchecked")
public class ServiceContainer {

    private ConcurrentMap<Class<?>, Object> impls = new ConcurrentHashMap<>();

    public <T> T addServiceImpl(Class<T> type, T impl) {
        return (T) impls.put(type, impl);
    }

    public <T> T getServiceImpl(Class<T> type) {
        return ((T) impls.get(type));
    }

}
