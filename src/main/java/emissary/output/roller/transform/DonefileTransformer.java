package emissary.output.roller.transform;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import emissary.output.roller.journal.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DonefileTransformer extends LsofTransformer {

    private static final Logger logger = LoggerFactory.getLogger(DonefileTransformer.class);

    public static final String DONE_EXT = ".bgdone";

    public DonefileTransformer(final Path outPath) throws IOException {
        super(outPath);
    }

    public void transform() throws IOException {

        Optional<Journal> journal;
        Path journalPath;
        for (Map.Entry<String, Collection<Journal>> entry : find().entrySet()) {
            logger.info("Transforming {}", entry);
            journal = entry.getValue().stream().findFirst();
            if (journal.isPresent()) {
                journalPath = journal.get().getJournalPath();
                transform(journalPath, getDonefile(journalPath.getParent(), entry.getKey()));
            }
        }
    }

    protected void transform(Path journal, Path donefile) throws IOException {
        if (!Files.exists(journal)) {
            logger.debug("Journal file no longer exists {}, skipping transform.", journal);
            return;
        }
        if (Files.exists(donefile)) {
            logger.debug("Done file {} already exists, skipping transform.", donefile);
            return;
        }

        logger.info("Writing done file {}", donefile);
        Files.write(donefile, "0".getBytes(UTF_8));
    }

    protected Path getDonefile(Path dir, String key) {
        return Paths.get(dir.toString(), key + DONE_EXT);
    }

}
