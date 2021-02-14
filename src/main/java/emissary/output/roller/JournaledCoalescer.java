package emissary.output.roller;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import emissary.output.roller.coalesce.Coalescer;
import emissary.output.roller.journal.JournaledChannelPool;
import emissary.output.roller.journal.Journaler;
import emissary.util.io.FileNameGenerator;

/**
 * The Rollable implementation that uses a journal to record offsets of completed writes to a pool of outputs. The
 * Journal serves as a write ahead log and records positions of all open file handles until rolled.
 * <p>
 * During a roll, all Journals are identified and their outputs are combined into a destination filename denoted by the
 * FileNameGenerator.
 *
 */
public class JournaledCoalescer extends Journaler {

    ICoalescer coalescer;

    /**
     * @see JournaledCoalescer#JournaledCoalescer(java.nio.file.Path, FileNameGenerator, int)
     * @param outPath The Path to use for reading input and writing combined output
     * @param fileNameGenerator The FileNameGenerator to use for unique destination file names
     * @throws IOException If there is some I/O problem.
     */
    public JournaledCoalescer(final Path outPath, final FileNameGenerator fileNameGenerator) throws IOException, InterruptedException {
        this(outPath, fileNameGenerator, JournaledChannelPool.DEFAULT_MAX);
    }

    /**
     * The Rollable with take all files in a Path and combine them into a single destination file on each roll.
     *
     * @param outPath The Path to use for reading input and writing combined output
     * @param fileNameGenerator The FileNameGenerator to use for unique destination file names
     * @param poolsize The max number of outputs for the pool.
     */
    public JournaledCoalescer(final Path outPath, final FileNameGenerator fileNameGenerator, int poolsize) throws IOException, InterruptedException {
        super(outPath, fileNameGenerator, poolsize);
        createCoalescer();
    }


    public void createCoalescer() throws IOException {
        coalescer = new Coalescer(this.outputPath);
    }


    @Override
    protected void postPoolInitHook(Collection<Path> paths) throws IOException {
        coalescer.coalesce(paths);
    }

}
