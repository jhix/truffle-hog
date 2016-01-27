package edu.kit.trufflehog.service.packetdataprocessor.profinetdataprocessor;

import edu.kit.trufflehog.command.trufflecommand.ITruffleCommand;
import edu.kit.trufflehog.util.INotifier;
import edu.kit.trufflehog.util.Notifier;

/**
 * <p>
 *     This class is a runnable notifier service that fetches packet data from the spp_profinet snort plugin, generates
 *     packet data objects and packs them into commands.
 *     Any other service can register as a listener and will receive the commands generated by this service.
 *     The class generalises the different types of inter-process communication.
 * </p>
 * <p>
 *     Possible implementations: {@link UnixSocketReceiver}, {@link MessageQueueReceiver}
 * </p>
 */
public abstract class TruffleReceiver extends Notifier<ITruffleCommand> implements INotifier<ITruffleCommand>, Runnable {

}
