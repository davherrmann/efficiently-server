package de.davherrmann.efficiently.server;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.gson.Gson;

public class ClientDataTest
{
    @Test
    public void testGson() throws Exception
    {
        // given / when
        final ClientData clientData = new Gson().fromJson(
            "{\"clientStateDiff\":{\"type\":\"de.davherrmann.efficiently.app.MySpecialState\",\"data\":{}},\"action\":{\"type\":\"initState\"}}",
            ClientData.class);

        // then
        assertThat(clientData.action().type(), is("initState"));
    }
}