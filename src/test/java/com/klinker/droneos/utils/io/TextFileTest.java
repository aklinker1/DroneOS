package com.klinker.droneos.utils.io;

import com.imarc.marc_i.utils.random.RandomString;
import com.klinker.droneos.utils.random.RandomString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextFileTest {

    @Test
    public void read() throws Exception {
        TextFile file = new TextFile("src/test/resources/TextFile_read.txt");
        assert file.exists();
        String actual = file.read();
        String expected =
                "Hello World!\n" +
                        "It's a beautiful day to code.";
        actual = actual.replace("\n\r", "\n");
        assertEquals(expected, actual);
    }

    @Test
    public void write() throws Exception {
        RandomString random = new RandomString(50);
        String expected = random.nextString() + '\n' + random.nextString();

        TextFile file = new TextFile("src/test/resources/TextFile_write.txt");
        file.write(expected);
        String actual = file.read();
        assertEquals(expected, actual);
    }

}