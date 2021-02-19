package emissary.output.roller;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import emissary.output.roller.journal.Journal;

public interface ITransformer {

    void transform() throws IOException;

    Map<String, Collection<Journal>> find() throws IOException;
}
