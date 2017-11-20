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

import com.alibaba.fastjson.JSONObject;
import com.httpinterface.common.Rpc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static com.httpinterface.common.ProtocolConstant.PROTOCOL_INTERFACE_NAME_KEY;
import static com.httpinterface.common.ProtocolConstant.PROTOCOL_METHOD_NAME_KEY;

/**
 * Created by yuan on 2017/11/18.
 */
public abstract class ServiceDispatcher extends HttpServlet {

    protected Rpc rpc;

    /**
     * 实现方法初始化 {@link #rpc} 对象
     */
    @Override
    public abstract void init() throws ServletException;


    @Override
    protected void service(HttpServletRequest req,
                           HttpServletResponse resp) throws ServletException, IOException {
        JSONObject o = JSONObject.parseObject(read(req.getInputStream()));
        String typeName = o.getString(PROTOCOL_INTERFACE_NAME_KEY);
        String methodName = o.getString(PROTOCOL_METHOD_NAME_KEY);

        Object response;
        try {
            response = getRpc().invoke(typeName, methodName, o);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        response(resp, JSONObject.toJSONString(response));
    }

    private static String read(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder buf = new StringBuilder();
        int len;
        char[] cbuf = new char[1024];
        while ((len = br.read(cbuf)) != -1) {
            buf.append(cbuf, 0, len);
        }
        return buf.toString();
    }

    private Rpc getRpc() {
        if (rpc == null) {
            throw new IllegalStateException("rpc 对象没有初始化");
        }
        return rpc;
    }

    private static void response(HttpServletResponse resp, String response) throws IOException {
        PrintWriter pw = resp.getWriter();
        pw.print(response);
        pw.flush();
    }
}
