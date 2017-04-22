package server;

import server.command.analyzers.*;
import server.command.analyzers.SilenceAnalyzer;
import server.conversion.ResponseConverter;
import server.conversion.ResponseType;

import java.io.IOException;
import java.util.*;

class CommandDigester {
    /*This list contains all of the analyzers at the disposal of the CommandDigester instance. We can always add more.*/
    private final List<CommandAnalyzer> availableAnalyzers = Collections.unmodifiableList(Arrays.asList(new HelpRequestAnalyzer(), new SendMessageAnalyzer(), new LeaveChatAnalyzer(), new ViewConnectedUsersAnalyzer(), new PermaBanAnalyzer(), new SilenceAnalyzer(), new RemoveBanAnalyzer(), new MakeModAnalyzer(), new RemoveModAnalyzer()));

    private final UserDataForInMemory userData;
    private final String[] splittedUserInput;

    public CommandDigester(UserDataForInMemory userData, String[] splittedUserInput) {
        this.userData = userData;
        this.splittedUserInput = splittedUserInput;
    }

    private boolean isInputSuccessfullyAnalyzed() throws IOException {
        for (CommandAnalyzer analyzer : availableAnalyzers) {
            if (analyzer.isValidCommand(splittedUserInput)) {
                String response = analyzer.analyze(userData, splittedUserInput);
                userData.getBundle().getDos().writeUTF(response);
                return true;
            }
        }
        return false;
    }

    public void digest() throws IOException {
        if (!isInputSuccessfullyAnalyzed()) {
            userData.getBundle().getDos().writeUTF(ResponseConverter.convertToStreamable(ResponseType.INVALID_COMMAND));
        }
    }
}
