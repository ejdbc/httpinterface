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
package com.httpinterface.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.httpinterface.common.Rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.httpinterface.common.ProtocolConstant.*;


/**
 * Created by yuan on 2017/11/18.
 */
@SuppressWarnings("unchecked")
public class ServiceProxy {
    private final ParameterPacker parameterPacker = new ParameterPacker();

    private final Rpc rpc;

    public ServiceProxy(Rpc rpc) {
        this.rpc = rpc;
    }

    public <T> T getServiceProxy(Class<T> type) {
        return (T) Proxy.newProxyInstance(getClass().
                getClassLoader(), new Class[]{type}, parameterPacker);
    }

    private class ParameterPacker implements InvocationHandler {
        @Override
        public Object invoke(Object proxy,
                             Method method, Object[] args) throws Throwable {
            String typeName = method.getDeclaringClass().getName();
            String methodName = method.getName();

            JSONObject pack = new JSONObject(true);

            pack.put(PROTOCOL_INTERFACE_NAME_KEY, typeName);
            pack.put(PROTOCOL_METHOD_NAME_KEY, methodName);
            pack.put(PROTOCOL_ARGUMENTS_TYPE_LIST_KEY, getArgumentsTypes(method));
            pack.put(PROTOCOL_ARGUMENTS_LIST_KEY, getArgumentValues(args));

            return rpc.send(pack);
        }

        private JSONArray getArgumentValues(Object[] originalArgs) {
            if (originalArgs == null || originalArgs.length == 0) {
                return EMPTY_ARGUMENTS;
            }
            JSONArray args = new JSONArray();
            for (Object o: originalArgs) {
                args.add(o == null ? "null" : o);
            }
            return args;
        }

        private JSONArray getArgumentsTypes(Method m) {
            Class[] types = m.getParameterTypes();
            if (types.length == 0) {
                return EMPTY_ARGUMENTS_TYPES;
            }
            JSONArray array = new JSONArray();
            for (Class type: types) {
                array.add(type.getName());
            }
            return array;
        }
    }

}
