package com.dev.dungcony.modules.users.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class ReceiverNotFound extends NotFoundException {

    public ReceiverNotFound() {
        super("Receiver not found");
    }

}
