package com.dev.dungcony.modules.users.exceptions;

import com.dev.dungcony.commons.exceptions.ConflictException;

public class ReceiverIdConflict extends ConflictException {
    public ReceiverIdConflict() {
        super("Receiver id conflict");
    }

}
