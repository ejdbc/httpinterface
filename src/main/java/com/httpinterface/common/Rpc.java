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
package com.httpinterface.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.httpinterface.client.ServiceProxy;
import com.httpinterface.common.jfinal.HttpKit;
import com.httpinterface.server.ServiceContainer;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.httpinterface.common.ProtocolConstant.*;


/**
 * Created by yuan on 2017/11/18.
 */
@SuppressWarnings("unchecked")
public class Rpc {
    private static final Class<?>[] EMPTY_PARAMETER_TYPES = new Class[0];
    private static final Object[] EMPTY_ARGUMENTS = new Object[0];

    private final String servletUrl;
    private ServiceContainer serviceContainer;
    private ServiceProxy serviceProxy;
    private final ConcurrentMap<String, Class<?>> cachedTypes = new ConcurrentHashMap<String, Class<?>>(){
        @Override
        public Class<?> get(Object key) {
            Class clazz = super.get(key);
            if (clazz != null) {
                return clazz;
            }

            if ("boolean".equals(key))
                return boolean.class;
            else if ("char".equals(key))
                return char.class;
            else if ("byte".equals(key))
                return byte.class;
            else if ("short".equals(key))
                return short.class;
            else if ("int".equals(key))
                return int.class;
            else if ("long".equals(key))
                return long.class;
            else if ("float".equals(key))
                return float.class;
            else if ("double".equals(key))
                return double.class;
            return null;
        }
    };

    public Rpc(String servletUrl) {
        this.servletUrl = servletUrl;
    }

    public Rpc() {
        this(null);
    }

    public ServiceContainer getServiceContainer() {
        if (serviceContainer == null) {
            synchronized (this) {
                if (serviceContainer == null) {
                    serviceContainer = new ServiceContainer();
                }
            }
        }
        return serviceContainer;
    }

    public ServiceProxy getServiceProxy() {
        if (serviceProxy == null) {
            synchronized (this) {
                if (serviceProxy == null) {
                    serviceProxy = new ServiceProxy(this);
                }
            }
        }
        return serviceProxy;
    }

    public Object send(JSONObject pack) throws ClassNotFoundException {
        String resp = HttpKit.post(servletUrl, pack.toJSONString());
        JSONObject o = ((JSONObject) JSON.parse(resp));
        return o.getObject(PROTOCOL_RESPONSE_KEY,
                getCachedInterface(o.getString(PROTOCOL_RESPONSE_TYPE_KEY)));
    }

    public Object invoke(String typeName, String methodName, JSONObject protocol) throws Exception {
        Object impl = getServiceContainer().getServiceImpl(getCachedInterface(typeName));
        JSONArray types = protocol.getJSONArray(PROTOCOL_ARGUMENTS_TYPE_LIST_KEY);
        Method m = getMethod(impl, methodName, types);

        JSONObject o = new JSONObject();
        m.setAccessible(true);
        o.put(PROTOCOL_RESPONSE_KEY, m.invoke(impl, getArguments(protocol)));
        o.put(PROTOCOL_RESPONSE_TYPE_KEY, m.getReturnType().getName());
        return o;
    }

    private Object[] getArguments(JSONObject protocol) throws ClassNotFoundException {
        JSONArray originalVal = protocol.getJSONArray(PROTOCOL_ARGUMENTS_LIST_KEY);
        if (originalVal.size() == 0) {
            return EMPTY_ARGUMENTS;
        }
        JSONArray originalType = protocol.getJSONArray(PROTOCOL_ARGUMENTS_TYPE_LIST_KEY);

        Object[] args = new Object[originalType.size()];
        for (int i = 0; i < originalVal.size(); i++) {
            Object o = originalVal.get(i);
            args[i] = cast(o, originalType.getString(i));
        }
        return args;
    }

    private Object cast(Object o, String type) throws ClassNotFoundException {
        if (o.equals("null"))
            return null;
        else
            return TypeUtils.castToJavaBean(o, getCachedInterface(type));
    }

    private Method getMethod(Object o, String methodName,
                             JSONArray argsTypes) throws ClassNotFoundException, NoSuchMethodException {
        if (argsTypes.size() == 0) {
            return o.getClass().getMethod(methodName, EMPTY_PARAMETER_TYPES);
        }
        Class<?>[] parameterTypes = new Class[argsTypes.size()];
        int i = 0;
        for (Object typeName: argsTypes) {
            parameterTypes[i++] = getCachedInterface(typeName.toString());
        }
        return o.getClass().getMethod(methodName, parameterTypes);
    }

    public  <T> Class<T> getCachedInterface(String typeName) throws ClassNotFoundException{
        Class<?> type = cachedTypes.get(typeName);
        if (type == null) {
            cachedTypes.put(typeName, type = Class.forName(typeName));
        }
        return ((Class<T>) type);
    }
}
