package emissary.output.roller.transform;

import static emissary.output.roller.journal.JournalReader.getJournals;
import static emissary.output.roller.journal.Journaler.validateOutputPath;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import emissary.output.roller.ITransformer;
import emissary.output.roller.journal.Journal;
import emissary.util.io.ListOpenFiles;

public abstract class LsofTransformer implements ITransformer {

    static ListOpenFiles lsof = new ListOpenFiles();

    Path outputPath;

    public LsofTransformer(final Path outPath) throws IOException {
        this.outputPath = outPath.toAbsolutePath();
        validateOutputPath(this.outputPath);
    }

    public Map<String, Collection<Journal>> find() throws IOException {
        return getJournals(outputPath, Integer.MAX_VALUE)
                .stream()
                .collect(Collectors.groupingBy(Journal::getKey))
                .entrySet().stream()
                .filter(map -> isKeyGroupClosed(map.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    protected boolean isKeyGroupClosed(Collection<Journal> journals) {
        for (Journal journal : journals) {
            if (lsof.isOpen(journal.getJournalPath())) {
                return false;
            }
        }
        return true;
    }

}
