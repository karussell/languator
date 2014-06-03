package com.graphhopper.localdet;

import java.io.File;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;

/**
 * @author Peter Karich
 */
public class OSMStream extends Thread implements StringStream, Sink {

    private boolean completed = false;
    private final PbfReader reader;
    private final LinkedBlockingDeque<String> queue = new LinkedBlockingDeque<String>(1000);
    private Exception exception;

    public OSMStream(File file) {
        setName(file.getName());
        reader = new PbfReader(file, 2);
        reader.setSink(this);
    }

    @Override
    public void run() {
        completed = false;
        try {
            reader.run();
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Override
    public boolean hasNext() {
        return !hasError() && (!completed || !queue.isEmpty());
    }

    public boolean hasError() {
        return exception != null;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String next() {
        try {
            // block if nothing available
            return queue.take();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void process(EntityContainer ec) {
        for (Tag t : ec.getEntity().getTags()) {
            if ("name".equals(t.getKey())) {
                try {
                    queue.put(t.getValue());
                    break;
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
    }

    public void initialize(Map<String, Object> map) {
    }

    public void complete() {
        completed = true;
    }

    public void release() {
    }

    public int getCurrentSize() {
        return queue.size();
    }
}
