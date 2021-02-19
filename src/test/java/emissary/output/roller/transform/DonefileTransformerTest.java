package emissary.output.roller.transform;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import emissary.output.roller.journal.JournaledChannelPool;
import emissary.output.roller.journal.KeyedOutput;
import emissary.test.core.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DonefileTransformerTest extends UnitTest {

    private Path directory;
    private String key;
    private Path donefile;
    private DonefileTransformer instance;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.directory = Files.createTempDirectory("DonefileTransformerTest");
        this.key = UUID.randomUUID().toString();
        this.instance = new DonefileTransformer(this.directory);
        this.donefile = this.instance.getDonefile(this.directory, this.key);
    }

    @Test
    void find() throws Exception {
        JournaledChannelPool pool = new JournaledChannelPool(this.directory, this.key, 3);
        try (KeyedOutput k1 = pool.getFree(); KeyedOutput k2 = pool.getFree()) {
            writeText(k1, "one line of text");
            writeText(k2, "two lines of text\nthe second line");
            assertFalse(Files.exists(this.donefile));
            instance.transform();
            assertFalse(Files.exists(this.donefile));
        } finally {
            pool.close();
        }

        assertFalse(Files.exists(this.donefile));
        instance.transform();
        assertTrue(Files.exists(this.donefile));
    }

    private void writeText(final KeyedOutput ko, final String text) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(Channels.newWriter(ko, "UTF-8"))) {
            bw.write(text);
            bw.flush();
            ko.commit();
        }
    }

}
