package de.davherrmann.efficiently.server;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class EfficientlyServer
{
    private final Class<?>[] sources;

    public EfficientlyServer()
    {
        this(new Class[]{});
    }

    public EfficientlyServer(Class<?>... sources)
    {
        this.sources = sources;
    }

    public void run()
    {
        new SpringApplicationBuilder() //
            .sources(EfficientlyServer.class) //
            .sources(sources) //
            .bannerMode(Banner.Mode.OFF) //
            .properties("server.port=8081") //
            .run();
    }

    public static void main(String[] args) throws Exception
    {
        new EfficientlyServer().run();
    }
}
