package dansapps.interakt.tests;

import dansapps.interakt.Interakt;
import dansapps.interakt.data.PersistentData;
import dansapps.interakt.objects.*;
import dansapps.interakt.services.LocalStorageService;
import dansapps.interakt.utils.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LocalStorageServiceTest {
    private final List<String> loggedErrors = new ArrayList<>();

    @Test
    public void testEverySaveOperationIsAttemptedAndNamedWhenItFails() {
        LocalStorageService localStorageService = getServiceOverFailingData();

        localStorageService.save();

        assertReported("saving actors", "actors-sentinel");
        assertReported("saving worlds", "worlds-sentinel");
        assertReported("saving regions", "regions-sentinel");
        assertReported("saving squares", "squares-sentinel");
        assertReported("saving time partitions", "timePartitions-sentinel");
        assertReported("saving action records", "actionRecords-sentinel");
        assertReported("saving entity records", "entityRecords-sentinel");
        Assert.assertEquals("Expected one error per save operation, got: " + loggedErrors, 7, loggedErrors.size());
    }

    @Test
    public void testEveryLoadOperationIsAttemptedAndNamedWhenItFails() {
        LocalStorageService localStorageService = getServiceOverFailingData();

        localStorageService.load();

        assertReported("loading actors", "actors-sentinel");
        assertReported("loading worlds", "worlds-sentinel");
        assertReported("loading regions", "regions-sentinel");
        assertReported("loading squares", "squares-sentinel");
        assertReported("loading time partitions", "timePartitions-sentinel");
        assertReported("loading action records", "actionRecords-sentinel");
        assertReported("loading entity records", "entityRecords-sentinel");
        Assert.assertEquals("Expected one error per load operation, got: " + loggedErrors, 7, loggedErrors.size());
    }

    /**
     * Every save and load operation reads its collection from persistent data before it touches
     * anything else, so a getter that throws fails that one operation and only that one. Giving each
     * getter a distinct sentinel makes it observable which operations ran and which error came from
     * which. The factories are only used after the getter returns, so null is sufficient for them.
     */
    private LocalStorageService getServiceOverFailingData() {
        PersistentData persistentData = new PersistentData() {
            @Override
            public Set<Actor> getActors() {
                throw new IllegalStateException("actors-sentinel");
            }

            @Override
            public Set<World> getWorlds() {
                throw new IllegalStateException("worlds-sentinel");
            }

            @Override
            public Set<Region> getRegions() {
                throw new IllegalStateException("regions-sentinel");
            }

            @Override
            public Set<Square> getSquares() {
                throw new IllegalStateException("squares-sentinel");
            }

            @Override
            public ArrayList<TimePartition> getTimePartitions() {
                throw new IllegalStateException("timePartitions-sentinel");
            }

            @Override
            public LinkedList<ActionRecord> getActionRecords() {
                throw new IllegalStateException("actionRecords-sentinel");
            }

            @Override
            public Set<EntityRecord> getEntityRecords() {
                throw new IllegalStateException("entityRecords-sentinel");
            }
        };
        return new LocalStorageService(null, null, null, null, null, null, null, getRecordingLogger(), persistentData);
    }

    private Logger getRecordingLogger() {
        return new Logger(new Interakt()) {
            @Override
            public void logError(String errorMessage) {
                loggedErrors.add(errorMessage);
            }
        };
    }

    private void assertReported(String description, String sentinel) {
        for (String loggedError : loggedErrors) {
            if (loggedError.contains(description) && loggedError.contains(sentinel)) {
                return;
            }
        }
        Assert.fail("No error naming '" + description + "' and carrying its own detail was logged. Logged: " + loggedErrors);
    }
}
