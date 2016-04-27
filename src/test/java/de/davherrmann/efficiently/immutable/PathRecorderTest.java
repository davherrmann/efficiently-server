package de.davherrmann.efficiently.immutable;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PathRecorderTest
{
    private final PathRecorder<POJO> pathRecorder = new PathRecorder<>(POJO.class);
    private final POJO path = pathRecorder.path();

    @Test
    public void pathIsRecorded() throws Exception
    {
        // given / when
        path.integer();

        // then
        assertThat(pathRecorder.lastCalledMethod().getName(), is("integer"));
    }

    @Test
    public void pathIsRecorded_inNestedObject() throws Exception
    {
        // given / when
        path.pojo().pojo().integer();

        // then
        assertThat(pathRecorder.lastCalledMethod().getName(), is("integer"));
        assertThat(pathRecorder.lastCalledNestedPath(), is(newArrayList("pojo", "pojo")));
    }

    private interface POJO
    {
        POJO pojo();

        int integer();
    }
}