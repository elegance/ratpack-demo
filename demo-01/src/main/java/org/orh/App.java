package org.orh;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import ratpack.form.Form;
import ratpack.handling.Handler;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;

/**
 * http://www.ibm.com/developerworks/cn/java/j-lo-ratpack-http-microservice/index.html
 * 
 * @author orh
 *
 */
public class App {
    static ObjectMapper objectMapper = null;

    public static void main(String[] args) throws Exception {
        new App().start();
    }

    public void start() throws Exception {
        // 1. Context 中的处理器链
        RatpackServer.start(server -> server.registry(Registry.single(new ArrayList<String>())).handlers(chain -> chain
                .get(ctx -> ctx.insert(addOutput("Hello"), addOutput("World"), ctx3 -> ctx3.render(json(ctx3.get(List.class)))))));

        // 2. 表单和文件上传
        RatpackServer.start(server -> server
                .handlers(chain -> chain.post("form", ctx -> ctx.parse(Form.class).then(form -> ctx.render(form.get("name")))).post("file",
                        ctx -> ctx.parse(Form.class).then(form -> ctx.render(form.file("file").getText())))));

    }

    private Handler addOutput(final String text) {
        return ctx -> {
            ctx.get(List.class).add(text);
            ctx.next();
        };
    }

    public static String json(Object object) {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
