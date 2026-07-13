package com.dev.dungcony.modules.users.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class RankNotFound extends NotFoundException {
    public RankNotFound() {
        super("Rank not found");
    }
}
