package de.davherrmann.efficiently.app;

import org.springframework.context.annotation.ComponentScan;

import de.davherrmann.efficiently.server.EfficientlyServer;

@ComponentScan
public class SampleApp
{
    public static void main(String[] args) throws Exception
    {
        new EfficientlyServer(SampleApp.class).run();
    }
}
