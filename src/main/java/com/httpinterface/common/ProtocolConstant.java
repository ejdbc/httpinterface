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

import com.alibaba.fastjson.JSONArray;

/**
 * Created by yuan on 2017/11/18.
 */
public class ProtocolConstant {

    public static final String PROTOCOL_INTERFACE_NAME_KEY = "PROTOCOL_INTERFACE_NAME_KEY";
    public static final String PROTOCOL_METHOD_NAME_KEY = "PROTOCOL_METHOD_NAME_KEY";

    public static final String PROTOCOL_ARGUMENTS_LIST_KEY = "PROTOCOL_ARGUMENTS_LIST_KEY";
    public static final String PROTOCOL_ARGUMENTS_TYPE_LIST_KEY = "PROTOCOL_ARGUMENTS_TYPE_LIST_KEY";

    public static final String PROTOCOL_RESPONSE_TYPE_KEY = "PROTOCOL_ARGUMENTS_LIST_KEY";
    public static final String PROTOCOL_RESPONSE_KEY = "PROTOCOL_ARGUMENTS_TYPE_LIST_KEY";

    public static final JSONArray EMPTY_ARGUMENTS_TYPES = new JSONArray();
    public static final JSONArray EMPTY_ARGUMENTS = new JSONArray();
}
