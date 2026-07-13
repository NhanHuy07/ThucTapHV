package com.dev.dungcony.modules.product.services.interfaces.comment;

import java.util.UUID;

public interface CommentRemoveService {

    public void removeMyComment(UUID uid, String productCode);

}
