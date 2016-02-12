package edu.kit.trufflehog.service.replaylogging;

import edu.kit.trufflehog.command.ICommand;
import edu.kit.trufflehog.model.FileSystem;
import edu.kit.trufflehog.model.graph.AbstractNetworkGraph;
import edu.kit.trufflehog.model.graph.GraphProxy;
import edu.kit.trufflehog.presenter.LoggedScheduledExecutor;
import edu.kit.trufflehog.util.IListener;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * <p>
 *     The ReplayLogSaveService saves the current graph state so that it can be fully reconstructed at a later date. It
 *     does this by creating {@link ReplayLog} objects at a fixed time interval (each ReplayLog object covers the data for
 *     this time interval). Thus the ReplayLogSaveService runs in its own thread.
 * </p>
 *
 * @author Julian Brendl
 * @version 1.0
 */
public class ReplayLogSaveService implements IListener<ICommand>, Runnable {
    private static final Logger logger = LogManager.getLogger(ReplayLogSaveService.class);

    private CommandLogger commandLogger;
    private SnapshotLogger snapshotLogger;
    private ReplayLogger replayLogger;
    private Instant startInstant;
    private FileSystem fileSystem;
    private File currentReplayLogFolder;

    private Lock lock;
    private ScheduledFuture<?> scheduledFuture;
    private LoggedScheduledExecutor executorService;
    private int createReplayLogInterval;

    /**
     * <p>
     *     Creates a new ReplayLogSaveService object.
     * </p>
     *
     * @param graphProxy The proxy object that contains the graph so that the SnapshotLogger can take a snapshot of it.
     * @param fileSystem The fileSystem object that gives access to the locations where files are saved on the hard drive.
     *                   This is necessary because of the ReplayLogger that needs to save ReplayLogs.
     */
    public ReplayLogSaveService(GraphProxy graphProxy, FileSystem fileSystem, LoggedScheduledExecutor executorService) {
        this.commandLogger = new CommandLogger();
        this.snapshotLogger = new SnapshotLogger(graphProxy);
        this.replayLogger = new ReplayLogger();
        this.fileSystem = fileSystem;
        this.currentReplayLogFolder = null;

        this.lock = new ReentrantLock();
        this.executorService = executorService;
        createReplayLogInterval = 10000; //TODO: hook up with settings stuff
    }

    /**
     * <p>
     *     Starts the replay log saving functionality of TruffleHog.
     * </p>
     * <p>
     *     This creates a new folder in the replaylog folder into which all recorded {@link ReplayLog}s will be saved.
     * </p>
     */
    public void startRecord() {
        //
        lock.lock();
        startInstant = Instant.now();
        currentReplayLogFolder = new File(fileSystem.getReplayLogFolder() + File.separator +
                startInstant.getEpochSecond());
        lock.unlock();

        currentReplayLogFolder.mkdir();

        scheduledFuture = executorService.scheduleAtFixedRate(this, 0, createReplayLogInterval, SECONDS);
        logger.debug("Recording replay logs..");
    }

    /**
     * <p>
     *     Stops the replay log saving functionality of TruffleHog.
     * </p>
     */
    public void stopRecord() {
        //
        lock.lock();
        startInstant = null;
        lock.unlock();

        scheduledFuture.cancel(true);
        logger.debug("Stopped recording replay logs..");
    }

    /**
     * <p>
     *     Starts the replay log save service.
     * </p>
     * <p>
     *     The ReplayLogSaveService saves the current graph state so that it can be fully reconstructed at a later date.
     *     It does this by creating {@link ReplayLog} objects at a fixed time interval (each ReplayLog object covers the
     *     data for this time interval). Thus the ReplayLogSaveService runs in its own thread.
     * </p>
     */
    @Override
    public void run() {
        // Copy all commands that were received until now into the temporary list with a lock, so that no commands are
        // added to the list while the list is being transferred
        lock.lock();
        commandLogger.swapMaps();
        lock.unlock();

        // Create a new replay log
        LinkedMap<ICommand, Instant> compressedCommandList = commandLogger.createCommandLog();
        AbstractNetworkGraph graph = snapshotLogger.takeSnapshot();
        ReplayLog replayLog = replayLogger.createReplayLog(graph, compressedCommandList);

        // Save the replay log to the disk
        replayLogger.saveReplayLog(replayLog, currentReplayLogFolder);
    }

    /**
     * <p>
     *     Through this method the ReplayLogSaveService receives new Commands that it should save into the list so that
     *     they can be turned into {@link ReplayLog}s later.
     * </p>
     *
     * @param command The command to save
     */
    @Override
    public void receive(ICommand command) {
        // Lock so that the list cannot be transferred while a command is being added to it. See the run method for more
        lock.lock();
        // Drop the command if we are currently not recording
        if (startInstant == null) {
            return;
        }
        commandLogger.addCommand(command, startInstant);
        lock.unlock();
    }
}
