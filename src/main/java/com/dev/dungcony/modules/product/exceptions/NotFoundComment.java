package com.dev.dungcony.modules.product.exceptions;

import com.dev.dungcony.commons.exceptions.NotFoundException;

public class NotFoundComment extends NotFoundException {
    public NotFoundComment() {
        super("Không tìm thấy bình luận");
    }

}
