package de.davherrmann.efficiently.server;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class EfficientlyServer
{
    public static void main(String[] args) throws Exception
    {
        new EfficientlyServer().run();
    }

    public void run()
    {
        new SpringApplicationBuilder() //
            .sources(EfficientlyServer.class) //
            .bannerMode(Banner.Mode.OFF) //
            .properties("server.port=8081") //
            .run();
    }
}
